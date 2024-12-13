package com.wireshield.av;

import com.wireshield.enums.runningStates;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static org.junit.Assert.*;

/*
 * Unit test for the {@link AntivirusManager} class. 
 * This class tests the core functionalities of the AntivirusManager, such as adding files to the scan buffer,
 * performing scans, and retrieving reports and statuses.
 */
public class AntivirusManagerTest {

    private static final Logger logger = LogManager.getLogger(AntivirusManagerTest.class);

    private AntivirusManager antivirusManager;
    private File testFile1;
    private File testFile2;

    /*
     * Sets up the test environment by creating an instance of {@link AntivirusManager}
     * and preparing sample test files.
     *
     * @throws IOException if file creation fails.
     */
    @Before
    public void setUp() throws IOException {
        antivirusManager = AntivirusManager.getInstance();
        logger.info("Created AntivirusManager instance");

        File tempDir = new File("tempTestFiles");
        if (!tempDir.exists()) {
            tempDir.mkdir();
        }

        testFile1 = new File(tempDir, "testfile1.txt");
        testFile2 = new File(tempDir, "testfile2.txt");

        if (testFile1.createNewFile()) {
            logger.info("Created file: " + testFile1.getName());
        } else {
            logger.error("Failed to create file: " + testFile1.getName());
        }

        if (testFile2.createNewFile()) {
            logger.info("Created file: " + testFile2.getName());
        } else {
            logger.error("Failed to create file: " + testFile2.getName());
        }
    }

    /*
     * Cleans up test resources by deleting the sample test files.
     */
    @After
    public void tearDown() {
        if (testFile1.exists() && testFile1.delete()) {
            logger.info("Deleted file: " + testFile1.getName());
        } else {
            logger.error("Failed to delete file: " + testFile1.getName());
        }

        if (testFile2.exists() && testFile2.delete()) {
            logger.info("Deleted file: " + testFile2.getName());
        } else {
            logger.error("Failed to delete file: " + testFile2.getName());
        }
    }

    /*
     * Tests the {@link AntivirusManager#addFileToScanBuffer(File)} method by adding files
     * and verifying their presence in the scan buffer.
     */
    @Test
    public void testAddFileToScanBuffer() {
        logger.info("Running testAddFileToScanBuffer...");

        antivirusManager.addFileToScanBuffer(testFile1);
        antivirusManager.addFileToScanBuffer(testFile2);
        logger.info("Files added to scan buffer");

        Map<File, Boolean> scanBuffer = antivirusManager.getScanBuffer();
        assertTrue(scanBuffer.containsKey(testFile1));
        assertTrue(scanBuffer.containsKey(testFile2));

        logger.info("Verified files in scan buffer");
    }

    /*
     * Tests the {@link AntivirusManager#performScan()} method by ensuring files are scanned
     * and removed from the buffer.
     */
    @Test
    public void testPerformScan() {
        logger.info("Running testPerformScan...");

        antivirusManager.addFileToScanBuffer(testFile1);
        antivirusManager.addFileToScanBuffer(testFile2);
        logger.info("Files added to scan buffer");

        antivirusManager.performScan();
        logger.info("Scan performed");

        Map<File, Boolean> scanBuffer = antivirusManager.getScanBuffer();
        assertFalse(scanBuffer.containsKey(testFile1));
        assertFalse(scanBuffer.containsKey(testFile2));

        logger.info("Verified files removed from scan buffer after scan");
    }

    /*
     * Tests the {@link AntivirusManager#getReport()} method by ensuring a scan report
     * is generated and is not null.
     */
    @Test
    public void testGetReport() {
        logger.info("Running testGetReport...");

        antivirusManager.addFileToScanBuffer(testFile1);
        antivirusManager.performScan();
        logger.info("Files added to buffer and scan performed");

        ScanReport report = antivirusManager.getReport();
        assertNotNull(report);
        logger.info("Scan report retrieved");
    }

    /*
     * Tests the {@link AntivirusManager#getStatus()} method to verify the initial status
     * of the antivirus manager.
     */
    @Test
    public void testGetStatus() {
        logger.info("Running testGetStatus...");

        runningStates status = antivirusManager.getStatus();
        assertEquals(runningStates.DOWN, status);
        logger.info("Verified antivirus status is DOWN");
    }

    /*
     * Tests the {@link AntivirusManager#getScanBuffer()} method by verifying
     * that files are correctly added to the buffer.
     */
    @Test
    public void testGetScanBuffer() {
        logger.info("Running testGetScanBuffer...");

        antivirusManager.addFileToScanBuffer(testFile1);
        antivirusManager.addFileToScanBuffer(testFile2);
        logger.info("Files added to scan buffer");

        Map<File, Boolean> scanBuffer = antivirusManager.getScanBuffer();
        assertTrue(scanBuffer.containsKey(testFile1));
        assertTrue(scanBuffer.containsKey(testFile2));

        logger.info("Verified files in scan buffer");
    }
}
