package com.example.mindful_reminder.service;

import static com.example.mindful_reminder.config.Constants.MINDFULNESS_JOURNAL_NOTIFICATION_WORKER;
import static com.example.mindful_reminder.config.Constants.MINDFULNESS_JOURNAL_REDIRECT;
import static com.example.mindful_reminder.config.Constants.NOTIFICATION_CHANNEL;
import static com.example.mindful_reminder.config.Constants.REDIRECT;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.mindful_reminder.R;
import com.example.mindful_reminder.activities.ActivityMain;
import com.example.mindful_reminder.databases.AppDatabase;
import com.example.mindful_reminder.entities.JournalEntry;

import java.time.LocalDate;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class MindfulnessJournalNotificationWorker extends Worker {

    private AppDatabase database;

    public MindfulnessJournalNotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        if (!isJournalEntryDone()) {
            Log.i(MINDFULNESS_JOURNAL_NOTIFICATION_WORKER, "Sending Notification Gratitude Journal Reminder");
            Intent intent = new Intent(getApplicationContext(), ActivityMain.class);
            intent.putExtra(REDIRECT, MINDFULNESS_JOURNAL_REDIRECT);
            TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(getApplicationContext());
            taskStackBuilder.addNextIntentWithParentStack(intent);
            PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), NOTIFICATION_CHANNEL)
                    .setSmallIcon(R.drawable.mindful_reminder_icon)
                    .setContentTitle("Don't forget to take a few moments to reflect on what you are grateful for today!")
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

    private boolean isJournalEntryDone() {
        boolean isDone = false;
        try {
            database = AppDatabase.getInstance(getApplicationContext());
            JournalEntry todaysEntry = database.gratitudeJournalDao().getEntryForDate(LocalDate.now()).get();
            if ((null != todaysEntry) && ((todaysEntry.getGratitudeEntry().length() > 0) || (todaysEntry.getRuminationEntry().length() > 0))) {
                isDone = true;
            }
        } catch (ExecutionException | InterruptedException ex) {
            ex.printStackTrace();
        } finally {
            database.cleanUp();
        }
        return isDone;
    }

}
