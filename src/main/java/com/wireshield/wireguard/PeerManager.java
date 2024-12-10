package com.wireshield.wireguard;

import java.util.ArrayList;
import java.util.List;

/**
 * The PeerManager class is responsible for managing WireGuard peers.
 */
public class PeerManager {
    
    private List<Peer> peers;

    /**
     * The constructor for the PeerManager class.
     */
    public PeerManager() {
    	this.peers = new ArrayList<>();
    }

    /**
     * Adds a peer.
     * 
     * @param peer
     *   The peer to be added.
     */
    public void addPeer(Map<String, Map<String, String>> peerData) {
        String PrivateKey = peerData.get("Interface").get("PrivateKey");
        String Address = peerData.get("Interface").get("Address");
        String DNS = peerData.get("Interface").get("DNS");
        String MTU = peerData.get("Interface").get("MTU");
        String PublicKey = peerData.get("Peer").get("PublicKey");
        String PresharedKey = peerData.get("Peer").get("PresharedKey");
        String Endpoint = peerData.get("Peer").get("Endpoint");
        String AllowedIPs = peerData.get("Peer").get("AllowedIPs");
        
        Peer peer = new Peer(PrivateKey, Address, DNS, MTU, PublicKey, PresharedKey, Endpoint, AllowedIPs);
        if (peer != null) {
            peers.add(peer);
        }
    }

    /**
     * Removes a peer.
     * 
     * @param id
     *   The ID of the peer to be removed.
     */
    public void removePeer(String id) {
    	if (id != null) {
            peers.removeIf(peer -> id.equals(peer.getId()));
        }
    }

    /**
     * Finds and returns a peer by ID.
     * 
     * @param id
     *   The ID of the peer to find.
     * 
     * @return Peer
     *   The peer with the specified ID, or null if not found.
     */
    public Peer getPeerById(String id) {
    	if (id != null) {
    		for (Peer peer : peers) {
                if (id.equals(peer.getId())) {
                    return peer;
                }
            }
    	}
        return null;
    }
    
    /**
     * Returns the peers.
     * 
     * @return Peer[]
     *   An array of all peers.
     */
    public Peer[] getPeers() {
        return peers.toArray(new Peer[0]);
    }
    

    public static Map<String, Map<String, String>> parsePeerConfig(String config) {
        // Mappa principale per memorizzare le sezioni [Interface] e [Peer]
        Map<String, Map<String, String>> configSections = new HashMap<>();

        // Suddividiamo la stringa in base alle sezioni (es. [Interface], [Peer])
        String[] sections = config.split("\\[");
        for (String section : sections) {
            if (section.trim().isEmpty()) {
                continue;
            }

            // Otteniamo il nome della sezione (es. "Interface" o "Peer")
            int endOfSectionName = section.indexOf("]");
            String sectionName = section.substring(0, endOfSectionName).trim();

            // Estrarre il contenuto della sezione e rimuovere spazi indesiderati
            String sectionBody = section.substring(endOfSectionName + 1).trim();

            // Parsing delle coppie chiave-valore della sezione
            Map<String, String> sectionParams = new HashMap<>();
            String[] lines = sectionBody.split("\n");
            for (String line : lines) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) { // Ignorare linee vuote o commenti
                    continue;
                }

                // Dividere la riga in chiave e valore usando "="
                String[] keyValue = line.split("=", 2);
                if (keyValue.length == 2) {
                    sectionParams.put(keyValue[0].trim(), keyValue[1].trim());
                }
            }

            // Aggiungiamo la sezione alla mappa principale
            configSections.put(sectionName, sectionParams);
        }

        return configSections;
    }

}
