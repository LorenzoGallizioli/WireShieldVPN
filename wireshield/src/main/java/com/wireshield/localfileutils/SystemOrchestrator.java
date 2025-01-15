package com.wireshield.localfileutils;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.wireshield.av.AntivirusManager;
import com.wireshield.av.ClamAV;
import com.wireshield.av.ScanReport;
import com.wireshield.av.VirusTotal;
import com.wireshield.wireguard.PeerManager;
import com.wireshield.wireguard.WireguardManager;
import com.wireshield.enums.runningStates;
import com.wireshield.enums.connectionStates;
import com.wireshield.enums.vpnOperations;

/**
 * The SystemOrchestrator class is responsible for orchestrating multiple system
 * components:
 * - Managing VPN connections.
 * - Managing antivirus operations.
 * - Managing download monitoring.
 * 
 * This class implements the Singleton design pattern to ensure only one
 * orchestrator is controlling the system's processes.
 */
public class SystemOrchestrator {

	private static final Logger logger = LogManager.getLogger(SystemOrchestrator.class);
	private static SystemOrchestrator instance; // Singleton instance

	private WireguardManager wireguardManager; // Manages VPN connections
	private DownloadManager downloadManager; // Manages download monitoring
	private AntivirusManager antivirusManager; // Manages antivirus operations
	private ClamAV clamAV; // Integrates ClamAV for file scanning
	private VirusTotal virusTotal; // Integrates VirusTotal for file scanning
	private runningStates avStatus = runningStates.DOWN; // Current antivirus status
	private runningStates monitorStatus; // Current download monitoring status

	/**
	 * Private constructor to initialize the SystemOrchestrator instance. Configures
	 * all necessary components.
	 */
	private SystemOrchestrator() {
		this.wireguardManager = WireguardManager.getInstance(); // Initialize WireguardManager
		this.antivirusManager = AntivirusManager.getInstance(); // Initialize AntivirusManager
		this.clamAV = ClamAV.getInstance(); // Initialize ClamAV
		this.virusTotal = VirusTotal.getInstance(); // Initialize VirusTotal
		this.wireguardManager = WireguardManager.getInstance(); // Link DownloadManager
		this.downloadManager = DownloadManager.getInstance(antivirusManager);

		antivirusManager.setClamAV(clamAV);
		antivirusManager.setVirusTotal(virusTotal);

		logger.info("SystemOrchestrator initialized.");
	}

	/**
	 * Retrieves the Singleton instance of SystemOrchestrator.
	 *
	 * @return The single instance of SystemOrchestrator.
	 */
	public static synchronized SystemOrchestrator getInstance() {
		if (instance == null) {
			instance = new SystemOrchestrator();
		}
		return instance;
	}

	/**
	 * Manages VPN connections by starting or stopping the VPN.
	 * 
	 * @param operation The VPN operation to perform (START or STOP).
	 * @param peer      The peer to connect to (if applicable).
	 */
	public void manageVPN(vpnOperations operation, String peer) {

		switch (operation) {
		case START:
			wireguardManager.setInterfaceUp(peer);
			break;

		case STOP:
			wireguardManager.setInterfaceDown();
			break;

		default:
			logger.error("Unsupported operation: {}", operation);
			break;
		}
	}

	/**
	 * Manages the download monitoring service by starting or stopping it.
	 * 
	 * @param status The desired state of the monitoring service (UP or DOWN).
	 */
	public void manageDownload(runningStates status) {
		this.monitorStatus = status; // Update download monitoring status
		logger.info("Managing download monitoring service. Desired state: {}", status);

		if (monitorStatus == runningStates.UP) {
			if (downloadManager.getMonitorStatus() != runningStates.UP) {
				logger.info("Starting download monitoring service...");
				try {
					downloadManager.startMonitoring(); // Start monitoring downloads
					logger.info("Download monitoring service started successfully.");
				} catch (IOException e) {
					logger.error("Error starting the download monitoring service: {}", e.getMessage(), e);
				}
			} else {
				logger.info("Download monitoring service is already running.");
			}
		} else {
			if (downloadManager.getMonitorStatus() != runningStates.DOWN) {
				logger.info("Stopping download monitoring service...");
				downloadManager.stopMonitoring(); // Stop monitoring downloads
				logger.info("Download monitoring service stopped successfully.");
			} else {
				logger.info("Download monitoring service is already stopped.");
			}
		}
	}

