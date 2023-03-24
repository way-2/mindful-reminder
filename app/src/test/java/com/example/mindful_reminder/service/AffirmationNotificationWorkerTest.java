package com.example.mindful_reminder.service;

import android.content.Context;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class AffirmationNotificationWorkerTest {

    @Mock
    Context context;

    AffirmationNotificationWorker affirmationNotificationWorker;

    @Test
    public void affirmationNotificationWorkerTest() {
        MockitoAnnotations.initMocks(this);
        affirmationNotificationWorker = new AffirmationNotificationWorker(context, null);
    }

}
