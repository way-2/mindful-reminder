package com.way2.mindful_reminder.fragments;

import static com.way2.mindful_reminder.config.Constants.AFFIRMATION_NOTIFICATION_TOGGLE;
import static com.way2.mindful_reminder.config.Constants.AFFIRMATION_NOTIFICATION_WORKER_TAG;
import static com.way2.mindful_reminder.config.Constants.MINDFULNESS_JOURNAL_NOTIFICATION_HOUR_LIST;
import static com.way2.mindful_reminder.config.Constants.MINDFULNESS_JOURNAL_NOTIFICATION_TOGGLE;
import static com.way2.mindful_reminder.config.Constants.MINDFULNESS_JOURNAL_NOTIFICATION_WORKER;
import static com.way2.mindful_reminder.config.Constants.NOTIFICATION_TIME_INTERVAL_LIST;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.way2.mindful_reminder.R;
import com.way2.mindful_reminder.databases.AppDatabase;
import com.way2.mindful_reminder.entities.JournalEntry;
import com.way2.mindful_reminder.service.WorkerManager;
import com.way2.mindful_reminder.util.GsonLocalDateAdapter;
import com.way2.mindful_reminder.util.MindfulReminder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import kotlin.io.ByteStreamsKt;

public class SettingsFragment extends PreferenceFragmentCompat {

