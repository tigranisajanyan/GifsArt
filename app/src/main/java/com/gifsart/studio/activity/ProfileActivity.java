package com.gifsart.studio.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.gifsart.studio.R;
import com.gifsart.studio.adapter.ProfileUserPhotosAdapter;
import com.gifsart.studio.helper.RecyclerItemClickListener;
import com.gifsart.studio.social.Photo;
import com.gifsart.studio.social.RequestConstants;
import com.gifsart.studio.social.UploadImageToPicsart;
import com.gifsart.studio.social.User;
import com.gifsart.studio.social.UserContraller;
import com.gifsart.studio.utils.GifsArtConst;
import com.gifsart.studio.utils.SpacesItemDecoration;
import com.gifsart.studio.utils.Utils;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    public static final String LOG_TAG = "profile_activity";

    private static final int REQUEST_SIGNIN_ACTIVITY = 111;
    private static final int REQUEST_SIGNUP_ACTIVITY = 222;
    private static final int REQUEST_PICK_IMAGE_FROM_GALLERY = 666;
    private static final int REQUEST_OPEN_PHOTO_EDIT = 667;


    private ProfileActivity context = this;

    private ViewGroup userProfileContainer;
    private ViewGroup getStartedContainer;
    private ImageView profileImageView;
    private Button getStartedButton;
    private Button signInButton;
    private RecyclerView userPhotosRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;

    private GridLayoutManager gridLayoutManager;

    private ProfileUserPhotosAdapter profileUserPhotosAdapter;

    private boolean isSignedIn = false;

    private User user;
    private ArrayList<Photo> userPhotos = new ArrayList<>();

    private int photosOffset = 0;
    private int photosLimit = 30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userProfileContainer = (ViewGroup) findViewById(R.id.profile_activity_user_container);
        getStartedContainer = (ViewGroup) findViewById(R.id.get_started_container);
        profileImageView = (ImageView) findViewById(R.id.profile_image_view);
        getStartedButton = (Button) findViewById(R.id.get_started);
        signInButton = (Button) findViewById(R.id.profile_activity_toolbar_signin);
        userPhotosRecyclerView = (RecyclerView) findViewById(R.id.user_photos_rec_view);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);

        user = UserContraller.readUserFromFile(this);

        gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        profileUserPhotosAdapter = new ProfileUserPhotosAdapter(this);

        userPhotosRecyclerView.setHasFixedSize(true);
        userPhotosRecyclerView.setClipToPadding(true);
        userPhotosRecyclerView.setLayoutManager(gridLayoutManager);
        userPhotosRecyclerView.setItemAnimator(new DefaultItemAnimator());

        userPhotosRecyclerView.setAdapter(profileUserPhotosAdapter);
        userPhotosRecyclerView.addItemDecoration(new SpacesItemDecoration((int) Utils.dpToPixel(2, this)));

        userPhotosRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(context, EditLocalPhotoActivity.class);
                intent.putExtra("image_url", profileUserPhotosAdapter.getItem(position).getUrl());
                intent.putExtra("is_public", profileUserPhotosAdapter.getItem(position).getIsPublic());
                intent.putExtra("photo_id", profileUserPhotosAdapter.getItem(position).getId());
                startActivityForResult(intent, REQUEST_OPEN_PHOTO_EDIT);
            }
        }));

        if (user != null) {
            isSignedIn = true;
            updateUserInfo();
            visibilitySwitcher(isSignedIn);
            if (Utils.haveNetworkConnection(this)) {
                final UserContraller userContraller = new UserContraller(this);
                userContraller.setOnRequestReadyListener(new UserContraller.UserRequest() {
                    @Override
                    public void onRequestReady(int requestNumber, String messege) {
                        if (requestNumber == RequestConstants.REQUEST_USER_SUCCESS_CODE) {
                            user = userContraller.getUser();
                            updateUserInfo();
                            userContraller.requestUserPhotos(user.getKey(), photosOffset, photosLimit);
                        } else if (requestNumber == RequestConstants.REQUEST_USER_PHOTO_SUCCESS_CODE) {
                            userPhotos = userContraller.getUserPhotos();
                            profileUserPhotosAdapter.addItems(userPhotos);
                        } else {
                            AlertDialog alert = UserContraller.setupDialogBuilder(context, messege).create();
                            alert.show();
                        }
                    }
                });
                userContraller.requestUser(user.getKey());
            }
        } else {
            isSignedIn = false;
            visibilitySwitcher(isSignedIn);
        }

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSignedIn) {
                    isSignedIn = false;
                    UserContraller.writeUserToFile(context, null);
                    profileUserPhotosAdapter.removeAllItems();
                    visibilitySwitcher(isSignedIn);
                    Toast.makeText(context, "Sign out", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(context, SignInActivity.class);
                    startActivityForResult(intent, REQUEST_SIGNIN_ACTIVITY);
                }
            }
        });

        getStartedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SignUpActivity.class);
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

        findViewById(R.id.profile_activity_toolbar_create).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.pink));
        swipeRefreshLayout.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                return false;
            }
        });
        ((SwipeRefreshLayout) findViewById(R.id.swipe_container)).setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                profileUserPhotosAdapter.notifyDataSetChanged();
                ((SwipeRefreshLayout) findViewById(R.id.swipe_container)).setRefreshing(false);
            }
        });

        /*LoginManager.getInstance().registerCallback(CallbackManager.Factory.create(), new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                return;
            }

            @Override
            public void onCancel() {
                AlertDialog alert = UserContraller.setupDialogBuilder(context, "Facebook login canceled").create();
                alert.show();
            }

            @Override
            public void onError(FacebookException error) {
                AlertDialog alert = UserContraller.setupDialogBuilder(context, error.getMessage()).create();
                alert.show();
            }
        });*/

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SIGNIN_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                isSignedIn = true;
                user = UserContraller.readUserFromFile(context);
                final UserContraller userContraller = new UserContraller(context);
                userContraller.setOnRequestReadyListener(new UserContraller.UserRequest() {
                    @Override
                    public void onRequestReady(int requestNumber, String messege) {
                        if (requestNumber == RequestConstants.REQUEST_USER_PHOTO_SUCCESS_CODE) {
                            userPhotos = userContraller.getUserPhotos();
                            profileUserPhotosAdapter.addItems(userPhotos);
                        } else {
                            AlertDialog alert = UserContraller.setupDialogBuilder(context, messege).create();
                            alert.show();
                        }
                    }
                });
                photosOffset = 0;
                userContraller.requestUserPhotos(user.getKey(), photosOffset, photosLimit);
                updateUserInfo();
                visibilitySwitcher(isSignedIn);
            } else {
                if (data != null) {
                    Bundle extras = data.getExtras();
                    if (extras.containsKey(GifsArtConst.INTENT_OPEN_SIGN_UP)) {
                        Intent intent = new Intent(context, SignUpActivity.class);
                        startActivityForResult(intent, REQUEST_SIGNUP_ACTIVITY);
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setMessage(data.getStringExtra("error_messege"))
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                }
            }
        }
        if (requestCode == REQUEST_SIGNUP_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                isSignedIn = true;
                user = UserContraller.readUserFromFile(context);
                updateUserInfo();
                visibilitySwitcher(isSignedIn);
            } else {
                if (data != null) {
                    Bundle extras = data.getExtras();
                    if (extras.containsKey(GifsArtConst.INTENT_OPEN_SIGN_IN)) {
                        Intent intent = new Intent(context, SignInActivity.class);
                        startActivityForResult(intent, REQUEST_SIGNIN_ACTIVITY);
                    } else {
                        AlertDialog alert = UserContraller.setupDialogBuilder(this, data.getStringExtra("error_messege")).create();
                        alert.show();
                    }
                }
            }
        }
        if (requestCode == REQUEST_PICK_IMAGE_FROM_GALLERY) {
            if (resultCode == RESULT_OK) {
                Uri imageUri = data.getData();
                final UploadImageToPicsart uploadImageToPicsart = new UploadImageToPicsart(context, user.getKey(), Utils.getRealPathFromURI(context, imageUri), "GifsArt", UploadImageToPicsart.PHOTO_PUBLIC.PUBLIC, UploadImageToPicsart.PHOTO_IS.GENERAL);
                uploadImageToPicsart.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                uploadImageToPicsart.setOnUploadedListener(new UploadImageToPicsart.ImageUploaded() {
                    @Override
                    public void uploadIsDone(boolean uploaded, String messege) {
                        if (uploaded) {
                            user.setPhoto(uploadImageToPicsart.getUploadedImageUrl());
                            UserContraller.writeUserToFile(context, user);
                        } else {
                            AlertDialog alert = UserContraller.setupDialogBuilder(context, messege).create();
                            alert.show();
                            updateUserInfo();
                        }
                    }
                });
                Glide.with(context).load(Utils.getRealPathFromURI(context, imageUri)).asBitmap().centerCrop().into(new BitmapImageViewTarget(profileImageView) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        profileImageView.setImageDrawable(circularBitmapDrawable);
                    }
                });
            } else {
            }
        }
        if (requestCode == REQUEST_OPEN_PHOTO_EDIT) {
            if (resultCode == RESULT_OK) {
                final UserContraller userContraller = new UserContraller(this);
                userContraller.setOnRequestReadyListener(new UserContraller.UserRequest() {
                    @Override
                    public void onRequestReady(int requestNumber, String messege) {
                        if (requestNumber == RequestConstants.REQUEST_USER_PHOTO_SUCCESS_CODE) {
                            userPhotos = userContraller.getUserPhotos();
                            profileUserPhotosAdapter.addItems(userPhotos);
                        } else {
                            AlertDialog alert = UserContraller.setupDialogBuilder(context, messege).create();
                            alert.show();
                        }
                    }
                });
                userContraller.requestUserPhotos(user.getKey(), photosOffset, photosLimit);
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
        //launchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //launchIntent.putExtra("from", "user");
        //launchIntent.putExtra("URI", "picsart://users?id=" + user.getId());
        startActivity(launchIntent);
    }

    private void visibilitySwitcher(boolean isSignedIn) {
        if (isSignedIn) {
            userProfileContainer.setVisibility(View.VISIBLE);
            getStartedContainer.setVisibility(View.INVISIBLE);
            swipeRefreshLayout.setVisibility(View.VISIBLE);
            signInButton.setText("Sign Out");
        } else {
            userProfileContainer.setVisibility(View.INVISIBLE);
            getStartedContainer.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setVisibility(View.GONE);
            signInButton.setText("Sign In");
        }
    }

    public void updateUserInfo() {
        //Glide.with(ProfileActivity.this).load(user.getPhoto() + "?r240x240").asBitmap().into(profileImageView);
        Glide.with(context).load(user.getPhoto() + "?r240x240").asBitmap().centerCrop().into(new BitmapImageViewTarget(profileImageView) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                profileImageView.setImageDrawable(circularBitmapDrawable);
            }
        });
        ((TextView) userProfileContainer.findViewById(R.id.username_text_view)).setText("@" + user.getName());
    }

}
