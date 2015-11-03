package com.gifsart.studio.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.gifsart.studio.R;

import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class GifPreviewActivity extends AppCompatActivity {

    public static final String EXTRA_GIF_PATH = "gif_file_path_string";
    private GifDrawable gifFromPath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gif_preview);

        Intent intent = getIntent();
        String gifPath = intent.getStringExtra(EXTRA_GIF_PATH);

        try {
            gifFromPath = new GifDrawable(gifPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        final GifImageView gifImageView = (GifImageView) findViewById(R.id.gif_img);
        if (gifFromPath != null)
            gifImageView.setImageDrawable(gifFromPath);

    }
}
