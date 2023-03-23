package com.example.mindful_reminder.fragments;

import static com.example.mindful_reminder.config.Constants.ENABLE_GRATITUDE_TUTORIAL;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceManager;

import com.example.mindful_reminder.R;
import com.example.mindful_reminder.databases.AppDatabase;
import com.example.mindful_reminder.entities.GratitudeJournalEntry;

import java.time.LocalDate;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class GratitudeJournalTodaysEntry extends Fragment {

    private AppDatabase database;
    private TextView headerTextView;
    private AppCompatEditText newEntryEditor;
    private AppCompatButton saveButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gratitude_journal_todays_entry, container, false);
        checkIfRunTutorial();
        setupUi(view);
        getEntryIfExists();
        setupEditor();
        setupSaveButton();
        return view;
    }

    private void setupEditor() {
        newEntryEditor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int lengthBefore, int lengthAfter) {
                if (lengthAfter > lengthBefore) {
                    if (s.toString().length() ==1) {
                        s = "\u2022 " + s;
                        newEntryEditor.setText(s);
                        newEntryEditor.setSelection(newEntryEditor.getText().length());
                    }
                    if (s.toString().endsWith("\n")) {
                        s = s.toString().replace("\n", "\n\u2022 ");
                        s = s.toString().replace("\u2022 \u2022", "\u2022");
                        newEntryEditor.setText(s);
                        newEntryEditor.setSelection(newEntryEditor.getText().length());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void checkIfRunTutorial() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        if (sharedPreferences.getBoolean(ENABLE_GRATITUDE_TUTORIAL, true)) {
            FragmentManager fragmentManager = getParentFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragment_frame, new GratitudeJournalStart()).commit();
        }
    }

    private void setupSaveButton() {
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    InputMethodManager inputMethodManager = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    database = AppDatabase.getInstance(requireContext());
                    if (!Objects.requireNonNull(newEntryEditor.getText()).toString().isEmpty()) {
                        GratitudeJournalEntry todaysEntry = new GratitudeJournalEntry(LocalDate.now(), Objects.requireNonNull(newEntryEditor.getText()).toString());
                        database.gratitudeJournalDao().insertGratitudeJournalEntry(todaysEntry).get();
                        newEntryEditor.clearFocus();
                    } else {
                        database.gratitudeJournalDao().deleteGratitudeJournalEntryByDate(LocalDate.now());
                    }
                } catch (ExecutionException | InterruptedException ex) {
                    ex.printStackTrace();
                } finally {
                    database.cleanUp();
                }
            }
        });
    }

    private void getEntryIfExists() {
        try {
            database = AppDatabase.getInstance(requireContext());
            String todaysEntry = database.gratitudeJournalDao().getEntryForDate(LocalDate.now()).get();
            newEntryEditor.setText(todaysEntry);
        } catch (ExecutionException | InterruptedException ex) {
            ex.printStackTrace();
        } finally {
            database.cleanUp();
        }
    }

    private void setupUi(View view) {
        headerTextView = (TextView) view.findViewById(R.id.header_text_view);
        newEntryEditor = (AppCompatEditText) view.findViewById(R.id.new_entry_editor);
        saveButton = (AppCompatButton) view.findViewById(R.id.save_button);
        String header = "Today I am grateful for...";
        headerTextView.setText(header);
    }

}