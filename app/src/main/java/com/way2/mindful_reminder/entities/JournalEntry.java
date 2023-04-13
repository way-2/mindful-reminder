package com.way2.mindful_reminder.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalDate;

@Entity(tableName = "journal_entries")
public class JournalEntry {

    @PrimaryKey
    @ColumnInfo(name = "entry_date")
    @NonNull
    private LocalDate entryDate;

    @ColumnInfo(name = "gratitude_entry")
    private String gratitudeEntry;

    @ColumnInfo(name = "rumination_entry")
    private String ruminationEntry;

    @ColumnInfo(name = "feeling_entry")
    private String feelingEntry;

    @ColumnInfo(name = "daily_affirmation")
    private String dailyAffirmation;

    public String getGratitudeEntry() {
        return gratitudeEntry;
    }

    public String getRuminationEntry() {
        return ruminationEntry;
    }

    public String getFeelingEntry() {
        return feelingEntry;
    }

    public String getDailyAffirmation() {
        return dailyAffirmation;
    }

    @NonNull
    public LocalDate getEntryDate() {
        return entryDate;
    }

    public void setGratitudeEntry(String gratitudeEntry) {
        this.gratitudeEntry = gratitudeEntry;
    }

    public void setRuminationEntry(String ruminationEntry) {
        this.ruminationEntry = ruminationEntry;
    }

    public void setFeelingEntry(String feelingEntry) {
        this.feelingEntry = feelingEntry;
    }

    public void setEntryDate(@NonNull LocalDate entryDate) {
        this.entryDate = entryDate;
    }

    public void setDailyAffirmation(String dailyAffirmation) {
        this.dailyAffirmation = dailyAffirmation;
    }

    public JournalEntry() {
        entryDate = LocalDate.now();
    }

}
