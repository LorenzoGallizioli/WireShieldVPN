package com.wireshield.av;

import java.io.File;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.wireshield.enums.warningClass;

/*
 * Implements antivirus scanning functionality using ClamAV.
 * This class uses the ClamAV command-line tool (`clamscan.exe`) to analyze files
 * for potential threats. It processes the output from ClamAV to generate detailed
 * scan reports.
 * This class follows the Singleton design pattern to ensure a single instance is used
 * throughout the application.
 */
public class ClamAV implements AVInterface {

	// Logger for logging ClamAV-related information and errors.
	private static final Logger logger = LogManager.getLogger(ClamAV.class);

	// Singleton instance of ClamAV.
	private static ClamAV instance;
	// Scan report generated after the most recent file analysis.
	private ScanReport clamavReport;

	/*
	 * Private constructor to enforce Singleton pattern.
	 * Initializes ClamAV and logs the creation of the instance.
	 */
	private ClamAV() {
		this.clamavReport = null; // Initialize the scan report as null
		logger.info("ClamAV initialized.");
	}

    /**
     * Retrieves the Singleton instance of ClamAV.
     * Ensures that only one instance of this class is created and used throughout the application.
     * 
     * @return The single instance of ClamAV.
     */
	public static synchronized ClamAV getInstance() {
		if (instance == null) {
			instance = new ClamAV();
		}
		return instance;
	}

    /**
     * Analyzes a file for potential threats using ClamAV.
     * This method interacts with the ClamAV command-line tool (`clamscan.exe`) to scan
     * the specified file. The scan results are processed and stored in a scan report.
     * 
     * @param file The file to be analyzed. It must not be null and must exist on the filesystem.
     */
	public void analyze(File file) {
		// Check if the file exists before proceeding
		if (file == null || !file.exists()) {
			clamavReport = new ScanReport(); // Initialize the scan report
			clamavReport.setFile(file);
			clamavReport.setValid(false);
			clamavReport.setThreatDetails("File does not exist.");
			clamavReport.setWarningClass(warningClass.CLEAR); // Mark the file as clear (no threat)

			if (file == null) {
				logger.warn("il file è nullo.");
			} else {
				logger.warn("File does not exist: {}", file.getAbsolutePath());
			}

			return;
		}

		try {
			// Define the path to the ClamAV command-line tool (clamscan.exe)
			String clamavPath = "C:\\Program Files\\ClamAV\\clamscan.exe";
			logger.info("ClamAV path: {}", clamavPath);

			// Initialize the process builder to run the ClamAV scan command
			ProcessBuilder processBuilder = new ProcessBuilder(clamavPath, file.getAbsolutePath());
			processBuilder.redirectErrorStream(true); // Redirect error stream to the output stream
			Process process = processBuilder.start(); // Start the scan process

			// Read the output from the ClamAV scan process
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			boolean threatDetected = false; // Flag for threat detection
			boolean suspiciousDetected = false; // Flag for suspicious activity detection
			String threatDetails = ""; // To store the threat details

			// Process each line of ClamAV output
			while ((line = reader.readLine()) != null) {
				logger.debug("ClamAV output: {}", line);

				// Check if ClamAV has found a threat
				if (line.contains("FOUND")) {
					threatDetected = true;
					threatDetails = line.substring(line.indexOf(":") + 2, line.lastIndexOf("FOUND")).trim();
					logger.info("Threat detected: {}", threatDetails);
					break; // Stop processing after a threat is found

				} else if (line.contains("suspicious")) { // Check if ClamAV has detected suspicious activity
					suspiciousDetected = true;
					threatDetails = line.substring(line.indexOf(":") + 2).trim();
					logger.info("Suspicious activity detected: {}", threatDetails);
					break; // Stop processing after suspicious activity is detected
				}
			}

			// Prepare the scan report
			clamavReport = new ScanReport();
			clamavReport.setFile(file);
			clamavReport.setValid(true); // Mark the report as valid
			clamavReport.setThreatDetected(threatDetected || suspiciousDetected); // Mark as true if any threat or
																					// suspicious activity detected

			// Handle the detected threats or suspicious activity
			if (threatDetected) {
				clamavReport.setThreatDetails(threatDetails); // Set threat details
				clamavReport.setWarningClass(warningClass.DANGEROUS); // Classify as dangerous
				logger.warn("Threat found, marking as dangerous.");
			} else if (suspiciousDetected) {
				clamavReport.setThreatDetails("Suspicious activity detected");
				clamavReport.setWarningClass(warningClass.SUSPICIOUS); // Classify as suspicious
				logger.warn("Suspicious activity detected, marking as suspicious.");
			} else {
				clamavReport.setThreatDetails("No threat detected");
				clamavReport.setWarningClass(warningClass.CLEAR); // Mark as clear (no threat)
				logger.info("No threat detected.");
			}

			reader.close(); // Close the reader after processing the output

		} catch (IOException e) {
			// Handle errors during the scanning process
			clamavReport = new ScanReport();
			clamavReport.setFile(file);
			clamavReport.setValid(false); // Mark the report as invalid
			clamavReport.setThreatDetails("Error during scan: " + e.getMessage());
			clamavReport.setWarningClass(warningClass.CLEAR); // Mark as clear due to error
			logger.error("Error during scan: {}", e.getMessage(), e); // Log the error details
		}
	}

	// Returns the generated scan report
	public ScanReport getReport() {
		return clamavReport;
	}
}
