package com.example.mindful_reminder.service;

import static com.example.mindful_reminder.config.Constants.AFFIRMATION_SHARED_PREFERENCE;
import static com.example.mindful_reminder.config.Constants.AFFIRMATION_UPDATED_SHARED_PREFERENCE;
import static com.example.mindful_reminder.config.Constants.GET_AFFIRMATION_TAG;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.preference.PreferenceManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.mindful_reminder.R;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class GetAffirmationWorker extends Worker {
    public static MutableLiveData<Boolean> updateDone = new MutableLiveData<>();
    private final SharedPreferences sharedPreferences;

    public GetAffirmationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    }

    @NonNull
    @Override
    public Result doWork() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(AFFIRMATION_SHARED_PREFERENCE, getRandomAffirmation());
        editor.putString(AFFIRMATION_UPDATED_SHARED_PREFERENCE, "Last Updated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a")));
        editor.apply();
        updateDone.postValue(true);
        Log.i(GET_AFFIRMATION_TAG, LocalDateTime.now() + " | Got affirmation " + sharedPreferences.getString(AFFIRMATION_SHARED_PREFERENCE, ""));
        return Result.success();
    }

    private String getRandomAffirmation() {
        String[] affirmationArray = getApplicationContext().getResources().getStringArray(R.array.affirmations_array);
        return affirmationArray[new Random().nextInt(affirmationArray.length)];
    }

}
