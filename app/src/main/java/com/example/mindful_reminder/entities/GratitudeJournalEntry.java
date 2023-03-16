package com.example.mindful_reminder.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalDate;

@Entity(tableName = "gratitude_journal_entries")
public class GratitudeJournalEntry {

    @PrimaryKey
    @ColumnInfo(name = "entry_date")
    @NonNull
    private LocalDate entryDate;

    @ColumnInfo(name = "entry")
    private String entry;

    public String getEntry() {
        return entry;
    }

    @NonNull
    public LocalDate getEntryDate() {
        return entryDate;
    }

    public void setEntry(String entry) {
        this.entry = entry;
    }

    public void setEntryDate(@NonNull LocalDate entryDate) {
        this.entryDate = entryDate;
    }

    public GratitudeJournalEntry() {
    }

    public GratitudeJournalEntry(@NonNull LocalDate entryDate, String entry) {
        this.entryDate = entryDate;
        this.entry = entry;
    }

}
