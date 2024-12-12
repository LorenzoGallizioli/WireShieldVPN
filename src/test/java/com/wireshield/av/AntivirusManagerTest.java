package com.wireshield.av;

import com.wireshield.enums.runningStates;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static org.junit.Assert.*;

public class AntivirusManagerTest {

    private AntivirusManager antivirusManager;
    private File testFile1;
    private File testFile2;

    @Before
    public void setUp() throws IOException {
        // Ottieni l'istanza di AntivirusManager
        antivirusManager = AntivirusManager.getInstance();

        // Crea alcuni file di esempio per il test
        testFile1 = new File("testfile1.txt");
        testFile2 = new File("testfile2.txt");

        // Per scopi di testing, creiamo dei file temporanei
        if (testFile1.createNewFile()) {
            System.out.println("Created file: " + testFile1.getName());
        }
        if (testFile2.createNewFile()) {
            System.out.println("Created file: " + testFile2.getName());
        }
    }

    @After
    public void tearDown() {
        // Elimina i file di test dopo ogni test
        if (testFile1.exists()) {
            testFile1.delete();
        }
        if (testFile2.exists()) {
            testFile2.delete();
        }
    }

    @Test
    public void testAddFileToScanBuffer() {
        // Aggiungi i file al buffer di scansione
        antivirusManager.addFileToScanBuffer(testFile1);
        antivirusManager.addFileToScanBuffer(testFile2);

        // Verifica che i file siano stati aggiunti al buffer
        Map<File, Boolean> scanBuffer = antivirusManager.getScanBuffer();
        assertTrue(scanBuffer.containsKey(testFile1));
        assertTrue(scanBuffer.containsKey(testFile2));
    }

    @Test
    public void testPerformScan() {
        // Aggiungi i file al buffer di scansione
        antivirusManager.addFileToScanBuffer(testFile1);
        antivirusManager.addFileToScanBuffer(testFile2);

        // Esegui la scansione dei file
        antivirusManager.performScan();

        // Verifica che i file siano stati scansionati e rimossi dal buffer
        Map<File, Boolean> scanBuffer = antivirusManager.getScanBuffer();
        assertFalse(scanBuffer.containsKey(testFile1));
        assertFalse(scanBuffer.containsKey(testFile2));
    }

    @Test
    public void testGetReport() {
        // Esegui la scansione dei file
        antivirusManager.addFileToScanBuffer(testFile1);
        antivirusManager.performScan();

        // Ottieni il report
        ScanReport report = antivirusManager.getReport();

        // Verifica che il report non sia null
        assertNotNull(report);
    }

    @Test
    public void testGetStatus() {
        // Ottieni lo stato dell'antivirus
        runningStates status = antivirusManager.getStatus();

        // Verifica che lo stato sia DOWN (stato iniziale)
        assertEquals(runningStates.DOWN, status);
    }

    @Test
    public void testGetScanBuffer() {
        // Aggiungi i file al buffer di scansione
        antivirusManager.addFileToScanBuffer(testFile1);
        antivirusManager.addFileToScanBuffer(testFile2);

        // Ottieni il buffer di scansione
        Map<File, Boolean> scanBuffer = antivirusManager.getScanBuffer();

        // Verifica che i file siano presenti nel buffer
        assertTrue(scanBuffer.containsKey(testFile1));
        assertTrue(scanBuffer.containsKey(testFile2));
    }
}