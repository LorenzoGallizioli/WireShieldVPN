package com.wireshield.av;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wireshield.enums.warningClass;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;

/**
 * Unit test class for the {@link VirusTotal} class. This class contains various
 * test cases to validate the functionality of the VirusTotal class, including
 * methods for analyzing files, retrieving reports, handling API keys, and
 * respecting request limits.
 */
public class VirusTotalTest {

	// VirusTotal object to test
	private VirusTotal virusTotal;
	// Example files for testing
	private File validFile;
	private File dangerousFile;
	private File invalidFile;

	// API Key
	private static final String TEST_API_KEY = "895b6aece66d9a168c9822eb4254f2f44993e347c5ea0ddf90708982e857d613";

	// CONFIG PATH
	private static final String CONFIG_PATH = "config/config.json"; // Path to the config.json

	/**
	 * Setup method for initializing resources before each test. Creates a
	 * VirusTotal instance and temporary files for testing purposes.
	 *
	 * @throws IOException if an error occurs while creating test files.
	 */
	@Before
	public void setUp() throws IOException {

		virusTotal = VirusTotal.getInstance(); // Initializes the VirusTotal object to be tested

		// Create a valid test file
		validFile = new File("validTestFile.txt");
		try (FileWriter writer = new FileWriter(validFile)) {
			writer.write("This is a valid file with no threats.");
		}

		// Create a simulated dangerous test file (e.g., EICAR test file)
		dangerousFile = new File("dangerousTestFile.com");
		try (FileWriter writer = new FileWriter(dangerousFile)) {
			writer.write("X5O!P%@AP[4\\PZX54(P^)7CC)7}$EICAR-STANDARD-ANTIVIRUS-TEST-FILE!$H+H*");
		}

		// Create a non-existent test file to test error handling
		invalidFile = new File("nonExistentTestFile.txt");

	}

	/**
	 * Cleanup method for releasing resources after each test. Deletes the temporary
	 * files created during the tests.
	 *
	 * @throws IOException if an error occurs while deleting files.
	 */
	@After
	public void tearDown() throws IOException {
		// Deletes temporary files after the tests
		if (validFile != null && validFile.exists()) {
			validFile.delete();
		}
		if (dangerousFile != null && dangerousFile.exists()) {
			dangerousFile.delete();
		}

		updateConfigWithValidApiKey(); // Restore the valid API key after each test
	}

	/**
	 * Tests the behavior when the API key is empty in the config.json file.
	 * Validates that the VirusTotal instance handles invalid and valid API keys
	 * correctly.
	 *
	 * @throws Exception if an error occurs during the test.
	 */
	@Test
	public void testInvalidApiKey() throws Exception {

		updateConfigWithEmptyApiKey(); // Set an empty API key in the config file

		// Reset the VirusTotal instance to test with the empty API key
		resetVirusTotalInstance();

		// Restore a valid API key in the config file
		updateConfigWithValidApiKey();

		resetVirusTotalInstance(); // Reset the instance again to test with the valid API key

		// Create a new VirusTotal instance with a valid API key
		VirusTotal validVirusTotal = VirusTotal.getInstance();

		assertNotNull(validVirusTotal); // Ensure that the VirusTotal object is created successfully

	}

	/**
	 * Tests the analyze() and getReport() methods. Verifies that the analysis and
	 * reporting functionalities produce correct results for valid, dangerous, and
	 * non-existent files.
	 *
	 * @throws InterruptedException if the test is interrupted during execution.
	 */
	@Test
	public void testAnalyzeAndGetReport() throws InterruptedException {
		// Test for the valid file
		virusTotal.analyze(validFile);
		ScanReport validReport = virusTotal.getReport();
		assertNotNull(validReport); // The report should not be null
		assertFalse(validReport.isThreatDetected()); // No threat should be detected
		assertEquals("The file is clean: no threats detected.", validReport.getThreatDetails()); // Correct details
		assertEquals(warningClass.CLEAR, validReport.getWarningClass()); // Warning class should be CLEAR

		// Analyze the EICAR file
		virusTotal.analyze(dangerousFile);
		ScanReport dangerousReport = virusTotal.getReport();
		assertNotNull(dangerousReport);
		assertTrue(dangerousReport.isThreatDetected()); // This should detect the EICAR test virus
		assertEquals("High risk: high percentage of malicious detections.", dangerousReport.getThreatDetails()); // Correct
																													// details
		assertEquals(warningClass.DANGEROUS, dangerousReport.getWarningClass()); // Warning class should be DANGEROUS

		// Analyze a non-existent file
		virusTotal.analyze(invalidFile);

		// Retrieve the report
		ScanReport invalidReport = virusTotal.getReport();
		assertNull("The Report of invalidFile is null because file is empty", invalidReport);

	}

