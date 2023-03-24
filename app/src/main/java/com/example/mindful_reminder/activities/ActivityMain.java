package com.example.mindful_reminder.activities;

import static com.example.mindful_reminder.config.Constants.GRATITUDE_REDIRECT;
import static com.example.mindful_reminder.config.Constants.NOTIFICATION_CHANNEL;
import static com.example.mindful_reminder.config.Constants.REDIRECT;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
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

import com.example.mindful_reminder.R;
import com.example.mindful_reminder.fragments.AboutFragment;
import com.example.mindful_reminder.fragments.AffirmationFragment;
import com.example.mindful_reminder.fragments.BreatheFragment;
import com.example.mindful_reminder.fragments.DailyMindfulnessActivity;
import com.example.mindful_reminder.fragments.GratitudeJournalCalendar;
import com.example.mindful_reminder.fragments.GratitudeJournalTodaysEntry;
import com.example.mindful_reminder.fragments.GroundingFragment;
import com.example.mindful_reminder.fragments.SettingsFragment;
import com.example.mindful_reminder.service.WorkerManager;
import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

public class ActivityMain extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private WorkerManager workerManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermissions();
        createNotificationChannel();
        setupWorkerManager();
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
        FragmentManager fragmentManager = getSupportFragmentManager();
        if ((null != getIntent().getExtras()) && (null != getIntent().getExtras().get(REDIRECT))) {
            String intentRedirectValue = getIntent().getExtras().get(REDIRECT).toString();
            if ("dailyMindfulnessFragment".equals(intentRedirectValue)) {
                fragmentManager.beginTransaction().replace(R.id.fragment_frame, new DailyMindfulnessActivity()).commit();
            } else if (GRATITUDE_REDIRECT.equals(intentRedirectValue)) {
                fragmentManager.beginTransaction().replace(R.id.fragment_frame, new GratitudeJournalTodaysEntry()).commit();
            }
        } else {
            fragmentManager.beginTransaction().replace(R.id.fragment_frame, new AffirmationFragment()).commit();
        }
    }

    private void setupWorkerManager() {
        workerManager = WorkerManager.getInstance();
        workerManager.startMindfulnessNotificationWorker(getApplicationContext());
        workerManager.startAffirmationNotificationWorker(getApplicationContext());
        workerManager.startGratitudeNotificationWorker(getApplicationContext());
        workerManager.startDailyActivityWorker(getApplicationContext());
        workerManager.cleanUp();
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
        fragmentManager.popBackStack();
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
            navigationView.setCheckedItem(R.id.nav_affirmation);
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
