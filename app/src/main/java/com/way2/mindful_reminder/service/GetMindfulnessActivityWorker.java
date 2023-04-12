package com.way2.mindful_reminder.service;

import static com.way2.mindful_reminder.config.Constants.DAILY_MINDFULNESS_ACTIVITY_SHARED_PREFERENCE;
import static com.way2.mindful_reminder.config.Constants.DAILY_MINDFULNESS_ACTIVITY_UPDATED_SHARED_PREFERENCE;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.preference.PreferenceManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.way2.mindful_reminder.R;
import com.way2.mindful_reminder.util.MindfulReminder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class GetMindfulnessActivityWorker extends Worker {

    public static MutableLiveData<Boolean> updateDone = new MutableLiveData<>();
    private final SharedPreferences sharedPreferences;

    public GetMindfulnessActivityWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MindfulReminder.getContext());
    }

    @NonNull
    @Override
    public Result doWork() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(DAILY_MINDFULNESS_ACTIVITY_SHARED_PREFERENCE, getRandomActivity());
        editor.putString(DAILY_MINDFULNESS_ACTIVITY_UPDATED_SHARED_PREFERENCE, "Last Updated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a")));
        editor.apply();
        updateDone.postValue(true);
        return Result.success();
    }

    private String getRandomActivity() {
        String[] activityArray = MindfulReminder.getContext().getResources().getStringArray(R.array.activity_name);
        return activityArray[new Random().nextInt(activityArray.length)];
    }
}
