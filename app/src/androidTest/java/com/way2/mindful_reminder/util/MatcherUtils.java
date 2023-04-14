package com.way2.mindful_reminder.util;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;

import android.view.View;
import android.widget.TextSwitcher;
import android.widget.TextView;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;

import com.github.lzyzsd.circleprogress.DonutProgress;

import org.hamcrest.Matcher;

public class MatcherUtils {

    public static String getText(final Matcher<View> matcher) {
        final String[] stringHolder = { null };
        onView(matcher).perform(new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isAssignableFrom(TextView.class);
            }

            @Override
            public String getDescription() {
                return "getting text from a TextView";
            }

            @Override
            public void perform(UiController uiController, View view) {
                TextView tv = (TextView)view; //Save, because of check in getConstraints()
                stringHolder[0] = tv.getText().toString();
            }
        });
        return stringHolder[0];
    }

    public static String getTextFromProgressDonut(final Matcher<View> matcher) {
        final String[] stringHolder = { null };
        onView(matcher).perform(new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isAssignableFrom(DonutProgress.class);
            }

            @Override
            public String getDescription() {
                return "getting text from a DonutProgress";
            }

            @Override
            public void perform(UiController uiController, View view) {
                DonutProgress tv = (DonutProgress) view; //Save, because of check in getConstraints()
                stringHolder[0] = tv.getText().toString();
            }
        });
        return stringHolder[0];
    }

    public static String getCurrentTextFromTextSwitcher(final Matcher<View> matcher) {
        final String[] stringHolder = { null };
        onView(matcher).perform(new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isAssignableFrom(TextSwitcher.class);
            }

            @Override
            public String getDescription() {
                return "getting text from a TextSwitcher";
            }

            @Override
            public void perform(UiController uiController, View view) {
                TextSwitcher textSwitcher = (TextSwitcher) view; //Save, because of check in getConstraints()
                TextView textView = (TextView) textSwitcher.getCurrentView();
                stringHolder[0] = textView.getText().toString();
            }
        });
        return stringHolder[0];
    }

}
