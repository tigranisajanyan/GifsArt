package com.gifsart.studio.activity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.decoder.PhotoUtils;
import com.decoder.VideoDecoder;
import com.gifsart.studio.R;
import com.gifsart.studio.adapter.Adapter;
import com.gifsart.studio.gifutils.SaveGIFAsyncTask;
import com.gifsart.studio.helper.SimpleItemTouchHelperCallback;
import com.gifsart.studio.item.GalleryItem;
import com.gifsart.studio.utils.GifImitation;
import com.gifsart.studio.utils.GifsArtConst;
import com.gifsart.studio.utils.SpacesItemDecoration;
import com.gifsart.studio.utils.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import pl.droidsonroids.gif.GifDrawable;


public class MakeGifActivity extends ActionBarActivity {

    private static final String root = Environment.getExternalStorageDirectory().toString();

    private ImageView imageView;
    private SeekBar speedSeekBar;
    private LinearLayout clipartLayout;

    private RecyclerView framesRecyclerView;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private RecyclerView.ItemAnimator itemAnimator;
    private Adapter adapter;

    private ArrayList<GalleryItem> galleryItemArrayList = new ArrayList<>();
    private ArrayList<String> selectedItemsArrayList = new ArrayList<>();

    private int speed = 500;
    private String videoPath;
    private GifImitation gifImitation;

