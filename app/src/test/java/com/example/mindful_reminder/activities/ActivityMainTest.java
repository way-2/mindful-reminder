package com.example.mindful_reminder.activities;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import androidx.core.app.ActivityCompat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ActivityMainTest {

    @Test
    void onCreateTest() {
        ActivityMain activityMain = spy(new ActivityMain());
        activityMain.onCreate(null);
        verify(ActivityCompat.checkSelfPermission(any(),any()), times(1));
    }

    @Test
    void onCreateOptionsMenu() {
    }

    @Test
    void onBackPressed() {
    }

    @Test
    void startAffirmationWorker() {
    }
}