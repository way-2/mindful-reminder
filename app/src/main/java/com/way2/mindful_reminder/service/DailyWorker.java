package com.way2.mindful_reminder.service;

import static com.way2.mindful_reminder.config.Constants.AFFIRMATION_SHARED_PREFERENCE;
import static com.way2.mindful_reminder.config.Constants.AFFIRMATION_UPDATED_SHARED_PREFERENCE;
import static com.way2.mindful_reminder.config.Constants.DAILY_ACTIVITY_TAG;
import static com.way2.mindful_reminder.config.Constants.DAILY_MINDFULNESS_ACTIVITY_SHARED_PREFERENCE;
import static com.way2.mindful_reminder.config.Constants.DAILY_MINDFULNESS_ACTIVITY_UPDATED_SHARED_PREFERENCE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.preference.PreferenceManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.way2.mindful_reminder.R;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class DailyWorker extends Worker {

    public static MutableLiveData<Boolean> updateDone = new MutableLiveData<>();
    private final SharedPreferences sharedPreferences;

    public DailyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    }

    @NonNull
    @Override
    public Result doWork() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(AFFIRMATION_SHARED_PREFERENCE, getRandomAffirmation());
        editor.putString(AFFIRMATION_UPDATED_SHARED_PREFERENCE, "Last Updated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a")));
        editor.putString(DAILY_MINDFULNESS_ACTIVITY_SHARED_PREFERENCE, getRandomActivity());
        editor.putString(DAILY_MINDFULNESS_ACTIVITY_UPDATED_SHARED_PREFERENCE, "Last Updated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a")));
        editor.apply();
        updateDone.postValue(true);
        Log.i(DAILY_ACTIVITY_TAG, LocalDateTime.now() + " | Got activity " + sharedPreferences.getString(DAILY_MINDFULNESS_ACTIVITY_SHARED_PREFERENCE, ""));
        return Result.success();
    }

    private String getRandomActivity() {
        String[] activityArray = getApplicationContext().getResources().getStringArray(R.array.activity_name);
        return activityArray[new Random().nextInt(activityArray.length)];
    }

    private String getRandomAffirmation() {
        String[] affirmationArray = getApplicationContext().getResources().getStringArray(R.array.affirmations_array);
        return affirmationArray[new Random().nextInt(affirmationArray.length)];
    }
}
