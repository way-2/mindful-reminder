package com.way2.mindful_reminder.fragments;

import static com.way2.mindful_reminder.config.Constants.ACTIVITY_NOTIFICATION_WORKER_TAG;
import static com.way2.mindful_reminder.config.Constants.AFFIRMATION_NOTIFICATION_TOGGLE;
import static com.way2.mindful_reminder.config.Constants.AFFIRMATION_NOTIFICATION_WORKER_TAG;
import static com.way2.mindful_reminder.config.Constants.DAILY_ACTIVITY_TOGGLE;
import static com.way2.mindful_reminder.config.Constants.DAILY_NOTIFICATION_HOUR_LIST;
import static com.way2.mindful_reminder.config.Constants.MINDFULNESS_JOURNAL_NOTIFICATION_HOUR_LIST;
import static com.way2.mindful_reminder.config.Constants.MINDFULNESS_JOURNAL_NOTIFICATION_TOGGLE;
import static com.way2.mindful_reminder.config.Constants.MINDFULNESS_JOURNAL_NOTIFICATION_WORKER;
import static com.way2.mindful_reminder.config.Constants.NOTIFICATION_TIME_INTERVAL_LIST;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.way2.mindful_reminder.R;
import com.way2.mindful_reminder.service.WorkerManager;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class SettingsFragment extends PreferenceFragmentCompat {

    private SwitchPreferenceCompat notificationSwitch;
    private ListPreference notificationIntervalListPreference;
    private SwitchPreferenceCompat dailyReminderSwitch;
    private ListPreference dailyReminderHourListPreference;
    private SwitchPreferenceCompat gratitudeReminderSwitch;
    private ListPreference gratitudeReminderHourListPreference;
    private SharedPreferences sharedPreferences;
    private WorkerManager workerManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        addPreferencesFromResource(R.xml.root_preferences);
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        workerManager = WorkerManager.getInstance();
        getUiElements();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        workerManager.cleanUp();
    }

    private void getUiElements() {
        affirmationSettings();
        activitySettings();
        gratitudeSettings();
    }

    private void gratitudeSettings() {
        gratitudeReminderSwitch = (SwitchPreferenceCompat) findPreference(MINDFULNESS_JOURNAL_NOTIFICATION_TOGGLE);
        gratitudeReminderHourListPreference = (ListPreference) findPreference(MINDFULNESS_JOURNAL_NOTIFICATION_HOUR_LIST);
        if (gratitudeReminderSwitch.isChecked()) {
            gratitudeReminderHourListPreference.setEnabled(true);
        }
        gratitudeReminderSwitch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
                if ((Boolean) newValue) {
                    workerManager.startGratitudeNotificationWorkerAlways(requireContext());
                    gratitudeReminderHourListPreference.setEnabled(true);
                } else {
                    workerManager.stopGratitudeNotificationWorker(requireContext());
                    gratitudeReminderHourListPreference.setEnabled(false);
                }
                return true;
            }
        });
        gratitudeReminderHourListPreference.setSummary(gratitudeReminderHourListPreference.getEntry());
        gratitudeReminderHourListPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
                String textValue = newValue.toString();
                int index = gratitudeReminderHourListPreference.findIndexOfValue(textValue);
                String entryString = gratitudeReminderHourListPreference.getEntries()[index].toString();
                gratitudeReminderHourListPreference.setSummary(entryString);
                try {
                    List<WorkInfo> workInfo = WorkManager.getInstance(requireContext()).getWorkInfosByTag(MINDFULNESS_JOURNAL_NOTIFICATION_WORKER).get();
                    if (!workInfo.isEmpty()) {
                        WorkInfo.State state = workInfo.get(0).getState();
                        if ((state == WorkInfo.State.RUNNING) || (state == WorkInfo.State.ENQUEUED)) {
                            workerManager.startGratitudeNotificationWorker(requireContext());
                        }
                    }
                } catch(ExecutionException | InterruptedException ex){
                    ex.printStackTrace();
                }
                return true;
            }
        });
    }

    private void activitySettings() {
        dailyReminderSwitch = (SwitchPreferenceCompat) findPreference(DAILY_ACTIVITY_TOGGLE);
        dailyReminderHourListPreference = (ListPreference) findPreference(DAILY_NOTIFICATION_HOUR_LIST);
        if (dailyReminderSwitch.isChecked()) {
            dailyReminderHourListPreference.setEnabled(true);
        }
        dailyReminderSwitch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
                if ((Boolean) newValue) {
                    workerManager.startMindfulnessNotificationWorkerAlways(requireContext());
                    dailyReminderHourListPreference.setEnabled(true);
                } else {
                    workerManager.stopActivityNotificationWorker(requireContext());
                    dailyReminderHourListPreference.setEnabled(false);
                }
                return true;
            }
        });
        dailyReminderHourListPreference.setSummary(dailyReminderHourListPreference.getEntry());
        dailyReminderHourListPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
                String textValue = newValue.toString();
                int index = dailyReminderHourListPreference.findIndexOfValue(textValue);
                String entryString = dailyReminderHourListPreference.getEntries()[index].toString();
                dailyReminderHourListPreference.setSummary(entryString);
                try {
                    List<WorkInfo> workInfo = WorkManager.getInstance(requireContext()).getWorkInfosByTag(ACTIVITY_NOTIFICATION_WORKER_TAG).get();
                    if (!workInfo.isEmpty()) {
                        WorkInfo.State state = workInfo.get(0).getState();
                        if ((state == WorkInfo.State.RUNNING) || (state == WorkInfo.State.ENQUEUED)) {
                            workerManager.startMindfulnessNotificationWorker(requireContext());
                        }
                    }
                } catch(ExecutionException | InterruptedException ex){
                    ex.printStackTrace();
                }
                return true;
            }
        });
    }

    private void affirmationSettings() {
        notificationSwitch = (SwitchPreferenceCompat) findPreference(AFFIRMATION_NOTIFICATION_TOGGLE);
        notificationIntervalListPreference = (ListPreference) findPreference(NOTIFICATION_TIME_INTERVAL_LIST);
        if (notificationSwitch.isChecked()) {
            notificationIntervalListPreference.setEnabled(true);
        }
        notificationSwitch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
                if ((Boolean) newValue) {
                    workerManager.startAffirmationNotificationWorkerAlways(requireContext());
                    notificationIntervalListPreference.setEnabled(true);
                } else {
                    workerManager.stopNotificationWorker(requireContext());
                    notificationIntervalListPreference.setEnabled(false);
                }
                return true;
            }
        });
        notificationIntervalListPreference.setSummary(notificationIntervalListPreference.getEntry());
        notificationIntervalListPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
                String textValue = newValue.toString();
                int index = notificationIntervalListPreference.findIndexOfValue(textValue);
                String entryString = notificationIntervalListPreference.getEntries()[index].toString();
                notificationIntervalListPreference.setSummary(entryString);
                try {
                    List<WorkInfo> workInfo = WorkManager.getInstance(requireContext()).getWorkInfosByTag(AFFIRMATION_NOTIFICATION_WORKER_TAG).get();
                    if (!workInfo.isEmpty()) {
                        WorkInfo.State state = workInfo.get(0).getState();
                        if ((state == WorkInfo.State.RUNNING) || (state == WorkInfo.State.ENQUEUED)) {
                            workerManager.startAffirmationNotificationWorker(requireContext());
                        }
                    }
                } catch(ExecutionException | InterruptedException ex){
                    ex.printStackTrace();
                }
                return true;
            }
        });
    }

}