package com.example.mindful_reminder.activites.activities;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.widget.TextView;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.Until;

import com.example.mindful_reminder.R;
import com.example.mindful_reminder.activities.ActivityMain;

import org.junit.Before;
import org.junit.Test;


public class ActivityMainTest {

    ActivityScenario<ActivityMain> activityMainActivityScenario;
    UiDevice device;

    private static final String PACKAGE_NAME = "com.example.mindful_reminder";


    @Before
    public void setup() {
        activityMainActivityScenario = ActivityScenario.launch(ActivityMain.class);
        activityMainActivityScenario.moveToState(Lifecycle.State.CREATED);
        device = UiDevice.getInstance(getInstrumentation());
        device.pressHome();
        final String launcherPackage = device.getLauncherPackageName();
        device.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)), 5000);
        Context context = ApplicationProvider.getApplicationContext();
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(PACKAGE_NAME);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
        device.wait(Until.hasObject(By.pkg(PACKAGE_NAME).depth(0)), 5000);
    }

    @Test
    public void onCreateInitialUi() {
        UiObject2 affirmationTextView = device.findObject(By.res(PACKAGE_NAME, "affirmation"));
        assertNotNull(affirmationTextView.getText());
        UiObject2 skipButton = device.findObject(By.res(PACKAGE_NAME, "skip_button"));
        assertTrue(skipButton.isEnabled());
        UiObject2 lastUpdatedTextView = device.findObject(By.res(PACKAGE_NAME, "affirmation_updated"));
        assertNotNull(lastUpdatedTextView.getText());
    }

    @Test
    public void skipButton() throws InterruptedException {
        UiObject2 affirmationTextView = device.findObject(By.res(PACKAGE_NAME, "affirmation"));
        String initialAffirmation = affirmationTextView.getText();
        assertNotNull(affirmationTextView.getText());
        UiObject2 updatedTextView = device.findObject(By.res(PACKAGE_NAME, "affirmation_updated"));
        String initialUpdatedString = updatedTextView.getText();
        assertNotNull(updatedTextView.getText());
        UiObject2 skipButton = device.findObject(By.res(PACKAGE_NAME, "skip_button"));
        assertTrue(skipButton.isEnabled());
        skipButton.click();
        Thread.sleep(25);
        assertNotEquals(affirmationTextView.getText(), initialAffirmation);
        assertNotEquals(updatedTextView.getText(), initialUpdatedString);
    }

    @Test
    public void testMenuAboutOption() {
        UiObject2 view = device.findObject(By.res(PACKAGE_NAME, "drawer_layout"));
        Rect bounds = view.getVisibleBounds();
        int centerX = bounds.centerX();
        int centerY = bounds.centerY();
        device.drag(0, 100, centerX, centerY, 100);
        device.findObject(By.text("About")).click();
        device.wait(Until.hasObject(By.clazz(PACKAGE_NAME, "AboutFragment").depth(0)), 5000);
        UiObject2 aboutText = device.findObject(By.res(PACKAGE_NAME, "about_text_1"));
        assertEquals(ApplicationProvider.getApplicationContext().getString(R.string.aboutBody), aboutText.getText());
        device.pressBack();
        device.wait(Until.hasObject(By.pkg(device.getLauncherPackageName()).depth(0)), 5000);
    }

    @Test
    public void testMenuBreatheOption() {
        UiObject2 view = device.findObject(By.res(PACKAGE_NAME, "drawer_layout"));
        Rect bounds = view.getVisibleBounds();
        int centerX = bounds.centerX();
        int centerY = bounds.centerY();
        device.drag(0, 100, centerX, centerY, 100);
        device.findObject(By.text("Breathing Helper")).click();
        device.wait(Until.hasObject(By.clazz(PACKAGE_NAME, "BreatheFragment").depth(0)), 5000);
        UiObject2 aboutText = device.findObject(By.res(PACKAGE_NAME, "breath_help_steps_text"));
        assertEquals(ApplicationProvider.getApplicationContext().getString(R.string.breath_steps), aboutText.getText());
        device.pressBack();
        device.wait(Until.hasObject(By.pkg(device.getLauncherPackageName()).depth(0)), 5000);
    }

    @Test
    public void testMenuActivityOption() {
        UiObject2 view = device.findObject(By.res(PACKAGE_NAME, "drawer_layout"));
        Rect bounds = view.getVisibleBounds();
        int centerX = bounds.centerX();
        int centerY = bounds.centerY();
        device.drag(0, 100, centerX, centerY, 100);
        device.findObject(By.text("Daily Mindfulness Activity")).click();
        device.wait(Until.hasObject(By.clazz(PACKAGE_NAME, "DailyMindfulnessActivity").depth(0)), 5000);
        UiObject2 aboutText = device.findObject(By.res(PACKAGE_NAME, "daily_mindfulness_activity"));
        assertNotNull(aboutText.getText());
        device.pressBack();
        device.wait(Until.hasObject(By.pkg(device.getLauncherPackageName()).depth(0)), 5000);
    }

    @Test
    public void testMenuGroundingOption() throws UiObjectNotFoundException {
        UiObject2 view = device.findObject(By.res(PACKAGE_NAME, "drawer_layout"));
        Rect bounds = view.getVisibleBounds();
        int centerX = bounds.centerX();
        int centerY = bounds.centerY();
        device.drag(0, 100, centerX, centerY, 100);
        device.findObject(By.text("Grounding Exercise")).click();
        device.wait(Until.hasObject(By.clazz(PACKAGE_NAME, "GroundingFragment").depth(0)), 5000);
        UiObject2 textSwitcher = device.findObject(By.res(PACKAGE_NAME, "grounding_text_switcher"));
        UiObject2 textView = textSwitcher.findObjects(By.clazz(TextView.class.getName())).get(0);
        assertEquals(ApplicationProvider.getApplicationContext().getString(R.string.groundingBody), textView.getText());
        device.pressBack();
        device.wait(Until.hasObject(By.pkg(device.getLauncherPackageName()).depth(0)), 5000);
    }

    @Test
    public void testMenuTodayEntryOption() {
        UiObject2 view = device.findObject(By.res(PACKAGE_NAME, "drawer_layout"));
        Rect bounds = view.getVisibleBounds();
        int centerX = bounds.centerX();
        int centerY = bounds.centerY();
        device.drag(0, 100, centerX, centerY, 100);
        device.findObject(By.text("Today's Journal Entry")).click();
        device.wait(Until.hasObject(By.clazz(PACKAGE_NAME, "GratitudeJournalTodaysEntry").depth(0)), 5000);
        UiObject2 aboutText = device.findObject(By.res(PACKAGE_NAME, "header_text_view"));
        assertNotNull(aboutText.getText());
        device.pressBack();
        device.wait(Until.hasObject(By.pkg(device.getLauncherPackageName()).depth(0)), 5000);
    }

    @Test
    public void testMenuReviewJournalOption() {
        UiObject2 view = device.findObject(By.res(PACKAGE_NAME, "drawer_layout"));
        Rect bounds = view.getVisibleBounds();
        int centerX = bounds.centerX();
        int centerY = bounds.centerY();
        device.drag(0, 100, centerX, centerY, 100);
        device.findObject(By.text("Review Previous Entries")).click();
        device.wait(Until.hasObject(By.clazz(PACKAGE_NAME, "GratitudeJournalStart").depth(0)), 5000);
        UiObject2 aboutText = device.findObject(By.res(PACKAGE_NAME, "calendar_header_text_view"));
        assertEquals(ApplicationProvider.getApplicationContext().getString(R.string.your_mindfulness_journal), aboutText.getText());
        device.pressBack();
        device.wait(Until.hasObject(By.pkg(device.getLauncherPackageName()).depth(0)), 5000);
    }

}