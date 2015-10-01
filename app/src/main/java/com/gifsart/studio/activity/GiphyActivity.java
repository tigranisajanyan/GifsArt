package com.gifsart.studio.activity;

import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.gifsart.studio.R;
import com.gifsart.studio.adapter.GiphyAdapter;
import com.gifsart.studio.gifutils.Giphy;
import com.gifsart.studio.item.GiphyItem;
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
    private static final String root = Environment.getExternalStorageDirectory().toString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giphy);

        init();

    }

    public void init() {

        giphyAdapter = new GiphyAdapter(this);

        recyclerView = (RecyclerView) findViewById(R.id.giphy_rec_view);
        gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        itemAnimator = new DefaultItemAnimator();

        recyclerView.setHasFixedSize(true);
        recyclerView.setClipToPadding(true);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setItemAnimator(itemAnimator);

        recyclerView.setAdapter(giphyAdapter);
        recyclerView.addItemDecoration(new SpacesItemDecoration(5));
/*
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);


               *//* boolean isHide = false;*//*
                if (!recyclerView.canScrollVertically(1)) {
                    Log.d("Tag", "end!!!!!");


                } else if (dy < 0) {
                    if (ishide) {
                        TranslateAnimation translation;
                        translation = new TranslateAnimation(0, 0, -100, 0);
                        translation.setStartOffset(0);
                        translation.setDuration(200);
                        translation.setFillAfter(true);
                        translation.setInterpolator(new AccelerateInterpolator());
                        btn.startAnimation(translation);
                        ishide = false;

                        Log.d("Tag", "Dovn");
                        //open       search typing tab
                        Animation ani = new ShowAnim(searchText, width*//* target layout height *//*);
                        ani.setStartOffset(200);
                        ani.setDuration(150*//* animation time *//*);
                        searchText.startAnimation(ani);
                        isshown = true;
                    }
                   *//* fbtn.setVisibility(View.INVISIBLE);*//*

                } else if (dy > 0) {

                    if (!ishide) {

                        TranslateAnimation translation;
                        translation = new TranslateAnimation(0, 0, 0, -100);
                        translation.setStartOffset(0);
                        translation.setDuration(200);
                        translation.setFillAfter(true);
                        translation.setInterpolator(new AccelerateInterpolator());
                        btn.startAnimation(translation);

                        Log.d("Tag", "Up");
                        //close search typing tab
                        ishide = true;
                        Animation ani = new ShowAnim(searchText, 0*//* target layout height *//*);
                        ani.setDuration(150*//* animation time *//*);
                        searchText.startAnimation(ani);
                    }
                   *//* fbtn.startAnimation(animBtn);*//*


                }

            }
        });*/


        if (Utils.haveNetworkConnection(this)) {

            Giphy giphy = new Giphy(this, "funny", 0, 30);
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

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_giphy, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_done) {

            if (giphyAdapter.getSelectedPosition() > -1) {

                DownloadFileAsyncTask downloadFileAsyncTask = new DownloadFileAsyncTask(GiphyActivity.this, root + GifsArtConst.SLASH + GifsArtConst.MY_DIR + "/tt.gif", giphyAdapter.getItem(giphyAdapter.getSelectedPosition()));
                downloadFileAsyncTask.setOnDownloadedListener(new DownloadFileAsyncTask.OnDownloaded() {
                    @Override
                    public void onDownloaded(boolean isDownloded) {
                        Intent intent = new Intent(GiphyActivity.this, MakeGifActivity.class);
                        intent.putExtra("gif_path", root + GifsArtConst.SLASH + GifsArtConst.MY_DIR + "/tt.gif");
                        intent.putExtra(GifsArtConst.INDEX, GifsArtConst.GIPHY_TO_GIF_INDEX);
                        startActivity(intent);
                        finish();
                    }
                });
                downloadFileAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
