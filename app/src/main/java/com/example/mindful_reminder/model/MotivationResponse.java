package com.example.mindful_reminder.model;

import java.io.Serializable;

public class MotivationResponse implements Serializable {
    private String affirmation;

    public String getAffirmation() {
        return affirmation;
    }

    public void setAffirmation(String affirmation) {
        this.affirmation = affirmation;
    }

}
