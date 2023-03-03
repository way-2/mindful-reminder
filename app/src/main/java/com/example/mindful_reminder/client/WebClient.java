package com.example.mindful_reminder.client;

import com.example.mindful_reminder.model.MotivationResponse;
import com.example.mindful_reminder.service.AffirmationsDevInterface;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WebClient {

    private String API_URL = "https://www.affirmations.dev";
    AffirmationsDevInterface affirmationsDevInterface;

    public WebClient() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();
        this.affirmationsDevInterface = retrofit.create(AffirmationsDevInterface.class);
    }

    public String getMotivated() {
        try {
            Call<MotivationResponse> motivationResponse = affirmationsDevInterface.getMotivation();
            Response<MotivationResponse> motivationResponseResponse = motivationResponse.execute();
            return motivationResponseResponse.body().getAffirmation();
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

}
