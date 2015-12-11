package com.gifsart.studio.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.gifsart.studio.R;
import com.gifsart.studio.adapter.ShareGifAdapter;
import com.gifsart.studio.social.FacebookConstants;
import com.gifsart.studio.social.ShareContraller;
import com.gifsart.studio.helper.RecyclerItemClickListener;
import com.gifsart.studio.item.ShareGifItem;
import com.gifsart.studio.utils.SpacesItemDecoration;
import com.gifsart.studio.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ShareGifActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    private LinearLayoutManager linearLayoutManager;
    private RecyclerView.ItemAnimator itemAnimator;
    private ShareGifAdapter shareGifAdapter;

    private String filePath;

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

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("gag", "done");
            }

            @Override
            public void onCancel() {
                Log.d("gag", "cancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("gag", error.toString());
            }
        });

        shareGifItems.add(new ShareGifItem(R.drawable.facebook, "Facebook"));
        shareGifItems.add(new ShareGifItem(R.drawable.instagram, "Instagram"));
        shareGifItems.add(new ShareGifItem(R.drawable.twitter, "Twitter"));
        shareGifItems.add(new ShareGifItem(R.drawable.messenger, "Messenger"));
        shareGifItems.add(new ShareGifItem(R.drawable.whatsapp, "Whatsapp"));

        filePath = getIntent().getStringExtra("saved_file_path");

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
                        shareFacebook();
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
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void shareFacebook() {

        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("publish_actions"));

        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse("http://cdn78.picsart.com/186853261001202.gif"))
                .build();

        ShareApi.share(content, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Log.d("gag", result.toString());
            }

            @Override
            public void onCancel() {
                Log.d("gag", "cancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("gag", error.toString());
            }
        });
    }
}
