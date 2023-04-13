package com.way2.mindful_reminder.databases;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.way2.mindful_reminder.dao.JournalDao;
import com.way2.mindful_reminder.entities.JournalEntry;
import com.way2.mindful_reminder.util.MindfulReminder;

@Database(entities = {JournalEntry.class}, version = 2, exportSchema = false)
@TypeConverters({com.way2.mindful_reminder.util.TypeConverters.class})
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase appDatabaseInstance;
    public static final String DATABASE_NAME = "mindfulReminderDatabase";

    public abstract JournalDao gratitudeJournalDao();

    public static synchronized AppDatabase getInstance() {
        if (appDatabaseInstance == null) {
            appDatabaseInstance = Room.databaseBuilder(MindfulReminder.getContext(), AppDatabase.class, DATABASE_NAME).addMigrations(MIGRATION_1_2)
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

    static final Migration MIGRATION_1_2 = new Migration(1,2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE journal_entries ADD COLUMN daily_affirmation TEXT");
        }
    };

    public void cleanUp() {
        appDatabaseInstance.close();
        appDatabaseInstance = null;
    }
}
