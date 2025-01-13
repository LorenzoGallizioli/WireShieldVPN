package com.wireshield.localfileutils;

import com.wireshield.av.AntivirusManager;
import com.wireshield.enums.connectionStates;
import com.wireshield.enums.runningStates;
import com.wireshield.wireguard.WireguardManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static org.junit.Assert.*;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/*
 * Unit test class for SystemOrchestrator.
 * This class tests various functionalities of the SystemOrchestrator class,
 * including managing VPN, AV, and download statuses, and retrieving system information.
 */
public class SystemOrchestratorTest {

    private static final Logger logger = LogManager.getLogger(SystemOrchestratorTest.class);

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
    	MockitoAnnotations.openMocks(this);
        orchestrator = SystemOrchestrator.getInstance();
        orchestrator.setGuardianState(runningStates.DOWN); // Ensure guardian is stopped

        orchestrator.setObjects(wireguardManager, antivirusManager, downloadManager);
    }

    /*
     * Tests the management of antivirus (AV) status.
     * This method verifies the ability to set and retrieve the AV status for values UP and DOWN.
     */
    @Test
    public void testManageAV() {
        logger.info("Running testManageAV...");

        // Test managing AV when it is UP
        orchestrator.manageAV(runningStates.UP);
        assertEquals(runningStates.UP, orchestrator.getAVStatus());
        logger.info("AV status set to UP");

        // Test managing AV when it is DOWN
        orchestrator.manageAV(runningStates.DOWN);
        assertEquals(runningStates.DOWN, orchestrator.getAVStatus());
        logger.info("AV status set to DOWN");
    }

    /*
     * Tests the management of download monitoring status.
     * This method verifies the ability to set and retrieve the download monitoring status for values UP and DOWN.
     */
    @Test
    public void testManageDownload() {
        logger.info("Running testManageDownload...");

        // Test managing download monitoring when it is UP
        orchestrator.manageDownload(runningStates.UP);
        assertEquals(runningStates.UP, orchestrator.getMonitorStatus());
        logger.info("Download monitoring status set to UP");

        // Test managing download monitoring when it is DOWN
        orchestrator.manageDownload(runningStates.DOWN);
        assertEquals(runningStates.DOWN, orchestrator.getMonitorStatus());
        logger.info("Download monitoring status set to DOWN");
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
        verify(antivirusManager, never()).forceStopPerformScan();

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
        verify(orchestrator.getAntivirusManager(), atLeast(1)).forceStopPerformScan();
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
