package com.wireshield;
import java.io.IOException;
import java.text.ParseException;
import com.wireshield.ui.UserInterface;

/*
 * Entry point for the WireShield application.
 */
public class Main {
    /**
     * Main method to launch the WireShield application.
     * 
     * @param args Command line arguments.
     */
    public static void main(String[] args) throws IOException, ParseException {
        UserInterface.main(args);
    }
}