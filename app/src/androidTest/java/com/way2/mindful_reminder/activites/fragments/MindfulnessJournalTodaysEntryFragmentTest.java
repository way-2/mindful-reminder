package com.way2.mindful_reminder.activites.fragments;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.way2.mindful_reminder.config.Constants.ENABLE_MINDFULNESS_TUTORIAL;
import static com.way2.mindful_reminder.util.MatcherUtils.getText;
import static org.junit.Assert.assertEquals;

import android.content.SharedPreferences;
import android.content.res.Resources;

import androidx.preference.PreferenceManager;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.way2.mindful_reminder.R;
import com.way2.mindful_reminder.activities.ActivityMain;
import com.way2.mindful_reminder.databases.AppDatabase;
import com.way2.mindful_reminder.entities.JournalEntry;
import com.way2.mindful_reminder.util.MindfulReminder;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.LocalDate;
import java.util.concurrent.ExecutionException;

@RunWith(AndroidJUnit4.class)
public class MindfulnessJournalTodaysEntryFragmentTest {

    @Rule
    public ActivityScenarioRule<ActivityMain> activityMainActivityScenarioRule = new ActivityScenarioRule<>(ActivityMain.class);
    Resources resources = ApplicationProvider.getApplicationContext().getResources();

    @Before
    public void setup() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MindfulReminder.getContext());
        sharedPreferences.edit().putBoolean(ENABLE_MINDFULNESS_TUTORIAL, false).commit();
        AppDatabase database = AppDatabase.getInstance();
        database.clearAllTables();
        database.cleanUp();
    }

    @Test
    public void mindfulnessJournalTodaysEntrySetupFragment() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_todays_journal_entry)).perform(ViewActions.click());
        onView(withId(R.id.header_text_view)).check(matches(isDisplayed()));
        String text = getText(withId(R.id.header_text_view));
        assertEquals(resources.getString(R.string.today_i), text);
        onView(withId(R.id.daily_gratitude_header)).check(matches(isDisplayed()));
        text = getText(withId(R.id.daily_gratitude_header));
        assertEquals(resources.getString(R.string.daily_gratitude_header_text), text);
        onView(withId(R.id.daily_worry_header)).check(matches(isDisplayed()));
        text = getText(withId(R.id.daily_worry_header));
        assertEquals(resources.getString(R.string.daily_worry_header_text), text);
        onView(withId(R.id.how_was_today_header)).check(matches(isDisplayed()));
        text = getText(withId(R.id.how_was_today_header));
        assertEquals(resources.getString(R.string.how_was_today_text), text);
        onView(withId(R.id.gratitude_entry_text)).check(matches(isDisplayed()));
        onView(withId(R.id.worry_entry_text)).check(matches(isDisplayed()));
        onView(withId(R.id.estatic_button)).check(matches(isDisplayed()));
        text = getText(withId(R.id.estatic_button));
        assertEquals(resources.getString(R.string.estatic_icon), text);
        onView(withId(R.id.happy_button)).check(matches(isDisplayed()));
        text = getText(withId(R.id.happy_button));
        assertEquals(resources.getString(R.string.happy_icon), text);
        onView(withId(R.id.meh_button)).check(matches(isDisplayed()));
        text = getText(withId(R.id.meh_button));
        assertEquals(resources.getString(R.string.meh_icon), text);
        onView(withId(R.id.unhappy_button)).check(matches(isDisplayed()));
        text = getText(withId(R.id.unhappy_button));
        assertEquals(resources.getString(R.string.unhappy_icon), text);
        onView(withId(R.id.sad_button)).check(matches(isDisplayed()));
        text = getText(withId(R.id.sad_button));
        assertEquals(resources.getString(R.string.sad_icon), text);
        onView(withId(R.id.angry_button)).check(matches(isDisplayed()));
        text = getText(withId(R.id.angry_button));
        assertEquals(resources.getString(R.string.angry_icon), text);
        onView(withId(R.id.save_button)).check(matches(isDisplayed()));
        text = getText(withId(R.id.save_button));
        assertEquals(resources.getString(R.string.save), text);
    }

    @Test
    public void mindfulnessJournalTodaysEntryFragment() throws ExecutionException, InterruptedException {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_todays_journal_entry)).perform(ViewActions.click());
        String gratitudeEntryTestText = "this is a test on the gratitude entry text box";
        String worryEntryTestText = "this is a test on the worry entry text box";
        onView(withId(R.id.gratitude_entry_text)).perform(ViewActions.click()).perform(ViewActions.replaceText(gratitudeEntryTestText)).perform(ViewActions.closeSoftKeyboard());
        onView(withId(R.id.worry_entry_text)).perform(ViewActions.click()).perform(ViewActions.replaceText(worryEntryTestText)).perform(ViewActions.closeSoftKeyboard());
        String text = getText(withId(R.id.gratitude_entry_text));
        assertEquals(gratitudeEntryTestText, text);
        text = getText(withId(R.id.worry_entry_text));
        assertEquals(worryEntryTestText, text);
        onView(withId(R.id.sad_button)).perform(ViewActions.click());
        onView(withId(R.id.save_button)).perform(ViewActions.click());
        AppDatabase database = AppDatabase.getInstance();
        JournalEntry journalEntryEntry = database.gratitudeJournalDao().getEntryForDate(LocalDate.now()).get();
        assertEquals(gratitudeEntryTestText, journalEntryEntry.getGratitudeEntry());
        assertEquals(worryEntryTestText, journalEntryEntry.getRuminationEntry());
        onView(withId(R.id.calendar_header_text_view)).check(matches(isDisplayed()));
    }

}
