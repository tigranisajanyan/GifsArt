package com.gifsart.studio.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.gifsart.studio.R;
import com.gifsart.studio.social.User;
import com.gifsart.studio.social.UserContraller;

public class EditLocalPhotoActivity extends AppCompatActivity {


    private ImageView imageView;
    private User user;
    private ViewGroup profileContainer;
    private TextView makeItPublic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_local_photo);


        final Intent intent = getIntent();
        user = UserContraller.readUserFromFile(this);

        imageView = (ImageView) findViewById(R.id.edit_local_photo_image_view);
        profileContainer = (ViewGroup) findViewById(R.id.edit_local_photo_activity_user_container);
        makeItPublic = (TextView) findViewById(R.id.make_photo_public);

        if (intent.getBooleanExtra("is_public", false)) {
            makeItPublic.setVisibility(View.INVISIBLE);
        } else {
            makeItPublic.setVisibility(View.VISIBLE);
        }

        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
        layoutParams.width = getResources().getDisplayMetrics().widthPixels;
        layoutParams.height = getResources().getDisplayMetrics().widthPixels;

        imageView.setLayoutParams(layoutParams);

        Glide.with(this).load(intent.getStringExtra("image_url") + "?r240x240f5").asGif().into(imageView);

        Glide.with(this).load(user.getPhoto() + "?r240x240f5").asBitmap().centerCrop().into(new BitmapImageViewTarget(((ImageView) profileContainer.findViewById(R.id.local_profile_image_view))) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                ((ImageView) profileContainer.findViewById(R.id.local_profile_image_view)).setImageDrawable(circularBitmapDrawable);
            }
        });

        ((TextView) findViewById(R.id.local_username_text_view)).setText("@" + user.getName());

        makeItPublic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserContraller userContraller = new UserContraller(EditLocalPhotoActivity.this);
                userContraller.setOnRequestReadyListener(new UserContraller.UserRequest() {
                    @Override
                    public void onRequestReady(int requestNumber, String messege) {

                    }
                });
                userContraller.updatePhotoInfo(intent.getStringExtra("photo_id"));
            }
        });

        findViewById(R.id.delete_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserContraller userContraller = new UserContraller(EditLocalPhotoActivity.this);
                userContraller.setOnRequestReadyListener(new UserContraller.UserRequest() {
                    @Override
                    public void onRequestReady(int requestNumber, String messege) {

                    }
                });
                userContraller.removeUserPhoto(intent.getStringExtra("photo_id"), user.getKey());
            }
        });

    }
}
