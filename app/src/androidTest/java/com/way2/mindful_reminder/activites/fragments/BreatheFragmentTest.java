package com.way2.mindful_reminder.activites.fragments;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.way2.mindful_reminder.util.MatcherUtils.getText;
import static com.way2.mindful_reminder.util.MatcherUtils.getTextFromProgressDonut;
import static org.junit.Assert.assertEquals;

import android.content.res.Resources;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.way2.mindful_reminder.R;
import com.way2.mindful_reminder.fragments.BreatheFragment;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class BreatheFragmentTest {

    Resources resources = ApplicationProvider.getApplicationContext().getResources();

    @Test
    public void breatheFragmentTest() {
        FragmentScenario.launchInContainer(BreatheFragment.class, null);
        onView(withId(R.id.breath_help_steps_text)).check(matches(isDisplayed()));
        String affirmation = getText(withId(R.id.breath_help_steps_text));
        assertEquals(resources.getString(R.string.breath_steps), affirmation);
        onView(withId(R.id.progress_donut)).check(matches(isDisplayed()));
        String progressDonutText = getTextFromProgressDonut(withId(R.id.progress_donut));
        assertEquals("Start", progressDonutText);
    }

}
