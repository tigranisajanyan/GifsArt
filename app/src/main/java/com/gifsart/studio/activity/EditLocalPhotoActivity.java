package com.gifsart.studio.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
import com.gifsart.studio.adapter.ProfileUserPhotosAdapter;
import com.gifsart.studio.gifutils.GiphyToByteArray;
import com.gifsart.studio.item.GiphyItem;
import com.gifsart.studio.social.RequestConstants;
import com.gifsart.studio.social.User;
import com.gifsart.studio.social.UserContraller;
import com.gifsart.studio.utils.GifsArtConst;

public class EditLocalPhotoActivity extends AppCompatActivity {


    private SimpleDraweeView imageView;
    private User user;
    private ViewGroup profileContainer;
    private TextView makeItPublic;
    private String photoId;
    private boolean isPublic;
    private String gifUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_local_photo);


        final Intent intent = getIntent();
        user = UserContraller.readUserFromFile(this);
        photoId = intent.getStringExtra("photo_id");
        isPublic = intent.getBooleanExtra("is_public", false);
        gifUrl = intent.getStringExtra("image_url");

        ProfileUserPhotosAdapter.deletedItems.removeAll(ProfileUserPhotosAdapter.deletedItems);

        imageView = (SimpleDraweeView) findViewById(R.id.edit_local_photo_image_view);
        profileContainer = (ViewGroup) findViewById(R.id.edit_local_photo_activity_user_container);
        makeItPublic = (TextView) findViewById(R.id.make_photo_public);

        if (isPublic) {
            makeItPublic.setVisibility(View.INVISIBLE);
        } else {
            makeItPublic.setVisibility(View.VISIBLE);
        }

        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
        layoutParams.width = getResources().getDisplayMetrics().widthPixels;
        layoutParams.height = getResources().getDisplayMetrics().widthPixels;

        imageView.setLayoutParams(layoutParams);

        //Glide.with(this).load(gifUrl + "?r240x240f5").asGif().into(imageView);
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                //.setLowResImageRequest(ImageRequest.fromUri(gifUrl + "?r240x240f5"))
                .setUri(Uri.parse(gifUrl + "?r240x240f5"))
                        //.setImageRequest(ImageRequest.fromUri(gifUrl + "?r240x240f5"))
                .setAutoPlayAnimations(true).build();
        imageView.setController(controller);

        Glide.with(this).load(user.getPhoto() + "?r240x240").asBitmap().centerCrop().into(new BitmapImageViewTarget(((ImageView) profileContainer.findViewById(R.id.local_profile_image_view))) {
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
                        if (requestNumber == RequestConstants.UPDATE_PHOTO_INFO_SUCCESS_CODE) {
                            isPublic = true;
                            makeItPublic.setVisibility(View.INVISIBLE);
                            setResult(RESULT_OK);
                        } else {
                            AlertDialog alert = UserContraller.setupDialogBuilder(EditLocalPhotoActivity.this, messege).create();
                            alert.show();
                        }
                    }
                });
                userContraller.updatePhotoInfo(photoId);
            }
        });

        findViewById(R.id.delete_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserContraller userContraller = new UserContraller(EditLocalPhotoActivity.this);
                userContraller.setOnRequestReadyListener(new UserContraller.UserRequest() {
                    @Override
                    public void onRequestReady(int requestNumber, String messege) {
                        if (requestNumber == RequestConstants.REMOVE_PHOTO_SUCCESS_CODE) {
                            ProfileUserPhotosAdapter.deletedItems.add(photoId);
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            AlertDialog alert = UserContraller.setupDialogBuilder(EditLocalPhotoActivity.this, messege).create();
                            alert.show();
                        }
                    }
                });
                userContraller.removeUserPhoto(photoId, user.getKey());
            }
        });


        findViewById(R.id.edit_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GiphyItem giphyItem = new GiphyItem();
                giphyItem.setOriginalGifUrl(gifUrl);
                GiphyToByteArray giphyToByteArray = new GiphyToByteArray(EditLocalPhotoActivity.this, giphyItem);
                giphyToByteArray.setOnDownloadedListener(new GiphyToByteArray.OnDownloaded() {
                    @Override
                    public void onDownloaded(boolean isDownladed) {
                        if (isDownladed) {
                            Intent intent1 = new Intent(EditLocalPhotoActivity.this, MakeGifActivity.class);
                            intent1.putExtra(GifsArtConst.INTENT_ACTIVITY_INDEX, GifsArtConst.INDEX_GIPHY_TO_GIF);
                            startActivity(intent1);
                            finish();
                        }
                    }
                });
                giphyToByteArray.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


            }
        });

        findViewById(R.id.share_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GiphyItem giphyItem = new GiphyItem();
                giphyItem.setOriginalGifUrl(gifUrl);
                GiphyToByteArray giphyToByteArray = new GiphyToByteArray(EditLocalPhotoActivity.this, giphyItem);
                giphyToByteArray.setOnDownloadedListener(new GiphyToByteArray.OnDownloaded() {
                    @Override
                    public void onDownloaded(boolean isDownladed) {
                        if (isDownladed) {
                            Intent intent = new Intent(EditLocalPhotoActivity.this, ShareGifActivity.class);
                            intent.putExtra("saved_file_path", Environment.getExternalStorageDirectory() + "/ttt.gif");
                            intent.putExtra("saved_file_url", gifUrl);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
                giphyToByteArray.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            }
        });
    }


}
