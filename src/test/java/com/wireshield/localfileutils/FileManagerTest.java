package com.wireshield.localfileutils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FileManagerTest {

    private String testFilePath;

    @Before
    public void setUp() {
        testFilePath = "testFile.txt";
    }

    @After
    public void tearDown() {
        File file = new File(testFilePath);
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    public void testCreateFile() {
        assertTrue("File should be created successfully.", FileManager.createFile(testFilePath));
        assertTrue("File should exist on the filesystem.", new File(testFilePath).exists());
    }

    @Test
    public void testWriteFile() {
    	FileManager.createFile(testFilePath);
        String content = "Test content";
        assertTrue("Content should be written successfully.", FileManager.writeFile(testFilePath, content));
        assertEquals("Content should match the written data.", content, FileManager.readFile(testFilePath).trim());
    }

    @Test
    public void testReadFile() {
    	FileManager.createFile(testFilePath);
        String content = "Read test content.";
        FileManager.writeFile(testFilePath, content);
        assertEquals("Content read should match the written data.", content, FileManager.readFile(testFilePath).trim());
    }

    @Test
    public void testDeleteFile() {
    	FileManager.createFile(testFilePath);
        assertTrue("File should be deleted successfully.", FileManager.deleteFile(testFilePath));
        assertFalse("File should not exist on the filesystem.", new File(testFilePath).exists());
    }

    @Test
    public void testGetProjectFolder() {
    	FileManager.createFile(testFilePath);
        String projectFolder = FileManager.getProjectFolder() + "\\" + testFilePath;
        assertNotNull("Project folder path should not be null.", projectFolder);
        assertTrue("Project folder path should exist.", new File(projectFolder).exists());
    }

}
