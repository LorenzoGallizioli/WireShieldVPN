package com.wireshield.av;

import java.io.File;

import com.wireshield.enums.warningClass;

/**
 * The ClamAV class is responsible for scanning files using ClamAV.
 */
public class ClamAV implements AVInterface {

    private ScanReport report;

    /**
     * The constructor for the ClamAV class.
     */
    public ClamAV() {}

    /**
     * Returns the scan report.
     * 
     * @return ScanReport
     *   The scan report.
     */
    public ScanReport getReport() {
        return report;
    }

    /**
     * Performs a scan of a file using ClamAV.
     * 
     * @param file
     *   The file to scan.
     * @param report
     *   The scan report.
     */
    @Override
    public void analyze(File file, ScanReport report) {
        // For testing purposes, simulate a scan result
        if (file.getName().endsWith(".exe")) {
            // Simulate detecting a virus in .exe files
            report.setThreatDetected(true);
            report.setThreatDetails("Trojan detected in " + file.getName());
            report.setWarningClass(warningClass.DANGEROUS);
        } else {
            // Simulate no threat for other types of files
            report.setThreatDetected(false);
            report.setThreatDetails("No threat detected in " + file.getName());
            report.setWarningClass(warningClass.CLEAR);
        }
    }
}
