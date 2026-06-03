package com.example.lostandfound;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lostandfound.api.ApiService;
import com.example.lostandfound.api.LoginRequest;
import com.example.lostandfound.api.RetrofitClient;
import com.example.lostandfound.models.UserResponse;
import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerFrameLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    EditText email, password;

    Button loginBtn;
    Button createAccountBtn;

    TextView forgotPassword;

    ShimmerFrameLayout shimmerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        loginBtn = findViewById(R.id.loginBtn);
        createAccountBtn = findViewById(R.id.createAccountBtn);

        forgotPassword = findViewById(R.id.forgotPassword);

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

        // 🔐 LOGIN
        loginBtn.setOnClickListener(v -> {

            shimmerLayout.stopShimmer();

            loginBtn.setEnabled(false);

            loginUser();
        });

        // 📝 CREATE ACCOUNT
        createAccountBtn.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    SignupActivity.class
            );

            startActivity(intent);
        });

        // 🔑 FORGOT PASSWORD
        forgotPassword.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    ForgotPasswordActivity.class
            );

            startActivity(intent);
        });
    }

    private void loginUser() {

        String emailStr = email.getText().toString().trim();

        String passwordStr = password.getText().toString().trim();

        // ✅ VALIDATION
        if (emailStr.isEmpty() || passwordStr.isEmpty()) {

            shimmerLayout.startShimmer();

            loginBtn.setEnabled(true);

            Toast.makeText(
                    this,
                    "Enter email & password",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        ApiService api = RetrofitClient
                .getInstance()
                .create(ApiService.class);

        LoginRequest request = new LoginRequest(
                emailStr,
                passwordStr
        );

        api.loginUser(request).enqueue(
                new Callback<UserResponse>() {

                    @Override
                    public void onResponse(
                            Call<UserResponse> call,
                            Response<UserResponse> response
                    ) {

                        loginBtn.setEnabled(true);

                        if (response.isSuccessful()
                                && response.body() != null) {

                            String token = response.body().token;

                            if (token == null || token.isEmpty()) {

                                shimmerLayout.startShimmer();

                                Toast.makeText(
                                        MainActivity.this,
                                        "Invalid server response",
                                        Toast.LENGTH_LONG
                                ).show();

                                return;
                            }

                            Log.d("TOKEN_DEBUG", token);

                            // ✅ SAVE LOGIN
                            getSharedPreferences("APP", MODE_PRIVATE)
                                    .edit()
                                    .putString("TOKEN", token)
                                    .putString("NAME", response.body().name)
                                    .putString("EMAIL", response.body().email)
                                    .apply();

                            Toast.makeText(
                                    MainActivity.this,
                                    "Login Success ✅",
                                    Toast.LENGTH_LONG
                            ).show();

                            startActivity(
                                    new Intent(
                                            MainActivity.this,
                                            DashboardActivity.class
                                    )
                            );

                            finish();

                        } else {

                            shimmerLayout.startShimmer();

                            try {

                                String error =
                                        response.errorBody().string();

                                // 📧 EMAIL NOT VERIFIED
                                if (error.contains("verify")) {

                                    Toast.makeText(
                                            MainActivity.this,
                                            "Verify your email first",
                                            Toast.LENGTH_LONG
                                    ).show();

                                    Intent intent =
                                            new Intent(
                                                    MainActivity.this,
                                                    OTPActivity.class
                                            );

                                    startActivity(intent);

                                } else {

                                    Toast.makeText(
                                            MainActivity.this,
                                            error,
                                            Toast.LENGTH_LONG
                                    ).show();
                                }

                            } catch (Exception e) {

                                Toast.makeText(
                                        MainActivity.this,
                                        "Login Failed ❌",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<UserResponse> call,
                            Throwable t
                    ) {

                        loginBtn.setEnabled(true);

                        shimmerLayout.startShimmer();

                        Toast.makeText(
                                MainActivity.this,
                                "Error: " + t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }
}