package com.hackday.inmobi.inmobihackday.networking.pushmodel;

import com.hackday.inmobi.inmobihackday.networking.model.User;
import com.hackday.inmobi.inmobihackday.networking.responsemodel.RideDetails;

import lombok.Data;

/**
 * Created by mohit.tibrewal on 20/02/16.
 */
@Data
public class InformAcceptedRideRequest {

    RideDetails ride;
    User user;
}
