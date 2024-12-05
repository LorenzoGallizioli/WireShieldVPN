package com.wireshield.wireguard;

/**
 * The Peer class is a representation of a WireGuard peer.
 */
public class Peer {
    private String endPoint;
    private String publicKeyPath;
    private String presharedKeyPath;
    private String allowedIps;
    private String name;
    private String id;

    /**
     * The constructor of the Peer class.
     */
    public Peer() {}

    /**
     * Returns the name of the peer.
     * 
     * @return String
     *   The name of the peer.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the ID of the peer.
     * 
     * @return String
     *   The ID of the peer.
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the endpoint of the peer.
     * 
     * @return String
     *   The endpoint of the peer. 
     */
    public String getEndPoint() {
        return endPoint;
    }

    /**
     * Returns the public key path of the peer.
     * 
     * @return String
     *   The public key path of the peer.
     */
    public String getPublicKeyPath() {
        return publicKeyPath;
    }

    /**
     * Returns the preshared key path of the peer.
     * 
     * @return String
     *   The preshared key path of the peer.
     */
    public String getPresharedKeyPath() {
        return presharedKeyPath;
    }

    /**
     * Returns the allowed IP addresses of the peer.
     * 
     * @return String
     *   The allowed IP addresses of the peer.
     *   
     */
    public String getAllowedIps() {
        return allowedIps;
    }
}
