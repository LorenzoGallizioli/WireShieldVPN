package com.wireshield.enums;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestRunningStates {

    // Testa i valori dell'enum
    @Test
    public void testEnumValues() {
        // Verifica che l'enum contenga i valori corretti
        assertEquals(runningStates.UP, runningStates.valueOf("UP"));
        assertEquals(runningStates.DOWN, runningStates.valueOf("DOWN"));
    }
}
