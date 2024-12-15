package com.wireshield.av;

import java.io.File;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.wireshield.enums.warningClass;

/*
 * This class handles file analysis using ClamAV, an antivirus software.
 * It scans files for potential threats (viruses, malware) and generates
 * detailed scan reports based on the scan results.
 * 
 * It interacts with the ClamAV command-line tool (`clamscan.exe`) to perform
 * the actual file scanning and processes its output to classify the file
 * as either clean, suspicious, or containing a threat.
 */
public class ClamAV {

	// Logger for logging activities and debugging, using Log4j for structured
	// logging
	private static final Logger logger = LogManager.getLogger(ClamAV.class);

	// Holds the most recent scan report after a file has been analyzed
	private ScanReport clamavReport;

	/*
	 * Constructor to initialize the ClamAV object. It initializes the
	 * `clamavReport` to null as no scan has been performed yet.
	 */
	public ClamAV() {
		this.clamavReport = null;
		logger.info("ClamAV initialized.");
	}

	/**
	 * Retrieves the scan report of the most recently analyzed file.
	 * 
	 * @return The ScanReport object containing details of the last scan.
	 */
	public ScanReport getReport() {
		return clamavReport;
	}

	/**
	 * Analyzes a given file using ClamAV antivirus to check for potential threats.
	 * The method runs the ClamAV scanner (clamscan) as an external process, reads
	 * its output, and classifies the file as clean, suspicious, or containing a
	 * threat.
	 * 
	 * @param file The file to be analyzed.
	 */
	public void analyze(File file) {
		// Check if the file is valid (exists) before scanning
		if (file == null || !file.exists()) {
			clamavReport = new ScanReport(); // Create a new empty scan report
			clamavReport.setFile(file);
			clamavReport.setValid(false); // The file is not valid if it doesn't exist
			clamavReport.setThreatDetails("File does not exist.");
			clamavReport.setWarningClass(warningClass.CLEAR); // No threat detected
			logger.warn("File does not exist: " + file);
			return;
		}

		try {
			// Path to the ClamAV executable (clamscan) - ensure this path is correct
			String clamavPath = "C:\\Program Files\\ClamAV\\clamscan.exe"; // Adjust path if necessary
			logger.info("ClamAV path: " + clamavPath);

			// Create a process to run the ClamAV scanner on the specified file
			ProcessBuilder processBuilder = new ProcessBuilder(clamavPath, file.getAbsolutePath());
			processBuilder.redirectErrorStream(true); // Merge standard output and error streams
			Process process = processBuilder.start(); // Start the ClamAV scan process

			// Read the output from the ClamAV scan process
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			boolean threatDetected = false; // Flag to indicate if a threat is found
			boolean suspiciousDetected = false; // Flag for suspicious files
			String threatDetails = ""; // String to hold the threat details if found

			// Process the output of ClamAV line by line
			while ((line = reader.readLine()) != null) {
				logger.debug("ClamAV output: " + line); // Log ClamAV output for debugging

				// Check if the line contains the word "FOUND", which indicates a virus or
				// malware
				if (line.contains("FOUND")) {
					threatDetected = true; // Mark as threat detected
					threatDetails = line.substring(line.indexOf(":") + 2, line.lastIndexOf("FOUND")).trim();
					logger.info("Threat detected: " + threatDetails);
					break; // Stop reading as we have already detected a threat
				}
				// Check if the line contains the word "suspicious", indicating suspicious
				// activity
				else if (line.contains("suspicious")) {
					suspiciousDetected = true; // Mark as suspicious file detected
					threatDetails = line.substring(line.indexOf(":") + 2).trim(); // Extract suspicious details
					logger.info("Suspicious activity detected: " + threatDetails);
					break; // Stop reading since we have identified suspicious activity
				}
			}

			// Create the scan report based on the findings
			clamavReport = new ScanReport(); // Create a new ScanReport object
			clamavReport.setFile(file); // Set the file that was scanned
			clamavReport.setValid(true); // Mark the file as valid for analysis
			clamavReport.setThreatDetected(threatDetected || suspiciousDetected); // Set if threat or suspicious was
																					// found

			// Handle the different possible outcomes based on the analysis
			if (threatDetected) {
				clamavReport.setThreatDetails(threatDetails); // Set threat details if a threat is detected
				clamavReport.setWarningClass(warningClass.DANGEROUS); // Mark as dangerous if a virus is found
				logger.warn("Threat found, marking as dangerous.");
			} else if (suspiciousDetected) {
				clamavReport.setThreatDetails("Suspicious activity detected");
				clamavReport.setWarningClass(warningClass.SUSPICIOUS); // Mark as suspicious if suspicious content is
																		// found
				logger.warn("Suspicious activity detected, marking as suspicious.");
			} else {
				clamavReport.setThreatDetails("No threat detected");
				clamavReport.setWarningClass(warningClass.CLEAR); // No threat found, so file is clean
				logger.info("No threat detected.");
			}

			// Close the reader after reading the entire output
			reader.close();

		} catch (IOException e) {
			// Handle any IOExceptions that may occur while running the scan
			clamavReport = new ScanReport(); // Create a new scan report in case of error
			clamavReport.setFile(file);
			clamavReport.setValid(false); // File is not valid if an error occurs during scanning
			clamavReport.setThreatDetails("Error during scan: " + e.getMessage());
			clamavReport.setWarningClass(warningClass.CLEAR); // Set as clear if there's an error
			logger.error("Error during scan: " + e.getMessage(), e); // Log the error
		}
	}

	// The following main method is for manual testing and is currently commented
	// out.
	public static void main(String[] args) {
		File fileToScan = new File("C:/Users/bnsda/Downloads/eicar.com");

		ClamAV clamAV = new ClamAV();
		clamAV.analyze(fileToScan);

		ScanReport report = clamAV.getReport();
		if (report != null) {
			System.out.println("\n--- Scan Report ---");
			System.out.println("File: " + report.getFile().getAbsolutePath());
			System.out.println("Valid: " + report.isValidReport());
			System.out.println("Threat Detected: " + report.isThreatDetected());
			System.out.println("Threat Details: " + report.getThreatDetails());
			System.out.println("Warning Class: " + report.getWarningClass());
		} else {
			System.out.println("No report generated.");
		}
	}
}
