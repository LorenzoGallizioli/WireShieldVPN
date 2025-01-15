package com.wireshield.wireguard;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the WireguardManager class in the WireGuard module.
 * These tests verify the functionality of managing the interface status (up/down) using the WireguardManager.
 */
public class WireguardManagerTest {
	
    // Configuration name for testing purposes
	String confName = "testPeer";
	
    // WireguardManager instance for performing operations
	WireguardManager wireguardManager;

    /**
     * Setup method that initializes the WireguardManager instance before each test.
     * This method is run before each test method is executed.
     */
    @Before
    public void setUp() {
    	wireguardManager = WireguardManager.getInstance();
    }
    
    /**
     * Test the setInterfaceUp() and setInterfaceDown() methods of WireguardManager.
     * This test verifies if the interface can be brought up and down, and if the active interface is correctly set or cleared.
     */
    @Test
    public void testSetInterface_UpDown() {

        // Construct configuration path using the confName
    	String confPath = confName + ".conf";
        // Print the configuration path to the console for debugging purposes
    	System.out.println(confPath);
        // Test bringing the interface up and verify it returns true
        assertTrue(wireguardManager.setInterfaceUp(confPath));

        // Wait for a brief period to allow the interface to be set up
        try {
			Thread.sleep(1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        // Print the active interface to ensure it matches the expected configuration
        System.out.println(wireguardManager.getConnection().getActiveInterface());
        
        // Verify that the active interface matches the confName (the expected name)
        assertEquals(wireguardManager.getConnection().getActiveInterface(), confName);

        // Test bringing the interface down and verify it returns true
        assertTrue(wireguardManager.setInterfaceDown());
        
        // Ensure that the active interface is now null after bringing it down
        assertEquals(null, wireguardManager.getConnection().getActiveInterface());
    } 
}
