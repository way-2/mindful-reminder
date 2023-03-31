package com.example.mindful_reminder.service;

import static com.example.mindful_reminder.config.Constants.DAILY_MINDFULNESS_ACTIVITY_SHARED_PREFERENCE;
import static com.example.mindful_reminder.config.Constants.DAILY_MINDFULNESS_REDIRECT;
import static com.example.mindful_reminder.config.Constants.NOTIFICATION_CHANNEL;
import static com.example.mindful_reminder.config.Constants.REDIRECT;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.preference.PreferenceManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.mindful_reminder.R;
import com.example.mindful_reminder.activities.ActivityMain;

import java.util.Arrays;
import java.util.Random;

public class MindfulnessActivityNotificationWorker extends Worker {
    private final SharedPreferences sharedPreferences;

    public MindfulnessActivityNotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    }

    @NonNull
    @Override
    public Result doWork() {
        int index = Arrays.asList(getApplicationContext().getResources().getStringArray(R.array.activity_name)).indexOf(sharedPreferences.getString(DAILY_MINDFULNESS_ACTIVITY_SHARED_PREFERENCE, ""));
        String activityDesc = getApplicationContext().getResources().getStringArray(R.array.activity_text)[index];
        Intent intent = new Intent(getApplicationContext(), ActivityMain.class);
        intent.putExtra(REDIRECT, DAILY_MINDFULNESS_REDIRECT);
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(getApplicationContext());
        taskStackBuilder.addNextIntentWithParentStack(intent);
        PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), NOTIFICATION_CHANNEL)
                .setSmallIcon(R.drawable.mindful_reminder_icon)
                .setContentTitle(sharedPreferences.getString(DAILY_MINDFULNESS_ACTIVITY_SHARED_PREFERENCE, ""))
                .setContentText(activityDesc)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "Notification Permission not granted", Toast.LENGTH_SHORT).show();
        } else {
            notificationManagerCompat.notify(new Random().nextInt(), builder.build());
        }
        return Result.success();
    }

}
