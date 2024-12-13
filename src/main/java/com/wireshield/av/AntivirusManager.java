package com.wireshield.av;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import com.wireshield.enums.runningStates;
import java.util.List;
import java.util.ArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * AntivirusManager is a singleton class that manages antivirus scanning.
 * It buffers files to be scanned, performs scans using ClamAV and VirusTotal,
 * and maintains a record of scan results.
 */
public class AntivirusManager {

    private static final Logger logger = LogManager.getLogger(AntivirusManager.class);

    private static AntivirusManager instance; // Singleton instance

    private ClamAV clamAV; // ClamAV scanner instance
    private VirusTotal virusTotal; // VirusTotal scanner instance
    private ScanReport finalReports; // Stores the final scan report
    private Map<File, Boolean> scanBuffer; // Map of files to scan and their scan status
    private runningStates scannerStatus; // Current state of the scanner
    private List<File> filesToRemove = new ArrayList<>(); // List of files to remove after scanning

    /**
     * Private constructor to enforce singleton pattern.
     */
    private AntivirusManager() {
        this.scanBuffer = new HashMap<>();
        this.scannerStatus = runningStates.DOWN;
        logger.info("AntivirusManager initialized.");
    }

    /**
     * Returns the singleton instance of AntivirusManager.
     *
     * @return The AntivirusManager instance.
     */
    public static synchronized AntivirusManager getInstance() {
        if (instance == null) {
            instance = new AntivirusManager();
            logger.info("AntivirusManager instance created.");
        }
        return instance;
    }

    /**
     * Adds a file to the scan buffer if it has not already been scanned.
     *
     * @param file The file to add.
     */
    public void addFileToScanBuffer(File file) {
        if (file != null && file.exists()) {
            if (!scanBuffer.containsKey(file)) {
                scanBuffer.put(file, false);
                logger.info("File added to scan buffer: {}", file.getName());
            } else {
                logger.warn("File is already in the scan buffer: {}", file.getName());
            }
        } else {
            logger.error("Invalid file or file does not exist: {}", file);
        }
    }

    /**
     * Performs antivirus scanning on files in the scan buffer.
     */
    public void performScan() {
        logger.info("Starting scan on {} file(s) in the scan buffer.", scanBuffer.size());

        for (Map.Entry<File, Boolean> entry : scanBuffer.entrySet()) {
            File file = entry.getKey();
            Boolean isScanned = entry.getValue();

            if (!isScanned) {
                logger.info("Scanning file: {}", file.getName());
                ScanReport report = new ScanReport(); // Holds scan details

                // Perform scanning with ClamAV and VirusTotal
                if (clamAV != null) {
                    clamAV.analyze(file, report);
                }
                if (virusTotal != null) {
                    virusTotal.analyze(file, report);
                }

                // Mark the file as scanned
                scanBuffer.put(file, true);
                logger.info("Scan completed for file: {}", file.getName());

                // Add file to removal list
                filesToRemove.add(file);
            } else {
                logger.debug("File already scanned: {}", file.getName());
            }
        }

        // Remove scanned files from the buffer
        for (File file : filesToRemove) {
            scanBuffer.remove(file);
            logger.info("Removed file from buffer: {}", file.getName());
        }
        filesToRemove.clear(); // Clear the removal list

        logger.info("Scan process completed.");
    }

    /**
     * Returns the final scan report.
     *
     * @return The ScanReport object.
     */
    protected ScanReport getReport() {
        logger.debug("Retrieving final scan report.");
        return finalReports;
    }

    /**
     * Returns the current status of the antivirus scanner.
     *
     * @return The runningStates of the scanner.
     */
    protected runningStates getStatus() {
        logger.debug("Retrieving scanner status: {}", scannerStatus);
        return scannerStatus;
    }

    /**
     * Returns the current scan buffer.
     *
     * @return A map of files and their scan status.
     */
    public Map<File, Boolean> getScanBuffer() {
        logger.debug("Retrieving scan buffer.");
        return scanBuffer;
    }

    /**
     * Main method for testing the AntivirusManager class.
     */
    /*
    public static void main(String[] args) {
        File file1 = new File("testfile1.txt");
        File file2 = new File("testfile2.txt");

        // Create temporary files for testing
        try {
            if (file1.createNewFile()) {
                logger.info("Created file: {}", file1.getName());
            }
            if (file2.createNewFile()) {
                logger.info("Created file: {}", file2.getName());
            }
        } catch (IOException e) {
            logger.error("Error creating test files.", e);
        }

        // Get the instance of AntivirusManager
        AntivirusManager antivirusManager = AntivirusManager.getInstance();

        // Add files to the scan buffer
        antivirusManager.addFileToScanBuffer(file1);
        antivirusManager.addFileToScanBuffer(file2);

        // Perform scan
        antivirusManager.performScan();

        // Verify the buffer state
        logger.info("Scan buffer after scan: {} file(s) remaining.", antivirusManager.getScanBuffer().size());
    }
    */
}
