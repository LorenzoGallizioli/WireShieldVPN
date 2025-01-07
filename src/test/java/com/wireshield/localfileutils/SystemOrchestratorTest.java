package com.wireshield.localfileutils;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import com.wireshield.enums.runningStates;
import com.wireshield.av.AntivirusManager;
import com.wireshield.enums.connectionStates;
import com.wireshield.enums.vpnOperations;
import com.wireshield.wireguard.WireguardManager;

public class SystemOrchestratorTest {

    private SystemOrchestrator orchestrator;
    private WireguardManager wireguardManager;
    private DownloadManager downloadManager;
    private AntivirusManager antivirusManager;

    @Before
    public void setUp() {
        // Instanza di SystemOrchestrator
        orchestrator = SystemOrchestrator.getInstance();

        // Creazione degli oggetti necessari
        wireguardManager = WireguardManager.getInstance();
        antivirusManager = AntivirusManager.getInstance();
        downloadManager = DownloadManager.getInstance(antivirusManager);

        // Impostiamo le dipendenze per il SystemOrchestrator
        orchestrator.getWireguardManager(); // Non è necessario un mock
        orchestrator.getDownloadManager();  // Non è necessario un mock
        orchestrator.getAntivirusManager(); // Non è necessario un mock
    }

    // Test per il metodo manageVPN con operazione START
    @Test
    public void testManageVPNStart() {
        // Eseguiamo il metodo con l'operazione START
        orchestrator.manageVPN(vpnOperations.START, "testPeer.conf");

        // Verifica se il metodo setInterfaceUp è stato chiamato
        assertTrue("The VPN interface should be up", wireguardManager.getConnectionStatus() == connectionStates.CONNECTED);
    }

    // Test per il metodo manageVPN con operazione STOP
    @Test
    public void testManageVPNStop() {
        // Eseguiamo il metodo con l'operazione STOP
        orchestrator.manageVPN(vpnOperations.STOP, "Peer1");

        // Verifica se il metodo setInterfaceDown è stato chiamato
        assertTrue("The VPN interface should be down", wireguardManager.getConnectionStatus() == connectionStates.DISCONNECTED);
    }

    // Test per il metodo manageDownload con stato UP
    @Test
    public void testManageDownloadUp() {
        // Eseguiamo il metodo per avviare il monitoraggio
        orchestrator.manageDownload(runningStates.UP);

        // Verifica che il metodo startMonitoring sia stato chiamato correttamente
        assertTrue("The download monitoring service should be running", downloadManager.getMonitorStatus() == runningStates.UP);
    }

    // Test per il metodo manageDownload con stato DOWN
    @Test
    public void testManageDownloadDown() {
        // Eseguiamo il metodo per fermare il monitoraggio
        orchestrator.manageDownload(runningStates.DOWN);

        // Verifica che il metodo stopMonitoring sia stato chiamato correttamente
        assertTrue("The download monitoring service should be stopped", downloadManager.getMonitorStatus() == runningStates.DOWN);
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
    	
        orchestrator.manageVPN(vpnOperations.START, "testPeer.conf");
    	
        // Chiamata al metodo
        runningStates status = orchestrator.getAVStatus();

        // Verifica che lo stato dell'antivirus sia quello previsto
        assertEquals("The antivirus status should be UP", runningStates.UP, status);
    }

    // Test per il metodo addPeer
    @Test
    public void testAddPeer() {
        String peerData = "Sample Peer Data";
        String peerName = "Peer1";

        // Verifica che non ci siano errori quando aggiungiamo il peer
        orchestrator.addPeer(peerData, peerName);
        assertNotNull("Peer should be added", wireguardManager.getPeerManager());
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
}
