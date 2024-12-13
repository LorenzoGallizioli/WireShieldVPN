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
    public ScanReport() {
        this.threatDetected = false; // Default to no threat detected
        this.threatDetails = "No threat detected"; // Default message
        this.warningClass = warningClass.CLEAR; // Default warning class
        this.isValid = true; // Default validity
    }

    // Getter and setter methods for all attributes

    public Boolean isThreatDetected() {
        return threatDetected;
    }

    public void setThreatDetected(Boolean threatDetected) {
        this.threatDetected = threatDetected;
    }

    public String getThreatDetails() {
        return threatDetails;
    }

    public void setThreatDetails(String threatDetails) {
        this.threatDetails = threatDetails;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public warningClass getWarningClass() {
        return warningClass;
    }

    public void setWarningClass(warningClass warningClass) {
        this.warningClass = warningClass;
    }

    public Boolean isValidReport() {
        return isValid;
    }

    public void setValid(Boolean isValid) {
        this.isValid = isValid;
    }

    /**
     * Method to return a string representation of the scan report.
     * 
     * @return String
     *   The string representation of the scan report.
     */
    @Override
    public String toString() {
        return "ScanReport {" +
               "file=" + (file != null ? file.getName() : "null") +
               ", threatDetected=" + threatDetected +
               ", threatDetails='" + threatDetails + '\'' +
               ", warningClass=" + warningClass +
               ", isValid=" + isValid +
               '}';
    }
}
