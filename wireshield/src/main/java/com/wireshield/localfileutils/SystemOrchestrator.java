package com.wireshield.localfileutils;

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
    private DownloadManager downloadManager;   // Manages download monitoring
    private AntivirusManager antivirusManager; // Manages antivirus operations
    private ClamAV clamAV;                     // Integrates ClamAV for file scanning
    private VirusTotal virusTotal;             // Integrates VirusTotal for file scanning
    private runningStates avStatus = runningStates.DOWN; // Current antivirus status
    private runningStates monitorStatus = runningStates.DOWN; // Current download monitoring status
    
    // Control variable for componentStatesGuardian thread --> runningStates.UP let the thread to continue running, runningStates.DOWN stops the thread execution
    private runningStates guardianState = runningStates.DOWN; 

	/**
	 * Private constructor to initialize the SystemOrchestrator instance. Configures
	 * all necessary components.
	 */
    private SystemOrchestrator() {
        this.wireguardManager = WireguardManager.getInstance(); // Initialize WireguardManager
        this.antivirusManager = AntivirusManager.getInstance(); // Initialize AntivirusManager
        this.downloadManager = DownloadManager.getInstance(antivirusManager); // Initialize DownloadManager
        this.clamAV = ClamAV.getInstance(); // Initialize ClamAV
        this.virusTotal = VirusTotal.getInstance(); // Initialize VirusTotal
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
		monitorStatus = status; // Update download monitoring status
		logger.info("Managing download monitoring service. Desired state: {}", status);

        if (monitorStatus == runningStates.UP) {
            if (downloadManager.getMonitorStatus() != runningStates.UP) {
                logger.info("Starting download monitoring service...");
                
                downloadManager.startMonitoring(); // Start monitoring downloads
                monitorStatus = downloadManager.getMonitorStatus();
                
                if(monitorStatus == runningStates.UP) {
                	logger.info("Download monitor service started successfully.");
                } else {
                	logger.error("Error occurred during Download monitor process starting.");
                }
                
            } else {
                logger.info("Download monitoring service is already running.");
            }
        }
        else 
        {
            if (downloadManager.getMonitorStatus() != runningStates.DOWN) {
                logger.info("Stopping download monitoring service...");
                
                downloadManager.forceStopMonitoring(); // Stop monitoring downloads
                monitorStatus = downloadManager.getMonitorStatus();
                
                if(monitorStatus == runningStates.DOWN) {
                	logger.info("Download monitor service stopped successfully.");
                } else {
                	logger.error("Error occurred during Download monitor process stopping.");
                }
                
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
		avStatus = status; // Update antivirus status
		logger.info("Managing antivirus service. Desired state: {}", status);

		if (avStatus == runningStates.UP) {
			if (antivirusManager.getScannerStatus() != runningStates.UP) {
				
                // Starting antivirus scan and providing progress
                antivirusManager.startScan();
                avStatus = antivirusManager.getScannerStatus();
                
                if(avStatus == runningStates.UP) {
                	logger.info("Antivirus service started successfully.");
                } else {
                	logger.error("Error occurred during AV process starting.");
                }
                
            } else { 
                logger.info("Antivirus service is already running.");
            }
        } 
		else 
		{
            if (antivirusManager.getScannerStatus() != runningStates.DOWN) {
                
                // Stopping the scan
                antivirusManager.stopScan(); 
                avStatus = antivirusManager.getScannerStatus();
                
                if(avStatus == runningStates.DOWN) {
                	logger.info("Antivirus service stopped successfully.");
                } else {
                	logger.error("Error occurred during AV process stopping.");
                }

            } else {
                logger.info("Antivirus service is already stopped.");
            }
        }

		// Display final scan reports
		List<ScanReport> finalReports = antivirusManager.getFinalReports();
		if (finalReports.isEmpty()) {} 
		else {
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
            while (guardianState == runningStates.UP && !Thread.currentThread().isInterrupted()) { // Check interface is up
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
            	else
            	{
            		if(downloadManager.getMonitorStatus() == runningStates.UP) {
        				manageDownload(runningStates.DOWN);
        			} if(antivirusManager.getScannerStatus() == runningStates.UP) {
        				manageAV(runningStates.DOWN);
        			}
            	}
            	
            	//logger.info("componentStatesGuardian: " + wireguardManager.getConnectionStatus() + antivirusManager.getScannerStatus() + downloadManager.getMonitorStatus());
                try {
                    Thread.sleep(200); // wait
                    
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.error("startComponentStatesGuardian() thread unexpecly interrupted");
                    
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
    
    /*
     * Only for test
     */
    protected void resetIstance() {
    	instance = null;
    }
    
}
