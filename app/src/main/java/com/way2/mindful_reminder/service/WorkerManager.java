package com.way2.mindful_reminder.service;

import static com.way2.mindful_reminder.config.Constants.ACTIVITY_NOTIFICATION_WORKER_TAG;
import static com.way2.mindful_reminder.config.Constants.AFFIRMATION_NOTIFICATION_TOGGLE;
import static com.way2.mindful_reminder.config.Constants.AFFIRMATION_NOTIFICATION_WORKER_TAG;
import static com.way2.mindful_reminder.config.Constants.DAILY_ACTIVITY_TAG;
import static com.way2.mindful_reminder.config.Constants.DAILY_ACTIVITY_TOGGLE;
import static com.way2.mindful_reminder.config.Constants.DAILY_NOTIFICATION_HOUR_LIST;
import static com.way2.mindful_reminder.config.Constants.MINDFULNESS_JOURNAL_NOTIFICATION_HOUR_LIST;
import static com.way2.mindful_reminder.config.Constants.MINDFULNESS_JOURNAL_NOTIFICATION_TOGGLE;
import static com.way2.mindful_reminder.config.Constants.MINDFULNESS_JOURNAL_NOTIFICATION_WORKER;
import static com.way2.mindful_reminder.config.Constants.NOTIFICATION_TIME_INTERVAL_LIST;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.Calendar;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class WorkerManager {

    private static WorkerManager workerManagerInstance;

    public static synchronized WorkerManager getInstance() {
        if (workerManagerInstance == null) {
            workerManagerInstance = new WorkerManager();
        }
        return workerManagerInstance;
    }

    public void cleanUp() {
        workerManagerInstance = null;
    }

    public void startGratitudeNotificationWorker(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (sharedPreferences.getBoolean(MINDFULNESS_JOURNAL_NOTIFICATION_TOGGLE, false)) {
            int notificationInterval = Integer.parseInt(sharedPreferences.getString(MINDFULNESS_JOURNAL_NOTIFICATION_HOUR_LIST, "20"));
            Calendar calendar = Calendar.getInstance();
            long nowMillis = calendar.getTimeInMillis();
            calendar.set(Calendar.HOUR_OF_DAY, notificationInterval);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            if (calendar.before(Calendar.getInstance())) {
                calendar.add(Calendar.DATE, 1);
            }
            long diff = calendar.getTimeInMillis() - nowMillis;
            PeriodicWorkRequest.Builder workBuilder = new PeriodicWorkRequest.Builder(MindfulnessJournalNotificationWorker.class, 24, TimeUnit.HOURS).setInitialDelay(diff, TimeUnit.MILLISECONDS).addTag(MINDFULNESS_JOURNAL_NOTIFICATION_WORKER);
            Constraints constraints = new Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                    .setRequiresCharging(false)
                    .setRequiresDeviceIdle(false)
                    .build();
            PeriodicWorkRequest runWork = workBuilder.setConstraints(constraints).build();
            WorkManager workManager = WorkManager.getInstance(context);
            workManager.enqueueUniquePeriodicWork(MINDFULNESS_JOURNAL_NOTIFICATION_WORKER, ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE, runWork);
        }
    }

    public void stopGratitudeNotificationWorker(Context context) {
        WorkManager workManager = WorkManager.getInstance(context);
        workManager.cancelUniqueWork(MINDFULNESS_JOURNAL_NOTIFICATION_WORKER);
    }

    public void startDailyActivityWorker(Context context) {
        Calendar calendar = Calendar.getInstance();
        long nowMillis = calendar.getTimeInMillis();
        calendar.set(Calendar.HOUR_OF_DAY, 1);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DATE, 1);
        }
        long diff = calendar.getTimeInMillis() - nowMillis;
        PeriodicWorkRequest.Builder workBuilder = new PeriodicWorkRequest.Builder(DailyWorker.class, 24, TimeUnit.HOURS).setInitialDelay(diff, TimeUnit.MILLISECONDS).addTag(DAILY_ACTIVITY_TAG).setId(UUID.randomUUID());
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .setRequiresCharging(false)
                .setRequiresDeviceIdle(false)
                .build();
        PeriodicWorkRequest runWork = workBuilder.setConstraints(constraints).build();
        WorkManager workManager = WorkManager.getInstance(context);
        workManager.enqueueUniquePeriodicWork(DAILY_ACTIVITY_TAG, ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE, runWork);
    }

    public void stopActivityNotificationWorker(Context context) {
        WorkManager workManager = WorkManager.getInstance(context);
        workManager.cancelUniqueWork(ACTIVITY_NOTIFICATION_WORKER_TAG);
    }

    public void stopNotificationWorker(Context context) {
        WorkManager workManager = WorkManager.getInstance(context);
        workManager.cancelUniqueWork(AFFIRMATION_NOTIFICATION_WORKER_TAG);
    }

    public void startMindfulnessNotificationWorker(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (sharedPreferences.getBoolean(DAILY_ACTIVITY_TOGGLE, false)) {
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
            long diff = calendar.getTimeInMillis() - nowMillis;
            PeriodicWorkRequest.Builder workBuilder = new PeriodicWorkRequest.Builder(MindfulnessActivityNotificationWorker.class, 24, TimeUnit.HOURS).setInitialDelay(diff, TimeUnit.MILLISECONDS).addTag(ACTIVITY_NOTIFICATION_WORKER_TAG);
            Constraints constraints = new Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                    .setRequiresCharging(false)
                    .setRequiresDeviceIdle(false)
                    .build();
            PeriodicWorkRequest runWork = workBuilder.setConstraints(constraints).build();
            WorkManager workManager = WorkManager.getInstance(context);
            workManager.enqueueUniquePeriodicWork(ACTIVITY_NOTIFICATION_WORKER_TAG, ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE, runWork);
        }
    }

    public void startAffirmationNotificationWorker(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (sharedPreferences.getBoolean(AFFIRMATION_NOTIFICATION_TOGGLE, false)) {
            long notificationInterval = Long.parseLong(sharedPreferences.getString(NOTIFICATION_TIME_INTERVAL_LIST, "30"));
            PeriodicWorkRequest.Builder workBuilder = new PeriodicWorkRequest.Builder(AffirmationNotificationWorker.class, notificationInterval, TimeUnit.MINUTES).setInitialDelay(15, TimeUnit.SECONDS).addTag(AFFIRMATION_NOTIFICATION_WORKER_TAG);
            Constraints constraints = new Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                    .setRequiresCharging(false)
                    .setRequiresDeviceIdle(false)
                    .build();
            PeriodicWorkRequest runWork = workBuilder.setConstraints(constraints).build();
            WorkManager workManager = WorkManager.getInstance(context);
            workManager.enqueueUniquePeriodicWork(AFFIRMATION_NOTIFICATION_WORKER_TAG, ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE, runWork);
        }
    }

    public void startMindfulnessNotificationWorkerAlways(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
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
        long diff = calendar.getTimeInMillis() - nowMillis;
        PeriodicWorkRequest.Builder workBuilder = new PeriodicWorkRequest.Builder(MindfulnessActivityNotificationWorker.class, 24, TimeUnit.HOURS).setInitialDelay(diff, TimeUnit.MILLISECONDS).addTag(ACTIVITY_NOTIFICATION_WORKER_TAG);
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .setRequiresCharging(false)
                .setRequiresDeviceIdle(false)
                .build();
        PeriodicWorkRequest runWork = workBuilder.setConstraints(constraints).build();
        WorkManager workManager = WorkManager.getInstance(context);
        workManager.enqueueUniquePeriodicWork(ACTIVITY_NOTIFICATION_WORKER_TAG, ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE, runWork);
    }

    public void startAffirmationNotificationWorkerAlways(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        long notificationInterval = Long.parseLong(sharedPreferences.getString(NOTIFICATION_TIME_INTERVAL_LIST, "30"));
        PeriodicWorkRequest.Builder workBuilder = new PeriodicWorkRequest.Builder(AffirmationNotificationWorker.class, notificationInterval, TimeUnit.MINUTES).setInitialDelay(15, TimeUnit.SECONDS).addTag(AFFIRMATION_NOTIFICATION_WORKER_TAG);
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .setRequiresCharging(false)
                .setRequiresDeviceIdle(false)
                .build();
        PeriodicWorkRequest runWork = workBuilder.setConstraints(constraints).build();
        WorkManager workManager = WorkManager.getInstance(context);
        workManager.enqueueUniquePeriodicWork(AFFIRMATION_NOTIFICATION_WORKER_TAG, ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE, runWork);
    }

    public void startGratitudeNotificationWorkerAlways(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        int notificationInterval = Integer.parseInt(sharedPreferences.getString(MINDFULNESS_JOURNAL_NOTIFICATION_HOUR_LIST, "20"));
        Calendar calendar = Calendar.getInstance();
        long nowMillis = calendar.getTimeInMillis();
        calendar.set(Calendar.HOUR_OF_DAY, notificationInterval);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DATE, 1);
        }
        long diff = calendar.getTimeInMillis() - nowMillis;
        PeriodicWorkRequest.Builder workBuilder = new PeriodicWorkRequest.Builder(MindfulnessJournalNotificationWorker.class, 24, TimeUnit.HOURS).setInitialDelay(diff, TimeUnit.MILLISECONDS).addTag(MINDFULNESS_JOURNAL_NOTIFICATION_WORKER);
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .setRequiresCharging(false)
                .setRequiresDeviceIdle(false)
                .build();
        PeriodicWorkRequest runWork = workBuilder.setConstraints(constraints).build();
        WorkManager workManager = WorkManager.getInstance(context);
        workManager.enqueueUniquePeriodicWork(MINDFULNESS_JOURNAL_NOTIFICATION_WORKER, ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE, runWork);
    }

}
