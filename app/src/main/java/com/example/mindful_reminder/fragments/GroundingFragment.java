package com.example.mindful_reminder.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextSwitcher;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.example.mindful_reminder.R;

public class GroundingFragment extends Fragment {
    private TextSwitcher textSwitcher;
    private AppCompatButton button;
    private int counter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grounding, container, false);
        textSwitcher = (TextSwitcher) view.findViewById(R.id.grounding_text_switcher);
        textSwitcher.setText(getString(R.string.groundingBody));
        button = (AppCompatButton) view.findViewById(R.id.counter_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seeStep();
            }
        });
        return view;
    }

    private void seeStep() {
        textSwitcher.setText(getString(R.string.groundingSee));
        counter = 0;
        button.setText(String.valueOf(counter));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (counter < 5) {
                    counter++;
                    button.setText(String.valueOf(counter));
                } else {
                    touchStep();
                }
            }
        });
    }

    private void touchStep() {
        textSwitcher.setText(getString(R.string.groundingTouch));
        counter = 0;
        button.setText(String.valueOf(counter));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (counter < 4) {
                    counter++;
                    button.setText(String.valueOf(counter));
                } else {
                    hearStep();
                }
            }
        });
    }

    private void hearStep() {
        textSwitcher.setText(getString(R.string.groundingHear));
        counter = 0;
        button.setText(String.valueOf(counter));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (counter < 3) {
                    counter++;
                    button.setText(String.valueOf(counter));
                } else {
                    smellStep();
                }
            }
        });
    }

    private void smellStep() {
        textSwitcher.setText(getString(R.string.groundingSmell));
        counter = 0;
        button.setText(String.valueOf(counter));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (counter < 2) {
                    counter++;
                    button.setText(String.valueOf(counter));
                } else {
                    tasteStep();
                }
            }
        });
    }

    private void tasteStep() {
        textSwitcher.setText(getString(R.string.groundingTaste));
        counter = 0;
        button.setText(String.valueOf(counter));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (counter < 1) {
                    counter++;
                    button.setText(String.valueOf(counter));
                } else {
                    completeStep();
                }
            }
        });
    }

    private void completeStep() {
        textSwitcher.setText(getString(R.string.groundingComplete));
        button.setText(getString(R.string.start));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seeStep();
            }
        });
    }

}