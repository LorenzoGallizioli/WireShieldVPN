package com.wireshield.wireguard;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.parser.ParseException;

import java.io.IOException;

import com.wireshield.av.FileManager;
import com.wireshield.enums.connectionStates;
import com.wireshield.enums.vpnOperations;

/**
 * The WireguardManager class is responsible for managing the wireguard VPN.
 */
public class WireguardManager {
    private static final Logger logger = LogManager.getLogger(WireguardManager.class);

    private static WireguardManager instance;
    private String wireguardPath;
    private String defaultPeerPath;
    private String logDumpPath;
    private Connection connection;
    private PeerManager peerManager;
    private String logs;

    private WireguardManager() {
    	this.wireguardPath = FileManager.getProjectFolder() + FileManager.getConfigValue("WIREGUARDEXE_STD_PATH");
        this.defaultPeerPath = FileManager.getProjectFolder() + FileManager.getConfigValue("PEER_STD_PATH");
        this.logDumpPath = FileManager.getProjectFolder() + FileManager.getConfigValue("LOGDUMP_STD_PATH");
    	
        File file = new File(wireguardPath);
        if (!file.exists() || !file.isFile()) {
            logger.error("WireGuard executable not found");
            return;
        }
        this.connection = Connection.getInstance();
        this.peerManager = PeerManager.getInstance();
        
        //this.startUpdateWireguardLogs(); // Start log update thread
    }
    
    /**
     * Public static method to get the Singleton instance of WireguardManager.
     * If the instance does not exist, it will be created with the provided wgPath.
     *
     * @param wgPath the path to the WireGuard executable.
     * @return the single instance of WireguardManager.
     * @throws ParseException 
     * @throws IOException 
     */
    public static synchronized WireguardManager getInstance() {
        if (instance == null) {
            instance = new WireguardManager();
        }
        return instance;
    }

    /**
     * Starts the wireguard inteface based on the configuration path.
     * 
     * @param configPath
     *   The configuration file path (Name).(extension) .
     * 
     * @return True if the interface is correctly up, false overwise.
     */
    public Boolean setInterfaceUp(String configPath) {
        String activeInterface = connection.getActiveInterface();
        if(activeInterface != null) {
            logger.error("WireGuard interface is already up.");
            return false; // Interface is up
        }

        try {
            // Command for wireguard interface start.
            ProcessBuilder processBuilder = new ProcessBuilder(
                wireguardPath, 
                "/installtunnelservice", 
                defaultPeerPath + configPath
            );
            
            System.out.println(wireguardPath + " /installtunnelservice " + defaultPeerPath + configPath);
            Process process = processBuilder.start();

            // Reads the output.
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                logger.info(line);
            }

            // Checks the exit code of the process.
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                logger.info("WireGuard interface started.");
                connection.setStatus(connectionStates.CONNECTED);
                return true;
            } else {
                logger.error("Error starting WireGuard interface.");
                return false;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Stops the active wireguard interface.
     * 
     * @return True if the interface has been stopped correctly, false overwise.
     */
    public Boolean setInterfaceDown() {
        String interfaceName = connection.getActiveInterface();

        if(interfaceName == null) {
            logger.error("No active WireGuard interface.");
            return false;
        }

        try {
            // Command for wireguard interface stop.
            ProcessBuilder processBuilder = new ProcessBuilder(
                wireguardPath, 
                "/uninstalltunnelservice", 
                interfaceName
            );
            Process process = processBuilder.start();

            // Reads the output.
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                logger.info(line);
            }

            // Checks the exit code of the process.
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                logger.info("WireGuard interface stopped.");
                connection.setStatus(connectionStates.DISCONNECTED);
                return true;
            } else {
                logger.error("Error stopping WireGuard interface.");
                return false;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Updates connection statistics in a synchronized manner.
     * <p>
     * This method waits until the active interface of the connection is available, then
     * performs the following updates:
     * <ul>
     *   <li>Updates the active interface.</li>
     *   <li>Updates traffic statistics.</li>
     *   <li>Updates the last handshake time.</li>
     * </ul>
     * </p>
     */
    private synchronized void updateConnectionStats() {
    	
    	// Wait that while the interface is actually up
    	while(connection.getActiveInterface() == null) {}
    	
    	// Update active interface
    	connection.updateActiveInterface();
    	
    	// Update traffic
    	connection.updateTraffic();
    	
    	// Update last hand-shake
    	connection.updateLastHandshakeTime();
    	
    }
    
    /**
     * Starts a thread to continuously update connection statistics.
     * <p>
     * The method runs a background task that repeatedly calls {@link #updateConnectionStats()}
     * as long as the connection status is {@code CONNECTED}. After each update, it logs the current
     * state of the connection and sleeps for 1 second before the next iteration. If the thread is
     * interrupted, it stops and logs an error.
     * </p>
     */
    public void startUpdateConnectionStats() {
    	Runnable task = () -> {
            while (connection.getStatus() == connectionStates.CONNECTED) { // Check interface is up
            	updateConnectionStats();
            	logger.info(connection.toString());
                try {
                    Thread.sleep(1000); // wait
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.error("updateConnectionStats() thread unexpecly interrupted");
                    break;
                }
            }
            logger.info("updateConnectionStats() thread stopped");
        };

        Thread thread = new Thread(task);
        thread.start();
    }
    
    /**
     * Starts a thread to continuously update WireGuard logs.
     * <p>
     * This method executes a background task that periodically dumps WireGuard logs
     * to a specified file using a {@link ProcessBuilder}. The logs are then read into
     * memory and stored. The task runs indefinitely, with a 1-second sleep between iterations.
     * If the thread is interrupted, it stops and logs an error.
     * </p>
     */
    public void startUpdateWireguardLogs() {
    	Runnable task = () -> {
    		while(true) {
    			try {
    				ProcessBuilder processBuilder = new ProcessBuilder();
    				processBuilder.command(wireguardPath, "/dumplog > " + this.logDumpPath);
    				processBuilder.redirectErrorStream(true);
                	processBuilder.start();
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    			
    			String logDump = FileManager.readFile(this.logDumpPath);
    			this.logs = logDump;
    		
    			try {
    				Thread.sleep(1000); // wait
    			} catch (InterruptedException e) {
    				Thread.currentThread().interrupt();
    				logger.error("startUpdateWireguardLogs() thread unexpecly interrupted");
    				break;
    			}
    		}
            // Code
        };

        Thread thread = new Thread(task);
        thread.start();
    }

    /**
     * Returns the connection.
     * 
     * @return Connection
     *   The connection.
     */
    public Connection getConnection() {
        return this.connection;
    }

    /**
     * Returns the peer manager.
     * 
     * @return PeerManager
     *   The peer manager.
     */
    public PeerManager getPeerManager() {
        return this.peerManager;
    }
    
    /**
     * Returns wg logs.
     * 
     * @return String
     *   wireguard logs.
     */
    public String getLog() {
    	return this.logs;
    }
}
