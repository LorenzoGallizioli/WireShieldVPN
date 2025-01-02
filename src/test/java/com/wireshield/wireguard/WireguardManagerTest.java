package com.wireshield.wireguard;

import static org.junit.Assert.*;
import java.io.File;
import java.io.IOException;

import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Test;

import com.wireshield.av.FileManager;

public class WireguardManagerTest {
	
	String configPath = "testPeer.conf";

    @Before
    public void setup() {
        File file = new File(FileManager.getProjectFolder() + FileManager.getConfigValue("WIREGUARDEXE_STD_PATH"));
        if (!file.exists() || !file.isFile()) {
            fail("[ERR] WireGuard executable not found.");
        }
    }
    
    @Test
    public void testSetInterfaceUp() {
        WireguardManager wireguardManager = WireguardManager.getInstance();
        assertTrue(wireguardManager.setInterfaceUp(configPath).booleanValue());
    }

    @Test
    public void testSetInterfaceDown() {
		try {
			Thread.sleep(1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
        WireguardManager wireguardManager = WireguardManager.getInstance();
        assertTrue(wireguardManager.setInterfaceDown());
    }

}
