
package com.wireshield.wireguard;

import static org.junit.Assert.*;
import java.util.Map;

import org.junit.Before;
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
	Map<String, Map<String, String>> extDatas = PeerManager.parsePeerConfig(testString);
	
	PeerManager pm; 
	String peerId;

	/*@Test
	public void testPeerManager() {
		fail("Not yet implemented");
	}*/
	
	@Before
	public void setUp() {
		pm = PeerManager.getInstance();	
	}

	@Test
	public void testRemovePeer() {
		peerId = pm.createPeer(extDatas, "A");
		//System.out.println(peerId);
		
		assertFalse(pm.removePeer(null));
		assertFalse(pm.removePeer(""));
		
		Peer[] p_before = pm.getPeers();
		//System.out.println(p_before[0].toString());
		assertTrue(pm.removePeer(peerId));
		
		Peer[] p_after = pm.getPeers();
		//System.out.println(p_after[0].toString());
		assertNotEquals(p_after, p_before);
		
	}

	@Test
	public void testAddAndGetPeers() {
	    peerId = pm.createPeer(extDatas, "B");
		//System.out.println(peerId);

	    Peer[] p = pm.getPeers();

	    assertTrue("Peers list is empty", p.length > 0);
	    assertTrue("First peer does not match the peer retrieved by ID", p[p.length - 1].equals(pm.getPeerById(peerId)));
	}

	@Test
	public void testGetPeerById() {
	    peerId = pm.createPeer(extDatas, "C");
		//System.out.println(peerId);

	    Peer peer = pm.getPeerById("");
	    assertNull(peer);
	    peer = pm.getPeerById(null);
	    assertNull(peer);
	    peer = pm.getPeerById(peerId);
	    //System.out.println(peer.toString());
	    
	    Peer[] p = pm.getPeers();
	    //System.out.println(p[0].toString());

	    assertEquals("Peer retrieved by ID does not match the first peer in the list", p[p.length - 1], peer);
	}
	

	@Test
	public void testParsePeerConfig() {
	    Map<String, Map<String, String>> extDatas = PeerManager.parsePeerConfig(testString);

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
