package com.hackday.inmobi.inmobihackday.networking.model;

import com.google.gson.annotations.SerializedName;
import com.hackday.inmobi.inmobihackday.Config;

import lombok.Data;

/**
 * Created by mohit.tibrewal on 20/02/16.
 */
@Data
public class User {

    private String name;

    @SerializedName("user_id")
    private String userId;

    @SerializedName("org_id")
    private String orgId;
}
