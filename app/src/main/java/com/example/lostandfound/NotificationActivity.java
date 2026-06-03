package com.example.lostandfound;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lostandfound.api.ApiService;
import com.example.lostandfound.api.RetrofitClient;
import com.example.lostandfound.models.NotificationModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationActivity extends AppCompatActivity {

    ImageView backBtn;
    ListView listView;
    TextView emptyText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        backBtn = findViewById(R.id.backBtn);
        listView = findViewById(R.id.listView);
        emptyText = findViewById(R.id.emptyText);

        backBtn.setOnClickListener(v -> finish());

        loadNotifications();
    }

    private void loadNotifications() {

        String token = getSharedPreferences("APP", MODE_PRIVATE)
                .getString("TOKEN", "");

        if (token.isEmpty()) {
            Toast.makeText(this, "Login required", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService api = RetrofitClient
                .getInstance()
                .create(ApiService.class);

        api.getNotifications("Bearer " + token)
                .enqueue(new Callback<List<NotificationModel>>() {

                    @Override
                    public void onResponse(Call<List<NotificationModel>> call,
                                           Response<List<NotificationModel>> response) {

                        if (response.isSuccessful() && response.body() != null) {

                            List<NotificationModel> list = response.body();

                            if (list.isEmpty()) {
                                emptyText.setVisibility(View.VISIBLE);
                                listView.setVisibility(View.GONE);
                                return;
                            }

                            emptyText.setVisibility(View.GONE);
                            listView.setVisibility(View.VISIBLE);

                            List<String> messages = new ArrayList<>();

                            for (NotificationModel n : list) {
                                String msg = n.getMessage() + "\n🕒 " + n.getCreatedAt();
                                messages.add(msg);
                            }

                            android.widget.ArrayAdapter<String> adapter =
                                    new android.widget.ArrayAdapter<>(
                                            NotificationActivity.this,
                                            android.R.layout.simple_list_item_1,
                                            messages
                                    );

                            listView.setAdapter(adapter);

                        } else {
                            Toast.makeText(
                                    NotificationActivity.this,
                                    "Failed: " + response.code(),
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<NotificationModel>> call, Throwable t) {
                        Toast.makeText(
                                NotificationActivity.this,
                                "Error: " + t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }
}