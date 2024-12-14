package com.wireshield.av;

import java.io.File;
import com.wireshield.enums.warningClass;

/*
 * The ScanReport class is responsible for storing and managing the scan report of a file.
 * It includes information such as whether a threat was detected, details about the threat, 
 * the file being scanned, the warning classification, and the validity of the report.
 */
public class ScanReport {
    
    private Boolean threatDetected;  // Indicates if a threat was detected in the file
    private String threatDetails;    // Details about the detected threat
    private File file;               // The file that was scanned
    private warningClass warningState;  // The classification of the warning (e.g., clear, suspicious, dangerous)
    private Boolean isValid;         // Indicates whether the scan report is valid

    /*
     * Constructor for the ScanReport class. Initializes default values.
     */
    public ScanReport() {
        this.threatDetected = false;  // Default to no threat detected
        this.threatDetails = "No threat detected";  // Default message
        this.warningState = warningClass.CLEAR;  // Default warning class is CLEAR
        this.isValid = true;  // Default validity is true
    }

    // Getter and setter methods for all attributes

    /**
     * Checks if a threat was detected based on the warning class.
     *
     * @return true if a threat is detected (DANGEROUS or SUSPICIOUS), otherwise false.
     */
    public Boolean isThreatDetected() {
        return warningState == warningClass.DANGEROUS || warningState == warningClass.SUSPICIOUS;
    }

    /**
     * Sets whether a threat was detected.
     *
     * @param threatDetected true if a threat was detected, otherwise false.
     */
    public void setThreatDetected(Boolean threatDetected) {
        this.threatDetected = threatDetected;
    }

    /**
     * Returns the details about the detected threat.
     *
     * @return a string containing threat details.
     */
    public String getThreatDetails() {
        return threatDetails;
    }

    /**
     * Sets the details of the detected threat.
     *
     * @param threatDetails a string containing details about the threat.
     */
    public void setThreatDetails(String threatDetails) {
        this.threatDetails = threatDetails;
    }

    /**
     * Returns the file that was scanned.
     *
     * @return the file being scanned.
     */
    public File getFile() {
        return file;
    }

    /**
     * Sets the file that was scanned.
     *
     * @param file the file to be scanned.
     */
    public void setFile(File file) {
        this.file = file;
    }

    /**
     * Returns the warning class associated with the scan report.
     *
     * @return the warning classification of the scan (e.g., CLEAR, SUSPICIOUS, DANGEROUS).
     */
    public warningClass getWarningClass() {
        return warningState;
    }

    /**
     * Sets the warning class for the scan report.
     *
     * @param warningClass the warning class (e.g., CLEAR, SUSPICIOUS, DANGEROUS).
     */
    public void setWarningClass(warningClass warningClass) {
        this.warningState = warningClass;
    }

    /**
     * Checks if the scan report is valid.
     *
     * @return true if the report is valid, otherwise false.
     */
    public Boolean isValidReport() {
        return isValid;
    }

    /**
     * Sets the validity of the scan report.
     *
     * @param isValid true if the report is valid, otherwise false.
     */
    public void setValid(Boolean isValid) {
        this.isValid = isValid;
    }

    /**
     * Provides a string representation of the scan report.
     *
     * @return a string containing the details of the scan report.
     */
    @Override
    public String toString() {
        return "ScanReport {" + 
               "file=" + (file != null ? file.getName() : "null") + 
               ", threatDetected=" + threatDetected + 
               ", threatDetails='" + threatDetails + '\'' + 
               ", warningClass=" + warningState + 
               ", isValid=" + isValid + 
               '}';
    }
}
