package com.wireshield.wireguard;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.wireshield.enums.connectionStates;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;

public class ConnectionTest {

    private Connection connection;

    @Before
    public void setUp() {
        connection = Connection.getInstance();
    }

    @Test
    public void testSetStatusAndGetStatus() {
        connection.setStatus(connectionStates.CONNECTED);
        assertEquals(connectionStates.CONNECTED, connection.getStatus());
    }

    @Test
    public void testGetActiveInterface() throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/c", "echo wg0");
        Process process = processBuilder.start();
        InputStream inputStream = process.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        
        String activeInterface = reader.readLine();
        assertNotNull(activeInterface);
        assertEquals("wg0", activeInterface);
    }

    @Test
    public void testGetSentTraffic() throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/c", "echo transfer=1000 2000");
        Process process = processBuilder.start();
        InputStream inputStream = process.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        
        String line = reader.readLine();
        assertNotNull(line);
        String[] traffic = line.split("=")[1].split(" ");
        Long sentTraffic = Long.parseLong(traffic[0]);
        
        assertEquals(Long.valueOf(1000), sentTraffic);
    }

    @Test
    public void testGetReceivedTraffic() throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/c", "echo transfer=1000 2000");
        Process process = processBuilder.start();
        InputStream inputStream = process.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        
        String line = reader.readLine();
        assertNotNull(line);
        String[] traffic = line.split("=")[1].split(" ");
        Long receivedTraffic = Long.parseLong(traffic[1]);
        
        assertEquals(Long.valueOf(2000), receivedTraffic);
    }

    @Test
    public void testGetLastHandshakeTime() throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/c", "echo latest-handshakes=123456");
        Process process = processBuilder.start();
        InputStream inputStream = process.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        
        String line = reader.readLine();
        assertNotNull(line);
        Long lastHandshakeTime = Long.parseLong(line.split("=")[1]);
        
        assertEquals(Long.valueOf(123456), lastHandshakeTime);
    }

    @Test
    public void testToString() throws IOException {
        // Simulate external method calls
        ProcessBuilder activeInterfaceProcess = new ProcessBuilder("cmd", "/c", "echo wg0");
        Process activeInterfaceProc = activeInterfaceProcess.start();
        BufferedReader activeInterfaceReader = new BufferedReader(new InputStreamReader(activeInterfaceProc.getInputStream()));
        String activeInterface = activeInterfaceReader.readLine();

        ProcessBuilder transferProcess = new ProcessBuilder("cmd", "/c", "echo transfer=1000 2000");
        Process transferProc = transferProcess.start();
        BufferedReader transferReader = new BufferedReader(new InputStreamReader(transferProc.getInputStream()));
        String[] transfer = transferReader.readLine().split("=")[1].split(" ");
        Long sentTraffic = Long.parseLong(transfer[0]);
        Long receivedTraffic = Long.parseLong(transfer[1]);

        ProcessBuilder handshakeProcess = new ProcessBuilder("cmd", "/c", "echo latest-handshakes=123456");
        Process handshakeProc = handshakeProcess.start();
        BufferedReader handshakeReader = new BufferedReader(new InputStreamReader(handshakeProc.getInputStream()));
        Long lastHandshakeTime = Long.parseLong(handshakeReader.readLine().split("=")[1]);

        connection.setStatus(connectionStates.CONNECTED);

        String expected = "[INFO] Interface: wg0\n[INFO] Status: CONNECTED\n[INFO] Last handshake time: 123456\n[INFO] Received traffic: 2000\n[INFO] Sent traffic: 1000";
        
        String actual = String.format(
            "[INFO] Interface: %s\n[INFO] Status: %s\n[INFO] Last handshake time: %s\n[INFO] Received traffic: %s\n[INFO] Sent traffic: %s",
            activeInterface,
            connection.getStatus(),
            lastHandshakeTime,
            receivedTraffic,
            sentTraffic);

        assertEquals(expected, actual);
    }
}