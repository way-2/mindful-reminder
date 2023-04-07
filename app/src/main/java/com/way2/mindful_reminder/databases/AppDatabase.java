package com.way2.mindful_reminder.databases;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.way2.mindful_reminder.dao.JournalDao;
import com.way2.mindful_reminder.entities.JournalEntry;

@Database(entities = {JournalEntry.class}, version = 1, exportSchema = false)
@TypeConverters({com.way2.mindful_reminder.util.TypeConverters.class})
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase appDatabaseInstance;
    public static final String DATABASE_NAME = "mindfulReminderDatabase";

    public abstract JournalDao gratitudeJournalDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (appDatabaseInstance == null) {
            appDatabaseInstance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DATABASE_NAME)
                    .addCallback(new Callback() {
                        @Override
                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
                            super.onCreate(db);
                        }

                        @Override
                        public void onDestructiveMigration(@NonNull SupportSQLiteDatabase db) {
                            super.onDestructiveMigration(db);
                        }

                        @Override
                        public void onOpen(@NonNull SupportSQLiteDatabase db) {
                            super.onOpen(db);
                        }
                    })
                    .build();
        }
        return appDatabaseInstance;
    }

    public void cleanUp() {
        appDatabaseInstance.close();
        appDatabaseInstance = null;
    }

}
