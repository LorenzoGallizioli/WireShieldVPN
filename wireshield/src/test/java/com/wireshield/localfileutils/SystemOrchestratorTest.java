package com.wireshield.localfileutils;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import com.wireshield.enums.runningStates;
import com.wireshield.av.AntivirusManager;
import com.wireshield.enums.connectionStates;
import com.wireshield.enums.vpnOperations;
import com.wireshield.wireguard.Peer;
import com.wireshield.wireguard.PeerManager;
import com.wireshield.wireguard.WireguardManager;

/**
 * This class contains unit tests for the functionalities of the
 * {@code SystemOrchestrator} class. It tests various operations such as
 * managing VPN, download, and antivirus, as well as retrieving statuses and
 * reports.
 */
public class SystemOrchestratorTest {

	// Instances of the SystemOrchestrator, WireguardManager, DownloadManager, and
	// AntivirusManager
	private SystemOrchestrator orchestrator;
	private WireguardManager wireguardManager;
	private DownloadManager downloadManager;
	private AntivirusManager antivirusManager;

	// Test string used for parsing peer configurations
	String testString = """
			[Interface]
			PrivateKey = cIm09yQB5PUxKIhUwyK8TwL6ulaemcllbuzSCaOG0UM=
			Address = 172.0.0.6/32
			DNS = 172.0.0.1
			MTU = 1420

			[Peer]
			PublicKey = DlaMue3dkdiExmuYtQVAPlreolMWXP5zg9l1omRUDDA=
			PresharedKey = 0GYNNk24bSVUKBVGQU2tS1+tu5wT/RV2dQ3Z2gFxrNU=
			AllowedIPs = 0.0.0.0/0, ::/0
			Endpoint = 140.238.212.179:51820
			""";

	// Map of parsed peer data
	Map<String, Map<String, String>> extDatas = PeerManager.parsePeerConfig(testString);

	// Peer ID and PeerManager instance for testing
	String peerId;
	PeerManager pm;

	/**
	 * Sets up the test environment before each test. This method initializes the
	 * {@code SystemOrchestrator}, {@code WireguardManager},
	 * {@code DownloadManager}, and {@code AntivirusManager}, and creates necessary
	 * instances for testing.
	 */
	@Before
	public void setUp() {
		// Instantiate the SystemOrchestrator
		orchestrator = SystemOrchestrator.getInstance();

		// Create instances of the necessary managers
		wireguardManager = WireguardManager.getInstance();
		antivirusManager = AntivirusManager.getInstance();
		downloadManager = DownloadManager.getInstance(antivirusManager);
		pm = PeerManager.getInstance();

		// Set dependencies for the SystemOrchestrator
		orchestrator.getWireguardManager();
		orchestrator.getDownloadManager();
		orchestrator.getAntivirusManager();

	}

	/**
	 * Test the {@code manageVPN} method with the START operation. This test checks
	 * if the VPN connection status is set to {@code CONNECTED} after starting the
	 * VPN.
	 */
	@Test
	public void testManageVPNStart() {
		// Execute the method with the START operation
		orchestrator.manageVPN(vpnOperations.START, "testPeer.conf");

		// Verify that the connection status is CONNECTED
		assertTrue("The VPN interface should be up",
				wireguardManager.getConnectionStatus() == connectionStates.CONNECTED);
	}

	/**
	 * Test the {@code manageVPN} method with the STOP operation. This test checks
	 * if the VPN connection status is set to {@code DISCONNECTED} after stopping
	 * the VPN.
	 */
	@Test
	public void testManageVPNStop() {
		// Execute the method with the STOP operation
		orchestrator.manageVPN(vpnOperations.STOP, null);

		// Verify that the connection status is DISCONNECTED
		assertTrue("The VPN interface should be down",
				wireguardManager.getConnectionStatus() == connectionStates.DISCONNECTED);
	}

	/**
	 * Test the {@code manageDownload} method with the UP state. This test verifies
	 * that the download monitoring service is started successfully.
	 */
	@Test
	public void testManageDownloadUp() {
		// Execute the method to start the download monitoring
		orchestrator.manageDownload(runningStates.UP);

		// Verify that the monitoring status is UP
		assertTrue("The download monitoring service should be running",
				downloadManager.getMonitorStatus() == runningStates.UP);
	}

	/**
	 * Test the {@code manageDownload} method with the DOWN state. This test
	 * verifies that the download monitoring service is stopped successfully.
	 */
	@Test
	public void testManageDownloadDown() {
		// Execute the method to stop the download monitoring
		orchestrator.manageDownload(runningStates.DOWN);

		// Verify that the monitoring status is DOWN
		assertTrue("The download monitoring service should be stopped",
				downloadManager.getMonitorStatus() == runningStates.DOWN);
	}

