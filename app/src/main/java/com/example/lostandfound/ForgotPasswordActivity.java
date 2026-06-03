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

import org.json.JSONObject;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText email;
    Button sendOtpBtn;

    ShimmerFrameLayout shimmerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        email = findViewById(R.id.email);

        sendOtpBtn = findViewById(R.id.sendOtpBtn);

        shimmerLayout = findViewById(R.id.shimmerLayout);

        // ✨ PREMIUM SHIMMER

        Shimmer shimmer = new Shimmer.AlphaHighlightBuilder()
                .setDuration(1200)
                .setBaseAlpha(0.7f)
                .setHighlightAlpha(1f)
                .setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
                .build();

        shimmerLayout.setShimmer(shimmer);

        shimmerLayout.startShimmer();

        // 📩 SEND RESET OTP

        sendOtpBtn.setOnClickListener(v -> sendOtp());
    }

    private void sendOtp() {

        String emailStr = email.getText().toString().trim();

        if (emailStr.isEmpty()) {

            Toast.makeText(
                    this,
                    "Enter your email",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        shimmerLayout.stopShimmer();

        sendOtpBtn.setEnabled(false);

        ApiService api = RetrofitClient
                .getInstance()
                .create(ApiService.class);

        HashMap<String, String> body = new HashMap<>();

        body.put("email", emailStr);

        api.forgotPassword(body).enqueue(new Callback<Void>() {

            @Override
            public void onResponse(
                    Call<Void> call,
                    Response<Void> response
            ) {

                sendOtpBtn.setEnabled(true);

                shimmerLayout.startShimmer();

                if (response.isSuccessful()) {

                    Toast.makeText(
                            ForgotPasswordActivity.this,
                            "Reset OTP sent 📧",
                            Toast.LENGTH_LONG
                    ).show();

                    Intent intent = new Intent(
                            ForgotPasswordActivity.this,
                            ResetOtpActivity.class
                    );

                    intent.putExtra("EMAIL", emailStr);

                    startActivity(intent);

                } else {

                    Toast.makeText(
                            ForgotPasswordActivity.this,
                            "Email not found",
                            Toast.LENGTH_LONG
                    ).show();
                }
            }

            @Override
            public void onFailure(
                    Call<Void> call,
                    Throwable t
            ) {

                sendOtpBtn.setEnabled(true);

                shimmerLayout.startShimmer();

                Toast.makeText(
                        ForgotPasswordActivity.this,
                        t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }
}