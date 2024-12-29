package com.wireshield.ui;
import com.wireshield.av.FileManager;
import com.wireshield.av.ScanReport;
import com.wireshield.enums.connectionStates;
import com.wireshield.enums.runningStates;
import com.wireshield.enums.vpnOperations;
import com.wireshield.localfileutils.SystemOrchestrator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javafx.application.Application;
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

public class UserInterface extends Application {

    private static final Logger logger = LogManager.getLogger(SystemOrchestrator.class);
    protected static SystemOrchestrator so;

    /**
     * JavaFX Buttons.
     */
    @FXML
    protected Button vpnButton, uploadPeerButton;

    /**
     * JavaFX Labels.
     */
    @FXML
    protected Label avStatusLabel;
    
    /**
     * JavaFX AnchorPanes.
     */
    @FXML
    protected AnchorPane homePane, logsPane, avPane, settingsPane;

    /**
     * JavaFX TextAreas.
     */
    @FXML
    protected TextArea logsArea, avFilesArea;

    /**
     * JavaFX HBox Buttons.
     */
    @FXML
    protected Button minimizeButton, closeButton;

    /**
     * JavaFX ListViews.
     */
    @FXML
    protected ListView<String> peerListView;
    protected ObservableList<String> peerList = FXCollections.observableArrayList();
    @FXML
    protected ListView<String> avFilesListView;
    protected ObservableList<String> avFilesList = FXCollections.observableArrayList();

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
        if (peerListView != null) {
            peerListView.setItems(peerList);
        }
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
        so.manageVPN(vpnOperations.STOP);
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
    }

    /**
     * Changes the state of the VPN.
     */
    @FXML
    public void changeVPNState() { 
        if (so.getConnectionStatus() == connectionStates.CONNECTED) {
            so.manageDownload(runningStates.DOWN);
            so.manageAV(runningStates.DOWN);
            so.manageVPN(vpnOperations.STOP);
            vpnButton.setText("Start VPN");
            uploadPeerButton.setDisable(false);
            logger.info("All services are stopped.");
        } else {
            so.manageVPN(vpnOperations.START);
            so.manageAV(runningStates.UP);
            so.manageDownload(runningStates.UP);
            vpnButton.setText("Stop VPN");
            uploadPeerButton.setDisable(true);
            logger.info("All services started successfully.");
        }
    }

    @FXML
    public void viewHome() {
        homePane.toFront();
        if (!checkFilesInDirectory()) {
            vpnButton.setDisable(true);
        } else {
            vpnButton.setDisable(false);
        }
    }

    @FXML
    public void viewLogs() {
        logsPane.toFront();
        if (so.getConnectionStatus() == connectionStates.DISCONNECTED) {
            logsArea.setText("No connection.\n");
            return;
        }
        String logs = so.getWireguardManager().getConnectionLogs();
        logsArea.setText(logs + "\n");
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
     * Displays the settings page.
     */
    @FXML
    public void viewSettings() {
        settingsPane.toFront();
        updatePeerList();
    }

    /**
     * Checks if there are any files in the peer directory.
     * 
     * @return True if there are files in the directory, false otherwise.
     */
    protected boolean checkFilesInDirectory() {
        String folderPath = FileManager.getProjectFolder() + FileManager.getConfigValue("PEER_STD_PATH");
        File directory = new File(folderPath);

        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.length() > 0) {
                        return true;
                    }
                }
            }
        }
        return false;
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
                logger.debug("File copied to: " + targetPath.toAbsolutePath());
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
                peerList.clear(); // Svuota la lista attuale
                for (File file : files) {
                    if (file.isFile() && file.length() > 0) {
                        peerList.add(file.getName()); // Aggiungi il nome del file alla lista
                        logger.debug("File added to peer list: " + file.getName());
                        logger.info("Peer list updated.");
                    }
                }
            }
        }
    }
}
