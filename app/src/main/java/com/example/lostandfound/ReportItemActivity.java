package com.example.lostandfound;

import android.net.Uri;
import android.os.Bundle;
import android.widget.*;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lostandfound.api.ApiService;
import com.example.lostandfound.api.ItemResponse;
import com.example.lostandfound.api.RetrofitClient;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import okhttp3.*;
import retrofit2.*;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportItemActivity extends AppCompatActivity {

    EditText titleInput, descInput, locationInput, phoneInput;
    Spinner typeSpinner;
    Button reportBtn, selectImageBtn;
    ImageView itemImage;

    Uri imageUri;

    // ✅ Modern Image Picker
    private final ActivityResultLauncher<String> pickImageLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.GetContent(),
                    uri -> {
                        if (uri != null) {
                            imageUri = uri;
                            itemImage.setImageURI(imageUri);
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_item);

        titleInput = findViewById(R.id.titleInput);
        descInput = findViewById(R.id.descInput);
        locationInput = findViewById(R.id.locationInput);
        phoneInput = findViewById(R.id.phoneInput);
        typeSpinner = findViewById(R.id.typeSpinner);
        reportBtn = findViewById(R.id.reportBtn);
        selectImageBtn = findViewById(R.id.selectImageBtn);
        itemImage = findViewById(R.id.itemImage);

        String[] types = {"lost", "found"};
        typeSpinner.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                types
        ));

        selectImageBtn.setOnClickListener(v -> openGallery());
        reportBtn.setOnClickListener(v -> submitItem());
    }

    private void openGallery() {
        pickImageLauncher.launch("image/*");
    }

    private void submitItem() {

        String title = titleInput.getText().toString().trim();
        String description = descInput.getText().toString().trim();
        String location = locationInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();
        String type = typeSpinner.getSelectedItem().toString();

        // 🔴 VALIDATION
        if (title.isEmpty()) {
            titleInput.setError("Enter title");
            return;
        }

        if (description.isEmpty()) {
            descInput.setError("Enter description");
            return;
        }

        if (location.isEmpty()) {
            locationInput.setError("Enter location");
            return;
        }

        if (phone.length() < 10) {
            phoneInput.setError("Enter valid phone");
            return;
        }

        String token = getSharedPreferences("APP", MODE_PRIVATE)
                .getString("TOKEN", "");

        if (token.isEmpty()) {
            Toast.makeText(this, "Session expired. Please login again.", Toast.LENGTH_LONG).show();
            return;
        }

        reportBtn.setEnabled(false);
        reportBtn.setText("Uploading...");

        // ✅ Convert to RequestBody
        RequestBody titleBody = RequestBody.create(MediaType.parse("text/plain"), title);
        RequestBody descBody = RequestBody.create(MediaType.parse("text/plain"), description);
        RequestBody locBody = RequestBody.create(MediaType.parse("text/plain"), location);
        RequestBody phoneBody = RequestBody.create(MediaType.parse("text/plain"), phone);
        RequestBody typeBody = RequestBody.create(MediaType.parse("text/plain"), type);

        MultipartBody.Part imagePart = null;

        // ✅ SAFE IMAGE CONVERSION
        if (imageUri != null) {
            try {
                InputStream is = getContentResolver().openInputStream(imageUri);

                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                int nRead;
                byte[] data = new byte[4096];

                while ((nRead = is.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }

                byte[] bytes = buffer.toByteArray();

                RequestBody reqFile =
                        RequestBody.create(MediaType.parse("image/*"), bytes);

                imagePart = MultipartBody.Part.createFormData(
                        "image",
                        "upload.jpg",
                        reqFile
                );

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Image error", Toast.LENGTH_SHORT).show();
            }
        }

        ApiService api = RetrofitClient.getInstance().create(ApiService.class);

        api.createItem(
                "Bearer " + token,
                titleBody,
                descBody,
                locBody,
                phoneBody,
                typeBody,
                imagePart
        ).enqueue(new Callback<ItemResponse>() {

            @Override
            public void onResponse(Call<ItemResponse> call, Response<ItemResponse> response) {

                reportBtn.setEnabled(true);
                reportBtn.setText("Report");

                if (response.isSuccessful()) {

                    Toast.makeText(ReportItemActivity.this,
                            "✅ Item reported successfully",
                            Toast.LENGTH_LONG).show();

                    finish();

                } else {

                    Toast.makeText(ReportItemActivity.this,
                            "Failed: " + response.code(),
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ItemResponse> call, Throwable t) {

                reportBtn.setEnabled(true);
                reportBtn.setText("Report");

                Toast.makeText(ReportItemActivity.this,
                        "Error: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}
