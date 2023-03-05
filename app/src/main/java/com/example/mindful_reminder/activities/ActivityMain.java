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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
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
import com.example.mindful_reminder.fragments.SettingsFragment;
import com.example.mindful_reminder.service.GetAffirmationWorker;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ActivityMain extends AppCompatActivity {

    private static final String TAG = ActivityMain.class.getSimpleName();
    public static final String NOTIFICATION_CHANNEL = "MINDFUL_REMINDER";
    private TextView affirmationTextView;
    public static UUID getAffirmationUuid;
    public static String affirmation;
    private MenuItem aboutMenuItem;
    private MenuItem helpMenuItem;
    private MenuItem settingsMenuItem;
    private HelpFragment helpFragment;
    private AboutFragment aboutFragment;
    private SettingsFragment settingsFragment;
    private AppCompatButton skipButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermissions();
        createNotificationChannel();
        setContentView(R.layout.activity_main);
        startAffirmationWorker();
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
        if (affirmationTextView.getText().length() < 1) {
            runAffirmationOneTime();
        }
        skipButton = (AppCompatButton) requireViewById(R.id.skip_button);
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
        settingsMenuItem = (MenuItem) menu.findItem(R.id.settings);
        settingsMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                loadFragmentSettingsFragment(settingsFragment);
                return true;
            }
        });
        return true;
    }

    private void loadFragmentSettingsFragment(SettingsFragment fragment) {
        if (fragment == null) {
            fragment = new SettingsFragment();
        }
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_frame, fragment);
        fragmentTransaction.addToBackStack(TAG);
        fragmentTransaction.commit();
        settingsFragment = fragment;
        mainUi(View.GONE);
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

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL, NOTIFICATION_CHANNEL, importance);
            channel.setDescription("Notification channel used by mindful reminder");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
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