	/**
	 * Test the {@code manageAV} method with the UP state. This test verifies that
	 * the antivirus scan is started successfully.
	 */
	@Test
	public void testManageAVUp() {
		// Execute the method to start the antivirus scan
		orchestrator.manageAV(runningStates.UP);

		// Verify that the antivirus scan status is UP
		assertTrue("The antivirus scan should be running", antivirusManager.getScannerStatus() == runningStates.UP);
	}

	/**
	 * Test the {@code manageAV} method with the DOWN state. This test verifies that
	 * the antivirus scan is stopped successfully.
	 */
	@Test
	public void testManageAVDown() {
		// Execute the method to stop the antivirus scan
		orchestrator.manageAV(runningStates.DOWN);

		// Verify that the antivirus scan status is DOWN
		assertTrue("The antivirus scan should be stopped", antivirusManager.getScannerStatus() == runningStates.DOWN);
	}

	/**
	 * Test the {@code getConnectionStatus} method. This test verifies that the
	 * correct connection status is returned.
	 */
	@Test
	public void testGetConnectionStatus() {

		orchestrator.manageVPN(vpnOperations.START, "testPeer.conf");

		// Call the method to get the connection status
		connectionStates status = orchestrator.getConnectionStatus();

		// Verify that the connection status is CONNECTED
		assertEquals("The connection status should be CONNECTED", connectionStates.CONNECTED, status);
	}

	/**
	 * Test the {@code getMonitorStatus} method. This test verifies that the correct
	 * monitoring status is returned.
	 */
	@Test
	public void testGetMonitorStatus() {

		orchestrator.manageDownload(runningStates.UP);

		// Call the method to get the monitoring status
		runningStates status = orchestrator.getMonitorStatus();

		// Verify that the monitoring status is UP
		assertEquals("The monitor status should be UP", runningStates.UP, status);
	}

	/**
	 * Test the {@code getAVStatus} method. This test verifies that the correct
	 * antivirus status is returned.
	 */
	@Test
	public void testGetAVStatus() {

		orchestrator.manageAV(runningStates.UP);

		// Call the method to get the antivirus status
		runningStates status = orchestrator.getAVStatus();

		// Verify that the antivirus status is UP
		assertEquals("The antivirus status should be UP", runningStates.UP, status);
	}

	/**
	 * Test the {@code addPeer} method. This test verifies that a peer is
	 * successfully added to the system.
	 */
	@Test
	public void testAddPeer() {

		// Add a peer and get the peer list
		peerId = pm.createPeer(extDatas, "B");

		Peer[] p = pm.getPeers();

		// Verify that the peer list is not empty and the peer has been added
		assertTrue("Peers list is empty", p.length > 0);
		assertEquals(p[p.length - 1], pm.getPeerById(peerId));
	}

	/**
	 * Test the {@code getReportInfo} method. This test verifies that the report
	 * information is retrieved correctly.
	 */
	@Test
	public void testGetReportInfo() {
		String report = "SampleReport";

		// Call the method to get the report info
		String result = orchestrator.getReportInfo(report);

		// Verify that the result is not null
		assertNotNull("Report info should not be null", result);
	}

	/**
	 * Test the {@code getWireguardManager} method. This test verifies that the
	 * WireguardManager instance is returned.
	 */
	@Test
	public void testGetWireguardManager() {

		// Call the method to get the WireguardManager instance
		WireguardManager manager = orchestrator.getWireguardManager();

		// Verify that the WireguardManager instance is not null
		assertNotNull("WireguardManager should not be null", manager);
	}

	/**
	 * Test the {@code getDownloadManager} method. This test verifies that the
	 * DownloadManager instance is returned.
	 */
	@Test
	public void testGetDownloadManager() {

		// Call the method to get the DownloadManager instance
		DownloadManager manager = orchestrator.getDownloadManager();

		// Verify that the DownloadManager instance is not null
		assertNotNull("DownloadManager should not be null", manager);
	}

	/**
	 * Test the {@code getAntivirusManager} method. This test verifies that the
	 * AntivirusManager instance is returned.
	 */
	@Test
	public void testGetAntivirusManager() {
		// Call the method to get the AntivirusManager instance
		AntivirusManager manager = orchestrator.getAntivirusManager();

		// Verify that the AntivirusManager instance is not null
		assertNotNull("AntivirusManager should not be null", manager);
	}
}
