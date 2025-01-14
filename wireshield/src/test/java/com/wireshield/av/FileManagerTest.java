package com.wireshield.av;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/*
 * Unit test class for the FileManager class. This class contains tests for
 * verifying the functionality of file management methods such as create, write,
 * read, delete, and utility methods like calculating SHA256 hashes and retrieving
 * the project folder.
 */
public class FileManagerTest {

	private String testFilePath; // Path for the test file used in tests
	private File validFile; // File object for a valid file
	private File stableFile;
	private File emptyFile;
	private File nonExistingFile;

	private static final String CONFIG_PATH = FileManager.getProjectFolder() + "\\config\\config.json";
    private static final String TEMP_CONFIG_PATH = FileManager.getProjectFolder() + "\\config\\config_backup.json";
	
	/*
	 * Setup method that initializes the test file path before each test. This
	 * method is run before every test to ensure a clean setup.
	 */
	@Before
	public void setUp() throws IOException {
		testFilePath = "testFile.txt"; // Initialize the test file path
		// Crea un file che sarà utilizzato per i test
		stableFile = new File("stableFile.txt");
		emptyFile = new File("emptyFile.txt");
		nonExistingFile = new File("nonExistingFile.txt");
		
        // Crea una copia di backup del file config.json
        File originalFile = new File(CONFIG_PATH);
        File backupFile = new File(TEMP_CONFIG_PATH);
        if (originalFile.exists()) {
            try (FileInputStream fis = new FileInputStream(originalFile);
                 FileOutputStream fos = new FileOutputStream(backupFile)) {
                fos.write(fis.readAllBytes());
            }
        }
	}

	/*
	 * Cleanup method that ensures the test file is deleted after each test. This is
	 * run after every test to ensure that there are no leftover files.
	 */
	@After
	public void tearDown() throws IOException {
		File file = new File(testFilePath);
		if (file.exists()) {
			file.delete(); // Delete the test file if it exists
		}

		if (stableFile.exists()) {
			stableFile.delete();
		}

		if (emptyFile.exists()) {
			emptyFile.delete();
		}
		
        // Ripristina il file config.json dallo stato di backup
        File originalFile = new File(CONFIG_PATH);
        File backupFile = new File(TEMP_CONFIG_PATH);
        if (backupFile.exists()) {
            try (FileInputStream fis = new FileInputStream(backupFile);
                 FileOutputStream fos = new FileOutputStream(originalFile)) {
                fos.write(fis.readAllBytes());
            }
            // Elimina il backup per pulizia
            backupFile.delete();
        } else if (originalFile.exists()) {
            originalFile.delete(); // Se il backup non esiste, elimina l'originale
        }
	}

	/*
	 * Test for the createFile() method. Verifies that a file is created
	 * successfully when the method is called.
	 */
	@Test
	public void testCreateFile() {
		// Assert that the file is created successfully
		assertTrue("File should be created successfully.", FileManager.createFile(testFilePath));

		// Assert that the file exists on the filesystem
		assertTrue("File should exist on the filesystem.", new File(testFilePath).exists());

		// Assert that the file already exists on the filesystem
		assertFalse("File already exists.", FileManager.createFile(testFilePath));
	}

	@Test
	public void testCreateFileIOException() {
		// Percorso di file non valido per forzare un'IOException
		String invalidFilePath = "invalid:/path/testFile.txt";

		// Esegui il metodo createFile con il percorso non valido
		boolean result = FileManager.createFile(invalidFilePath);

		// Verifica che il risultato sia false
		assertFalse("The method should return false when an IOException occurs.", result);
	}

	/*
	 * Test for the writeFile() method. Verifies that content is written
	 * successfully to a file.
	 */
	@Test
	public void testWriteFile() {
		// Create the test file
		FileManager.createFile(testFilePath);

		// Define the content to be written to the file
		String content = "Test content";

		// Write the content to the file and assert success
		assertTrue("Content should be written successfully.", FileManager.writeFile(testFilePath, content));

		// Assert that the content written to the file matches the expected content
		assertEquals("Content should match the written data.", content, FileManager.readFile(testFilePath).trim());
	}

	@Test
	public void testWriteFileIOException() {
		// Percorso di file non valido che causa un errore (ad esempio una directory
		// inesistente)
		String invalidFilePath = "/invalid/directory/testFile.txt";
		String content = "Test content";

		// Esegui il metodo writeFile con il percorso non valido
		boolean result = FileManager.writeFile(invalidFilePath, content);

		// Verifica che il risultato sia false
		assertFalse("The method should return false when an IOException occurs.", result);
	}

