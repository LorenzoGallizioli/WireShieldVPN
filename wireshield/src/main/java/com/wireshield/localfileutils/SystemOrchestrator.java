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
 * Orchestrates various system components, including antivirus, download monitoring, and VPN connections.
 */
public class SystemOrchestrator {

    private static final Logger logger = LogManager.getLogger(SystemOrchestrator.class);
    private static SystemOrchestrator instance;

    private WireguardManager wireguardManager; // Manages VPN connections
    private DownloadManager downloadManager;   // Manages download monitoring
    private AntivirusManager antivirusManager; // Manages antivirus operations
    private ClamAV clamAV;                     // Analyzes files with ClamAV
    private VirusTotal virusTotal;             // Analyzes files with VirusTotal
    private runningStates avStatus = runningStates.DOWN; // Antivirus service status
    private runningStates monitorStatus;       // Download monitoring service status
    private connectionStates connectionStatus; // VPN connection status
    
    // Control variable for componentStatesGuardian thread --> runningStates.UP let the thread to continue running, runningStates.DOWN stops the thread execution
    private runningStates guardianState = runningStates.DOWN; 

    /*
     * Initializes the SystemOrchestrator instance with necessary components.
     */
    private SystemOrchestrator() {
        this.wireguardManager = WireguardManager.getInstance(); // Initialize WireguardManager
        this.antivirusManager = AntivirusManager.getInstance(); // Initialize AntivirusManager
        this.downloadManager = DownloadManager.getInstance(antivirusManager);
        this.clamAV = ClamAV.getInstance(); // Initialize ClamAV
        this.virusTotal = VirusTotal.getInstance(); // Initialize VirusTotal
        antivirusManager.setClamAV(clamAV);
        antivirusManager.setVirusTotal(virusTotal);

        logger.info("SystemOrchestrator initialized.");
    }
    
    /**
     * Static method to get the Singleton instance of SystemOrchestrator.
     *
     * @return the single instance of SystemOrchestrator.
     */
    public static synchronized SystemOrchestrator getInstance() {
        if (instance == null) {
            instance = new SystemOrchestrator();
        }
        return instance;
    }
    
    /**
     * Sets the necessary managers for the test class functionality.
     *
     * @param wireguardManager the WireGuard manager to be configured
     * @param antivirusManager the Antivirus manager to be configured
     * @param downloadManager the Download manager to be configured
     */
    protected void setObjects(WireguardManager wireguardManager, AntivirusManager antivirusManager, DownloadManager downloadManager) {
    	this.wireguardManager = wireguardManager;
    	this.antivirusManager = antivirusManager;
    	this.downloadManager = downloadManager;
    }

    /**
     * Manages the VPN connection.
     * 
     * @param operation The operation to be performed (START or STOP).
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
     * Manages the download monitoring service.
     * 
     * @param status The desired state of the download monitoring service (UP or DOWN).
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
                    this.monitorStatus = runningStates.DOWN;
                }
            } else {
                logger.info("Download monitoring service is already running.");
            }
        } else {
            if (downloadManager.getMonitorStatus() != runningStates.DOWN) {
                logger.info("Stopping download monitoring service...");
                downloadManager.forceStopMonitoring(); // Stop monitoring downloads
				logger.info("Download monitoring service stopped successfully.");
            } else {
                logger.info("Download monitoring service is already stopped.");
            }
        }
    }

    /**
     * Manages the antivirus service.
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
                    antivirusManager.startPerformScan(); 
                    logger.info("Antivirus service started successfully.");
                } catch (Exception e) {
                    logger.error("Error while starting antivirus service: {}", e.getMessage(), e);
                    this.avStatus = antivirusManager.getScannerStatus();
                }
            } else {
                logger.info("Antivirus service is already running.");
            }
        } else {
            if (antivirusManager.getScannerStatus() != runningStates.DOWN) {
                logger.info("Stopping antivirus service...");
                
                // Stopping the scan
                try {
                    antivirusManager.forceStopPerformScan(); 
                    logger.info("Antivirus service stopped successfully.");
                } catch (Exception e) {
                    logger.error("Error while stopping antivirus service: {}", e.getMessage(), e);
                    this.avStatus = antivirusManager.getScannerStatus();
                }
            } else {
                logger.info("Antivirus service is already stopped.");
            }
        }

        // Print final scan reports using printReport() method
        List<ScanReport> finalReports = antivirusManager.getFinalReports();
        if (finalReports.isEmpty()) {
            logger.info("No scan reports available.");
        } else {
            logger.info("Printing final scan reports:");
            for (ScanReport report : finalReports) {
                report.printReport();  // Use printReport method to print the report
            }
        }
    }

    /**
     * Retrieves the current VPN connection status.
     * 
     * @return The current VPN connection status.
     */
    public connectionStates getConnectionStatus() {
        this.connectionStatus = wireguardManager.getConnectionStatus();
        logger.debug("Retrieving connection status: {}", connectionStatus);
        return connectionStatus;
    }

