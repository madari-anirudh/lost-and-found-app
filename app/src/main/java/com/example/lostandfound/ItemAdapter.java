package com.example.lostandfound;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lostandfound.models.ItemModel;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private List<ItemModel> items;

    public ItemAdapter(List<ItemModel> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_row, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ItemModel item = items.get(position);



        //  BASIC DETAILS
        holder.title.setText(item.getTitle());
        holder.status.setText("Status: " + safe(item.getStatus()));
        holder.type.setText("Type: " + safe(item.getType()));
        holder.description.setText("Desc: " + safe(item.getDescription()));
        holder.location.setText("Location: " + safe(item.getLocation()));
        holder.phone.setText("Phone: " + safe(item.getPhone()));


        // ================= IMAGE BUTTON =================
        if (item.getImage() != null && !item.getImage().isEmpty()) {

            holder.viewImageBtn.setVisibility(View.VISIBLE);

            holder.viewImageBtn.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), ImagePreviewActivity.class);
                intent.putExtra("image", item.getImage());
                v.getContext().startActivity(intent);
            });

        } else {
            holder.viewImageBtn.setVisibility(View.GONE);
        }

        // ================= VIEW FINDER BUTTON =================
        if ("matched".equals(item.getStatus())) {

            holder.viewFinderBtn.setVisibility(View.VISIBLE);

            //  CHANGE BUTTON TEXT BASED ON TYPE
            if ("lost".equals(item.getType())) {
                holder.viewFinderBtn.setText("View Finder");
            } else {
                holder.viewFinderBtn.setText("View Lost User");
            }

            holder.viewFinderBtn.setOnClickListener(v -> {

                Intent intent = new Intent(
                        v.getContext(),
                        MatchDetailsActivity.class
                );

                intent.putExtra("id", item.get_id());
                intent.putExtra("title", item.getTitle());
                intent.putExtra("type", item.getType());

                // ✅ SAFE NULL CHECK
                if (item.getMatchDetails() != null) {

                    ItemModel.MatchDetails details =
                            item.getMatchDetails();

                    intent.putExtra(
                            "name",
                            details.getTitle() != null
                                    ? details.getTitle()
                                    : "Not available"
                    );

                    intent.putExtra(
                            "phone",
                            details.getPhone() != null
                                    ? details.getPhone()
                                    : "Not available"
                    );

                    intent.putExtra(
                            "location",
                            details.getLocation() != null
                                    ? details.getLocation()
                                    : "Not available"
                    );

                    intent.putExtra(
                            "description",
                            details.getDescription() != null
                                    ? details.getDescription()
                                    : "Not available"
                    );

                } else {

                    intent.putExtra("name", "Not available");
                    intent.putExtra("phone", "Not available");
                    intent.putExtra("location", "Not available");
                    intent.putExtra("description", "Not available");
                }

                v.getContext().startActivity(intent);

            });
        } else {
            holder.viewFinderBtn.setVisibility(View.GONE);
        }


        //  DISABLE ROW CLICK
        holder.itemView.setOnClickListener(null);

        // ========== Date & Time ==============
        String rawDate = item.getCreatedAt();

        if (rawDate != null) {
            try {
                java.text.SimpleDateFormat input = new java.text.SimpleDateFormat(
                        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.getDefault()
                );

                java.text.SimpleDateFormat output = new java.text.SimpleDateFormat(
                        "dd MMM yyyy, hh:mm a", java.util.Locale.getDefault()
                );

                java.util.Date dateObj = input.parse(rawDate);

                holder.date.setText("Reported: " + output.format(dateObj));

            } catch (Exception e) {
                holder.date.setText("Reported: -");
            }
        } else {
            holder.date.setText("Reported: -");
        }
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    //  SAFE NULL HANDLER
    private String safe(String value) {
        return value == null || value.isEmpty() ? "-" : value;
    }

    // ================= VIEW HOLDER =================
    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView title, status, type, description, location, phone, date;
        Button viewImageBtn, viewFinderBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            status = itemView.findViewById(R.id.status);
            type = itemView.findViewById(R.id.type);
            description = itemView.findViewById(R.id.description);
            location = itemView.findViewById(R.id.location);
            phone = itemView.findViewById(R.id.phone);
            date = itemView.findViewById(R.id.date);
            viewImageBtn = itemView.findViewById(R.id.viewImageBtn);
            viewFinderBtn = itemView.findViewById(R.id.viewFinderBtn);
        }
    }
}