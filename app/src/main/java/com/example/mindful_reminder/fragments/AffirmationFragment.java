package com.example.mindful_reminder.fragments;

import static com.example.mindful_reminder.activities.ActivityMain.AFFIRMATION_SHARED_PREFERENCE;
import static com.example.mindful_reminder.activities.ActivityMain.AFFIRMATION_UPDATED_SHARED_PREFERENCE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.transition.TransitionInflater;
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

import com.example.mindful_reminder.R;
import com.example.mindful_reminder.service.GetAffirmationWorker;

public class AffirmationFragment extends Fragment {

    private SharedPreferences sharedPreferences;
    private AppCompatButton skipButton;
    private TextView affirmationTextView;
    private TextView affirmationUpdatedTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_affirmation, container, false);
        setupSharedPreferences();
        setupUi(view);
        TransitionInflater transitionInflater = TransitionInflater.from(requireContext());
        return view;
    }


    private void setupSharedPreferences() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        if (!sharedPreferences.contains(AFFIRMATION_SHARED_PREFERENCE)) {
            runAffirmationOneTime();
        }
    }

    private void setupUi(View view) {
        affirmationUpdatedTextView = (TextView) view.findViewById(R.id.affirmation_updated);
        affirmationTextView = (TextView) view.findViewById(R.id.affirmation);
        affirmationTextView.setText(sharedPreferences.getString(AFFIRMATION_SHARED_PREFERENCE,""));
        affirmationUpdatedTextView.setText(sharedPreferences.getString(AFFIRMATION_UPDATED_SHARED_PREFERENCE, ""));
        skipButton = (AppCompatButton) view.findViewById(R.id.skip_button);
        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runAffirmationOneTime();
            }
        });
    }

    public void runAffirmationOneTime() {
        WorkRequest workRequest = OneTimeWorkRequest.from(GetAffirmationWorker.class);
        WorkManager workManager = WorkManager.getInstance(requireContext());
        workManager.enqueue(workRequest);
        workManager.getWorkInfoByIdLiveData(workRequest.getId()).observe(getViewLifecycleOwner(), new Observer<WorkInfo>() {
            @Override
            public void onChanged(WorkInfo workInfo) {
                if (null != workInfo && workInfo.getState().equals(WorkInfo.State.SUCCEEDED)) {

                    GetAffirmationWorker.updateDone.observe(getViewLifecycleOwner(),
                            new Observer<Boolean>() {
                                @Override
                                public void onChanged(Boolean updateDone) {
                                    workManager.getWorkInfosForUniqueWorkLiveData(GetAffirmationWorker.GET_AFFIRMATION_TAG).removeObservers(getViewLifecycleOwner());
                                    affirmationTextView.setText(sharedPreferences.getString(AFFIRMATION_SHARED_PREFERENCE, ""));
                                    affirmationUpdatedTextView.setText(sharedPreferences.getString(AFFIRMATION_UPDATED_SHARED_PREFERENCE, ""));
                                }
                            });
                }
            }
        });
    }

}