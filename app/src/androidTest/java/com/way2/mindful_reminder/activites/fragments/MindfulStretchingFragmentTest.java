package com.way2.mindful_reminder.activites.fragments;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.way2.mindful_reminder.util.MatcherUtils.getCurrentTextFromTextSwitcher;
import static org.junit.Assert.assertEquals;

import android.content.res.Resources;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.way2.mindful_reminder.R;
import com.way2.mindful_reminder.fragments.MindfulStretchingFragment;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MindfulStretchingFragmentTest {
    Resources resources = ApplicationProvider.getApplicationContext().getResources();

    @Test
    public void mindfulStretchingFragmentTest() {
        String[] stepsArray = resources.getStringArray(R.array.mindful_stretching);
        FragmentScenario.launchInContainer(MindfulStretchingFragment.class, null);
        for (int i = 0; i < stepsArray.length ; i++) {
            onView(withId(R.id.text_switcher)).check(matches(isDisplayed()));
            String textViewText = getCurrentTextFromTextSwitcher(withId(R.id.text_switcher));
            assertEquals(stepsArray[i], textViewText);
            onView(withId(R.id.next_button)).perform(ViewActions.click());
        }
        for (int i = 2; i < stepsArray.length ; i++) {
            onView(withId(R.id.text_switcher)).check(matches(isDisplayed()));
            String textViewText = getCurrentTextFromTextSwitcher(withId(R.id.text_switcher));
            assertEquals(stepsArray[i], textViewText);
            onView(withId(R.id.next_button)).perform(ViewActions.click());
        }
    }

}
