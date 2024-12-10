package com.wireshield.wireguard;

import java.util.UUID;

/**
 * The Peer class is a representation of a WireGuard peer.
 */
public class Peer {
    private String privateKey;
    private String address;
    private String DNS;
    private String MTU;
    private String publicKey;
    private String presharedKey;
    private String endPoint;
    private String allowedIPs;
    private String name;
    private String id;

    /**
     * The constructor of the Peer class.
     * @param allowedIPs2 
     * @param endpoint2 
     * @param presharedKey 
     */
    public Peer(String PrivateKey, String Address, String DNS, String MTU, String PublicKey, String PresharedKey, String Endpoint, String AllowedIPs, String name) {
    	this.privateKey = PrivateKey;
    	this.address = Address;
    	this.DNS = DNS;
    	this.MTU = MTU;
    	this.publicKey = PublicKey;
    	this.presharedKey = PresharedKey;
    	this.endPoint = Endpoint;
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
     * Returns the endPoint of the peer.
     * 
     * @return String
     *   The endPoint of the peer. 
     */
    public String getEndPoint() {
        return endPoint;
    }

    /**
     * Returns the public key of the peer.
     * 
     * @return String
     *   The public key of the peer.
     */
    public String getPublicKeyPath() {
        return publicKey;
    }

    /**
     * Returns the preshared key of the peer.
     * 
     * @return String
     *   The preshared key of the peer.
     */
    public String getPresharedKeyPath() {
        return presharedKey;
    }

    /**
     * Returns the allowed IP addresses of the peer.
     * 
     * @return String
     *   The allowed IP addresses of the peer.
     *   
     */
    public String getAllowedIps() {
        return allowedIPs;
    }
    
    /**
     * Retrieves the private key.
     * 
     * @return the private key as a String.
     */
    public String getPrivateKey() {
        return privateKey;
    }

    /**
     * Retrieves the address.
     * 
     * @return the address as a String.
     */
    public String getAddress() {
        return address;
    }

    /**
     * Retrieves the DNS.
     * 
     * @return the DNS as a String.
     */
    public String getDNS() {
        return DNS;
    }

    /**
     * Retrieves the MTU.
     * 
     * @return the MTU as a String.
     */
    public String getMTU() {
        return MTU;
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
