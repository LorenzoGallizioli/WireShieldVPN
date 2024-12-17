package com.wireshield.av;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wireshield.enums.warningClass;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.client.HttpClient;
import org.apache.http.HttpResponse;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.client.utils.URIBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.security.MessageDigest;

/*
 * VirusTotal class interacts with the VirusTotal API to analyze files for potential threats.
 * It provides methods for file hash calculation, uploading files, and retrieving analysis reports.
 */
public class VirusTotal {

    private static final String API_KEY = "895b6aece66d9a168c9822eb4254f2f44993e347c5ea0ddf90708982e857d613"; // Replace with your actual API key
    private ScanReport scanReport;

    /**
     * Calculates the SHA256 hash of a given file.
     * 
     * @param file The file to calculate the SHA256 hash for.
     * @return The calculated SHA256 hash as a hexadecimal string, or null if an error occurs.
     */
    String calculateSHA256(File file) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            try (FileInputStream fis = new FileInputStream(file)) {
                byte[] byteArray = new byte[1024];
                int bytesCount;
                while ((bytesCount = fis.read(byteArray)) != -1) {
                    digest.update(byteArray, 0, bytesCount);
                }
            }
            byte[] bytes = digest.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Analyzes the provided file by uploading it to VirusTotal.
     * This method calculates the SHA256 hash, uploads the file, and stores the result in the scan report.
     *
     * @param file The file to analyze.
     */
    public void analyze(File file) {
        // Check if the file is valid
        if (file == null || !file.exists()) {
            System.out.println("The file does not exist.");
            scanReport = new ScanReport();  // Initialize report as invalid
            scanReport.setValid(false);
            return;
        }

        // Calculate the SHA256 hash of the file
        String fileHash = calculateSHA256(file);
        if (fileHash != null) {
            System.out.println("Calculated SHA256: " + fileHash);
        } else {
            System.out.println("Failed to calculate SHA256.");
        }

        // Upload the file to VirusTotal for analysis
        try {
            HttpClient client = HttpClients.createDefault();
            URI uri = new URIBuilder("https://www.virustotal.com/api/v3/files").build();

            // Log the request URL for debugging
            System.out.println("Sending request to: " + uri.toString());

            HttpPost post = new HttpPost(uri);
            post.addHeader("x-apikey", API_KEY);

            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addPart("file", new FileBody(file));
            post.setEntity(builder.build());

            HttpResponse response = client.execute(post);
            int statusCode = response.getStatusLine().getStatusCode();

            // Log response status code
            System.out.println("HTTP Status Code: " + statusCode);

            if (statusCode == 200) {
                // Process the response and retrieve the scan ID
                InputStream responseStream = response.getEntity().getContent();
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode responseJson = objectMapper.readTree(responseStream);

                // Log the response JSON
                System.out.println("Response JSON: " + responseJson.toString());

                JsonNode dataNode = responseJson.path("data");
                JsonNode attributesNode = dataNode.path("attributes");

                String status = attributesNode.path("status").asText("Status not found");

                // Log the status and scan ID
                System.out.println("Status: " + status);
                String scanId = dataNode.path("id").asText();
                System.out.println("Scan ID: " + scanId);

                // Initialize the scan report with file and scan ID
                scanReport = new ScanReport(scanId, file);
                scanReport.setSha256(fileHash);
            } else {
                // Handle request error
                System.out.println("Request error: " + statusCode);
                scanReport = new ScanReport();  // Initialize as invalid
                scanReport.setValid(false);
                scanReport.setThreatDetails("Error during file analysis.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            scanReport = new ScanReport();  // Initialize as invalid
            scanReport.setValid(false);
            scanReport.setThreatDetails("Error during file analysis.");
        }
    }

    /**
     * Retrieves the analysis report for the previously uploaded file using the scan ID.
     * Polls the VirusTotal API until the analysis status is completed.
     *
     * @return The ScanReport with analysis results, or null if an error occurred.
     */
    public ScanReport getReport() {
        // Ensure that there is a valid scan report
        if (scanReport == null || scanReport.getScanId() == null) {
            System.out.println("No report available. Please perform an analysis first.");
            return null;
        }

        try {
            // Poll the VirusTotal API for the analysis report using the scan ID
            HttpClient client = HttpClients.createDefault();
            URI uri = new URIBuilder("https://www.virustotal.com/api/v3/analyses/" + scanReport.getScanId()).build();

            boolean isCompleted = false;
            while (!isCompleted) {
                // Log request URL for report retrieval
                System.out.println("Requesting report for Scan ID: " + scanReport.getScanId());
                System.out.println("Request URL: " + uri.toString());

                HttpGet get = new HttpGet(uri);
                get.addHeader("x-apikey", API_KEY);

                HttpResponse response = client.execute(get);
                int statusCode = response.getStatusLine().getStatusCode();

                // Log response status code
                System.out.println("HTTP Status Code: " + statusCode);

                if (statusCode == 200) {
                    // Process the report response
                    InputStream responseStream = response.getEntity().getContent();
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode responseJson = objectMapper.readTree(responseStream);

                    // Log the response JSON
                    System.out.println("Response JSON: " + responseJson.toString());

                    JsonNode dataNode = responseJson.path("data");
                    JsonNode attributesNode = dataNode.path("attributes");

                    String status = attributesNode.path("status").asText("Status not found");
                    System.out.println("Status: " + status);

                    if ("completed".equalsIgnoreCase(status)) {
                        // Extract statistics from the completed report
                        JsonNode statsNode = attributesNode.path("stats");

                        int maliciousCount = statsNode.path("malicious").asInt();
                        int harmlessCount = statsNode.path("harmless").asInt();
                        int suspiciousCount = statsNode.path("suspicious").asInt();
                        int undetectedCount = statsNode.path("undetected").asInt();

                        // Log analysis stats
                        System.out.println("Malicious: " + maliciousCount);
                        System.out.println("Harmless: " + harmlessCount);
                        System.out.println("Suspicious: " + suspiciousCount);
                        System.out.println("Undetected: " + undetectedCount);

                        // Update scan report with results
                        scanReport.setValid(true);
                        scanReport.setMaliciousCount(maliciousCount);
                        scanReport.setHarmlessCount(harmlessCount);
                        scanReport.setSuspiciousCount(suspiciousCount);
                        scanReport.setUndetectedCount(undetectedCount);

                        // Classify threat risk
                        if (maliciousCount > 0) {
                            double totalScans = maliciousCount + harmlessCount + suspiciousCount + undetectedCount;
                            double maliciousPercentage = (maliciousCount / totalScans) * 100;
                            double suspiciousPercentage = (suspiciousCount / totalScans) * 100;

                            scanReport.setThreatDetected(true);

                            if (maliciousPercentage > 70 || maliciousCount > 50) {
                                scanReport.setWarningClass(warningClass.DANGEROUS);
                                scanReport.setThreatDetails("High risk: high percentage of malicious detections.");
                            } else if (maliciousPercentage > 30 || suspiciousPercentage > 40 || maliciousCount > 10) {
                                scanReport.setWarningClass(warningClass.DANGEROUS);
                                scanReport.setThreatDetails("Moderate risk: the file might be harmful.");
                            } else {
                                scanReport.setWarningClass(warningClass.SUSPICIOUS);
                                scanReport.setThreatDetails("Low risk: some suspicious or malicious detections.");
                            }
                        } else if (suspiciousCount > 0) {
                            scanReport.setThreatDetected(true);

                            if (suspiciousCount > 5) {
                                scanReport.setWarningClass(warningClass.SUSPICIOUS);
                                scanReport.setThreatDetails("Suspicious file: multiple suspicious detections.");
                            } else {
                                scanReport.setWarningClass(warningClass.SUSPICIOUS);
                                scanReport.setThreatDetails("Suspicious file: low number of suspicious detections.");
                            }
                        } else {
                            scanReport.setThreatDetected(false);
                            scanReport.setWarningClass(warningClass.CLEAR);
                            scanReport.setThreatDetails("The file is clean: no threat detected.");
                        }

                        // Mark the scan as completed
                        isCompleted = true;
                    } else {
                        // Not completed yet, retry after 5 seconds
                        System.out.println("Scan not completed yet. Waiting...");
                        Thread.sleep(5000);
                    }
                } else {
                    // Handle errors during report retrieval
                    System.out.println("Error retrieving the report: " + statusCode);
                    scanReport = new ScanReport(); // Reset report on error
                    scanReport.setValid(false);
                    isCompleted = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            scanReport = new ScanReport();
            scanReport.setValid(false);
            scanReport.setThreatDetails("Error during report retrieval.");
        }

        return scanReport;
    }
}