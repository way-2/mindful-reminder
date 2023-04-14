package com.way2.mindful_reminder.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TypeConvertersTest {

    @Test
    public void fromStringTest() {
        String inString = "2022-10-16";
        LocalDate outDate = TypeConverters.fromString(inString);
        assertEquals(inString, outDate.format(DateTimeFormatter.ISO_DATE));
    }

    @Test
    public void toStringTest() {
        LocalDate inDate = LocalDate.now();
        String outString = TypeConverters.toString(inDate);
        assertEquals(inDate.format(DateTimeFormatter.ISO_DATE), outString);
    }
}
