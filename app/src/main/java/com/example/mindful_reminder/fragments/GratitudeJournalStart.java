package com.example.mindful_reminder.fragments;

import static com.example.mindful_reminder.fragments.SettingsFragment.ENABLE_GRATITUDE_TUTORIAL;

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

import com.example.mindful_reminder.R;

public class GratitudeJournalStart extends Fragment {

    private TextSwitcher textSwitcher;
    private AppCompatButton nextButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gratitude_journal_start, container, false);
        textSwitcher = (TextSwitcher) view.findViewById(R.id.gratitude_intro_text_switcher);
        textSwitcher.setText(getString(R.string.gratitude_journal_intro_1));
        nextButton = (AppCompatButton) view.findViewById(R.id.next_button);
        nextButton.setOnClickListener(startOnClickListener());
        return view;
    }

    private View.OnClickListener startOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textSwitcher.setText(getString(R.string.gratitude_journal_intro_2));
                nextButton.setOnClickListener(secondOnClickListener());
            }
        };
    }

    private View.OnClickListener secondOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textSwitcher.setText(getString(R.string.gratitude_journal_intro_3));
                nextButton.setOnClickListener(getStartedOnClickListener());
            }
        };
    }

    private View.OnClickListener getStartedOnClickListener() {
        nextButton.setText("Let's Get Started");
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
                sharedPreferences.edit().putBoolean(ENABLE_GRATITUDE_TUTORIAL, false).apply();
                FragmentManager fragmentManager = getParentFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.fragment_frame, new GratitudeJournalTodaysEntry()).commit();
            }
        };
    }

}