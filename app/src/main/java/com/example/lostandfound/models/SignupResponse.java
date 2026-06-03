package com.example.lostandfound.models;

import com.google.gson.annotations.SerializedName;

public class SignupResponse {

    @SerializedName("userId")
    private String userId;

    @SerializedName("message")
    private String message;

    public String getUserId() {
        return userId;
    }

    public String getMessage() {
        return message;
    }
}