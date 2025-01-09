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
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class VirusTotalTest {

	// VirusTotal object to test
	private VirusTotal virusTotal;
	// Example files for testing
	private File validFile;
	private File dangerousFile;
	private File invalidFile;

	// API Key
	private static final String testApiKey = "895b6aece66d9a168c9822eb4254f2f44993e347c5ea0ddf90708982e857d613";

	// CONFIG PATH
	private static final String CONFIG_PATH = "config/config.json"; // Percorso del file config.json

	/*
	 * Setup method for tests. Creates temporary files to be used during tests. This
	 * is run before each test.
	 */
	@Before
	public void setUp() throws IOException {

		virusTotal = VirusTotal.getInstance(); // Initializes the VirusTotal object to be tested

		// Creazione di un file valido per il test
		validFile = new File("validTestFile.txt");
		try (FileWriter writer = new FileWriter(validFile)) {
			writer.write("This is a valid file with no threats.");
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
	 * Cleanup method after ach test. Deletes the temporary files created for tests.
	 * This is run after each test.
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

		updateConfigWithValidApiKey();
	}

	// Testa il comportamento quando la API_KEY è vuota nel file config.json
	@Test
	public void testInvalidApiKey() throws Exception {

		updateConfigWithEmptyApiKey(); // Imposta la API_KEY vuota nel file

		// Reset l'istanza di VirusTotal per testare la creazione di una nuova istanza
		resetVirusTotalInstance(); // Usa la riflessione per resettare l'istanza

		// Fase 1: Controlliamo che il logger invii un errore quando la API_KEY è vuota
		VirusTotal tempVirusTotal = VirusTotal.getInstance(); // Crea l'istanza, il costruttore verrà chiamato e
																// verificherà
																// la API_KEY vuota

		// Fase 2: Ora reinseriamo una API_KEY valida nel file config.json
		updateConfigWithValidApiKey();

		resetVirusTotalInstance(); // Usa la riflessione per resettare l'istanza

		// Eseguiamo di nuovo il costruttore VirusTotal con la API_KEY valida
		VirusTotal validVirusTotal = VirusTotal.getInstance();

		assertNotNull(validVirusTotal); // Assicuriamoci che l'oggetto VirusTotal sia stato creato correttamente
		
	}

	/*
	 * Test for the analyze() method. Verifies that the analysis function produces
	 * the correct results for different types of files:
	 * 
	 * - Valid file - Suspicious file - Non-existent file
	 */
	@Test
	public void testAnalyzeAndGetReport() throws IOException {
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
		assertEquals(warningClass.DANGEROUS, dangerousReport.getWarningClass()); // Warning class should be suspicious

		// Esegui l'analisi
		virusTotal.analyze(invalidFile);

		// Recupera il report
		ScanReport invalidReport = virusTotal.getReport();
		assertNull("The Report of invalidFile is null because file is empty", invalidReport);

	}

	/*
	 * Test for uploading and receiving the scan ID via VirusTotal's API.
	 */
	@Test
	public void testUploadAndGetScanId() throws IOException, InterruptedException {
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

	/*
	 * Test the ensureApiKeyFileExists() method. Verifies that it correctly handles
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
	
	/*
	 * Test the getApiKey() method. Verifies that the API key is correctly read from
	 * the file.
	 */
	@Test
	public void testGetApiKeyExist() {
		// Simulate reading an API key from the file
		String apiKey = virusTotal.getApiKey();
		assertNotNull(apiKey); // Ensure the API key is not null
		assertEquals(testApiKey, apiKey); // Check if it matches the expected API key	
	}
	
	/*
	 * Test the getApiKey() method. Verifies that the API key is correctly read from
	 * the file.
	 */
	@Test
	public void testGetApiKeyNotExist() throws IOException {
		
		updateConfigWithNullApiKey(); // Imposta la API_KEY vuota nel file

		// Simulate reading an API key from the file
		String apiKey = virusTotal.getApiKey();
		assertNull(apiKey);
		
		updateConfigWithValidApiKey();
	}

	@Test
	public void testApiKeyFileInsertApiKey() throws IOException {

		updateConfigWithEmptyApiKey(); // Imposta la API_KEY vuota nel file

		System.out.println(
			    "\n" +
			    "1) You must insert an empty API key to trigger the logger message: INVALID INPUT.\n" +
			    "2) Then, you must enter the correct API key to proceed."
			    + "\n"
			);
		
		// Simulate the case where the API key file doesn't exist or is empty
		virusTotal.ensureApiKeyFileExists();

		// Simulate the file containing a valid API key
		String apiKey = virusTotal.getApiKey();
		assertNotNull(apiKey); // The API key should exist
	}

	/*
	 * Test the canMakeRequest() method. Verifies that it respects the request limit
	 * and returns true when the limit has not been reached.
	 */
	@Test
	public void testCanMakeRequest() {

		// Simulate filling the queue with valid requests
		long currentTime = System.currentTimeMillis();
		System.out.println("Current time: " + currentTime); // Debug

		for (int i = 0; i < VirusTotal.REQUEST_LIMIT; i++) {
			virusTotal.requestTimestamps.add(currentTime - (i * 10)); // Adds requests with minimal intervals
			System.out.println("Added timestamp: " + (currentTime - (i * 10))); // Debug
		}

		// Now the limit should have been reached
		boolean canRequest = virusTotal.canMakeRequest(); 
		System.out.println("Can make request (before adding new): " + canRequest); // Debug

		// It should return false, so the assertFalse assertion should pass
		assertFalse(canRequest);

		// Simulate the passing of time and adding a new request
		virusTotal.requestTimestamps.poll(); // Removes an old request
		System.out.println("Removed one old request. Current timestamps: " + virusTotal.requestTimestamps); // Debug

		canRequest = virusTotal.canMakeRequest();
		System.out.println("Can make request (after adding new): " + canRequest); // Debug

		// Now it should be allowed to make a request, so the assertTrue assertion
		// should pass
		assertTrue(canRequest);

		// Adding sleep of 1 minute after this test
		try {
			Thread.sleep(60000); // 1 minute sleep
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt(); // Restore the interrupted status
		}
	}

	@Test
	public void testAnalyzeRequestLimitExceeded() {
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

		assertNull("The Report of validFile is null because request limit exceeded", validFileReport);

		// Simulate removing an old request to free up space
		virusTotal.requestTimestamps.poll();

		// Verify that requests can now be made
		assertTrue("Expected canMakeRequest to return true after removing an old request.",
				virusTotal.canMakeRequest());

		// Attempt to analyze the file again, which should now proceed
		virusTotal.analyze(validFile);

		// Verify that the scan report is now valid
		validFileReport = virusTotal.getReport(); // Get the updated report
		assertNotNull("Scan report should not be null after successful analysis.", validFileReport);
		assertTrue("Scan report should be valid after successful analysis.", validFileReport.isValidReport());
		assertEquals("SHA256 hash should match for the analyzed file.", FileManager.calculateSHA256(validFile),
				validFileReport.getSha256());
	}

	// Metodo per aggiornare il file config.json con una API_KEY vuota
	private void updateConfigWithEmptyApiKey() throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode configNode = objectMapper.readTree(new File(CONFIG_PATH));

		// Imposta la API_KEY vuota
		((com.fasterxml.jackson.databind.node.ObjectNode) configNode).put("api_key", "");

		// Scrivi nuovamente nel file config.json
		objectMapper.writeValue(new File(CONFIG_PATH), configNode);
	}
	
	// Metodo per aggiornare il file config.json con una API_KEY vuota
	private void updateConfigWithNullApiKey() throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode configNode = objectMapper.readTree(new File(CONFIG_PATH));

		// Imposta la API_KEY vuota
		((com.fasterxml.jackson.databind.node.ObjectNode) configNode).putNull("api_key");

		// Scrivi nuovamente nel file config.json
		objectMapper.writeValue(new File(CONFIG_PATH), configNode);
	}

	// Metodo per aggiornare il file config.json con una API_KEY valida
	private void updateConfigWithValidApiKey() throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode configNode = objectMapper.readTree(new File(CONFIG_PATH));

		// Imposta la API_KEY valida
		((com.fasterxml.jackson.databind.node.ObjectNode) configNode).put("api_key", testApiKey);

		// Scrivi nuovamente nel file config.json
		objectMapper.writeValue(new File(CONFIG_PATH), configNode);
	}

	private void resetVirusTotalInstance() throws NoSuchFieldException, IllegalAccessException {
		// Usa la riflessione per accedere al campo 'instance' e metterlo a null
		Field instanceField = VirusTotal.class.getDeclaredField("instance");
		instanceField.setAccessible(true); // Permetti l'accesso a un campo privato
		instanceField.set(null, null); // Imposta l'istanza a null
	}
}