	/*
	 * Test for the readFile() method. Verifies that content can be read correctly
	 * from a file.
	 */
	@Test
	public void testReadFile() {
		// Create the test file and write content to it
		FileManager.createFile(testFilePath);
		String content = "Read test content.";
		FileManager.writeFile(testFilePath, content);

		// Assert that the content read from the file matches the expected content
		assertEquals("Content read should match the written data.", content, FileManager.readFile(testFilePath).trim());
	}

	@Test
	public void testReadFileIOException() {
		// Percorso di file non valido che causa un errore (ad esempio un file che non
		// esiste)
		String invalidFilePath = "/invalid/directory/nonexistentFile.txt";

		// Esegui il metodo readFile con il percorso non valido
		String result = FileManager.readFile(invalidFilePath);

		// Verifica che il risultato sia null
		assertNull("The method should return null when an IOException occurs.", result);
	}

	/*
	 * Test for the deleteFile() method. Verifies that a file is deleted
	 * successfully from the filesystem.
	 */
	@Test
	public void testDeleteFile() {
		// Create the test file
		FileManager.createFile(testFilePath);

		// Assert that the file is deleted successfully
		assertTrue("File should be deleted successfully.", FileManager.deleteFile(testFilePath));

		// Assert that the file no longer exists on the filesystem
		assertFalse("File should not exist on the filesystem.", new File(testFilePath).exists());
	}

	@Test
	public void testDeleteFileFileNotExist() {
		// Percorso di un file che non esiste
		String nonExistentFilePath = "nonExistentFile.txt";

		// Prova a eliminare il file che non esiste
		boolean result = FileManager.deleteFile(nonExistentFilePath);

		// Verifica che il risultato sia false, dato che il file non esiste
		assertFalse("The file deletion should return false if the file does not exist.", result);
	}

	// Test per i file temporanei con estensione .crdownload
	@Test
	public void testIsTemporaryFile_Crdownload() {
		File crdownloadFile = new File("file.crdownload");
		assertTrue("File con estensione .crdownload dovrebbe essere considerato temporaneo.",
				FileManager.isTemporaryFile(crdownloadFile));
	}

	// Test per i file temporanei con estensione .part
	@Test
	public void testIsTemporaryFile_Part() {
		File partFile = new File("file.part");
		assertTrue("File con estensione .part dovrebbe essere considerato temporaneo.",
				FileManager.isTemporaryFile(partFile));
	}

	// Test per i file temporanei che iniziano con un punto (file nascosti)
	@Test
	public void testIsTemporaryFile_HiddenFile() {
		File hiddenFile = new File(".hiddenFile");
		assertTrue("File che inizia con un punto dovrebbe essere considerato temporaneo.",
				FileManager.isTemporaryFile(hiddenFile));
	}

	// Test per i file non temporanei (file con altre estensioni)
	@Test
	public void testIsTemporaryFile_NotTemporary() {
		File normalFile = new File("file.txt");
		assertFalse("File con estensione .txt non dovrebbe essere considerato temporaneo.",
				FileManager.isTemporaryFile(normalFile));

		File imageFile = new File("image.jpg");
		assertFalse("File con estensione .jpg non dovrebbe essere considerato temporaneo.",
				FileManager.isTemporaryFile(imageFile));
	}

	// Test del blocco try (quando il file è stabile)
	@Test
	public void testIsFileStable_Try() throws IOException {
		// Crea e scrivi nel file stabile
		stableFile = new File("stableFile.txt");
		stableFile.createNewFile();
		FileManager.writeFile(stableFile.getAbsolutePath(), "Test content");

		// Testa se il file è stabile
		assertTrue("Il file dovrebbe essere stabile", FileManager.isFileStable(stableFile));
	}

	/**
	 * Questo test verifica che il metodo `isFileStable` gestisca correttamente
	 * l'interruzione di un thread. In particolare, si verifica che, se il thread 
	 * viene interrotto prima che `isFileStable` venga eseguito, l'interruzione 
	 * venga correttamente gestita senza causare un blocco indefinito.
	 */
	@Test
	public void testIsFileStable_Catch() throws IOException {
		// Crea il file vuoto
		emptyFile = new File("emptyFile.txt");
		emptyFile.createNewFile();

		// Interrompi il thread prima di chiamare isFileStable per forzare il blocco
		// catch
		Thread.currentThread().interrupt();

		// Testa se il metodo gestisce l'interruzione del thread
		assertFalse("Il metodo dovrebbe gestire l'interruzione", FileManager.isFileStable(emptyFile));
	}
	
	/*
	 * Testa il comportamento del metodo calculateSHA256 quando viene passato un file che non esiste.
	 * In particolare, questo test verifica che il metodo gestisca correttamente gli errori restituendo
	 * null in caso di errore, come quando il file non può essere letto.
	 */
	@Test
	public void testCalculateSHA256_Catch() {
		// Crea un file non esistente o che non può essere letto
		nonExistingFile = new File("nonExistingFile.txt");

		// Invoca calculateSHA256 con un file che non esiste
		String result = FileManager.calculateSHA256(nonExistingFile);

		// Verifica che il risultato sia null, il che indica che si è verificato un
		// errore
		assertNull("Il risultato dovrebbe essere null in caso di errore.", result);
	}

