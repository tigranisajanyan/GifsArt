package com.gifsart.studio.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.gifsart.studio.R;
import com.gifsart.studio.adapter.ShareGifAdapter;
import com.gifsart.studio.helper.RecyclerItemClickListener;
import com.gifsart.studio.item.ShareGifItem;
import com.gifsart.studio.social.ShareContraller;
import com.gifsart.studio.social.UploadImageToPicsart;
import com.gifsart.studio.social.UserContraller;

import java.util.ArrayList;

public class ShareGifActivity extends AppCompatActivity {

    private static final int REQUEST_SIGNIN_ACTIVITY = 111;
    private static final String SHARE_GIF_TITLE = "Title";
    private static final String SHARE_GIF_ACTIVTY_TITLE = "Share";

    private android.support.v7.app.ActionBar actionBar;
    private RecyclerView shareItemRecyclerView;

    private LinearLayoutManager linearLayoutManager;
    private RecyclerView.ItemAnimator itemAnimator;
    private ShareGifAdapter shareGifAdapter;

    private String filePath;
    private String fileUrl;

    private ArrayList<ShareGifItem> shareGifItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_gif);

        actionBar = getSupportActionBar();
        actionBar.setTitle(SHARE_GIF_ACTIVTY_TITLE);
        actionBar.setDisplayHomeAsUpEnabled(true);

        //StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        //StrictMode.setThreadPolicy(policy);

        shareGifItems.add(new ShareGifItem(R.drawable.facebook, R.color.color_facebook, ShareContraller.ShareGifType.FACEBOOK));
        shareGifItems.add(new ShareGifItem(R.drawable.messenger, R.color.color_messenger, ShareContraller.ShareGifType.MESSENGER));
        shareGifItems.add(new ShareGifItem(R.drawable.twitter, R.color.color_twitter, ShareContraller.ShareGifType.TWITTER));
        shareGifItems.add(new ShareGifItem(R.drawable.instagram, R.color.color_instagram, ShareContraller.ShareGifType.INSTAGRAM));
        shareGifItems.add(new ShareGifItem(R.drawable.whatsapp, R.color.color_whatsapp, ShareContraller.ShareGifType.WHATSAPP));
        shareGifItems.add(new ShareGifItem(R.drawable.whatsapp, R.color.color_email, ShareContraller.ShareGifType.EMAIL));
        shareGifItems.add(new ShareGifItem(R.drawable.whatsapp, R.color.color_more, ShareContraller.ShareGifType.MORE));

        filePath = getIntent().getStringExtra("saved_file_path");
        fileUrl = getIntent().getStringExtra("saved_file_url");

        shareItemRecyclerView = (RecyclerView) findViewById(R.id.share_gif_activity_rec_view);

        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        itemAnimator = new DefaultItemAnimator();
        shareGifAdapter = new ShareGifAdapter(shareGifItems, this);

        shareItemRecyclerView.setHasFixedSize(true);
        shareItemRecyclerView.setClipToPadding(true);
        shareItemRecyclerView.setLayoutManager(linearLayoutManager);
        shareItemRecyclerView.setItemAnimator(itemAnimator);
        shareItemRecyclerView.setAdapter(shareGifAdapter);

        shareItemRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position == 0) {
                    if (fileUrl == null) {
                        Intent intent = new Intent(ShareGifActivity.this, SignInActivity.class);
                        startActivityForResult(intent, REQUEST_SIGNIN_ACTIVITY);
                    } else {
                        shareFacebook();
                    }
                } else {
                    ShareContraller shareContraller = new ShareContraller(filePath, ShareGifActivity.this);
                    shareContraller.shareGif(shareGifItems.get(position).getItemType());
                }
            }
        }));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SIGNIN_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                final UploadImageToPicsart uploadImageToPicsart = new UploadImageToPicsart(ShareGifActivity.this, UserContraller.readUserFromFile(ShareGifActivity.this).getKey(), filePath, SHARE_GIF_TITLE, UploadImageToPicsart.PHOTO_PUBLIC.PUBLIC, UploadImageToPicsart.PHOTO_IS.GENERAL);
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
                uploadImageToPicsart.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_share_gif, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.action_done:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
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
