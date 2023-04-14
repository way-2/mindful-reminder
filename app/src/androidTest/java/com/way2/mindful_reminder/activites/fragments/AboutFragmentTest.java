package com.way2.mindful_reminder.activites.fragments;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.res.Resources;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.way2.mindful_reminder.R;
import com.way2.mindful_reminder.fragments.AboutFragment;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class AboutFragmentTest {

    Resources resources = ApplicationProvider.getApplicationContext().getResources();

    @Test
    public void testAboutFragment() {
        FragmentScenario.launchInContainer(AboutFragment.class, null);
        onView(withId(R.id.about_text_1)).check(matches(isDisplayed())).check(matches(withText(resources.getString(R.string.aboutBody))));
        onView(withId(R.id.about_text_2)).check(matches(isDisplayed())).check(matches(withText(resources.getString(R.string.send_feedback))));
    }

}
