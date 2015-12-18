package com.gifsart.studio.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.gifsart.studio.R;
import com.gifsart.studio.social.ErrorHandler;
import com.gifsart.studio.social.RequestConstants;
import com.gifsart.studio.social.UploadImageToPicsart;
import com.gifsart.studio.social.User;
import com.gifsart.studio.social.UserContraller;
import com.gifsart.studio.utils.Utils;

public class PersonalizeUserActivity extends AppCompatActivity {

    private static final String LOG_TAG = "user_info_activity";
    private static final int REQUEST_PICK_IMAGE_FROM_GALLERY = 666;

    private ImageView profileImageView;
    private EditText profileNameEditText;
    private EditText profileUsernameEditText;
    private Button doneButton;

    private boolean isSignUpedByFacebook = false;
    private Uri imageUri;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personalize_user);

        profileImageView = (ImageView) findViewById(R.id.change_user_profile_image);
        profileNameEditText = (EditText) findViewById(R.id.profile_name_edit_text);
        profileUsernameEditText = (EditText) findViewById(R.id.profile_username_edit_text);
        doneButton = (Button) findViewById(R.id.done_button);

        Intent intent = getIntent();
        isSignUpedByFacebook = intent.getBooleanExtra("sign_up_with_facebook", false);

        user = UserContraller.readUserFromFile(this);

        if (isSignUpedByFacebook) {
            profileUsernameEditText.setVisibility(View.VISIBLE);
        } else {
            profileUsernameEditText.setVisibility(View.INVISIBLE);
        }

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImageFromGallery();
            }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final UploadImageToPicsart uploadImageToPicsart = new UploadImageToPicsart(PersonalizeUserActivity.this, user.getKey(), Utils.getRealPathFromURI(PersonalizeUserActivity.this, imageUri), "GifsArt", UploadImageToPicsart.PHOTO_PUBLIC.PUBLIC, UploadImageToPicsart.PHOTO_IS.AVATAR);
                uploadImageToPicsart.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                uploadImageToPicsart.setOnUploadedListener(new UploadImageToPicsart.ImageUploaded() {
                    @Override
                    public void uploadIsDone(boolean uploaded, String messege) {
                        if (uploaded) {
                            UserContraller userContraller = new UserContraller(PersonalizeUserActivity.this);
                            userContraller.setOnRequestReadyListener(new UserContraller.UserRequest() {
                                @Override
                                public void onRequestReady(int requestNumber, String messege) {
                                    if (requestNumber == RequestConstants.UPLOAD_USER_INFO_SUCCESS_CODE) {
                                        user.setName(profileNameEditText.getText().toString());
                                        if (isSignUpedByFacebook) {
                                            user.setUsername(profileUsernameEditText.getText().toString());
                                        }
                                        user.setPhoto(uploadImageToPicsart.getUploadedImageUrl());
                                        UserContraller.writeUserToFile(PersonalizeUserActivity.this, user);
                                        setResult(RESULT_OK);
                                    } else {
                                        setResult(RESULT_CANCELED, ErrorHandler.createErrorMessege(messege));
                                    }
                                    finish();
                                }
                            });
                            if (isSignUpedByFacebook) {
                                userContraller.uploadUserInfo(user.getKey(), profileNameEditText.getText().toString(), profileUsernameEditText.getText().toString());
                            } else {
                                userContraller.uploadUserInfo(user.getKey(), profileNameEditText.getText().toString(), "");
                            }
                        } else {
                            AlertDialog alert = UserContraller.setupDialogBuilder(PersonalizeUserActivity.this, messege).create();
                            alert.show();
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PICK_IMAGE_FROM_GALLERY) {
            if (resultCode == RESULT_OK) {
                imageUri = data.getData();
                Glide.with(PersonalizeUserActivity.this).load(Utils.getRealPathFromURI(PersonalizeUserActivity.this, imageUri)).into(profileImageView);
            } else {
                setResult(RESULT_CANCELED);
            }
        }
    }

    public void pickImageFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_PICK_IMAGE_FROM_GALLERY);
    }

}
