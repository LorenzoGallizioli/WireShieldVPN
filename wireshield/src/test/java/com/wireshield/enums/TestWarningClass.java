package com.wireshield.enums;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestWarningClass {

    // Testa i valori dell'enum
    @Test
    public void testEnumValues() {
        // Verifica che l'enum contenga i valori corretti
        assertEquals(warningClass.CLEAR, warningClass.valueOf("CLEAR"));
        assertEquals(warningClass.SUSPICIOUS, warningClass.valueOf("SUSPICIOUS"));
        assertEquals(warningClass.DANGEROUS, warningClass.valueOf("DANGEROUS"));
    }
}
