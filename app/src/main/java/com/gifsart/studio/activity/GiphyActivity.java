package com.gifsart.studio.activity;

import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.gifsart.studio.R;
import com.gifsart.studio.adapter.GiphyAdapter;
import com.gifsart.studio.gifutils.GifUtils;
import com.gifsart.studio.gifutils.Giphy;
import com.gifsart.studio.helper.RecyclerItemClickListener;
import com.gifsart.studio.item.GiphyItem;
import com.gifsart.studio.utils.CheckSpaceSingleton;
import com.gifsart.studio.utils.DownloadFileAsyncTask;
import com.gifsart.studio.utils.GifsArtConst;
import com.gifsart.studio.utils.SpacesItemDecoration;
import com.gifsart.studio.utils.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import pl.droidsonroids.gif.GifDrawable;

public class GiphyActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    private GiphyAdapter giphyAdapter;

    private ArrayList<GiphyItem> giphyItems = new ArrayList<>();
    private String tag = GifsArtConst.GIPHY_TAG;

    private int offset = 0;
    private int limit = GifsArtConst.GIPHY_LIMIT_COUNT;

    private static final String giphyName = "giphy.gif";
    private int lastSelectedPosition = -1;
    private SharedPreferences sharedPreferences;

    private static final String root = Environment.getExternalStorageDirectory().toString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giphy);

        init();

    }

    public void init() {

        sharedPreferences = getSharedPreferences(GifsArtConst.SHARED_PREFERENCES, MODE_PRIVATE);
        giphyAdapter = new GiphyAdapter(tag, false, this);

        recyclerView = (RecyclerView) findViewById(R.id.giphy_rec_view);
        gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);

        recyclerView.setHasFixedSize(true);
        recyclerView.setClipToPadding(true);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(giphyAdapter);
        recyclerView.addItemDecoration(new SpacesItemDecoration(5));

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (lastSelectedPosition == -1) {
                    if (CheckSpaceSingleton.getInstance().haveEnoughSpaceInt(giphyItems.get(position).getFramesCount())) {
                        giphyItems.get(position).setIsSelected(true);
                        giphyAdapter.notifyItemChanged(lastSelectedPosition);
                        giphyAdapter.notifyItemChanged(position);
                        lastSelectedPosition = position;
                        return;
                    } else {
                        Toast.makeText(GiphyActivity.this, "No enough space", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (lastSelectedPosition == position) {
                        giphyItems.get(position).setIsSelected(false);
                        giphyAdapter.notifyItemChanged(lastSelectedPosition);
                        lastSelectedPosition = -1;
                    } else {
                        if (CheckSpaceSingleton.getInstance().haveEnoughSpaceInt(giphyItems.get(position).getFramesCount())) {
                            giphyItems.get(lastSelectedPosition).setIsSelected(false);
                            giphyItems.get(position).setIsSelected(true);
                            giphyAdapter.notifyItemChanged(lastSelectedPosition);
                            giphyAdapter.notifyItemChanged(position);
                            lastSelectedPosition = position;
                            return;
                        } else {
                            Toast.makeText(GiphyActivity.this, "No enough space", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        }));

        if (Utils.haveNetworkConnection(this)) {
            Giphy giphy = new Giphy(this, tag, false, offset, limit);
            giphy.requestGiphy();
            giphy.setOnDownloadedListener(new Giphy.GiphyListener() {
                @Override
                public void onGiphyDownloadFinished(ArrayList<GiphyItem> items) {
                    giphyItems.addAll(items);
                    giphyAdapter.addItems(giphyItems);
                }
            });
        } else {
            Toast.makeText(this, "No Wifi Connection", Toast.LENGTH_SHORT).show();
            finish();
        }

        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView search = (SearchView) findViewById(R.id.search_giphy_search_view);
        search.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
        search.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                tag = GifsArtConst.GIPHY_TAG;
                giphyAdapter.setTag(tag);
                Giphy giphy = new Giphy(GiphyActivity.this, tag, false, offset, limit);
                giphy.requestGiphy();
                giphy.setOnDownloadedListener(new Giphy.GiphyListener() {
                    @Override
                    public void onGiphyDownloadFinished(ArrayList<GiphyItem> items) {
                        giphyAdapter.clear();
                        giphyAdapter.addItems(items);
                        search.clearFocus();
                    }
                });
                return false;
            }
        });

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                Giphy giphy = new Giphy(GiphyActivity.this, query, false, 0, limit);
                giphy.requestGiphy();
                giphy.setOnDownloadedListener(new Giphy.GiphyListener() {
                    @Override
                    public void onGiphyDownloadFinished(ArrayList<GiphyItem> items) {
                        tag = query;
                        giphyAdapter.setTag(tag);
                        giphyAdapter.clear();
                        giphyAdapter.addItems(items);
                        search.clearFocus();
                    }
                });
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText == "") {

                }
                return false;
            }
        });

        findViewById(R.id.giphy_next_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lastSelectedPosition != -1) {
                    if (!sharedPreferences.getBoolean(GifsArtConst.SHARED_PREFERENCES_IS_OPENED, false)) {
                        sendIntentWithGif(new Intent(GiphyActivity.this, MakeGifActivity.class), false);
                    } else {
                        sendIntentWithGif(new Intent(), true);
                    }
                } else {
                    Toast.makeText(GiphyActivity.this, "No gif is selected", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void sendIntentWithGif(final Intent intent, final boolean isOpened) {

        DownloadFileAsyncTask downloadFileAsyncTask = new DownloadFileAsyncTask(GiphyActivity.this, root + "/" + GifsArtConst.DIR_GIPHY + "/" + giphyName, giphyItems.get(lastSelectedPosition));
        downloadFileAsyncTask.setOnDownloadedListener(new DownloadFileAsyncTask.OnDownloaded() {
            @Override
            public void onDownloaded(boolean isDownloded) {
                intent.putExtra(GifsArtConst.INTENT_GIF_PATH, root + "/" + GifsArtConst.DIR_GIPHY + "/" + giphyName);
                intent.putExtra(GifsArtConst.INTENT_ACTIVITY_INDEX, GifsArtConst.INDEX_GIPHY_TO_GIF);
                CheckSpaceSingleton.getInstance().addAllocatedSpaceInt(GifUtils.getGifFramesCount(root + "/" + GifsArtConst.DIR_GIPHY + "/" + giphyName));
                if (isOpened) {
                    setResult(RESULT_OK, intent);
                } else {
                    startActivity(intent);
                }
                finish();
            }
        });
        downloadFileAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

}
