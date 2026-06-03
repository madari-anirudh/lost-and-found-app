package com.example.lostandfound;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lostandfound.api.ApiService;
import com.example.lostandfound.api.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MatchDetailsActivity extends AppCompatActivity {

    private TextView itemTitle, finderName, finderPhone, finderLocation;
    private Button callFinderBtn, confirmBtn;

    private String phone = "";
    private String itemId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_details);

        initializeViews();
        receiveIntentData();
        setupListeners();
    }

    private void initializeViews() {
        itemTitle = findViewById(R.id.itemTitle);
        finderName = findViewById(R.id.finderName);
        finderPhone = findViewById(R.id.finderPhone);
        finderLocation = findViewById(R.id.finderLocation);

        callFinderBtn = findViewById(R.id.callFinderBtn);
        confirmBtn = findViewById(R.id.confirmBtn);
    }

    private void receiveIntentData() {

        Intent intent = getIntent();

        if (intent == null) return;

        itemId = intent.getStringExtra("id");
        phone = intent.getStringExtra("phone");

        String title = intent.getStringExtra("title");
        String name = intent.getStringExtra("name");
        String location = intent.getStringExtra("location");
        String description = intent.getStringExtra("description");

        String type = intent.getStringExtra("type");

        // ITEM TITLE
        itemTitle.setText(
                title != null
                        ? title
                        : "No Title"
        );

        // NAME
        if (type != null && type.equals("lost")) {

            finderName.setText(
                    name != null
                            ? "Finder Item: " + name
                            : "Finder Item: Not available"
            );

        } else {

            finderName.setText(
                    name != null
                            ? "Lost User Item: " + name
                            : "Lost User Item: Not available"
            );
        }

        // PHONE
        finderPhone.setText(
                phone != null && !phone.isEmpty()
                        ? "Phone: " + phone
                        : "Phone: Not available"
        );

        // LOCATION
        finderLocation.setText(
                location != null
                        ? "Location: " + location
                        : "Location: Not available"
        );
    }

    private void setupListeners() {

        callFinderBtn.setOnClickListener(v -> {

            if (phone != null && !phone.isEmpty()) {

                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + phone));
                startActivity(callIntent);

            } else {
                Toast.makeText(
                        MatchDetailsActivity.this,
                        "Phone number not available",
                        Toast.LENGTH_LONG
                ).show();
            }
        });

        confirmBtn.setOnClickListener(v -> confirmItemReceived());
    }

    private void confirmItemReceived() {

        if (itemId == null || itemId.isEmpty()) {
            Toast.makeText(this, "Item ID missing", Toast.LENGTH_LONG).show();
            return;
        }

        String token = getSharedPreferences("APP", MODE_PRIVATE)
                .getString("TOKEN", "");

        ApiService api = RetrofitClient
                .getInstance()
                .create(ApiService.class);

        api.confirmItem("Bearer " + token, itemId)
                .enqueue(new Callback<Void>() {

                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {

                        if (response.isSuccessful()) {

                            Toast.makeText(
                                    MatchDetailsActivity.this,
                                    "Item marked as completed ✅",
                                    Toast.LENGTH_LONG
                            ).show();

                            finish();

                        } else {

                            Toast.makeText(
                                    MatchDetailsActivity.this,
                                    "Failed to update item",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {

                        Toast.makeText(
                                MatchDetailsActivity.this,
                                "Error: " + t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }
}