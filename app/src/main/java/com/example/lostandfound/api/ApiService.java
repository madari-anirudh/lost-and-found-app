package com.example.lostandfound.api;

import com.example.lostandfound.models.ItemModel;
import com.example.lostandfound.models.AppVersionModel;
import java.util.HashMap;
import java.util.List;
import com.example.lostandfound.models.NotificationModel;
import com.example.lostandfound.models.ResendOtpRequest;
import com.example.lostandfound.models.SignupResponse;
import com.example.lostandfound.models.UserResponse;
import com.example.lostandfound.models.VerifyOtpRequest;
import com.example.lostandfound.models.VerifyOtpResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {
    @GET("/api/app/version")
    Call<AppVersionModel> getLatestVersion();

    // AUTH

    @POST("users/register")
    Call<SignupResponse> registerUser(
            @Body SignupRequest request
    );

    @POST("users/login")
    Call<UserResponse> loginUser(
            @Body LoginRequest request
    );


// ITEMS

    @GET("items")
    Call<List<ItemModel>> getItems();


// FORGOT PASSWORD

    @POST("users/forgot-password")
    Call<Void> forgotPassword(
            @Body HashMap<String, String> body
    );

    @POST("users/verify-reset-otp")
    Call<Void> verifyResetOtp(
            @Body HashMap<String, String> body
    );

    @POST("users/reset-password")
    Call<Void> resetPassword(
            @Body HashMap<String, String> body
    );


// NOTIFICATIONS

    @GET("notifications")
    Call<List<NotificationModel>> getNotifications(
            @Header("Authorization") String token
    );


// OTP

    @POST("users/verify-otp")
    Call<VerifyOtpResponse> verifyOtp(
            @Body VerifyOtpRequest request
    );


// RESEND OTP

    @POST("users/resend-otp")
    Call<Object> resendOtp(
            @Body ResendOtpRequest request
    );


// MY ITEMS

    @GET("items/my-items")
    Call<List<ItemModel>> getMyItems(
            @Header("Authorization") String token
    );


// CREATE ITEM

    @Multipart
    @POST("items")
    Call<ItemResponse> createItem(

            @Header("Authorization") String token,

            @Part("title") RequestBody title,
            @Part("description") RequestBody description,
            @Part("location") RequestBody location,
            @Part("phone") RequestBody phone,
            @Part("type") RequestBody type,

            @Part MultipartBody.Part image
    );


// CONFIRM MATCH

    @PUT("items/confirm/{id}")
    Call<Void> confirmItem(

            @Header("Authorization") String token,

            @Path("id") String itemId
    );
}