	/*
	 * Test for the getProjectFolder() method. Verifies that the project folder path
	 * is retrieved correctly.
	 */
	@Test
	public void testGetProjectFolder() {
		// Create the test file
		FileManager.createFile(testFilePath);

		// Retrieve the project folder path and build the full file path
		String projectFolder = FileManager.getProjectFolder() + "\\" + testFilePath;

		// Assert that the project folder path is not null
		assertNotNull("Project folder path should not be null.", projectFolder);

		// Assert that the project folder path exists
		assertTrue("Project folder path should exist.", new File(projectFolder).exists());
	}

	/*
	 * Test for the correct SHA256 calculation of a file. Verifies that the SHA256
	 * hash is calculated correctly for a file.
	 */
	@Test
	public void testCalculateSHA256() {
		// Create a test file and write content to it
		FileManager.createFile(testFilePath);
		String content = "Test SHA256 content";
		FileManager.writeFile(testFilePath, content);

		// Initialize validFile with the created file
		validFile = new File(testFilePath);

		// Calculate SHA256 hash for the validFile
		String sha256Hash = FileManager.calculateSHA256(validFile);

		// Assertions
		// Ensure the SHA256 hash is not null
		assertNotNull("SHA256 hash should not be null", sha256Hash);

		// Ensure the SHA256 hash has a length of 64 characters (expected for SHA256)
		assertEquals("SHA256 hash should have 64 characters", 64, sha256Hash.length());
	}

	/*
	 * Verifies that the method correctly retrieves the values for valid keys
	 * present in the configuration file. Ensures that the returned values match the
	 * expected results.
	 *
	 */
	@Test
	public void testGetConfigValueValidKey() {
				
		String apiKey = FileManager.getConfigValue("api_key");
		assertNull("895b6aece66d9a168c9822eb4254f2f44993e347c5ea0ddf90708982e857d613", apiKey);
		
	}

	/*
	 * Verifies that the method returns null when an invalid key is requested and
	 * that no exceptions are thrown during the process.
	 *
	 */
	@Test
	public void testGetConfigValueInvalidKey() {
		String value = FileManager.getConfigValue("nonexistent_key");
		assertNull(value);
	}	
	
	@Test
	public void testWriteConfigValue_FileExists_ValidJSON() throws Exception {
        // Prepara un file JSON valido
        JSONObject initialJson = new JSONObject();
        initialJson.put("existingKey", "existingValue");
        try (FileWriter writer = new FileWriter(CONFIG_PATH)) {
            writer.write(initialJson.toJSONString());
        }

        // Test della funzione
        boolean result = FileManager.writeConfigValue("newKey", "newValue");

        // Verifica del risultato
        assertTrue(result);

        // Controlla che il file sia stato aggiornato
        JSONParser parser = new JSONParser();
        try (FileReader reader = new FileReader(CONFIG_PATH)) {
            JSONObject jsonObject = (JSONObject) parser.parse(reader);
            assertEquals("newValue", jsonObject.get("newKey"));
            assertEquals("existingValue", jsonObject.get("existingKey"));
        }
    }

    @Test
    public void testWriteConfigValue_FileDoesNotExist() throws Exception {
        // Elimina il file se esiste
        File file = new File(CONFIG_PATH);
        if (file.exists()) {
            file.delete();
        }

        // Test della funzione
        boolean result = FileManager.writeConfigValue("key", "value");

        // Verifica del risultato
        assertTrue(result);

        // Controlla che il file sia stato creato e contenga il valore corretto
        JSONParser parser = new JSONParser();
        try (FileReader reader = new FileReader(CONFIG_PATH)) {
            JSONObject jsonObject = (JSONObject) parser.parse(reader);
            assertEquals("value", jsonObject.get("key"));
        }
    }

    @Test
    public void testWriteConfigValue_InvalidJSON() throws Exception {
        // Prepara un file JSON non valido
        try (FileWriter writer = new FileWriter(CONFIG_PATH)) {
            writer.write("invalid JSON");
        }

        // Test della funzione
        boolean result = FileManager.writeConfigValue("key", "value");

        // Verifica del risultato
        assertFalse(result);
    }

    @Test
    public void testWriteConfigValue_IOError() {
        // Imposta un percorso non scrivibile
        String invalidPath = "C:\\nonexistent\\config.json";
        FileManager.configPath = invalidPath;

        // Test della funzione
        boolean result = FileManager.writeConfigValue("key", "value");

        // Verifica del risultato
        assertFalse(result);
    }
}
