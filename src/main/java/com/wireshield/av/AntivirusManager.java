package com.wireshield.av;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.wireshield.enums.runningStates;
import com.wireshield.enums.warningClass;

/**
 * AntivirusManager manages antivirus scanning of files. It adds files to a scan
 * buffer, scans them with ClamAV and VirusTotal, and keeps track of the scan
 * results.
 */
public class AntivirusManager {

	private static final Logger logger = LogManager.getLogger(AntivirusManager.class);

	private ClamAV clamAV; // ClamAV scanner instance
	private VirusTotal virusTotal; // VirusTotal scanner instance
	private List<File> scanBuffer = new ArrayList<>(); // List of files to scan
	private List<File> filesToRemove = new ArrayList<>(); // Files to remove after scanning
	private List<ScanReport> finalReports = new ArrayList<>(); // Stores the final scan reports
	private runningStates scannerStatus; // Current state of the scanner

	/**
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
		if (file != null && file.exists()) {
			if (!scanBuffer.contains(file)) {
				scanBuffer.add(file);
				logger.info("File added to scan buffer: {}", file.getName());
				notify(); // Notify the scanning thread that a new file is available
			} else {
				logger.warn("File is already in the scan buffer: {}", file.getName());
			}
		} else {
			logger.error("Invalid file or file does not exist: {}", file);
		}
	}

	/**
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

		// Continue scanning until the process is stopped
		while (scannerStatus == runningStates.UP) {
			if (scanBuffer.isEmpty()) {
				logger.info("No files to scan, waiting for new files...");
				synchronized (this) {
					try {
						wait(); // Wait for notification that a new file is available
					} catch (InterruptedException e) {
						logger.error("Scanning interrupted.", e);
						break; // Exit if the scanning is interrupted
					}
				}
				continue; // Recheck the buffer when a new file is added
			}

			// Create a temporary list for this cycle to store files that were scanned
			List<File> currentFilesToScan = new ArrayList<>(scanBuffer);

			for (File file : currentFilesToScan) {
				logger.info("Scanning file: {}", file.getName());
				ScanReport finalReport = new ScanReport(); // Consolidated report for the file
				finalReport.setFile(file);

				// Scan with ClamAV
				// Scan with ClamAV
				if (clamAV != null) {
				    clamAV.analyze(file, finalReport); // Update report with ClamAV's scan
				    ScanReport clamAVReport = clamAV.getReport(); // Get ClamAV's report
				    if (clamAVReport != null) {
				        mergeReports(finalReport, clamAVReport); // Merge ClamAV report into finalReport
				    }
				}

				// Scan with VirusTotal if ClamAV flag it as a severe threat
				if (finalReport.isThreatDetected() && virusTotal != null) {
				    virusTotal.analyze(file, finalReport); // Update report with VirusTotal's scan
				    ScanReport virusTotalReport = virusTotal.getReport(); // Get VirusTotal's report
				    if (virusTotalReport != null) {
				        mergeReports(finalReport, virusTotalReport); // Merge VirusTotal report into finalReport
				    }
				}

				// If a threat is detected, log it and add the file to the removal list
				if (finalReport.getWarningClass() == warningClass.DANGEROUS
						|| finalReport.getWarningClass() == warningClass.SUSPICIOUS) {
					logger.warn("Threat detected in file: {}", file.getName());
					filesToRemove.add(file); // Add file to removal list
				}

				// Add the consolidated report to finalReports
				finalReports.add(finalReport);
				scanBuffer.remove(file); // Remove the file from the buffer after scanning
				logger.info("Scan completed for file: {}", file.getName());
				logger.info("Scan report for file {}: {}", file.getName(), finalReport);
			}
		}

		logger.info("Scanning process finished or stopped");
	}

	/**
	 * Stops the scanning process.
	 */
	public void stopPerformScan() {
		scannerStatus = runningStates.DOWN; // Set status to DOWN (stopped)
		logger.info("Scanning process stopped.");
	}

	/**
	 * Merges the attributes of a source ScanReport into a target ScanReport.
	 *
	 * @param target The ScanReport to be updated.
	 * @param source The ScanReport with additional details.
	 */
	private void mergeReports(ScanReport target, ScanReport source) {
	    if (source != null && source.isThreatDetected()) {
	        // Aggiornamento della variabile threatDetected
	        target.setThreatDetected(true);

	        // Gestione dei dettagli della minaccia
	        if (target.getThreatDetails().equals("No threat detected")) {
	            target.setThreatDetails(source.getThreatDetails());
	        } else {
	            target.setThreatDetails(target.getThreatDetails() + "; " + source.getThreatDetails());
	        }

	        // Impostazione della classe di avviso più grave
	        if (source.getWarningClass().compareTo(target.getWarningClass()) > 0) {
	            target.setWarningClass(source.getWarningClass());
	        }
	    }

	    // Aggiornamento della validità del report
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
	 * Returns the final scan reports, which includes the results of all scanned
	 * files.
	 *
	 * @return The final scan reports.
	 */
	public List<ScanReport> getFinalReports() {
		return finalReports;
	}

	/**
	 * Returns the current list of files in the scan buffer.
	 *
	 * @return The list of files to be scanned.
	 */
	public List<File> getScanBuffer() {
		return scanBuffer;
	}

	// Aggiungi i metodi per impostare ClamAV e VirusTotal
	public void setClamAV(ClamAV clamAV) {
		this.clamAV = clamAV;
	}

	public void setVirusTotal(VirusTotal virusTotal) {
		this.virusTotal = virusTotal;
	}

/*	public static void main(String[] args) {
		// Creazione di ClamAV e VirusTotal
		ClamAV clamAV = new ClamAV();
		VirusTotal virusTotal = new VirusTotal();

		// Creazione di AntivirusManager passando gli oggetti direttamente nel
		// costruttore
		AntivirusManager antivirusManager = new AntivirusManager();

		// Impostazione di ClamAV e VirusTotal
		antivirusManager.setClamAV(clamAV);
		antivirusManager.setVirusTotal(virusTotal);

		// Aggiungi alcuni file al buffer di scansione (creando file vuoti se non
		// esistono)
		antivirusManager.addFileToScanBuffer(new File("file1.exe"));
		antivirusManager.addFileToScanBuffer(new File("file2.txt"));
		antivirusManager.addFileToScanBuffer(new File("file3.pdf"));

		// Avvio della scansione in un thread separato
		Thread scanThread = new Thread(() -> {
			antivirusManager.startPerformScan(); // Avvia la scansione
		});
		scanThread.start();

		// Aggiunta di un altro file durante la scansione
		try {
			Thread.sleep(5000); // Attendi 5 secondi
			antivirusManager.addFileToScanBuffer(new File("file4.exe")); // Aggiungi un nuovo file al buffer
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Fermare la scansione
		try {
			Thread.sleep(10000); // Attendi 10 secondi
			antivirusManager.stopPerformScan(); // Ferma la scansione
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Stampa i report finali
		antivirusManager.getFinalReports().forEach(report -> {
			System.out.println("File: " + report.getFile().getName());
			System.out.println("Threat Detected: " + report.isThreatDetected());
			System.out.println("Threat Details: " + report.getThreatDetails());
			System.out.println("Warning Class: " + report.getWarningClass());
			System.out.println("Is Valid: " + report.isValidReport());
			System.out.println("----------");
		});
	} */
}
