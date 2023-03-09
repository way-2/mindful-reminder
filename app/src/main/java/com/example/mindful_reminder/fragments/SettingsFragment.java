package com.example.mindful_reminder.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.example.mindful_reminder.R;
import com.example.mindful_reminder.service.NotificationWorker;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class SettingsFragment extends PreferenceFragmentCompat {

    private SwitchPreferenceCompat notificationSwitch;
    private ListPreference notificationIntervalListPreference;
    private SharedPreferences sharedPreferences;
    private static final String NOTIFICATION_TOGGLE_SWITCH_KEY = "notification_toggle";
    private static final String NOTIFICATION_TIME_INTERVAL_LIST = "notification_time_interval_list";
    private static final String NOTIFICATION_INTERVAL_LONG = "notification_interval_long";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getUiElements();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void getUiElements() {
        notificationSwitch = (SwitchPreferenceCompat) findPreference(NOTIFICATION_TOGGLE_SWITCH_KEY);
        notificationSwitch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
                if ((Boolean) newValue) {
                    startNotificationWorker();
                } else {
                    stopNotificationWorker();
                }
                return true;
            }
        });
        notificationIntervalListPreference = (ListPreference) findPreference(NOTIFICATION_TIME_INTERVAL_LIST);
        if (!sharedPreferences.contains(NOTIFICATION_INTERVAL_LONG)) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putLong(NOTIFICATION_INTERVAL_LONG, Long.parseLong(notificationIntervalListPreference.getValue()));
            editor.apply();
        }
        notificationIntervalListPreference.setSummary(notificationIntervalListPreference.getEntry());
        notificationIntervalListPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
                String textValue = newValue.toString();
                int index = notificationIntervalListPreference.findIndexOfValue(textValue);
                String entryString = notificationIntervalListPreference.getEntries()[index].toString();
                String entryValue = notificationIntervalListPreference.getEntryValues()[index].toString();
                long longValue = Long.parseLong(entryValue);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putLong(NOTIFICATION_INTERVAL_LONG, longValue);
                editor.apply();
                notificationIntervalListPreference.setSummary(entryString);
                try {
                    List<WorkInfo> workInfo = WorkManager.getInstance(requireContext()).getWorkInfosByTag(NotificationWorker.NOTIFICATION_WORKER_TAG).get();
                    if (!workInfo.isEmpty()) {
                        WorkInfo.State state = workInfo.get(0).getState();
                        if ((state == WorkInfo.State.RUNNING) || (state == WorkInfo.State.ENQUEUED)) {
                            startNotificationWorker();
                        }
                    }
                    } catch(ExecutionException | InterruptedException ex){
                        ex.printStackTrace();
                    }
                return true;
            }
        });
    }

    private void stopNotificationWorker() {
        WorkManager workManager = WorkManager.getInstance(requireContext().getApplicationContext());
        workManager.cancelUniqueWork(NotificationWorker.NOTIFICATION_WORKER_TAG);
    }

    private void startNotificationWorker() {
        long notificationInterval = sharedPreferences.getLong(NOTIFICATION_INTERVAL_LONG, 30);
        Log.i("notificationSetting", "Setting notifications to every " + notificationInterval + " minutes");
        PeriodicWorkRequest.Builder workBuilder = new PeriodicWorkRequest.Builder(NotificationWorker.class, notificationInterval, TimeUnit.MINUTES).setInitialDelay(15, TimeUnit.SECONDS).addTag(NotificationWorker.NOTIFICATION_WORKER_TAG);
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresCharging(false)
                .setRequiresDeviceIdle(false)
                .build();
        PeriodicWorkRequest runWork = workBuilder.setConstraints(constraints).build();
        WorkManager workManager = WorkManager.getInstance(requireContext().getApplicationContext());
        workManager.enqueueUniquePeriodicWork(NotificationWorker.NOTIFICATION_WORKER_TAG, ExistingPeriodicWorkPolicy.KEEP, runWork);
    }

}