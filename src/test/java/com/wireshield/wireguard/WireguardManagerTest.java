package com.wireshield.wireguard;

import static org.junit.Assert.*;
import java.io.File;
import org.junit.Before;
import org.junit.Test;

public class WireguardManagerTest {

    @Before
    public void setup() {
        File file = new File("C:\\Program Files\\WireGuard\\wireguard.exe");
        if (!file.exists() || !file.isFile()) {
            fail("[ERR] WireGuard executable not found.");
        }
    }

    @Test
    public void testSetInterfaceUp() {
        WireguardManager wireguardManager = new WireguardManager("C:\\Program Files\\WireGuard\\wireguard.exe");
        assertTrue(wireguardManager.setInterfaceUp("C:\\\\Program Files\\\\WireGuard\\\\Data\\\\Configurations\\\\peer5_galliz.conf.dpapi"));
    }

    @Test
    public void testSetInterfaceDown() {
		try {
			Thread.sleep(1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
        WireguardManager wireguardManager = new WireguardManager("C:\\Program Files\\WireGuard\\wireguard.exe");
        assertTrue(wireguardManager.setInterfaceDown());
    }

}
