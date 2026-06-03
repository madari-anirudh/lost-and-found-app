package com.example.lostandfound;
import android.view.*;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;
import com.example.lostandfound.models.ItemModel;
import java.util.List;

public class HorizontalItemAdapter extends RecyclerView.Adapter<HorizontalItemAdapter.ViewHolder> {

    List<ItemModel> items;

    public HorizontalItemAdapter(List<ItemModel> items) {
        this.items = items;
    }

            //CreateViewHolder
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_card, parent, false);

        return new ViewHolder(view);
    }

            //BindViewHolder

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        ItemModel item = items.get(position);

        holder.title.setText(item.getTitle());
        holder.location.setText(item.getLocation());
        holder.date.setText(formatDate(item.getCreatedAt()));

               //  Status UI Color     

         String status = item.getStatus();

holder.status.setText(status.toUpperCase());

if (status.equalsIgnoreCase("searching")) {
    holder.status.setBackgroundColor(0xFFE53935); // 🔴 RED
} 
else if (status.equalsIgnoreCase("matched")) {
    holder.status.setBackgroundColor(0xFF43A047); // 🟢 GREEN
} 
else if (status.equalsIgnoreCase("completed")) {
    holder.status.setBackgroundColor(0xFF1E88E5); // 🔵 BLUE
}


                 //   open details
       holder.itemView.setOnClickListener(v -> {

    Toast.makeText(
            v.getContext(),
            item.getTitle(),
            Toast.LENGTH_SHORT
    ).show();

});
    }

        //getItemCount
    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView title, status, location, date;

        public ViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            status = itemView.findViewById(R.id.status);
            location = itemView.findViewById(R.id.location);
            date = itemView.findViewById(R.id.date);
        }
    }
    // Date formate
    private String formatDate(String dateStr) {
        try {
            java.text.SimpleDateFormat input =
                    new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

            java.text.SimpleDateFormat output =
                    new java.text.SimpleDateFormat("dd MMM yyyy");

            return output.format(input.parse(dateStr));

        } catch (Exception e) {
            return "Recent";
        }
    }
}