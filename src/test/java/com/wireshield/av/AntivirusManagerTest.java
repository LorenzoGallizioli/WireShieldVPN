package com.wireshield.av;

import org.junit.Before;
import org.junit.Test;
import com.wireshield.enums.runningStates;
import com.wireshield.enums.warningClass;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.*;

import com.wireshield.localfileutils.FileManager;

/*
 * Test class for the AntivirusManager class.
 * This class contains unit tests for validating the functionality of the AntivirusManager, 
 * including adding files to the scan buffer, performing scans, stopping scans, and merging reports.
 */
public class AntivirusManagerTest {

	private AntivirusManager antivirusManager;
	private ClamAV clamAV;
	private VirusTotal virusTotal;
	runningStates avStatus = runningStates.DOWN; // Stato iniziale DOWN

	/*
	 * Set up the environment for each test. This method runs before each test case
	 * to initialize the AntivirusManager and the scanning tools (ClamAV and
	 * VirusTotal).
	 */
	@Before
	public void setUp() {
		clamAV = new ClamAV(); // Uses the real implementation of ClamAV
		virusTotal = new VirusTotal(); // Uses the real implementation of VirusTotal
		antivirusManager = new AntivirusManager();
		antivirusManager.setClamAV(clamAV);
		antivirusManager.setVirusTotal(virusTotal);
	}

	/**
	 * Utility method to create a file with specific content.
	 * 
	 * @param fileName The name of the file to create.
	 * @return The created File object.
	 * @throws IOException If an I/O error occurs during file creation.
	 */
	private File createFileWithContent(String fileName) throws IOException {
		File file = new File(fileName);
		try (FileOutputStream fos = new FileOutputStream(file)) {
			fos.write("Ciao".getBytes()); // Writes "Ciao" to the file
		}
		return file;
	}

	/**
	 * Test the addFileToScanBuffer method. This test verifies that a file is
	 * correctly added to the scan buffer of the AntivirusManager.
	 * 
	 * @throws IOException If an I/O error occurs during file creation.
	 */
	@Test
	public void testAddFileToScanBuffer() throws IOException {
		// // Create a file with "Ciao" content
		File file = createFileWithContent("testfile.exe");
		antivirusManager.addFileToScanBuffer(file);

		// Verify that the file is in the scan buffer
		assertTrue("The file should be in the buffer", antivirusManager.getScanBuffer().contains(file));
	}

	/**
	 * Test the startPerformScan method. This test checks if the scan is performed
	 * correctly, files are removed from the buffer after scanning, and scan reports
	 * are generated with correct threat classifications.
	 * 
	 * @throws IOException          If an I/O error occurs during file creation.
	 * @throws InterruptedException If the thread is interrupted during the scan.
	 */

	@Test
	public void testStartPerformScan() throws InterruptedException, IOException {
		// Declare the initial antivirus service state
		runningStates avStatus = runningStates.DOWN; // Initial state is DOWN

		// Create sample files
		File file1 = createFileWithContent("file1.exe"); // Malicious file
		File file2 = createFileWithContent("file2.txt"); // Clean file

		// Add the files to the scan buffer
		antivirusManager.addFileToScanBuffer(file1);
		antivirusManager.addFileToScanBuffer(file2);

		// Verify that the files have been added correctly
		assertEquals("The scan buffer should contain 2 files", 2, antivirusManager.getScanBuffer().size());

		// Change state to UP and start the antivirus service
		avStatus = runningStates.UP;

		if (avStatus == runningStates.UP) {
			if (antivirusManager.getScannerStatus() != runningStates.UP) {
				try {
					antivirusManager.startPerformScan();
				} catch (Exception e) {
					fail("Error starting the antivirus service: " + e.getMessage());
				}
			}
		}

		// Wait for the scan buffer to be empty and reports to be generated
		while (!antivirusManager.getScanBuffer().isEmpty() || antivirusManager.getFinalReports().size() < 2) {
			Thread.sleep(1000); // Wait briefly before checking again
		}

		// Verify that the scan buffer is empty after scanning
		assertTrue("The buffer should be empty after the scan", antivirusManager.getScanBuffer().isEmpty());

		// Verify that the final reports have been generated
		List<ScanReport> finalReports = antivirusManager.getFinalReports();
		assertFalse("Final reports should be present", finalReports.isEmpty());

		// Verify that there are exactly 2 reports, one for each file
		assertEquals("There should be exactly 2 reports", 2, finalReports.size());

		// Verify the report for file1.exe
		boolean foundFile1 = false;
		boolean foundFile2 = false;

		for (ScanReport report : finalReports) {
			// Get the file name from the File object
			String fileName = report.getFile().getName(); // Use getName() to get the file name as a string
			System.out.println("Report file: '" + fileName + "'"); // Debug the file name

			if ("file1.exe".equals(fileName)) {
				foundFile1 = true;
				System.out.println("foundFile1 set to true for file1.exe");
				assertFalse("file1.exe should not be detected as a threat", report.isThreatDetected());
				assertEquals("The threat level for file1.exe should be CLEAR", warningClass.CLEAR,
						report.getWarningClass());
			}

			if ("file2.txt".equals(fileName)) {
				foundFile2 = true;
				System.out.println("foundFile2 set to true for file2.txt");
				assertFalse("file2.txt should not be detected as a threat", report.isThreatDetected());
				assertEquals("The threat level for file2.txt should be CLEAR", warningClass.CLEAR,
						report.getWarningClass());
			}
		}

		// Output debug information for found files
		System.out.println("foundFile1: " + foundFile1);
		System.out.println("foundFile2: " + foundFile2);

		// Assert that both files were found in the reports
		assertTrue(foundFile1);
		assertTrue(foundFile2);
	}

