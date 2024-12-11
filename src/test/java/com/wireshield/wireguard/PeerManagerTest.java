
package com.wireshield.wireguard;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

public class PeerManagerTest {
	
	String testString =   "[Interface]\r\n"
						+ "PrivateKey = cIm09yQB5PUxKIhUwyK8TwL6ulaemcllbuzSCaOG0UM=\r\n"
						+ "Address = 172.0.0.6/32\r\n"
						+ "DNS = 172.0.0.1\r\n"
						+ "MTU = 1420\r\n"
						+ "\r\n"
						+ "[Peer]\r\n"
						+ "PublicKey = DlaMue3dkdiExmuYtQVAPlreolMWXP5zg9l1omRUDDA=\r\n"
						+ "PresharedKey = 0GYNNk24bSVUKBVGQU2tS1+tu5wT/RV2dQ3Z2gFxrNU=\r\n"
						+ "AllowedIPs = 0.0.0.0/0, ::/0\r\n"
						+ "Endpoint = 140.238.212.179:51820\r\n";

	@Test
	public void testPeerManager() {
		fail("Not yet implemented");
	}

	@Test
	public void testAddPeer() {
		fail("Not yet implemented");
	}

	@Test
	public void testRemovePeer() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetPeers() {
		
		fail("Not yet implemented");
	}
	
	@Test
	public void testParsePeerConfig() {
		Map<String, Map<String, String>> extDatas = PeerManager.parsePeerConfig(testString);
		if (!extDatas.get("Interface").get("PrivateKey").equals("cIm09yQB5PUxKIhUwyK8TwL6ulaemcllbuzSCaOG0UM=")) {
			fail("err");
		} else if(!extDatas.get("Interface").get("Address").equals("172.0.0.6/32")) {
			fail("err");
		} else if(!extDatas.get("Interface").get("DNS").equals("172.0.0.1")) {
			fail("err");
		} else if(!extDatas.get("Interface").get("MTU").equals("1420")) {
			fail("err");
		} else if(!extDatas.get("Peer").get("PublicKey").equals("DlaMue3dkdiExmuYtQVAPlreolMWXP5zg9l1omRUDDA=")) {
			fail("err");
		} else if(!extDatas.get("Peer").get("PresharedKey").equals("0GYNNk24bSVUKBVGQU2tS1+tu5wT/RV2dQ3Z2gFxrNU=")) {
			fail("err");
		} else if(!extDatas.get("Peer").get("AllowedIPs").equals("0.0.0.0/0, ::/0")) {
			fail("err");
		} else if(!extDatas.get("Peer").get("Endpoint").equals("140.238.212.179:51820")) {
			fail("err");
		}
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
