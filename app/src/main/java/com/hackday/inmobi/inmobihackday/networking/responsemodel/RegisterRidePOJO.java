package com.hackday.inmobi.inmobihackday.networking.responsemodel;

import com.hackday.inmobi.inmobihackday.networking.model.Location;
import com.hackday.inmobi.inmobihackday.networking.model.User;

import lombok.Data;

/**
 * Created by mohit.tibrewal on 20/02/16.
 */
@Data
public class RegisterRidePOJO {

    User user;
    long timestamp;
    Location currentLocation;
    Location destination;
}
