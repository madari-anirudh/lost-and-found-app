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

public class NewPasswordActivity extends AppCompatActivity {

    EditText newPassword, confirmPassword;

    Button resetBtn;

    ShimmerFrameLayout shimmerLayout;

    String email, otp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);

        newPassword = findViewById(R.id.newPassword);

        confirmPassword = findViewById(R.id.confirmPassword);

        resetBtn = findViewById(R.id.resetBtn);

        shimmerLayout = findViewById(R.id.shimmerLayout);

        // 📩 GET DATA

        email = getIntent().getStringExtra("EMAIL");

        otp = getIntent().getStringExtra("OTP");

        // ✨ SHIMMER

        Shimmer shimmer = new Shimmer.AlphaHighlightBuilder()
                .setDuration(1200)
                .setBaseAlpha(0.7f)
                .setHighlightAlpha(1f)
                .setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
                .build();

        shimmerLayout.setShimmer(shimmer);

        shimmerLayout.startShimmer();

        // 🔐 RESET PASSWORD

        resetBtn.setOnClickListener(v -> resetPassword());
    }

    private void resetPassword() {

        String pass1 = newPassword.getText().toString().trim();

        String pass2 = confirmPassword.getText().toString().trim();

        // ✅ VALIDATION

        if (pass1.isEmpty() || pass2.isEmpty()) {

            Toast.makeText(
                    this,
                    "Enter all fields",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (!pass1.equals(pass2)) {

            Toast.makeText(
                    this,
                    "Passwords do not match",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (pass1.length() < 6) {

            Toast.makeText(
                    this,
                    "Password must be at least 6 characters",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        shimmerLayout.stopShimmer();

        resetBtn.setEnabled(false);

        ApiService api = RetrofitClient
                .getInstance()
                .create(ApiService.class);

        HashMap<String, String> body = new HashMap<>();

        body.put("email", email);

        body.put("otp", otp);

        body.put("newPassword", pass1);

        api.resetPassword(body).enqueue(new Callback<Void>() {

            @Override
            public void onResponse(
                    Call<Void> call,
                    Response<Void> response
            ) {

                resetBtn.setEnabled(true);

                shimmerLayout.startShimmer();

                if (response.isSuccessful()) {

                    Toast.makeText(
                            NewPasswordActivity.this,
                            "Password Reset Successful ✅",
                            Toast.LENGTH_LONG
                    ).show();

                    // 🚀 REDIRECT TO LOGIN PAGE

                    Intent intent = new Intent(
                            NewPasswordActivity.this,
                            MainActivity.class
                    );

                    intent.addFlags(
                            Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                    Intent.FLAG_ACTIVITY_NEW_TASK
                    );

                    startActivity(intent);

                    finish();

                } else {

                    Toast.makeText(
                            NewPasswordActivity.this,
                            "Failed to reset password",
                            Toast.LENGTH_LONG
                    ).show();
                }
            }

            @Override
            public void onFailure(
                    Call<Void> call,
                    Throwable t
            ) {

                resetBtn.setEnabled(true);

                shimmerLayout.startShimmer();

                Toast.makeText(
                        NewPasswordActivity.this,
                        t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }
}