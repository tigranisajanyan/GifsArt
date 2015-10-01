package com.gifsart.studio.activity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
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
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.bumptech.glide.Glide;
import com.decoder.PhotoUtils;
import com.decoder.VideoDecoder;
import com.gifsart.studio.R;
import com.gifsart.studio.adapter.SlideAdapter;
import com.gifsart.studio.gifutils.SaveGIFAsyncTask;
import com.gifsart.studio.helper.SimpleItemTouchHelperCallback;
import com.gifsart.studio.item.GalleryItem;
import com.gifsart.studio.item.MakeGifItem;
import com.gifsart.studio.utils.GifImitation;
import com.gifsart.studio.utils.GifsArtConst;
import com.gifsart.studio.utils.SpacesItemDecoration;
import com.gifsart.studio.utils.Type;
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

    private RecyclerView framesRecyclerView;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private RecyclerView.ItemAnimator itemAnimator;
    private SlideAdapter slideAdapter;

    private ArrayList<GalleryItem> galleryItemArrayList = new ArrayList<>();
    private ArrayList<String> selectedItemsArrayList = new ArrayList<>();
    private ArrayList<MakeGifItem> makeGifItems = new ArrayList<>();

    private int speed = 500;
    private String videoPath;
    private GifImitation gifImitation;

    private ViewGroup container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_gif);

        if (getIntent().getIntExtra(GifsArtConst.INDEX, 0) == GifsArtConst.FROM_GALLERY_TO_GIF_INDEX) {

            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("gagas");
            progressDialog.setCancelable(false);
            selectedItemsArrayList = getIntent().getStringArrayListExtra(GifsArtConst.IMAGE_PATHS);
            for (int i = 0; i < selectedItemsArrayList.size(); i++) {

                if (Utils.getMimeType(selectedItemsArrayList.get(i)).toLowerCase().contains("image") && !Utils.getMimeType(selectedItemsArrayList.get(i)).toLowerCase().contains("gif")) {

                    Bitmap bitmap = ImageLoader.getInstance().loadImageSync(GifsArtConst.FILE_PREFIX + selectedItemsArrayList.get(i));
                    bitmap = Utils.scaleCenterCrop(bitmap, GifsArtConst.FRAME_SIZE, GifsArtConst.FRAME_SIZE);

                    MakeGifItem makeGifItem = new MakeGifItem(200, Type.IMAGE);
                    makeGifItem.setBitmap(bitmap);
                    makeGifItems.add(makeGifItem);

                } else if (Utils.getMimeType(selectedItemsArrayList.get(i)).toLowerCase().contains("gif")) {

                    GifDrawable gifDrawable = null;
                    try {
                        gifDrawable = new GifDrawable(selectedItemsArrayList.get(i));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    MakeGifItem makeGifItem = new MakeGifItem(gifDrawable.getFrameDuration(0), Type.GIF);

                    ArrayList<Bitmap> bitmaps = new ArrayList<>();
                    for (int j = 0; j < gifDrawable.getNumberOfFrames(); j++) {
                        Bitmap bitmap = gifDrawable.seekToFrameAndGet(j);
                        bitmaps.add(bitmap);
                    }
                    makeGifItem.setBitmap(bitmaps.get(0));
                    makeGifItem.setBitmaps(bitmaps);
                    makeGifItems.add(makeGifItem);


                } else if (Utils.getMimeType(selectedItemsArrayList.get(i)).toLowerCase().contains("video")) {

                    VideoDecoder videoDecoder = new VideoDecoder(MakeGifActivity.this, selectedItemsArrayList.get(i), Integer.MAX_VALUE, 2, root + GifsArtConst.SLASH + GifsArtConst.MY_DIR);
                    videoDecoder.extractVideoFrames();
                    final int finalI = i;
                    videoDecoder.setOnDecodeFinishedListener(new VideoDecoder.OnDecodeFinishedListener() {
                        @Override
                        public void onFinish(boolean isDone) {

                            MakeGifItem makeGifItem = new MakeGifItem(40, Type.VIDEO);
                            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                            mmr.setDataSource(selectedItemsArrayList.get(finalI));
                            Bitmap b = mmr.getFrameAtTime(100000, MediaMetadataRetriever.OPTION_CLOSEST); // frame at 100 mls
                            makeGifItem.setBitmap(b);

                            ArrayList<Bitmap> bitmaps = new ArrayList<>();
                            videoPath = selectedItemsArrayList.get(finalI);
                            File file = new File(root, GifsArtConst.MY_DIR);
                            File[] files = file.listFiles();
                            for (int i = 0; i < files.length; i++) {

                                ByteBuffer buffer = PhotoUtils.readBufferFromFile(files[i].getAbsolutePath(), PhotoUtils.checkBufferSize(videoPath, 2));
                                Bitmap bitmap = PhotoUtils.fromBufferToBitmap(PhotoUtils.checkFrameWidth(videoPath, 2), PhotoUtils.checkFrameHeight(videoPath, 2), buffer);
                                bitmaps.add(bitmap);

                            }
                            makeGifItem.setBitmaps(bitmaps);
                            makeGifItems.add(makeGifItem);
                            slideAdapter.notifyDataSetChanged();
                        }
                    });
                }

            }

        }
        if (getIntent().getIntExtra(GifsArtConst.INDEX, 0) == GifsArtConst.GIPHY_TO_GIF_INDEX) {
            GifDrawable gifDrawable = null;
            try {
                gifDrawable = new GifDrawable(getIntent().getStringExtra("gif_path"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            MakeGifItem makeGifItem = new MakeGifItem(100, Type.GIF);
            ArrayList<Bitmap> bitmaps = new ArrayList<>();
            for (int i = 0; i < gifDrawable.getNumberOfFrames(); i++) {
                //GalleryItem galleryItem = new GalleryItem(gifDrawable.seekToFrameAndGet(i), null, true, true, 0, 0, Type.IMAGE);
                //galleryItem.setFrameDuration(100);
                //galleryItemArrayList.add(galleryItem);
                bitmaps.add(gifDrawable.seekToFrameAndGet(i));
            }
            makeGifItem.setBitmap(bitmaps.get(0));
            makeGifItem.setBitmaps(bitmaps);
            makeGifItems.add(makeGifItem);
        }
        if (getIntent().getIntExtra(GifsArtConst.INDEX, 0) == GifsArtConst.SHOOT_GIF_INDEX) {
            int frameSize = getIntent().getIntExtra("frame_size", 2);
            videoPath = getIntent().getStringExtra(GifsArtConst.VIDEO_PATH);
            File file = new File(root, GifsArtConst.MY_DIR);
            File[] files = file.listFiles();
            MakeGifItem makeGifItem = new MakeGifItem(40, Type.VIDEO);
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(videoPath);
            Bitmap b = mmr.getFrameAtTime(100000, MediaMetadataRetriever.OPTION_CLOSEST); // frame at 100 mls
            makeGifItem.setBitmap(b);
            ArrayList<Bitmap> bitmaps = new ArrayList<>();

            for (int i = 0; i < files.length; i++) {

                ByteBuffer buffer = PhotoUtils.readBufferFromFile(files[i].getAbsolutePath(), PhotoUtils.checkBufferSize(videoPath, frameSize));
                Bitmap bitmap = PhotoUtils.fromBufferToBitmap(PhotoUtils.checkFrameWidth(videoPath, frameSize), PhotoUtils.checkFrameHeight(videoPath, frameSize), buffer);

                bitmaps.add(bitmap);
            }
            makeGifItem.setBitmaps(bitmaps);
            makeGifItems.add(makeGifItem);
        }

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
            SaveGIFAsyncTask saveGIFAsyncTask = new SaveGIFAsyncTask(root + GifsArtConst.SLASH + GifsArtConst.GIF_NAME, makeGifItems, MakeGifActivity.this);
            saveGIFAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        return true;
    }

    private void init() {

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().widthPixels);
        container = (ViewGroup) findViewById(R.id.main_view_container);
        container.setLayoutParams(layoutParams);

        imageView = (ImageView) findViewById(R.id.image_view);
        speedSeekBar = (SeekBar) findViewById(R.id.seek_bar);
        framesRecyclerView = (RecyclerView) findViewById(R.id.rec_view);

        imageView.setDrawingCacheEnabled(true);
        slideAdapter = new SlideAdapter(makeGifItems, this);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL);
        itemAnimator = new DefaultItemAnimator();

        framesRecyclerView.setHasFixedSize(true);
        framesRecyclerView.setClipToPadding(true);
        framesRecyclerView.setLayoutManager(staggeredGridLayoutManager);
        framesRecyclerView.setItemAnimator(itemAnimator);
        framesRecyclerView.setAdapter(slideAdapter);
        framesRecyclerView.addItemDecoration(new SpacesItemDecoration(1));
        framesRecyclerView.setItemAnimator(new DefaultItemAnimator());

        ItemTouchHelper.Callback callback =
                new SimpleItemTouchHelperCallback(slideAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(framesRecyclerView);

        gifImitation = new GifImitation(MakeGifActivity.this, imageView, makeGifItems, 300);
        gifImitation.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(getApplicationContext());
        horizontalLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

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
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // gifImitation.cancel(true);
    }

}
