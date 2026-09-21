package com.itz0cat.catpay.command;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AmountParserTest {

    @Test
    public void testStandardIntegerAmounts() {
        assertEquals(500L, AmountParser.parse("500"));
        assertEquals(5000L, AmountParser.parse("5000"));
        assertEquals(500_000L, AmountParser.parse("500000"));
        assertEquals(500_000_000L, AmountParser.parse("500000000"));
        assertEquals(500_000_000L, AmountParser.parse("500,000,000"));
        assertEquals(500_000_000L, AmountParser.parse("$500,000,000"));
    }

    @Test
    public void testShorthandNotations() {
        assertEquals(1_000L, AmountParser.parse("1k"));
        assertEquals(10_000L, AmountParser.parse("10k"));
        assertEquals(1_000_000L, AmountParser.parse("1m"));
        assertEquals(10_000_000L, AmountParser.parse("10m"));
        assertEquals(1_500_000L, AmountParser.parse("1.5m"));
        assertEquals(2_000_000_000L, AmountParser.parse("2b"));
        assertEquals(1_000_000_000_000L, AmountParser.parse("1000b"));
    }

    @Test
    public void testCommaFormatting() {
        assertEquals("500", AmountParser.format(500));
        assertEquals("5,000", AmountParser.format(5000));
        assertEquals("500,000", AmountParser.format(500000));
        assertEquals("500,000,000", AmountParser.format(500000000));
        assertEquals("1,000,000,000,000", AmountParser.format(1_000_000_000_000L));
    }

    @Test
    public void testInvalidInputs() {
        assertThrows(IllegalArgumentException.class, () -> AmountParser.parse("0"));
        assertThrows(IllegalArgumentException.class, () -> AmountParser.parse("-100"));
        assertThrows(IllegalArgumentException.class, () -> AmountParser.parse("abc"));
        assertThrows(IllegalArgumentException.class, () -> AmountParser.parse(""));
        assertThrows(IllegalArgumentException.class, () -> AmountParser.parse(null));
        assertThrows(IllegalArgumentException.class, () -> AmountParser.parse("99999999999999999999999999999999999"));
    }
}
