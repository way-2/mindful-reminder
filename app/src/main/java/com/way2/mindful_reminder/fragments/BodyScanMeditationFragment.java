package com.way2.mindful_reminder.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextSwitcher;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.way2.mindful_reminder.R;

public class BodyScanMeditationFragment extends Fragment {

    private TextSwitcher textSwitcher;
    private AppCompatButton nextButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exercises, container, false);
        String[] stepsArray = getResources().getStringArray(R.array.body_scan);
        textSwitcher = view.findViewById(R.id.text_switcher);
        textSwitcher.setText(stepsArray[0]);
        nextButton = view.findViewById(R.id.next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            int counter = 0;
            @Override
            public void onClick(View v) {
                counter++;
                if (counter < stepsArray.length) {
                } else {
                    counter = 2;
                }
                textSwitcher.setText(stepsArray[counter]);
            }
        });
        return view;
    }
}