package com.wireshield.wireguard;

import static org.junit.Assert.*;
import java.io.File;

import org.junit.Before;
import org.junit.Test;

import com.wireshield.av.FileManager;

public class WireguardManagerTest {
	
	String confName = "testPeer";
	WireguardManager wireguardManager;

    @Before
    public void setUp() {
    	wireguardManager = WireguardManager.getInstance();
    }
    
    @Test
    public void testSetInterface_UpDown() {

    	String confPath = confName + ".conf";
    	System.out.println(confPath);
        assertTrue(wireguardManager.setInterfaceUp(confPath));
        
        try {
			Thread.sleep(1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        System.out.println(wireguardManager.getConnection().getActiveInterface());
        assertEquals(wireguardManager.getConnection().getActiveInterface(), confName);

        assertTrue(wireguardManager.setInterfaceDown());
        assertEquals(wireguardManager.getConnection().getActiveInterface(), null);

    } 

}



