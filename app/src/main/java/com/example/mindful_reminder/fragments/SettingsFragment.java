package com.example.mindful_reminder.fragments;

import static com.example.mindful_reminder.config.Constants.ACTIVITY_NOTIFICATION_WORKER_TAG;
import static com.example.mindful_reminder.config.Constants.AFFIRMATION_NOTIFICATION_TOGGLE;
import static com.example.mindful_reminder.config.Constants.AFFIRMATION_NOTIFICATION_WORKER_TAG;
import static com.example.mindful_reminder.config.Constants.DAILY_ACTIVITY_TOGGLE;
import static com.example.mindful_reminder.config.Constants.DAILY_NOTIFICATION_HOUR_LIST;
import static com.example.mindful_reminder.config.Constants.GRATITUDE_NOTIFICATION_HOUR_LIST;
import static com.example.mindful_reminder.config.Constants.GRATITUDE_NOTIFICATION_TOGGLE;
import static com.example.mindful_reminder.config.Constants.GRATITUDE_NOTIFICATION_WORKER;
import static com.example.mindful_reminder.config.Constants.NOTIFICATION_TIME_INTERVAL_LIST;

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
import com.example.mindful_reminder.service.GratitudeNotificationWorker;
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
    private SwitchPreferenceCompat gratitudeReminderSwitch;
    private ListPreference gratitudeReminderHourListPreference;
    private SharedPreferences sharedPreferences;

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
        getUiElements();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void getUiElements() {
        affirmationSettings();
        activitySettings();
        gratitudeSettings();
    }

    private void gratitudeSettings() {
        gratitudeReminderSwitch = (SwitchPreferenceCompat) findPreference(GRATITUDE_NOTIFICATION_TOGGLE);
        gratitudeReminderHourListPreference = (ListPreference) findPreference(GRATITUDE_NOTIFICATION_HOUR_LIST);
        if (gratitudeReminderSwitch.isChecked()) {
            gratitudeReminderHourListPreference.setEnabled(true);
        }
        gratitudeReminderSwitch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
                if ((Boolean) newValue) {
                    startGratitudeNotificationWorker();
                    gratitudeReminderHourListPreference.setEnabled(true);
                } else {
                    stopGratitudeNotificationWorker();
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
                    List<WorkInfo> workInfo = WorkManager.getInstance(requireContext()).getWorkInfosByTag(GRATITUDE_NOTIFICATION_WORKER).get();
                    if (!workInfo.isEmpty()) {
                        WorkInfo.State state = workInfo.get(0).getState();
                        if ((state == WorkInfo.State.RUNNING) || (state == WorkInfo.State.ENQUEUED)) {
                            startGratitudeNotificationWorker();
                        }
                    }
                } catch(ExecutionException | InterruptedException ex){
                    ex.printStackTrace();
                }
                return true;
            }
        });
    }

    private void startGratitudeNotificationWorker() {
        int notificationInterval = Integer.parseInt(sharedPreferences.getString(GRATITUDE_NOTIFICATION_HOUR_LIST, "20"));
        Calendar calendar = Calendar.getInstance();
        long nowMillis = calendar.getTimeInMillis();
        calendar.set(Calendar.HOUR_OF_DAY, notificationInterval);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DATE, 1);
        }
        Log.i(GRATITUDE_NOTIFICATION_WORKER, "Setting gratitude notification to daily at " + calendar.getTime());
        long diff = calendar.getTimeInMillis() - nowMillis;
        PeriodicWorkRequest.Builder workBuilder = new PeriodicWorkRequest.Builder(GratitudeNotificationWorker.class, 24, TimeUnit.HOURS).setInitialDelay(diff, TimeUnit.MILLISECONDS).addTag(GRATITUDE_NOTIFICATION_WORKER);
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .setRequiresCharging(false)
                .setRequiresDeviceIdle(false)
                .build();
        PeriodicWorkRequest runWork = workBuilder.setConstraints(constraints).build();
        WorkManager workManager = WorkManager.getInstance(requireContext().getApplicationContext());
        workManager.enqueueUniquePeriodicWork(GRATITUDE_NOTIFICATION_WORKER, ExistingPeriodicWorkPolicy.UPDATE, runWork);
    }

    private void stopGratitudeNotificationWorker() {
        WorkManager workManager = WorkManager.getInstance(requireContext().getApplicationContext());
        workManager.cancelUniqueWork(GRATITUDE_NOTIFICATION_WORKER);

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
                    startMindfulnessNotificationWorker();
                    dailyReminderHourListPreference.setEnabled(true);
                } else {
                    stopActivityNotificationWorker();
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
                    startNotificationWorker();
                    notificationIntervalListPreference.setEnabled(true);
                } else {
                    stopNotificationWorker();
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

    private void startMindfulnessNotificationWorker() {
        int notificationInterval = Integer.parseInt(sharedPreferences.getString(DAILY_NOTIFICATION_HOUR_LIST, "8"));
        Calendar calendar = Calendar.getInstance();
        long nowMillis = calendar.getTimeInMillis();
        calendar.set(Calendar.HOUR_OF_DAY, notificationInterval);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DATE, 1);
        }
        Log.i(ACTIVITY_NOTIFICATION_WORKER_TAG, "Setting activity notification to daily at " + calendar.getTime());
        long diff = calendar.getTimeInMillis() - nowMillis;
        PeriodicWorkRequest.Builder workBuilder = new PeriodicWorkRequest.Builder(MindfulnessActivityNotificationWorker.class, 24, TimeUnit.HOURS).setInitialDelay(diff, TimeUnit.MILLISECONDS).addTag(ACTIVITY_NOTIFICATION_WORKER_TAG);
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .setRequiresCharging(false)
                .setRequiresDeviceIdle(false)
                .build();
        PeriodicWorkRequest runWork = workBuilder.setConstraints(constraints).build();
        WorkManager workManager = WorkManager.getInstance(requireContext().getApplicationContext());
        workManager.enqueueUniquePeriodicWork(ACTIVITY_NOTIFICATION_WORKER_TAG, ExistingPeriodicWorkPolicy.UPDATE, runWork);
    }

    private void stopActivityNotificationWorker() {
        WorkManager workManager = WorkManager.getInstance(requireContext().getApplicationContext());
        workManager.cancelUniqueWork(ACTIVITY_NOTIFICATION_WORKER_TAG);
    }

    private void stopNotificationWorker() {
        WorkManager workManager = WorkManager.getInstance(requireContext().getApplicationContext());
        workManager.cancelUniqueWork(AFFIRMATION_NOTIFICATION_WORKER_TAG);
    }

    private void startNotificationWorker() {
        long notificationInterval = Long.parseLong(sharedPreferences.getString(NOTIFICATION_TIME_INTERVAL_LIST, "30"));
        Log.i("notificationSetting", "Setting notifications to every " + notificationInterval + " minutes");
        PeriodicWorkRequest.Builder workBuilder = new PeriodicWorkRequest.Builder(AffirmationNotificationWorker.class, notificationInterval, TimeUnit.MINUTES).setInitialDelay(15, TimeUnit.SECONDS).addTag(AFFIRMATION_NOTIFICATION_WORKER_TAG);
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .setRequiresCharging(false)
                .setRequiresDeviceIdle(false)
                .build();
        PeriodicWorkRequest runWork = workBuilder.setConstraints(constraints).build();
        WorkManager workManager = WorkManager.getInstance(requireContext().getApplicationContext());
        workManager.enqueueUniquePeriodicWork(AFFIRMATION_NOTIFICATION_WORKER_TAG, ExistingPeriodicWorkPolicy.KEEP, runWork);
    }

}