    private ViewGroup container;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_gif);

        if (getIntent().getIntExtra(GifsArtConst.INDEX, 0) == GifsArtConst.IMAGES_TO_GIF_INDEX) {

            selectedItemsArrayList = getIntent().getStringArrayListExtra(GifsArtConst.IMAGE_PATHS);
            for (int i = 0; i < selectedItemsArrayList.size(); i++) {

                if (Utils.getMimeType(selectedItemsArrayList.get(i)).toLowerCase().contains("image") && !Utils.getMimeType(selectedItemsArrayList.get(i)).toLowerCase().contains("gif")) {
                    Bitmap bitmap = ImageLoader.getInstance().loadImageSync(GifsArtConst.FILE_PREFIX + selectedItemsArrayList.get(i));
                    bitmap = Utils.scaleCenterCrop(bitmap, GifsArtConst.FRAME_SIZE, GifsArtConst.FRAME_SIZE);
                    GalleryItem galleryItem = new GalleryItem(bitmap, selectedItemsArrayList.get(i), true, true, bitmap.getWidth(), bitmap.getHeight(), GalleryItem.Type.IMAGE);
                    galleryItem.setFrameDuration(200);
                    galleryItemArrayList.add(galleryItem);
                } else if (Utils.getMimeType(selectedItemsArrayList.get(i)).toLowerCase().contains("gif")) {
                    GifDrawable gifDrawable = null;
                    try {
                        gifDrawable = new GifDrawable(selectedItemsArrayList.get(i));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    for (int j = 0; j < gifDrawable.getNumberOfFrames(); j++) {
                        Bitmap bitmap = gifDrawable.seekToFrameAndGet(j);
                        GalleryItem galleryItem = new GalleryItem(Utils.squareFit(bitmap, GifsArtConst.FRAME_SIZE), null, true, true, 0, 0, GalleryItem.Type.GIF);
                        galleryItem.setFrameDuration(gifDrawable.getFrameDuration(0));
                        galleryItemArrayList.add(galleryItem);
                    }
                } else if (Utils.getMimeType(selectedItemsArrayList.get(i)).toLowerCase().contains("video")) {
                    VideoDecoder videoDecoder = new VideoDecoder(MakeGifActivity.this, selectedItemsArrayList.get(i), Integer.MAX_VALUE, 2, root + GifsArtConst.SLASH + GifsArtConst.MY_DIR);
                    videoDecoder.extractVideoFrames();
                    final int finalI = i;
                    videoDecoder.setOnDecodeFinishedListener(new VideoDecoder.OnDecodeFinishedListener() {
                        @Override
                        public void onFinish(boolean isDone) {
                            videoPath = selectedItemsArrayList.get(finalI);
                            File file = new File(root, GifsArtConst.MY_DIR);
                            File[] files = file.listFiles();
                            for (int i = 0; i < files.length; i++) {

                                ByteBuffer buffer = PhotoUtils.readBufferFromFile(files[i].getAbsolutePath(), PhotoUtils.checkBufferSize(videoPath, 2));
                                Bitmap bitmap = PhotoUtils.fromBufferToBitmap(PhotoUtils.checkFrameWidth(videoPath, 2), PhotoUtils.checkFrameHeight(videoPath, 2), buffer);

                                GalleryItem galleryItem = new GalleryItem(Utils.squareFit(bitmap, GifsArtConst.FRAME_SIZE), null, true, true, bitmap.getWidth(), bitmap.getHeight(), GalleryItem.Type.VIDEO);
                                galleryItem.setFrameDuration(40);
                                galleryItemArrayList.add(galleryItem);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    });
                }

            }


        }
        if (getIntent().getIntExtra(GifsArtConst.INDEX, 0) == 2) {
            ArrayList<Bitmap> bitmaps = new ArrayList<>();
            GifDrawable gifDrawable = null;
            try {
                gifDrawable = new GifDrawable(getIntent().getStringExtra("gif_path"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < gifDrawable.getNumberOfFrames(); i++) {
                bitmaps.add(gifDrawable.seekToFrameAndGet(i));
                GalleryItem galleryItem = new GalleryItem(bitmaps.get(i), null, true, true, 0, 0, GalleryItem.Type.IMAGE);
                galleryItem.setFrameDuration(100);
                galleryItemArrayList.add(galleryItem);
            }
        }
        if (getIntent().getIntExtra(GifsArtConst.INDEX, 0) == 3) {
            int frameSize = getIntent().getIntExtra("frame_size", 2);
            videoPath = getIntent().getStringExtra(GifsArtConst.VIDEO_PATH);
            File file = new File(root, GifsArtConst.MY_DIR);
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {

                ByteBuffer buffer = PhotoUtils.readBufferFromFile(files[i].getAbsolutePath(), PhotoUtils.checkBufferSize(videoPath, frameSize));
                Bitmap bitmap = PhotoUtils.fromBufferToBitmap(PhotoUtils.checkFrameWidth(videoPath, frameSize), PhotoUtils.checkFrameHeight(videoPath, frameSize), buffer);

                GalleryItem galleryItem = new GalleryItem(bitmap, null, true, true, bitmap.getWidth(), bitmap.getHeight(), GalleryItem.Type.IMAGE);
                galleryItem.setFrameDuration(40);
                galleryItemArrayList.add(galleryItem);
            }
        }

        //GalleryItem item = new GalleryItem();
        //item.setBitmap(BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_menu_add));
        //galleryItemArrayList.add(item);
        init();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_next) {
            SaveGIFAsyncTask saveGIFAsyncTask = new SaveGIFAsyncTask(root + GifsArtConst.SLASH + GifsArtConst.GIF_NAME, galleryItemArrayList, MakeGifActivity.this);
            saveGIFAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        return true;
    }

    private void init() {

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().widthPixels);
        container = (ViewGroup) findViewById(R.id.main_view_container);
        container.setLayoutParams(layoutParams);

        imageView = (ImageView) findViewById(R.id.image_view);
        speedSeekBar = (SeekBar) findViewById(R.id.seek_bar);
        framesRecyclerView = (RecyclerView) findViewById(R.id.rec_view);

        imageView.setDrawingCacheEnabled(true);
        adapter = new Adapter(galleryItemArrayList, selectedItemsArrayList, this);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL);
        itemAnimator = new DefaultItemAnimator();

        framesRecyclerView.setHasFixedSize(true);
        framesRecyclerView.setClipToPadding(true);
        framesRecyclerView.setLayoutManager(staggeredGridLayoutManager);
        framesRecyclerView.setItemAnimator(itemAnimator);
        framesRecyclerView.setAdapter(adapter);
        framesRecyclerView.addItemDecoration(new SpacesItemDecoration(1));
        framesRecyclerView.setItemAnimator(new DefaultItemAnimator());

        ItemTouchHelper.Callback callback =
                new SimpleItemTouchHelperCallback(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(framesRecyclerView);

        gifImitation = new GifImitation(MakeGifActivity.this, imageView, galleryItemArrayList, 500);
        gifImitation.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(getApplicationContext());
        horizontalLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        clipartLayout = (LinearLayout) findViewById(R.id.clipart_horizontal_list_container);
        clipartLayout.setVisibility(View.VISIBLE);
        clipartLayout.animate().translationY(200);


        speedSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                speed = 100 + (progress * 50);
                gifImitation.changeDuration(speed);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        findViewById(R.id.add_clipart_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.setLooping(true);
                    }
                });*/
            }
        });

        findViewById(R.id.add_effect_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        findViewById(R.id.add_gif_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        gifImitation.cancel(true);
    }

}
