package com.example.lostandfound.fragments;
import android.util.Log;
import android.os.Bundle;
import android.view.*;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lostandfound.ItemAdapter;
import com.example.lostandfound.R;
import com.example.lostandfound.api.ApiService;
import com.example.lostandfound.api.RetrofitClient;
import com.example.lostandfound.models.ItemModel;

import java.util.List;

import retrofit2.*;

public class MyItemsFragment extends Fragment {

    RecyclerView recyclerView;

    public MyItemsFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.fragment_my_items, container, false);

        recyclerView = view.findViewById(R.id.myItemsRecycler);

        loadItems();

        return view;
    }

    private void loadItems(){

        String token = requireActivity()
                .getSharedPreferences("APP",0)
                .getString("TOKEN","");

        ApiService api = RetrofitClient.getInstance().create(ApiService.class);

        api.getMyItems("Bearer " + token)
                .enqueue(new Callback<List<ItemModel>>() {

                    @Override
public void onResponse(Call<List<ItemModel>> call, Response<List<ItemModel>> response) {

    if (response.isSuccessful()) {

        List<ItemModel> items = response.body();

        // 🔥 DEBUG LOG
        Log.d("API_RESPONSE", "Items: " + items);

        // 🔥 SHOW COUNT
        Toast.makeText(getContext(),
                "Items Count: " + (items != null ? items.size() : 0),
                Toast.LENGTH_LONG).show();

        if (items != null && !items.isEmpty()) {

            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(new ItemAdapter(items));

        } else {
            Toast.makeText(getContext(),
                    "No items found",
                    Toast.LENGTH_LONG).show();
        }

    } else {
        Toast.makeText(getContext(),
                "Response not successful",
                Toast.LENGTH_LONG).show();
    }
}

                    @Override
                    public void onFailure(Call<List<ItemModel>> call, Throwable t) {

                        Toast.makeText(getContext(),
                                t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}