	/**
	 * Manages the antivirus service by starting or stopping it.
	 * 
	 * @param status The desired state of the antivirus service (UP or DOWN).
	 */
	public void manageAV(runningStates status) {
		this.avStatus = status; // Update antivirus status
		logger.info("Managing antivirus service. Desired state: {}", status);

		if (avStatus == runningStates.UP) {
			if (antivirusManager.getScannerStatus() != runningStates.UP) {
				logger.info("Starting antivirus service...");

				// Starting antivirus scan and providing progress
				try {
					antivirusManager.startScan(); // Start antivirus scan
					logger.info("Antivirus service started successfully.");
				} catch (Exception e) {
					logger.error("Error while starting antivirus service: {}", e.getMessage(), e);
				}
			} else {
				logger.info("Antivirus service is already running.");
			}
		} else {
			if (antivirusManager.getScannerStatus() != runningStates.DOWN) {
				logger.info("Stopping antivirus service...");

				// Stopping the scan
				try {
					antivirusManager.stopScan(); // Stop antivirus scan
					logger.info("Antivirus service stopped successfully.");
				} catch (Exception e) {
					logger.error("Error while stopping antivirus service: {}", e.getMessage(), e);
				}
			} else {
				logger.info("Antivirus service is already stopped.");
			}
		}

		// Display final scan reports
		List<ScanReport> finalReports = antivirusManager.getFinalReports();
		if (finalReports.isEmpty()) {
			logger.info("No scan reports available.");
		} else {
			logger.info("Printing final scan reports:");
			for (ScanReport report : finalReports) {
				report.printReport(); // Display each report
			}
		}
	}

	/**
	 * Retrieves the current VPN connection status.
	 *
	 * @return The current VPN connection status.
	 */
	public connectionStates getConnectionStatus() {
		connectionStates connectionStatus = wireguardManager.getConnectionStatus();
		logger.debug("Retrieving connection status: {}", connectionStatus);
		return connectionStatus;
	}

	/**
	 * Retrieves the current download monitoring service status.
	 *
	 * @return The current monitoring status.
	 */
	public runningStates getMonitorStatus() {
		logger.debug("Retrieving monitoring status: {}", monitorStatus);
		return monitorStatus;
	}

	/**
	 * Retrieves the current antivirus service status.
	 *
	 * @return The current antivirus status.
	 */
	public runningStates getAVStatus() {
		logger.debug("Retrieving antivirus status: {}", avStatus);
		return avStatus;
	}

	/**
	 * Adds a new peer to the VPN configuration.
	 * 
	 * @param peerData Configuration data for the peer.
	 * @param peerName The name of the peer.
	 */
	public void addPeer(String peerData, String peerName) {
		logger.info("Adding new peer with name: {}", peerName);
		Map<String, Map<String, String>> peer = PeerManager.parsePeerConfig(peerName);
		wireguardManager.getPeerManager().createPeer(peer, peerName);
		logger.info("Peer added successfully: {}", peerName);
	}

	/**
	 * Retrieves the WireguardManager instance.
	 * 
	 * @return The WireguardManager instance.
	 */
	public WireguardManager getWireguardManager() {
		return wireguardManager;
	}

	/**
	 * Retrieves report information based on the report name.
	 * 
	 * @param report The name or identifier of the report to retrieve.
	 * @return The report information.
	 */
	public String getReportInfo(String report) {
		logger.info("Retrieving report info for report: {}", report);
		return ""; // Dummy implementation for now
	}

	/**
	 * Retrieves the DownloadManager instance.
	 * 
	 * @return The DownloadManager instance.
	 */
	public DownloadManager getDownloadManager() {
		logger.debug("Retrieving DownloadManager instance.");
		return downloadManager;
	}

	/**
	 * Retrieves the AntivirusManager instance.
	 *
	 * @return The AntivirusManager instance.
	 */
	public AntivirusManager getAntivirusManager() {
		return antivirusManager;
	}
}
