package com.way2.mindful_reminder.activites.fragments;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.way2.mindful_reminder.util.MatcherUtils.getText;
import static org.junit.Assert.assertTrue;

import android.content.res.Resources;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.way2.mindful_reminder.R;
import com.way2.mindful_reminder.fragments.DailyMindfulnessActivity;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;

@RunWith(AndroidJUnit4.class)
public class DailyMindfulnessActivityFragmentTest {

    Resources resources = ApplicationProvider.getApplicationContext().getResources();

    @Test
    public void affirmationFragmentTest() {
        FragmentScenario.launchInContainer(DailyMindfulnessActivity.class, null);
        onView(withId(R.id.daily_mindfulness_activity)).check(matches(isDisplayed()));
        String activity = getText(withId(R.id.daily_mindfulness_activity));
        assertTrue(Arrays.asList(resources.getStringArray(R.array.activity_name)).contains(activity));
        onView(withId(R.id.daily_mindfulness_activity_details)).check(matches(isDisplayed()));
        String activityDetails = getText(withId(R.id.daily_mindfulness_activity_details));
        assertTrue(Arrays.asList(resources.getStringArray(R.array.activity_text)).contains(activityDetails));
    }

}
