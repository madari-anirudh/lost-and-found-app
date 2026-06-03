package com.example.lostandfound;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lostandfound.api.ApiService;
import com.example.lostandfound.api.RetrofitClient;
import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetOtpActivity extends AppCompatActivity {

    EditText otp;

    Button verifyBtn;

    ShimmerFrameLayout shimmerLayout;

    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_otp);

        otp = findViewById(R.id.otp);

        verifyBtn = findViewById(R.id.verifyBtn);

        shimmerLayout = findViewById(R.id.shimmerLayout);

        // 📧 GET EMAIL FROM PREVIOUS SCREEN

        email = getIntent().getStringExtra("EMAIL");

        // ✨ SHIMMER

        Shimmer shimmer = new Shimmer.AlphaHighlightBuilder()
                .setDuration(1200)
                .setBaseAlpha(0.7f)
                .setHighlightAlpha(1f)
                .setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
                .build();

        shimmerLayout.setShimmer(shimmer);

        shimmerLayout.startShimmer();

        // ✅ VERIFY OTP

        verifyBtn.setOnClickListener(v -> verifyOtp());
    }

    private void verifyOtp() {

        String otpStr = otp.getText().toString().trim();

        if (otpStr.isEmpty()) {

            Toast.makeText(
                    this,
                    "Enter OTP",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        shimmerLayout.stopShimmer();

        verifyBtn.setEnabled(false);

        ApiService api = RetrofitClient
                .getInstance()
                .create(ApiService.class);

        HashMap<String, String> body = new HashMap<>();

        body.put("email", email);

        body.put("otp", otpStr);

        api.verifyResetOtp(body).enqueue(new Callback<Void>() {

            @Override
            public void onResponse(
                    Call<Void> call,
                    Response<Void> response
            ) {

                verifyBtn.setEnabled(true);

                shimmerLayout.startShimmer();

                if (response.isSuccessful()) {

                    Toast.makeText(
                            ResetOtpActivity.this,
                            "OTP Verified ✅",
                            Toast.LENGTH_LONG
                    ).show();

                    Intent intent = new Intent(
                            ResetOtpActivity.this,
                            NewPasswordActivity.class
                    );

                    intent.putExtra("EMAIL", email);

                    intent.putExtra("OTP", otpStr);

                    startActivity(intent);

                    finish();

                } else {

                    Toast.makeText(
                            ResetOtpActivity.this,
                            "Invalid OTP",
                            Toast.LENGTH_LONG
                    ).show();
                }
            }

            @Override
            public void onFailure(
                    Call<Void> call,
                    Throwable t
            ) {

                verifyBtn.setEnabled(true);

                shimmerLayout.startShimmer();

                Toast.makeText(
                        ResetOtpActivity.this,
                        t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }
}