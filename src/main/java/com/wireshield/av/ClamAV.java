package com.wireshield.av;

import com.wireshield.localfileutils.File;

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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'analyze'");
    };

}
