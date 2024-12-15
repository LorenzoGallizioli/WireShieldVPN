package com.wireshield.av;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import com.wireshield.enums.warningClass;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Test class for VirusTotal. This class contains unit tests to verify the
 * functionality of the VirusTotal class in detecting threats in files.
 */
public class VirusTotalTest {

    // VirusTotal object to test
    private VirusTotal virusTotal;  
    // Example files for testing
    private File validFile;
    private File suspiciousFile;
    private File invalidFile;

    /**
     * Setup method for tests. Creates temporary files to be used during tests.
     * This is run before each test.
     */
    @Before
    public void setUp() throws IOException {
        virusTotal = new VirusTotal();  // Initializes the VirusTotal object to be tested

        // Create temporary files for testing
        validFile = File.createTempFile("validfile", ".txt");
        writeToFile(validFile, "This is a valid file for testing.");

        suspiciousFile = File.createTempFile("suspiciousfile", ".txt");
        writeSuspiciousFile(suspiciousFile);  // Write suspicious content to simulate threat

        invalidFile = new File("nonexistentfile.txt");  // Non-existent file
    }

    /**
     * Cleanup method after each test. Deletes the temporary files created for tests.
     * This is run after each test.
     */
    @After
    public void tearDown() {
        // Deletes temporary files after the tests
        if (validFile != null && validFile.exists()) {
            validFile.delete();
        }
        if (suspiciousFile != null && suspiciousFile.exists()) {
            suspiciousFile.delete();
        }
        if (invalidFile != null && invalidFile.exists()) {
            invalidFile.delete();
        }
    }

    /**
     * Helper method to write content to a file.
     * 
     * @param file The file to write to
     * @param content The content to write into the file
     * @throws IOException If an error occurs during file writing
     */
    private void writeToFile(File file, String content) throws IOException {
        FileWriter writer = new FileWriter(file);
        writer.write(content);
        writer.close();
    }

    /**
     * Helper method to write suspicious content to a file.
     * This simulates suspicious file content for testing purposes.
     * 
     * @param file The file to write suspicious content to
     * @throws IOException If an error occurs during file writing
     */
    private void writeSuspiciousFile(File file) throws IOException {
        FileWriter writer = new FileWriter(file);
        // Simulate suspicious content in the file
        writer.write("This file contains suspicious content that mimics known malware signatures.");
        writer.close();
    }

    /**
     * Test for the analyze() method. Verifies that the analysis function
     * produces the correct results for different types of files:
     * 
     * - Valid file
     * - Suspicious file
     * - Non-existent file
     */
    @Test
    public void testAnalyze() throws IOException {
        // Test for the valid file
        virusTotal.analyze(validFile);
        ScanReport validReport = virusTotal.getReport();
        assertNotNull(validReport);  // The report should not be null
        assertFalse(validReport.isThreatDetected());  // No threat should be detected
        assertEquals("The file is clean: no threat detected.", validReport.getThreatDetails());  // Correct details
        assertEquals(warningClass.CLEAR, validReport.getWarningClass());  // Warning class should be CLEAR

        // Test for the suspicious file
        virusTotal.analyze(suspiciousFile);
        ScanReport suspiciousReport = virusTotal.getReport();
        assertNotNull(suspiciousReport);  // The report should not be null
        assertTrue(suspiciousReport.isThreatDetected());  // A suspicious threat should be detected
        assertEquals("Suspicious file: multiple suspicious detections.", suspiciousReport.getThreatDetails());  // Correct details
        assertEquals(warningClass.SUSPICIOUS, suspiciousReport.getWarningClass());  // Warning class should be SUSPICIOUS

        // Test for the non-existent file
        virusTotal.analyze(invalidFile);
        ScanReport invalidReport = virusTotal.getReport();
        assertNotNull(invalidReport);  // The report should not be null
        assertFalse(invalidReport.isThreatDetected());  // No threat should be detected
        assertEquals("File does not exist.", invalidReport.getThreatDetails());  // Correct details
        assertEquals(warningClass.CLEAR, invalidReport.getWarningClass());  // Warning class should be CLEAR
    }

    /**
     * Test for the analyze() method using the EICAR test virus file.
     * Verifies that the EICAR test file is detected as a threat.
     */
    @Test
    public void testAnalyzeWithEICAR() throws IOException {
        // Create a test file with the EICAR content
        File eicarFile = File.createTempFile("eicar_test", ".com");
        writeToFile(eicarFile, "X5O!P%@AP[4\\PZX54(P^)7CC)7}$A*G!"); // EICAR content

        // Analyze the EICAR file
        virusTotal.analyze(eicarFile);
        ScanReport eicarReport = virusTotal.getReport();

        // Assert that it is detected as a threat
        assertNotNull(eicarReport);
        assertTrue(eicarReport.isThreatDetected());  // This should detect the EICAR test virus
        assertEquals("Low risk: some suspicious or malicious detections.", eicarReport.getThreatDetails());  // Correct details
        assertEquals(warningClass.SUSPICIOUS, eicarReport.getWarningClass());  // Warning class should be suspicious
    }

    /**
     * Test for the getReport() method. Verifies that getReport returns the correct
     * report after analyzing a valid file.
     * 
     * Verifies that the report contains the correct details for the analyzed file.
     */
    @Test
    public void testGetReport() throws IOException {
        // First, analyze the valid file
        virusTotal.analyze(validFile);

        // Now test that getReport returns the correct report
        ScanReport report = virusTotal.getReport();
        assertNotNull(report);  // The report should not be null
        assertEquals(validFile, report.getFile());  // The analyzed file should match the valid file
        assertFalse(report.isThreatDetected());  // No threat should be detected
        assertEquals("The file is clean: no threat detected.", report.getThreatDetails());  // Correct details
        assertEquals(warningClass.CLEAR, report.getWarningClass());  // Warning class should be CLEAR
    }
}
