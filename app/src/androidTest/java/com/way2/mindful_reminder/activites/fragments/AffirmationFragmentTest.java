package com.way2.mindful_reminder.activites.fragments;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withResourceName;
import static com.way2.mindful_reminder.util.MatcherUtils.getText;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import android.content.res.Resources;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.way2.mindful_reminder.R;
import com.way2.mindful_reminder.fragments.AffirmationFragment;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;

@RunWith(AndroidJUnit4.class)
public class AffirmationFragmentTest {

    Resources resources = ApplicationProvider.getApplicationContext().getResources();

    @Test
    public void affirmationFragmentTest() {
        FragmentScenario.launchInContainer(AffirmationFragment.class, null);
        onView(withId(R.id.affirmation)).check(matches(isDisplayed()));
        String affirmation = getText(withId(R.id.affirmation));
        assertTrue(Arrays.asList(resources.getStringArray(R.array.affirmations_array)).contains(affirmation));
        onView(withId(R.id.affirmation_image_view)).check(matches(isDisplayed()));
        onView(withId(R.id.affirmation_image_border)).check(matches(withResourceName("affirmation_image_border")));
        onView(withId(R.id.skip_button)).check(matches(isDisplayed())).check(matches(isEnabled()));
        onView(withId(R.id.affirmation_updated)).check(matches(isDisplayed()));
        onView(withId(R.id.skip_button)).perform(ViewActions.click());
        String newAffirmation = getText(withId(R.id.affirmation));
        assertTrue(Arrays.asList(resources.getStringArray(R.array.affirmations_array)).contains(newAffirmation));
        assertNotEquals(affirmation, newAffirmation);
    }

}
