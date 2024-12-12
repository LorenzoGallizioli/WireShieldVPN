package com.wireshield;

import com.wireshield.enums.vpnOperations;
import com.wireshield.localfileutils.SystemOrchestrator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Entry point dell'applicazione.
 */
public class Main 
{
    private static final Logger logger = LogManager.getLogger(Main.class);
    
    public static void main( String[] args )
    {
        SystemOrchestrator so = new SystemOrchestrator();
        so.manageVPN(vpnOperations.START);
        logger.info("Hello World!");
    }
}
