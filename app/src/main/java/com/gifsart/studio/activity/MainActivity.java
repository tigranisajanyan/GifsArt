package com.gifsart.studio.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.decoder.VideoDecoder;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.gifsart.studio.R;
import com.gifsart.studio.adapter.GalleryAdapter;
import com.gifsart.studio.image_picker.ImageData;
import com.gifsart.studio.image_picker.OnImagesRetrievedListener;
import com.gifsart.studio.image_picker.PhoneImagesRetriever;
import com.gifsart.studio.item.GalleryItem;
import com.gifsart.studio.utils.GifsArtConst;
import com.gifsart.studio.utils.SpacesItemDecoration;
import com.gifsart.studio.utils.Type;
import com.gifsart.studio.utils.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private GridLayoutManager gridLayoutManager;
    private RecyclerView.ItemAnimator itemAnimator;
    private GalleryAdapter galleryAdapter;
    private ArrayList<GalleryItem> customGalleryArrayList = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Fresco.initialize(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Utils.initImageLoader(getApplicationContext());
        ImageLoader.getInstance().clearMemoryCache();
        ImageLoader.getInstance().clearDiskCache();

        Utils.createDir(GifsArtConst.MY_DIR);
        Utils.createDir(GifsArtConst.DIR_GIPHY);
        Utils.createDir(GifsArtConst.DIR_GPU_IMAGES);
        Utils.createDir(GifsArtConst.DIR_VIDEO_FRAMES);

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
        recyclerView.addItemDecoration(new SpacesItemDecoration((int) Utils.dpToPixel(2, this)));

        sharedPreferences = getApplicationContext().getSharedPreferences(GifsArtConst.SHARED_PREFERENCES, MODE_PRIVATE);
        editor = sharedPreferences.edit();

        new InitGalleryItems().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        /*GalleryItem galleryItem1 = new GalleryItem(BitmapFactory.decodeResource(getResources(), R.drawable.camera_icon));
        galleryItem1.setIsSeleted(false);
        GalleryItem galleryItem2 = new GalleryItem(BitmapFactory.decodeResource(getResources(), R.drawable.giphy_icon));
        galleryItem2.setIsSeleted(false);

        customGalleryArrayList.add(galleryItem1);
        customGalleryArrayList.add(galleryItem2);

        PhoneImagesRetriever retriever = new PhoneImagesRetriever(getLoaderManager(), getApplicationContext());
        retriever.retrieveImages(new OnImagesRetrievedListener() {
            @Override
            public void onImagesRetrieved(ArrayList<ImageData> data) {
                for (int i = 0; i < data.size(); i++) {

                    customGalleryArrayList.add(new GalleryItem(data.get(i).getImagePath()));
                }
                galleryAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }
        });*/

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

                galleryAdapter.notifyDataSetChanged();

                // if MainActivity is opened for the first time will do this
                if (!sharedPreferences.getBoolean(GifsArtConst.SHARED_PREFERENCES_IS_OPENED, false)) {
                    final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                    progressDialog.setMessage(getApplicationContext().getResources().getString(R.string.please_wait));
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    boolean hasVideo = false;
                    ArrayList<String> arrayList = galleryAdapter.getSelected();
                    for (int i = 0; i < arrayList.size(); i++) {
                        if (Utils.getMimeType(arrayList.get(i)) != null && Utils.getMimeType(arrayList.get(i)) == Type.VIDEO) {

                            VideoDecoder videoDecoder = new VideoDecoder(MainActivity.this, arrayList.get(i), Integer.MAX_VALUE, GifsArtConst.VIDEO_FRAME_SCALE_SIZE, GifsArtConst.VIDEOS_DECODED_FRAMES_DIR);
                            videoDecoder.extractVideoFrames();
                            videoDecoder.setOnDecodeFinishedListener(new VideoDecoder.OnDecodeFinishedListener() {
                                @Override
                                public void onFinish(boolean isDone) {
                                    Intent intent = new Intent(MainActivity.this, MakeGifActivity.class);
                                    intent.putExtra(GifsArtConst.INTENT_ACTIVITY_INDEX, GifsArtConst.INDEX_FROM_GALLERY_TO_GIF);
                                    intent.putExtra(GifsArtConst.INTENT_VIDEO_FRAME_SCALE_SIZE, GifsArtConst.VIDEO_FRAME_SCALE_SIZE);
                                    intent.putStringArrayListExtra(GifsArtConst.INTENT_DECODED_IMAGE_PATHS, galleryAdapter.getSelected());
                                    intent.putExtra(GifsArtConst.INTENT_DECODED_IMAGES_OUTPUT_DIR, GifsArtConst.VIDEOS_DECODED_FRAMES_DIR);
                                    startActivity(intent);

                                    galleryAdapter.deselectAll();
                                    progressDialog.dismiss();
                                }
                            });
                            hasVideo = true;
                        }
                    }
                    if (!hasVideo) {
                        Intent intent = new Intent(MainActivity.this, MakeGifActivity.class);
                        intent.putExtra(GifsArtConst.INTENT_ACTIVITY_INDEX, GifsArtConst.INDEX_FROM_GALLERY_TO_GIF);
                        intent.putStringArrayListExtra(GifsArtConst.INTENT_DECODED_IMAGE_PATHS, galleryAdapter.getSelected());
                        startActivity(intent);

                        galleryAdapter.deselectAll();
                        progressDialog.dismiss();
                    }
                } else {    // if MainActivity is reopened will do this
                    final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                    progressDialog.setMessage(getApplicationContext().getResources().getString(R.string.please_wait));
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    boolean hasVideo = false;
                    ArrayList<String> arrayList = galleryAdapter.getSelected();
                    for (int i = 0; i < arrayList.size(); i++) {
                        if (Utils.getMimeType(arrayList.get(i)) != null && Utils.getMimeType(arrayList.get(i)) == Type.VIDEO) {
                            File file = new File(GifsArtConst.VIDEOS_DECODED_FRAMES_DIR);
                            file.mkdirs();

                            VideoDecoder videoDecoder = new VideoDecoder(MainActivity.this, arrayList.get(i), Integer.MAX_VALUE, GifsArtConst.VIDEO_FRAME_SCALE_SIZE, GifsArtConst.VIDEOS_DECODED_FRAMES_DIR);
                            videoDecoder.extractVideoFrames();
                            videoDecoder.setOnDecodeFinishedListener(new VideoDecoder.OnDecodeFinishedListener() {
                                @Override
                                public void onFinish(boolean isDone) {
                                    Intent intent = new Intent();
                                    intent.putExtra(GifsArtConst.INTENT_ACTIVITY_INDEX, GifsArtConst.INDEX_FROM_GALLERY_TO_GIF);
                                    intent.putExtra(GifsArtConst.INTENT_VIDEO_FRAME_SCALE_SIZE, GifsArtConst.VIDEO_FRAME_SCALE_SIZE);
                                    intent.putStringArrayListExtra(GifsArtConst.INTENT_DECODED_IMAGE_PATHS, galleryAdapter.getSelected());
                                    intent.putExtra(GifsArtConst.INTENT_DECODED_IMAGES_OUTPUT_DIR, GifsArtConst.VIDEOS_DECODED_FRAMES_DIR);
                                    setResult(RESULT_OK, intent);

                                    galleryAdapter.deselectAll();
                                    progressDialog.dismiss();
                                    finish();
                                }
                            });
                            hasVideo = true;
                        }
                    }
                    if (!hasVideo) {
                        Intent intent = new Intent();
                        intent.putExtra(GifsArtConst.INTENT_ACTIVITY_INDEX, GifsArtConst.INDEX_FROM_GALLERY_TO_GIF);
                        intent.putStringArrayListExtra(GifsArtConst.INTENT_DECODED_IMAGE_PATHS, galleryAdapter.getSelected());
                        setResult(RESULT_OK, intent);

                        galleryAdapter.deselectAll();
                        progressDialog.dismiss();
                        finish();

                    }
                }

            } else {
                Toast.makeText(MainActivity.this, getResources().getString(R.string.no_images_selected), Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        return super.

                onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GifsArtConst.REQUEST_CODE_SHOOTING_GIF_REOPENED && resultCode == RESULT_OK) {
            Intent intent = new Intent();
            intent.putExtra(GifsArtConst.INTENT_ACTIVITY_INDEX, data.getIntExtra(GifsArtConst.INTENT_ACTIVITY_INDEX, GifsArtConst.INDEX_SHOOT_GIF));
            intent.putExtra(GifsArtConst.INTENT_VIDEO_FRAME_SCALE_SIZE, GifsArtConst.VIDEO_FRAME_SCALE_SIZE);
            intent.putExtra(GifsArtConst.INTENT_VIDEO_PATH, data.getStringExtra(GifsArtConst.INTENT_VIDEO_PATH));
            intent.putExtra(GifsArtConst.INTENT_FRONT_CAMERA, data.getBooleanExtra(GifsArtConst.INTENT_FRONT_CAMERA, false));
            setResult(RESULT_OK, intent);
            finish();
        }
        if (requestCode == GifsArtConst.REQUEST_CODE_GIPHY_REOPENED && resultCode == RESULT_OK) {
            Intent intent = new Intent();
            intent.putExtra(GifsArtConst.INTENT_ACTIVITY_INDEX, GifsArtConst.INDEX_GIPHY_TO_GIF);
            intent.putExtra(GifsArtConst.INTENT_GIF_PATH, data.getStringExtra(GifsArtConst.INTENT_GIF_PATH));
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    /**
     * Loading all gallery items to recuclerview with AsyncTask
     */
    class InitGalleryItems extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            GalleryItem galleryItem1 = new GalleryItem(BitmapFactory.decodeResource(getResources(), R.drawable.camera_icon));
            galleryItem1.setIsSeleted(false);
            galleryItem1.setType(Type.NONE);
            GalleryItem galleryItem2 = new GalleryItem(BitmapFactory.decodeResource(getResources(), R.drawable.giphy_icon));
            galleryItem2.setIsSeleted(false);
            galleryItem2.setType(Type.NONE);

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        editor.clear();
        editor.commit();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        galleryAdapter.notifyDataSetChanged();
    }

}
