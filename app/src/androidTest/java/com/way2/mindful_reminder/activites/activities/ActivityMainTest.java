package com.way2.mindful_reminder.activites.activities;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.way2.mindful_reminder.config.Constants.ENABLE_MINDFULNESS_TUTORIAL;
import static org.junit.Assert.assertTrue;

import android.content.SharedPreferences;

import androidx.lifecycle.Lifecycle;
import androidx.preference.PreferenceManager;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.way2.mindful_reminder.BuildConfig;
import com.way2.mindful_reminder.R;
import com.way2.mindful_reminder.activities.ActivityMain;
import com.way2.mindful_reminder.util.MindfulReminder;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ActivityMainTest {

    @Rule
    public ActivityScenarioRule<ActivityMain> activityMainActivityScenarioRule = new ActivityScenarioRule<>(ActivityMain.class);

    @Before
    public void setup() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MindfulReminder.getContext());
        sharedPreferences.edit().putBoolean(ENABLE_MINDFULNESS_TUTORIAL, false).commit();
    }

    @Test
    public void validateLaunchFragmentTest() {
        // validation for activity main
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()));
        onView(withId(R.id.drawer_layout)).check(matches(isDisplayed()));
        onView(withId(R.id.fragment_frame)).check(matches(isDisplayed()));
        onView(withId(R.id.affirmation)).check(matches(isDisplayed()));
        // validation for navigation drawer
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_affirmation)).check(matches(isDisplayed()));
        onView(withId(R.id.nav_activity)).check(matches(isDisplayed()));
        onView(withId(R.id.nav_breathe_helper)).check(matches(isDisplayed()));
        onView(withId(R.id.nav_grounding)).check(matches(isDisplayed()));
        onView(withId(R.id.nav_todays_journal_entry)).check(matches(isDisplayed()));
        onView(withId(R.id.nav_review_journal)).check(matches(isDisplayed()));
        onView(withId(R.id.nav_settings)).check(matches(isDisplayed()));
        onView(withId(R.id.nav_about)).check(matches(isDisplayed()));
        onView(withId(R.id.nav_progressive_muscle_relaxation)).check(matches(isDisplayed()));
        onView(withId(R.id.nav_body_scan_meditation)).check(matches(isDisplayed()));
        onView(withId(R.id.nav_mindful_stretch)).check(matches(isDisplayed()));
        onView(withId(R.id.version_text_view)).check(matches(withText("Version " + BuildConfig.VERSION_NAME))).check(matches(isDisplayed()));
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.close());
    }

    @Test
    public void validateNavDrawerAffirmationFragmentTest() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_affirmation)).perform(ViewActions.scrollTo(),ViewActions.click());
        onView(withId(R.id.affirmation)).check(matches(isDisplayed()));
    }

    @Test
    public void validateNavDrawerActivityFragmentTest() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_activity)).perform(ViewActions.scrollTo(),ViewActions.click());
        onView(withId(R.id.daily_mindfulness_activity)).check(matches(isDisplayed()));
    }

    @Test
    public void validateNavDrawerBreatheHelperFragmentTest() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_breathe_helper)).perform(ViewActions.scrollTo(),ViewActions.click());
        onView(withId(R.id.breath_help_steps_text)).check(matches(isDisplayed()));
    }

    @Test
    public void validateNavDrawerGroundingFragmentTest() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_grounding)).perform(ViewActions.scrollTo(),ViewActions.click());
        onView(withId(R.id.progress_donut)).check(matches(isDisplayed()));
    }

    @Test
    public void validateNavDrawerBodyScanMeditationFragmentTest() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_body_scan_meditation)).perform(ViewActions.scrollTo(),ViewActions.click());
        onView(withId(R.id.next_button)).check(matches(isDisplayed()));
    }

    @Test
    public void validateNavDrawerMindfulStretchingFragmentTest() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_mindful_stretch)).perform(ViewActions.scrollTo(),ViewActions.click());
        onView(withId(R.id.next_button)).check(matches(isDisplayed()));
    }

    @Test
    public void validateNavDrawerProgressiveMuscleRelaxationFragmentTest() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_progressive_muscle_relaxation)).perform(ViewActions.scrollTo(),ViewActions.click());
        onView(withId(R.id.next_button)).check(matches(isDisplayed()));
    }

    @Test
    public void validateNavDrawerTodaysJournalEntryFragmentTest() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_todays_journal_entry)).perform(ViewActions.scrollTo(),ViewActions.click());
        onView(withId(R.id.header_text_view)).check(matches(isDisplayed()));
    }

    @Test
    public void validateNavDrawerReviewJournalFragmentTest() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_review_journal)).perform(ViewActions.scrollTo(),ViewActions.click());
        onView(withId(R.id.calendar_header_text_view)).check(matches(isDisplayed()));
    }

    @Test
    public void validateNavDrawerSettingsFragmentTest() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_settings)).perform(ViewActions.scrollTo(),ViewActions.click());
        onView(withText("Toggle Affirmation Notifications")).check(matches(isDisplayed()));
    }

    @Test
    public void validateNavDrawerAboutFragmentTest() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_about)).perform(ViewActions.scrollTo(),ViewActions.click());
        onView(withId(R.id.about_text_1)).check(matches(isDisplayed()));
    }

    @Test
    public void validateBackButtonFromFragmentTest() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_activity)).perform(ViewActions.scrollTo(),ViewActions.click());
        onView(withId(R.id.daily_mindfulness_activity)).check(matches(isDisplayed()));
        Espresso.pressBack();
        onView(withId(R.id.affirmation)).check(matches(isDisplayed()));
    }

    @Test
    public void validateBackButtonFromHomeTest() {
        Espresso.pressBackUnconditionally();
        assertTrue(activityMainActivityScenarioRule.getScenario().getState().isAtLeast(Lifecycle.State.DESTROYED));
    }

}
