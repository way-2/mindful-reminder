package com.way2.mindful_reminder.service;

import static com.way2.mindful_reminder.config.Constants.AFFIRMATION_SHARED_PREFERENCE;
import static com.way2.mindful_reminder.config.Constants.AFFIRMATION_UPDATED_SHARED_PREFERENCE;
import static com.way2.mindful_reminder.config.Constants.DAILY_MINDFULNESS_ACTIVITY_SHARED_PREFERENCE;
import static com.way2.mindful_reminder.config.Constants.DAILY_MINDFULNESS_ACTIVITY_UPDATED_SHARED_PREFERENCE;
import static com.way2.mindful_reminder.config.Constants.IMAGE_SHARED_PREFERENCE;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.preference.PreferenceManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.way2.mindful_reminder.R;
import com.way2.mindful_reminder.databases.AppDatabase;
import com.way2.mindful_reminder.util.MindfulReminder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class DailyWorker extends Worker {
    public static MutableLiveData<Boolean> updateDone = new MutableLiveData<>();
    private final SharedPreferences sharedPreferences;
    private AppDatabase database;

    public DailyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MindfulReminder.getContext());
    }

    @NonNull
    @Override
    public Result doWork() {
        updateAffirmation();
        doDatabasePurge();
        return Result.success();
    }

    private void doDatabasePurge() {
        try {
            LocalDate oldDate = LocalDate.now().minusYears(10);
            database = AppDatabase.getInstance();
            Integer countDeleted = database.gratitudeJournalDao().deleteWhereOlderThan(oldDate).get();
        } catch (ExecutionException | InterruptedException ex) {
            ex.printStackTrace();
        } finally {
            database.cleanUp();
        }
    }

    private void updateAffirmation() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(AFFIRMATION_SHARED_PREFERENCE, getRandomAffirmation());
        editor.putString(AFFIRMATION_UPDATED_SHARED_PREFERENCE, "Last Updated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a")));
        editor.putString(DAILY_MINDFULNESS_ACTIVITY_SHARED_PREFERENCE, getRandomActivity());
        editor.putString(DAILY_MINDFULNESS_ACTIVITY_UPDATED_SHARED_PREFERENCE, "Last Updated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a")));
        editor.putInt(IMAGE_SHARED_PREFERENCE, getRandomImage());
        editor.apply();
        updateDone.postValue(true);
    }

    private int getRandomImage() {
        TypedArray images = MindfulReminder.getContext().getResources().obtainTypedArray(R.array.background_images);
        int choice = images.getResourceId((int) (Math.random() * images.length()), R.drawable.mindful_reminder);
        images.recycle();
        return choice;
    }

    private String getRandomActivity() {
        String[] activityArray = MindfulReminder.getContext().getResources().getStringArray(R.array.activity_name);
        return activityArray[new Random().nextInt(activityArray.length)];
    }

    private String getRandomAffirmation() {
        String[] affirmationArray = MindfulReminder.getContext().getResources().getStringArray(R.array.affirmations_array);
        return affirmationArray[new Random().nextInt(affirmationArray.length)];
    }
}
