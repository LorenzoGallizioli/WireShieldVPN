package com.wireshield.av;

import java.io.File;

import com.wireshield.enums.warningClass;

/**
 * The ScanReport class is responsible for storing the scan report of a file.
 */
public class ScanReport {
    private Boolean threatDetected;
    private String threatDetails;
    private File file;
    private warningClass warningClass;
    private Boolean isValid;

    /**
     * The constructor for the ScanReport class.
     */
    public ScanReport() {}

    /**
     * Returns if a threat was detected in a file.
     * 
     * @return Boolean
     *   True if a threat was detected, false otherwise.
     */
    public Boolean isThreatDetected() {
        return threatDetected;
    }

    /**
     * Returns the details of the threat detected in a file.
     * 
     * @return String
     *   The details of the threat detected in a file. 
     */
    public String getThreatDetails() {
        return threatDetails;
    }

    /**
     * Returns the file that was scanned.
     * 
     * @return File
     *   The file that was scanned.
     */
    public File getFile() {
        return file;
    }

    /**
     * Returns the warning class of the scan report.
     * 
     * @return warningClass
     *   The warning class of the scan report.
     */
    public warningClass getWarningClass() {
        return warningClass;
    }

    /**
     * Returns if the scan report is valid.
     * 
     * @return Boolean
     *   True if the scan report is valid, false otherwise.
     */
    public Boolean isValidReport() {
        return isValid;
    }

    /**
     * Method to return a string representation of the scan report.
     * 
     * @return String
     *   The string representation of the scan report.
     */
    @Override
    public String toString() {
        return "";
    }
}
