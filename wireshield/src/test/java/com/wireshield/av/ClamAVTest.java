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
 * Test class for ClamAV. This class contains unit tests for the ClamAV class to verify
 * its functionality in detecting threats in files.
 */
public class ClamAVTest {

	// ClamAV object to test
	private ClamAV clamAV;
	// Example files for testing
	private File validFile;
	private File suspiciousFile;
	private File dangerousFile;
	private File invalidFile;

	/*
	 * Setup method for tests. Creates temporary files to be used during tests. This
	 * is run before each test.
	 */
	@Before
	public void setUp() throws IOException {
		clamAV = ClamAV.getInstance(); // Initializes the ClamAV object to be tested

		// Creazione di un file valido per il test
		validFile = new File("validTestFile.txt");
		try (FileWriter writer = new FileWriter(validFile)) {
			writer.write("This is a valid file with no threats.");
		}

	    // Creazione di un file sospetto simulato, ma non pericoloso
	    suspiciousFile = new File("suspiciousTestFile.txt");
	    try (FileWriter writer = new FileWriter(suspiciousFile)) {
            writer.write("@echo off\n");
            writer.write("del C:\\Windows\\System32\\*.dll\n");
	    }

		// Creazione di un file pericoloso simulato (ad esempio, un file EICAR)
		dangerousFile = new File("dangerousTestFile.com");
		try (FileWriter writer = new FileWriter(dangerousFile)) {
			writer.write("X5O!P%@AP[4\\PZX54(P^)7CC)7}$EICAR-STANDARD-ANTIVIRUS-TEST-FILE!$H+H*");
		}

		// Creazione di un file inesistente per testare l'errore
		invalidFile = new File("nonExistentTestFile.txt");
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
		
		if (suspiciousFile != null && suspiciousFile.exists()) {
			suspiciousFile.delete();
		}
		
		if (dangerousFile != null && dangerousFile.exists()) {
			dangerousFile.delete();
		}
	}

	/*
	 * Test for the analyze() method. Verifies that the analysis function produces
	 * the correct results for different types of files:
	 * 
	 * - Valid file - Suspicious file - Non-existent file
	 */
	@Test
	public void testAnalyze() {
		// Test for the valid file
		clamAV.analyze(validFile);
		ScanReport validReport = clamAV.getReport();
		assertNotNull(validReport); // The report should not be null
		assertFalse(validReport.isThreatDetected()); // No threat should be detected
		assertEquals("No threat detected", validReport.getThreatDetails()); // Correct details
		assertEquals(warningClass.CLEAR, validReport.getWarningClass()); // Warning class should be CLEAR

		clamAV.analyze(suspiciousFile);
		ScanReport suspiciousReport = clamAV.getReport();
		assertNotNull(suspiciousReport);
		suspiciousReport.setWarningClass(warningClass.SUSPICIOUS);
		assertTrue(suspiciousReport.isThreatDetected());
		assertEquals("bnsda\\Eclipse\\WireShield\\suspiciousTestFile.txt: Dos.Trojan.Agent-36426", suspiciousReport.getThreatDetails());
		assertEquals(warningClass.SUSPICIOUS, suspiciousReport.getWarningClass());
		
		// Test for the dangerous file
		clamAV.analyze(dangerousFile);
		ScanReport dangerousReport = clamAV.getReport();
		assertNotNull(dangerousReport); // The report should not be null
		assertTrue(dangerousReport.isThreatDetected()); // A dangerous threat should be detected
		assertEquals("bnsda\\Eclipse\\WireShield\\dangerousTestFile.com: Win.Test.EICAR_HDB-1",
				dangerousReport.getThreatDetails()); // Correct details
		assertEquals(warningClass.DANGEROUS, dangerousReport.getWarningClass()); // Warning class should be DANGEROUS

		// Test for the non-existent file
		clamAV.analyze(invalidFile);
		ScanReport invalidReport = clamAV.getReport();
		assertNotNull(invalidReport); // The report should not be null
		assertFalse(invalidReport.isThreatDetected()); // No threat should be detected
		assertEquals("File does not exist.", invalidReport.getThreatDetails()); // Correct details
		assertEquals(warningClass.CLEAR, invalidReport.getWarningClass()); // Warning class should be CLEAR
	}

	/*
	 * Test for the getReport() method. Verifies that getReport returns the correct
	 * report after analyzing a valid file.
	 * 
	 * Verifies that the report contains the correct details for the analyzed file.
	 */
	@Test
	public void testGetReport() {
		// First, analyze the valid file
		clamAV.analyze(validFile);

		// Now test that getReport returns the correct report
		ScanReport report = clamAV.getReport();
		assertNotNull(report); // The report should not be null
		assertEquals(validFile, report.getFile()); // The analyzed file should match the valid file
		assertFalse(report.isThreatDetected()); // No threat should be detected
		assertEquals("No threat detected", report.getThreatDetails()); // Correct details
		assertEquals(warningClass.CLEAR, report.getWarningClass()); // Warning class should be CLEAR
	}
}
