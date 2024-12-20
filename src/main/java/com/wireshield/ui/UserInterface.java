package com.wireshield.ui;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import com.wireshield.localfileutils.SystemOrchestrator;
public class UserInterface extends Application {

    private SystemOrchestrator systemOrchestrator;


    @Override
    public void start(Stage primaryStage) {
        Label label = new Label("Benvenuto in JavaFX!");
        StackPane root = new StackPane(label);
        Scene scene = new Scene(root, 400, 300);

        primaryStage.setTitle("Applicazione JavaFX");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Metodo main richiesto per il punto di ingresso
    public static void main(String[] args) {
        launch(args); // Metodo di avvio JavaFX
    }
    
    /**
     * The constructor of the UserInterface.
     */
    public UserInterface(){}

    /**
     * Changes the state of the VPN.
     */
    public void changeVPNState(){}

    /**
     * Imports a peer.
     */
    public void importPeer(){}

    /**
     * Shows the available peers.
     */
    public void showAvailablePeers(){}
}
