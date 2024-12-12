package com.wireshield.localfileutils;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.wireshield.av.AntivirusManager;
import com.wireshield.wireguard.PeerManager;
import com.wireshield.wireguard.WireguardManager;
import com.wireshield.enums.runningStates;
import com.wireshield.enums.connectionStates;
import com.wireshield.enums.vpnOperations;

/**
 * The SystemOrchestrator class is responsible for orchestrating the system.
 */
public class SystemOrchestrator {
    private static final Logger logger = LogManager.getLogger(SystemOrchestrator.class);

    private WireguardManager wireguardManager;
    private DownloadManager downloadManager;
    private AntivirusManager antivirusManager;
    private runningStates avStatus;
    private runningStates monitorStatus;
    private connectionStates connectionStatus;

    /**
     * The constructor for the SystemOrchestrator class.
     */
    public SystemOrchestrator() {}

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
     * Method to manage the antivirus.
     * 
     * @param status
     *   The antivirus status.
     */
    public void manageAV(runningStates status) {}

    /**
     * Returns the connection status.
     * 
     * @return connectionStates
     *   The connection status.
     */
    public connectionStates getConnectionStatus() {
        return connectionStatus;
    }

    /**
     * Returns the monitor status.
     * 
     * @return runningStates
     *   The monitor status. 
     */
    public runningStates getMonitorStatus() {
        return monitorStatus;
    }

    /**
     * Returns the antivirus status.
     * 
     * @return runningStates
     *   The antivirus status.
     */
    public runningStates getAVStatus() {
        return avStatus;
    }

    /**
     * Creates a peer.
     * 
     * @param peer
     *   The peer to be created.
     */
    public void addPeer(String peerData, String peerName){
		Map<String, Map<String, String>> peer = PeerManager.parsePeerConfig(peerName);
        wireguardManager.getPeerManager().createPeer(peer, peerName);
    }

    /**
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
     */
    public String getReportInfo (String report){
        return "";
    }
}
