package com.wireshield.av;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.Queue;
import java.util.LinkedList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.wireshield.enums.runningStates;
import com.wireshield.enums.warningClass;
import javax.swing.JOptionPane;

/**
 * Manages antivirus scanning tasks and orchestrates file analysis using ClamAV
 * and VirusTotal. This class is implemented as a singleton.
 */
public class AntivirusManager {

	private static final Logger logger = LogManager.getLogger(AntivirusManager.class);

	private static AntivirusManager instance;

	private ClamAV clamAV;
	private VirusTotal virusTotal;
	private Queue<File> scanBuffer = new LinkedList<>();
	private List<File> filesToRemove = new ArrayList<>();
	private List<ScanReport> finalReports = new ArrayList<>();
	private runningStates scannerStatus;

	private Thread scanThread;
	static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // Maximum file size for VirusTotal analysis (10 MB)

	private AntivirusManager() {
		logger.info("AntivirusManager initialized.");
		scannerStatus = runningStates.DOWN;
	}

	/**
	 * Returns the singleton instance of AntivirusManager.
	 *
	 * @return the AntivirusManager instance.
	 */
	public static synchronized AntivirusManager getInstance() {
		if (instance == null) {
			instance = new AntivirusManager();
		}
		return instance;
	}

	/**
	 * Adds a file to the scan buffer for future analysis.
	 *
	 * @param file the file to add to the scan buffer.
	 */
	public synchronized void addFileToScanBuffer(File file) {
		if (file == null || !file.exists()) {
			logger.error("Invalid file or file does not exist.");
			return;
		}
		if (!scanBuffer.contains(file)) {
			scanBuffer.add(file);
			logger.info("File added to scan buffer: {}", file.getName());
			notify(); // Notify scanning thread of new file
		} else {
			logger.warn("File is already in the scan buffer: {}", file.getName());
		}
	}

	public void startScan() {
		if (scannerStatus == runningStates.UP) {
			logger.warn("Scan process is already running.");
			return;
		}

		scannerStatus = runningStates.UP;
		logger.info("Starting antivirus scan process...");

		scanThread = new Thread(() -> {
			try {
				performScan();
			} finally {
				synchronized (this) {
					scannerStatus = runningStates.DOWN;
					notifyAll(); // Notify all threads waiting on this object
				}
			}
		});

		scanThread.start();
	}

	private void performScan() {
		while (scannerStatus == runningStates.UP) {
			File fileToScan;

			// Retrieve the next file to scan from the buffer
			synchronized (scanBuffer) {
				fileToScan = scanBuffer.poll();
			}

			// Wait for new files if the buffer is empty
			if (fileToScan == null) {
				synchronized (this) {
					try {
						if (Thread.currentThread().isInterrupted()) {
							break; // Exit if thread is interrupted
						}
						wait();
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt(); // Restore interrupt status
						break;
					}
				}
				continue;
			}

			// Perform the file scanning
			ScanReport finalReport = new ScanReport();
			finalReport.setFile(fileToScan);

			if (clamAV != null) {
				clamAV.analyze(fileToScan);
				ScanReport clamAVReport = clamAV.getReport();
				if (clamAVReport != null) {
					mergeReports(finalReport, clamAVReport);
				}
			}

			if (finalReport.isThreatDetected() && virusTotal != null && fileToScan.length() <= MAX_FILE_SIZE) {
				virusTotal.analyze(fileToScan);
				ScanReport virusTotalReport = virusTotal.getReport();
				if (virusTotalReport != null) {
					mergeReports(finalReport, virusTotalReport);
				}
			} else if (fileToScan.length() > MAX_FILE_SIZE) {
				logger.warn("File is too large for VirusTotal analysis: {}", fileToScan.getName());
			}

			// Add the report to final results
			finalReports.add(finalReport);

			if (finalReport.getWarningClass() == warningClass.DANGEROUS
					|| finalReport.getWarningClass() == warningClass.SUSPICIOUS) {
				logger.warn("Threat detected in file: {}", fileToScan.getName());
				JOptionPane.showMessageDialog(null, "Threat detected in file: " + fileToScan.getName(),
						"Threat Detected", JOptionPane.WARNING_MESSAGE); // Show warning dialog
				filesToRemove.add(fileToScan);
			}

			if (Thread.currentThread().isInterrupted()) {
				break; // Exit loop if thread is interrupted
			}
		}
	}

	/*
	 * Stops the ongoing antivirus scan process gracefully.
	 */
	public void stopScan() {
		if (scannerStatus == runningStates.DOWN) {
			logger.warn("No scan process is running.");
			return;
		}

		scannerStatus = runningStates.DOWN;

		if (scanThread != null && scanThread.isAlive()) {
			scanThread.interrupt();
			try {
				scanThread.join(); // Wait for the thread to terminate
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt(); // Restore interrupt status
			}
		}
	}

	/**
	 * Sets the ClamAV engine for file analysis.
	 *
	 * @param clamAV the ClamAV instance.
	 */
	public void setClamAV(ClamAV clamAV) {
		this.clamAV = clamAV;
	}

	/**
	 * Sets the VirusTotal engine for file analysis.
	 *
	 * @param virusTotal the VirusTotal instance.
	 */
	public void setVirusTotal(VirusTotal virusTotal) {
		this.virusTotal = virusTotal;
	}

	/**
	 * Retrieves the current status of the scanner.
	 *
	 * @return the scanner status.
	 */
	public runningStates getScannerStatus() {
		return scannerStatus;
	}

	/**
	 * Retrieves the list of final scan reports.
	 *
	 * @return the list of scan reports.
	 */
	public List<ScanReport> getFinalReports() {
		return finalReports;
	}

	/**
	 * Retrieves the current state of the scan buffer.
	 *
	 * @return a copy of the scan buffer.
	 */
	public synchronized List<File> getScanBuffer() {
		return new ArrayList<>(scanBuffer);
	}

	/**
	 * Merges details from one scan report into another.
	 *
	 * @param target the target report to be updated.
	 * @param source the source report to merge from.
	 */
	void mergeReports(ScanReport target, ScanReport source) {
		if (source != null && source.isThreatDetected()) {
			target.setThreatDetected(true);
			target.setThreatDetails(source.getThreatDetails());

			if (source.getWarningClass().compareTo(target.getWarningClass()) > 0) {
				target.setWarningClass(source.getWarningClass());
			}

			target.setMaliciousCount(target.getMaliciousCount() + source.getMaliciousCount());
			target.setHarmlessCount(target.getHarmlessCount() + source.getHarmlessCount());
			target.setSuspiciousCount(target.getSuspiciousCount() + source.getSuspiciousCount());
			target.setUndetectedCount(target.getUndetectedCount() + source.getUndetectedCount());
		}

		if (source != null && source.getSha256() != null && !source.getSha256().equals(target.getSha256())) {
			target.setSha256(source.getSha256());
		}

		target.setValid(target.isValidReport() && (source != null && source.isValidReport()));
	}
}
