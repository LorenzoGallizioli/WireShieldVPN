package com.wireshield.av;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import com.wireshield.enums.warningClass;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
/*
 * Test class for VirusTotal. This class contains unit tests to verify the
 * functionality of the VirusTotal class in detecting threats in files.
 */
public class VirusTotalTest {

    // VirusTotal object to test
    private VirusTotal virusTotal;
    // Example files for testing
    private File validFile;
    private File eicarFile;

    /*
     * Setup method for tests. Creates temporary files to be used during tests. This
     * is run before each test.
     */
    @Before
    public void setUp() throws IOException {
        virusTotal = new VirusTotal(); // Initializes the VirusTotal object to be tested

        // Create temporary files for testing
        validFile = File.createTempFile("validfile", ".txt");
        writeToFile(validFile, "This is a valid file for testing.");

        // Create the EICAR test virus file
        eicarFile = File.createTempFile("eicar_test", ".com");
        writeToFile(eicarFile, "X5O!P%@AP[4\\PZX54(P^)7CC)7}$A*G!");
    }

    /*
     * Cleanup method after each test. Deletes the temporary files created for
     * tests. This is run after each test.
     */
    @After
    public void tearDown() {
        // Deletes temporary files after the tests
        if (validFile != null && validFile.exists()) {
            validFile.delete();
        }
        if (eicarFile != null && eicarFile.exists()) {
            eicarFile.delete();
        }
    }

    /**
     * Helper method to write content to a file.
     * 
     * @param file    The file to write to
     * @param content The content to write into the file
     * @throws IOException If an error occurs during file writing
     */
    private void writeToFile(File file, String content) throws IOException {
        FileWriter writer = new FileWriter(file);
        writer.write(content);
        writer.close();
    }

    /*
     * Test for the analyze() method. Verifies that the analysis function produces
     * the correct results for different types of files:
     * 
     * - Valid file - Suspicious file - Non-existent file
     */
    @Test
    public void testAnalyze() throws IOException {
        // Test for the valid file
        virusTotal.analyze(validFile);
        ScanReport validReport = virusTotal.getReport();
        assertNotNull(validReport); // The report should not be null
        assertFalse(validReport.isThreatDetected()); // No threat should be detected
        assertEquals("The file is clean: no threat detected.", validReport.getThreatDetails()); // Correct details
        assertEquals(warningClass.CLEAR, validReport.getWarningClass()); // Warning class should be CLEAR
    }

    /*
     * Test for the analyze() method using the EICAR test virus file. Verifies that
     * the EICAR test file is detected as a threat.
     */
    @Test
    public void testAnalyzeWithEICAR() throws IOException {
        // Analyze the EICAR file
        virusTotal.analyze(eicarFile);
        ScanReport eicarReport = virusTotal.getReport();

        // Assert that it is detected as a threat
        assertNotNull(eicarReport);
        assertTrue(eicarReport.isThreatDetected()); // This should detect the EICAR test virus
        assertEquals("Low risk: some suspicious or malicious detections.", eicarReport.getThreatDetails()); // Correct details
        assertEquals(warningClass.SUSPICIOUS, eicarReport.getWarningClass()); // Warning class should be suspicious
    }

    /*
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
        assertNotNull(report); // The report should not be null
        assertEquals(validFile, report.getFile()); // The analyzed file should match the valid file
        assertFalse(report.isThreatDetected()); // No threat should be detected
        assertEquals("The file is clean: no threat detected.", report.getThreatDetails()); // Correct details
        assertEquals(warningClass.CLEAR, report.getWarningClass()); // Warning class should be CLEAR
    }

    /*
     * Test for the correct SHA256 calculation of a file.
     */
    @Test
    public void testCalculateSHA256() {
        // Calculate SHA256 for validFile
        String sha256Hash = virusTotal.calculateSHA256(validFile);
        assertNotNull(sha256Hash); // SHA256 should be calculated
        assertEquals(64, sha256Hash.length()); // SHA256 hash length should be 64 characters
    }

    /*
     * Test for uploading and receiving the scan ID via VirusTotal's API.
     */
    @Test
    public void testUploadAndGetScanId() throws IOException {
        // Upload the file to VirusTotal and get the report
        virusTotal.analyze(validFile);
        ScanReport report = virusTotal.getReport();

        // Check if the scan ID was received
        assertNotNull(report.getScanId());
    }
}
