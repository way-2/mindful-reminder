package com.way2.mindful_reminder.util;

import androidx.room.TypeConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TypeConverters {


    @TypeConverter
    public static LocalDate fromString(String dateString) {
        return LocalDate.parse(dateString, DateTimeFormatter.ISO_DATE);
    }

    @TypeConverter
    public static String toString(LocalDate localDate) {
        return localDate.format(DateTimeFormatter.ISO_DATE);
    }

}
