package com.way2.mindful_reminder.activites.fragments;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.way2.mindful_reminder.util.MatcherUtils.getCurrentTextFromTextSwitcher;
import static com.way2.mindful_reminder.util.MatcherUtils.getText;
import static org.junit.Assert.assertEquals;

import android.content.res.Resources;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.way2.mindful_reminder.R;
import com.way2.mindful_reminder.fragments.MindfulnessJournalStart;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MindfulnessJournalStartFragmentTest {

    Resources resources = ApplicationProvider.getApplicationContext().getResources();

    @Test
    public void mindfulnessJournalStartFragment() {
        FragmentScenario.launchInContainer(MindfulnessJournalStart.class, null);
        onView(withId(R.id.mindfulness_intro_text_switcher)).check(matches(isDisplayed()));
        assertEquals(resources.getString(R.string.next), getText(withId(R.id.next_button)));
        String textViewText = getCurrentTextFromTextSwitcher(withId(R.id.mindfulness_intro_text_switcher));
        assertEquals(resources.getString(R.string.gratitude_journal_intro_1), textViewText);
        onView(withId(R.id.next_button)).perform(ViewActions.click());
        textViewText = getCurrentTextFromTextSwitcher(withId(R.id.mindfulness_intro_text_switcher));
        assertEquals(resources.getString(R.string.gratitude_journal_intro_2), textViewText);
        onView(withId(R.id.next_button)).perform(ViewActions.click());
        textViewText = getCurrentTextFromTextSwitcher(withId(R.id.mindfulness_intro_text_switcher));
        assertEquals(resources.getString(R.string.gratitude_journal_intro_3), textViewText);
        assertEquals("Let's Get Started", getText(withId(R.id.next_button)));
    }

}
