package com.example.mindful_reminder.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.mindful_reminder.R;
import com.example.mindful_reminder.service.NotificationWorker;

import java.util.concurrent.TimeUnit;

public class SettingsFragment extends PreferenceFragmentCompat {

    private static SwitchPreferenceCompat notificationSwitch;
    private static final String NOTIFICATION_TOGGLE_SWITCH_KEY = "notification_toggle";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getUiElements();
        setHasOptionsMenu(true);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        menu.findItem(R.id.settings).setVisible(false);
        menu.findItem(R.id.settings).setEnabled(false);
        menu.findItem(R.id.help).setVisible(false);
        menu.findItem(R.id.help).setEnabled(false);
        menu.findItem(R.id.about).setVisible(false);
        menu.findItem(R.id.about).setEnabled(false);
        super.onPrepareOptionsMenu(menu);
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
    }

    private void stopNotificationWorker() {
        WorkManager workManager = WorkManager.getInstance(requireContext().getApplicationContext());
        workManager.cancelUniqueWork(NotificationWorker.NOTIFICATION_WORKER_TAG);
    }

    private void startNotificationWorker() {
        PeriodicWorkRequest.Builder workBuilder = new PeriodicWorkRequest.Builder(NotificationWorker.class, 30, TimeUnit.MINUTES).setInitialDelay(15, TimeUnit.SECONDS).addTag(NotificationWorker.NOTIFICATION_WORKER_TAG);
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