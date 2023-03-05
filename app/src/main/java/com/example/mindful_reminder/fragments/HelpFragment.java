package com.example.mindful_reminder.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.mindful_reminder.R;

public class HelpFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_help, container, false);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        menu.findItem(R.id.settings).setVisible(false);
        menu.findItem(R.id.settings).setEnabled(false);
        menu.findItem(R.id.help).setVisible(false);
        menu.findItem(R.id.help).setEnabled(false);
        menu.findItem(R.id.about).setVisible(false);
        menu.findItem(R.id.about).setEnabled(false);
        super.onPrepareOptionsMenu(menu);
    }

}