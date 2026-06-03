package com.example.lostandfound;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.example.lostandfound.api.ApiService;
import com.example.lostandfound.api.RetrofitClient;
import com.example.lostandfound.models.ItemModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyItemsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ShimmerFrameLayout shimmerLayout;
    TextView emptyText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_items);

        recyclerView = findViewById(R.id.myItemsRecycler);
        shimmerLayout = findViewById(R.id.shimmerLayout);
        emptyText = findViewById(R.id.emptyText);

        //  CRASH SAFETY CHECK
        if (recyclerView == null || shimmerLayout == null || emptyText == null) {
            Toast.makeText(this, "Layout error: check XML IDs", Toast.LENGTH_LONG).show();
            return;
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadItems();
    }

    private void loadItems() {

        //  TOKEN CHECK (PREVENT AUTO LOGOUT CRASH)
        String token = getSharedPreferences("APP", MODE_PRIVATE)
                .getString("TOKEN", "");

        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Session expired. Please login again.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // SHOW SHIMMER
        shimmerLayout.setVisibility(View.VISIBLE);
        shimmerLayout.startShimmer();
        recyclerView.setVisibility(View.GONE);
        emptyText.setVisibility(View.GONE);

        ApiService api = RetrofitClient
                .getInstance()
                .create(ApiService.class);

        api.getMyItems("Bearer " + token)
                .enqueue(new Callback<List<ItemModel>>() {

                    @Override
                    public void onResponse(Call<List<ItemModel>> call,
                                           Response<List<ItemModel>> response) {

                        //  STOP SHIMMER
                        shimmerLayout.stopShimmer();
                        shimmerLayout.setVisibility(View.GONE);

                        if (response.isSuccessful()) {

                            List<ItemModel> items = response.body();

                            //  NULL SAFETY
                            if (items == null || items.isEmpty()) {
                                emptyText.setVisibility(View.VISIBLE);
                                emptyText.setText("No items found");
                                recyclerView.setVisibility(View.GONE);
                                return;
                            }

                            recyclerView.setAdapter(new ItemAdapter(items));
                            recyclerView.setVisibility(View.VISIBLE);
                            emptyText.setVisibility(View.GONE);

                        } else {
                            emptyText.setVisibility(View.VISIBLE);
                            emptyText.setText("Failed to load items (" + response.code() + ")");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<ItemModel>> call, Throwable t) {

                        //  STOP SHIMMER
                        shimmerLayout.stopShimmer();
                        shimmerLayout.setVisibility(View.GONE);

                        emptyText.setVisibility(View.VISIBLE);
                        emptyText.setText("Error loading data");

                        Toast.makeText(
                                MyItemsActivity.this,
                                "Error: " + t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }
}