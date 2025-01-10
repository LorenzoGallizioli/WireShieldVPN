
package com.wireshield.wireguard;

import static org.junit.Assert.*;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class PeerManagerTest {
	
	String testString = """
            [Interface]
            PrivateKey = cIm09yQB5PUxKIhUwyK8TwL6ulaemcllbuzSCaOG0UM=
            Address = 172.0.0.6/32
            DNS = 172.0.0.1
            MTU = 1420
            
            [Peer]
            PublicKey = DlaMue3dkdiExmuYtQVAPlreolMWXP5zg9l1omRUDDA=
            PresharedKey = 0GYNNk24bSVUKBVGQU2tS1+tu5wT/RV2dQ3Z2gFxrNU=
            AllowedIPs = 0.0.0.0/0, ::/0
            Endpoint = 140.238.212.179:51820
            """;

	Map<String, Map<String, String>> extDatas = PeerManager.parsePeerConfig(testString);
	
	PeerManager pm; 
	String peerId;
	
	@Before
	public void setUp() {
		pm = PeerManager.getInstance();	
	}

	@Test
	public void testRemovePeer() {
		peerId = pm.createPeer(extDatas, "A");
		
		assertFalse(pm.removePeer(null));
		assertFalse(pm.removePeer(""));
		
		Peer[] pBefore = pm.getPeers();
		assertTrue(pm.removePeer(peerId));
		
		Peer[] pAfter = pm.getPeers();
		assertNotEquals(pAfter, pBefore);
		
	}

	@Test
	public void testAddAndGetPeers() {
	    peerId = pm.createPeer(extDatas, "B");

	    Peer[] p = pm.getPeers();

	    assertTrue("Peers list is empty", p.length > 0);
	    assertEquals(p[p.length - 1], pm.getPeerById(peerId));
	}

	@Test
	public void testGetPeerById() {
	    peerId = pm.createPeer(extDatas, "C");

	    Peer peer = pm.getPeerById("");
	    assertNull(peer);
	    peer = pm.getPeerById(null);
	    assertNull(peer);
	    peer = pm.getPeerById(peerId);
	    
	    Peer[] p = pm.getPeers();

	    assertEquals("Peer retrieved by ID does not match the first peer in the list", p[p.length - 1], peer);
	} 
	

	@Test
	public void testParsePeerConfig() {
	    assertEquals("PrivateKey mismatch", "cIm09yQB5PUxKIhUwyK8TwL6ulaemcllbuzSCaOG0UM=", extDatas.get("Interface").get("PrivateKey"));
	    assertEquals("Address mismatch", "172.0.0.6/32", extDatas.get("Interface").get("Address"));
	    assertEquals("DNS mismatch", "172.0.0.1", extDatas.get("Interface").get("DNS"));
	    assertEquals("MTU mismatch", "1420", extDatas.get("Interface").get("MTU"));
	    assertEquals("PublicKey mismatch", "DlaMue3dkdiExmuYtQVAPlreolMWXP5zg9l1omRUDDA=", extDatas.get("Peer").get("PublicKey"));
	    assertEquals("PresharedKey mismatch", "0GYNNk24bSVUKBVGQU2tS1+tu5wT/RV2dQ3Z2gFxrNU=", extDatas.get("Peer").get("PresharedKey"));
	    assertEquals("AllowedIPs mismatch", "0.0.0.0/0, ::/0", extDatas.get("Peer").get("AllowedIPs"));
	    assertEquals("Endpoint mismatch", "140.238.212.179:51820", extDatas.get("Peer").get("Endpoint"));
	}

}
