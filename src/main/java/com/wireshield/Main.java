package com.wireshield;

import com.wireshield.enums.*;
import com.wireshield.localfileutils.SystemOrchestrator;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.parser.ParseException;

/*
 * Entry point for the WireShield application.
 */
public class Main {
    /**
     * Main method to launch the WireShield application.
     * 
     * @param args Command line arguments (not used).
     * @throws ParseException 
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException, ParseException {
        SystemOrchestrator so = SystemOrchestrator.getInstance();
        //so.manageVPN(vpnOperations.START);
        //so.getWireguardManager().startUpdateWireguardLogs();
        
        /*while(true) {
        	try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	System.out.println(so.getWireguardManager().getLog());
        }*/
        so.manageVPN(vpnOperations.STOP);
        //so.manageDownload(runningStates.UP);
        //so.manageAV(runningStates.UP);
    }
}