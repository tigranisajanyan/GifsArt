package com.gifsart.studio.activity;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.VideoView;

import com.decoder.PhotoUtils;
import com.decoder.VideoDecoder;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.gifsart.studio.R;
import com.gifsart.studio.adapter.Adapter;
import com.gifsart.studio.adapter.GiphyAdapter;
import com.gifsart.studio.gifutils.SaveGIFAsyncTask;
import com.gifsart.studio.helper.RecyclerItemClickListener;
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
import pl.droidsonroids.gif.GifImageView;


public class MakeGifActivity extends ActionBarActivity {

    private static final String root = Environment.getExternalStorageDirectory().toString();


    private ImageView imageView;
    private VideoView videoView;
    private GifImageView gifImageView;
    private SeekBar speedSeekBar;
    private LinearLayout clipartLayout;

    private RecyclerView recyclerView;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private RecyclerView.ItemAnimator itemAnimator;
    private Adapter adapter;

    private ArrayList<GalleryItem> galleryItemArrayList = new ArrayList<>();
    private ArrayList<String> giphyItemUrls = new ArrayList<>();

    private int speed = 500;
    private String videoPath;
    private boolean isHide = true;
    private GifImitation gifImitation;
    private RecyclerView actionsRecyclerView = null;
    private GiphyAdapter giphyAdapter = null;

    private ViewGroup container;

    public static final int ACTION_ADD_EFFECT = 10;
    public static final int ACTION_ADD_CLIPART = 11;
    public static final int ACTION_ADD_GIF = 12;

    public int selectedAction = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_gif);

        if (getIntent().getIntExtra(GifsArtConst.INDEX, 0) == GifsArtConst.IMAGES_TO_GIF_INDEX) {

            final ArrayList<String> arrayList = getIntent().getStringArrayListExtra(GifsArtConst.IMAGE_PATHS);
            for (int i = 0; i < arrayList.size(); i++) {

                if (Utils.getMimeType(arrayList.get(i)).toLowerCase().contains("image") && !Utils.getMimeType(arrayList.get(i)).toLowerCase().contains("gif")) {
                    Bitmap bitmap = ImageLoader.getInstance().loadImageSync(GifsArtConst.FILE_PREFIX + arrayList.get(i));
                    bitmap = Utils.scaleCenterCrop(bitmap, GifsArtConst.FRAME_SIZE, GifsArtConst.FRAME_SIZE);
                    GalleryItem galleryItem = new GalleryItem(bitmap, arrayList.get(i), true, true, bitmap.getWidth(), bitmap.getHeight(), GalleryItem.Type.IMAGE);
                    galleryItemArrayList.add(galleryItem);
                } else if (Utils.getMimeType(arrayList.get(i)).toLowerCase().contains("gif")) {
                    GalleryItem galleryItem = new GalleryItem(null, arrayList.get(i), true, true, 0, 0, GalleryItem.Type.GIF);
                    galleryItemArrayList.add(galleryItem);
                } else if (Utils.getMimeType(arrayList.get(i)).toLowerCase().contains("video")) {
                    GalleryItem galleryItem = new GalleryItem(null, arrayList.get(i), true, true, 0, 0, GalleryItem.Type.VIDEO);
                    galleryItemArrayList.add(galleryItem);
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
                galleryItemArrayList.add(galleryItem);
            }
            /*ArrayList<Bitmap> bitmaps = Utils.getGifFramesPath(getIntent().getStringExtra("gif_path"));
            for (int i = 0; i < bitmaps.size(); i++) {
                GalleryItem galleryItem = new GalleryItem(bitmaps.get(i), null, true, true, 0, 0, GalleryItem.Type.IMAGE);
                galleryItemArrayList.add(galleryItem);
            }*/
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
            SaveGIFAsyncTask saveGIFAsyncTask = new SaveGIFAsyncTask(root + GifsArtConst.SLASH + GifsArtConst.MY_DIR + GifsArtConst.SLASH + GifsArtConst.GIF_NAME, galleryItemArrayList, speed, MakeGifActivity.this);
            saveGIFAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else if (id == R.id.action_next) {
            //DrawClipArtOnMainFrames drawClipArtOnMainFrames = new DrawClipArtOnMainFrames(MakeGifActivity.this, adapter, mainView, galleryItemArrayList, container, clipartLayout, isHide);
            //drawClipArtOnMainFrames.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        }
        return true;
    }

    private void init() {

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().widthPixels);
        container = (ViewGroup) findViewById(R.id.main_view_container);
        container.setLayoutParams(layoutParams);

        imageView = (ImageView) findViewById(R.id.image_view);
        videoView = (VideoView) findViewById(R.id.video_view);
        gifImageView = (GifImageView) findViewById(R.id.gif_image_view);
        speedSeekBar = (SeekBar) findViewById(R.id.seek_bar);
        recyclerView = (RecyclerView) findViewById(R.id.rec_view);

        imageView.setDrawingCacheEnabled(true);
        adapter = new Adapter(galleryItemArrayList, this);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL);
        itemAnimator = new DefaultItemAnimator();

        recyclerView.setHasFixedSize(true);
        recyclerView.setClipToPadding(true);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerView.setItemAnimator(itemAnimator);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new SpacesItemDecoration(1));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        ItemTouchHelper.Callback callback =
                new SimpleItemTouchHelperCallback(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);

        gifImitation = new GifImitation(MakeGifActivity.this, container, galleryItemArrayList, 500);
        gifImitation.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(getApplicationContext());
        horizontalLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        actionsRecyclerView = (RecyclerView) findViewById(R.id.actions_recycler_view);
        actionsRecyclerView.setLayoutManager(horizontalLayoutManager);
        actionsRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                switch (selectedAction) {
                    case ACTION_ADD_CLIPART:
                        /*if (mainView != null) {
                            mainView.addClipart(clipartList[position]);
                        }*/
                        break;
                    case ACTION_ADD_EFFECT:
                        //EffectItem selectedItem = effectsAdapter.getItem(position);
                        //selectedItem.getAction().startAction(galleryItemArrayList, adapter);
                        break;
                    case ACTION_ADD_GIF:
                        /*Uri uri = Uri.parse(giphyItemUrls.get(position));
                        DraweeController controller = Fresco.newDraweeControllerBuilder()
                                .setUri(uri)
                                .setAutoPlayAnimations(true).build();

                        gifImageView.setController(controller);*/
                        break;

                }
            }
        }));

        giphyAdapter = new GiphyAdapter(this);

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
