package com.hackday.inmobi.inmobihackday.networking.responsemodel;

import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * Created by mohit.tibrewal on 20/02/16.
 */
@Data
public class RideDetails {

    @SerializedName("ride_id")
    String rideId;
}
