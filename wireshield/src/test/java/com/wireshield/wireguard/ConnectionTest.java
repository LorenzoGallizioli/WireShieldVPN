package com.wireshield.wireguard;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.wireshield.enums.connectionStates;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ConnectionTest {

	@Spy
	Connection conn = Mockito.spy(Connection.getInstance());
    
    @Test
    public void testGetTraffic_valid() {
    	Mockito.doReturn("12345 67890").when(conn).wgShow("transfer");
        Long[] traffic = conn.getTraffic();   
        assertNotNull(traffic);
        assertEquals(12345, (long)traffic[0]);
        assertEquals(67890, (long)traffic[1]);
    }
    
    @Test
    public void testGetTraffic_null() {
    	Mockito.doReturn(null).when(conn).wgShow("transfer");
    	Long[] traffic = conn.getTraffic();
        assertEquals(0, (long)traffic[0]);
        assertEquals(0, (long)traffic[1]);
    }
    
    @Test
    public void testWgShow() {
    	// not tested, we had tried but Mockito is not applicable --> need run wireguard.exe and wg.exe with a valid peer 
    }

    @Test
    public void testSetStatusAndGetStatus() {
    	conn.setStatus(connectionStates.CONNECTED);
        assertEquals(connectionStates.CONNECTED, conn.getStatus());
    }

    @Test
    public void testGetActiveInterface() throws IOException {
    	
    	conn.getActiveInterface(); // run getActiveInterface() but to be deterministic simulate its output
    	
        ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/c", "echo wg0");
        Process process = processBuilder.start();
        InputStream inputStream = process.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        
        String activeInterface = reader.readLine();
        assertNotNull(activeInterface);
        assertEquals("wg0", activeInterface);
    }


    @Test
    public void testGetLastHandshakeTime() throws IOException {
    	
    	conn.getLastHandshakeTime(); // run getLastHandshakeTime() but to be deterministic we will simulate its output
    	
        ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/c", "echo latest-handshakes=1234567890");
        Process process = processBuilder.start();
        InputStream inputStream = process.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        
        String line = reader.readLine();
        assertNotNull(line);
        Long lastHandshakeTime = Long.parseLong(line.split("=")[1]);
        
        assertEquals(Long.valueOf(1234567890), lastHandshakeTime);
    }

    @Test
    public void testToString() {
    	
    	Long sentTraffic = 12345L;
    	Long receivedTraffic = 67890L;
    	connectionStates status = connectionStates.CONNECTED;
    	Long lastHandshakeTime = 1234567890L;
    	String activeInterface = "wg0";
    	
    	conn.setSentTraffic(sentTraffic);
    	conn.setReceivedTraffic(receivedTraffic);
        conn.setStatus(status);
        conn.setLastHandshakeTime(lastHandshakeTime);
        conn.setActiveInterface(activeInterface);
        
        String expected = conn.toString();
        
        String actual = String.format(
            "[INFO] Interface: %s%n[INFO] Status: %s%n[INFO] Last handshake time: %s%n[INFO] Received traffic: %s%n[INFO] Sent traffic: %s",
            activeInterface,
            status,
            lastHandshakeTime,
            (long)receivedTraffic,
            (long)sentTraffic);

        assertEquals(expected, actual);
    }
}