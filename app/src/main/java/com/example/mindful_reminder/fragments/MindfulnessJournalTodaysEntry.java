package com.example.mindful_reminder.fragments;

import static com.example.mindful_reminder.config.Constants.ENABLE_MINDFULNESS_TUTORIAL;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceManager;

import com.example.mindful_reminder.R;
import com.example.mindful_reminder.databases.AppDatabase;
import com.example.mindful_reminder.entities.JournalEntry;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class MindfulnessJournalTodaysEntry extends Fragment implements View.OnClickListener {

    private AppDatabase database;
    private AppCompatEditText gratitudeTextEntry;
    private AppCompatEditText ruminationEntryText;
    private AppCompatButton saveButton;
    private JournalEntry todaysEntry;
    private Button[] buttons = new Button[6];
    private Button buttonUnfocus;
    private int[] buttonId = {R.id.estatic_button, R.id.happy_button, R.id.meh_button, R.id.unhappy_button, R.id.sad_button, R.id.angry_button};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mindfulness_journal_todays_entry, container, false);
        checkIfRunTutorial();
        setupUi(view);
        getEntryIfExists();
        setupEditors();
        setupSaveButton();
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.estatic_button:
                setFocus(buttonUnfocus, buttons[0]);
                break;
            case R.id.happy_button:
                setFocus(buttonUnfocus, buttons[1]);
                break;
            case R.id.meh_button:
                setFocus(buttonUnfocus, buttons[2]);
                break;
            case R.id.unhappy_button:
                setFocus(buttonUnfocus, buttons[3]);
                break;
            case R.id.sad_button:
                setFocus(buttonUnfocus, buttons[4]);
                break;
            case R.id.angry_button:
                setFocus(buttonUnfocus, buttons[5]);
                break;
        }
    }

    private void setFocus(Button buttonUnfocus, Button buttonFocus) {
        buttonUnfocus.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.round_button_style_not_selected, null));
        buttonFocus.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.round_button_selected, null));
        this.buttonUnfocus = buttonFocus;
    }

    private void setupEditors() {
        gratitudeTextEntry.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int lengthBefore, int lengthAfter) {
                if (lengthAfter > lengthBefore) {
                    if (s.toString().length() ==1) {
                        s = "\u2022 " + s;
                        gratitudeTextEntry.setText(s);
                        gratitudeTextEntry.setSelection(gratitudeTextEntry.getText().length());
                    }
                    if (s.toString().endsWith("\n")) {
                        s = s.toString().replace("\n", "\n\u2022 ");
                        s = s.toString().replace("\u2022 \u2022", "\u2022");
                        gratitudeTextEntry.setText(s);
                        gratitudeTextEntry.setSelection(gratitudeTextEntry.getText().length());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ruminationEntryText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int lengthBefore, int lengthAfter) {
                if (lengthAfter > lengthBefore) {
                    if (s.toString().length() ==1) {
                        s = "\u2022 " + s;
                        ruminationEntryText.setText(s);
                        ruminationEntryText.setSelection(ruminationEntryText.getText().length());
                    }
                    if (s.toString().endsWith("\n")) {
                        s = s.toString().replace("\n", "\n\u2022 ");
                        s = s.toString().replace("\u2022 \u2022", "\u2022");
                        ruminationEntryText.setText(s);
                        ruminationEntryText.setSelection(ruminationEntryText.getText().length());
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
        if (sharedPreferences.getBoolean(ENABLE_MINDFULNESS_TUTORIAL, true)) {
            FragmentManager fragmentManager = getParentFragmentManager();
            fragmentManager.popBackStack();
            fragmentManager.beginTransaction().replace(R.id.fragment_frame, new MindfulnessJournalStart()).addToBackStack(null).commit();
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
                    todaysEntry.setGratitudeEntry(gratitudeTextEntry.getText().toString());
                    todaysEntry.setRuminationEntry(ruminationEntryText.getText().toString());
                    todaysEntry.setFeelingEntry(buttonUnfocus.getText().toString());
                    database.gratitudeJournalDao().insertGratitudeJournalEntry(todaysEntry).get();
                    gratitudeTextEntry.clearFocus();
                    ruminationEntryText.clearFocus();
                } catch (ExecutionException | InterruptedException ex) {
                    ex.printStackTrace();
                } finally {
                    database.cleanUp();
                    if (!Objects.requireNonNull(gratitudeTextEntry.getText()).toString().isEmpty()) {
                        FragmentManager fragmentManager = getParentFragmentManager();
                        fragmentManager.popBackStack();
                        fragmentManager.beginTransaction().replace(R.id.fragment_frame, new MindfulnessJournalCalendar()).addToBackStack(null).commit();
                    }
                }
            }
        });
    }

    private void getEntryIfExists() {
        try {
            database = AppDatabase.getInstance(requireContext());
            JournalEntry dbTodaysEntry = database.gratitudeJournalDao().getEntryForDate(LocalDate.now()).get();
            if (null != dbTodaysEntry) {
                gratitudeTextEntry.setText(dbTodaysEntry.getGratitudeEntry());
                ruminationEntryText.setText(dbTodaysEntry.getRuminationEntry());
                Arrays.stream(buttons).filter(button -> dbTodaysEntry.getFeelingEntry().contentEquals(button.getText())).findFirst().ifPresent(toSelect -> {
                    toSelect.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.round_button_selected, null));
                    buttonUnfocus = toSelect;
                });
                todaysEntry = dbTodaysEntry;
            } else {
                todaysEntry = new JournalEntry();
            }
        } catch (ExecutionException | InterruptedException ex) {
            ex.printStackTrace();
        } finally {
            database.cleanUp();
        }
    }

    private void setupUi(View view) {
        gratitudeTextEntry = (AppCompatEditText) view.findViewById(R.id.gratitude_entry_text);
        ruminationEntryText = (AppCompatEditText) view.findViewById(R.id.rumination_entry_text);
        saveButton = (AppCompatButton) view.findViewById(R.id.save_button);
        for (int i = 0; i < buttons.length; i++) {
            buttons[i] = (Button) view.findViewById(buttonId[i]);
            buttons[i].setOnClickListener(this);
        }
        buttonUnfocus = buttons[0];
    }

}