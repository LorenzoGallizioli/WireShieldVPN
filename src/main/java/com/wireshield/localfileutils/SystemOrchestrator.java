package com.wireshield.localfileutils;

import java.io.IOException;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
<<<<<<< HEAD
=======
    private static final Logger logger = LogManager.getLogger(SystemOrchestrator.class);

    private WireguardManager wireguardManager;
    private DownloadManager downloadManager;
    private AntivirusManager antivirusManager;
    private runningStates avStatus;
    private runningStates monitorStatus;
    private connectionStates connectionStatus;
>>>>>>> main

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
        this.antivirusManager = new AntivirusManager();

        // Pass the AntivirusManager instance to the DownloadManager
        this.setDownloadManager(new DownloadManager(antivirusManager));
        logger.info("SystemOrchestrator initialized.");
    }

    /**
     * Method to manage the VPN connection.
     * 
     * @param operation
     *   The operation to be performed.
     */
    public void manageVPN(vpnOperations operation) {
        // UI (scelta peer) -> peerID -> manageVPN(vpnOperations operation, String peerID) -> getPathById(peerID) -> setInterfaceUp(configPath)   
        String wgPath = "C:\\Program Files\\WireGuard\\wireguard.exe";
        String configPath = "C:\\Users\\loren\\Downloads\\peer5_galliz.conf";
        wireguardManager = new WireguardManager(wgPath);
        switch (operation) {
            case START:
                if (wireguardManager.setInterfaceUp(configPath)) {
                    logger.info("Interfaccia avviata con successo.");
                } else {
                    logger.error("Errore nell'avvio dell'interfaccia.");
                }
            break;
        
            case STOP:
                if (wireguardManager.setInterfaceDown()) {
                    logger.info("Interfaccia arrestata con successo.");
                } else {
                    logger.error("Errore nell'arresto dell'interfaccia.");
                }
                break;
            
            default:
                logger.error("Operazione non supportata: " + operation);
            break;
        }
    
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
            if (downloadManager.getMonitorStatus() != runningStates.UP) {
                logger.info("Starting download monitoring service...");
                try {
                    downloadManager.startMonitoring(); // Start monitoring
                } catch (IOException e) {
                    logger.error("Error starting the download monitoring service: {}", e.getMessage(), e);
                }
            } else {
                logger.info("Download monitoring is already running.");
            }
        } else {
            if (downloadManager.getMonitorStatus() != runningStates.DOWN) {
                logger.info("Stopping download monitoring service...");
                downloadManager.stopMonitoring(); // Stop monitoring
            } else {
                logger.info("Download monitoring is already stopped.");
            }
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
            antivirusManager.startPerformScan(); // Start the antivirus scan
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
<<<<<<< HEAD
     * Retrieves report information for the specified report.
     *
     * @param report The identifier of the report to retrieve.
     * @return A string containing the report information.
=======
     * Gets the wireguard manager.
     * 
     * @return WireguardManager
     *   The wireguard manager.
     */
    public WireguardManager getWireguardManager() {
        return wireguardManager;
    }

    /**
     * Gets the report info.
     * 
     * @param report
     *   The report to be retrieved.
     * @return String
     *   The report info.
>>>>>>> main
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
