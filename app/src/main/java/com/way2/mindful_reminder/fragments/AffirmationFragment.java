package com.way2.mindful_reminder.fragments;

import static com.way2.mindful_reminder.config.Constants.AFFIRMATION_SHARED_PREFERENCE;
import static com.way2.mindful_reminder.config.Constants.AFFIRMATION_UPDATED_SHARED_PREFERENCE;
import static com.way2.mindful_reminder.config.Constants.IMAGE_SHARED_PREFERENCE;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.way2.mindful_reminder.service.DailyWorker;
import com.way2.mindful_reminder.util.MindfulReminder;

public class AffirmationFragment extends Fragment {

    private SharedPreferences sharedPreferences;
    private TextView affirmationTextView;
    private TextView affirmationUpdatedTextView;
    private ImageView imageView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_affirmation, container, false);
        setupSharedPreferences();
        setupUi(view);
        return view;
    }


    private void setupSharedPreferences() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MindfulReminder.getContext());
        if (!sharedPreferences.contains(AFFIRMATION_SHARED_PREFERENCE) || !sharedPreferences.contains(IMAGE_SHARED_PREFERENCE)) {
            runAffirmationOneTime();
        }
    }

    private void setupUi(View view) {
        affirmationUpdatedTextView = view.findViewById(R.id.affirmation_updated);
        affirmationTextView = view.findViewById(R.id.affirmation);
        affirmationTextView.setText(sharedPreferences.getString(AFFIRMATION_SHARED_PREFERENCE,""));
        affirmationUpdatedTextView.setText(sharedPreferences.getString(AFFIRMATION_UPDATED_SHARED_PREFERENCE, ""));
        imageView = view.findViewById(R.id.affirmation_image_view);
        setImage();
        AppCompatButton skipButton = view.findViewById(R.id.skip_button);
        skipButton.setOnClickListener(v -> runAffirmationOneTime());
    }

    private void setImage() {
        int choice = sharedPreferences.getInt(IMAGE_SHARED_PREFERENCE, R.drawable.high_quality_photo__calm_s2914371834_st64_g7_5);
        try {
            imageView.setBackgroundResource(choice);
        } catch (Resources.NotFoundException notFoundException) {
            Log.d(this.getClass().getSimpleName(), "Using old image setting default, new image will be picked on next daily update");
            imageView.setBackgroundResource(R.drawable.mindful_reminder);
        }
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
    }

    public void runAffirmationOneTime() {
        WorkRequest workRequest = OneTimeWorkRequest.from(DailyWorker.class);
        WorkManager workManager = WorkManager.getInstance(MindfulReminder.getContext());
        workManager.enqueue(workRequest);
        workManager.getWorkInfoByIdLiveData(workRequest.getId()).observe(getViewLifecycleOwner(), workInfo -> {
            if (null != workInfo && workInfo.getState().equals(WorkInfo.State.SUCCEEDED)) {

                DailyWorker.updateDone.observe(getViewLifecycleOwner(),
                        new Observer<>() {
                            @Override
                            public void onChanged(Boolean updateDone) {
                                affirmationTextView.setText(sharedPreferences.getString(AFFIRMATION_SHARED_PREFERENCE, ""));
                                affirmationUpdatedTextView.setText(sharedPreferences.getString(AFFIRMATION_UPDATED_SHARED_PREFERENCE, ""));
                                setImage();
                            }
                        });
            }
        });
    }

}