package com.wireshield.localfileutils;

import com.wireshield.av.AntivirusManager;
import com.wireshield.enums.connectionStates;
import com.wireshield.enums.runningStates;
import com.wireshield.wireguard.WireguardManager;
import com.wireshield.enums.vpnOperations;
import com.wireshield.wireguard.Peer;
import com.wireshield.wireguard.PeerManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.longThat;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;



public class SystemOrchestratorTest {

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

	Map<String, Map<String, String>> extDatas = PeerManager.parsePeerConfig(testString);

	String peerId;
	PeerManager pm;
	Logger logger = LogManager.getLogger(SystemOrchestratorTest.class);

    private SystemOrchestrator orchestrator;

    @Mock
    private WireguardManager wireguardManager;

    @Mock
    private DownloadManager downloadManager;

    @Mock
    private AntivirusManager antivirusManager;

    /*
     * Sets up the test environment.
     * This method is executed before each test and initializes the SystemOrchestrator instance.
     */
    @Before
    public void setUp() {
		pm = PeerManager.getInstance();

    	MockitoAnnotations.openMocks(this);
        orchestrator = SystemOrchestrator.getInstance();
        orchestrator.setGuardianState(runningStates.DOWN); // Ensure guardian is stopped

        orchestrator.setObjects(wireguardManager, antivirusManager, downloadManager);
    }

	// Test per il metodo manageVPN con operazione START
	@Test
	public void testManageVPNStart() {
		// Eseguiamo il metodo con l'operazione START
		orchestrator.manageVPN(vpnOperations.START, "testPeer.conf");

        // Test managing download monitoring when it is UP
        orchestrator.manageDownload(runningStates.UP);
        assertEquals(runningStates.UP, orchestrator.getMonitorStatus());
        logger.info("Download monitoring status set to UP");

        // Test managing download monitoring when it is DOWN
        orchestrator.manageDownload(runningStates.DOWN);
        assertEquals(runningStates.DOWN, orchestrator.getMonitorStatus());
        logger.info("Download monitoring status set to DOWN");
    }

	// Test per il metodo manageVPN con operazione STOP
	@Test
	public void testManageVPNStop() {
		// Eseguiamo il metodo con l'operazione STOP
		orchestrator.manageVPN(vpnOperations.STOP, null);

		// Verifica se il metodo setInterfaceDown è stato chiamato
		assertTrue("The VPN interface should be down",
				wireguardManager.getConnectionStatus() == connectionStates.DISCONNECTED);
	}

	// Test per il metodo manageDownload con stato UP
	@Test
	public void testManageDownloadUp() {
		// Eseguiamo il metodo per avviare il monitoraggio
		orchestrator.manageDownload(runningStates.UP);

		// Verifica che il metodo startMonitoring sia stato chiamato correttamente
		assertTrue("The download monitoring service should be running",
				downloadManager.getMonitorStatus() == runningStates.UP);
	}

	// Test per il metodo manageDownload con stato DOWN
	@Test
	public void testManageDownloadDown() {
		// Eseguiamo il metodo per fermare il monitoraggio
		orchestrator.manageDownload(runningStates.DOWN);

		// Verifica che il metodo stopMonitoring sia stato chiamato correttamente
		assertTrue("The download monitoring service should be stopped",
				downloadManager.getMonitorStatus() == runningStates.DOWN);
	}

	// Test per il metodo manageAV con stato UP
	@Test
	public void testManageAVUp() {
		// Eseguiamo il metodo per avviare l'antivirus
		orchestrator.manageAV(runningStates.UP);

		// Verifica che il metodo startScan sia stato chiamato correttamente
		assertTrue("The antivirus scan should be running", antivirusManager.getScannerStatus() == runningStates.UP);
	}

	// Test per il metodo manageAV con stato DOWN
	@Test
	public void testManageAVDown() {
		// Eseguiamo il metodo per fermare l'antivirus
		orchestrator.manageAV(runningStates.DOWN);

		// Verifica che il metodo stopScan sia stato chiamato correttamente
		assertTrue("The antivirus scan should be stopped", antivirusManager.getScannerStatus() == runningStates.DOWN);
	}

	// Test per il metodo getConnectionStatus
	@Test
	public void testGetConnectionStatus() {

		orchestrator.manageVPN(vpnOperations.START, "testPeer.conf");

		// Chiamata al metodo
		connectionStates status = orchestrator.getConnectionStatus();

		// Verifica che lo stato di connessione sia quello previsto
		assertEquals("The connection status should be CONNECTED", connectionStates.CONNECTED, status);
	}

	// Test per il metodo getMonitorStatus
	@Test
	public void testGetMonitorStatus() {

		orchestrator.manageDownload(runningStates.UP);

		// Chiamata al metodo
		runningStates status = orchestrator.getMonitorStatus();

		// Verifica che lo stato di monitoraggio sia quello previsto
		assertEquals("The monitor status should be UP", runningStates.UP, status);
	}

	// Test per il metodo getAVStatus
	@Test
	public void testGetAVStatus() {

		orchestrator.manageAV(runningStates.UP);

		// Chiamata al metodo
		runningStates status = orchestrator.getAVStatus();

		// Verifica che lo stato dell'antivirus sia quello previsto
		assertEquals("The antivirus status should be UP", runningStates.UP, status);
	}

