package com.way2.mindful_reminder.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextSwitcher;

import androidx.fragment.app.Fragment;

import com.way2.mindful_reminder.R;
import com.way2.mindful_reminder.views.CircleProgressBar;

public class GroundingFragment extends Fragment {
    private TextSwitcher textSwitcher;
    private CircleProgressBar donutProgress;
    private int counter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grounding, container, false);
        textSwitcher = view.findViewById(R.id.grounding_text_switcher);
        textSwitcher.setText(getString(R.string.groundingBody));
        donutProgress = view.findViewById(R.id.progress_donut);
        donutProgress.setOnClickListener(v -> seeStep());
        return view;
    }

    private void seeStep() {
        textSwitcher.setText(getString(R.string.groundingSee));
        counter = 0;
        donutProgress.setProgress(0);
        donutProgress.setProgressText(String.valueOf(counter));
        donutProgress.setOnClickListener(v -> {
            if (counter < 5) {
                counter++;
                float percent = ((float) counter/ (float) 5) * 100;
                donutProgress.setProgress(percent);
                donutProgress.setProgressText(String.valueOf(counter));
            } else {
                donutProgress.setProgress(0);
                touchStep();
            }
        });
    }

    private void touchStep() {
        textSwitcher.setText(getString(R.string.groundingTouch));
        counter = 0;
        donutProgress.setProgressText(String.valueOf(counter));
        donutProgress.setOnClickListener(v -> {
            if (counter < 4) {
                counter++;
                float percent = ((float) counter/ (float) 4) * 100;
                donutProgress.setProgress(percent);
                donutProgress.setProgressText(String.valueOf(counter));
            } else {
                donutProgress.setProgress(0);
                hearStep();
            }
        });
    }

    private void hearStep() {
        textSwitcher.setText(getString(R.string.groundingHear));
        counter = 0;
        donutProgress.setProgressText(String.valueOf(counter));
        donutProgress.setOnClickListener(v -> {
            if (counter < 3) {
                counter++;
                float percent = ((float) counter/ (float) 3) * 100;
                donutProgress.setProgress(percent);
                donutProgress.setProgressText(String.valueOf(counter));
            } else {
                donutProgress.setProgress(0);
                smellStep();
            }
        });
    }

    private void smellStep() {
        textSwitcher.setText(getString(R.string.groundingSmell));
        counter = 0;
        donutProgress.setProgressText(String.valueOf(counter));
        donutProgress.setOnClickListener(v -> {
            if (counter < 2) {
                counter++;
                float percent = ((float) counter/ (float) 2) * 100;
                donutProgress.setProgress(percent);
                donutProgress.setProgressText(String.valueOf(counter));
            } else {
                donutProgress.setProgress(0);
                tasteStep();
            }
        });
    }

    private void tasteStep() {
        textSwitcher.setText(getString(R.string.groundingTaste));
        counter = 0;
        donutProgress.setProgressText(String.valueOf(counter));
        donutProgress.setOnClickListener(v -> {
            if (counter < 1) {
                counter++;
                float percent = ((float) counter) * 100;
                donutProgress.setProgress(percent);
                donutProgress.setProgressText(String.valueOf(counter));
            } else {
                donutProgress.setProgress(0);
                completeStep();
            }
        });
    }

    private void completeStep() {
        textSwitcher.setText(getString(R.string.groundingComplete));
        donutProgress.setProgressText(getString(R.string.start));
        donutProgress.setOnClickListener(v -> {
            donutProgress.setProgress(0);
            seeStep();
        });
    }

}