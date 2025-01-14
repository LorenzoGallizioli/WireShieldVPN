package com.wireshield.enums;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestVpnOperations {

    // Testa i valori dell'enum
    @Test
    public void testEnumValues() {
        // Verifica che l'enum contenga i valori corretti
        assertEquals(vpnOperations.START, vpnOperations.valueOf("START"));
        assertEquals(vpnOperations.STOP, vpnOperations.valueOf("STOP"));
    }
}
