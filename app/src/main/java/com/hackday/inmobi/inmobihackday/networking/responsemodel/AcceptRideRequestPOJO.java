package com.hackday.inmobi.inmobihackday.networking.responsemodel;

import com.hackday.inmobi.inmobihackday.networking.model.User;

import lombok.Data;

/**
 * Created by mohit.tibrewal on 20/02/16.
 */
@Data
public class AcceptRideRequestPOJO {

    RideDetails ride;
    User acceptingUser;
    User requestingUser;
}
