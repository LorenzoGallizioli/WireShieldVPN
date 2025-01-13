package com.wireshield.av;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wireshield.enums.warningClass;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.HttpResponse;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.client.utils.URIBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.util.Queue;
import java.util.LinkedList;

public class VirusTotal implements AVInterface {

	private static final Logger logger = LogManager.getLogger(VirusTotal.class);

	private static VirusTotal instance; // Singleton instance
	private String apiKey; // API Key for accessing the VirusTotal API
	private String virustotalUri; // URI for VirusTotal API
	static final int REQUEST_LIMIT = 4; // Maximum requests allowed per minute
	private static final long ONE_MINUTE_IN_MILLIS = 60L * 1000; // Duration of one minute in milliseconds
	Queue<Long> requestTimestamps = new LinkedList<>(); // Tracks timestamps of API requests
	private ScanReport scanReport; // Stores the scan report for the last analyzed file

	/*
	 * Private constructor that initializes the VirusTotal instance with
	 * configuration data.
	 */
	private VirusTotal() {
		this.apiKey = FileManager.getConfigValue("api_key");
		this.virustotalUri = FileManager.getConfigValue("VIRUSTOTAL_URI");
		if (this.apiKey == null || this.apiKey.trim().isEmpty()) {
			logger.error("Invalid API key. Restart the program and enter a valid key.");
		}
	}

	/**
	 * Retrieves the singleton instance of VirusTotal.
	 *
	 * @return The singleton VirusTotal instance.
	 */
	public static synchronized VirusTotal getInstance() {
		if (instance == null) {
			instance = new VirusTotal();
		}
		return instance;
	}

	/**
	 * Analyzes a file by uploading it to VirusTotal for a threat analysis. If the
	 * request limit is exceeded or the file is invalid, sets an invalid ScanReport.
	 *
	 * @param file The file to analyze.
	 */
	public void analyze(File file) {
		// Check request limits
		if (!canMakeRequest()) {
			logger.warn("VirusTotal analysis canceled. Proceeding with ClamAV check only.");
			scanReport = new ScanReport();
			scanReport.setValid(false);
			scanReport.setThreatDetails("VirusTotal analysis rejected: request limit exceeded.");
			return;
		}

		// Validate the file
		if (file == null || !file.exists()) {
			logger.error("The file does not exist.");
			scanReport = new ScanReport();
			scanReport.setValid(false);
			return;
		}

		String fileHash = FileManager.calculateSHA256(file);
		if (fileHash != null) {
			logger.info("SHA256 calculated: {}", fileHash);
		} else {
			logger.error("Error calculating SHA256.");
		}

		try (CloseableHttpClient client = HttpClients.createDefault()) {
			// Build the URI for the VirusTotal "files" endpoint
			URI uri = new URIBuilder(virustotalUri + "/files").build();

			// Create an HTTP POST request to upload the file
			HttpPost post = new HttpPost(uri);
			post.addHeader("x-apikey", apiKey); // Add the API key as a header for authentication

			// Create a multipart entity to attach the file to the POST request
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			builder.addPart("file", new FileBody(file)); // Add the file as a part of the request
			post.setEntity(builder.build());

			// Execute the POST request and get the response
			HttpResponse response = client.execute(post);
			int statusCode = response.getStatusLine().getStatusCode(); // Extract the HTTP status code

			// Handle the response based on the status code
			if (statusCode == 200) {
				// If the response is successful, parse the response body
				InputStream responseStream = response.getEntity().getContent();
				ObjectMapper objectMapper = new ObjectMapper();
				JsonNode responseJson = objectMapper.readTree(responseStream);

				// Extract the scan ID from the response JSON
				String scanId = responseJson.path("data").path("id").asText();

				logger.info("Analysis completed. Scan ID: {}", scanId);

				// Create a new ScanReport and set its details
				scanReport = new ScanReport(scanId, file);
				scanReport.setSha256(fileHash);

				// Record the timestamp of this request to enforce the API rate limit
				requestTimestamps.add(System.currentTimeMillis());
			} else {
				// If the response indicates an error, log it and create an invalid ScanReport
				logger.error("Error during analysis. HTTP Status Code: {}", statusCode);
				scanReport = new ScanReport();
				scanReport.setValid(false);
				scanReport.setThreatDetails("Error analyzing the file.");
			}
		} catch (Exception e) {
			// Handle exceptions that occur during the file analysis
			logger.error("Exception during file analysis.", e);

			// Create an invalid ScanReport with error details
			scanReport = new ScanReport();
			scanReport.setValid(false);
			scanReport.setThreatDetails("Error analyzing the file.");
		}
	}

