package com.example.mindful_reminder.service;

import com.example.mindful_reminder.model.MotivationResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface AffirmationsDevInterface {
    @GET("/")
    Call<MotivationResponse> getMotivation();
}
