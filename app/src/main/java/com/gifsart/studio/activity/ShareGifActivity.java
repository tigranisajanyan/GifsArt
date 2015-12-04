package com.gifsart.studio.activity;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.gifsart.studio.R;
import com.gifsart.studio.adapter.ShareGifAdapter;
import com.gifsart.studio.social.ShareContraller;
import com.gifsart.studio.helper.RecyclerItemClickListener;
import com.gifsart.studio.item.ShareGifItem;
import com.gifsart.studio.utils.SpacesItemDecoration;
import com.gifsart.studio.utils.Utils;

import java.util.ArrayList;

public class ShareGifActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    private LinearLayoutManager linearLayoutManager;
    private RecyclerView.ItemAnimator itemAnimator;
    private ShareGifAdapter shareGifAdapter;

    private ArrayList<ShareGifItem> shareGifItems = new ArrayList<>();
    private static final String EXTRA_GIF_PATH = "gif_file_path_string";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_gif);

        shareGifItems.add(new ShareGifItem(R.drawable.facebook, "Facebook"));
        shareGifItems.add(new ShareGifItem(R.drawable.instagram, "Instagram"));
        shareGifItems.add(new ShareGifItem(R.drawable.twitter, "Twitter"));
        shareGifItems.add(new ShareGifItem(R.drawable.messenger, "Messenger"));
        shareGifItems.add(new ShareGifItem(R.drawable.whatsapp, "Whatsapp"));

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
                ShareContraller shareContraller = new ShareContraller(Environment.getExternalStorageDirectory() + "/test.gif", ShareGifActivity.this);
                switch (position) {
                    case 0:
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
                intent.putExtra(EXTRA_GIF_PATH, Environment.getExternalStorageDirectory() + "/test.gif");
                startActivity(intent);
                finish();
            }
        });

    }

}
