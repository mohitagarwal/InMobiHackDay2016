package com.hackday.inmobi.inmobihackday.networking;

import com.hackday.inmobi.inmobihackday.networking.responsemodel.AcceptRideRequestPOJO;
import com.hackday.inmobi.inmobihackday.networking.responsemodel.AvailableRides;
import com.hackday.inmobi.inmobihackday.networking.responsemodel.RegisterRidePOJO;
import com.hackday.inmobi.inmobihackday.networking.responsemodel.RideDetails;
import com.hackday.inmobi.inmobihackday.networking.responsemodel.UpdateRiderLocationPOJO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by mohit.tibrewal on 20/02/16.
 */
public interface ApiEndpoints {

    @Headers({"Content-Type: application/json"})
    @GET("rides")
    Call<List<AvailableRides>> requestRide(@Query("user_id") String userId,
                                           @Query("timestamp") long timestamp,
                                           @Query("start_lat") double currentLocationLat,
                                           @Query("start_lng") double currentLocationLong,
                                           @Query("end_lat") double endLocationLat,
                                           @Query("end_lng") double endLocationLong);

    @Headers({"Content-Type: application/json"})
    @POST("rides")
    Call<RideDetails> registerRide(@Body RegisterRidePOJO postBody);

    @Headers({"Content-Type: application/json"})
    @POST("rides/updateRiderLocation")
    Call<String> updateRiderLocation(@Body UpdateRiderLocationPOJO riderLocation);

    @Headers({"Content-Type: application/json"})
    @POST("rides/acceptRideRequest")
    Call<String> acceptRideRequest(@Body AcceptRideRequestPOJO postBody);
}
