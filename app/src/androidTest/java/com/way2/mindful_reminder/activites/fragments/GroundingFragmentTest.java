package com.way2.mindful_reminder.activites.fragments;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.way2.mindful_reminder.util.MatcherUtils.getCurrentTextFromTextSwitcher;
import static com.way2.mindful_reminder.util.MatcherUtils.getTextFromProgressDonut;
import static org.junit.Assert.assertEquals;

import android.content.res.Resources;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.way2.mindful_reminder.R;
import com.way2.mindful_reminder.fragments.GroundingFragment;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class GroundingFragmentTest {

    Resources resources = ApplicationProvider.getApplicationContext().getResources();

    @Test
    public void breatheFragmentTest() {
        FragmentScenario.launchInContainer(GroundingFragment.class, null);
        onView(withId(R.id.grounding_text_switcher)).check(matches(isDisplayed()));
        String textViewText = getCurrentTextFromTextSwitcher(withId(R.id.grounding_text_switcher));
        assertEquals(resources.getString(R.string.groundingBody), textViewText);
        onView(withId(R.id.progress_donut)).check(matches(isDisplayed()));
        String progressDonutText = getTextFromProgressDonut(withId(R.id.progress_donut));
        assertEquals("Start", progressDonutText);
        onView(withId(R.id.progress_donut)).perform(ViewActions.click());
        progressDonutText = getTextFromProgressDonut(withId(R.id.progress_donut));
        assertEquals("0", progressDonutText);
        textViewText = getCurrentTextFromTextSwitcher(withId(R.id.grounding_text_switcher));
        assertEquals(resources.getString(R.string.groundingSee), textViewText);
        onView(withId(R.id.progress_donut)).perform(ViewActions.click());
        progressDonutText = getTextFromProgressDonut(withId(R.id.progress_donut));
        assertEquals("1", progressDonutText);
        onView(withId(R.id.progress_donut)).perform(ViewActions.click());
        progressDonutText = getTextFromProgressDonut(withId(R.id.progress_donut));
        assertEquals("2", progressDonutText);
        onView(withId(R.id.progress_donut)).perform(ViewActions.click());
        progressDonutText = getTextFromProgressDonut(withId(R.id.progress_donut));
        assertEquals("3", progressDonutText);
        onView(withId(R.id.progress_donut)).perform(ViewActions.click());
        progressDonutText = getTextFromProgressDonut(withId(R.id.progress_donut));
        assertEquals("4", progressDonutText);
        onView(withId(R.id.progress_donut)).perform(ViewActions.click());
        progressDonutText = getTextFromProgressDonut(withId(R.id.progress_donut));
        assertEquals("5", progressDonutText);
        onView(withId(R.id.progress_donut)).perform(ViewActions.click());
        progressDonutText = getTextFromProgressDonut(withId(R.id.progress_donut));
        assertEquals("0", progressDonutText);
        textViewText = getCurrentTextFromTextSwitcher(withId(R.id.grounding_text_switcher));
        assertEquals(resources.getString(R.string.groundingTouch), textViewText);
        onView(withId(R.id.progress_donut)).perform(ViewActions.click());
        progressDonutText = getTextFromProgressDonut(withId(R.id.progress_donut));
        assertEquals("1", progressDonutText);
        onView(withId(R.id.progress_donut)).perform(ViewActions.click());
        progressDonutText = getTextFromProgressDonut(withId(R.id.progress_donut));
        assertEquals("2", progressDonutText);
        onView(withId(R.id.progress_donut)).perform(ViewActions.click());
        progressDonutText = getTextFromProgressDonut(withId(R.id.progress_donut));
        assertEquals("3", progressDonutText);
        onView(withId(R.id.progress_donut)).perform(ViewActions.click());
        progressDonutText = getTextFromProgressDonut(withId(R.id.progress_donut));
        assertEquals("4", progressDonutText);
        onView(withId(R.id.progress_donut)).perform(ViewActions.click());
        progressDonutText = getTextFromProgressDonut(withId(R.id.progress_donut));
        assertEquals("0", progressDonutText);
        textViewText = getCurrentTextFromTextSwitcher(withId(R.id.grounding_text_switcher));
        assertEquals(resources.getString(R.string.groundingHear), textViewText);
        onView(withId(R.id.progress_donut)).perform(ViewActions.click());
        progressDonutText = getTextFromProgressDonut(withId(R.id.progress_donut));
        assertEquals("1", progressDonutText);
        onView(withId(R.id.progress_donut)).perform(ViewActions.click());
        progressDonutText = getTextFromProgressDonut(withId(R.id.progress_donut));
        assertEquals("2", progressDonutText);
        onView(withId(R.id.progress_donut)).perform(ViewActions.click());
        progressDonutText = getTextFromProgressDonut(withId(R.id.progress_donut));
        assertEquals("3", progressDonutText);
        onView(withId(R.id.progress_donut)).perform(ViewActions.click());
        progressDonutText = getTextFromProgressDonut(withId(R.id.progress_donut));
        assertEquals("0", progressDonutText);
        textViewText = getCurrentTextFromTextSwitcher(withId(R.id.grounding_text_switcher));
        assertEquals(resources.getString(R.string.groundingSmell), textViewText);
        onView(withId(R.id.progress_donut)).perform(ViewActions.click());
        progressDonutText = getTextFromProgressDonut(withId(R.id.progress_donut));
        assertEquals("1", progressDonutText);
        onView(withId(R.id.progress_donut)).perform(ViewActions.click());
        progressDonutText = getTextFromProgressDonut(withId(R.id.progress_donut));
        assertEquals("2", progressDonutText);
        onView(withId(R.id.progress_donut)).perform(ViewActions.click());
        progressDonutText = getTextFromProgressDonut(withId(R.id.progress_donut));
        assertEquals("0", progressDonutText);
        textViewText = getCurrentTextFromTextSwitcher(withId(R.id.grounding_text_switcher));
        assertEquals(resources.getString(R.string.groundingTaste), textViewText);
        onView(withId(R.id.progress_donut)).perform(ViewActions.click());
        progressDonutText = getTextFromProgressDonut(withId(R.id.progress_donut));
        assertEquals("1", progressDonutText);
        onView(withId(R.id.progress_donut)).perform(ViewActions.click());
        progressDonutText = getTextFromProgressDonut(withId(R.id.progress_donut));
        assertEquals("Start", progressDonutText);
        textViewText = getCurrentTextFromTextSwitcher(withId(R.id.grounding_text_switcher));
        assertEquals(resources.getString(R.string.groundingComplete), textViewText);
        onView(withId(R.id.progress_donut)).perform(ViewActions.click());
        progressDonutText = getTextFromProgressDonut(withId(R.id.progress_donut));
        assertEquals("0", progressDonutText);
        textViewText = getCurrentTextFromTextSwitcher(withId(R.id.grounding_text_switcher));
        assertEquals(resources.getString(R.string.groundingSee), textViewText);
    }

}