    private ListPreference notificationIntervalListPreference;
    private ListPreference gratitudeReminderHourListPreference;
    private WorkerManager workerManager;
    private Gson gson;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MindfulReminder.getContext());
        addPreferencesFromResource(R.xml.root_preferences);
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        workerManager = WorkerManager.getInstance();
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").registerTypeAdapter(LocalDate.class, new GsonLocalDateAdapter()).create();
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
        gratitudeSettings();
        setupButtons();
    }

    private void setupButtons() {
        Preference exportButton = findPreference(getString(R.string.export_button));
        Preference importButton = findPreference(getString(R.string.import_button));
        exportButton.setOnPreferenceClickListener(getExportBehavior());
        importButton.setOnPreferenceClickListener(getImportBehavior());
    }

    private Preference.OnPreferenceClickListener getExportBehavior() {
        return preference -> {
            createFile();
            return true;
        };
    }

    private void createFile() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/json");
        intent.putExtra(Intent.EXTRA_TITLE, "my_journal_entries.json");
        createFileActivityResultLauncher.launch(intent);
    }

    final ActivityResultLauncher<Intent> createFileActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if ((result.getResultCode() == Activity.RESULT_OK) && (null != result.getData())) {
                        Intent data = result.getData();
                        try (OutputStream outputStream = MindfulReminder.getInstance().getContentResolver().openOutputStream(data.getData())) {
                            AppDatabase database = null;
                            List<JournalEntry> journalEntries = new ArrayList<>();
                            try {
                                database = AppDatabase.getInstance();
                                journalEntries = database.gratitudeJournalDao().getAllEntries().get();
                            } catch (ExecutionException | InterruptedException ex) {
                                ex.printStackTrace();
                            } finally {
                                database.cleanUp();
                            }
                            outputStream.write(gson.toJson(journalEntries).getBytes());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
    );

    private File getFilePath() {
        return MindfulReminder.getInstance().getExternalFilesDir(null);
    }

    private Preference.OnPreferenceClickListener getImportBehavior() {
        return preference -> {
            openFile();
            return true;
        };
    }

    private void openFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/json");
        intent.putExtra(Intent.EXTRA_TITLE, "my_journal_entries.json");
        openFileActivityResultLauncher.launch(intent);
    }

    final ActivityResultLauncher<Intent> openFileActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if ((result.getResultCode() == Activity.RESULT_OK) && (null != result.getData())) {
                        Intent data = result.getData();
                        File file = new File(data.getData().getPath());
                        try (InputStream inputStream = MindfulReminder.getInstance().getContentResolver().openInputStream(data.getData())) {
                            byte[] bytes = ByteStreamsKt.readBytes(inputStream);
                            String jsonString = new String(bytes);
                            List<JournalEntry> journalEntries = Arrays.asList(gson.fromJson(jsonString, JournalEntry[].class));
                            AppDatabase database = null;
                            try {
                                database = AppDatabase.getInstance();
                                database.gratitudeJournalDao().insertGratitudeJournalEntries(journalEntries);
                            } finally {
                                database.cleanUp();
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
    );

    private void gratitudeSettings() {
        SwitchPreferenceCompat gratitudeReminderSwitch = findPreference(MINDFULNESS_JOURNAL_NOTIFICATION_TOGGLE);
        gratitudeReminderHourListPreference = findPreference(MINDFULNESS_JOURNAL_NOTIFICATION_HOUR_LIST);
        if (gratitudeReminderSwitch.isChecked()) {
            gratitudeReminderHourListPreference.setEnabled(true);
        }
        gratitudeReminderSwitch.setOnPreferenceChangeListener((preference, newValue) -> {
            if ((Boolean) newValue) {
                workerManager.startGratitudeNotificationWorkerAlways();
                gratitudeReminderHourListPreference.setEnabled(true);
            } else {
                workerManager.stopGratitudeNotificationWorker();
                gratitudeReminderHourListPreference.setEnabled(false);
            }
            return true;
        });
        gratitudeReminderHourListPreference.setSummary(gratitudeReminderHourListPreference.getEntry());
        gratitudeReminderHourListPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            String textValue = newValue.toString();
            int index = gratitudeReminderHourListPreference.findIndexOfValue(textValue);
            String entryString = gratitudeReminderHourListPreference.getEntries()[index].toString();
            gratitudeReminderHourListPreference.setSummary(entryString);
            try {
                List<WorkInfo> workInfo = WorkManager.getInstance(MindfulReminder.getContext()).getWorkInfosByTag(MINDFULNESS_JOURNAL_NOTIFICATION_WORKER).get();
                if (!workInfo.isEmpty()) {
                    WorkInfo.State state = workInfo.get(0).getState();
                    if ((state == WorkInfo.State.RUNNING) || (state == WorkInfo.State.ENQUEUED)) {
                        workerManager.startGratitudeNotificationWorker();
                    }
                }
            } catch (ExecutionException | InterruptedException ex) {
                ex.printStackTrace();
            }
            return true;
        });
    }

    private void affirmationSettings() {
        SwitchPreferenceCompat notificationSwitch = findPreference(AFFIRMATION_NOTIFICATION_TOGGLE);
        notificationIntervalListPreference = findPreference(NOTIFICATION_TIME_INTERVAL_LIST);
        if (notificationSwitch.isChecked()) {
            notificationIntervalListPreference.setEnabled(true);
        }
        notificationSwitch.setOnPreferenceChangeListener((preference, newValue) -> {
            if ((Boolean) newValue) {
                workerManager.startAffirmationNotificationWorkerAlways();
                notificationIntervalListPreference.setEnabled(true);
            } else {
                workerManager.stopNotificationWorker();
                notificationIntervalListPreference.setEnabled(false);
            }
            return true;
        });
        notificationIntervalListPreference.setSummary(notificationIntervalListPreference.getEntry());
        notificationIntervalListPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            String textValue = newValue.toString();
            int index = notificationIntervalListPreference.findIndexOfValue(textValue);
            String entryString = notificationIntervalListPreference.getEntries()[index].toString();
            notificationIntervalListPreference.setSummary(entryString);
            try {
                List<WorkInfo> workInfo = WorkManager.getInstance(MindfulReminder.getContext()).getWorkInfosByTag(AFFIRMATION_NOTIFICATION_WORKER_TAG).get();
                if (!workInfo.isEmpty()) {
                    WorkInfo.State state = workInfo.get(0).getState();
                    if ((state == WorkInfo.State.RUNNING) || (state == WorkInfo.State.ENQUEUED)) {
                        workerManager.startAffirmationNotificationWorker();
                    }
                }
            } catch (ExecutionException | InterruptedException ex) {
                ex.printStackTrace();
            }
            return true;
        });
    }

}