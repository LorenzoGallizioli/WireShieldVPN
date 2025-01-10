package com.wireshield;

import com.wireshield.ui.*;
import javax.swing.JOptionPane;
import java.io.IOException;

public class Main {
    /**
     * Main method to launch the WireShield application.
     * 
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        // Check if the application is running as an admin.
        if (!isRunningAsAdmin()) {
            // Info messagge using Swing.
            JOptionPane.showMessageDialog(null,
                    "You must run this application as an administrator.", "Administrator Required",
                    JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        }
        UserInterface.main(args);
    }

    /**
     * Check if the application is running as an admin.
     * 
     * @return true if the application is running as an admin, false otherwise.
     */
    private static boolean isRunningAsAdmin() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("net", "session");
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            process.waitFor();
            return process.exitValue() == 0;
        } catch (IOException e) {           
            return false;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
}
