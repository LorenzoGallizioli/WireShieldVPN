package com.wireshield.localfileutils;

import com.wireshield.av.AntivirusManager;
import com.wireshield.av.FileManager;
import com.wireshield.enums.runningStates;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.HashSet;
import java.util.Set;

/**
 * The DownloadManager class is responsible for:
 * - Monitoring a download directory for new files.
 * - Detecting and processing new downloads.
 * - Adding detected files to the antivirus scanning queue.
 * 
 * This class utilizes the Singleton design pattern to ensure a single instance
 * manages the download directory monitoring process.
 */
public class DownloadManager {

	private static final Logger logger = LogManager.getLogger(DownloadManager.class);

	private static DownloadManager instance; // Singleton instance
	private String downloadPath; // Path to the monitored download directory
	private final Set<String> detectedFiles = new HashSet<>(); // Tracks already detected files
	private AntivirusManager antivirusManager; // Manages file scanning
	private runningStates monitorStatus; // Current monitoring status (UP or DOWN)
	private WatchService watchService; // Monitors file system events
	private Thread monitorThread; // Runs the monitoring process

    /**
     * Private constructor to initialize the DownloadManager instance.
     *
     * @param antivirusManager The AntivirusManager instance for file scanning.
     */
	private DownloadManager(AntivirusManager antivirusManager) {
		this.downloadPath = getDefaultDownloadPath(); // Automatically set default download path
		this.monitorStatus = runningStates.DOWN; // Initially not monitoring
		this.antivirusManager = antivirusManager;
		logger.info("DownloadManager initialized with path: {}", getDownloadPath());
	}

    /**
     * Returns the singleton instance of DownloadManager.
     *
     * @param antivirusManager The AntivirusManager instance (only required for first initialization).
     * @return The single instance of DownloadManager.
     */
	public static synchronized DownloadManager getInstance(AntivirusManager antivirusManager) {
		if (instance == null) {
			instance = new DownloadManager(antivirusManager);
		}
		return instance;
	}

    /**
     * Determines the default download directory path based on the user's operating system.
     *
     * @return The default download directory path as a String.
     */
	public String getDefaultDownloadPath() {
		String userHome = System.getProperty("user.home");
		String downloadFolder = "Downloads";

		if (System.getProperty("os.name").toLowerCase().contains("win")) {
			return userHome + "\\" + downloadFolder; // Windows path format
		} else {
			return userHome + "/" + downloadFolder; // UNIX-like path format
		}
	}

    /**
     * Starts monitoring the download directory for new files.
     * Detected files will be added to the antivirus scanning queue.
     *
     * @throws IOException If an error occurs while setting up the WatchService.
     */
	public void startMonitoring() throws IOException {
		if (monitorStatus == runningStates.UP) {
			logger.warn("Already monitoring the download directory.");
			return; // Already monitoring
		}

		monitorStatus = runningStates.UP; // Set monitoring status to active
		logger.info("Monitoring directory: {}", getDownloadPath());

		// Create WatchService to monitor directory
		try {
			watchService = FileSystems.getDefault().newWatchService();
			Path path = Paths.get(getDownloadPath());

            // Register the directory for creation events
			path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

			// Start monitoring in a new thread
			monitorThread = new Thread(() -> {
				// Loop to monitor the directory as long as the status is UP
				while (monitorStatus == runningStates.UP) {
					try {
						WatchKey key = watchService.take(); // Wait for events

                        // Process events
						for (WatchEvent<?> event : key.pollEvents()) {
							if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
								Path newFilePath = path.resolve((Path) event.context());
								File newFile = newFilePath.toFile();

								if (!FileManager.isTemporaryFile(newFile) && FileManager.isFileStable(newFile)) {
									String fileName = newFile.getAbsolutePath();

									// Check if file is already detected
									if (!detectedFiles.contains(fileName)) {
										detectedFiles.add(fileName);
										logger.info("New file detected: {}", newFile.getName());
										antivirusManager.addFileToScanBuffer(newFile); // Add to antivirus queue
									}
								}
							}
						}

						key.reset(); // Continue watching for further events

					} catch (InterruptedException e) {
						// Handle interruption gracefully, but don't stop the monitoring thread
						if (monitorStatus == runningStates.UP) {
							logger.info("Monitoring interrupted, but continuing...");
							Thread.currentThread().interrupt(); // Preserve interruption flag
						}
					}
				}
			});

			monitorThread.start(); // Begin monitoring

		} catch (IOException e) {
			logger.error("Error creating WatchService: {}", e.getMessage(), e);
		}
	}

    /**
     * Stops monitoring the download directory and shuts down the monitoring thread.
     */
	public void stopMonitoring() {
		if (monitorStatus == runningStates.DOWN) {
			logger.warn("Monitoring is already stopped.");
			return; // Already stopped
		}

		monitorStatus = runningStates.DOWN; // Set status to inactive
		try {
			if (monitorThread != null && monitorThread.isAlive()) {
				monitorThread.interrupt(); // Interrupt monitoring thread
				monitorThread.join(); // Wait for the thread to finish
			}

			if (watchService != null) {
				watchService.close(); // Close WatchService
			}

			logger.info("Stopped monitoring the directory.");

		} catch (IOException e) {
			logger.error("Error stopping monitoring due to IO issue: {}", e.getMessage(), e);
		} catch (InterruptedException e) {
			logger.error("Thread interrupted while stopping monitoring: {}", e.getMessage(), e);
			Thread.currentThread().interrupt(); // Preserve interruption flag
		}
	}

    /**
     * Returns the current monitoring status (UP or DOWN).
     *
     * @return The current monitoring status.
     */
	public runningStates getMonitorStatus() {
		return monitorStatus;
	}

    /**
     * Retrieves the path to the download directory being monitored.
     *
     * @return The monitored download directory path.
     */
	public String getDownloadPath() {
		return downloadPath;
	}
}
