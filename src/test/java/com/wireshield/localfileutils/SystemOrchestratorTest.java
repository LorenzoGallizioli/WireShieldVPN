package com.wireshield.localfileutils;

import com.wireshield.enums.runningStates;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


public class SystemOrchestratorTest {

    private SystemOrchestrator systemOrchestrator;
	
    @Before
    public void setUp() {
        systemOrchestrator = new SystemOrchestrator();
    }

    @After
    public void tearDown() {
        systemOrchestrator = null;
    }

	@Test
	public void testSystemOrchestrator() {
		fail("Not yet implemented");
	}

	@Test
	public void testManageVPN() {
		fail("Not yet implemented");
	}

	@Test
	public void testManageAV() {
        systemOrchestrator.manageAV(runningStates.UP);
        assertEquals(runningStates.UP, systemOrchestrator.getAVStatus());

        systemOrchestrator.manageAV(runningStates.DOWN);
        assertEquals(runningStates.DOWN, systemOrchestrator.getAVStatus());
	}

    @Test
    public void testManageDownload() {
        systemOrchestrator.manageDownload(runningStates.UP);
        assertEquals(runningStates.UP, systemOrchestrator.getMonitorStatus());

        systemOrchestrator.manageDownload(runningStates.DOWN);
        assertEquals(runningStates.DOWN, systemOrchestrator.getMonitorStatus());
    }
	
	@Test
	public void testGetConnectionStatus() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetMonitorStatus() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetAVStatus() {
		fail("Not yet implemented");
	}

	@Test
	public void testCreatePeer() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetReportInfo() {
		fail("Not yet implemented");
	}

	@Test
	public void testObject() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetClass() {
		fail("Not yet implemented");
	}

	@Test
	public void testHashCode() {
		fail("Not yet implemented");
	}

	@Test
	public void testEquals() {
		fail("Not yet implemented");
	}

	@Test
	public void testClone() {
		fail("Not yet implemented");
	}

	@Test
	public void testToString() {
		fail("Not yet implemented");
	}

	@Test
	public void testNotify() {
		fail("Not yet implemented");
	}

	@Test
	public void testNotifyAll() {
		fail("Not yet implemented");
	}

	@Test
	public void testWait() {
		fail("Not yet implemented");
	}

	@Test
	public void testWaitLong() {
		fail("Not yet implemented");
	}

	@Test
	public void testWaitLongInt() {
		fail("Not yet implemented");
	}

	@Test
	public void testFinalize() {
		fail("Not yet implemented");
	}

}
