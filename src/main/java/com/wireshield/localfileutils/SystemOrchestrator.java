package com.wireshield.localfileutils;

import java.util.Map;

import com.wireshield.av.AntivirusManager;
import com.wireshield.wireguard.PeerManager;
import com.wireshield.wireguard.WireguardManager;
import com.wireshield.enums.runningStates;
import com.wireshield.enums.connectionStates;
import com.wireshield.enums.vpnOperations;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/*
 * The SystemOrchestrator class orchestrates various components of the system,
 * including antivirus, download monitoring, and VPN connections.
 */
public class SystemOrchestrator {

    private static final Logger logger = LogManager.getLogger(SystemOrchestrator.class);

    private WireguardManager wireguardManager; // Manages VPN connections
    private DownloadManager downloadManager;   // Manages download monitoring
    private AntivirusManager antivirusManager; // Manages antivirus operations
    private runningStates avStatus;           // Status of the antivirus
    private runningStates monitorStatus;      // Status of the download monitoring
    private connectionStates connectionStatus; // Status of the VPN connection

    /*
     * Constructs a SystemOrchestrator instance, initializing its components.
     */
    public SystemOrchestrator() {
        // Create a singleton instance of AntivirusManager
        this.antivirusManager = AntivirusManager.getInstance();

        // Pass the AntivirusManager instance to the DownloadManager
        this.setDownloadManager(new DownloadManager(antivirusManager));
        logger.info("SystemOrchestrator initialized.");
    }

    /**
     * Manages VPN connections by performing the specified operation and updating the status.
     *
     * @param operation The VPN operation to be performed (e.g., connect, disconnect).
     * @param status    The current connection status.
     */
    public void manageVPN(vpnOperations operation, connectionStates status) {
        logger.info("Managing VPN operation: {}, Current status: {}", operation, status);
        // Implementation for managing VPN connections would go here.
        // This could involve interacting with WireguardManager.
    }

    /**
     * Manages the download monitoring service, starting or stopping it based on the status.
     *
     * @param status The desired running state of the download monitoring service.
     */
    public void manageDownload(runningStates status) {
        this.monitorStatus = status; // Update the monitoring status
        logger.info("Managing download monitoring, Desired state: {}", status);

        if (monitorStatus == runningStates.UP) {
            logger.info("Starting download monitoring service...");
            new Thread(getDownloadManager()::startMonitoring).start(); // Start monitoring in a new thread
        } else {
            logger.info("Stopping download monitoring service...");
            // Logic to stop monitoring could be implemented here if needed.
        }
    }

    /**
     * Manages the antivirus service, starting or stopping it based on the status.
     *
     * @param status The desired running state of the antivirus service.
     */
    public void manageAV(runningStates status) {
        this.avStatus = status; // Update the antivirus status
        logger.info("Managing antivirus service, Desired state: {}", status);

        if (status == runningStates.UP) {
            logger.info("Starting antivirus scan...");
            antivirusManager.performScan(); // Start the antivirus scan
        } else {
            logger.info("Stopping antivirus...");
            // Logic to stop the antivirus service if required
        }
    }

    /**
     * Returns the current connection status of the VPN.
     *
     * @return The connection status.
     */
    public connectionStates getConnectionStatus() {
        logger.debug("Retrieving connection status: {}", connectionStatus);
        return connectionStatus;
    }

    /**
     * Returns the current status of the download monitoring service.
     *
     * @return The monitoring status.
     */
    public runningStates getMonitorStatus() {
        logger.debug("Retrieving monitoring status: {}", monitorStatus);
        return monitorStatus;
    }

    /**
     * Returns the current status of the antivirus service.
     *
     * @return The antivirus status.
     */
    public runningStates getAVStatus() {
        logger.debug("Retrieving antivirus status: {}", avStatus);
        return avStatus;
    }

    /**
     * Creates a new peer in the VPN.
     *
     * @param peer The peer to be created.
     */
    public void addPeer(String peerData, String peerName) {
        logger.info("Adding new peer with name: {}", peerName);
        Map<String, Map<String, String>> peer = PeerManager.parsePeerConfig(peerName);
        wireguardManager.getPeerManager().createPeer(peer, peerName);
        logger.info("Peer added successfully: {}", peerName);
    }

    /**
     * Retrieves report information for the specified report.
     *
     * @param report The identifier of the report to retrieve.
     * @return A string containing the report information.
     */
    public String getReportInfo(String report) {
        logger.info("Retrieving report info for report: {}", report);
        // Return report details (dummy implementation for now)
        return "";
    }

    /**
     * Returns the instance of DownloadManager.
     *
     * @return The DownloadManager instance.
     */
    public DownloadManager getDownloadManager() {
        logger.debug("Retrieving DownloadManager instance.");
        return downloadManager;
    }

    /**
     * Sets the instance of DownloadManager.
     *
     * @param downloadManager The DownloadManager instance to be set.
     */
    public void setDownloadManager(DownloadManager downloadManager) {
        logger.debug("Setting DownloadManager instance.");
        this.downloadManager = downloadManager;
    }
}
