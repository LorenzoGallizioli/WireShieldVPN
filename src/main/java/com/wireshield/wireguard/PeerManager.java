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
    public void addPeer(Peer peer) {
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
    

}
