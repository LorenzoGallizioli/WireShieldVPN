package com.wireshield.wireguard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The PeerManager class is responsible for managing WireGuard peers.
 */
public class PeerManager {

	private static PeerManager instance;
	private List<Peer> peers;

	/**
	 * The constructor for the PeerManager class.
	 */
	private PeerManager() {
		this.peers = new ArrayList<>();
	}

	/**
	 * Public method to get the Singleton instance.
	 * 
	 * @return the single instance of PeerManager.
	 */
	public static synchronized PeerManager getInstance() {
		if (instance == null) {
			instance = new PeerManager();
		}
		return instance;
	}

	/**
	 * Retrive datas from Map, create Peer object and add it to peerManager peer
	 * array.
	 * 
	 * @param Map    <String, Map> peer info
	 * 
	 * @param String peer name
	 */
	public String createPeer(Map<String, Map<String, String>> peerData, String name) {
		String privateKey = peerData.get("Interface").get("PrivateKey");
		String address = peerData.get("Interface").get("Address");
		String dns = peerData.get("Interface").get("DNS");
		String mtu = peerData.get("Interface").get("MTU");
		String publicKey = peerData.get("Peer").get("PublicKey");
		String presharedKey = peerData.get("Peer").get("PresharedKey");
		String endpoint = peerData.get("Peer").get("Endpoint");
		String allowedIPs = peerData.get("Peer").get("AllowedIPs");

		Peer p = new Peer(privateKey, address, dns, mtu, publicKey, presharedKey, endpoint, allowedIPs, name);
		peers.add(p);
		return p.getId();
	}

	/**
	 * Removes a peer.
	 * 
	 * @param id The ID of the peer to be removed.
	 */
	public boolean removePeer(String id) {
		if (id != null && !id.isEmpty()) {
			return peers.removeIf(p -> id.equals(p.getId()));
		}
		return false;
	}

	/**
	 * Finds and returns a peer by ID.
	 * 
	 * @param id The ID of the peer to find.
	 * 
	 * @return Peer The peer with the specified ID, or null if not found.
	 */
	public Peer getPeerById(String id) {
		if (id != null && !id.isEmpty()) {
			for (Peer p : peers) {
				// System.out.println(p.getId());
				if (id.equals(p.getId())) {
					return p;
				}
			}

		}
		return null;
	}

	/**
	 * Returns the peers.
	 * 
	 * @return Peer[] An array of all peers.
	 */
	public Peer[] getPeers() {
		Peer[] p = this.peers.toArray(new Peer[peers.size()]);
		return p;
	}

	/**
	 * Parses a configuration string into a map containing sections and their
	 * key-value pairs.
	 *
	 * @param config the configuration string to parse.
	 * @return a map where each key is the name of a section (e.g., "Interface",
	 *         "Peer") and the value is another map containing key-value pairs
	 *         within that section.
	 */
	public static Map<String, Map<String, String>> parsePeerConfig(String config) {
		Map<String, Map<String, String>> configSections = new HashMap<>();

		String[] sections = config.split("\\[");
		for (String section : sections) {
			if (section.trim().isEmpty()) {
				continue;
			}

			int endOfSectionName = section.indexOf("]");
			String sectionName = section.substring(0, endOfSectionName).trim();

			String sectionBody = section.substring(endOfSectionName + 1).trim();

			Map<String, String> sectionParams = new HashMap<>();
			String[] lines = sectionBody.split("\n");
			for (String line : lines) {
				line = line.trim();
				if (line.isEmpty() || line.startsWith("#")) {
					continue;
				}

				String[] keyValue = line.split("=", 2);
				if (keyValue.length == 2) {
					sectionParams.put(keyValue[0].trim(), keyValue[1].trim());
				}
			}

			configSections.put(sectionName, sectionParams);
		}

		return configSections;
	}
}
