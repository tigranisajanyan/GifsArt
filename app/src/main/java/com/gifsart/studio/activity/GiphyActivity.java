package com.gifsart.studio.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

import com.gifsart.studio.R;
import com.gifsart.studio.adapter.GiphyAdapter;
import com.gifsart.studio.gifutils.Giphy;
import com.gifsart.studio.item.GiphyItem;
import com.gifsart.studio.utils.CheckSpaceSingleton;
import com.gifsart.studio.utils.DownloadFileAsyncTask;
import com.gifsart.studio.utils.GifsArtConst;
import com.gifsart.studio.utils.SpacesItemDecoration;
import com.gifsart.studio.utils.Utils;

import java.util.ArrayList;

public class GiphyActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    private RecyclerView.ItemAnimator itemAnimator;
    private GiphyAdapter giphyAdapter;

    private String tag = GifsArtConst.GIPHY_TAG;

    private int offset = 0;
    private int limit = 30;

    SharedPreferences sharedPreferences;
    private static final String root = Environment.getExternalStorageDirectory().toString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giphy);

        init();

    }

    public void init() {

        giphyAdapter = new GiphyAdapter(tag, false, this);

        recyclerView = (RecyclerView) findViewById(R.id.giphy_rec_view);
        gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        itemAnimator = new DefaultItemAnimator();

        recyclerView.setHasFixedSize(true);
        recyclerView.setClipToPadding(true);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setItemAnimator(itemAnimator);

        recyclerView.setAdapter(giphyAdapter);
        recyclerView.addItemDecoration(new SpacesItemDecoration(5));

        sharedPreferences = getSharedPreferences(GifsArtConst.SHARED_PREFERENCES, MODE_PRIVATE);

        if (Utils.haveNetworkConnection(this)) {

            Giphy giphy = new Giphy(this, tag, false, offset, limit);
            giphy.requestGiphy();
            giphy.setOnDownloadedListener(new Giphy.GiphyListener() {
                @Override
                public void onGiphyDownloadFinished(ArrayList<GiphyItem> items) {
                    giphyAdapter.addItems(items);
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
                Giphy giphy = new Giphy(GiphyActivity.this, tag, false, 0, 30);
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
                Giphy giphy = new Giphy(GiphyActivity.this, query, false, 0, 30);
                giphy.requestGiphy();
                giphy.setOnDownloadedListener(new Giphy.GiphyListener() {
                    @Override
                    public void onGiphyDownloadFinished(ArrayList<GiphyItem> items) {
                        tag = query;
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
                if (!sharedPreferences.getBoolean(GifsArtConst.SHARED_PREFERENCES_IS_OPENED, false)) {
                    if (giphyAdapter.getSelectedPosition() > -1) {
                        sendIntentWithGif(new Intent(GiphyActivity.this, MakeGifActivity.class), false);
                    }
                } else {
                    if (giphyAdapter.getSelectedPosition() > -1) {
                        sendIntentWithGif(new Intent(), true);
                    }
                }
            }
        });
    }

    public void sendIntentWithGif(final Intent intent, final boolean isOpened) {
        DownloadFileAsyncTask downloadFileAsyncTask = new DownloadFileAsyncTask(GiphyActivity.this, root + GifsArtConst.SLASH + GifsArtConst.MY_DIR + "/giphy/giphy.gif", giphyAdapter.getItem(giphyAdapter.getSelectedPosition()));
        downloadFileAsyncTask.setOnDownloadedListener(new DownloadFileAsyncTask.OnDownloaded() {
            @Override
            public void onDownloaded(boolean isDownloded) {
                intent.putExtra(GifsArtConst.INTENT_GIF_PATH, root + GifsArtConst.SLASH + GifsArtConst.MY_DIR + "/giphy/giphy.gif");
                intent.putExtra(GifsArtConst.INTENT_ACTIVITY_INDEX, GifsArtConst.INDEX_GIPHY_TO_GIF);
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
