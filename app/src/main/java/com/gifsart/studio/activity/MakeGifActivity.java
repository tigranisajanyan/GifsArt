package com.gifsart.studio.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
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
import android.transition.Transition;
import android.transition.TransitionValues;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.decoder.PhotoUtils;
import com.gifsart.studio.R;
import com.gifsart.studio.adapter.EffectsAdapter;
import com.gifsart.studio.adapter.SlideAdapter;
import com.gifsart.studio.clipart.MainView;
import com.gifsart.studio.effects.GPUEffects;
import com.gifsart.studio.effects.GPUImageFilterTools;
import com.gifsart.studio.gifutils.GifUtils;
import com.gifsart.studio.gifutils.SaveGIFAsyncTask;
import com.gifsart.studio.helper.RecyclerItemClickListener;
import com.gifsart.studio.helper.SimpleItemTouchHelperCallback;
import com.gifsart.studio.item.GifItem;
import com.gifsart.studio.gifutils.GifImitation;
import com.gifsart.studio.utils.GifsArtConst;
import com.gifsart.studio.utils.SpacesItemDecoration;
import com.gifsart.studio.utils.Type;
import com.gifsart.studio.utils.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageView;


public class MakeGifActivity extends ActionBarActivity {

    private static final String root = Environment.getExternalStorageDirectory().toString();

    private GPUImageView imageView;
    private SeekBar speedSeekBar;

    private RecyclerView framesRecyclerView;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private RecyclerView.ItemAnimator itemAnimator;
    private SlideAdapter slideAdapter;

    private ArrayList<String> selectedItemsArrayList = new ArrayList<>();
    private ArrayList<GifItem> gifItems = new ArrayList<>();

    private int speed = 10;
    private String videoPath;
    private GifImitation gifImitation;

    private ViewGroup container;
    private MainView mainView;
    private int square_fit_mode = GifsArtConst.FIT_MODE_ORIGINAL;

