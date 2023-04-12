package com.way2.mindful_reminder.fragments;

import static com.way2.mindful_reminder.config.Constants.DAILY_MINDFULNESS_ACTIVITY_SHARED_PREFERENCE;
import static com.way2.mindful_reminder.config.Constants.DAILY_MINDFULNESS_ACTIVITY_UPDATED_SHARED_PREFERENCE;
import static com.way2.mindful_reminder.config.Constants.GET_ACTIVITY_TAG;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.preference.PreferenceManager;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.way2.mindful_reminder.R;
import com.way2.mindful_reminder.service.GetMindfulnessActivityWorker;
import com.way2.mindful_reminder.util.MindfulReminder;

import java.util.Arrays;

public class DailyMindfulnessActivity extends Fragment {

    private SharedPreferences sharedPreferences;
    private AppCompatButton skipButton;
    private TextView activityTextView;
    private TextView activityDetailsTextView;
    private TextView activityUpdatedTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_daily_mindfulness_activity, container, false);
        setupSharedPreferences();
        setupUi(view);
        return view;
    }


    private void setupSharedPreferences() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MindfulReminder.getContext());
        if (!sharedPreferences.contains(DAILY_MINDFULNESS_ACTIVITY_SHARED_PREFERENCE)) {
            runActivityOneTime();
        }
    }

    private void setupUi(View view) {
        activityUpdatedTextView = (TextView) view.findViewById(R.id.activity_updated);
        activityTextView = (TextView) view.findViewById(R.id.daily_mindfulness_activity);
        activityDetailsTextView = (TextView) view.findViewById(R.id.daily_mindfulness_activity_details);
        activityTextView.setText(sharedPreferences.getString(DAILY_MINDFULNESS_ACTIVITY_SHARED_PREFERENCE,""));
        activityUpdatedTextView.setText(sharedPreferences.getString(DAILY_MINDFULNESS_ACTIVITY_UPDATED_SHARED_PREFERENCE, ""));
        if (sharedPreferences.contains(DAILY_MINDFULNESS_ACTIVITY_SHARED_PREFERENCE)) {
            int index = Arrays.asList(getResources().getStringArray(R.array.activity_name)).indexOf(sharedPreferences.getString(DAILY_MINDFULNESS_ACTIVITY_SHARED_PREFERENCE, ""));
            activityDetailsTextView.setText(getResources().getStringArray(R.array.activity_text)[index]);
        }
        skipButton = (AppCompatButton) view.findViewById(R.id.skip_button);
        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runActivityOneTime();
            }
        });
    }

    public void runActivityOneTime() {
        WorkRequest workRequest = OneTimeWorkRequest.from(GetMindfulnessActivityWorker.class);
        WorkManager workManager = WorkManager.getInstance(MindfulReminder.getContext());
        workManager.enqueue(workRequest);
        workManager.getWorkInfoByIdLiveData(workRequest.getId()).observe(getViewLifecycleOwner(), new Observer<WorkInfo>() {
            @Override
            public void onChanged(WorkInfo workInfo) {
                if (null != workInfo && workInfo.getState().equals(WorkInfo.State.SUCCEEDED)) {

                    GetMindfulnessActivityWorker.updateDone.observe(getViewLifecycleOwner(),
                            new Observer<Boolean>() {
                                @Override
                                public void onChanged(Boolean updateDone) {
                                    workManager.getWorkInfosForUniqueWorkLiveData(GET_ACTIVITY_TAG).removeObservers(getViewLifecycleOwner());
                                    activityTextView.setText(sharedPreferences.getString(DAILY_MINDFULNESS_ACTIVITY_SHARED_PREFERENCE, ""));
                                    activityUpdatedTextView.setText(sharedPreferences.getString(DAILY_MINDFULNESS_ACTIVITY_UPDATED_SHARED_PREFERENCE, ""));
                                    int index = Arrays.asList(getResources().getStringArray(R.array.activity_name)).indexOf(sharedPreferences.getString(DAILY_MINDFULNESS_ACTIVITY_SHARED_PREFERENCE, ""));
                                    activityDetailsTextView.setText(getResources().getStringArray(R.array.activity_text)[index]);
                                }
                            });
                }
            }
        });
    }

}