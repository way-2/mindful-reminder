package com.example.mindful_reminder.service;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.mindful_reminder.R;
import com.example.mindful_reminder.activities.ActivityMain;

import java.util.Random;

public class NotificationWorker extends Worker {
    public static final String NOTIFICATION_WORKER_TAG = "NotificationWorker";

    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        int dndStatus = 0;
        try {
            dndStatus = Settings.Global.getInt(getApplicationContext().getContentResolver(), "zen_mode");
        } catch (Settings.SettingNotFoundException ex) {
            Log.w(NOTIFICATION_WORKER_TAG, "Unable to find zen_mode status");
        }
        if (dndStatus == 0) {
            Log.i(NOTIFICATION_WORKER_TAG, "Sending Notification for affirmation " + ActivityMain.affirmation);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), ActivityMain.NOTIFICATION_CHANNEL)
                    .setSmallIcon(R.drawable.happy_brain)
                    .setContentTitle(ActivityMain.affirmation)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
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
