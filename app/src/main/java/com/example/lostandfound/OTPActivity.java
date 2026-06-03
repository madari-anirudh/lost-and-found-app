package com.example.lostandfound;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lostandfound.api.ApiService;
import com.example.lostandfound.api.RetrofitClient;
import com.example.lostandfound.models.ResendOtpRequest;
import com.example.lostandfound.models.VerifyOtpRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OTPActivity extends AppCompatActivity {

    EditText otp1, otp2, otp3, otp4;
    Button verifyBtn;
    TextView resendBtn;
    ProgressBar resendLoader;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("OTP_DEBUG", "STEP 1 - Before setContentView");

        setContentView(R.layout.activity_otp);

        Log.d("OTP_DEBUG", "STEP 2 - After setContentView");

        //  4 OTP fields
        otp1 = findViewById(R.id.otp1);
        otp2 = findViewById(R.id.otp2);
        otp3 = findViewById(R.id.otp3);
        otp4 = findViewById(R.id.otp4);

        verifyBtn = findViewById(R.id.verifyBtn);
        resendBtn = findViewById(R.id.resendBtn);

        Log.d("OTP_DEBUG", "STEP 3 - Views initialized");

        userId = getIntent().getStringExtra("USER_ID");
        Log.d("OTP_DEBUG", "Received USER_ID: " + userId);

        //  AUTO MOVE CURSOR
        moveNext(otp1, otp2);
        moveNext(otp2, otp3);
        moveNext(otp3, otp4);

        //  VERIFY BUTTON
        verifyBtn.setOnClickListener(v -> verifyOtp());

        //  RESEND BUTTON
        resendBtn.setOnClickListener(v -> {
            resendOtp();
        });
        resendLoader = findViewById(R.id.resendLoader);

        //  SAFETY CHECK
        if (userId == null) {
            Toast.makeText(this, "Error: Missing userId", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    //  AUTO MOVE FUNCTION
    private void moveNext(EditText current, EditText next) {
        current.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(android.text.Editable s) {
                if (s.length() == 1) {
                    next.requestFocus();
                }
            }
        });
    }

// ============================= ressend otp =====================
private void resendOtp() {

    // 🔥 Show loader & disable button
    resendLoader.setVisibility(View.VISIBLE);
    resendBtn.setEnabled(false);

    ApiService api = RetrofitClient.getInstance().create(ApiService.class);

    String email = getSharedPreferences("APP", MODE_PRIVATE)
            .getString("EMAIL", "");

    if (email.isEmpty()) {
        resendLoader.setVisibility(View.GONE);
        resendBtn.setEnabled(true);
        Toast.makeText(this, "Email not found", Toast.LENGTH_LONG).show();
        return;
    }

    ResendOtpRequest request = new ResendOtpRequest(email);

    api.resendOtp(request).enqueue(new Callback<Object>() {

        @Override
        public void onResponse(Call<Object> call, Response<Object> response) {

            // 🔥 Hide loader
            resendLoader.setVisibility(View.GONE);
            resendBtn.setEnabled(true);

            if (response.isSuccessful()) {
                Toast.makeText(OTPActivity.this,
                        "OTP resent 📧",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(OTPActivity.this,
                        "Failed to resend",
                        Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(Call<Object> call, Throwable t) {

            resendLoader.setVisibility(View.GONE);
            resendBtn.setEnabled(true);

            Toast.makeText(OTPActivity.this,
                    "Error: " + t.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    });
}
// ============================= verify otp =====================
    private void verifyOtp() {

        //  COMBINE OTP
        String otp = otp1.getText().toString()
                + otp2.getText().toString()
                + otp3.getText().toString()
                + otp4.getText().toString();

        if (otp.length() != 4) {
            Toast.makeText(this, "Enter complete OTP", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService api = RetrofitClient.getInstance().create(ApiService.class);

        VerifyOtpRequest request = new VerifyOtpRequest(userId, otp);

        api.verifyOtp(request).enqueue(
                new Callback<com.example.lostandfound.models.VerifyOtpResponse>() {

                    @Override
                    public void onResponse(
                            Call<com.example.lostandfound.models.VerifyOtpResponse> call,
                            Response<com.example.lostandfound.models.VerifyOtpResponse> response) {

                        if (response.isSuccessful() && response.body() != null) {

                            String token = response.body().token;

                            getSharedPreferences("APP", MODE_PRIVATE)
                                    .edit()
                                    .putString("TOKEN", token)
                                    .putString("NAME", response.body().name)
                                    .putString("EMAIL", response.body().email)
                                    .apply();

                            Toast.makeText(OTPActivity.this,
                                    "Email Verified ✅",
                                    Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(OTPActivity.this, DashboardActivity.class));
                            finish();

                        } else {

                            try {
                                String error = response.errorBody().string();
                                Toast.makeText(OTPActivity.this,
                                        error,
                                        Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                Toast.makeText(OTPActivity.this,
                                        "Invalid OTP ❌",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<com.example.lostandfound.models.VerifyOtpResponse> call,
                            Throwable t) {

                        Toast.makeText(OTPActivity.this,
                                "Error: " + t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}