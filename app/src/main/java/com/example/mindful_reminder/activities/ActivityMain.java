package com.example.mindful_reminder.activities;

import static com.example.mindful_reminder.fragments.SettingsFragment.DAILY_ACTIVITY_TOGGLE_SWITCH_KEY;
import static com.example.mindful_reminder.fragments.SettingsFragment.DAILY_NOTIFICATION_HOUR_LIST;
import static com.example.mindful_reminder.fragments.SettingsFragment.NOTIFICATION_TIME_INTERVAL_LIST;
import static com.example.mindful_reminder.fragments.SettingsFragment.NOTIFICATION_TOGGLE_SWITCH_KEY;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceManager;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.mindful_reminder.R;
import com.example.mindful_reminder.fragments.AboutFragment;
import com.example.mindful_reminder.fragments.AffirmationFragment;
import com.example.mindful_reminder.fragments.BreatheFragment;
import com.example.mindful_reminder.fragments.DailyMindfulnessActivity;
import com.example.mindful_reminder.fragments.GratitudeJournalCalendar;
import com.example.mindful_reminder.fragments.GratitudeJournalTodaysEntry;
import com.example.mindful_reminder.fragments.GroundingFragment;
import com.example.mindful_reminder.fragments.SettingsFragment;
import com.example.mindful_reminder.service.AffirmationNotificationWorker;
import com.example.mindful_reminder.service.DailyWorker;
import com.example.mindful_reminder.service.MindfulnessActivityNotificationWorker;
import com.google.android.material.navigation.NavigationView;

