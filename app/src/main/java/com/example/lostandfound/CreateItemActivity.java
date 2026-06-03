package com.example.lostandfound;

import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lostandfound.api.ApiService;
import com.example.lostandfound.api.ItemResponse;
import com.example.lostandfound.api.RetrofitClient;

import okhttp3.*;
import retrofit2.*;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateItemActivity extends AppCompatActivity {

    EditText titleInput, descriptionInput, locationInput, phoneInput;
    Button createBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_item);

        titleInput = findViewById(R.id.titleInput);
        descriptionInput = findViewById(R.id.descriptionInput);
        locationInput = findViewById(R.id.locationInput);
        phoneInput = findViewById(R.id.phoneInput);
        createBtn = findViewById(R.id.createBtn);

        createBtn.setOnClickListener(v -> createItem());
    }

    private void createItem() {

        String title = titleInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();
        String location = locationInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();
        String type = "lost";

        if (title.isEmpty() || description.isEmpty() || location.isEmpty()) {
            Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String token = getSharedPreferences("APP", MODE_PRIVATE)
                .getString("TOKEN", "");

        createBtn.setEnabled(false);
        createBtn.setText("Submitting...");

        //  RequestBody
        RequestBody titleBody = RequestBody.create(MediaType.parse("text/plain"), title);
        RequestBody descBody = RequestBody.create(MediaType.parse("text/plain"), description);
        RequestBody locBody = RequestBody.create(MediaType.parse("text/plain"), location);
        RequestBody phoneBody = RequestBody.create(MediaType.parse("text/plain"), phone);
        RequestBody typeBody = RequestBody.create(MediaType.parse("text/plain"), type);

        ApiService api = RetrofitClient.getInstance().create(ApiService.class);

        // NO IMAGE → pass null
        api.createItem(
                "Bearer " + token,
                titleBody,
                descBody,
                locBody,
                phoneBody,
                typeBody,
                null
        ).enqueue(new Callback<ItemResponse>() {

            @Override
            public void onResponse(Call<ItemResponse> call, Response<ItemResponse> response) {

                createBtn.setEnabled(true);
                createBtn.setText("Create");

                if (response.isSuccessful()) {
                    Toast.makeText(CreateItemActivity.this,
                            "✅ Item created", Toast.LENGTH_LONG).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ItemResponse> call, Throwable t) {

                createBtn.setEnabled(true);
                createBtn.setText("Create");

                Toast.makeText(CreateItemActivity.this,
                        "Error: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}