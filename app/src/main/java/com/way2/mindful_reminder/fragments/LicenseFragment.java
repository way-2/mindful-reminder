package com.way2.mindful_reminder.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.way2.mindful_reminder.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class LicenseFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_license, container, false);
        setupText(view);
        return view;
    }

    private void setupText(View view) {
        TextView licenseTextView = view.findViewById(R.id.license_text_view);
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(getActivity().getAssets().open("LICENSE")));
            String str;
            while ((str = reader.readLine()) != null) {
                stringBuilder.append(str);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        licenseTextView.setText(stringBuilder.toString());
    }
}