import java.util.Calendar;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ActivityMain extends AppCompatActivity {
    public static final String NOTIFICATION_CHANNEL = "MINDFUL_REMINDER";
    public static final String DAILY_MINDFULNESS_ACTIVITY_SHARED_PREFERENCE = "daily_mindfulness_activity_shared_preference";
    public static final String AFFIRMATION_SHARED_PREFERENCE = "affirmation_shared_preference";
    public static final String AFFIRMATION_UPDATED_SHARED_PREFERENCE = "affirmation_updated_shared_preference";
    public static final String DAILY_MINDFULNESS_ACTIVITY_UPDATED_SHARED_PREFERENCE = "daily_mindfulness_activity_updated_shared_preference";
    public static UUID dailyActivityUuid;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermissions();
        createNotificationChannel();
        startMindfulnessNotificationWorker();
        startAffirmationNotificationWorker();
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) requireViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        drawerLayout = (DrawerLayout) requireViewById(R.id.drawer_layout);
        navigationView = (NavigationView) requireViewById(R.id.nView);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                selectDrawerItem(item);
                return true;
            }
        });
        actionBarDrawerToggle = setupDrawerToggele();
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        startDailyActivityWorker();
        FragmentManager fragmentManager = getSupportFragmentManager();
        if ((null != getIntent().getExtras()) && (null != getIntent().getExtras().get("redirect"))) {
            String intentRedirectValue = getIntent().getExtras().get("redirect").toString();
            if ("dailyMindfulnessFragment".equals(intentRedirectValue)) {
                fragmentManager.beginTransaction().replace(R.id.fragment_frame, new DailyMindfulnessActivity()).commit();
            }
        } else {
            fragmentManager.beginTransaction().replace(R.id.fragment_frame, new AffirmationFragment()).commit();
        }
    }

    private ActionBarDrawerToggle setupDrawerToggele() {
        return new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
    }

    private void selectDrawerItem(MenuItem item) {
        Fragment fragment = null;
        Class fragmentClass = null;
        switch (item.getItemId()) {
            case R.id.nav_breathe_helper:
                fragmentClass = BreatheFragment.class;
                break;
            case R.id.nav_about:
                fragmentClass = AboutFragment.class;
                break;
            case R.id.nav_affirmation:
                fragmentClass = AffirmationFragment.class;
                break;
            case R.id.nav_activity:
                fragmentClass = DailyMindfulnessActivity.class;
                break;
            case R.id.nav_grounding:
                fragmentClass = GroundingFragment.class;
                break;
            case R.id.nav_settings:
                fragmentClass = SettingsFragment.class;
                break;
            case R.id.nav_todays_journal_entry:
                fragmentClass = GratitudeJournalTodaysEntry.class;
                break;
            case R.id.nav_review_journal:
                fragmentClass = GratitudeJournalCalendar.class;
                break;
        }
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_in,
                        R.anim.fade_out
                )
                .replace(R.id.fragment_frame, fragment)
                .addToBackStack(null)
                .commit();
        item.setChecked(true);
        drawerLayout.closeDrawers();
    }

    private void checkPermissions() {
        String[] requestedPermissions = new String[]{Manifest.permission.POST_NOTIFICATIONS};
        for (String s: requestedPermissions) {
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), s) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{s}, 0);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
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

    public void startDailyActivityWorker() {
        Calendar calendar = Calendar.getInstance();
        long nowMillis = calendar.getTimeInMillis();
        calendar.set(Calendar.HOUR_OF_DAY, 1);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DATE, 1);
        }
        long diff = calendar.getTimeInMillis() - nowMillis;
        PeriodicWorkRequest.Builder workBuilder = new PeriodicWorkRequest.Builder(DailyWorker.class, 24, TimeUnit.HOURS).setInitialDelay(diff, TimeUnit.MILLISECONDS).addTag(DailyWorker.DAILY_ACTIVITY_TAG).setId(UUID.randomUUID());
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .setRequiresCharging(false)
                .setRequiresDeviceIdle(false)
                .build();
        PeriodicWorkRequest runWork = workBuilder.setConstraints(constraints).build();
        WorkManager workManager = WorkManager.getInstance(getApplicationContext());
        workManager.enqueueUniquePeriodicWork(DailyWorker.DAILY_ACTIVITY_TAG, ExistingPeriodicWorkPolicy.KEEP, runWork);
        dailyActivityUuid = runWork.getId();
    }

    private void startMindfulnessNotificationWorker() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (sharedPreferences.getBoolean(DAILY_ACTIVITY_TOGGLE_SWITCH_KEY, false)) {
            int notificationInterval = Integer.parseInt(sharedPreferences.getString(DAILY_NOTIFICATION_HOUR_LIST, "8"));
            Log.i("notificationSetting", "Setting notifications to daily at " + notificationInterval);
            Calendar calendar = Calendar.getInstance();
            long nowMillis = calendar.getTimeInMillis();
            calendar.set(Calendar.HOUR_OF_DAY, notificationInterval);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            if (calendar.before(Calendar.getInstance())) {
                calendar.add(Calendar.DATE, 1);
            }
            long diff = calendar.getTimeInMillis() - nowMillis;
            PeriodicWorkRequest.Builder workBuilder = new PeriodicWorkRequest.Builder(MindfulnessActivityNotificationWorker.class, 24, TimeUnit.HOURS).setInitialDelay(diff, TimeUnit.SECONDS).addTag(MindfulnessActivityNotificationWorker.ACTIVITY_NOTIFICATION_WORKER_TAG);
            Constraints constraints = new Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                    .setRequiresCharging(false)
                    .setRequiresDeviceIdle(false)
                    .build();
            PeriodicWorkRequest runWork = workBuilder.setConstraints(constraints).build();
            WorkManager workManager = WorkManager.getInstance(getApplicationContext());
            workManager.enqueueUniquePeriodicWork(MindfulnessActivityNotificationWorker.ACTIVITY_NOTIFICATION_WORKER_TAG, ExistingPeriodicWorkPolicy.UPDATE, runWork);
        }
    }

    private void startAffirmationNotificationWorker() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (sharedPreferences.getBoolean(NOTIFICATION_TOGGLE_SWITCH_KEY, false)) {
            long notificationInterval = Long.parseLong(sharedPreferences.getString(NOTIFICATION_TIME_INTERVAL_LIST, "30"));
            Log.i("notificationSetting", "Setting notifications to every " + notificationInterval + " minutes");
            PeriodicWorkRequest.Builder workBuilder = new PeriodicWorkRequest.Builder(AffirmationNotificationWorker.class, notificationInterval, TimeUnit.MINUTES).setInitialDelay(15, TimeUnit.SECONDS).addTag(AffirmationNotificationWorker.AFFIRMATION_NOTIFICATION_WORKER_TAG);
            Constraints constraints = new Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                    .setRequiresCharging(false)
                    .setRequiresDeviceIdle(false)
                    .build();
            PeriodicWorkRequest runWork = workBuilder.setConstraints(constraints).build();
            WorkManager workManager = WorkManager.getInstance(getApplicationContext());
            workManager.enqueueUniquePeriodicWork(AffirmationNotificationWorker.AFFIRMATION_NOTIFICATION_WORKER_TAG, ExistingPeriodicWorkPolicy.KEEP, runWork);
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }
}
