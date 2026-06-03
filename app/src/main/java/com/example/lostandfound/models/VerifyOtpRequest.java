package com.example.lostandfound.models;

public class VerifyOtpRequest {
    String userId;
    String otp;

    public VerifyOtpRequest(String userId, String otp) {
        this.userId = userId;
        this.otp = otp;
    }
}