package com.wireshield.enums;

import static org.junit.Assert.*;

import org.junit.Test;

public class connectionStatesTest {

    // Testa i valori dell'enum
    @Test
    public void testEnumValues() {
        // Verifica che l'enum contenga i valori corretti
        assertEquals(connectionStates.CONNECTED, connectionStates.valueOf("CONNECTED"));
        assertEquals(connectionStates.CONNECTION_IN_PROGRESS, connectionStates.valueOf("CONNECTION_IN_PROGRESS"));
        assertEquals(connectionStates.DISCONNECTED, connectionStates.valueOf("DISCONNECTED"));
    }
}
