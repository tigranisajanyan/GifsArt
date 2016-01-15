package com.gifsart.studio.activity;

import android.app.SearchManager;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.SearchView;

import com.gifsart.studio.R;
import com.gifsart.studio.adapter.GiphyAdapter;
import com.gifsart.studio.gifutils.GiphyApiRequest;
import com.gifsart.studio.item.GiphyItem;
import com.gifsart.studio.utils.SpacesItemDecoration;

import java.util.ArrayList;

public class GiphyStickerActivity extends AppCompatActivity {

    private SearchView searchView;
    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    private RecyclerView.ItemAnimator itemAnimator;

    private GiphyAdapter giphyAdapter;

    private String tag = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giphy_sticker);

        recyclerView = (RecyclerView) findViewById(R.id.giphy_sticker_rec_view);
        gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        itemAnimator = new DefaultItemAnimator();

        recyclerView.setHasFixedSize(true);
        recyclerView.setClipToPadding(true);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setItemAnimator(itemAnimator);

        recyclerView.addItemDecoration(new SpacesItemDecoration(5));

        giphyAdapter = new GiphyAdapter(new ArrayList<GiphyItem>(),"", true, this);
        recyclerView.setAdapter(giphyAdapter);

        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) findViewById(R.id.giphy_sticker_search);

        searchView.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                GiphyApiRequest giphyApiRequest = new GiphyApiRequest(GiphyStickerActivity.this, query, true, 0, 30);
                giphyApiRequest.requestGiphy();
                giphyApiRequest.setOnDownloadedListener(new GiphyApiRequest.GiphyListener() {
                    @Override
                    public void onGiphyDownloadFinished(ArrayList<GiphyItem> items) {
                        tag = query;
                        giphyAdapter.clear();
                        giphyAdapter.addItems(items);
                        searchView.clearFocus();
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
    }
}
