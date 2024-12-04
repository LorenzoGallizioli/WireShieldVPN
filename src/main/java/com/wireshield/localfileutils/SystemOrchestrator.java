package com.wireshield.localfileutils;

import com.wireshield.av.AntivirusManager;
import com.wireshield.wireguard.WireguardManager;
import com.wireshield.enums.runningStates;
import com.wireshield.enums.connectionStates;
import com.wireshield.enums.vpnOperations;

public class SystemOrchestrator {
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
     * @param status
     *   The connection status.
     */
    public void manageVPN(vpnOperations operation, connectionStates status) {}

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
    public void createPeer(String peer){}

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
