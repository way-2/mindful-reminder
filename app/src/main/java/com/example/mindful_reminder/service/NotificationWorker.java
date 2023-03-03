package com.example.mindful_reminder.service;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.mindful_reminder.R;
import com.example.mindful_reminder.activities.GetMotivation;

import java.util.Random;

public class NotificationWorker extends Worker {
    public static final String NOTIFICATION_WORKER_TAG = "NotificationWorker";

    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
//        Log.i(NOTIFICATION_WORKER_TAG, "Sending Notification for affirmation " + GetAffirmationWorker.affirmation);
        Log.i(NOTIFICATION_WORKER_TAG, "Sending Notification for affirmation " + GetMotivation.affirmation);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), GetMotivation.NOTIFICATION_CHANNEL)
                .setSmallIcon(R.drawable.happy_brain)
                .setContentTitle(GetMotivation.affirmation)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        notificationManagerCompat.notify(new Random().nextInt(), builder.build());
        return Result.success();
    }

}
