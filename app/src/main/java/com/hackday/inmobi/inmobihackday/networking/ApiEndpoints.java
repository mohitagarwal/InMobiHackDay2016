package com.hackday.inmobi.inmobihackday.networking;

import com.hackday.inmobi.inmobihackday.networking.response.BaseResponse;

import retrofit2.Call;
import retrofit2.http.POST;

/**
 * Created by mohit.tibrewal on 20/02/16.
 */
public interface ApiEndpoints {

    @POST("rides")
    Call<BaseResponse> registerRide();
}
