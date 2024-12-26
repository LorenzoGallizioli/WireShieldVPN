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

/**
 * The WireguardManager class is responsible for managing the wireguard VPN.
 */
public class WireguardManager {
    private static final Logger logger = LogManager.getLogger(WireguardManager.class);

    private static WireguardManager instance;
    private String wireguardPath;
    private String defaultPeerPath;
    private Connection connection;
    private PeerManager peerManager;

    private WireguardManager() {
    	this.wireguardPath = FileManager.getProjectFolder() + FileManager.getConfigValue("WIREGUARDEXE_STD_PATH");
        this.defaultPeerPath = FileManager.getProjectFolder() + FileManager.getConfigValue("PEER_STD_PATH");
    	
        File file = new File(wireguardPath);
        if (!file.exists() || !file.isFile()) {
            logger.error("WireGuard executable not found");
            return;
        }
        this.connection = Connection.getInstance();
        this.peerManager = PeerManager.getInstance();
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
     * Returns the connection.
     * 
     * @return Connection
     *   The connection.
     */
    public connectionStates getConnectionStatus() {
        return connection.getStatus();
    }

    /**
     * Returns the connection logs.
     * 
     * @return String
     *   The connection logs. 
     */
    public String getConnectionLogs(){
        return connection.toString();
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
}
