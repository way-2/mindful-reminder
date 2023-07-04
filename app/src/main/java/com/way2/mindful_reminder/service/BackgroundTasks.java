package com.way2.mindful_reminder.service;

import static com.way2.mindful_reminder.config.Constants.NOTIFICATION_CHANNEL;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.way2.mindful_reminder.BuildConfig;
import com.way2.mindful_reminder.R;
import com.way2.mindful_reminder.activities.ActivityMain;
import com.way2.mindful_reminder.util.MindfulReminder;

public class BackgroundTasks {

    private ActivityMain activityMain;

    public void setContext(ActivityMain activityMain) {
        this.activityMain = activityMain;
    }

    public final Runnable activityMainStartupTasks = () -> {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkPermissions();
        }
        createNotificationChannel();
        setupWorkerManager();
    };
    public final Runnable activityMainUiSetup = () -> {
        String versionString = "Version " + BuildConfig.VERSION_NAME;
        activityMain.updateTextView(R.id.version_text_view, versionString);
    };

    private void setupWorkerManager() {
        WorkerManager workerManager = WorkerManager.getInstance();
        workerManager.stopActivityNotificationWorker();
        workerManager.startAffirmationNotificationWorker();
        workerManager.startGratitudeNotificationWorker();
        workerManager.startDailyActivityWorker();
        workerManager.cleanUp();
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private void checkPermissions() {
        String[] requestedPermissions = new String[]{Manifest.permission.POST_NOTIFICATIONS};
        for (String s: requestedPermissions) {
            if (ActivityCompat.checkSelfPermission(MindfulReminder.getContext(), s) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this.activityMain, new String[]{s}, 0);
            }
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL, NOTIFICATION_CHANNEL, importance);
            channel.setDescription("Notification channel used by mindful reminder");
            NotificationManager notificationManager = MindfulReminder.getContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}
