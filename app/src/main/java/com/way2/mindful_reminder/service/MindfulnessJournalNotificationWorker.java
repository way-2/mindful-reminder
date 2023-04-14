package com.way2.mindful_reminder.service;

import static com.way2.mindful_reminder.config.Constants.MINDFULNESS_JOURNAL_NOTIFICATION_ID;
import static com.way2.mindful_reminder.config.Constants.MINDFULNESS_JOURNAL_REDIRECT;
import static com.way2.mindful_reminder.config.Constants.NOTIFICATION_CHANNEL;
import static com.way2.mindful_reminder.config.Constants.REDIRECT;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.way2.mindful_reminder.R;
import com.way2.mindful_reminder.activities.ActivityMain;
import com.way2.mindful_reminder.databases.AppDatabase;
import com.way2.mindful_reminder.entities.JournalEntry;
import com.way2.mindful_reminder.util.MindfulReminder;

import java.time.LocalDate;
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
            Intent intent = new Intent(MindfulReminder.getContext(), ActivityMain.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(REDIRECT, MINDFULNESS_JOURNAL_REDIRECT);
            TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(MindfulReminder.getContext());
            taskStackBuilder.addNextIntentWithParentStack(intent);
            PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(MindfulReminder.getContext(), NOTIFICATION_CHANNEL)
                    .setSmallIcon(R.drawable.mindful_reminder_icon)
                    .setContentTitle("Don't forget to take a few moments to reflect on what you are grateful for today!")
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(MindfulReminder.getContext());
            if (ActivityCompat.checkSelfPermission(MindfulReminder.getContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MindfulReminder.getContext(), "Notification Permission not granted", Toast.LENGTH_SHORT).show();
            } else {
                notificationManagerCompat.notify(MINDFULNESS_JOURNAL_NOTIFICATION_ID, builder.build());
            }
        }
        return Result.success();
    }

    private boolean isJournalEntryDone() {
        boolean isDone = false;
        try {
            database = AppDatabase.getInstance();
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
