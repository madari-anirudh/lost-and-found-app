package com.example.lostandfound.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.lostandfound.NotificationActivity;
import com.example.lostandfound.R;
import com.example.lostandfound.api.ApiService;
import com.example.lostandfound.api.RetrofitClient;
import com.example.lostandfound.models.ItemModel;
import com.example.lostandfound.HorizontalItemAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeFragment extends Fragment {

// variables

    RecyclerView horizontalRecycler;
    List<ItemModel> items;
    HorizontalItemAdapter adapter;
    EditText searchBar;
    TextView greetingText;
    ImageView notificationIcon;

    public HomeFragment(){}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){



        View view = inflater.inflate(R.layout.fragment_home, container, false);
        searchBar = view.findViewById(R.id.searchBar);
        horizontalRecycler = view.findViewById(R.id.horizontalRecycler);
        greetingText = view.findViewById(R.id.greetingText);
        
        greetingText.setText("Find it !");

        items = new ArrayList<>();

        adapter = new HorizontalItemAdapter(items);

        horizontalRecycler.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        );

        horizontalRecycler.setAdapter(adapter);
        searchBar.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                filterItems(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        notificationIcon = view.findViewById(R.id.notificationIcon);
        notificationIcon.setOnClickListener(v -> {

            Toast.makeText(getContext(),
                    "No new notifications 🔔",
                    Toast.LENGTH_SHORT).show();

        });
        notificationIcon.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), NotificationActivity.class));
        });


        loadItems();

        return view;
    }

                 //filter
    private void filterItems(String query) {

        List<ItemModel> filteredList = new ArrayList<>();

        for (ItemModel item : items) {

            if (item.getTitle() != null &&
                    item.getTitle().toLowerCase().contains(query.toLowerCase())) {

                filteredList.add(item);
            }
        }

        adapter = new HorizontalItemAdapter(filteredList);
        horizontalRecycler.setAdapter(adapter);
    }

                        // LoadItem
    private void loadItems() {

        String token = getActivity()
                .getSharedPreferences("APP", getContext().MODE_PRIVATE)
                .getString("TOKEN", "");

        ApiService api = RetrofitClient.getInstance().create(ApiService.class);

        api.getMyItems("Bearer " + token).enqueue(new Callback<List<ItemModel>>() {

            @Override
            public void onResponse(Call<List<ItemModel>> call, Response<List<ItemModel>> response) {

                if (response.isSuccessful() && response.body() != null) {

                    items.clear();
                    items.addAll(response.body());

                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<ItemModel>> call, Throwable t) {

                Toast.makeText(getContext(),
                        "Error: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}

