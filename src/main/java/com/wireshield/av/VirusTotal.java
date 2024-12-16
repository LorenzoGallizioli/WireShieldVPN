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

public class VirusTotal {

    private static final String API_KEY = "895b6aece66d9a168c9822eb4254f2f44993e347c5ea0ddf90708982e857d613"; // Replace with your API key
    private ScanReport scanReport;

    // Method to calculate the SHA256 hash of a file
    private String calculateSHA256(File file) {
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

    // Method to upload the file to VirusTotal
    public void analyze(File file) {
        if (file == null || !file.exists()) {
            System.out.println("The file does not exist.");
            scanReport = new ScanReport();  // Ensure the object is created even in case of error
            scanReport.setValid(false);
            return;
        }

        // Calculate SHA256 of the file and store it in ScanReport
        String fileHash = calculateSHA256(file);
        if (fileHash != null) {
            System.out.println("Calculated SHA256: " + fileHash);
        } else {
            System.out.println("Failed to calculate SHA256.");
        }

        // Original logic for file upload
        try {
            HttpClient client = HttpClients.createDefault();
            URI uri = new URIBuilder("https://www.virustotal.com/api/v3/files").build();

            // Add a print to verify the request URL
            System.out.println("Sending request to: " + uri.toString());

            HttpPost post = new HttpPost(uri);
            post.addHeader("x-apikey", API_KEY);

            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addPart("file", new FileBody(file));
            post.setEntity(builder.build());

            HttpResponse response = client.execute(post);
            int statusCode = response.getStatusLine().getStatusCode();

            // Print the response status code
            System.out.println("HTTP Status Code: " + statusCode);

            if (statusCode == 200) {
                InputStream responseStream = response.getEntity().getContent();
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode responseJson = objectMapper.readTree(responseStream);

                // Print the JSON response for debugging
                System.out.println("Response JSON: " + responseJson.toString());

                JsonNode dataNode = responseJson.path("data");
                JsonNode attributesNode = dataNode.path("attributes");

                // Safely extract the status
                String status = attributesNode.path("status").asText("Status not found");

                // Print the status for verification
                System.out.println("Status: " + status);

                String scanId = dataNode.path("id").asText();

                // Print the scan ID for verification
                System.out.println("Scan ID: " + scanId);

                // Set the file and scan ID in the report
                scanReport = new ScanReport(scanId, file);
                // Also store the SHA256 hash
                scanReport.setSha256(fileHash);
            } else {
                System.out.println("Request error: " + statusCode);
                scanReport = new ScanReport();  // Ensure the object is initialized
                scanReport.setValid(false);
                scanReport.setThreatDetails("Error during file analysis.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            scanReport = new ScanReport();  // Ensure the object is initialized
            scanReport.setValid(false);
            scanReport.setThreatDetails("Error during file analysis.");
        }
    }

    // Method to retrieve the analysis report using the ID, only when the status is 'completed'
    public ScanReport getReport() {
        if (scanReport == null || scanReport.getScanId() == null) {
            System.out.println("No report available. Please perform an analysis first.");
            return null;
        }

        try {
            HttpClient client = HttpClients.createDefault();
            URI uri = new URIBuilder("https://www.virustotal.com/api/v3/analyses/" + scanReport.getScanId()).build();

            // Loop continues to request the report until it's completed
            boolean isCompleted = false;
            while (!isCompleted) {
                // Print the request URL for retrieving the report
                System.out.println("Requesting report for Scan ID: " + scanReport.getScanId());
                System.out.println("Request URL: " + uri.toString());

                HttpGet get = new HttpGet(uri);
                get.addHeader("x-apikey", API_KEY);

                HttpResponse response = client.execute(get);
                int statusCode = response.getStatusLine().getStatusCode();

                // Print the response status code
                System.out.println("HTTP Status Code: " + statusCode);

                if (statusCode == 200) {
                    InputStream responseStream = response.getEntity().getContent();
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode responseJson = objectMapper.readTree(responseStream);

                    // Print the JSON response for debugging
                    System.out.println("Response JSON: " + responseJson.toString());

                    // Navigate through JSON nodes to get the status
                    JsonNode dataNode = responseJson.path("data");
                    JsonNode attributesNode = dataNode.path("attributes");

                    // Safely extract the status
                    String status = attributesNode.path("status").asText("Status not found");

                    // Print the status for verification
                    System.out.println("Status: " + status);

                    // Check if the status is "completed"
                    if ("completed".equalsIgnoreCase(status)) {
                        // Extract analysis stats
                        JsonNode statsNode = attributesNode.path("stats");

                        // Extract counters from JSON
                        int maliciousCount = statsNode.path("malicious").asInt();
                        int harmlessCount = statsNode.path("harmless").asInt();
                        int suspiciousCount = statsNode.path("suspicious").asInt();
                        int undetectedCount = statsNode.path("undetected").asInt();

                        // Print analysis counters
                        System.out.println("Malicious: " + maliciousCount);
                        System.out.println("Harmless: " + harmlessCount);
                        System.out.println("Suspicious: " + suspiciousCount);
                        System.out.println("Undetected: " + undetectedCount);

                        // Update the ScanReport object
                        scanReport.setValid(true);
                        scanReport.setMaliciousCount(maliciousCount);
                        scanReport.setHarmlessCount(harmlessCount);
                        scanReport.setSuspiciousCount(suspiciousCount);
                        scanReport.setUndetectedCount(undetectedCount);

                        // Determine risk based on the counters
                        if (maliciousCount > 0) {
                            double totalScans = maliciousCount + harmlessCount + suspiciousCount + undetectedCount;
                            double maliciousPercentage = (maliciousCount / totalScans) * 100;
                            double suspiciousPercentage = (suspiciousCount / totalScans) * 100;

                            scanReport.setThreatDetected(true);

                            if (maliciousPercentage > 70 || maliciousCount > 50) {
                                // High risk
                                scanReport.setWarningClass(warningClass.DANGEROUS);
                                scanReport.setThreatDetails("High risk: high percentage of malicious detections.");
                            } else if (maliciousPercentage > 30 || suspiciousPercentage > 40 || maliciousCount > 10) {
                                // Moderate risk
                                scanReport.setWarningClass(warningClass.DANGEROUS);
                                scanReport.setThreatDetails("Moderate risk: the file might be harmful.");
                            } else {
                                // Low risk
                                scanReport.setWarningClass(warningClass.SUSPICIOUS);
                                scanReport.setThreatDetails("Low risk: some suspicious or malicious detections.");
                            }
                        } else if (suspiciousCount > 0) {
                            scanReport.setThreatDetected(true);

                            if (suspiciousCount > 5) {
                                // Multiple suspicious detections
                                scanReport.setWarningClass(warningClass.SUSPICIOUS);
                                scanReport.setThreatDetails("Suspicious file: multiple suspicious detections.");
                            } else {
                                // Few suspicious detections
                                scanReport.setWarningClass(warningClass.SUSPICIOUS);
                                scanReport.setThreatDetails("Suspicious file: low number of suspicious detections.");
                            }
                        } else {
                            // No threat detected
                            scanReport.setThreatDetected(false);
                            scanReport.setWarningClass(warningClass.CLEAR);
                            scanReport.setThreatDetails("The file is clean: no threat detected.");
                        }

                        // Set the status as completed
                        isCompleted = true;
                    } else {
                        // Not completed yet, wait 5 seconds and try again
                        System.out.println("Scan not completed yet. Waiting...");
                        Thread.sleep(5000); // Wait 5 seconds before retrying
                    }
                } else {
                    // Handle error while retrieving the report
                    System.out.println("Error retrieving the report: " + statusCode);
                    scanReport = new ScanReport();
                    scanReport.setValid(false);
                    isCompleted = true; // Stop the loop in case of error
                }
            }
        } catch (Exception e) {
            // Handle any network or JSON exceptions
            e.printStackTrace();
            scanReport = new ScanReport();
            scanReport.setValid(false);
            scanReport.setThreatDetails("Error during report retrieval.");
        }

        return scanReport;
    }

    // Temporary main method for testing
    public static void main(String[] args) {
        VirusTotal virusTotal = new VirusTotal();

        // Replace with the file path you want to scan
        File fileToScan = new File("C:/Users/bnsda/Downloads/CAP01.pdf");
        virusTotal.analyze(fileToScan);

        // Retrieve and display the report after analysis
        ScanReport report = virusTotal.getReport();
        if (report != null) {
            System.out.println("\n--- Scan Report ---");
            System.out.println("File: " + report.getFile().getAbsolutePath());
            System.out.println("SHA256: " + report.getSha256());
            System.out.println("Valid: " + report.isValidReport());
            System.out.println("Threat Detected: " + report.isThreatDetected());
            System.out.println("Threat Details: " + report.getThreatDetails());
            System.out.println("Warning Class: " + report.getWarningClass());
        } else {
            System.out.println("No report generated.");
        }
    }
}
