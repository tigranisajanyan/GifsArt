package com.gifsart.studio.activity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.gifsart.studio.R;
import com.gifsart.studio.adapter.GalleryAdapter;
import com.gifsart.studio.item.GalleryItem;
import com.gifsart.studio.utils.GifsArtConst;
import com.gifsart.studio.utils.SpacesItemDecoration;
import com.gifsart.studio.utils.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private GridLayoutManager gridLayoutManager;
    private RecyclerView.ItemAnimator itemAnimator;
    private GalleryAdapter galleryAdapter;
    private ArrayList<GalleryItem> customGalleryArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Fresco.initialize(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Utils.initImageLoader(getApplicationContext());
        ImageLoader.getInstance().clearMemoryCache();
        ImageLoader.getInstance().clearDiskCache();

        Utils.createDir(GifsArtConst.MY_DIR);

        init();

    }

    public void init() {

        galleryAdapter = new GalleryAdapter(customGalleryArrayList, this, (int) Utils.getBitmapWidth(this), getSupportActionBar());

        recyclerView = (RecyclerView) findViewById(R.id.gallery_rec_view);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        gridLayoutManager = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        itemAnimator = new DefaultItemAnimator();

        recyclerView.setHasFixedSize(true);
        recyclerView.setClipToPadding(true);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setItemAnimator(itemAnimator);

        recyclerView.setAdapter(galleryAdapter);
        recyclerView.addItemDecoration(new SpacesItemDecoration(4));

        new MyTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_next) {
            if (galleryAdapter.getSelected().size() > 0) {

                Intent intent = new Intent(MainActivity.this, MakeGifActivity.class);
                intent.putExtra(GifsArtConst.INDEX, GifsArtConst.FROM_GALLERY_TO_GIF_INDEX);
                intent.putStringArrayListExtra(GifsArtConst.IMAGE_PATHS, galleryAdapter.getSelected());

                startActivity(intent);
                galleryAdapter.deselectAll();

            } else {
                Toast.makeText(MainActivity.this, "no images selected", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class MyTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            GalleryItem galleryItem1 = new GalleryItem(BitmapFactory.decodeResource(getResources(), R.drawable.camera_icon));
            GalleryItem galleryItem2 = new GalleryItem(BitmapFactory.decodeResource(getResources(), R.drawable.giphy_icon));

            customGalleryArrayList.add(galleryItem1);
            customGalleryArrayList.add(galleryItem2);
            customGalleryArrayList.addAll(Utils.getGalleryPhotos(MainActivity.this));

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            galleryAdapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);
        }
    }

}
