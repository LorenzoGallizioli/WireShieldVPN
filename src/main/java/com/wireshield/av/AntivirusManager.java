package com.wireshield.av;

import java.io.File;

import com.wireshield.enums.runningStates;

/**
 * The AntivirusManager class is responsible for managing the antivirus.
 */

public class AntivirusManager {
    private ClamAV clamAV;
    private VirusTotal virusTotal;
    private ScanReport finalReports;
    private File[] scanBuffer;
    private runningStates scannerStatus;

    /**
     * Constructor for the AntivirusManager class.
     */
    public AntivirusManager() {}

    /**
     * Method to perform the scan.
     * 
     * @param file
     *   The file to be scanned.
     * @param report
     *   The scan report.
     * @param status
     *   The scan status.
     */
    protected void performScan(File file, ScanReport report, runningStates status) {}

    /**
     * Method to add a file to the scan buffer.
     * 
     * @param file
     *   The file to be added.
     */
    protected void addFileToScanBuffer(File file){}

    /**
     * Method to get the scan report.
     * 
     * @return ScanReport
     *   The scan report.
     */
    protected ScanReport getReport() {
        return finalReports;
    }

    /**
     * Method to get the scan status.
     * 
     * @return runningStates
     *   The scan status.
     */
    protected runningStates getStatus() {
        return scannerStatus;
    }
}
