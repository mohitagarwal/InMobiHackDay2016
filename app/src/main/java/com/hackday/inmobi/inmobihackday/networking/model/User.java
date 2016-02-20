package com.hackday.inmobi.inmobihackday.networking.model;

import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * Created by mohit.tibrewal on 20/02/16.
 */
@Data
public class User {

    @SerializedName("username")
    private String username;

    @SerializedName("user_id")
    private String userId;

    @SerializedName("organisation_id")
    private String organisationId;
}
