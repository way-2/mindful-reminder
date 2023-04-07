package com.way2.mindful_reminder.service;

import static com.way2.mindful_reminder.config.Constants.AFFIRMATION_NOTIFICATION_WORKER_TAG;
import static com.way2.mindful_reminder.config.Constants.AFFIRMATION_SHARED_PREFERENCE;
import static com.way2.mindful_reminder.config.Constants.NOTIFICATION_CHANNEL;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.preference.PreferenceManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.way2.mindful_reminder.R;
import com.way2.mindful_reminder.activities.ActivityMain;

import java.util.Random;

public class AffirmationNotificationWorker extends Worker {
    private final SharedPreferences sharedPreferences;

    public AffirmationNotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    }

    @NonNull
    @Override
    public Result doWork() {
        int dndStatus = 0;
        try {
            dndStatus = Settings.Global.getInt(getApplicationContext().getContentResolver(), "zen_mode");
        } catch (Settings.SettingNotFoundException ex) {
            Log.w(AFFIRMATION_NOTIFICATION_WORKER_TAG, "Unable to find zen_mode status");
        }
        if (dndStatus == 0) {
            Log.i(AFFIRMATION_NOTIFICATION_WORKER_TAG, "Sending Notification for affirmation " + sharedPreferences.getString(AFFIRMATION_SHARED_PREFERENCE, ""));
            Intent intent = new Intent(getApplicationContext(), ActivityMain.class);
            TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(getApplicationContext());
            taskStackBuilder.addNextIntentWithParentStack(intent);
            PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), NOTIFICATION_CHANNEL)
                    .setSmallIcon(R.drawable.mindful_reminder_icon)
                    .setContentTitle(sharedPreferences.getString(AFFIRMATION_SHARED_PREFERENCE, ""))
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "Notification Permission not granted", Toast.LENGTH_SHORT).show();
            } else {
                notificationManagerCompat.notify(new Random().nextInt(), builder.build());
            }
        }
        return Result.success();
    }

}
