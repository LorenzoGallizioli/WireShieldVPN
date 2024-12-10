package com.wireshield.localfileutils;

import com.wireshield.av.AntivirusManager;
import com.wireshield.wireguard.WireguardManager;
import com.wireshield.enums.runningStates;
import com.wireshield.enums.connectionStates;
import com.wireshield.enums.vpnOperations;

/**
 * The SystemOrchestrator class is responsible for orchestrating the system.
 */
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
	public SystemOrchestrator() {

		// Crea una sola istanza di AntivirusManager
		this.antivirusManager = AntivirusManager.getInstance();

		// Passa questa istanza di AntivirusManager al DownloadManager
		this.downloadManager = new DownloadManager(antivirusManager);

	}

	/**
	 * Method to manage the VPN connection.
	 * 
	 * @param operation The operation to be performed.
	 * @param status    The connection status.
	 */
	public void manageVPN(vpnOperations operation, connectionStates status) {
	}

	public void manageDownload(runningStates status) {
		// Imposta lo stato dell'antivirus
		this.monitorStatus = status;

		if (monitorStatus == runningStates.UP) {
			System.out.println("Starting download monitoring service...");
			new Thread(downloadManager::startMonitoring).start();
		} else {
			System.out.println("Stopping Download monitoring service...");
		}
	}

	/**
	 * Method to manage the antivirus.
	 * 
	 * @param status The antivirus status.
	 */
	public void manageAV(runningStates status) {
		// Imposta lo stato dell'antivirus
		this.avStatus = status;

		// Avvia o ferma la scansione dell'antivirus in base allo stato
		if (status == runningStates.UP) {
			System.out.println("Starting antivirus scan...");
			antivirusManager.performScan(); // Avvia la scansione dei file
		} else {
			System.out.println("Stopping antivirus...");
			// Logica per fermare l'antivirus se necessario
		}
	}

	/**
	 * Returns the connection status.
	 * 
	 * @return connectionStates The connection status.
	 */
	public connectionStates getConnectionStatus() {
		return connectionStatus;
	}

	/**
	 * Returns the monitor status.
	 * 
	 * @return runningStates The monitor status.
	 */
	public runningStates getMonitorStatus() {
		return monitorStatus;
	}

	/**
	 * Returns the antivirus status.
	 * 
	 * @return runningStates The antivirus status.
	 */
	public runningStates getAVStatus() {
		return avStatus;
	}

	/**
	 * Creates a peer.
	 * 
	 * @param peer The peer to be created.
	 */
	public void createPeer(String peer) {
	}

	/**
	 * Gets the report info.
	 * 
	 * @param report The report to be retrieved.
	 * @return String The report info.
	 */
	public String getReportInfo(String report) {
		return "";
	}
}
