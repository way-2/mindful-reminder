package com.way2.mindful_reminder.service;

import static com.way2.mindful_reminder.config.Constants.DAILY_MINDFULNESS_ACTIVITY_SHARED_PREFERENCE;
import static com.way2.mindful_reminder.config.Constants.DAILY_MINDFULNESS_REDIRECT;
import static com.way2.mindful_reminder.config.Constants.MINDFULNESS_ACTIVITY_NOTIFICATION_ID;
import static com.way2.mindful_reminder.config.Constants.NOTIFICATION_CHANNEL;
import static com.way2.mindful_reminder.config.Constants.REDIRECT;

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

import com.way2.mindful_reminder.R;
import com.way2.mindful_reminder.activities.ActivityMain;
import com.way2.mindful_reminder.util.MindfulReminder;

import java.util.Arrays;

public class MindfulnessActivityNotificationWorker extends Worker {
    private final SharedPreferences sharedPreferences;

    public MindfulnessActivityNotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MindfulReminder.getContext());
    }

    @NonNull
    @Override
    public Result doWork() {
        int index = Arrays.asList(MindfulReminder.getContext().getResources().getStringArray(R.array.activity_name)).indexOf(sharedPreferences.getString(DAILY_MINDFULNESS_ACTIVITY_SHARED_PREFERENCE, ""));
        String activityDesc = MindfulReminder.getContext().getResources().getStringArray(R.array.activity_text)[index];
        Intent intent = new Intent(MindfulReminder.getContext(), ActivityMain.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(REDIRECT, DAILY_MINDFULNESS_REDIRECT);
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(MindfulReminder.getContext());
        taskStackBuilder.addNextIntentWithParentStack(intent);
        PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(MindfulReminder.getContext(), NOTIFICATION_CHANNEL)
                .setSmallIcon(R.drawable.mindful_reminder_icon)
                .setContentTitle(sharedPreferences.getString(DAILY_MINDFULNESS_ACTIVITY_SHARED_PREFERENCE, ""))
                .setContentText(activityDesc)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(MindfulReminder.getContext());
        if (ActivityCompat.checkSelfPermission(MindfulReminder.getContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(MindfulReminder.getContext(), "Notification Permission not granted", Toast.LENGTH_SHORT).show();
        } else {
            notificationManagerCompat.notify(MINDFULNESS_ACTIVITY_NOTIFICATION_ID, builder.build());
        }
        return Result.success();
    }

}
