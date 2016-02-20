package com.hackday.inmobi.inmobihackday.networking;

import com.hackday.inmobi.inmobihackday.networking.response.BaseResponse;
import com.hackday.inmobi.inmobihackday.networking.responsemodel.AvailableRides;
import com.hackday.inmobi.inmobihackday.networking.responsemodel.RegisterRidePOJO;
import com.hackday.inmobi.inmobihackday.networking.responsemodel.RideDetails;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by mohit.tibrewal on 20/02/16.
 */
public interface ApiEndpoints {

    @GET("requestRide")
    Call<List<AvailableRides>> requestRide(@Query("user_id") String userId,
                                           @Query("timestamp") long timestamp,
                                           @Query("start_lat") double currentLocationLat,
                                           @Query("start_lng") double currentLocationLong,
                                           @Query("end_lat") double endLocationLat,
                                           @Query("end_lng") double endLocationLong);

    @POST("registerRide")
    Call<RideDetails> registerRide(@Body RegisterRidePOJO postBody);

    
}
