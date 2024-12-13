package com.wireshield.localfileutils;

import com.wireshield.av.AntivirusManager;
import com.wireshield.enums.runningStates;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.HashSet;
import java.util.Set;

/*
 * DownloadManager is responsible for monitoring a designated download directory.
 * It detects newly downloaded files and adds them to the antivirus scan queue.
 */
public class DownloadManager {

    private static final Logger logger = LogManager.getLogger(DownloadManager.class);

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
        this.setMonitorStatus(runningStates.DOWN);
        this.antivirusManager = antivirusManager;
        logger.info("DownloadManager initialized with path: {}", downloadPath);
    }

    /**
     * Determines the default download directory path based on the operating system.
     *
     * @return The default download directory path.
     */
    public String getDefaultDownloadPath() {
        String userHome = System.getProperty("user.home");
        String downloadFolder = "Downloads";

        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            return userHome + "\\" + downloadFolder;
        } else {
            return userHome + "/" + downloadFolder;
        }
    }

    /*
     * Starts monitoring the download directory. Detects new files and adds them to the
     * antivirus scan queue.
     */
    public void startMonitoring() {
        setMonitorStatus(runningStates.UP);

        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
            Path path = Paths.get(downloadPath);

            path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

            logger.info("Monitoring directory: {}", downloadPath);

            while (true) {
                WatchKey key = watchService.take();

                for (WatchEvent<?> event : key.pollEvents()) {
                    if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                        Path newFilePath = path.resolve((Path) event.context());
                        File newFile = newFilePath.toFile();

                        if (!isTemporaryFile(newFile) && isFileStable(newFile)) {
                            String fileName = newFile.getAbsolutePath();

                            if (!detectedFiles.contains(fileName)) {
                                detectedFiles.add(fileName);
                                logger.info("New file detected: {}", newFile.getName());
                                antivirusManager.addFileToScanBuffer(newFile);
                            }
                        }
                    }
                }

                key.reset();
            }
        } catch (IOException e) {
            logger.error("Error monitoring directory: {}", e.getMessage(), e);
        } catch (InterruptedException e) {
            logger.error("Monitoring interrupted: {}", e.getMessage(), e);
            Thread.currentThread().interrupt();
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
            Thread.sleep(500);
            return file.exists() && file.canRead() && file.length() > 0;
        } catch (InterruptedException e) {
            logger.error("Error checking file stability: {}", e.getMessage(), e);
            Thread.currentThread().interrupt();
            return false;
        }
    }

	public runningStates getMonitorStatus() {
		return monitorStatus;
	}

	public void setMonitorStatus(runningStates monitorStatus) {
		this.monitorStatus = monitorStatus;
		logger.info("Monitor status updated to: {}", monitorStatus);
	}
     
    /* Main method for testing the DownloadManager class.
     * 
    public static void main(String[] args) {
        AntivirusManager antivirusManager = AntivirusManager.getInstance();
        DownloadManager downloadManager = new DownloadManager(antivirusManager);
        downloadManager.startMonitoring();
    }
    */
}
