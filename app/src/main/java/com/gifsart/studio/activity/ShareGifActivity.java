package com.gifsart.studio.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.facebook.CallbackManager;
import com.facebook.login.LoginManager;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.gifsart.studio.R;
import com.gifsart.studio.adapter.ShareGifAdapter;
import com.gifsart.studio.helper.RecyclerItemClickListener;
import com.gifsart.studio.item.ShareGifItem;
import com.gifsart.studio.social.ShareContraller;
import com.gifsart.studio.social.UploadImageToPicsart;
import com.gifsart.studio.social.UserContraller;
import com.gifsart.studio.utils.SpacesItemDecoration;
import com.gifsart.studio.utils.Utils;

import java.util.ArrayList;

public class ShareGifActivity extends AppCompatActivity {

    private static final int REQUEST_SIGNIN_ACTIVITY = 111;

    private RecyclerView recyclerView;

    private LinearLayoutManager linearLayoutManager;
    private RecyclerView.ItemAnimator itemAnimator;
    private ShareGifAdapter shareGifAdapter;

    private String filePath;
    private String fileUrl;

    private CallbackManager callbackManager;
    private LoginManager loginManager;


    private ArrayList<ShareGifItem> shareGifItems = new ArrayList<>();
    private static final String EXTRA_GIF_PATH = "gif_file_path_string";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_gif);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        shareGifItems.add(new ShareGifItem(R.drawable.facebook, "Facebook"));
        shareGifItems.add(new ShareGifItem(R.drawable.instagram, "Instagram"));
        shareGifItems.add(new ShareGifItem(R.drawable.twitter, "Twitter"));
        shareGifItems.add(new ShareGifItem(R.drawable.messenger, "Messenger"));
        shareGifItems.add(new ShareGifItem(R.drawable.whatsapp, "Whatsapp"));

        filePath = getIntent().getStringExtra("saved_file_path");
        fileUrl = getIntent().getStringExtra("saved_file_url");

        recyclerView = (RecyclerView) findViewById(R.id.share_gif_activity_rec_view);

        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        itemAnimator = new DefaultItemAnimator();
        shareGifAdapter = new ShareGifAdapter(shareGifItems, this);

        recyclerView.setHasFixedSize(true);
        recyclerView.setClipToPadding(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(itemAnimator);

        recyclerView.setAdapter(shareGifAdapter);
        recyclerView.addItemDecoration(new SpacesItemDecoration((int) Utils.dpToPixel(4, this)));

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ShareContraller shareContraller = new ShareContraller(filePath, ShareGifActivity.this);
                switch (position) {
                    case 0:
                        if (fileUrl == null) {
                            Intent intent = new Intent(ShareGifActivity.this, SignInActivity.class);
                            startActivityForResult(intent, REQUEST_SIGNIN_ACTIVITY);
                        } else {
                            shareFacebook();
                        }
                        break;
                    case 1:
                        shareContraller.shareInstagram();
                        break;
                    case 2:
                        shareContraller.shareTwitter();
                        break;
                    case 3:
                        shareContraller.shareMessenger();
                        break;
                    case 4:
                        shareContraller.shareWhatsApp();
                        break;
                    default:
                        break;
                }
            }
        }));

        findViewById(R.id.share_gif_activity_done_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShareGifActivity.this, GifPreviewActivity.class);
                intent.putExtra(EXTRA_GIF_PATH, filePath);
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SIGNIN_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                final UploadImageToPicsart uploadImageToPicsart = new UploadImageToPicsart(ShareGifActivity.this, UserContraller.readUserFromFile(ShareGifActivity.this).getKey(), filePath, "title", UploadImageToPicsart.PHOTO_PUBLIC.PUBLIC, UploadImageToPicsart.PHOTO_IS.GENERAL);
                uploadImageToPicsart.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                uploadImageToPicsart.setOnUploadedListener(new UploadImageToPicsart.ImageUploaded() {
                    @Override
                    public void uploadIsDone(boolean uploaded, String messege) {
                        if (uploaded) {
                            fileUrl = uploadImageToPicsart.getUploadedImageUrl();
                        } else {
                            AlertDialog alert = UserContraller.setupDialogBuilder(ShareGifActivity.this, messege).create();
                            alert.show();
                        }
                    }
                });
            }
        }
    }

    public void shareFacebook() {
        if (ShareDialog.canShow(ShareLinkContent.class)) {
            String desc = "GifsArt";
            ShareLinkContent linkContent = new ShareLinkContent.Builder().setContentTitle("title").setContentDescription(desc).
                    setContentUrl(Uri.parse(fileUrl)).build();
            ShareDialog.show(this, linkContent);
        }
    }


}
