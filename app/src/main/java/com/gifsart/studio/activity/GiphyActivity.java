package com.gifsart.studio.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import com.gifsart.studio.R;
import com.gifsart.studio.adapter.GiphyAdapter;
import com.gifsart.studio.gifutils.GifUtils;
import com.gifsart.studio.gifutils.Giphy;
import com.gifsart.studio.gifutils.GiphyToByteArray;
import com.gifsart.studio.helper.RecyclerItemClickListener;
import com.gifsart.studio.item.GiphyItem;
import com.gifsart.studio.utils.CheckFreeSpaceSingleton;
import com.gifsart.studio.utils.GifsArtConst;
import com.gifsart.studio.utils.SpacesItemDecoration;
import com.gifsart.studio.utils.Utils;

import java.util.ArrayList;

public class GiphyActivity extends AppCompatActivity {

    private RecyclerView giphyRecyclerView;
    private GridLayoutManager gridLayoutManager;
    private GiphyAdapter giphyAdapter;

    private ArrayList<GiphyItem> giphyItems = new ArrayList<>();
    private String tag = GifsArtConst.GIPHY_TAG;

    private int offset = 0;
    private int limit = GifsArtConst.GIPHY_LIMIT_COUNT;
    private int lastSelectedPosition = -1;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giphy);

        init();

    }

    public void init() {

        sharedPreferences = getSharedPreferences(GifsArtConst.SHARED_PREFERENCES, MODE_PRIVATE);
        giphyAdapter = new GiphyAdapter(giphyItems, tag, false, this);

        giphyRecyclerView = (RecyclerView) findViewById(R.id.giphy_rec_view);
        gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);

        giphyRecyclerView.setHasFixedSize(true);
        giphyRecyclerView.setClipToPadding(true);
        giphyRecyclerView.setLayoutManager(gridLayoutManager);
        giphyRecyclerView.setItemAnimator(new DefaultItemAnimator());

        giphyRecyclerView.setAdapter(giphyAdapter);
        giphyRecyclerView.addItemDecoration(new SpacesItemDecoration(5));

        if (Utils.haveNetworkConnection(this)) {
            Giphy giphy = new Giphy(this, tag, false, offset, limit);
            giphy.requestGiphy();
            giphy.setOnDownloadedListener(new Giphy.GiphyListener() {
                @Override
                public void onGiphyDownloadFinished(ArrayList<GiphyItem> items) {
                    giphyItems.addAll(items);
                    giphyAdapter.notifyDataSetChanged();
                }
            });
        } else {
            Toast.makeText(this, "No Wifi Connection", Toast.LENGTH_SHORT).show();
            finish();
        }

        giphyRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (lastSelectedPosition == -1) {
                    if (CheckFreeSpaceSingleton.getInstance().haveEnoughSpaceInt(giphyItems.get(position).getFramesCount())) {
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
                        if (CheckFreeSpaceSingleton.getInstance().haveEnoughSpaceInt(giphyItems.get(position).getFramesCount())) {
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

        findViewById(R.id.giphy_toolbar_back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
        GiphyToByteArray giphyToByteArray = new GiphyToByteArray(GiphyActivity.this, giphyItems.get(lastSelectedPosition));
        giphyToByteArray.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        giphyToByteArray.setOnDownloadedListener(new GiphyToByteArray.OnDownloaded() {
            @Override
            public void onDownloaded(boolean isDownladed) {
                if (isDownladed) {
                    intent.putExtra(GifsArtConst.INTENT_ACTIVITY_INDEX, GifsArtConst.INDEX_GIPHY_TO_GIF);
                    CheckFreeSpaceSingleton.getInstance().addAllocatedSpaceInt(GifUtils.getGifFramesCountFromByteArray(GiphyToByteArray.buffer));
                    if (isOpened) {
                        setResult(RESULT_OK, intent);
                    } else {
                        startActivity(intent);
                    }
                    finish();
                }
            }
        });
    }

}
