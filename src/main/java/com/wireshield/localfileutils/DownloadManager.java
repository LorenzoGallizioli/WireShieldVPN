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

public class DownloadManager {

	private static final Logger logger = LogManager.getLogger(DownloadManager.class);

	private String downloadPath; // Path to the monitored download directory
	private final Set<String> detectedFiles = new HashSet<>(); // Tracks already detected files
	private AntivirusManager antivirusManager; // Manages antivirus scanning
	private runningStates monitorStatus; // Current monitoring status (UP or DOWN)
	private WatchService watchService; // WatchService to monitor file system events
	private Thread monitorThread; // Thread to run the monitoring process

	/**
	 * Constructs a DownloadManager instance.
	 *
	 * @param antivirusManager The AntivirusManager instance for file scanning.
	 */
	public DownloadManager(AntivirusManager antivirusManager) {
		this.downloadPath = getDefaultDownloadPath();
		this.monitorStatus = runningStates.DOWN; // Initially DOWN
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
	 * Starts monitoring the download directory in a separate thread. Detects new
	 * files and adds them to the antivirus scan queue.
	 */
	public void startMonitoring() throws IOException {
		if (monitorStatus == runningStates.UP) {
			logger.warn("Already monitoring the download directory.");
			return;
		}

		monitorStatus = runningStates.UP;
		logger.info("Monitoring directory: {}", downloadPath);

		// Create WatchService to monitor directory
		try {
			watchService = FileSystems.getDefault().newWatchService();
			Path path = Paths.get(downloadPath);

			// Register the directory to listen for new file creation events
			path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

			// Start monitoring in a new thread
			monitorThread = new Thread(() -> {
				// Loop to monitor the directory as long as the status is UP
				while (monitorStatus == runningStates.UP) {
					try {
						WatchKey key = watchService.take(); // Blocking call

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

						key.reset(); // Reset the key to continue watching for further events

					} catch (InterruptedException e) {
						// Handle interruption gracefully, but don't stop the monitoring thread
						if (monitorStatus == runningStates.UP) {
							logger.info("Monitoring interrupted, but continuing...");
							Thread.currentThread().interrupt(); // Preserve interruption flag
						}
					}
				}
			});

			// Start the monitoring thread
			monitorThread.start();

		} catch (IOException e) {
			logger.error("Error creating WatchService: {}", e.getMessage(), e);
		}
	}

	/**
	 * Stops monitoring the download directory.
	 */
	public void stopMonitoring() {
		if (monitorStatus == runningStates.DOWN) {
			logger.warn("Monitoring is already stopped.");
			return;
		}

		monitorStatus = runningStates.DOWN;
		try {
			// Stop the monitor thread and close WatchService
			if (monitorThread != null && monitorThread.isAlive()) {
				monitorThread.interrupt();
				monitorThread.join(); // Wait for the thread to finish
			}

			if (watchService != null) {
				watchService.close();
			}

			logger.info("Stopped monitoring the directory.");

		} catch (IOException | InterruptedException e) {
			logger.error("Error stopping monitoring: {}", e.getMessage(), e);
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
			Thread.sleep(500); // Wait for a short moment to confirm stability
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

	/*	public static void main(String[] args) throws IOException {
        // Create an instance of AntivirusManager
        AntivirusManager antivirusManager = new AntivirusManager();

        // Create an instance of DownloadManager
        DownloadManager downloadManager = new DownloadManager(antivirusManager);

        // Get the default download path using the method of DownloadManager
        String downloadPath = downloadManager.getDefaultDownloadPath();
        System.out.println("Monitored download directory: " + downloadPath);

        // Start monitoring
        downloadManager.startMonitoring();
        System.out.println("Monitoring started. Please download a file into your 'Downloads' folder...");

        // Simulate a process or waiting for the monitor to stop
        try {
            Thread.sleep(100000); // Simulating file download time (100 seconds)
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Now stop monitoring after the 100 seconds
        downloadManager.stopMonitoring();
        System.out.println("Monitoring stopped.");
    } */
}
