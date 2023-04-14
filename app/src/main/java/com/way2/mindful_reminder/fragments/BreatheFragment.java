package com.way2.mindful_reminder.fragments;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.github.lzyzsd.circleprogress.DonutProgress;
import com.way2.mindful_reminder.R;

public class BreatheFragment extends Fragment {
    private DonutProgress donutProgress;
    private CountDownTimer countdownIn;
    private CountDownTimer countdownHold;
    private CountDownTimer countdownOut;
    private int loopCount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_breathe, container, false);
        donutProgress = view.findViewById(R.id.progress_donut);
        createCountDownTimers();
        donutProgress.setOnClickListener(v -> runBreathingSession());
        return view;
    }

    private void runBreathingSession() {
        loopCount = 0;
        donutProgress.setClickable(false);
        countdownIn.start();
    }

    private void createCountDownTimers() {
        countdownOut = new CountDownTimer((8L * 1000L), 50L) {
            @Override
            public void onTick(long millisUntilFinished) {
                String textString = "Breathe out for " + (millisUntilFinished / 1000);
                donutProgress.setText(textString);
                float progressFraction = (8 - ((float) millisUntilFinished / 1000)) / 8;
                float percentComplete = (progressFraction * 100);
                donutProgress.setProgress(percentComplete);
            }

            @Override
            public void onFinish() {
                loopCount++;
                if (loopCount > 3) {
                    donutProgress.setText("Done");
                    donutProgress.setProgress(0);
                    donutProgress.setClickable(true);
                } else {
                    donutProgress.setProgress(100);
                    countdownIn.start();
                }
            }
        };
        countdownHold = new CountDownTimer((7L * 1000L), 50L) {
            @Override
            public void onTick(long millisUntilFinished) {
                String textString = "Hold breath for " + (millisUntilFinished / 1000);
                donutProgress.setText(textString);
                float progressFraction = (7 - ((float) millisUntilFinished / 1000)) / 7;
                float percentComplete = (progressFraction * 100);
                donutProgress.setProgress(percentComplete);
            }

            @Override
            public void onFinish() {
                donutProgress.setText(String.valueOf(0));
                donutProgress.setProgress(100);
                countdownOut.start();
            }
        };
        countdownIn = new CountDownTimer((4L * 1000L), 50L) {
            @Override
            public void onTick(long millisUntilFinished) {
                String textString = "Breathe in for " + (millisUntilFinished / 1000);
                donutProgress.setText(textString);
                float progressFraction = (4 - ((float) millisUntilFinished / 1000)) / 4;
                float percentComplete = (progressFraction * 100);
                donutProgress.setProgress(percentComplete);
            }

            @Override
            public void onFinish() {
                donutProgress.setText(String.valueOf(0));
                donutProgress.setProgress(100);
                countdownHold.start();
            }
        };

    }
}