package com.way2.mindful_reminder.activities;

import static com.way2.mindful_reminder.config.Constants.DAILY_MINDFULNESS_REDIRECT;
import static com.way2.mindful_reminder.config.Constants.MINDFULNESS_JOURNAL_REDIRECT;
import static com.way2.mindful_reminder.config.Constants.REDIRECT;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.navigation.NavigationView;
import com.way2.mindful_reminder.R;
import com.way2.mindful_reminder.fragments.AboutFragment;
import com.way2.mindful_reminder.fragments.AffirmationFragment;
import com.way2.mindful_reminder.fragments.BreatheFragment;
import com.way2.mindful_reminder.fragments.DailyMindfulnessActivity;
import com.way2.mindful_reminder.fragments.GroundingFragment;
import com.way2.mindful_reminder.fragments.MindfulnessJournalCalendar;
import com.way2.mindful_reminder.fragments.MindfulnessJournalStart;
import com.way2.mindful_reminder.fragments.MindfulnessJournalTodaysEntry;
import com.way2.mindful_reminder.fragments.SettingsFragment;
import com.way2.mindful_reminder.service.BackgroundTasks;
import com.way2.mindful_reminder.util.MindfulReminder;

import java.util.Objects;

public class ActivityMain extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupUi();
        runBackgroundWork();
        onNewIntent(getIntent());
    }

    public void updateTextView(int id, String text) {
        TextView textView = findViewById(id);
        textView.setText(text);
    }

    private void setupUi() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nView);
        navigationView.setNavigationItemSelectedListener(item -> {
            selectDrawerItem(item);
            return true;
        });
        actionBarDrawerToggle = setupDrawerToggle();
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_frame, new AffirmationFragment()).commit();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if ((null != intent.getExtras()) && (null != intent.getExtras().get(REDIRECT))) {
            String intentRedirectValue = intent.getExtras().get(REDIRECT).toString();
            if (DAILY_MINDFULNESS_REDIRECT.equals(intentRedirectValue)) {
                fragmentManager.beginTransaction().replace(R.id.fragment_frame, new DailyMindfulnessActivity()).addToBackStack(null).commit();
            } else if (MINDFULNESS_JOURNAL_REDIRECT.equals(intentRedirectValue)) {
                fragmentManager.beginTransaction().replace(R.id.fragment_frame, new MindfulnessJournalTodaysEntry()).addToBackStack(null).commit();
            }
        }
        super.onNewIntent(intent);
    }

    private void runBackgroundWork() {
        BackgroundTasks backgroundTasks = new BackgroundTasks();
        backgroundTasks.setContext(this);
        MindfulReminder.getInstance().getThreadPoolExecutor().execute(backgroundTasks.activityMainStartupTasks);
        MindfulReminder.getInstance().getThreadPoolExecutor().execute(backgroundTasks.activityMainUiSetup);
        do {
            Log.d("ACTIVITY_MAIN", "Number of active threads: " + MindfulReminder.getInstance().getThreadPoolExecutor().getActiveCount());
        } while (MindfulReminder.getInstance().getThreadPoolExecutor().getActiveCount() > 0);
        Log.d("ACTIVITY_MAIN", "Number of active threads: " + MindfulReminder.getInstance().getThreadPoolExecutor().getActiveCount());
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
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
                fragmentClass = MindfulnessJournalTodaysEntry.class;
                break;
            case R.id.nav_review_journal:
                fragmentClass = MindfulnessJournalCalendar.class;
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragment_frame);
        if (!(f instanceof MindfulnessJournalStart)) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                navigationView.setCheckedItem(R.id.nav_affirmation);
                getSupportFragmentManager().popBackStack();
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }
}
