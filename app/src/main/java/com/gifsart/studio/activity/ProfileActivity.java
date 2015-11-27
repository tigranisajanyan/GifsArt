package com.gifsart.studio.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.gifsart.studio.R;
import com.gifsart.studio.social.UploadImageToPicsart;
import com.gifsart.studio.utils.GifsArtConst;
import com.gifsart.studio.utils.Utils;

public class ProfileActivity extends AppCompatActivity {

    private static final int REQUEST_SIGNIN_ACTIVITY = 100;
    private static final int REQUEST_SIGNUP_ACTIVITY = 200;
    private static final int REQUEST_PICK_IMAGE_FROM_GALLERY = 666;

    private ViewGroup userProfileContainer;
    private ImageView profileImageView;
    private Button getStartedButton;
    private Button signInButton;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private String userApiKey = "";
    private String userId;

    private boolean isSignedIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sharedPreferences = getApplicationContext().getSharedPreferences(GifsArtConst.SHARED_PREFERENCES, MODE_PRIVATE);
        userApiKey = sharedPreferences.getString("user_api_key", "");
        userId = sharedPreferences.getString("user_id", "");

        if (userId != "" || userApiKey != "") {
            isSignedIn = true;
        } else {
            isSignedIn = false;
        }

        userProfileContainer = (ViewGroup) findViewById(R.id.profile_activity_user_container);
        profileImageView = (ImageView) findViewById(R.id.profile_image_view);
        getStartedButton = (Button) findViewById(R.id.get_started);
        signInButton = (Button) findViewById(R.id.profile_activity_toolbar_signin);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSignedIn) {
                    SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences(GifsArtConst.SHARED_PREFERENCES, MODE_PRIVATE).edit();
                    editor.putString("user_api_key", "");
                    editor.commit();
                    isSignedIn = false;
                    userProfileContainer.setVisibility(View.INVISIBLE);
                    Toast.makeText(ProfileActivity.this, "Log out", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    Intent intent = new Intent(ProfileActivity.this, SignInActivity.class);
                    startActivityForResult(intent, REQUEST_SIGNIN_ACTIVITY);
                }
            }
        });

        getStartedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, SignUpActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP_ACTIVITY);
            }
        });

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImageFromGallery();
            }
        });

        findViewById(R.id.picsart_deeplink).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deepLinkToPicsart();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SIGNIN_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                Glide.with(ProfileActivity.this).load(data.getStringExtra("photo_url")).into(profileImageView);
                ((TextView) userProfileContainer.findViewById(R.id.username_text_view)).setText(data.getStringExtra("name"));
                isSignedIn = true;
                userApiKey = data.getStringExtra("api_key");
                userProfileContainer.setVisibility(View.VISIBLE);
            } else {

            }
        }
        if (requestCode == REQUEST_SIGNUP_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                Glide.with(ProfileActivity.this).load(data.getStringExtra("photo_url")).into((ImageView) userProfileContainer.findViewById(R.id.profile_image_view));
                ((TextView) userProfileContainer.findViewById(R.id.username_text_view)).setText(data.getStringExtra("name"));
                isSignedIn = true;
                userProfileContainer.setVisibility(View.VISIBLE);
            } else {

            }
        }
        if (requestCode == REQUEST_PICK_IMAGE_FROM_GALLERY) {
            if (resultCode == RESULT_OK) {

                Uri imageUri = data.getData();
                UploadImageToPicsart uploadImageToPicsart = new UploadImageToPicsart(userApiKey, Utils.getRealPathFromURI(ProfileActivity.this, imageUri), UploadImageToPicsart.PHOTO_IS.AVATAR);
                uploadImageToPicsart.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                Glide.with(ProfileActivity.this).load(Utils.getRealPathFromURI(ProfileActivity.this, imageUri)).into(profileImageView);
            } else {

            }
        }
    }

    public void pickImageFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_PICK_IMAGE_FROM_GALLERY);
    }

    public void deepLinkToPicsart() {
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.picsart.studio");
        startActivity(launchIntent);
    }
}
