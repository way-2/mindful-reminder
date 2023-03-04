package com.example.mindful_reminder.activities;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.example.mindful_reminder.R;
import com.example.mindful_reminder.fragments.AboutFragment;
import com.example.mindful_reminder.fragments.HelpFragment;
import com.example.mindful_reminder.service.GetAffirmationWorker;
import com.example.mindful_reminder.service.NotificationWorker;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ActivityMain extends AppCompatActivity {

    private static final String TAG = ActivityMain.class.getSimpleName();
    public static final String NOTIFICATION_CHANNEL = "MINDFUL_REMINDER";
    private TextView affirmationTextView;
    private UUID getAffirmationUuid;
    private SwitchCompat switchCompat;
    public static String affirmation;
    private MenuItem aboutMenuItem;
    private MenuItem helpMenuItem;
    private HelpFragment helpFragment;
    private AboutFragment aboutFragment;
    private AppCompatButton skipButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermissions();
        createNotificationChannel();
        setContentView(R.layout.activity_main);
        setupUi();
    }

    private void checkPermissions() {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 0);
        }
    }

    private void updateAffirmationUi() {
        WorkManager workManager = WorkManager.getInstance(getApplicationContext());
        workManager.getWorkInfoByIdLiveData(getAffirmationUuid).observe(this, new Observer<WorkInfo>() {
            @Override
            public void onChanged(WorkInfo workInfo) {
                if (null != workInfo && workInfo.getState().equals(WorkInfo.State.RUNNING)) {
                    GetAffirmationWorker.affirmationObservable.observe(ActivityMain.this,
                            new Observer<String>() {
                                @Override
                                public void onChanged(String s) {
                                    workManager.getWorkInfosForUniqueWorkLiveData(GetAffirmationWorker.GET_AFFIRMATION_TAG).removeObservers(ActivityMain.this);
                                    affirmation = s;
                                    affirmationTextView.setText(affirmation);
                                }
                            });
                }
            }
        });
    }

    private void setupUi() {
        affirmationTextView = (TextView) requireViewById(R.id.affirmation);
        skipButton = (AppCompatButton) requireViewById(R.id.skip_button);
        runAffirmationOneTime();
        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runAffirmationOneTime();
            }
        });
    }

    private void runAffirmationOneTime() {
        WorkRequest workRequest = OneTimeWorkRequest.from(GetAffirmationWorker.class);
        WorkManager workManager = WorkManager.getInstance(getApplicationContext());
        workManager.enqueue(workRequest);
        workManager.getWorkInfoByIdLiveData(workRequest.getId()).observe(ActivityMain.this, new Observer<WorkInfo>() {
            @Override
            public void onChanged(WorkInfo workInfo) {
                if (null != workInfo && workInfo.getState().equals(WorkInfo.State.SUCCEEDED)) {
                    GetAffirmationWorker.affirmationObservable.observe(ActivityMain.this,
                            new Observer<String>() {
                                @Override
                                public void onChanged(String s) {
                                    workManager.getWorkInfosForUniqueWorkLiveData(GetAffirmationWorker.GET_AFFIRMATION_TAG).removeObservers(ActivityMain.this);
                                    affirmation = s;
                                    affirmationTextView.setText(affirmation);
                                }
                            });
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_menu, menu);
        MenuItem itemSwitch = menu.findItem(R.id.switch_action_bar);
        switchCompat = (SwitchCompat) itemSwitch.getActionView().findViewById(R.id.switch2);
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startAffirmationWorker();
                    startNotificationWorker();
                } else {
                    stopNotificationWorker();
                    stopAffirmationWorker();
                }
            }
        });

        getMenuInflater().inflate(R.menu.menu_main, menu);
        aboutMenuItem = (MenuItem) menu.findItem(R.id.about);
        aboutMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                loadFragmentAboutFragment(aboutFragment);
                return true;
            }
        });
        helpMenuItem = (MenuItem) menu.findItem(R.id.help);
        helpMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                loadFragmentHelpFragment(helpFragment);
                return true;
            }
        });
        return true;
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            mainUi(View.VISIBLE);
        } else {
            super.onBackPressed();
        }
    }

    private void mainUi(int status) {
        skipButton.setVisibility(status);
        affirmationTextView.setVisibility(status);
    }

    private void loadFragmentHelpFragment(HelpFragment fragment) {
        if (fragment == null) {
            fragment = new HelpFragment();
        }
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_frame, fragment);
        fragmentTransaction.addToBackStack(TAG);
        fragmentTransaction.commit();
        helpFragment = fragment;
        mainUi(View.GONE);
    }

    private void loadFragmentAboutFragment(AboutFragment fragment) {
        if (fragment == null) {
            fragment = new AboutFragment();
        }
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_frame, fragment);
        fragmentTransaction.addToBackStack(TAG);
        fragmentTransaction.commit();
        aboutFragment = fragment;
        mainUi(View.GONE);
    }

    private void stopAffirmationWorker() {
        WorkManager workManager = WorkManager.getInstance(getApplicationContext());
        workManager.cancelUniqueWork(GetAffirmationWorker.GET_AFFIRMATION_TAG);
    }

    private void stopNotificationWorker() {
        WorkManager workManager = WorkManager.getInstance(getApplicationContext());
        workManager.cancelUniqueWork(NotificationWorker.NOTIFICATION_WORKER_TAG);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL, NOTIFICATION_CHANNEL, importance);
            channel.setDescription("Notification channel used by mindful reminder");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void startNotificationWorker() {
        PeriodicWorkRequest.Builder workBuilder = new PeriodicWorkRequest.Builder(NotificationWorker.class, 30, TimeUnit.MINUTES).setInitialDelay(15, TimeUnit.SECONDS).addTag(NotificationWorker.NOTIFICATION_WORKER_TAG);
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresCharging(false)
                .setRequiresDeviceIdle(false)
                .build();
        PeriodicWorkRequest runWork = workBuilder.setConstraints(constraints).build();
        WorkManager workManager = WorkManager.getInstance(getApplicationContext());
        workManager.enqueueUniquePeriodicWork(NotificationWorker.NOTIFICATION_WORKER_TAG, ExistingPeriodicWorkPolicy.KEEP, runWork);
    }

    public void startAffirmationWorker() {
        PeriodicWorkRequest.Builder workBuilder = new PeriodicWorkRequest.Builder(GetAffirmationWorker.class, 24, TimeUnit.HOURS).addTag(GetAffirmationWorker.GET_AFFIRMATION_TAG).setId(UUID.randomUUID());
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresCharging(false)
                .setRequiresDeviceIdle(false)
                .build();
        PeriodicWorkRequest runWork = workBuilder.setConstraints(constraints).build();
        WorkManager workManager = WorkManager.getInstance(getApplicationContext());
        workManager.enqueueUniquePeriodicWork(GetAffirmationWorker.GET_AFFIRMATION_TAG, ExistingPeriodicWorkPolicy.KEEP, runWork);
        getAffirmationUuid = runWork.getId();
        updateAffirmationUi();
    }

}
