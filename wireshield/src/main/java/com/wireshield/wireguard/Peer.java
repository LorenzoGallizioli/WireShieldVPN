package com.wireshield.wireguard;

import java.util.UUID;

/**
 * The Peer class is a representation of a WireGuard peer.
 */
public class Peer {
    private String privateKey;
    private String address;
    private String dns;
    private String mtu;
    private String publicKey;
    private String presharedKey;
    private String endPoint;
    private String allowedIPs;
    private String name;
    private String id;

    /**
     * The constructor of the Peer class.
     * @param PrivateKey
     * @param Address
     * @param DNS 
     * @param MTU
     * @param PublicKey
     * @param PresharedKey
     * @param Endpoint
     * @param AllowedIPs
     * @param name
     * 
     */
    public Peer(String privateKey, String address, String dns, String mtu, String publicKey, String presharedKey, String endpoint, String allowedIPs, String name) {
    	this.privateKey = privateKey;
    	this.address = address;
    	this.dns = dns;
    	this.mtu = mtu;
    	this.publicKey = publicKey;
    	this.presharedKey = presharedKey;
    	this.endPoint = endpoint;
    	this.allowedIPs = allowedIPs;
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
        return this.id;
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
        return dns;
    }

    /**
     * Retrieves the MTU.
     * 
     * @return the MTU as a String.
     */
    public String getMTU() {
        return mtu;
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
    private static String generateUniqueId() {
        return UUID.randomUUID().toString();
    }
    
    @Override
    public String toString() {
        return String.format(
            "[INFO] ID: %s%n" +
            "[INFO] Name: %s%n" +
            "[INFO] Private Key: %s%n" +
            "[INFO] Address: %s%n" +
            "[INFO] DNS: %s%n" +
            "[INFO] MTU: %s%n" +
            "[INFO] Public Key: %s%n" +
            "[INFO] Preshared Key: %s%n" +
            "[INFO] Endpoint: %s%n" +
            "[INFO] Allowed IPs: %s",
            id,
            name,
            privateKey,
            address,
            dns,
            mtu,
            publicKey,
            presharedKey,
            endPoint,
            allowedIPs
        );
    }


}