    /**
     * Retrieves the current download monitoring status.
     * 
     * @return The current download monitoring status.
     */
    public runningStates getMonitorStatus() {
        logger.debug("Retrieving monitoring status: {}", monitorStatus);
        return monitorStatus;
    }

    /**
     * Retrieves the current antivirus status.
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
     * @param peerData The peer configuration data.
     * @param peerName The name of the peer to be added.
     */
    public void addPeer(String peerData, String peerName) {
        logger.info("Adding new peer with name: {}", peerName);
        Map<String, Map<String, String>> Data = PeerManager.parsePeerConfig(peerName);
        wireguardManager.getPeerManager().createPeer(Data, peerName);
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
    
    
    /**
     * Monitors the states of essential system components and handles failures.
     * <p>
     * This method starts a thread to periodically check the operational states of components. 
     * If a critical component fails, the system logs the error, stops related services, 
     * and shuts down the VPN for the selected peer.
     * </p>
     *
     * <h2>Functionality:</h2>
     * <ul>
     *   <li>Checks if the interface is up and the connection is active.</li>
     *   <li>Handles failures by stopping downloads, AV services, or the VPN as needed.</li>
     *   <li>Runs every 200ms until the guardian state changes from {@code runningStates.UP}.</li>
     * </ul>
     *
     * <h2>Thread Management:</h2>
     * The thread terminates gracefully when interrupted or when the guardian state changes.
     */
    public void statesGuardian() {
    	Runnable task = () -> {
            while (guardianState == runningStates.UP) { // Check interface is up
            	if(wireguardManager.getConnectionStatus() == connectionStates.CONNECTED) {
            		if(antivirusManager.getScannerStatus() == runningStates.DOWN || downloadManager.getMonitorStatus() == runningStates.DOWN) {
            			
            			logger.error("An essential component has encountered an error - Shutting down services...");
            			
            			if(antivirusManager.getScannerStatus() == runningStates.DOWN && downloadManager.getMonitorStatus() == runningStates.UP) {
            				manageDownload(runningStates.DOWN);
            			} else if(downloadManager.getMonitorStatus() == runningStates.DOWN && antivirusManager.getScannerStatus() == runningStates.UP) {
            				manageAV(runningStates.DOWN);
            			}
            			
            			manageVPN(vpnOperations.STOP,null);
            		}
            	}
            	
            	logger.info("componentStatesGuardian: " + wireguardManager.getConnectionStatus() + antivirusManager.getScannerStatus() + downloadManager.getMonitorStatus());
                try {
                    Thread.sleep(200); // wait
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.error("startComponentStatesGuardian() thread unexpecly interrupted");
                    break;
                }
            }
            logger.info("startComponentStatesGuardian() thread stopped");
        };
        
        Thread thread = new Thread(task);
        guardianState = runningStates.UP;
        thread.start();
    }
    
    /**
     * Sets guardianState.
     *
     * @param enum runningStates object.
     */
    public void setGuardianState(runningStates s) {
    	guardianState = s;
    }
    
    /**
     * Retrieves guardianState.
     *
     * @return enum runningStates object.
     */
    public runningStates getGuardianState() {
    	return guardianState;
    }
    
}
