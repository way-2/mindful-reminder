package com.way2.mindful_reminder.util;

import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;

import android.view.View;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;

import org.hamcrest.Matcher;

public class TestUtils {

    public static ViewAction forceTypeText(String text) {
        return new ViewAction() {
            @Override
            public String getDescription() {
                return "Force type text";
            }

            @Override
            public Matcher<View> getConstraints() {
                return isAssignableFrom(AppCompatEditText.class);
            }

            @Override
            public void perform(UiController uiController, View view) {
                AppCompatEditText editText = (AppCompatEditText) view;
                editText.append(text);
            }
        };
    }
}
