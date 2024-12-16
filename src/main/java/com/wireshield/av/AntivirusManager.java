package com.wireshield.av;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.wireshield.enums.runningStates;
import com.wireshield.enums.warningClass;
import java.util.Queue;
import java.util.LinkedList;

/*
 * AntivirusManager manages antivirus scanning of files. 
 * It adds files to a scan buffer, scans them with ClamAV and VirusTotal, 
 * and keeps track of the scan results.
 */
public class AntivirusManager {

	private static final Logger logger = LogManager.getLogger(AntivirusManager.class);

	private ClamAV clamAV; // ClamAV scanner instance
	private VirusTotal virusTotal; // VirusTotal scanner instance
	private Queue<File> scanBuffer = new LinkedList<>(); // Queue for FIFO file handling
	private List<File> filesToRemove = new ArrayList<>(); // Files to be removed after scanning
	private List<ScanReport> finalReports = new ArrayList<>(); // Stores the final scan reports
	private runningStates scannerStatus; // Current state of the scanner

	private Thread scanThread; // A reference to the scanning thread

	/*
	 * Constructs an AntivirusManager instance.
	 */
	public AntivirusManager() {
		logger.info("AntivirusManager initialized.");
		scannerStatus = runningStates.DOWN;
	}

	/**
	 * Adds a file to the scan buffer if it has not already been added. Notifies the
	 * scanning thread when a new file is added.
	 *
	 * @param file The file to add.
	 */
	public synchronized void addFileToScanBuffer(File file) {
		if (file == null || !file.exists()) {
			logger.error("Invalid file or file does not exist: {}", file);
			return; // Aggiungere un controllo per file nulli o non esistenti
		}
		if (!scanBuffer.contains(file)) {
			scanBuffer.add(file);
			logger.info("File added to scan buffer: {}", file.getName());
			notify(); // Notify the scanning thread that a new file is available
		} else {
			logger.warn("File is already in the scan buffer: {}", file.getName());
		}
	}

	/*
	 * Starts the scanning process. It will continue scanning until there are no
	 * more files in the buffer.
	 */
	public void startPerformScan() {

		if (scannerStatus == runningStates.UP) {
			logger.warn("Scan process is already running.");
			return; // If scanning is already running, return early
		}

		scannerStatus = runningStates.UP; // Set status to UP (scanning)
		logger.info("Starting antivirus scan process...");

		// Start the scanning process in a new thread
		scanThread = new Thread(() -> {
			while (scannerStatus == runningStates.UP) {
				File fileToScan;

				// Safely retrieve the file from the queue
				synchronized (scanBuffer) {
					fileToScan = scanBuffer.poll(); // Extract the first file from the queue
				}

				if (fileToScan == null) {
					logger.info("No files to scan, waiting for new files...");
					synchronized (this) {
						try {
							wait(); // Wait for a notification that a new file is available
						} catch (InterruptedException e) {
							logger.error("Scanning interrupted.", e);
							break; // Exit if execution is interrupted
						}
					}
					continue; // Retry the loop once a notification is received
				}

				logger.info("Scanning file: {}", fileToScan.getName());
				ScanReport finalReport = new ScanReport(); // Consolidated report for the file
				finalReport.setFile(fileToScan);

				// Scan with ClamAV
				if (clamAV != null) {
					clamAV.analyze(fileToScan); // Update the report with ClamAV
					ScanReport clamAVReport = clamAV.getReport(); // Get ClamAV report
					if (clamAVReport != null) {
						mergeReports(finalReport, clamAVReport); // Merge ClamAV report
					}
				}

				// Scan with VirusTotal if ClamAV detects a severe threat
				if (finalReport.isThreatDetected() && virusTotal != null) {
					virusTotal.analyze(fileToScan); // Update the report with VirusTotal
					ScanReport virusTotalReport = virusTotal.getReport(); // Get VirusTotal report
					if (virusTotalReport != null) {
						mergeReports(finalReport, virusTotalReport); // Merge VirusTotal report
					}
				}

				// Handle threats
				if (finalReport.getWarningClass() == warningClass.DANGEROUS
						|| finalReport.getWarningClass() == warningClass.SUSPICIOUS) {
					logger.warn("Threat detected in file: {}", fileToScan.getName());
					filesToRemove.add(fileToScan); // Add the file to the list of files to be removed
				}

				// Add the consolidated report to the final reports
				finalReports.add(finalReport);
				logger.info("Scan completed for file: {}", fileToScan.getName());
				logger.info("Scan report for file {}: {}", fileToScan.getName(), finalReport);
			}

			logger.info("Scanning process finished or stopped.");
		});

		// Start the scanning thread
		scanThread.start();
	}

	/*
	 * Stops the scanning process.
	 */
	public void stopPerformScan() {
		if (scannerStatus == runningStates.DOWN) {
			logger.warn("No scan process is running.");
			return; // If no scan is running, return early
		}

		scannerStatus = runningStates.DOWN; // Set status to DOWN (stopped)
		logger.info("Scanning process stopped.");

		// Stop the scan thread if it is still running
		if (scanThread != null && scanThread.isAlive()) {
			scanThread.interrupt(); // Interrupt the thread to stop scanning
		}
	}

	/**
	 * Merges the attributes of a source ScanReport into a target ScanReport.
	 *
	 * @param target The ScanReport to be updated.
	 * @param source The ScanReport with additional details.
	 */
	void mergeReports(ScanReport target, ScanReport source) {
		if (source != null && source.isThreatDetected()) {
			// Update the threatDetected flag
			target.setThreatDetected(true);

			// Handle threat details
			if (target.getThreatDetails().equals("No threat detected")) {
				target.setThreatDetails(source.getThreatDetails());
			} else {
				target.setThreatDetails(target.getThreatDetails() + "; " + source.getThreatDetails());
			}

			// Set the most severe warning class
			if (source.getWarningClass().compareTo(target.getWarningClass()) > 0) {
				target.setWarningClass(source.getWarningClass());
			}
		}

		// Update report validity
		boolean validReport = target.isValidReport() && (source != null && source.isValidReport());
		target.setValid(validReport);
	}

	/**
	 * Returns the current scanner status.
	 *
	 * @return The current state of the scanner.
	 */
	public runningStates getScannerStatus() {
		return scannerStatus;
	}

	/**
	 * Returns the final scan reports, which include the results of all scanned
	 * files.
	 *
	 * @return The final scan reports.
	 */
	public List<ScanReport> getFinalReports() {
		return finalReports;
	}

	/**
	 * Returns a copy of the scan buffer containing the files currently waiting for
	 * scanning. Since the scan buffer is dynamic, its content may change during
	 * processing.
	 *
	 * @return A copy of the scan buffer.
	 */
	public synchronized List<File> getScanBuffer() {
		logger.info("Returning the current state of the scan buffer. Size: {}", scanBuffer.size());
		return new ArrayList<>(scanBuffer); // Returns a copy
	}

	/**
	 * Sets the ClamAV scanner instance.
	 *
	 * @param clamAV The ClamAV instance to be set.
	 */
	public void setClamAV(ClamAV clamAV) {
		this.clamAV = clamAV;
	}

	/**
	 * Sets the VirusTotal scanner instance.
	 *
	 * @param virusTotal The VirusTotal instance to be set.
	 */
	public void setVirusTotal(VirusTotal virusTotal) {
		this.virusTotal = virusTotal;
	}
}
