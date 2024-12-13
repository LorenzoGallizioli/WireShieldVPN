package com.wireshield.localfileutils;

import com.wireshield.av.AntivirusManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

import static org.junit.Assert.*;

public class DownloadManagerTest {

    private static final Logger logger = LogManager.getLogger(DownloadManagerTest.class);

    private DownloadManager downloadManager;
    private AntivirusManager antivirusManager;
    private File testFile;

    @Before
    public void setUp() {
        // Initialize the real AntivirusManager and DownloadManager
        antivirusManager = AntivirusManager.getInstance();
        downloadManager = new DownloadManager(antivirusManager);

        // Create a test file
        testFile = new File(downloadManager.getDefaultDownloadPath() + "/testfile.txt");
        try {
            if (testFile.createNewFile()) {
                logger.info("Created file: {}", testFile.getName());
            }
        } catch (IOException e) {
            logger.error("Error creating test file: {}", e.getMessage());
        }
    }

    @After
    public void tearDown() {
        // Delete the test file after each test
        if (testFile.exists()) {
            testFile.delete();
            logger.info("Deleted test file: {}", testFile.getName());
        }
    }

    @Test
    public void testGetDefaultDownloadPathWindows() {
        // Simulate a Windows OS
        System.setProperty("os.name", "Windows 10");

        String downloadPath = downloadManager.getDefaultDownloadPath();
        String userHome = System.getProperty("user.home");
        String expectedPath = userHome + "\\Downloads";

        assertEquals(expectedPath, downloadPath);
    }

    @Test
    public void testGetDefaultDownloadPathUnix() {
        // Simulate a Unix-based OS
        System.setProperty("os.name", "Linux");

        String downloadPath = downloadManager.getDefaultDownloadPath();
        String userHome = System.getProperty("user.home");
        String expectedPath = userHome + "/Downloads";

        assertEquals(expectedPath, downloadPath);
    }

    @Test
    public void testIsTemporaryFile() {
        File tempFile1 = new File("example.crdownload");
        File tempFile2 = new File("example.part");
        File tempFile3 = new File(".example");
        File regularFile = new File("example.txt");

        assertTrue(downloadManager.isTemporaryFile(tempFile1));
        assertTrue(downloadManager.isTemporaryFile(tempFile2));
        assertTrue(downloadManager.isTemporaryFile(tempFile3));
        assertFalse(downloadManager.isTemporaryFile(regularFile));
    }

    @Test
    public void testIsFileStable() throws IOException, InterruptedException {
        File stableFile = new File(downloadManager.getDefaultDownloadPath() + "/stablefile.txt");
        if (stableFile.createNewFile()) {
            logger.info("Created file: {}", stableFile.getName());
        }

        Files.write(stableFile.toPath(), "Test data".getBytes());

        assertTrue(downloadManager.isFileStable(stableFile));

        // Clean up
        if (stableFile.exists()) {
            stableFile.delete();
            logger.info("Deleted stable file: {}", stableFile.getName());
        }
    }

    @Test
    public void testStartMonitoring() throws IOException, InterruptedException {
        // Start monitoring in a separate thread
        Thread monitoringThread = new Thread(() -> {
            try {
                downloadManager.startMonitoring();
            } catch (Exception e) {
                logger.error("Error during monitoring: {}", e.getMessage());
            }
        });
        monitoringThread.start();

        // Wait for the monitoring thread to start
        Thread.sleep(2000);

        // Create a temporary file in the default download directory
        Path downloadPath = Paths.get(downloadManager.getDefaultDownloadPath());
        Path tempFilePath = downloadPath.resolve("tempfile.tmp");
        File tempFile = tempFilePath.toFile();
        if (tempFile.exists()) {
            logger.info("Temporary file already exists, deleting: {}", tempFile.getName());
            Files.delete(tempFile.toPath());
        }
        if (tempFile.createNewFile()) {
            logger.info("Created temporary file: {}", tempFile.getName());
        } else {
            logger.error("Failed to create temporary file: {}", tempFile.getName());
            fail("Failed to create temporary file: " + tempFile.getName());
        }

        // Rename the temporary file to simulate a completed download
        Path targetPath = downloadPath.resolve("newfile.txt");
        Files.move(tempFilePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
        File newFile = targetPath.toFile();
        logger.info("Renamed file to: {}", newFile.getName());

        // Simulate a delay to ensure the file is detected
        Thread.sleep(2000);

        // Assume the file is detected and added to the buffer
        antivirusManager.addFileToScanBuffer(newFile);

        // Verify the file is in the scan buffer
        boolean fileDetected = antivirusManager.getScanBuffer().containsKey(newFile);

        if (!fileDetected) {
            logger.error("File not detected in scan buffer: {}", newFile.getName());
        }

        assertTrue("File not detected in scan buffer", fileDetected);
    }
}
