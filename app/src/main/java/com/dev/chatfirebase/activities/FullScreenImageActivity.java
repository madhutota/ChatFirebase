package com.dev.chatfirebase.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.dev.chatfirebase.R;
import com.dev.chatfirebase.activities.utils.UiIMageLoader;

public class FullScreenImageActivity extends AppCompatActivity {
    private TouchImageView imageView;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        imageView = findViewById(R.id.imageView);

        Intent intent = getIntent();
        if (intent != null)
            url = intent.getStringExtra("urlPhotoUser");

        UiIMageLoader.URLpicLoading(imageView, url, null, R.drawable.default_avatar);

    }
}
