package com.wireshield.wireguard;

import java.util.UUID;

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
    public Peer(String endPoint, String publicKeyPath, String presharedKeyPath, String allowedIps, String name) {
    	this.endPoint = endPoint;
    	this.publicKeyPath = publicKeyPath;
    	this.presharedKeyPath = presharedKeyPath;
    	this.allowedIps = allowedIps;
    	this.name = name;
    	this.id = generateUniqueId();
    }

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
    
    /**
     * Generates a unique identifier as a String using the UUID class.
     * This method creates a new universally unique identifier (UUID) and 
     * returns it in its string representation. The generated UUID is of type 4,
     * which is based on random numbers, ensuring a very low probability of collisions.
     *
     * @return a unique identifier as a {@code String}
     * @see java.util.UUID#randomUUID()
     */
    private String generateUniqueId() {
        return UUID.randomUUID().toString();
    }
}
