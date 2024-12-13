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

public class AntivirusManagerTest {

    private static final Logger logger = LogManager.getLogger(AntivirusManagerTest.class);

    private AntivirusManager antivirusManager;
    private File testFile1;
    private File testFile2;

    @Before
    public void setUp() throws IOException {
        // Ottieni l'istanza di AntivirusManager
        antivirusManager = AntivirusManager.getInstance();
        logger.info("Created AntivirusManager instance");

        // Crea alcuni file di esempio per il test in una cartella temporanea
        File tempDir = new File("tempTestFiles");
        if (!tempDir.exists()) {
            tempDir.mkdir();
        }

        testFile1 = new File(tempDir, "testfile1.txt");
        testFile2 = new File(tempDir, "testfile2.txt");

        // Creazione dei file
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

    @After
    public void tearDown() {
        // Elimina i file di test dopo ogni test
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

    @Test
    public void testAddFileToScanBuffer() {
        logger.info("Running testAddFileToScanBuffer...");

        // Aggiungi i file al buffer di scansione
        antivirusManager.addFileToScanBuffer(testFile1);
        antivirusManager.addFileToScanBuffer(testFile2);
        logger.info("Files added to scan buffer");

        // Verifica che i file siano stati aggiunti al buffer
        Map<File, Boolean> scanBuffer = antivirusManager.getScanBuffer();
        assertTrue(scanBuffer.containsKey(testFile1));
        assertTrue(scanBuffer.containsKey(testFile2));

        logger.info("Verified files in scan buffer");
    }

    @Test
    public void testPerformScan() {
        logger.info("Running testPerformScan...");

        // Aggiungi i file al buffer di scansione
        antivirusManager.addFileToScanBuffer(testFile1);
        antivirusManager.addFileToScanBuffer(testFile2);
        logger.info("Files added to scan buffer");

        // Esegui la scansione dei file
        antivirusManager.performScan();
        logger.info("Scan performed");

        // Verifica che i file siano stati scansionati e rimossi dal buffer
        Map<File, Boolean> scanBuffer = antivirusManager.getScanBuffer();
        assertFalse(scanBuffer.containsKey(testFile1));
        assertFalse(scanBuffer.containsKey(testFile2));

        logger.info("Verified files removed from scan buffer after scan");
    }

    @Test
    public void testGetReport() {
        logger.info("Running testGetReport...");

        // Esegui la scansione dei file
        antivirusManager.addFileToScanBuffer(testFile1);
        antivirusManager.performScan();
        logger.info("Files added to buffer and scan performed");

        // Ottieni il report
        ScanReport report = antivirusManager.getReport();

        // Verifica che il report non sia null
        assertNotNull(report);
        logger.info("Scan report retrieved");
    }

    @Test
    public void testGetStatus() {
        logger.info("Running testGetStatus...");

        // Ottieni lo stato dell'antivirus
        runningStates status = antivirusManager.getStatus();

        // Verifica che lo stato sia DOWN (stato iniziale)
        assertEquals(runningStates.DOWN, status);
        logger.info("Verified antivirus status is DOWN");
    }

    @Test
    public void testGetScanBuffer() {
        logger.info("Running testGetScanBuffer...");

        // Aggiungi i file al buffer di scansione
        antivirusManager.addFileToScanBuffer(testFile1);
        antivirusManager.addFileToScanBuffer(testFile2);
        logger.info("Files added to scan buffer");

        // Ottieni il buffer di scansione
        Map<File, Boolean> scanBuffer = antivirusManager.getScanBuffer();

        // Verifica che i file siano presenti nel buffer
        assertTrue(scanBuffer.containsKey(testFile1));
        assertTrue(scanBuffer.containsKey(testFile2));
        logger.info("Verified files in scan buffer");
    }
}