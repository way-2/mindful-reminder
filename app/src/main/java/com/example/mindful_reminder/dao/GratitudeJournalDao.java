package com.example.mindful_reminder.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.mindful_reminder.entities.GratitudeJournalEntry;
import com.google.common.util.concurrent.ListenableFuture;

import java.time.LocalDate;
import java.util.List;

@Dao
public interface GratitudeJournalDao {

    @Query("SELECT entry FROM gratitude_journal_entries WHERE entry_date = :entryDate")
    ListenableFuture<String> getEntryForDate(LocalDate entryDate);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    ListenableFuture<Long> insertGratitudeJournalEntry(GratitudeJournalEntry gratitudeJournalEntry);

    @Query("SELECT entry_date FROM gratitude_journal_entries")
    ListenableFuture<List<LocalDate>> getEntryDates();

    @Query("DELETE FROM gratitude_journal_entries WHERE entry_date = :entryDate")
    ListenableFuture<Integer> deleteGratitudeJournalEntryByDate(LocalDate entryDate);

}
