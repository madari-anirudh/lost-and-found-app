package com.example.lostandfound;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class ImagePreviewActivity extends AppCompatActivity {

    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);

        imageView = findViewById(R.id.fullImage);


        String imageUrl = getIntent().getStringExtra("image");

        Glide.with(this)
                .load(imageUrl)
                .into(imageView);
    }
}