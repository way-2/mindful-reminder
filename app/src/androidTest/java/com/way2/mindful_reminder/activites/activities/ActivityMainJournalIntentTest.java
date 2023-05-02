package com.way2.mindful_reminder.activites.activities;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.way2.mindful_reminder.config.Constants.ENABLE_MINDFULNESS_TUTORIAL;
import static com.way2.mindful_reminder.config.Constants.MINDFULNESS_JOURNAL_REDIRECT;
import static com.way2.mindful_reminder.config.Constants.REDIRECT;
import static org.junit.Assert.assertTrue;

import android.content.Intent;
import android.content.SharedPreferences;

import androidx.lifecycle.Lifecycle;
import androidx.preference.PreferenceManager;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.way2.mindful_reminder.R;
import com.way2.mindful_reminder.activities.ActivityMain;
import com.way2.mindful_reminder.util.MindfulReminder;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ActivityMainJournalIntentTest {

    static Intent journalIntent;
    static {
        journalIntent = new Intent(ApplicationProvider.getApplicationContext(), ActivityMain.class);
        journalIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        journalIntent.putExtra(REDIRECT, MINDFULNESS_JOURNAL_REDIRECT);
    }

    @Rule
    public ActivityScenarioRule<ActivityMain> activityMainActivityScenarioRule = new ActivityScenarioRule<>(journalIntent);

    @Before
    public void setup() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MindfulReminder.getContext());
        sharedPreferences.edit().putBoolean(ENABLE_MINDFULNESS_TUTORIAL, false).commit();
    }

    @Test
    public void validateLaunchFragmentTest() {
        onView(withId(R.id.header_text_view)).check(matches(isDisplayed()));
    }

    @Test
    public void validateBackFromIntentTest() {
        Espresso.pressBack();
        onView(withId(R.id.affirmation)).check(matches(isDisplayed()));
    }

    @Test
    public void validateBackButtonFromHomeTest() {
        Espresso.pressBackUnconditionally();
        assertTrue(activityMainActivityScenarioRule.getScenario().getState().isAtLeast(Lifecycle.State.DESTROYED));
    }

}
