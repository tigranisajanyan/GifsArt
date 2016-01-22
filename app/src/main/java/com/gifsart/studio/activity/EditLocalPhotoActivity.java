package com.gifsart.studio.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.gifsart.studio.R;
import com.gifsart.studio.adapter.ProfileUserPhotosAdapter;
import com.gifsart.studio.gifutils.DownloadGifFromGiphyToFile;
import com.gifsart.studio.social.RequestConstants;
import com.gifsart.studio.social.User;
import com.gifsart.studio.social.UserContraller;
import com.gifsart.studio.utils.AnimatedProgressDialog;
import com.gifsart.studio.utils.GifsArtConst;
import com.gifsart.studio.utils.Utils;

public class EditLocalPhotoActivity extends AppCompatActivity {

    private EditLocalPhotoActivity context = this;

    private ImageView imageView;
    private TextView makeItPublic;

    private User user;

    private String photoId;
    private String gifUrl;

    private boolean isPublic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_local_photo);

        final Intent intent = getIntent();
        user = UserContraller.readUserFromFile(this);
        photoId = intent.getStringExtra(GifsArtConst.INTENT_PHOTO_ID);
        isPublic = intent.getBooleanExtra(GifsArtConst.INTENT_IS_PUBLIC, false);
        gifUrl = intent.getStringExtra(GifsArtConst.INTENT_IMAGE_URL);

        ProfileUserPhotosAdapter.deletedItems.removeAll(ProfileUserPhotosAdapter.deletedItems);//// TODO: 1/22/16 yesim incha, nayi!!!

        imageView = (ImageView) findViewById(R.id.edit_local_photo_image_view);
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

        Glide.with(context).asGif().load(gifUrl).thumbnail(Glide.with(context).asGif().load(gifUrl + GifsArtConst.DOWNLOAD_GIF_POSTFIX_240_F5)).apply(RequestOptions.noTransform().diskCacheStrategy(DiskCacheStrategy.RESOURCE)).into(imageView);

        if (!user.getPhoto().equals(GifsArtConst.EMPTY_PROFILE_IMAGE_PATH)) {
            Glide.with(this).asBitmap().load(user.getPhoto() + GifsArtConst.DOWNLOAD_GIF_POSTFIX_240).into(new BitmapImageViewTarget(((ImageView) findViewById(R.id.local_profile_image_view))) {
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable circularBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(getResources(), resource);
                    circularBitmapDrawable.setCircular(true);
                    ((ImageView) findViewById(R.id.local_profile_image_view)).setImageDrawable(circularBitmapDrawable);
                }
            });
        } else {
            ((ImageView) findViewById(R.id.local_profile_image_view)).setImageDrawable(getResources().getDrawable(R.drawable.profile_pic_personalize));
        }

        ((TextView) findViewById(R.id.local_username_text_view)).setText("@" + user.getName());

        makeItPublic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.haveNetworkConnection(context)) {
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
                } else {
                    Toast.makeText(context, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.delete_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(EditLocalPhotoActivity.this)
                        .setTitle("Delete gif")
                        .setMessage("Are you sure you want to delete this entry?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
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
                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

        findViewById(R.id.edit_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Glide.with(context)
                        .load("")
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(GlideException e, Object o, Target<Drawable> target, boolean b) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable drawable, Object o, Target<Drawable> target, DataSource dataSource, boolean b) {
                                return false;
                            }
                        })
                        .preload();

                final AnimatedProgressDialog animatedProgressDialog = new AnimatedProgressDialog(context);
                animatedProgressDialog.show();
                DownloadGifFromGiphyToFile downloadGifFromGiphyToFile = new DownloadGifFromGiphyToFile(EditLocalPhotoActivity.this, Environment.getExternalStorageDirectory() + "/ttt.gif", gifUrl);
                downloadGifFromGiphyToFile.setOnDownloadedListener(new DownloadGifFromGiphyToFile.OnDownloaded() {
                    @Override
                    public void onDownloaded(boolean isDownladed) {
                        if (isDownladed) {
                            Intent intent1 = new Intent(EditLocalPhotoActivity.this, MakeGifActivity.class);
                            intent1.putExtra(GifsArtConst.INTENT_ACTIVITY_INDEX, GifsArtConst.INDEX_GIPHY_TO_GIF);
                            startActivity(intent1);
                            animatedProgressDialog.dismiss();
                            finish();
                        }
                    }
                });
                downloadGifFromGiphyToFile.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });

        findViewById(R.id.share_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AnimatedProgressDialog animatedProgressDialog = new AnimatedProgressDialog(context);
                animatedProgressDialog.show();
                DownloadGifFromGiphyToFile downloadGifFromGiphyToFile = new DownloadGifFromGiphyToFile(EditLocalPhotoActivity.this, Environment.getExternalStorageDirectory() + "/ttt.gif", gifUrl);
                downloadGifFromGiphyToFile.setOnDownloadedListener(new DownloadGifFromGiphyToFile.OnDownloaded() {
                    @Override
                    public void onDownloaded(boolean isDownladed) {
                        if (isDownladed) {
                            Intent intent = new Intent(EditLocalPhotoActivity.this, ShareGifActivity.class);
                            intent.putExtra(GifsArtConst.INTENT_SAVED_FILE_PATH, Environment.getExternalStorageDirectory() + "/ttt.gif");
                            intent.putExtra(GifsArtConst.INTENT_SAVED_FILE_URL, gifUrl);
                            startActivity(intent);
                            animatedProgressDialog.dismiss();
                        }
                    }
                });
                downloadGifFromGiphyToFile.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });

        findViewById(R.id.edit_local_photo_activity_toolbar_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