	/**
	 * Test to verify that large files are excluded from VirusTotal scanning. This
	 * ensures that files exceeding the MAX_FILE_SIZE are not processed by
	 * VirusTotal.
	 * 
	 * @throws IOException If an I/O error occurs during file creation.
	 */
	@Test
	public void testLargeFileExclusionFromVirusTotal() throws IOException, InterruptedException {
		// Define a large file size exceeding the limit (e.g., 20 MB if MAX_FILE_SIZE is
		// 10 MB)
		final long LARGE_FILE_SIZE = 20 * 1024 * 1024; // 20 MB

		// Generate a large string content to simulate a large file (filling with
		// repetitive characters)
		StringBuilder largeContentBuilder = new StringBuilder((int) LARGE_FILE_SIZE);
		for (int i = 0; i < LARGE_FILE_SIZE; i++) {
			largeContentBuilder.append('A'); // Fill with repetitive characters
		}
		String largeContent = largeContentBuilder.toString();

		// Define the file path as a string
		String largeFilePath = "largeTestFile.txt";

		// Write the large file using FileManager
		FileManager fileManager = new FileManager();
		FileManager.writeFile(largeFilePath, largeContent);

		// Create a File object for the large file
		File largeFile = new File(largeFilePath);

		// Add the large file to the scan buffer
		antivirusManager.addFileToScanBuffer(largeFile);

		// Perform the scan
		antivirusManager.startPerformScan();

		// Wait for the scan to complete
		while (!antivirusManager.getScanBuffer().isEmpty() || antivirusManager.getFinalReports().size() < 1) {
			Thread.sleep(1000); // Wait briefly before checking again
		}

		// Verify that the large file is removed from the scan buffer after processing
		assertFalse("The large file should be removed from the scan buffer after processing",
				antivirusManager.getScanBuffer().contains(largeFile));

		// Verify that no VirusTotal analysis was performed for the large file
		boolean virusTotalNotCalled = true;
		for (ScanReport report : antivirusManager.getFinalReports()) {
			if (report.getFile().equals(largeFile.getName())) {
				assertTrue("Large file should not trigger VirusTotal analysis",
						largeFile.length() > AntivirusManager.MAX_FILE_SIZE);
				if (report.getThreatDetails().contains("VirusTotal")) {
					virusTotalNotCalled = false;
				}
			}
		}

		// Assert that VirusTotal was not called for the large file
		assertTrue("Large file should not be marked as scanned by VirusTotal", virusTotalNotCalled);

		// Clean up the large test file
		File fileToDelete = new File(largeFilePath);
		if (fileToDelete.exists()) {
			fileToDelete.delete();
		}
	}

	/**
	 * Test the stopPerformScan method. This test checks whether the scan can be
	 * stopped correctly, and verifies the scan status is updated.
	 * 
	 * @throws InterruptedException If the thread is interrupted during the scan.
	 * @throws IOException          If an I/O error occurs during file creation.
	 */
	@Test
	public void testStopPerformScan() throws InterruptedException, IOException {
		// Create a file to be added to the scan buffer
		File file1 = createFileWithContent("file1.exe"); // Create a .exe file
		antivirusManager.addFileToScanBuffer(file1);

		// Start the scan in a separate thread
		Thread scanThread = new Thread(() -> {
			antivirusManager.startPerformScan();
		});
		scanThread.start();

		// Wait for the scanner status to change to UP (indicating the scan is running)
		while (antivirusManager.getScannerStatus() != runningStates.UP) {
			Thread.sleep(100); // Wait briefly before checking again
		}

		// Stop the scan
		antivirusManager.stopPerformScan();

		// Verify that the scan status is DOWN (indicating the scan has been stopped)
		assertEquals("The scan status should be DOWN", runningStates.DOWN, antivirusManager.getScannerStatus());
	}

	/*
	 * Test the mergeReports method. This test verifies that reports are correctly
	 * merged, updating threat status and details.
	 */
	@Test
	public void testMergeReports() {
		AntivirusManager manager = new AntivirusManager();

		// Create the target report
		ScanReport target = new ScanReport();
		target.setThreatDetected(false);
		target.setThreatDetails("No threat detected");
		target.setWarningClass(warningClass.CLEAR);
		target.setValid(true);

		// Create the source report
		ScanReport source = new ScanReport();
		source.setThreatDetected(true);
		source.setThreatDetails("Suspicious behavior detected");
		source.setWarningClass(warningClass.SUSPICIOUS);
		source.setValid(false);

		// Perform the merge
		manager.mergeReports(target, source);

		// Verify that the changes are applied correctly
		assertTrue(target.isThreatDetected()); // The target should flag the threat
		assertEquals("Suspicious behavior detected", target.getThreatDetails()); //
		// Threat details should be updated
		assertEquals(warningClass.SUSPICIOUS, target.getWarningClass()); // Warning
		// class should be updated
		assertFalse(target.isValidReport()); // Report validity should be updated
	}
}
