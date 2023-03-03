package com.example.mindful_reminder.service;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.mindful_reminder.client.WebClient;

import java.time.LocalDateTime;

public class GetAffirmationWorker extends Worker {
    public static final String GET_AFFIRMATION_TAG = "GetAffirmationWorker";
    private static final String TAG = GetAffirmationWorker.class.getSimpleName();
    private String affirmation;
    public static MutableLiveData<String> affirmationObservable = new MutableLiveData<>();
    private WebClient webClient;

    public GetAffirmationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.webClient = new WebClient();
    }

    @NonNull
    @Override
    public Result doWork() {
        affirmation = webClient.getMotivated();
        affirmationObservable.postValue(affirmation);
        Log.i(TAG, LocalDateTime.now() + " | Got affirmation " + affirmation);
        return Result.success();
    }

}
