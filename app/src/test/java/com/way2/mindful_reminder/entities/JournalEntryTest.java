package com.way2.mindful_reminder.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.way2.mindful_reminder.entities.JournalEntry;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

public class JournalEntryTest {

    @Test
    public void journalEntryTest() {
        JournalEntry journalEntry = new JournalEntry();
        LocalDate entryDate = LocalDate.now();
        String feelingEntry = "this is a test feeling";
        String gratitudeEntry = "this is a test gratitude entry";
        String ruminationEntry = "this is a test rumination entry";
        String dailyAffirmationEntry = "this is a test daily affirmation entry";
        journalEntry.setEntryDate(LocalDate.now());
        journalEntry.setFeelingEntry(feelingEntry);
        journalEntry.setGratitudeEntry(gratitudeEntry);
        journalEntry.setRuminationEntry(ruminationEntry);
        journalEntry.setDailyAffirmation(dailyAffirmationEntry);
        assertEquals(entryDate, journalEntry.getEntryDate());
        assertEquals(feelingEntry, journalEntry.getFeelingEntry());
        assertEquals(gratitudeEntry, journalEntry.getGratitudeEntry());
        assertEquals(ruminationEntry, journalEntry.getRuminationEntry());
        assertEquals(dailyAffirmationEntry, journalEntry.getDailyAffirmation());
    }
}
