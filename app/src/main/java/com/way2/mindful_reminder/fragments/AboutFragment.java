package com.way2.mindful_reminder.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;
import com.way2.mindful_reminder.R;

public class AboutFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        setupButtons(view);
        return view;
    }

    private void setupButtons(View view) {
        Button openSourceCreditsButton = view.findViewById(R.id.open_source_credits_button);
        openSourceCreditsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), OssLicensesMenuActivity.class));
            }
        });

        Button licenseButton = view.findViewById(R.id.license_button);
        licenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getParentFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.fragment_frame, new LicenseFragment()).addToBackStack(null).commit();
            }
        });
    }

}