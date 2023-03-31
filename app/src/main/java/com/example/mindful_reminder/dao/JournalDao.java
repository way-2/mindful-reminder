package com.example.mindful_reminder.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.mindful_reminder.entities.JournalEntry;
import com.google.common.util.concurrent.ListenableFuture;

import java.time.LocalDate;
import java.util.List;

@Dao
public interface JournalDao {

    @Query("SELECT * FROM journal_entries WHERE entry_date = :entryDate")
    ListenableFuture<JournalEntry> getEntryForDate(LocalDate entryDate);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    ListenableFuture<Long> insertGratitudeJournalEntry(JournalEntry journalEntry);

    @Query("SELECT entry_date FROM journal_entries")
    ListenableFuture<List<LocalDate>> getEntryDates();

}