	/**
	 * Retrieves the analysis report for the previously uploaded file. Polls the
	 * VirusTotal API until the analysis is complete.
	 *
	 * @return The ScanReport containing the analysis results, or null if an error
	 *         occurred.
	 * @throws InterruptedException if the thread is interrupted while waiting for
	 *                              the report.
	 */
	public ScanReport getReport() throws InterruptedException {
		// Ensure a valid scan report exists
		if (scanReport == null || scanReport.getScanId() == null) {
			logger.warn("No report available. Please perform an analysis first.");
			return null;
		}

		try (CloseableHttpClient client = HttpClients.createDefault()) {
			URI uri = new URIBuilder(virustotalUri + "/analyses/" + scanReport.getScanId()).build();
			boolean isCompleted = false;
			logger.info("Waiting for the report...");
			while (!isCompleted) {
				HttpGet get = new HttpGet(uri);
				get.addHeader("x-apikey", apiKey);

				HttpResponse response = client.execute(get);
				int statusCode = response.getStatusLine().getStatusCode();

				if (statusCode == 200) {
					InputStream responseStream = response.getEntity().getContent();
					ObjectMapper objectMapper = new ObjectMapper();
					JsonNode responseJson = objectMapper.readTree(responseStream);

					JsonNode attributesNode = responseJson.path("data").path("attributes");

					String status = attributesNode.path("status").asText("Status not found");

					if ("completed".equalsIgnoreCase(status)) {
						JsonNode statsNode = attributesNode.path("stats");

						// Parse detection counts
						int maliciousCount = statsNode.path("malicious").asInt();
						int harmlessCount = statsNode.path("harmless").asInt();
						int suspiciousCount = statsNode.path("suspicious").asInt();
						int undetectedCount = statsNode.path("undetected").asInt();

						// Update the ScanReport
						scanReport.setValid(true);
						scanReport.setMaliciousCount(maliciousCount);
						scanReport.setHarmlessCount(harmlessCount);
						scanReport.setSuspiciousCount(suspiciousCount);
						scanReport.setUndetectedCount(undetectedCount);

						// Determine threat level
						if (maliciousCount > 0) {
							double totalScans = (double) maliciousCount + harmlessCount + suspiciousCount
									+ undetectedCount;
							double maliciousPercentage = (maliciousCount / totalScans) * 100;

							scanReport.setThreatDetected(true);

							if (maliciousPercentage > 70 || maliciousCount > 50) {
								scanReport.setWarningClass(warningClass.DANGEROUS);
								scanReport.setThreatDetails("High risk: high percentage of malicious detections.");
							} else {
								scanReport.setWarningClass(warningClass.SUSPICIOUS);
								scanReport.setThreatDetails("Moderate risk: the file might be harmful.");
							}
						} else {
							scanReport.setThreatDetected(false);
							scanReport.setWarningClass(warningClass.CLEAR);
							scanReport.setThreatDetails("The file is clean: no threats detected.");
						}
						isCompleted = true;
					} else {
						Thread.sleep(5000);
					}
				} else {
					logger.error("Error retrieving the report. HTTP Status Code: {}", statusCode);
					return null;
				}
			}
		} catch (InterruptedException e) {
			logger.error("Thread was interrupted while retrieving the report.", e);
			throw e; // Rethrow the InterruptedException
		} catch (Exception e) {
			logger.error("Exception while getting the analysis report.", e);
		}
		logger.info("Report retrieved successfully.");
		return scanReport;
	}

	/**
	 * Checks if a new API request can be made without exceeding the rate limit. If
	 * the limit is reached, informs the user about the remaining wait time.
	 *
	 * @return true if a request can be made, false otherwise.
	 */
	boolean canMakeRequest() {
		long currentTime = System.currentTimeMillis();

		// Remove timestamps older than 1 minute
		while (!requestTimestamps.isEmpty() && currentTime - requestTimestamps.peek() > ONE_MINUTE_IN_MILLIS) {
			requestTimestamps.poll();
		}

		if (requestTimestamps.size() < REQUEST_LIMIT) {
			return true;
		} else {
			long oldestRequestTime = requestTimestamps.peek();
			long timeToWait = ONE_MINUTE_IN_MILLIS - (currentTime - oldestRequestTime);
			logger.warn("Request limit exceeded! VirusTotal will be available in: {} seconds.", (timeToWait / 1000));
			return false;
		}
	}

	/**
	 * Reads the API key from the config.json file.
	 *
	 * @return The API key as a String, or null if the file is empty or missing.
	 */
	String getApiKey() {

		String userApiKey;
		userApiKey = FileManager.getConfigValue("api_key");
		if (userApiKey != null) {
			return userApiKey.trim(); // Remove extra spaces
		}
		return null;
	}

	/*
	 * Ensures that the section api_key in config.json file exists. If the file does
	 * not exist or is empty, prompts the user to enter the API key and saves it to
	 * the file.
	 */
	void ensureApiKeyFileExists() {
		String apiKeyContent = FileManager.getConfigValue("api_key");

		// If the API key exists, skip the process
		if (apiKeyContent != null && !apiKeyContent.trim().isEmpty()) {
			logger.info("API key exists and is valid: skipped.");
			return; // La chiave è già presente, non è necessario fare nulla
		}

		logger.info("API key file is missing or empty. Please enter your API key:");

		try (java.util.Scanner scanner = new java.util.Scanner(System.in)) {
			String userApiKey = null;

			// Keep prompting until a non-empty key is provided
			while (userApiKey == null || userApiKey.trim().isEmpty()) {
				userApiKey = scanner.nextLine().trim();

				if (userApiKey.isEmpty()) {
					logger.error("Invalid input. The API key cannot be empty. Please enter a valid API key.");
					logger.info("Please try again.");
				}
			}

			// Save the API key to the configuration file
			if (FileManager.writeConfigValue("api_key", apiKey)) {
				logger.info("API key saved successfully!");
			} else {
				logger.error("Error saving the API key. Please ensure the configuration file is writable.");
				return;
			}

			// Reload the API key from the configuration file
			this.apiKey = FileManager.getConfigValue("api_key");

			if (this.apiKey == null || this.apiKey.trim().isEmpty()) {
				logger.error("Invalid API key. Please restart the program and provide a valid key.");
			} else {
				logger.info("API key loaded: {}", this.apiKey);
			}

		} catch (Exception e) {
			logger.error("Error during API key input: {}", e.getMessage(), e);
		}
	}
}