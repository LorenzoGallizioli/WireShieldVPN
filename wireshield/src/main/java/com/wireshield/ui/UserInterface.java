package com.wireshield.ui;
import com.wireshield.av.FileManager;
import com.wireshield.av.ScanReport;
import com.wireshield.enums.connectionStates;
import com.wireshield.enums.runningStates;
import com.wireshield.enums.vpnOperations;
import com.wireshield.localfileutils.SystemOrchestrator;
import com.wireshield.wireguard.WireguardManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;

/**
 * The UserInterface class controls the UI of the application using JavaFX.
 */
public class UserInterface extends Application {

    private static final Logger logger = LogManager.getLogger(UserInterface.class);
    protected static SystemOrchestrator so;
    protected static WireguardManager wg;

    /**
     * JavaFX Buttons.
     */
    @FXML
    protected Button vpnButton;

    /**
     * JavaFX Labels.
     */
    @FXML
    protected Label avStatusLabel, connLabel;
    
    /**
     * JavaFX AnchorPanes.
     */
    @FXML
    protected AnchorPane homePane; 
    @FXML
    protected AnchorPane logsPane;
    @FXML
    protected AnchorPane avPane;

    /**
     * JavaFX TextAreas.
     */
    @FXML
    protected TextArea logsArea;
    @FXML
    protected TextArea avFilesArea;

    /**
     * JavaFX HBox Buttons.
     */
    @FXML
    protected Button minimizeButton;
    @FXML
    protected Button closeButton;

    /**
     * JavaFX ListViews.
     */
    @FXML
    protected ListView<String> peerListView;
    protected ObservableList<String> peerList = FXCollections.observableArrayList();
    @FXML
    protected ListView<String> avFilesListView;
    protected ObservableList<String> avFilesList = FXCollections.observableArrayList();
    protected String selectedPeerFile; // Memorize the selected peer file.
    private static double xOffset = 0;
    private static double yOffset = 0;

