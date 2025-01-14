package com.wireshield.wireguard;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.parser.ParseException;
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
	private String logDumpPath;
	private Connection connection;
	private PeerManager peerManager;
	private String logs;

	private WireguardManager() {
		this.wireguardPath = FileManager.getProjectFolder() + FileManager.getConfigValue("WIREGUARDEXE_STD_PATH");
		this.defaultPeerPath = FileManager.getProjectFolder() + FileManager.getConfigValue("PEER_STD_PATH");
		this.logDumpPath = FileManager.getProjectFolder() + FileManager.getConfigValue("LOGDUMP_STD_PATH");

		System.out.println(wireguardPath);

		File file = new File(wireguardPath);
		if (!file.exists() || !file.isFile()) {
			logger.error("WireGuard executable not found");
			return;
		}

		if (Connection.getInstance() != null)
			this.connection = Connection.getInstance();
		else
			throw new IllegalStateException("Il costruttore di Connection ha restituito un oggetto null");
		if (PeerManager.getInstance() != null)
			this.peerManager = PeerManager.getInstance();
		else
			throw new IllegalStateException("Il costruttore di PeerManager ha restituito un oggetto null");

		this.startUpdateWireguardLogs(); // Start log update thread
	}

	/**
	 * Public static method to get the Singleton instance of WireguardManager. If
	 * the instance does not exist, it will be created with the provided wgPath.
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
	 * @param configFileName The configuration file name (Name).(extension) .
	 * 
	 * @return True if the interface is correctly up, false overwise.
	 */
	public Boolean setInterfaceUp(String configFileName) {
		String activeInterface = connection.getActiveInterface();
		if (activeInterface != null) {
			logger.warn("WireGuard interface is already up.");
			return false; // Interface is up
		}

		try {
			// Command for wireguard interface start.
			ProcessBuilder processBuilder = new ProcessBuilder(wireguardPath, "/installtunnelservice",
					defaultPeerPath + configFileName);

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
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt(); // Reinterrompe il thread
			logger.error("Thread was interrupted while stopping the WireGuard interface.");
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

		if (interfaceName == null) {
			logger.info("No active WireGuard interface.");
			connection.setStatus(connectionStates.DISCONNECTED);
			return false;
		}

		try {
			// Command for wireguard interface stop.
			ProcessBuilder processBuilder = new ProcessBuilder(wireguardPath, "/uninstalltunnelservice", interfaceName);
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
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt(); // Reinterrompe il thread
			logger.error("Thread was interrupted while stopping the WireGuard interface.");
			return false;
		}
	}

	/**
	 * Updates connection statistics in a synchronized manner.
	 * <p>
	 * This method waits until the active interface of the connection is available,
	 * then performs the following updates:
	 * <ul>
	 * <li>Updates the active interface.</li>
	 * <li>Updates traffic statistics.</li>
	 * <li>Updates the last handshake time.</li>
	 * </ul>
	 * </p>
	 */
	private synchronized void updateConnectionStats() {

		// Wait that while the interface is actually up
		while (connection.getActiveInterface() == null) {
	        // Interfaccia non attiva, controlla di nuovo subito dopo
		}

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
	 * The method runs a background task that repeatedly calls
	 * {@link #updateConnectionStats()} as long as the connection status is
	 * {@code CONNECTED}. After each update, it logs the current state of the
	 * connection and sleeps for 1 second before the next iteration. If the thread
	 * is interrupted, it stops and logs an error.
	 * </p>
	 */
	public void startUpdateConnectionStats() {
		Runnable task = () -> {
			while (connection.getStatus() == connectionStates.CONNECTED) { // Check interface is up
				
				// Update connection stats
				updateConnectionStats();
				
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
	
	private void updateWireguardLogs(String[] command) {
		try {
			File logFile = new File(logDumpPath);
	        if (logFile.exists() && logFile.isFile()) {
	        	
	        	ProcessBuilder processBuilder = new ProcessBuilder(command);
				processBuilder.redirectErrorStream(true);
				
		        try {
		        	Process process = processBuilder.start();
					process.waitFor();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
	        	
	            String logDump = FileManager.readFile(logDumpPath);
	            this.logs = logDump;
	        } else {
	        	logger.error(logDumpPath + " not exits - Creating... ");
	        	if(FileManager.createFile(logDumpPath)) {
	        		logger.info(logDumpPath + " created.");
	        	} else {
	        		logger.error("Error occured during " + logDumpPath + " creation.");	        		
	        	}
	        }
	        
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Starts a thread to continuously update WireGuard logs.
	 * <p>
	 * This method executes a background task that periodically dumps WireGuard logs
	 * to a specified file using a {@link ProcessBuilder}. The logs are then read
	 * into memory and stored. The task runs indefinitely, with a 1-second sleep
	 * between iterations. If the thread is interrupted, it stops and logs an error.
	 * </p>
	 */
	public void startUpdateWireguardLogs() {
		Runnable task = () -> {
			while (true) {
				
				// Update this.logs
				String[] command = {
			            "cmd.exe", "/c", wireguardPath + " /dumplog > " + logDumpPath
			    };
				updateWireguardLogs(command);

				try {
					Thread.sleep(500); // wait
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					logger.error("startUpdateWireguardLogs() thread unexpecly interrupted");
					break;
				}
			}
		};

		Thread thread = new Thread(task);
		thread.start();
	}
	
    /**
     * Returns the connection logs.
     * 
     * @return String
     *   The connection logs. 
     */
    public String getConnectionLogs(){
        connection.updateActiveInterface();
        connection.updateTraffic();
        connection.updateLastHandshakeTime();
        return connection.toString();
    }

	/**
	 * Returns the connection.
	 * 
	 * @return Connection The connection.
	 */
	public connectionStates getConnectionStatus() {
		return connection.getStatus();
	}

	/**
	 * Returns the peer manager.
	 * 
	 * @return PeerManager The peer manager.
	 */
	public PeerManager getPeerManager() {
		return this.peerManager;
	}

	/**
	 * Returns the connection object.
	 * 
	 * @return Connection The connection.
	 */
	protected Connection getConnection() {
		return this.connection;
	}

	/**
	 * Returns reversed wg logs.
	 * 
	 * @return String wireguard logs.
	 */
	public String getLog() {
    	String[] lines = this.logs.split("\n");
    	Collections.reverse(Arrays.asList(lines));
    	this.logs = String.join("\n", lines);
		return this.logs;
	}
}
