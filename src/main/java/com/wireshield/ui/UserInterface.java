package com.wireshield.ui;
import com.wireshield.av.FileManager;
import com.wireshield.enums.connectionStates;
import com.wireshield.enums.runningStates;
import com.wireshield.enums.vpnOperations;
import com.wireshield.localfileutils.SystemOrchestrator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * The UserInterface class represents the user interface for the WireShield application.
 */
public class UserInterface extends Application {

    private static SystemOrchestrator so;
    
    /**
     * JavaFX Buttons.
     */
    @FXML
    private Button vpnButton, uploadPeerButton;
    
    /**
     * JavaFX AnchorPanes.
     */
    @FXML
    private AnchorPane homePane, logsPane, avPane, settingsPane;

    /**
     * JavaFX TextAreas.
     */
    @FXML
    private TextArea logsArea, avStatusArea, avFilesArea;
    
    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MainView.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
            primaryStage.setTitle("Wireshield");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // Metodo main richiesto per il punto di ingresso.
    public static void main(String[] args) {
        so = SystemOrchestrator.getInstance();
        so.manageVPN(vpnOperations.STOP); // Per sicurezza, stoppa qualsiasi VPN wireguard prima di iniziare.
        launch(args); // Metodo di avvio JavaFX.
    }
    
    /**
     * Changes the state of the VPN.
     */
    @FXML
    public void changeVPNState(){ 
        if (so.getConnectionStatus() == connectionStates.CONNECTED){
            so.manageDownload(runningStates.DOWN);
            so.manageAV(runningStates.DOWN);
            so.manageVPN(vpnOperations.STOP);
            vpnButton.setText("Start VPN");
            uploadPeerButton.setDisable(false);
        }
        else{
            so.manageVPN(vpnOperations.START);
            so.manageAV(runningStates.UP);
            so.manageDownload(runningStates.UP);
            vpnButton.setText("Stop VPN");
            uploadPeerButton.setDisable(true);
        }
    }

    /**
     * Displays the home page.
     */
    @FXML
    public void viewHome(){
        homePane.toFront();
    }

    /**
     * Displays the logs page.
     */
    @FXML
    public void viewLogs(){
        logsPane.toFront();
        if (so.getConnectionStatus() == connectionStates.DISCONNECTED){
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
    public void viewAv(){
        runningStates avStatus = so.getAVStatus();
        avStatusArea.setText(avStatus + "\n");
        if (avStatus == runningStates.UP){
            avFilesArea.setText(so.getAntivirusManager().getFinalReports() + "\n");
        }
        avPane.toFront();
    }

    /**
     * Displays the settings page.
     */
    @FXML
    public void viewSettings(){
        settingsPane.toFront();
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
        
        // Mostra la finestra di selezione file
        Stage stage = new Stage();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            try {
                // Percorso di destinazione
                Path targetPath = Path.of(defaultPeerPath, selectedFile.getName());

                // Copia il file nella cartella di destinazione
                Files.createDirectories(targetPath.getParent()); // Assicura che la cartella esista
                Files.copy(selectedFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);

                System.out.println("File copied to: " + targetPath.toAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Failed to copy the file.");
            }
        } else {
            System.out.println("No file selected.");
        }
    }

    /*
     * Imports a peer.
     */
    public void importPeer(){}

    /*
     * Shows the available peers.
     */
    public void showAvailablePeers(){}
}
