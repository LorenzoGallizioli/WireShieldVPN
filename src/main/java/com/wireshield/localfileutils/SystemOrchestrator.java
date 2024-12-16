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

/*
 * The SystemOrchestrator class orchestrates various components of the system, 
 * including antivirus, download monitoring, and VPN connections.
 */
public class SystemOrchestrator {

    private static final Logger logger = LogManager.getLogger(SystemOrchestrator.class);

    private WireguardManager wireguardManager; // Manages VPN connections
    private DownloadManager downloadManager;   // Manages download monitoring
    private AntivirusManager antivirusManager; // Manages antivirus operations
    private ClamAV clamAV;
    private VirusTotal virusTotal;
    
    private runningStates avStatus;            // Status of the antivirus
    private runningStates monitorStatus;       // Status of the download monitoring
    private connectionStates connectionStatus; // Status of the VPN connection

    /*
     * Constructs a SystemOrchestrator instance, initializing its components.
     */
    public SystemOrchestrator() {
        // Create a singleton instance of AntivirusManager
        this.antivirusManager = new AntivirusManager();
     // Configura ClamAV e VirusTotal
        this.clamAV = new ClamAV(); // Inizializza la tua implementazione di ClamAV
        this.virusTotal = new VirusTotal(); // Inizializza la tua implementazione di VirusTotal
        // Pass the AntivirusManager instance to the DownloadManager
        this.setDownloadManager(new DownloadManager(antivirusManager));
        // Imposta gli scanner su AntivirusManager
        antivirusManager.setClamAV(clamAV);
        antivirusManager.setVirusTotal(virusTotal);
        
        logger.info("SystemOrchestrator initialized.");
    }

    /**
     * Manages the VPN connection.
     * 
     * @param operation
     *   The operation to be performed (START or STOP).
     */
    public void manageVPN(vpnOperations operation) {
        // Define the path to the WireGuard executable and the config file
        String wgPath = "C:\\Program Files\\WireGuard\\wireguard.exe";
        String configPath = "C:\\Users\\loren\\Downloads\\peer5_galliz.conf";
        
        // Initialize the WireguardManager with the executable path
        wireguardManager = new WireguardManager(wgPath);
        
        switch (operation) {
            case START:
                if (wireguardManager.setInterfaceUp(configPath)) {
                    logger.info("Interface successfully started.");
                } else {
                    logger.error("Error starting the interface.");
                }
                break;

            case STOP:
                if (wireguardManager.setInterfaceDown()) {
                    logger.info("Interface successfully stopped.");
                } else {
                    logger.error("Error stopping the interface.");
                }
                break;

            default:
                logger.error("Unsupported operation: {}", operation);
                break;
        }
    }

    /**
     * Manages the download monitoring service, starting or stopping it based on the status.
     * 
     * @param status 
     *   The desired running state of the download monitoring service (UP or DOWN).
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
     * @param status 
     *   The desired running state of the antivirus service (UP or DOWN).
     */
    public void manageAV(runningStates status) {

        this.avStatus = status; // Correggere l'assegnazione dello stato dell'antivirus
        logger.info("Managing antivirus service, Desired state: {}", status);

        if (monitorStatus == runningStates.UP) {
            if (antivirusManager.getScannerStatus() != runningStates.UP) {
                logger.info("Starting antivirus service...");
                antivirusManager.startPerformScan(); // Start monitoring
            } else {
                logger.info("Antivirus service is already running.");
            }
        } else {
            if (antivirusManager.getScannerStatus() != runningStates.DOWN) {
                logger.info("Stopping antivirus service...");
                antivirusManager.stopPerformScan(); // Stop monitoring
            } else {
                logger.info("Antivirus service is already stopped.");
            }
        }

        // Print final reports after scanning
        List<ScanReport> finalReports = antivirusManager.getFinalReports(); // Get the list of final reports
        if (finalReports.isEmpty()) {
            logger.info("No reports available.");
        } else {
            finalReports.forEach(report -> {
                logger.info("----------");
                logger.info("File: " + report.getFile().getName());

                // Check if a threat was detected
                if (report.isThreatDetected()) {
                    logger.info("Threat Detected: YES");
                    logger.info("Threat Details: " + report.getThreatDetails());
                    logger.info("Warning Class: " + report.getWarningClass());
                } else {
                    logger.info("Threat Detected: NO");
                }

                // Check the validity of the report
                if (report.isValidReport()) {
                    logger.info("Report is Valid.");
                } else {
                    logger.info("Report is INVALID.");
                }

                logger.info("----------");
            });
        }
    }

    /**
     * Returns the current connection status of the VPN.
     * 
     * @return The current VPN connection status.
     */
    public connectionStates getConnectionStatus() {
        logger.debug("Retrieving connection status: {}", connectionStatus);
        return connectionStatus;
    }

    /**
     * Returns the current status of the download monitoring service.
     * 
     * @return The current download monitoring status.
     */
    public runningStates getMonitorStatus() {
        logger.debug("Retrieving monitoring status: {}", monitorStatus);
        return monitorStatus;
    }

    /**
     * Returns the current status of the antivirus service.
     * 
     * @return The current antivirus status.
     */
    public runningStates getAVStatus() {
        logger.debug("Retrieving antivirus status: {}", avStatus);
        return avStatus;
    }

    /**
     * Creates a new peer in the VPN configuration.
     * 
     * @param peerData
     *   The peer configuration data.
     * @param peerName
     *   The name of the peer to be added.
     */
    public void addPeer(String peerData, String peerName) {
        logger.info("Adding new peer with name: {}", peerName);
        Map<String, Map<String, String>> peer = PeerManager.parsePeerConfig(peerName);
        wireguardManager.getPeerManager().createPeer(peer, peerName);
        logger.info("Peer added successfully: {}", peerName);
    }

    /**
     * Returns the WireguardManager instance.
     * 
     * @return The WireguardManager instance.
     */
    public WireguardManager getWireguardManager() {
        return wireguardManager;
    }

    /**
     * Returns the report information based on the report name.
     * 
     * @param report
     *   The name or identifier of the report to retrieve.
     * @return The report information (dummy implementation for now).
     */
    public String getReportInfo(String report) {
        logger.info("Retrieving report info for report: {}", report);
        // Return dummy report details for now
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
     * @param downloadManager
     *   The DownloadManager instance to be set.
     */
    public void setDownloadManager(DownloadManager downloadManager) {
        logger.debug("Setting DownloadManager instance.");
        this.downloadManager = downloadManager;
    }
}