	/**
	 * Tests the upload and retrieval of the scan ID via VirusTotal's API. Ensures
	 * that a scan ID is generated and retrieved correctly.
	 *
	 * @throws InterruptedException if the test is interrupted during execution.
	 */
	@Test
	public void testUploadAndGetScanId() throws InterruptedException {
		// Upload the file to VirusTotal and get the report
		virusTotal.analyze(validFile);
		ScanReport report = virusTotal.getReport();
		Thread.sleep(100);
		// Check if the scan ID was received
		assertNotNull(report.getScanId());

		// Adding sleep of 1 minute after this test
		try {
			Thread.sleep(60000); // 1 minute sleep
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt(); // Restore the interrupted status
		}
	}

	/**
	 * Tests the ensureApiKeyFileExists() method. Verifies that it correctly handles
	 * file existence and creates a new API key file if necessary.
	 */
	@Test
	public void testApiKeyFileExists() {
		// Simulate the case where the API key file doesn't exist or is empty
		virusTotal.ensureApiKeyFileExists();

		// Check if the API key file is created
		File apiKeyFile = new File("config/config.json");
		assertTrue(apiKeyFile.exists()); // Verify the file was created

		// Simulate the file containing a valid API key
		String apiKey = virusTotal.getApiKey();
		assertNotNull(apiKey); // The API key should exist
	}

	/**
	 * Tests the getApiKey() method when the API key exists. Ensures the API key is
	 * correctly read from the config.json file.
	 */
	@Test
	public void testGetApiKeyExist() {
		// Simulate reading an API key from the file
		String apiKey = virusTotal.getApiKey();
		assertNotNull(apiKey); // Ensure the API key is not null
		assertEquals(TEST_API_KEY, apiKey); // Check if it matches the expected API key
	}

	/**
	 * Tests the getApiKey() method when the API key does not exist. Ensures the
	 * method handles a missing or null API key correctly.
	 *
	 * @throws IOException if an error occurs while modifying the config file.
	 */
	@Test
	public void testGetApiKeyNotExist() throws IOException {

		updateConfigWithNullApiKey(); // Set a null API key in the config file

		// Simulate reading an API key from the file
		String apiKey = virusTotal.getApiKey();
		assertNull(apiKey); // Ensure the API key is null

		updateConfigWithValidApiKey(); // Restore the valid API key
	}

	/**
	 * Tests the behavior when an empty API key is provided in the config file.
	 * Ensures that the logger and user input mechanism are triggered.
	 *
	 * @throws IOException if an error occurs while modifying the config file.
	 */
	@Test
	public void testApiKeyFileInsertApiKey() throws IOException {

		updateConfigWithEmptyApiKey(); // Set an empty API key in the config file

		System.out.println(
				"\n 1) You must insert an empty API key to trigger the logger message: INVALID INPUT.\n 2) Then, you must enter the correct API key to proceed. \n");

		// Simulate the case where the API key file doesn't exist or is empty
		virusTotal.ensureApiKeyFileExists();

		// Simulate the file containing a valid API key
		String apiKey = virusTotal.getApiKey();
		assertNotNull(apiKey); // The API key should exist
	}

	/**
	 * Tests the canMakeRequest() method. Verifies that it respects the request
	 * limit and returns true or false based on whether the limit has been reached.
	 */
	@Test
	public void testCanMakeRequest() {

		// Simulate filling the queue with valid requests
		long currentTime = System.currentTimeMillis();
		System.out.println("Current time: " + currentTime);

		for (int i = 0; i < VirusTotal.REQUEST_LIMIT; i++) {
			virusTotal.requestTimestamps.add(currentTime - (i * 10)); // Adds requests with minimal intervals
			System.out.println("Added timestamp: " + (currentTime - (i * 10)));
		}

		// Now the limit should have been reached
		boolean canRequest = virusTotal.canMakeRequest();
		System.out.println("Can make request (before adding new): " + canRequest);
		assertFalse(canRequest); // It should return false

		// Simulate removing an old request to free up space
		virusTotal.requestTimestamps.poll();
		System.out.println("Removed one old request. Current timestamps: " + virusTotal.requestTimestamps);

		canRequest = virusTotal.canMakeRequest();
		System.out.println("Can make request (after adding new): " + canRequest);

		assertTrue(canRequest); // Now it should return true

		// Adding sleep of 1 minute after this test
		try {
			Thread.sleep(60000); // 1 minute sleep
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt(); // Restore the interrupted status
		}
	}

