package com.example.lostandfound;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lostandfound.api.ApiService;
import com.example.lostandfound.api.RetrofitClient;
import com.example.lostandfound.api.SignupRequest;
import com.example.lostandfound.models.SignupResponse;
import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerFrameLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {

    EditText nameField, emailField, passwordField;
    Button signupBtn;
    ShimmerFrameLayout shimmerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        nameField = findViewById(R.id.name);
        emailField = findViewById(R.id.email);
        passwordField = findViewById(R.id.password);
        signupBtn = findViewById(R.id.signupBtn);
        shimmerLayout = findViewById(R.id.shimmerLayout);

        //  Custom shimmer effect
        Shimmer shimmer = new Shimmer.AlphaHighlightBuilder()
                .setDuration(1200)
                .setBaseAlpha(0.7f)
                .setHighlightAlpha(1f)
                .setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
                .build();

        shimmerLayout.setShimmer(shimmer);
        shimmerLayout.startShimmer();

        signupBtn.setOnClickListener(v -> {

            //  Stop shimmer & disable button (loading state)
            shimmerLayout.stopShimmer();
            signupBtn.setEnabled(false);

            registerUser();
        });
    }

    private void registerUser() {

        String name = nameField.getText().toString().trim();
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {

            shimmerLayout.startShimmer();   //  restart shimmer
            signupBtn.setEnabled(true);

            Toast.makeText(this, "All fields required", Toast.LENGTH_SHORT).show();
            return;
        }

        SignupRequest request = new SignupRequest(name, email, password);

        ApiService api = RetrofitClient.getInstance().create(ApiService.class);

        api.registerUser(request).enqueue(new Callback<SignupResponse>() {

            @Override
            public void onResponse(Call<SignupResponse> call, Response<SignupResponse> response) {

                //  re-enable button
                signupBtn.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {

                    String userId = response.body().getUserId();

                    Log.d("OTP_DEBUG", "UserId: " + userId);
                    Log.d("OTP_DEBUG", "Opening OTP Activity...");

                    if (userId == null || userId.isEmpty()) {

                        shimmerLayout.startShimmer();

                        Toast.makeText(SignupActivity.this,
                                "UserId not received",
                                Toast.LENGTH_LONG).show();
                        return;
                    }


                    // ✅ SAVE EMAIL FOR RESEND OTP
                    getSharedPreferences("APP", MODE_PRIVATE)
                            .edit()
                            .putString("EMAIL", email)
                            .apply();

                    Toast.makeText(SignupActivity.this,
                            "OTP sent to email 📧",
                            Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(SignupActivity.this, OTPActivity.class);
                    intent.putExtra("USER_ID", userId);
                    startActivity(intent);
                    finish();

                } else {

                    shimmerLayout.startShimmer(); //  restart shimmer

                    try {
                        String error = response.errorBody().string();
                        Toast.makeText(SignupActivity.this,
                                "Error: " + error,
                                Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(SignupActivity.this,
                                "Signup Failed",
                                Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<SignupResponse> call, Throwable t) {

                signupBtn.setEnabled(true);
                shimmerLayout.startShimmer(); //  restart shimmer

                Toast.makeText(SignupActivity.this,
                        "Error: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}