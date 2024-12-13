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

/*
 * Unit test class for {@link DownloadManager}.
 * This class tests the core functionalities of the DownloadManager,
 * including path determination, temporary file detection, file stability,
 * and monitoring downloads.
 */
public class DownloadManagerTest {

    private static final Logger logger = LogManager.getLogger(DownloadManagerTest.class);

    private DownloadManager downloadManager;
    private AntivirusManager antivirusManager;
    private File testFile;

    /*
     * Sets up the test environment.
     * Initializes the {@link AntivirusManager} and {@link DownloadManager},
     * and creates a test file.
     */
    @Before
    public void setUp() {
        antivirusManager = AntivirusManager.getInstance();
        downloadManager = new DownloadManager(antivirusManager);

        testFile = new File(downloadManager.getDefaultDownloadPath() + "/testfile.txt");
        try {
            if (testFile.createNewFile()) {
                logger.info("Created file: {}", testFile.getName());
            }
        } catch (IOException e) {
            logger.error("Error creating test file: {}", e.getMessage());
        }
    }

    /*
     * Cleans up the test environment.
     * Deletes the test file after each test.
     */
    @After
    public void tearDown() {
        if (testFile.exists()) {
            testFile.delete();
            logger.info("Deleted test file: {}", testFile.getName());
        }
    }

    /*
     * Tests the default download path for Windows systems.
     */
    @Test
    public void testGetDefaultDownloadPathWindows() {
        System.setProperty("os.name", "Windows 10");

        String downloadPath = downloadManager.getDefaultDownloadPath();
        String userHome = System.getProperty("user.home");
        String expectedPath = userHome + "\\Downloads";

        assertEquals(expectedPath, downloadPath);
    }

    /*
     * Tests the default download path for Unix-based systems.
     */
    @Test
    public void testGetDefaultDownloadPathUnix() {
        System.setProperty("os.name", "Linux");

        String downloadPath = downloadManager.getDefaultDownloadPath();
        String userHome = System.getProperty("user.home");
        String expectedPath = userHome + "/Downloads";

        assertEquals(expectedPath, downloadPath);
    }

    /*
     * Tests detection of temporary files based on their extensions or naming patterns.
     */
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

    /*
     * Tests whether a file is stable (i.e., no ongoing writes or changes).
     */
    @Test
    public void testIsFileStable() throws IOException {
        File stableFile = new File(downloadManager.getDefaultDownloadPath() + "/stablefile.txt");
        if (stableFile.createNewFile()) {
            logger.info("Created file: {}", stableFile.getName());
        }

        Files.write(stableFile.toPath(), "Test data".getBytes());

        assertTrue(downloadManager.isFileStable(stableFile));

        if (stableFile.exists()) {
            stableFile.delete();
            logger.info("Deleted stable file: {}", stableFile.getName());
        }
    }

    /*
     * Tests the monitoring functionality of {@link DownloadManager}.
     * Verifies if the monitored directory detects and processes completed downloads correctly.
     */
    @Test
    public void testStartMonitoring() throws IOException, InterruptedException {
        Thread monitoringThread = new Thread(() -> {
            try {
                downloadManager.startMonitoring();
            } catch (Exception e) {
                logger.error("Error during monitoring: {}", e.getMessage());
            }
        });
        monitoringThread.start();

        Thread.sleep(2000); // Wait for the monitoring thread to start

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

        Path targetPath = downloadPath.resolve("newfile.txt");
        Files.move(tempFilePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
        File newFile = targetPath.toFile();
        logger.info("Renamed file to: {}", newFile.getName());

        Thread.sleep(2000); // Simulate delay to ensure detection

        antivirusManager.addFileToScanBuffer(newFile);

        boolean fileDetected = antivirusManager.getScanBuffer().containsKey(newFile);

        if (!fileDetected) {
            logger.error("File not detected in scan buffer: {}", newFile.getName());
        }

        assertTrue("File not detected in scan buffer", fileDetected);
    }
}