	/**
	 * Tests the behavior when the request limit is exceeded during file analysis.
	 * Ensures the system correctly handles the limit and resumes normal operation
	 * once the limit is no longer exceeded.
	 *
	 * @throws InterruptedException if the test is interrupted during execution.
	 */
	@Test
	public void testAnalyzeRequestLimitExceeded() throws InterruptedException {
		// Simulate filling the request queue to reach the limit
		long currentTime = System.currentTimeMillis();
		for (int i = 0; i < VirusTotal.REQUEST_LIMIT; i++) {
			virusTotal.requestTimestamps.add(currentTime - (i * 10)); // Simulate rapid consecutive requests
		}

		// Verify that no more requests can be made
		assertFalse("Expected canMakeRequest to return false after reaching the request limit.",
				virusTotal.canMakeRequest());

		// Attempt to analyze the file
		virusTotal.analyze(validFile);

		// Verify that the scan report reflects the request limit exceeded scenario
		ScanReport validFileReport = virusTotal.getReport();

		assertNull("The Report of validFile is null because request limit exceeded", validFileReport); // Report should
																										// be null due
																										// to request
																										// limit

		// Simulate removing an old request to free up space
		virusTotal.requestTimestamps.poll();

		// Verify that requests can now be made
		assertTrue("Expected canMakeRequest to return true after removing an old request.",
				virusTotal.canMakeRequest());

		// Attempt to analyze the file again, which should now proceed
		virusTotal.analyze(validFile);

		// Verify that the scan report is now valid
		validFileReport = virusTotal.getReport();
		assertNotNull("Scan report should not be null after successful analysis.", validFileReport); // Scan report
																										// should not be
																										// null
		assertTrue("Scan report should be valid after successful analysis.", validFileReport.isValidReport()); // Scan
																												// report
																												// should
																												// be
																												// valid
		assertEquals("SHA256 hash should match for the analyzed file.", FileManager.calculateSHA256(validFile), // Check
																												// hash
																												// matches
				validFileReport.getSha256());
	}

	/**
	 * Updates the config.json file with an empty API key.
	 *
	 * @throws IOException if an error occurs while updating the file.
	 */
	private void updateConfigWithEmptyApiKey() throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode configNode = objectMapper.readTree(new File(CONFIG_PATH));

		// Set the API_KEY to empty
		((com.fasterxml.jackson.databind.node.ObjectNode) configNode).put("api_key", "");

		// Write back to the config.json file
		objectMapper.writeValue(new File(CONFIG_PATH), configNode);
	}

	/**
	 * Updates the config.json file with a null API key.
	 *
	 * @throws IOException if an error occurs while updating the file.
	 */
	private void updateConfigWithNullApiKey() throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode configNode = objectMapper.readTree(new File(CONFIG_PATH));

		// Set the API_KEY to null
		((com.fasterxml.jackson.databind.node.ObjectNode) configNode).putNull("api_key");

		// Write back to the config.json file
		objectMapper.writeValue(new File(CONFIG_PATH), configNode);
	}

	/**
	 * Updates the config.json file with a valid API key.
	 *
	 * @throws IOException if an error occurs while updating the file.
	 */
	private void updateConfigWithValidApiKey() throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode configNode = objectMapper.readTree(new File(CONFIG_PATH));

		// Set the API_KEY to a valid value
		((com.fasterxml.jackson.databind.node.ObjectNode) configNode).put("api_key", TEST_API_KEY);

		// Write back to the config.json file
		objectMapper.writeValue(new File(CONFIG_PATH), configNode);
	}

	/**
	 * Resets the VirusTotal singleton instance for testing purposes.
	 *
	 * @throws NoSuchFieldException   if the instance field is not found.
	 * @throws IllegalAccessException if the instance field is inaccessible.
	 */
	private void resetVirusTotalInstance() throws NoSuchFieldException, IllegalAccessException {
		// Use reflection to access the 'instance' field and set it to null
		Field instanceField = VirusTotal.class.getDeclaredField("instance");
		instanceField.setAccessible(true); // Allow access to a private field
		instanceField.set(null, null); // Set the instance to null
	}
}
