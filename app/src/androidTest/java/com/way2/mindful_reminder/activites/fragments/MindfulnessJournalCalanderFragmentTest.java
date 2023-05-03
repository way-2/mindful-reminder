package com.way2.mindful_reminder.activites.fragments;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.way2.mindful_reminder.config.Constants.ENABLE_MINDFULNESS_TUTORIAL;
import static com.way2.mindful_reminder.util.MatcherUtils.getText;
import static org.hamcrest.Matchers.not;
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
import java.time.format.DateTimeFormatter;

@RunWith(AndroidJUnit4.class)
public class MindfulnessJournalCalanderFragmentTest {

    @Rule
    public ActivityScenarioRule<ActivityMain> activityMainActivityScenarioRule = new ActivityScenarioRule<>(ActivityMain.class);
    Resources resources = ApplicationProvider.getApplicationContext().getResources();
    JournalEntry journalEntry;

    @Before
    public void setup() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MindfulReminder.getContext());
        sharedPreferences.edit().putBoolean(ENABLE_MINDFULNESS_TUTORIAL, false).commit();
        AppDatabase database = AppDatabase.getInstance();
        database.clearAllTables();
        journalEntry = new JournalEntry();
        journalEntry.setEntryDate(LocalDate.now().withDayOfMonth(18));
        journalEntry.setFeelingEntry("this is a test feeling");
        journalEntry.setGratitudeEntry("this is a test gratitude");
        journalEntry.setRuminationEntry("this is a test rumination");
        journalEntry.setDailyAffirmation("this is a test affirmation");
        database.gratitudeJournalDao().insertGratitudeJournalEntry(journalEntry);
        database.cleanUp();
    }

    @Test
    public void mindfulnessJournalCalendarSetupFragment() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_review_journal)).perform(ViewActions.click());
        onView(withId(R.id.calendar_header_text_view)).check(matches(isDisplayed()));
        String text = getText(withId(R.id.calendar_header_text_view));
        assertEquals(resources.getString(R.string.your_mindfulness_journal), text);
        onView(withId(R.id.mindfulness_journal_events_calendar)).check(matches(isDisplayed()));
        onView(withId(R.id.journal_review_layout)).check(matches(not(isDisplayed())));
    }

    @Test
    public void mindfulnessJournalCalendarSelectDateWithDataFragment() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_review_journal)).perform(ViewActions.click());
        onView(withId(R.id.calendar_header_text_view)).check(matches(isDisplayed()));
        onView(withText(String.valueOf(journalEntry.getEntryDate().getDayOfMonth()))).perform(ViewActions.click());
        onView(withId(R.id.journal_review_layout)).check(matches(isDisplayed()));
        onView(withId(R.id.entry_header_text)).check(matches(isDisplayed()));
        String text = getText(withId(R.id.entry_header_text));
        DateTimeFormatter HEADER_DATE_FORMAT = DateTimeFormatter.ofPattern("MMMM dd yyyy");
        String dateString = journalEntry.getEntryDate().format(HEADER_DATE_FORMAT);
        assertEquals("On " + dateString + "...", text);
        onView(withId(R.id.mindfulness_journal_day_entry)).check(matches(isDisplayed()));
        text = getText(withId(R.id.mindfulness_journal_day_entry));
        assertEquals("I was grateful for...\n" + journalEntry.getGratitudeEntry(), text);
        onView(withId(R.id.worry_journal_day_entry)).check(matches(isDisplayed()));
        text = getText(withId(R.id.worry_journal_day_entry));
        assertEquals("I was worrying about...\n" + journalEntry.getRuminationEntry(), text);
        onView(withId(R.id.how_was_i_today_entry)).check(matches(isDisplayed()));
        text = getText(withId(R.id.how_was_i_today_entry));
        assertEquals(journalEntry.getFeelingEntry(), text);
        onView(withId(R.id.this_days_affirmation)).check(matches(isDisplayed()));
        text = getText(withId(R.id.this_days_affirmation));
        assertEquals("My affirmation was...\n" + journalEntry.getDailyAffirmation(), text);
        onView(withText(String.valueOf(journalEntry.getEntryDate().getDayOfMonth()+1))).perform(ViewActions.click());
        onView(withId(R.id.journal_review_layout)).check(matches(not(isDisplayed())));
    }

}
