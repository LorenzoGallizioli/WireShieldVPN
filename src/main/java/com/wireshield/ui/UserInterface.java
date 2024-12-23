package com.wireshield.ui;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.io.IOException;

import com.wireshield.enums.connectionStates;
import com.wireshield.enums.vpnOperations;
import com.wireshield.localfileutils.SystemOrchestrator;

public class UserInterface extends Application {

    private static SystemOrchestrator so;
    
    @FXML
    private Button vpnButton;

        @Override
        public void start(Stage primaryStage) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("MainView.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root);
                primaryStage.setTitle("Wireshield");
                primaryStage.setScene(scene);
                primaryStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    
        // Metodo main richiesto per il punto di ingresso
    public static void main(String[] args) {
        so = new SystemOrchestrator();
        launch(args); // Metodo di avvio JavaFX
    }
    
    /**
     * Changes the state of the VPN.
     */
    @FXML
    public void changeVPNState(){ 
        if (so.getConnectionStatus() == connectionStates.CONNECTED){
            so.manageVPN(vpnOperations.STOP);
            vpnButton.setText("Start VPN");
        }
        else{
            so.manageVPN(vpnOperations.START);
            vpnButton.setText("Stop VPN");
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
