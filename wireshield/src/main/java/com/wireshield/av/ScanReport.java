package com.wireshield.av;

import java.io.File;
import com.wireshield.enums.warningClass;

/*
 * Represents the scan report for a file, including threat detection results and details.
 */
public class ScanReport {

    private Boolean threatDetected;  // Indicates if a threat was detected in the file
    private String threatDetails;    // Details about the detected threat
    private File file;               // The file that was scanned
    private warningClass warningState;  // The classification of the warning (e.g., clear, suspicious, dangerous)
    private Boolean isValid;         // Indicates whether the scan report is valid
    private String scanId;           // The scan ID generated by VirusTotal for the scan
    private String sha256;           // SHA256 hash of the file
    
    // Threat detection counters
    private int maliciousCount;      // Number of malicious detections
    private int harmlessCount;       // Number of harmless detections
    private int suspiciousCount;     // Number of suspicious detections
    private int undetectedCount;     // Number of undetected detections

    /*
     * Default constructor that initializes the scan report with default values.
     */
    public ScanReport() {
        this.threatDetected = false;
        this.threatDetails = "No threat detected";
        this.warningState = warningClass.CLEAR;
        this.isValid = true;
        this.maliciousCount = 0;
        this.harmlessCount = 0;
        this.suspiciousCount = 0;
        this.undetectedCount = 0;
        this.scanId = "";  // Default empty scanId
    }

    /**
     * Constructor that initializes the scan report with a given scanId and file.
     * @param scanId The scan ID
     * @param file The file that was scanned
     */
    public ScanReport(String scanId, File file) {
        this();
        this.scanId = scanId;  // Set the scan ID
        this.file = file;
    }

    /**
     * Gets the SHA256 hash of the file.
     * @return The SHA256 hash
     */
    public String getSha256() {
        return sha256;
    }

    /**
     * Sets the SHA256 hash of the file.
     * @param sha256 The SHA256 hash
     */
    public void setSha256(String sha256) {
        this.sha256 = sha256;
    }
    
    /**
     * Gets the scan ID.
     * @return The scan ID
     */
    public String getScanId() {
        return scanId;
    }

    /**
     * Checks if a threat was detected based on the warning classification.
     * @return True if a threat was detected, otherwise false
     */
    public Boolean isThreatDetected() {
        return warningState == warningClass.DANGEROUS || warningState == warningClass.SUSPICIOUS;
    }

    /**
     * Sets whether a threat was detected.
     * @param threatDetected The threat detection status
     */
    public void setThreatDetected(Boolean threatDetected) {
        this.threatDetected = threatDetected;
    }

    /**
     * Gets the details of the threat detected.
     * @return The threat details
     */
    public String getThreatDetails() {
        return threatDetails;
    }

    /**
     * Sets the details of the detected threat.
     * @param threatDetails The threat details
     */
    public void setThreatDetails(String threatDetails) {
        this.threatDetails = threatDetails;
    }

    /**
     * Gets the file that was scanned.
     * @return The file
     */
    public File getFile() {
        return file;
    }

    /**
     * Sets the file that was scanned.
     * @param file The file
     */
    public void setFile(File file) {
        this.file = file;
    }

    /**
     * Gets the warning classification of the scan.
     * @return The warning class
     */
    public warningClass getWarningClass() {
        return warningState;
    }

    /**
     * Sets the warning classification of the scan.
     * @param warningClass The warning classification
     */
    public void setWarningClass(warningClass warningClass) {
        this.warningState = warningClass;
    }

    /**
     * Checks if the scan report is valid.
     * @return True if the report is valid, otherwise false
     */
    public Boolean isValidReport() {
        return isValid;
    }

    /**
     * Sets the validity of the scan report.
     * @param isValid The validity status
     */
    public void setValid(Boolean isValid) {
        this.isValid = isValid;
    }

    /**
     * Gets the number of malicious detections in the scan.
     * @return The malicious count
     */
    public int getMaliciousCount() {
        return maliciousCount;
    }

    /**
     * Sets the number of malicious detections in the scan.
     * @param maliciousCount The malicious count
     */
    public void setMaliciousCount(int maliciousCount) {
        this.maliciousCount = maliciousCount;
    }

    /**
     * Gets the number of harmless detections in the scan.
     * @return The harmless count
     */
    public int getHarmlessCount() {
        return harmlessCount;
    }

    /**
     * Sets the number of harmless detections in the scan.
     * @param harmlessCount The harmless count
     */
    public void setHarmlessCount(int harmlessCount) {
        this.harmlessCount = harmlessCount;
    }

    /**
     * Gets the number of suspicious detections in the scan.
     * @return The suspicious count
     */
    public int getSuspiciousCount() {
        return suspiciousCount;
    }

    /**
     * Sets the number of suspicious detections in the scan.
     * @param suspiciousCount The suspicious count
     */
    public void setSuspiciousCount(int suspiciousCount) {
        this.suspiciousCount = suspiciousCount;
    }

    /**
     * Gets the number of undetected detections in the scan.
     * @return The undetected count
     */
    public int getUndetectedCount() {
        return undetectedCount;
    }

    /**
     * Sets the number of undetected detections in the scan.
     * @param undetectedCount The undetected count
     */
    public void setUndetectedCount(int undetectedCount) {
        this.undetectedCount = undetectedCount;
    }

    /*
     * Prints the scan report in a human-readable format.
     */
    public void printReport() {
        String separator = "-------------------------------------------------";
        
        System.out.println(separator);
        System.out.println("Scan Report");
        System.out.println(separator);
        
        // Display all information in a tabular format
        System.out.printf("%-20s: %s%n", "File", file.getName());
        System.out.printf("%-20s: %s%n", "SHA256 Hash", getSha256() != null ? getSha256() : "Not Available");
        System.out.printf("%-20s: %s%n", "Threat Detected", isThreatDetected() ? "Yes" : "No");
        System.out.printf("%-20s: %s%n", "Threat Details", threatDetails);
        System.out.printf("%-20s: %s%n", "Warning Class", warningState);
        System.out.printf("%-20s: %s%n", "Report Status", isValidReport() ? "Valid" : "Invalid");
        System.out.printf("%-20s: %d%n", "Malicious Count", maliciousCount);
        System.out.printf("%-20s: %d%n", "Harmless Count", harmlessCount);
        System.out.printf("%-20s: %d%n", "Suspicious Count", suspiciousCount);
        System.out.printf("%-20s: %d%n", "Undetected Count", undetectedCount);
        
        System.out.println(separator);
    }

    /**
     * Returns a string representation of the scan report.
     * @return a string representation of the ScanReport object
     */
    @Override
    public String toString() {
        return "ScanReport {" + 
               "scanId='" + scanId + '\'' +
               ", file=" + (file != null ? file.getName() : "null") + 
               ", SHA256 Hash=" + (getSha256() != null ? getSha256() : "Not Available") +
               ", threatDetected=" + threatDetected + 
               ", threatDetails='" + threatDetails + '\'' + 
               ", warningClass=" + warningState + 
               ", isValid=" + isValid + 
               ", maliciousCount=" + maliciousCount + 
               ", harmlessCount=" + harmlessCount + 
               ", suspiciousCount=" + suspiciousCount + 
               ", undetectedCount=" + undetectedCount + 
               '}';
    }
}
