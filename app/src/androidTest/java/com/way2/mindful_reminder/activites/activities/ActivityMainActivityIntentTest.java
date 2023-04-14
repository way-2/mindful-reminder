package com.way2.mindful_reminder.activites.activities;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.way2.mindful_reminder.config.Constants.DAILY_MINDFULNESS_REDIRECT;
import static com.way2.mindful_reminder.config.Constants.REDIRECT;
import static org.junit.Assert.assertTrue;

import android.content.Intent;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.way2.mindful_reminder.R;
import com.way2.mindful_reminder.activities.ActivityMain;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ActivityMainActivityIntentTest {

    static Intent activityIntent;
    static {
        activityIntent = new Intent(ApplicationProvider.getApplicationContext(), ActivityMain.class);
        activityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activityIntent.putExtra(REDIRECT, DAILY_MINDFULNESS_REDIRECT);
    }

    @Rule
    public ActivityScenarioRule<ActivityMain> activityMainActivityScenarioRule = new ActivityScenarioRule<>(activityIntent);

    @Test
    public void validateLaunchFragmentTest() {
        onView(withId(R.id.daily_mindfulness_activity)).check(matches(isDisplayed()));
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
