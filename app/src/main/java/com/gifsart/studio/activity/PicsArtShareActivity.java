package com.gifsart.studio.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.gifsart.studio.R;
import com.gifsart.studio.social.UploadImageToPicsart;
import com.gifsart.studio.social.User;
import com.gifsart.studio.social.UserContraller;
import com.gifsart.studio.utils.Utils;

import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class PicsArtShareActivity extends AppCompatActivity {

    private GifImageView imageView;
    private EditText editText;
    private Switch switchButton;

    private String filePath;

    private UploadImageToPicsart.PHOTO_PUBLIC photo_public = UploadImageToPicsart.PHOTO_PUBLIC.PUBLIC;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pics_art_share);

        imageView = (GifImageView) findViewById(R.id.pics_art_share_image_view);
        editText = (EditText) findViewById(R.id.say_something_edittext);
        switchButton = (Switch) findViewById(R.id.toggle_button);

        filePath = getIntent().getStringExtra("saved_file_path");

        GifDrawable gifDrawable = null;
        try {
            gifDrawable = new GifDrawable(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        imageView.setImageDrawable(gifDrawable);

        user = UserContraller.readUserFromFile(this);

        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    photo_public = UploadImageToPicsart.PHOTO_PUBLIC.PUBLIC;
                } else {
                    photo_public = UploadImageToPicsart.PHOTO_PUBLIC.PRIVATE;

                }
            }
        });

        findViewById(R.id.picsart_share_activity_toolbar_done_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.haveNetworkConnection(PicsArtShareActivity.this)) {
                    final UploadImageToPicsart uploadImageToPicsart = new UploadImageToPicsart(PicsArtShareActivity.this, user.getKey(), filePath, editText.getText().toString(), photo_public, UploadImageToPicsart.PHOTO_IS.GENERAL);
                    uploadImageToPicsart.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    uploadImageToPicsart.setOnUploadedListener(new UploadImageToPicsart.ImageUploaded() {
                        @Override
                        public void uploadIsDone(boolean uploaded, String messege) {
                            Intent intent = new Intent(PicsArtShareActivity.this, ShareGifActivity.class);
                            intent.putExtra("saved_file_path", Environment.getExternalStorageDirectory() + "/test.gif");
                            intent.putExtra("saved_file_url", uploadImageToPicsart.getUploadedImageUrl());
                            startActivity(intent);
                            finish();
                        }
                    });
                } else {
                    Toast.makeText(PicsArtShareActivity.this, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}
