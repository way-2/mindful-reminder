package com.example.mindful_reminder.activities;

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
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.mindful_reminder.R;
import com.example.mindful_reminder.fragments.AboutFragment;
import com.example.mindful_reminder.fragments.BreatheFragment;
import com.example.mindful_reminder.fragments.HelpFragment;
import com.example.mindful_reminder.fragments.MainFragment;
import com.example.mindful_reminder.service.GetAffirmationWorker;
import com.google.android.material.navigation.NavigationView;

import java.util.Calendar;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ActivityMain extends AppCompatActivity {

    private static final String TAG = ActivityMain.class.getSimpleName();
    public static final String NOTIFICATION_CHANNEL = "MINDFUL_REMINDER";
    public static final String AFFIRMATION_SHARED_PREFERENCE = "affirmation_shared_preference";
    public static final String AFFIRMATION_UPDATED_SHARED_PREFERENCE = "affirmation_updated_shared_preference";
    public static UUID getAffirmationUuid;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermissions();
        createNotificationChannel();
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
        startAffirmationWorker();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_frame, new MainFragment()).commit();
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
            case R.id.nav_help:
                fragmentClass = HelpFragment.class;
                break;
            case R.id.nav_main:
                fragmentClass = MainFragment.class;
                break;
        }
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_frame, fragment).addToBackStack(null).commit();
        item.setChecked(true);
        drawerLayout.closeDrawers();
    }

    private void checkPermissions() {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 0);
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

    public void startAffirmationWorker() {
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
        PeriodicWorkRequest.Builder workBuilder = new PeriodicWorkRequest.Builder(GetAffirmationWorker.class, 24, TimeUnit.HOURS).setInitialDelay(diff, TimeUnit.MILLISECONDS).addTag(GetAffirmationWorker.GET_AFFIRMATION_TAG).setId(UUID.randomUUID());
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresCharging(false)
                .setRequiresDeviceIdle(false)
                .build();
        PeriodicWorkRequest runWork = workBuilder.setConstraints(constraints).build();
        WorkManager workManager = WorkManager.getInstance(getApplicationContext());
        workManager.enqueueUniquePeriodicWork(GetAffirmationWorker.GET_AFFIRMATION_TAG, ExistingPeriodicWorkPolicy.KEEP, runWork);
        getAffirmationUuid = runWork.getId();
//        workManager.getWorkInfoByIdLiveData(getAffirmationUuid).observe(this, new Observer<WorkInfo>() {
//            @Override
//            public void onChanged(WorkInfo workInfo) {
//                if (null != workInfo && workInfo.getState().equals(WorkInfo.State.RUNNING)) {
//                    GetAffirmationWorker.updateDone.observe(this,
//                            new Observer<Boolean>() {
//                                @Override
//                                public void onChanged(Boolean updateDone) {
//                                    workManager.getWorkInfosForUniqueWorkLiveData(GetAffirmationWorker.GET_AFFIRMATION_TAG).removeObservers(ActivityMain.this);
//                                    affirmationTextView.setText(sharedPreferences.getString(AFFIRMATION_SHARED_PREFERENCE, ""));
//                                    affirmationUpdatedTextView.setText(sharedPreferences.getString(AFFIRMATION_UPDATED_SHARED_PREFERENCE, ""));
//                                }
//                            });
//                }
//            }
//        });
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