    /**
     * Start the application.
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MainView.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
            primaryStage.initStyle(StageStyle.UNDECORATED);
            primaryStage.setTitle("Wireshield");
            primaryStage.setScene(scene);
            primaryStage.show();
            // Aggiungi eventi per trascinare la finestra
            root.setOnMousePressed(event -> {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            });
            root.setOnMouseDragged(event -> {
                primaryStage.setX(event.getScreenX() - xOffset);
                primaryStage.setY(event.getScreenY() - yOffset);
            });
            logger.info("Main view loaded successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Failed to load the main view.");
        }
    }

    /**
     * Initialize the user interface.
     */
    @FXML
    public void initialize() {
        viewHome();
        updatePeerList();
        startDynamicConnectionLogsUpdate();
        startDynamicLogUpdate();
        // Set the disable property of the peerListView based on the connection status.
        peerListView.setDisable(so.getConnectionStatus() == connectionStates.CONNECTED);

        // Set the disable property of the peerListView based on the connection status.
        if (vpnButton.getText().equals("Start VPN")) {
            vpnButton.setDisable(true);
        }

        peerListView.setItems(peerList);
        peerListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selectedPeerFile = newValue;
                if (vpnButton.getText().equals("Start VPN")) {
                    vpnButton.setDisable(false); // Enable the button when the text is "Start VPN" and a file is selected.
                }
                logger.info("File selezionato nella peer list: {}", selectedPeerFile);
            } else {
                if (vpnButton.getText().equals("Start VPN")) {
                    vpnButton.setDisable(true); // Disable the button when the text is "Start VPN" and no file is selected.
                }
                logger.info("Nessun file selezionato.");
            }
        });
    
    
        if (avFilesListView != null) {
            avFilesListView.setItems(avFilesList);
        }
    }
    

    /**
     * Main method to launch the WireShield application.
     * 
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        so = SystemOrchestrator.getInstance();
        so.manageVPN(vpnOperations.STOP,null);
        wg = so.getWireguardManager();
        launch(args);
    }

    /**
     * Minimizes the application window.
     */
    @FXML
    public void minimizeWindow() {
        Stage stage = (Stage) minimizeButton.getScene().getWindow();
        stage.setIconified(true);
        logger.info("Window minimized.");
    }

    /**
     * Closes the application window.
     */
    @FXML
    public void closeWindow() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
        so.manageDownload(runningStates.DOWN);
        so.manageAV(runningStates.DOWN);
        so.manageVPN(vpnOperations.STOP, null);
        System.exit(0);
    }

    /**
     * Changes the state of the VPN.
     */
    @FXML
    public void changeVPNState() { 

        if (so.getConnectionStatus() == connectionStates.CONNECTED) {
        	
        	// Stop execution componentStatesGuardian thread
        	so.setGuardianState(runningStates.DOWN);
        	
            so.manageDownload(runningStates.DOWN);
            so.manageAV(runningStates.DOWN);
            so.manageVPN(vpnOperations.STOP, null);
            vpnButton.setText("Start VPN");
            peerListView.setDisable(false); // Enable the selection of peers.
            if(selectedPeerFile == null) {
                vpnButton.setDisable(true);
            }
            logger.info("All services are stopped.");
        } else {
            so.manageVPN(vpnOperations.START, selectedPeerFile);
            so.manageAV(runningStates.UP);
            so.manageDownload(runningStates.UP);
            
            // Start execution componentStatesGuardian thread
            so.statesGuardian();
            
            vpnButton.setText("Stop VPN");
            peerListView.setDisable(true); // Disable the selection of peers.
            logger.info("All services started successfully.");
        }
    }
   
    /**
     * Displays the home page.
     */
    @FXML
    public void viewHome() {
        homePane.toFront();
        updatePeerList();
    }

    /**
     * Displays the logs page.
     */
    @FXML
    public void viewLogs() {
        logsPane.toFront();
        logger.info("Viewing logs...");
    }

    /**
     * Displays the antivirus page.
     */
    @FXML
    public void viewAv() {
        runningStates avStatus = so.getAVStatus();
        avStatusLabel.setText(avStatus.toString());
        if (avStatus == runningStates.UP) {
            avFilesList.clear();
            List<ScanReport> reports = so.getAntivirusManager().getFinalReports();
            for (ScanReport report : reports) {
                String fileName = report.getFile().getName();
                String warningClass = report.getWarningClass().toString();
                avFilesList.add(fileName + " - " + warningClass);
            }
        }
        avPane.toFront();
    }

    /**
     * Handles the file selection event and copies the selected file to the peer directory.
     * 
     * @param event 
     *   The action event triggered when a file is selected.
     */
    @FXML
    public void handleFileSelection(ActionEvent event) {
        String defaultPeerPath = FileManager.getProjectFolder() + FileManager.getConfigValue("PEER_STD_PATH");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select a File");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("WireGuard Config Files (*.conf)", "*.conf")
        );

        Stage stage = new Stage();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            try {
                Path targetPath = Path.of(defaultPeerPath, selectedFile.getName());
                Files.createDirectories(targetPath.getParent());
                Files.copy(selectedFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
                logger.debug("File copied to: {}", targetPath.toAbsolutePath());
                updatePeerList();
                logger.info("File copied successfully.");
            } catch (IOException e) {
                e.printStackTrace();
                logger.error("Failed to copy the file.");
            }
        } else {
            logger.info("No file selected.");
        }
    }

    /**
     * Updates the peer list based on the files in the peer directory.
     */
    protected void updatePeerList() {
        String folderPath = FileManager.getProjectFolder() + FileManager.getConfigValue("PEER_STD_PATH");
        File directory = new File(folderPath);

        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                peerList.clear(); // Empty the peer list.
                for (File file : files) {
                    if (file.isFile() && file.length() > 0) {
                        peerList.add(file.getName()); // Add the file name to the peer list.
                        logger.debug("File added to peer list: {}", file.getName());
                        logger.info("Peer list updated.");
                    }
                }
            }
        }
    }

    /**
     * Starts a thread that dynamically updates the logs area.
     */
    protected void startDynamicLogUpdate() {
        Runnable task = () -> {
            while (true) {
                try {
                    // Recupera i log aggiornati
                    String logs = wg.getLog();
                    // Aggiorna logsArea sul thread JavaFX
                    Platform.runLater(() -> {
                        double scrollPosition = logsArea.getScrollTop();
                        logsArea.clear();
                        logsArea.setText(logs);
                        logsArea.setScrollTop(scrollPosition);
                    });
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.error("Dynamic log update thread interrupted.");
                    break;
                } catch (Exception e) {
                    logger.error("Error updating logs dynamically: ", e);
                }
            }
        };

        Thread Thread = new Thread(task);
        Thread.start();
    }

     /**
     * Starts a thread that dynamically updates the logs area.
     */
    protected void startDynamicConnectionLogsUpdate() {
        Runnable task = () -> {
            while (true) {
                try {
                    // Retrieve updated logs.
                    String logs = wg.getConnectionLogs();
                    // Update logsArea on JavaFX thread.
                    Platform.runLater(() -> {
                        connLabel.setText("");;
                        connLabel.setText(logs);
                    });
                    Thread.sleep(1000); // Wait for 1 second before the next update.
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.error("Dynamic connection logs update thread interrupted.");
                    break;
                } catch (Exception e) {
                    logger.error("Error updating connection logs: ", e);
                }
            }
        };

        Thread logUpdateThread = new Thread(task);
        logUpdateThread.setDaemon(true); // Assicura che il thread si fermi con l'applicazione
        logUpdateThread.start();
    }

}
