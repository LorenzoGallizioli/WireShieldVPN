package com.wireshield.localfileutils;

import com.wireshield.av.AntivirusManager;
import com.wireshield.enums.runningStates;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.HashSet;
import java.util.Set;

/**
 * DownloadManager is responsible for monitoring a designated download directory.
 * It detects newly downloaded files and adds them to the antivirus scan queue.
 */
public class DownloadManager {

    private String downloadPath; // Path to the monitored download directory
    private final Set<String> detectedFiles = new HashSet<>(); // Tracks already detected files
    private AntivirusManager antivirusManager; // Manages antivirus scanning
    private runningStates monitorStatus; // Current monitoring status

    /**
     * Constructs a DownloadManager instance.
     *
     * @param antivirusManager The AntivirusManager instance for file scanning.
     */
    public DownloadManager(AntivirusManager antivirusManager) {
        this.downloadPath = getDefaultDownloadPath();
        this.monitorStatus = runningStates.DOWN;
        this.antivirusManager = antivirusManager;
    }

    /**
     * Determines the default download directory path based on the operating system.
     *
     * @return The default download directory path.
     */
    public String getDefaultDownloadPath() {
        String userHome = System.getProperty("user.home");
        String downloadFolder = "Downloads";

        // Differentiate between Windows and Unix-based operating systems
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            return userHome + "\\" + downloadFolder;
        } else {
            return userHome + "/" + downloadFolder;
        }
    }

    /**
     * Starts monitoring the download directory. Detects new files and adds them to the
     * antivirus scan queue.
     */
    public void startMonitoring() {
        monitorStatus = runningStates.UP;

        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
            Path path = Paths.get(downloadPath);

            // Register the WatchService for file creation events in the directory
            path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

            System.out.println("Monitoring directory: " + downloadPath);

            // Continuous loop to monitor the directory
            while (true) {
                WatchKey key = watchService.take(); // Waits for an event

                for (WatchEvent<?> event : key.pollEvents()) {
                    if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                        Path newFilePath = path.resolve((Path) event.context());
                        File newFile = newFilePath.toFile();

                        // Check if the file is valid (not temporary and stable)
                        if (!isTemporaryFile(newFile) && isFileStable(newFile)) {
                            String fileName = newFile.getAbsolutePath();

                            // Add the file if it hasn't already been processed
                            if (!detectedFiles.contains(fileName)) {
                                detectedFiles.add(fileName);
                                System.out.println("New file detected: " + newFile.getName());

                                // Add the file to the antivirus scan queue
                                antivirusManager.addFileToScanBuffer(newFile);
                            }
                        }
                    }
                }

                // Reset the WatchKey to continue receiving events
                key.reset();
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Error monitoring directory: " + e.getMessage());
        }
    }

    /**
     * Determines if a file is temporary or incomplete.
     *
     * @param file The file to check.
     * @return true if the file is temporary, false otherwise.
     */
    public boolean isTemporaryFile(File file) {
        String fileName = file.getName().toLowerCase();

        // Check for extensions or names indicating temporary files
        return fileName.endsWith(".crdownload") || fileName.endsWith(".part") || fileName.startsWith(".");
    }

    /**
     * Checks if a file is stable, meaning the download is complete.
     *
     * @param file The file to check.
     * @return true if the file is stable, false otherwise.
     */
    public boolean isFileStable(File file) {
        try {
            // Wait briefly to verify file stability
            Thread.sleep(500);
            return file.exists() && file.canRead() && file.length() > 0;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /**
     * Main method for testing the DownloadManager class.
     */
    /*
    public static void main(String[] args) {
        AntivirusManager antivirusManager = AntivirusManager.getInstance();
        DownloadManager downloadManager = new DownloadManager(antivirusManager);
        downloadManager.startMonitoring();
    }
    */
}
