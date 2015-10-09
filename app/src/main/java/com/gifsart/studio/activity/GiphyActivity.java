package com.gifsart.studio.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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

    SharedPreferences sharedPreferences;
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

        sharedPreferences = getSharedPreferences(GifsArtConst.SHARED_PREFERENCES, MODE_PRIVATE);

        if (Utils.haveNetworkConnection(this)) {

            Giphy giphy = new Giphy(this, GifsArtConst.GIPHY_TAG, 0, 30);
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

            if (!sharedPreferences.getBoolean(GifsArtConst.SHARED_PREFERENCES_IS_OPENED, false)) {
                if (giphyAdapter.getSelectedPosition() > -1) {

                    DownloadFileAsyncTask downloadFileAsyncTask = new DownloadFileAsyncTask(GiphyActivity.this, root + GifsArtConst.SLASH + GifsArtConst.MY_DIR + "/giphy/giphy.gif", giphyAdapter.getItem(giphyAdapter.getSelectedPosition()));
                    downloadFileAsyncTask.setOnDownloadedListener(new DownloadFileAsyncTask.OnDownloaded() {
                        @Override
                        public void onDownloaded(boolean isDownloded) {
                            Intent intent = new Intent(GiphyActivity.this, MakeGifActivity.class);
                            intent.putExtra(GifsArtConst.INTENT_GIF_PATH, root + GifsArtConst.SLASH + GifsArtConst.MY_DIR + "/giphy/giphy.gif");
                            intent.putExtra(GifsArtConst.INTENT_ACTIVITY_INDEX, GifsArtConst.INDEX_GIPHY_TO_GIF);
                            startActivity(intent);
                            finish();
                        }
                    });
                    downloadFileAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                }
            } else {
                if (giphyAdapter.getSelectedPosition() > -1) {

                    DownloadFileAsyncTask downloadFileAsyncTask = new DownloadFileAsyncTask(GiphyActivity.this, root + GifsArtConst.SLASH + GifsArtConst.MY_DIR + "/giphy/giphy.gif", giphyAdapter.getItem(giphyAdapter.getSelectedPosition()));
                    downloadFileAsyncTask.setOnDownloadedListener(new DownloadFileAsyncTask.OnDownloaded() {
                        @Override
                        public void onDownloaded(boolean isDownloded) {
                            Intent intent = new Intent();
                            intent.putExtra(GifsArtConst.INTENT_GIF_PATH, root + GifsArtConst.SLASH + GifsArtConst.MY_DIR + "/giphy/giphy.gif");
                            intent.putExtra(GifsArtConst.INTENT_ACTIVITY_INDEX, GifsArtConst.INDEX_GIPHY_TO_GIF);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    });
                    downloadFileAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                }
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
