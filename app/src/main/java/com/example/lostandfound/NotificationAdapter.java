package com.example.lostandfound;

import android.content.Context;
import android.view.*;
import android.widget.*;
import com.example.lostandfound.models.NotificationModel;

import java.util.List;

public class NotificationAdapter extends ArrayAdapter<NotificationModel> {

    public NotificationAdapter(Context context, List<NotificationModel> list) {
        super(context, 0, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(android.R.layout.simple_list_item_2, parent, false);
        }

        NotificationModel n = getItem(position);

        TextView t1 = convertView.findViewById(android.R.id.text1);
        TextView t2 = convertView.findViewById(android.R.id.text2);

        t1.setText(n.getMessage());
        t2.setText(n.getCreatedAt());

        return convertView;
    }
}