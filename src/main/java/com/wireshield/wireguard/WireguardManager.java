package com.wireshield.wireguard;

/**
 * The WireGuardManager class is responsible for managing WireGuard.
 */
public class WireguardManager {
    private Connection connection;
    private PeerManager peerManager;

    /**
     * The constructor for WireGuardManager.
     */
    public WireguardManager() {}

    /**
     * Sets the interface up.
     * 
     * @param peerId
     *   The peer id.
     * 
     * @return Boolean
     *   True if the interface was set up, false otherwise.
     */
    public Boolean setInterfaceUp(String peerId){
        return true;
    }

    /**
     * Sets the interface down.
     * 
     * @return Boolean
     *   True if the interface was set down, false otherwise. 
     */
    public Boolean setInterfaceDown(){
        return true;
    }

    /**
     * Returns the peer manager.
     * 
     * @return PeerManager
     *   The peer manager.
     */
    public PeerManager getPeerManager() {
        return peerManager;
    }

    /**
     * Returns the connection.
     * 
     * @return Connection
     *   The connection.
     */
    public Connection getConnection() {
        return connection;
    }
}
