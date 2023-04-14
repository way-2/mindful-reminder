package com.way2.mindful_reminder.fragments;

import static com.way2.mindful_reminder.config.Constants.ENABLE_MINDFULNESS_TUTORIAL;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextSwitcher;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceManager;

import com.way2.mindful_reminder.R;
import com.way2.mindful_reminder.util.MindfulReminder;

public class MindfulnessJournalStart extends Fragment {

    private TextSwitcher textSwitcher;
    private AppCompatButton nextButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mindfulness_journal_start, container, false);
        textSwitcher = view.findViewById(R.id.mindfulness_intro_text_switcher);
        textSwitcher.setText(getString(R.string.gratitude_journal_intro_1));
        nextButton = view.findViewById(R.id.next_button);
        nextButton.setOnClickListener(startOnClickListener());
        return view;
    }

    private View.OnClickListener startOnClickListener() {
        return v -> {
            textSwitcher.setText(getString(R.string.gratitude_journal_intro_2));
            nextButton.setOnClickListener(secondOnClickListener());
        };
    }

    private View.OnClickListener secondOnClickListener() {
        return v -> {
            textSwitcher.setText(getString(R.string.gratitude_journal_intro_3));
            nextButton.setOnClickListener(getStartedOnClickListener());
        };
    }

    private View.OnClickListener getStartedOnClickListener() {
        nextButton.setText("Let's Get Started");
        return v -> {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MindfulReminder.getContext());
            sharedPreferences.edit().putBoolean(ENABLE_MINDFULNESS_TUTORIAL, false).apply();
            FragmentManager fragmentManager = getParentFragmentManager();
            fragmentManager.popBackStack();
            fragmentManager.beginTransaction().replace(R.id.fragment_frame, new MindfulnessJournalTodaysEntry()).addToBackStack(null).commit();
        };
    }

}