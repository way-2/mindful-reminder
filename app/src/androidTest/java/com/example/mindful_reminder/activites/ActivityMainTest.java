package com.example.mindful_reminder.activites;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.content.Intent;

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
    public void onCreateInitialUi() throws UiObjectNotFoundException {
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
    public void testMenuHelpOption() throws InterruptedException {
        device.pressMenu();
        Thread.sleep(25);
        device.findObject(By.text("Help")).click();
        device.wait(Until.hasObject(By.clazz(PACKAGE_NAME, "HelpFragment").depth(0)), 5000);
        UiObject2 helpText1 = device.findObject(By.res(PACKAGE_NAME, "help_text_1"));
        UiObject2 helpText2 = device.findObject(By.res(PACKAGE_NAME, "help_text_2"));
        UiObject2 helpText3 = device.findObject(By.res(PACKAGE_NAME, "help_text_3"));
        assertEquals(ApplicationProvider.getApplicationContext().getString(R.string.helpBodyStart), helpText1.getText());
        assertEquals(ApplicationProvider.getApplicationContext().getString(R.string.helpBodyList), helpText2.getText());
        assertEquals(ApplicationProvider.getApplicationContext().getString(R.string.helpBodyEnd), helpText3.getText());
        device.pressBack();
        device.wait(Until.hasObject(By.pkg(device.getLauncherPackageName()).depth(0)), 5000);
    }

    @Test
    public void testMenuAboutOption() throws InterruptedException {
        device.pressMenu();
        Thread.sleep(25);
        device.findObject(By.text("About")).click();
        device.wait(Until.hasObject(By.clazz(PACKAGE_NAME, "AboutFragment").depth(0)), 5000);
        UiObject2 aboutText = device.findObject(By.res(PACKAGE_NAME, "about_text_1"));
        assertEquals(ApplicationProvider.getApplicationContext().getString(R.string.aboutBody), aboutText.getText());
        device.pressBack();
        device.wait(Until.hasObject(By.pkg(device.getLauncherPackageName()).depth(0)), 5000);
    }

}