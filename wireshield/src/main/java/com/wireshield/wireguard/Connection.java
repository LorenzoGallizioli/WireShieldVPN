package com.wireshield.wireguard;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.parser.ParseException;
import com.wireshield.av.FileManager;
import com.wireshield.enums.connectionStates;

/**
 * The Connection class represents a WireGuard connection.
 */
public class Connection {
    private static final Logger logger = LogManager.getLogger(Connection.class);

    private static Connection instance;
    private connectionStates status;
    private long sentTraffic;
    private long receivedTraffic;
    private long lastHandshakeTime;
    private String activeInterface;
    private String wgPath;

    private Connection() {
    	wgPath = FileManager.getProjectFolder() + FileManager.getConfigValue("WGEXE_STD_PATH");
    	status = connectionStates.DISCONNECTED;
        sentTraffic = 0;
        receivedTraffic = 0;
        lastHandshakeTime = 0;
        activeInterface = "";
    }
    
    /**
     * Public method to get the Singleton instance.
     * 
     * @return the single instance of Connection.
     * @throws ParseException 
     * @throws IOException 
     */
    public static synchronized Connection getInstance() {
        if (instance == null) {
            instance = new Connection();
        }
        return instance;
    }
    
    /**
     * Updates the traffic of the connection.
     * 
     * @param sentTraffic
     *   The traffic sent in bytes.
     * @param receivedTraffic
     *   The traffic received in bytes.
     */
    public Long[] getTraffic() {
    	this.updateTraffic();
    	
    	Long[] traffic = new Long[2];
    	
    	traffic[0] = this.sentTraffic;
    	traffic[1] = this.receivedTraffic;
    	
    	return traffic;
    }
    
    /**
     * Retrieves the traffic sent and received.
     * 
     * @return Long[]
     *   The traffic sent and received in bytes.
     */
    public void updateTraffic() {
        String trafficString = wgShow("transfer");
        
        if(trafficString != null) {
	        this.sentTraffic = Long.parseLong(trafficString.trim().split("\\s+")[0]);
	        this.receivedTraffic = Long.parseLong(trafficString.trim().split("\\s+")[1]);
        } else {
        	this.sentTraffic = 0;
        	this.receivedTraffic = 0;
        }
    }
    
    /**
     * Execute the wg show command for retrieving informations about the connection.
     * 
     * @param param
     *   [public-key | private-key | listen-port | fwmark | peers | preshared-keys | endpoints | allowed-ips | latest-handshakes | transfer | persistent-keepalive | dump]
     * @return
     */
    protected String wgShow(String param) {
        activeInterface = this.getActiveInterface();
        if (activeInterface == null || param == null) return null;
        
        try {
        	ProcessBuilder processBuilder = new ProcessBuilder(wgPath, "show", activeInterface, param);
        	Process process = processBuilder.start();

        	BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        	String line;
        	while ((line = reader.readLine()) != null) {
        		return line.split("=")[1].trim();
        	}
        	return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Update the active interface variable.
     */
    protected void updateActiveInterface() {
    	
    	Process process = null;
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(wgPath, "show", "interfaces");
            process = processBuilder.start();

            // Read the output of the command
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
                  
            while ((line = reader.readLine()) != null) {
                if (!line.isEmpty()) {
                    this.activeInterface = line; // Get only the first wg interface up and exit
                    return;
                }
            }
            this.activeInterface = null; // Interface is down
            
        } catch (IOException e) {
            logger.error("Error getting active interface: " + e.getMessage());  
            this.activeInterface = null;
            
        } finally {
        	if (process != null) {
                process.destroy();
                
            }
        	
        }

    }
    
    /**
     * Retrive the active interface.
     * 
     * @return String|null
     *   The name of the interface or null if not found.
     */
    protected String getActiveInterface() {
    	this.updateActiveInterface();
    	return this.activeInterface;
    }
    
    /**
     * Retrieves the status of the connection.
     * 
     * @return connectionStates
     *   The status.
     */
    public connectionStates getStatus() {
        return this.status;
    }
    
    /**
     * Sets the connection status.
     * 
     * @param status
     *   The status.
     */
    public void setStatus(connectionStates status) {
        this.status = status;
    }
    
    /**
     * Updates the last handshake time variable.
     */
    public void updateLastHandshakeTime() {
        String latestHandShake = wgShow("latest-handshakes"); 
        if (latestHandShake != null) this.lastHandshakeTime = Long.parseLong(latestHandShake);
    }
    
    /**
     * Retrieves the last handshake time.
     * 
     * @return Long
     *   The last handshake time.
     */
    public Long getLastHandshakeTime() {
    	this.updateLastHandshakeTime();
    	return this.lastHandshakeTime;
    }

    /**
     * Describes the connection.
     */
    @Override
    public String toString() {
        return String.format(
            "[INFO] Interface: %s%n[INFO] Status: %s%n[INFO] Last handshake time: %s%n[INFO] Received traffic: %s%n[INFO] Sent traffic: %s",
            this.activeInterface,
            this.status,
            this.lastHandshakeTime,
            this.receivedTraffic,
            this.sentTraffic);
    }
    

	protected void setSentTraffic(long sentTraffic) {
		this.sentTraffic = sentTraffic;
	}

	protected void setReceivedTraffic(long receivedTraffic) {
		this.receivedTraffic = receivedTraffic;
	}

	protected void setLastHandshakeTime(long lastHandshakeTime) {
		this.lastHandshakeTime = lastHandshakeTime;
	}

	protected void setActiveInterface(String activeInterface) {
		this.activeInterface = activeInterface;
	}

}
