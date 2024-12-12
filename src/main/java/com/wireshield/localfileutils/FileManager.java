package com.wireshield.localfileutils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileManager {
	
	/**
     * Creates a new file at the specified path.
     *
     * @param filePath the path where the file will be created
     * @return true if the file is successfully created, false if the file already exists or an error occurs
     */
    public boolean createFile(String filePath) {
        File file = new File(filePath);
        try {
            if (file.createNewFile()) {
                System.out.println("File creato: " + file.getName());
                return true;
            } else {
                System.out.println("Il file esiste già.");
                return false;
            }
        } catch (IOException e) {
            System.out.println("Errore durante la creazione del file: " + e.getMessage());
            return false;
        }
    }

    /**
     * Writes the specified content to a file at the given path.
     *
     * @param filePath the path of the file to write to
     * @param content the content to write to the file
     * @return true if the content is successfully written, false if an error occurs
     */
    public boolean writeFile(String filePath, String content) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(content);
            System.out.println("Scrittura completata.");
            return true;
        } catch (IOException e) {
            System.out.println("Errore durante la scrittura del file: " + e.getMessage());
            return false;
        }
    }

    /**
     * Reads the content of a file from the specified path.
     *
     * @param filePath the path of the file to read
     * @return the content of the file as a String, or null if an error occurs
     */
    public String readFile(String filePath) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            System.out.println("Errore durante la lettura del file: " + e.getMessage());
            return null;
        }
        return content.toString();
    }

    /**
     * Deletes the file at the specified path.
     *
     * @param filePath the path of the file to delete
     * @return true if the file is successfully deleted, false if an error occurs or the file does not exist
     */
    public boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.delete()) {
            System.out.println("File eliminato: " + file.getName());
            return true;
        } else {
            System.out.println("Errore durante l'eliminazione del file.");
            return false;
        }
    }

    /**
     * Retrieves the standard folder where the project is located.
     *
     * @return the absolute path of the project folder
     */
    public String getProjectFolder() {
        return new File("").getAbsolutePath();
    }


}
