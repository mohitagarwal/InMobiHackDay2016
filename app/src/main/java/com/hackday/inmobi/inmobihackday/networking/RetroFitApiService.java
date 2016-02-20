package com.hackday.inmobi.inmobihackday.networking;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by mohit.tibrewal on 20/02/16.
 */
public class RetroFitApiService {
    public static final String API_URL = "http://10.14.121.192:46000";

    public static ApiEndpoints instance;

    public static ApiEndpoints getInstance() {
        if (instance == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            instance = retrofit.create(ApiEndpoints.class);
        }
        return instance;
    }
}