	// Test per il metodo addPeer
	@Test
	public void testAddPeer() {
		peerId = pm.createPeer(extDatas, "B");

		Peer[] p = pm.getPeers();

		assertTrue("Peers list is empty", p.length > 0);
		assertEquals(p[p.length - 1], pm.getPeerById(peerId));
	} 

	// Test per il metodo getReportInfo
	@Test
	public void testGetReportInfo() {
		String report = "SampleReport";

		// Chiamata al metodo
		String result = orchestrator.getReportInfo(report);

		// Verifica che la risposta non sia nulla
		assertNotNull("Report info should not be null", result);
	}

	// Test per il metodo getWireguardManager
	@Test
	public void testGetWireguardManager() {
		// Chiamata al metodo
		WireguardManager manager = orchestrator.getWireguardManager();

		// Verifica che venga restituito il WireguardManager
		assertNotNull("WireguardManager should not be null", manager);
	}

	// Test per il metodo getDownloadManager
	@Test
	public void testGetDownloadManager() {
		// Chiamata al metodo
		DownloadManager manager = orchestrator.getDownloadManager();

		// Verifica che venga restituito il DownloadManager
		assertNotNull("DownloadManager should not be null", manager);
	}

	// Test per il metodo getAntivirusManager
	@Test
	public void testGetAntivirusManager() {
		// Chiamata al metodo
		AntivirusManager manager = orchestrator.getAntivirusManager();

		// Verifica che venga restituito l'AntivirusManager
		assertNotNull("AntivirusManager should not be null", manager);
	}

    @Test
    public void testStatesGuardian_NormalOperation() throws InterruptedException {
        // Mock normal operation
        when(wireguardManager.getConnectionStatus()).thenReturn(connectionStates.CONNECTED);
        when(antivirusManager.getScannerStatus()).thenReturn(runningStates.UP);
        when(downloadManager.getMonitorStatus()).thenReturn(runningStates.UP);

        orchestrator.setGuardianState(runningStates.UP);
        orchestrator.statesGuardian();

        // Allow some time for the thread to execute
        Thread.sleep(500);

        // Verify no actions taken
        verify(wireguardManager, never()).setInterfaceDown();
        verify(downloadManager, never()).forceStopMonitoring();
        verify(antivirusManager, never()).stopScan();

        orchestrator.setGuardianState(runningStates.DOWN); // Stop the guardian thread
    }
    
    /**
     * Test method. Simulate a AV failure and detects if GuardianState Thread takes action.
     * It is not possible to verify effectiveness but only that it invokes the designated functions.
     */
    @Test
    public void testStatesGuardian_AVComponentFailure() throws InterruptedException {
        // Mock antivirus failure
        when(orchestrator.getWireguardManager().getConnectionStatus()).thenReturn(connectionStates.CONNECTED);
        when(orchestrator.getAntivirusManager().getScannerStatus()).thenReturn(runningStates.DOWN);
        when(orchestrator.getDownloadManager().getMonitorStatus()).thenReturn(runningStates.UP);

        orchestrator.statesGuardian();

        // Allow some time for the thread to detect the failure
        Thread.sleep(500);

        // Verify actions taken
        verify(orchestrator.getDownloadManager(), atLeast(1)).forceStopMonitoring();
        verify(orchestrator.getWireguardManager(), atLeast(1)).setInterfaceDown();

        orchestrator.setGuardianState(runningStates.DOWN); // Stop the guardian thread
    }
    
    /**
     * Test method. Simulate a DowmloadMonitor failure and detects if GuardianState Thread takes action.
     * It is not possible to verify effectiveness but only that it invokes the designated functions.
     */
    @Test
    public void testStatesGuardian_DownloadComponentFailure() throws InterruptedException {
        // Mock antivirus failure
        when(orchestrator.getWireguardManager().getConnectionStatus()).thenReturn(connectionStates.CONNECTED);
        when(orchestrator.getAntivirusManager().getScannerStatus()).thenReturn(runningStates.UP);
        when(orchestrator.getDownloadManager().getMonitorStatus()).thenReturn(runningStates.DOWN);

        orchestrator.statesGuardian();

        // Allow some time for the thread to detect the failure
        Thread.sleep(500);

        // Verify actions taken
        verify(orchestrator.getAntivirusManager(), atLeast(1)).stopScan();
        verify(orchestrator.getWireguardManager(), atLeast(1)).setInterfaceDown();

        orchestrator.setGuardianState(runningStates.DOWN); // Stop the guardian thread
    }
    
    @Test
    public void testStatesGuardian_ThreadRunningControls() throws InterruptedException {
        orchestrator.statesGuardian();
        
        // Allow the thread to start
        Thread.sleep(200);
        
        assertEquals(runningStates.UP, orchestrator.getGuardianState());
        orchestrator.setGuardianState(runningStates.DOWN);

        // Allow the thread to terminate gracefully
        Thread.sleep(200);

        assertEquals(runningStates.DOWN, orchestrator.getGuardianState());
    }
    
    @Test
    public void testSetAndGetGuardianState() {
        // Set state and verify
    	orchestrator.setGuardianState(runningStates.UP);
        assertEquals(runningStates.UP, orchestrator.getGuardianState());

        orchestrator.setGuardianState(runningStates.DOWN);
        assertEquals(runningStates.DOWN, orchestrator.getGuardianState());
    }
}
