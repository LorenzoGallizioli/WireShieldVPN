package com.wireshield.av;

import java.io.File;

/**
 * The VirusTotal class is responsible for scanning files using VirusTotal.
 */
public class VirusTotal implements AVInterface {

    private String endPoint;
    private String apiKey;
    private ScanReport report;

    /**
     * Constructor for the VirusTotal class.
     */
    public VirusTotal() {}

    /**
     * Imports VirusTotal API keys from a file.
     * 
     * @param filePath
     *   The path to the file containing the API keys.
     */
    public void importApisFromFile(String filePath) {}
    
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
     * Performs a scan of a file using VirusTotal.
     * 
     * @param file
     *   The file to scan.
     * @param report
     *   The scan report.
     */
    @Override
    public void analyze(File file, ScanReport report) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'analyze'");
    }
}
