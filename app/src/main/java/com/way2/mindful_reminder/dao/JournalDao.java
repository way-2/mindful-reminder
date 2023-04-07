package com.way2.mindful_reminder.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.google.common.util.concurrent.ListenableFuture;
import com.way2.mindful_reminder.entities.JournalEntry;

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

    @Query("DELETE FROM journal_entries WHERE entry_date > :oldDate")
    ListenableFuture<Integer> deleteWhereOlderThan(LocalDate oldDate);;

}
