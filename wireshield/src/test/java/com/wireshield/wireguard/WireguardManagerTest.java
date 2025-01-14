package com.wireshield.wireguard;

import static org.junit.Assert.*;

import java.net.Socket;

import org.junit.Before;
import org.junit.Test;

import com.wireshield.av.FileManager;

public class WireguardManagerTest {
	
	String confName = "testPeer";
	String logDumpPath = FileManager.getProjectFolder() + FileManager.getConfigValue("LOGDUMP_STD_PATH");
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
        assertEquals(null, wireguardManager.getConnection().getActiveInterface());

    } 
    
    @Test
    public void updateWireguardLogs() throws InterruptedException {
    	
    	FileManager.deleteFile(logDumpPath);
    	FileManager.createFile(logDumpPath);
    	
    	String log_0 = FileManager.readFile(logDumpPath);
    	//System.out.println("log_0: " + log_0);
    	
    	wireguardManager.startUpdateWireguardLogs();
    	
    	Thread.sleep(1000);
    	
    	//System.out.println(wireguardManager.getLog());
    	assertNotEquals(log_0, wireguardManager.getLog());
    }

}



