package com.wireshield.localfileutils;

import com.wireshield.enums.runningStates;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static org.junit.Assert.*;

public class SystemOrchestratorTest {

    private static final Logger logger = LogManager.getLogger(SystemOrchestratorTest.class);

    private SystemOrchestrator systemOrchestrator;

    @Before
    public void setUp() {
        systemOrchestrator = new SystemOrchestrator();
        logger.info("SystemOrchestrator instance created");
    }

    @After
    public void tearDown() {
        systemOrchestrator = null;
        logger.info("SystemOrchestrator instance destroyed");
    }

    @Test
    public void testSystemOrchestrator() {
        logger.info("Running testSystemOrchestrator...");
        fail("Not yet implemented");
    }

    @Test
    public void testManageVPN() {
        logger.info("Running testManageVPN...");
        fail("Not yet implemented");
    }

    @Test
    public void testManageAV() {
        logger.info("Running testManageAV...");

        // Test managing AV when UP
        systemOrchestrator.manageAV(runningStates.UP);
        assertEquals(runningStates.UP, systemOrchestrator.getAVStatus());
        logger.info("AV status set to UP");

        // Test managing AV when DOWN
        systemOrchestrator.manageAV(runningStates.DOWN);
        assertEquals(runningStates.DOWN, systemOrchestrator.getAVStatus());
        logger.info("AV status set to DOWN");
    }

    @Test
    public void testManageDownload() {
        logger.info("Running testManageDownload...");

        // Test managing download when UP
        systemOrchestrator.manageDownload(runningStates.UP);
        assertEquals(runningStates.UP, systemOrchestrator.getMonitorStatus());
        logger.info("Download monitor status set to UP");

        // Test managing download when DOWN
        systemOrchestrator.manageDownload(runningStates.DOWN);
        assertEquals(runningStates.DOWN, systemOrchestrator.getMonitorStatus());
        logger.info("Download monitor status set to DOWN");
    }

    @Test
    public void testGetConnectionStatus() {
        logger.info("Running testGetConnectionStatus...");
        fail("Not yet implemented");
    }

    @Test
    public void testGetMonitorStatus() {
        logger.info("Running testGetMonitorStatus...");
        fail("Not yet implemented");
    }

    @Test
    public void testGetAVStatus() {
        logger.info("Running testGetAVStatus...");
        fail("Not yet implemented");
    }

    @Test
    public void testCreatePeer() {
        logger.info("Running testCreatePeer...");
        fail("Not yet implemented");
    }

    @Test
    public void testGetReportInfo() {
        logger.info("Running testGetReportInfo...");
        fail("Not yet implemented");
    }

    @Test
    public void testObject() {
        logger.info("Running testObject...");
        fail("Not yet implemented");
    }

    @Test
    public void testGetClass() {
        logger.info("Running testGetClass...");
        fail("Not yet implemented");
    }

    @Test
    public void testHashCode() {
        logger.info("Running testHashCode...");
        fail("Not yet implemented");
    }

    @Test
    public void testEquals() {
        logger.info("Running testEquals...");
        fail("Not yet implemented");
    }

    @Test
    public void testClone() {
        logger.info("Running testClone...");
        fail("Not yet implemented");
    }

    @Test
    public void testToString() {
        logger.info("Running testToString...");
        fail("Not yet implemented");
    }

    @Test
    public void testNotify() {
        logger.info("Running testNotify...");
        fail("Not yet implemented");
    }

    @Test
    public void testNotifyAll() {
        logger.info("Running testNotifyAll...");
        fail("Not yet implemented");
    }

    @Test
    public void testWait() {
        logger.info("Running testWait...");
        fail("Not yet implemented");
    }

    @Test
    public void testWaitLong() {
        logger.info("Running testWaitLong...");
        fail("Not yet implemented");
    }

    @Test
    public void testWaitLongInt() {
        logger.info("Running testWaitLongInt...");
        fail("Not yet implemented");
    }

    @Test
    public void testFinalize() {
        logger.info("Running testFinalize...");
        fail("Not yet implemented");
    }
}