    private GPUImageFilter gpuImageFilter = new GPUImageFilter();
    private GPUImageFilterTools.FilterAdjuster mFilterAdjuster;
    private GPUEffects.FilterList filters = new GPUEffects.FilterList();
    String filterName = GPUEffects.FilterType.NONE.name();

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    LinearLayout group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_gif);

        // Checking index, if selected items are from gallery will enter this scope
        if (getIntent().getIntExtra(GifsArtConst.INTENT_ACTIVITY_INDEX, 0) == GifsArtConst.INDEX_FROM_GALLERY_TO_GIF) {

            selectedItemsArrayList = getIntent().getStringArrayListExtra(GifsArtConst.INTENT_DECODED_IMAGE_PATHS);
            for (int i = 0; i < selectedItemsArrayList.size(); i++) {

                if (Utils.getMimeType(selectedItemsArrayList.get(i)) == Type.IMAGE) {

                    Bitmap bitmap = ImageLoader.getInstance().loadImageSync(GifsArtConst.FILE_PREFIX + selectedItemsArrayList.get(i));
                    bitmap = Utils.scaleCenterCrop(bitmap, GifsArtConst.GIF_FRAME_SIZE, GifsArtConst.GIF_FRAME_SIZE);

                    GifItem gifItem = new GifItem(GifsArtConst.IMAGE_FRAME_DURATION, Type.IMAGE);
                    gifItem.setCurrentDuration(GifsArtConst.IMAGE_FRAME_DURATION);
                    gifItem.setBitmap(bitmap);
                    gifItems.add(gifItem);

                } else if (Utils.getMimeType(selectedItemsArrayList.get(i)) == Type.GIF) {

                    GifItem gifItem = new GifItem(GifUtils.getGifFrameDuration(selectedItemsArrayList.get(i)), Type.GIF);
                    gifItem.setCurrentDuration(GifUtils.getGifFrameDuration(selectedItemsArrayList.get(i)));
                    gifItem.setBitmap(GifUtils.getGifFrames(selectedItemsArrayList.get(i)).get(0));
                    gifItem.setBitmaps(GifUtils.getGifFrames(selectedItemsArrayList.get(i)));
                    gifItems.add(gifItem);

                } else if (Utils.getMimeType(selectedItemsArrayList.get(i)) == Type.VIDEO) {


                    ArrayList<Bitmap> bitmaps = new ArrayList<>();
                    videoPath = selectedItemsArrayList.get(i);
                    int scaleSize = getIntent().getIntExtra(GifsArtConst.INTENT_VIDEO_FRAME_SCALE_SIZE, GifsArtConst.VIDEO_FRAME_SCALE_SIZE);
                    File file = new File(GifsArtConst.VIDEOS_DECODED_FRAMES_DIR);
                    File[] files = file.listFiles();
                    for (int j = 0; j < files.length; j++) {

                        if (j % 3 != 0) {
                            ByteBuffer buffer = PhotoUtils.readBufferFromFile(files[j].getAbsolutePath(), PhotoUtils.checkBufferSize(videoPath, scaleSize));
                            Bitmap bitmap = PhotoUtils.fromBufferToBitmap(PhotoUtils.checkFrameWidth(videoPath, scaleSize), PhotoUtils.checkFrameHeight(videoPath, scaleSize), buffer);
                            bitmaps.add(bitmap);
                        }

                    }
                    GifItem gifItem = new GifItem(Utils.checkVideoFrameDuration(videoPath, bitmaps.size()), Type.VIDEO);
                    gifItem.setCurrentDuration(Utils.checkVideoFrameDuration(videoPath, bitmaps.size()));
                    gifItem.setBitmap(Utils.getVideoFirstFrame(videoPath));
                    gifItem.setBitmaps(bitmaps);
                    gifItems.add(gifItem);
                }

            }

        }

        // Checking index, if selected item is from giphy activity will enter this scope
        if (getIntent().getIntExtra(GifsArtConst.INTENT_ACTIVITY_INDEX, 0) == GifsArtConst.INDEX_GIPHY_TO_GIF) {

            GifItem gifItem = new GifItem(GifUtils.getGifFrameDuration(getIntent().getStringExtra(GifsArtConst.INTENT_GIF_PATH)), Type.GIF);
            gifItem.setCurrentDuration(GifUtils.getGifFrameDuration(getIntent().getStringExtra(GifsArtConst.INTENT_GIF_PATH)));
            gifItem.setBitmap(GifUtils.getGifFrames(getIntent().getStringExtra(GifsArtConst.INTENT_GIF_PATH)).get(0));
            gifItem.setBitmaps(GifUtils.getGifFrames(getIntent().getStringExtra(GifsArtConst.INTENT_GIF_PATH)));
            gifItems.add(gifItem);
        }

        // Checking index, if selected item is a video from shooting gif activity will enter this scope
        if (getIntent().getIntExtra(GifsArtConst.INTENT_ACTIVITY_INDEX, 0) == GifsArtConst.INDEX_SHOOT_GIF) {
            int frameSize = getIntent().getIntExtra(GifsArtConst.INTENT_VIDEO_FRAME_SCALE_SIZE, GifsArtConst.VIDEO_FRAME_SCALE_SIZE);
            videoPath = getIntent().getStringExtra(GifsArtConst.INTENT_VIDEO_PATH);
            File file = new File(GifsArtConst.VIDEOS_DECODED_FRAMES_DIR);
            File[] files = file.listFiles();

            ArrayList<Bitmap> bitmaps = new ArrayList<>();

            for (int i = 0; i < files.length; i++) {

                if (i % 3 != 0) {
                    ByteBuffer buffer = PhotoUtils.readBufferFromFile(files[i].getAbsolutePath(), PhotoUtils.checkBufferSize(videoPath, frameSize));
                    Bitmap bitmap = PhotoUtils.fromBufferToBitmap(PhotoUtils.checkFrameWidth(videoPath, frameSize), PhotoUtils.checkFrameHeight(videoPath, frameSize), buffer);

                    bitmaps.add(bitmap);
                }
            }
            GifItem gifItem = new GifItem(Utils.checkVideoFrameDuration(videoPath, bitmaps.size()), Type.VIDEO);
            gifItem.setCurrentDuration(Utils.checkVideoFrameDuration(videoPath, bitmaps.size()));
            gifItem.setBitmap(Utils.getVideoFirstFrame(videoPath));

            gifItem.setBitmaps(bitmaps);
            gifItems.add(gifItem);
        }

        addPlusButton();

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
            gifItems.remove(gifItems.size() - 1);
            gifImitation.cancel(true);
            final SaveGIFAsyncTask saveGIFAsyncTask = new SaveGIFAsyncTask(root + GifsArtConst.SLASH + GifsArtConst.GIF_NAME, gifItems, square_fit_mode, imageView, gpuImageFilter, MakeGifActivity.this);
            saveGIFAsyncTask.setIsDoneListener(new SaveGIFAsyncTask.IsDone() {
                @Override
                public void isDone(boolean done) {

                    if (done) {
                        int num = 0;
                        File file = new File(Environment.getExternalStorageDirectory() + "/" + GifsArtConst.DIR_GPU_IMAGES);
                        File[] files = file.listFiles();
                        for (int k = 0; k < gifItems.size(); k++) {
                            if (gifItems.get(k).getType() == Type.IMAGE) {
                                gifItems.get(k).setBitmap(BitmapFactory.decodeFile(files[num].getAbsolutePath()));
                                num++;
                            } else {
                                for (int j = 0; j < gifItems.get(k).getBitmaps().size(); j++) {
                                    gifItems.get(k).getBitmaps().set(j, BitmapFactory.decodeFile(files[num].getAbsolutePath()));
                                    num++;
                                }
                            }
                        }
                        saveGIFAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }
                }
            });
        }
        return true;
    }

    private void init() {

        group = (LinearLayout) findViewById(R.id.container);
        group.setY(getResources().getDisplayMetrics().heightPixels);

        sharedPreferences = getSharedPreferences(GifsArtConst.SHARED_PREFERENCES, MODE_PRIVATE);
        editor = sharedPreferences.edit();

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().widthPixels);
        container = (ViewGroup) findViewById(R.id.main_view_container);
        container.setLayoutParams(layoutParams);

        imageView = (GPUImageView) findViewById(R.id.image_view);
        imageView.setScaleType(GPUImage.ScaleType.CENTER_INSIDE);
        speedSeekBar = (SeekBar) findViewById(R.id.seek_bar);
        framesRecyclerView = (RecyclerView) findViewById(R.id.rec_view);

        slideAdapter = new SlideAdapter(gifItems, this, getApplicationContext());
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL);
        itemAnimator = new DefaultItemAnimator();

        framesRecyclerView.setHasFixedSize(true);
        framesRecyclerView.setClipToPadding(true);
        framesRecyclerView.setLayoutManager(staggeredGridLayoutManager);
        framesRecyclerView.setItemAnimator(itemAnimator);
        framesRecyclerView.setAdapter(slideAdapter);
        framesRecyclerView.addItemDecoration(new SpacesItemDecoration(2));
        framesRecyclerView.setItemAnimator(new DefaultItemAnimator());

        ItemTouchHelper.Callback callback =
                new SimpleItemTouchHelperCallback(slideAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(framesRecyclerView);

        gifImitation = new GifImitation(MakeGifActivity.this, imageView, gifItems, speed);
        gifImitation.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        speedSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                speed = (20 - progress);
                gifImitation.changeDuration(speed);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        findViewById(R.id.square_fit_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.square_fit_layout, null, false);
                group.removeAllViews();
                group.addView(view);
                group.setY(getResources().getDisplayMetrics().widthPixels);
                initSquareFitLayout();
            }
        });

        findViewById(R.id.add_effects_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*Intent intent = new Intent(MakeGifActivity.this, EffectsActivity.class);
                intent.putExtra(GifsArtConst.INTENT_IMAGE_BITMAP, Utils.bitmapToByteArray(gifItems.get(0).getBitmap()));
                intent.putExtra(GifsArtConst.INTENT_SQUARE_FIT_MODE, square_fit_mode);
                intent.putExtra(GifsArtConst.INTENT_EFFECT_FILTER, filterName);
                startActivityForResult(intent, GifsArtConst.REQUEST_CODE_EFFECTS_ACTIVITY);*/

                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.effects_layout, null, false);
                group.removeAllViews();
                group.addView(view);
                group.setY(getResources().getDisplayMetrics().widthPixels);
                initEffectsLayout();
            }
        });

        findViewById(R.id.add_clipart_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*Intent intent = new Intent(MakeGifActivity.this, ClipArtActivity.class);
                intent.putExtra(GifsArtConst.INTENT_IMAGE_BITMAP, Utils.bitmapToByteArray(gifItems.get(0).getBitmap()));
                intent.putExtra(GifsArtConst.INTENT_SQUARE_FIT_MODE, square_fit_mode);
                intent.putExtra(GifsArtConst.INTENT_EFFECT_FILTER, filterName);
                startActivityForResult(intent, GifsArtConst.REQUEST_CODE_EFFECTS_ACTIVITY);*/
            }
        });

        final ImageButton button = (ImageButton) findViewById(R.id.play_button);
        button.setSelected(true);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (button.isSelected()) {
                    gifImitation.onPause();
                    button.setSelected(false);
                } else {
                    gifImitation.onResume();
                    button.setSelected(true);
                }
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GifsArtConst.REQUEST_CODE_EDIT_FRAME_ACTIVITY && resultCode == RESULT_OK) {

        }
        if (requestCode == GifsArtConst.REQUEST_CODE_EFFECTS_ACTIVITY && resultCode == RESULT_OK) {
            filterName = data.getStringExtra(GifsArtConst.INTENT_EFFECT_FILTER);
            gpuImageFilter = GPUEffects.createFilterForType(GPUEffects.FilterType.valueOf(filterName));
            imageView.setFilter(gpuImageFilter);
        }
        if (requestCode == GifsArtConst.REQUEST_CODE_MAIN_ACTIVITY && resultCode == RESULT_OK) {

            gifItems.remove(gifItems.size() - 1);

            if (data.getIntExtra(GifsArtConst.INTENT_ACTIVITY_INDEX, 0) == GifsArtConst.INDEX_FROM_GALLERY_TO_GIF) {
                ArrayList<String> addedItemsArray = data.getStringArrayListExtra(GifsArtConst.INTENT_DECODED_IMAGE_PATHS);
                for (int i = 0; i < addedItemsArray.size(); i++) {

                    if (Utils.getMimeType(addedItemsArray.get(i)) == Type.IMAGE) {

                        Bitmap bitmap = ImageLoader.getInstance().loadImageSync(GifsArtConst.FILE_PREFIX + addedItemsArray.get(i));
                        bitmap = Utils.scaleCenterCrop(bitmap, GifsArtConst.GIF_FRAME_SIZE, GifsArtConst.GIF_FRAME_SIZE);

                        GifItem gifItem = new GifItem(GifsArtConst.IMAGE_FRAME_DURATION, Type.IMAGE);
                        gifItem.setCurrentDuration(GifsArtConst.IMAGE_FRAME_DURATION);
                        gifItem.setBitmap(bitmap);
                        gifItems.add(gifItem);

                    } else if (Utils.getMimeType(addedItemsArray.get(i)) == Type.GIF) {

                        GifItem gifItem = new GifItem(GifUtils.getGifFrameDuration(addedItemsArray.get(i)), Type.GIF);
                        gifItem.setCurrentDuration(GifUtils.getGifFrameDuration(addedItemsArray.get(i)));
                        gifItem.setBitmap(GifUtils.getGifFrames(addedItemsArray.get(i)).get(0));
                        gifItem.setBitmaps(GifUtils.getGifFrames(addedItemsArray.get(i)));
                        gifItems.add(gifItem);

                    } else if (Utils.getMimeType(addedItemsArray.get(i)) == Type.VIDEO) {

                        ArrayList<Bitmap> bitmaps = new ArrayList<>();
                        videoPath = addedItemsArray.get(i);
                        int scaleSize = data.getIntExtra(GifsArtConst.INTENT_VIDEO_FRAME_SCALE_SIZE, GifsArtConst.VIDEO_FRAME_SCALE_SIZE);
                        File file = new File(GifsArtConst.VIDEOS_DECODED_FRAMES_DIR);
                        File[] files = file.listFiles();
                        for (int j = 0; j < files.length; j++) {

                            if (j % 3 != 0) {
                                ByteBuffer buffer = PhotoUtils.readBufferFromFile(files[j].getAbsolutePath(), PhotoUtils.checkBufferSize(videoPath, scaleSize));
                                Bitmap bitmap = PhotoUtils.fromBufferToBitmap(PhotoUtils.checkFrameWidth(videoPath, scaleSize), PhotoUtils.checkFrameHeight(videoPath, scaleSize), buffer);
                                bitmaps.add(bitmap);
                            }

                        }
                        GifItem gifItem = new GifItem(Utils.checkVideoFrameDuration(videoPath, bitmaps.size()), Type.VIDEO);
                        gifItem.setCurrentDuration(Utils.checkVideoFrameDuration(videoPath, bitmaps.size()));
                        gifItem.setBitmap(Utils.getVideoFirstFrame(videoPath));
                        gifItem.setBitmaps(bitmaps);
                        gifItems.add(gifItem);
                    }
                    selectedItemsArrayList.add(addedItemsArray.get(i));
                }
            }
            if (data.getIntExtra(GifsArtConst.INTENT_ACTIVITY_INDEX, 0) == GifsArtConst.INDEX_GIPHY_TO_GIF) {

                GifItem gifItem = new GifItem(GifUtils.getGifFrameDuration(data.getStringExtra(GifsArtConst.INTENT_GIF_PATH)), Type.GIF);
                gifItem.setCurrentDuration(GifUtils.getGifFrameDuration(data.getStringExtra(GifsArtConst.INTENT_GIF_PATH)));
                gifItem.setBitmap(GifUtils.getGifFrames(data.getStringExtra(GifsArtConst.INTENT_GIF_PATH)).get(0));
                gifItem.setBitmaps(GifUtils.getGifFrames(data.getStringExtra(GifsArtConst.INTENT_GIF_PATH)));
                gifItems.add(gifItem);
            }
            if (data.getIntExtra(GifsArtConst.INTENT_ACTIVITY_INDEX, 0) == GifsArtConst.INDEX_SHOOT_GIF) {
                int frameSize = data.getIntExtra(GifsArtConst.INTENT_VIDEO_FRAME_SCALE_SIZE, GifsArtConst.VIDEO_FRAME_SCALE_SIZE);
                videoPath = data.getStringExtra(GifsArtConst.INTENT_VIDEO_PATH);
                File file = new File(GifsArtConst.VIDEOS_DECODED_FRAMES_DIR);
                File[] files = file.listFiles();

                ArrayList<Bitmap> bitmaps = new ArrayList<>();

                for (int i = 0; i < files.length; i++) {

                    if (i % 3 != 0) {
                        ByteBuffer buffer = PhotoUtils.readBufferFromFile(files[i].getAbsolutePath(), PhotoUtils.checkBufferSize(videoPath, frameSize));
                        Bitmap bitmap = PhotoUtils.fromBufferToBitmap(PhotoUtils.checkFrameWidth(videoPath, frameSize), PhotoUtils.checkFrameHeight(videoPath, frameSize), buffer);

                        bitmaps.add(bitmap);
                    }
                }
                GifItem gifItem = new GifItem(Utils.checkVideoFrameDuration(videoPath, bitmaps.size()), Type.VIDEO);
                gifItem.setCurrentDuration(Utils.checkVideoFrameDuration(videoPath, bitmaps.size()));
                gifItem.setBitmap(Utils.getVideoFirstFrame(videoPath));

                gifItem.setBitmaps(bitmaps);
                gifItems.add(gifItem);
            }
            addPlusButton();
            slideAdapter.notifyDataSetChanged();

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gifImitation.cancel(true);
        editor.clear();
        editor.commit();
    }

    public void initMainView() {
        Bitmap bm = Bitmap.createBitmap(GifsArtConst.GIF_FRAME_SIZE, GifsArtConst.GIF_FRAME_SIZE, Bitmap.Config.ARGB_8888);
        bm.eraseColor(Color.TRANSPARENT);
        Bitmap mutableBitmap = bm.copy(Bitmap.Config.ARGB_8888, true);
        mainView = new MainView(this, mutableBitmap);
        mainView.setId(R.id.mainViewId);
    }

    public void saveClipart() {
        if (mainView.getClipartItem() != null) {

            Bitmap resultBitmap = mainView.getOrigBitmap();
            Canvas canvas = new Canvas(resultBitmap);
            Paint paint = new Paint();

            Matrix transformMatrix = new Matrix();
            transformMatrix.postRotate(mainView.getClipartItem().getRotation(), mainView.getClipartItem().getBitmap().getWidth() / 2, mainView.getClipartItem().getBitmap().getHeight() / 2);
            transformMatrix.postTranslate(mainView.getClipartItem().getX() * Math.min(resultBitmap.getWidth(), resultBitmap.getHeight()) / Math.max(resultBitmap.getWidth(), resultBitmap.getHeight()), mainView.getClipartItem().getY());
            transformMatrix.postScale(mainView.getClipartItem().getScaleX(), mainView.getClipartItem().getScaleY());
            canvas.drawBitmap(resultBitmap, 0, 0, paint);
            canvas.scale((float) Math.max(resultBitmap.getWidth(), resultBitmap.getHeight()) / mainView.getWidth(), (float) Math.max(resultBitmap.getWidth(), resultBitmap.getHeight()) / mainView.getWidth(), 0, 0);
            canvas.drawBitmap(mainView.getClipartItem().getBitmap(), transformMatrix, paint);
            //mainView.removeClipArt();
        }
    }

    //  This method is adding framesRecyclerView last item(button),
    //  wich type is none and it's for adding new gifitems from gallery, shooting gif and/or giphy activities
    public void addPlusButton() {
        GifItem gifItem = new GifItem();
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.add_icon);
        gifItem.setBitmap(bitmap);
        gifItems.add(gifItem);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void initSquareFitLayout() {

        group.findViewById(R.id.original_fit_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setScaleType(GPUImage.ScaleType.CENTER_INSIDE);
                square_fit_mode = GifsArtConst.FIT_MODE_ORIGINAL;
            }
        });

        group.findViewById(R.id.square_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setScaleType(GPUImage.ScaleType.CENTER_CROP);
                square_fit_mode = GifsArtConst.FIT_MODE_SQUARE;
                //group.removeAllViews();
                //group.setY(getResources().getDisplayMetrics().heightPixels);
            }
        });

        group.findViewById(R.id.square_fit_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setScaleType(GPUImage.ScaleType.CENTER_INSIDE);
                square_fit_mode = GifsArtConst.FIT_MODE_SQUARE_FIT;
            }
        });
    }

    public void initEffectsLayout() {

        mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(gpuImageFilter);
        filters.addFilter("None", GPUEffects.FilterType.NONE);
        filters.addFilter("Contrast", GPUEffects.FilterType.CONTRAST);
        filters.addFilter("Invert", GPUEffects.FilterType.INVERT);
        filters.addFilter("Hue", GPUEffects.FilterType.HUE);
        filters.addFilter("Gamma", GPUEffects.FilterType.GAMMA);
        filters.addFilter("Brightness", GPUEffects.FilterType.BRIGHTNESS);
        filters.addFilter("Sepia", GPUEffects.FilterType.SEPIA);
        filters.addFilter("Grayscale", GPUEffects.FilterType.GRAYSCALE);
        filters.addFilter("Sharpness", GPUEffects.FilterType.SHARPEN);
        filters.addFilter("Emboss", GPUEffects.FilterType.EMBOSS);
        filters.addFilter("Posterize", GPUEffects.FilterType.POSTERIZE);

        RecyclerView recyclerView = (RecyclerView) group.findViewById(R.id.effects_rec_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        itemAnimator = new DefaultItemAnimator();

        recyclerView.setHasFixedSize(true);
        recyclerView.setClipToPadding(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(itemAnimator);
        recyclerView.addItemDecoration(new SpacesItemDecoration((int) Utils.dpToPixel(2, this)));

        EffectsAdapter effectsAdapter = new EffectsAdapter(filters, this);
        effectsAdapter.addAll(apply(filters));

        recyclerView.setAdapter(effectsAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                filterName = filters.filters.get(position).name();
                switchFilterTo(GPUEffects.createFilterForType(filters.filters.get(position)));

            }
        }));

        group.findViewById(R.id.opacity_seek_bar).setVisibility(
                mFilterAdjuster.canAdjust() ? View.VISIBLE : View.INVISIBLE);
        ((SeekBar) group.findViewById(R.id.opacity_seek_bar)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mFilterAdjuster != null) {
                    mFilterAdjuster.adjust(progress);
                }
                imageView.requestRender();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void switchFilterTo(final GPUImageFilter filter) {
        if (gpuImageFilter == null
                || (filter != null && !gpuImageFilter.getClass().equals(filter.getClass()))) {
            gpuImageFilter = filter;
            imageView.setFilter(gpuImageFilter);
            mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(gpuImageFilter);

            findViewById(R.id.opacity_seek_bar).setVisibility(
                    mFilterAdjuster.canAdjust() ? View.VISIBLE : View.INVISIBLE);
        }
    }

    public ArrayList<Bitmap> apply(final GPUEffects.FilterList filters) {

        ArrayList<Bitmap> bitmaps = new ArrayList<>();
        GPUImage gpuImage = new GPUImage(this);
        gpuImage.setImage(gifItems.get(0).getBitmap());
        for (int i = 0; i < filters.filters.size(); i++) {
            gpuImage.setFilter(GPUEffects.createFilterForType(filters.filters.get(i)));
            bitmaps.add(gpuImage.getBitmapWithFilterApplied());
        }
        return bitmaps;
    }

}
