package org.codec58.configs;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConvertorTest extends Convertor {

    @Test
    void testDouble2NumericType() {
        Class<Integer> toCast = Integer.class;
        Double cast = 2.0d;
        int toAssert = 2;
        assertEquals(toAssert, Convertor.double2NumericType(cast, toCast));
    }

    @Test
    void testIsNumericType() {
        assertTrue(Convertor.isNumericType(int.class));
    }

    @Test
    void testIsNumber() {
        assertTrue(Convertor.isNumber("1.1"));
    }

    @Test
    void testParseObject() {
        String toParse = "tRuE";
        assertTrue((Boolean) Convertor.parseObject(toParse, Boolean.class));
    }

    @Test
    void testGetNumber() {
        String toGet = "1.2";
        assertEquals(1.2d, Convertor.getNumber(toGet));
    }

    @Test
    void testIsBoolean() {
        assertTrue(Convertor.isBoolean("tRuE"));
    }

    @Test
    void testGetBoolean() {
        assertTrue(Convertor.getBoolean("tRuE"));
    }
}