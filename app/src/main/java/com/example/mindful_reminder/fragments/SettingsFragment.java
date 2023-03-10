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
import com.example.mindful_reminder.service.AffirmationNotificationWorker;
import com.example.mindful_reminder.service.MindfulnessActivityNotificationWorker;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class SettingsFragment extends PreferenceFragmentCompat {

    private SwitchPreferenceCompat notificationSwitch;
    private ListPreference notificationIntervalListPreference;
    private SwitchPreferenceCompat dailyReminderSwitch;
    private ListPreference dailyReminderHourListPreference;
    private SharedPreferences sharedPreferences;
    private static final String NOTIFICATION_TOGGLE_SWITCH_KEY = "notification_toggle";
    private static final String DAILY_ACTIVITY_TOGGLE_SWITCH_KEY = "daily_activity_notification_toggle";
    private static final String NOTIFICATION_TIME_INTERVAL_LIST = "notification_time_interval_list";
    private static final String DAILY_NOTIFICATION_HOUR_LIST = "daily_notification_hour_list";
    private static final String NOTIFICATION_INTERVAL_LONG = "notification_interval_long";
    private static final String DAILY_REMINDER_HOUR = "daily_reminder_hour";

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
        dailyReminderSwitch = (SwitchPreferenceCompat) findPreference(DAILY_ACTIVITY_TOGGLE_SWITCH_KEY);
        notificationIntervalListPreference = (ListPreference) findPreference(NOTIFICATION_TIME_INTERVAL_LIST);
        dailyReminderHourListPreference = (ListPreference) findPreference(DAILY_NOTIFICATION_HOUR_LIST);
        if (notificationSwitch.isChecked()) {
            notificationIntervalListPreference.setEnabled(true);
        }
        if (dailyReminderSwitch.isChecked()) {
            dailyReminderHourListPreference.setEnabled(true);
        }
        notificationSwitch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
                if ((Boolean) newValue) {
                    startNotificationWorker();
                    notificationIntervalListPreference.setEnabled(true);
                } else {
                    stopNotificationWorker();
                    notificationIntervalListPreference.setEnabled(false);
                }
                return true;
            }
        });
        dailyReminderSwitch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
                if ((Boolean) newValue) {
                    startMindfulnessNotificationWorker();
                    dailyReminderHourListPreference.setEnabled(true);

                } else {
                    stopActivityNotificationWorker();
                    dailyReminderHourListPreference.setEnabled(false);
                }
                return true;
            }
        });
        if (!sharedPreferences.contains(NOTIFICATION_INTERVAL_LONG)) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putLong(NOTIFICATION_INTERVAL_LONG, Long.parseLong(notificationIntervalListPreference.getValue()));
            editor.apply();
        }
        if (!sharedPreferences.contains(DAILY_REMINDER_HOUR)) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(DAILY_REMINDER_HOUR, Integer.parseInt(dailyReminderHourListPreference.getValue()));
            editor.apply();
        }
        notificationIntervalListPreference.setSummary(notificationIntervalListPreference.getEntry());
        dailyReminderHourListPreference.setSummary(dailyReminderHourListPreference.getEntry());
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
                    List<WorkInfo> workInfo = WorkManager.getInstance(requireContext()).getWorkInfosByTag(AffirmationNotificationWorker.AFFIRMATION_NOTIFICATION_WORKER_TAG).get();
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
        dailyReminderHourListPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
                String textValue = newValue.toString();
                int index = dailyReminderHourListPreference.findIndexOfValue(textValue);
                String entryString = dailyReminderHourListPreference.getEntries()[index].toString();
                String entryValue = dailyReminderHourListPreference.getEntryValues()[index].toString();
                int intValue = Integer.parseInt(entryValue);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(DAILY_REMINDER_HOUR, intValue);
                editor.apply();
                dailyReminderHourListPreference.setSummary(entryString);
                try {
                    List<WorkInfo> workInfo = WorkManager.getInstance(requireContext()).getWorkInfosByTag(MindfulnessActivityNotificationWorker.ACTIVITY_NOTIFICATION_WORKER_TAG).get();
                    if (!workInfo.isEmpty()) {
                        WorkInfo.State state = workInfo.get(0).getState();
                        if ((state == WorkInfo.State.RUNNING) || (state == WorkInfo.State.ENQUEUED)) {
                            startMindfulnessNotificationWorker();
                        }
                    }
                } catch(ExecutionException | InterruptedException ex){
                    ex.printStackTrace();
                }
                return true;
            }
        });
    }

    private void startMindfulnessNotificationWorker() {
        int notificationInterval = sharedPreferences.getInt(DAILY_REMINDER_HOUR, Integer.parseInt("8"));
        Log.i("notificationSetting", "Setting notifications to every " + notificationInterval + " minutes");
        Calendar calendar = Calendar.getInstance();
        long nowMillis = calendar.getTimeInMillis();
//        calendar.set(Calendar.HOUR_OF_DAY, notificationInterval);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DATE, 1);
        }
        long diff = calendar.getTimeInMillis() - nowMillis;
        PeriodicWorkRequest.Builder workBuilder = new PeriodicWorkRequest.Builder(MindfulnessActivityNotificationWorker.class, 24, TimeUnit.HOURS).setInitialDelay(diff, TimeUnit.SECONDS).addTag(MindfulnessActivityNotificationWorker.ACTIVITY_NOTIFICATION_WORKER_TAG);
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresCharging(false)
                .setRequiresDeviceIdle(false)
                .build();
        PeriodicWorkRequest runWork = workBuilder.setConstraints(constraints).build();
        WorkManager workManager = WorkManager.getInstance(requireContext().getApplicationContext());
        workManager.enqueueUniquePeriodicWork(MindfulnessActivityNotificationWorker.ACTIVITY_NOTIFICATION_WORKER_TAG, ExistingPeriodicWorkPolicy.KEEP, runWork);
    }

    private void stopActivityNotificationWorker() {
        WorkManager workManager = WorkManager.getInstance(requireContext().getApplicationContext());
        workManager.cancelUniqueWork(MindfulnessActivityNotificationWorker.ACTIVITY_NOTIFICATION_WORKER_TAG);
    }

    private void stopNotificationWorker() {
        WorkManager workManager = WorkManager.getInstance(requireContext().getApplicationContext());
        workManager.cancelUniqueWork(AffirmationNotificationWorker.AFFIRMATION_NOTIFICATION_WORKER_TAG);
    }

    private void startNotificationWorker() {
        long notificationInterval = sharedPreferences.getLong(NOTIFICATION_INTERVAL_LONG, 30);
        Log.i("notificationSetting", "Setting notifications to every " + notificationInterval + " minutes");
        PeriodicWorkRequest.Builder workBuilder = new PeriodicWorkRequest.Builder(AffirmationNotificationWorker.class, notificationInterval, TimeUnit.MINUTES).setInitialDelay(15, TimeUnit.SECONDS).addTag(AffirmationNotificationWorker.AFFIRMATION_NOTIFICATION_WORKER_TAG);
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresCharging(false)
                .setRequiresDeviceIdle(false)
                .build();
        PeriodicWorkRequest runWork = workBuilder.setConstraints(constraints).build();
        WorkManager workManager = WorkManager.getInstance(requireContext().getApplicationContext());
        workManager.enqueueUniquePeriodicWork(AffirmationNotificationWorker.AFFIRMATION_NOTIFICATION_WORKER_TAG, ExistingPeriodicWorkPolicy.KEEP, runWork);
    }

}