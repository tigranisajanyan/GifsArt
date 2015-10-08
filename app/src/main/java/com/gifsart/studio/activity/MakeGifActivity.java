package com.gifsart.studio.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.decoder.PhotoUtils;
import com.gifsart.studio.R;
import com.gifsart.studio.adapter.SlideAdapter;
import com.gifsart.studio.clipart.MainView;
import com.gifsart.studio.effects.GPUEffects;
import com.gifsart.studio.gifutils.GifUtils;
import com.gifsart.studio.gifutils.SaveGIFAsyncTask;
import com.gifsart.studio.helper.SimpleItemTouchHelperCallback;
import com.gifsart.studio.item.GifItem;
import com.gifsart.studio.utils.GifImitation;
import com.gifsart.studio.utils.GifsArtConst;
import com.gifsart.studio.utils.SpacesItemDecoration;
import com.gifsart.studio.utils.Type;
import com.gifsart.studio.utils.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageHueFilter;
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
    private int scale_type = GifsArtConst.MODE_ORIGINAL;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_gif);

        if (getIntent().getIntExtra(GifsArtConst.INDEX, 0) == GifsArtConst.FROM_GALLERY_TO_GIF_INDEX) {

            selectedItemsArrayList = getIntent().getStringArrayListExtra(GifsArtConst.IMAGE_PATHS);
            for (int i = 0; i < selectedItemsArrayList.size(); i++) {

                if (Utils.getMimeType(selectedItemsArrayList.get(i)) == Type.IMAGE) {

                    Bitmap bitmap = ImageLoader.getInstance().loadImageSync(GifsArtConst.FILE_PREFIX + selectedItemsArrayList.get(i));
                    bitmap = Utils.scaleCenterCrop(bitmap, GifsArtConst.FRAME_SIZE, GifsArtConst.FRAME_SIZE);

                    GifItem gifItem = new GifItem(GifsArtConst.IMAGE_FRAME_DURATION, Type.IMAGE);
                    gifItem.setBitmap(bitmap);
                    gifItems.add(gifItem);

                } else if (Utils.getMimeType(selectedItemsArrayList.get(i)) == Type.GIF) {

                    GifItem gifItem = new GifItem(GifUtils.getGifFrameDuration(selectedItemsArrayList.get(i)), Type.GIF);
                    gifItem.setBitmap(GifUtils.getGifFrames(selectedItemsArrayList.get(i)).get(0));
                    gifItem.setBitmaps(GifUtils.getGifFrames(selectedItemsArrayList.get(i)));
                    gifItems.add(gifItem);

                } else if (Utils.getMimeType(selectedItemsArrayList.get(i)) == Type.VIDEO) {


                    ArrayList<Bitmap> bitmaps = new ArrayList<>();
                    videoPath = selectedItemsArrayList.get(i);
                    File file = new File(Environment.getExternalStorageDirectory() + "/GifsArt/vid");
                    File[] files = file.listFiles();
                    for (int j = 0; j < files.length; j++) {

                        if (j % 3 != 0) {
                            ByteBuffer buffer = PhotoUtils.readBufferFromFile(files[j].getAbsolutePath(), PhotoUtils.checkBufferSize(videoPath, 2));
                            Bitmap bitmap = PhotoUtils.fromBufferToBitmap(PhotoUtils.checkFrameWidth(videoPath, 2), PhotoUtils.checkFrameHeight(videoPath, 2), buffer);
                            bitmaps.add(bitmap);
                        }

                    }
                    GifItem gifItem = new GifItem(Utils.checkVideoFrameDuration(videoPath, bitmaps.size()), Type.VIDEO);
                    gifItem.setBitmap(Utils.getVideoFirstFrame(videoPath));
                    gifItem.setBitmaps(bitmaps);
                    gifItems.add(gifItem);
                }

            }

        }
        if (getIntent().getIntExtra(GifsArtConst.INDEX, 0) == GifsArtConst.GIPHY_TO_GIF_INDEX) {

            GifItem gifItem = new GifItem(GifUtils.getGifFrameDuration(getIntent().getStringExtra("gif_path")), Type.GIF);
            gifItem.setBitmap(GifUtils.getGifFrames(getIntent().getStringExtra("gif_path")).get(0));
            gifItem.setBitmaps(GifUtils.getGifFrames(getIntent().getStringExtra("gif_path")));
            gifItems.add(gifItem);
        }
        if (getIntent().getIntExtra(GifsArtConst.INDEX, 0) == GifsArtConst.SHOOT_GIF_INDEX) {
            int frameSize = getIntent().getIntExtra("frame_size", 2);
            videoPath = getIntent().getStringExtra(GifsArtConst.VIDEO_PATH);
            File file = new File(root, GifsArtConst.MY_DIR);
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
            SaveGIFAsyncTask saveGIFAsyncTask = new SaveGIFAsyncTask(root + GifsArtConst.SLASH + GifsArtConst.GIF_NAME, gifItems, scale_type, MakeGifActivity.this);
            saveGIFAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        return true;
    }

    private void init() {

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
        framesRecyclerView.addItemDecoration(new SpacesItemDecoration(1));
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
                speed = 5 + progress;
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

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                Bitmap bitmap = gifItems.get(0).getBitmap();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                Intent intent = new Intent(MakeGifActivity.this, SquareFitActivity.class);
                intent.putExtra("image", byteArray);
                intent.putExtra("square_mode", scale_type);
                startActivityForResult(intent, GifsArtConst.SQUARE_FIT_ACTIVITY_REQUEST_CODE);
            }
        });

        findViewById(R.id.add_effects_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                Bitmap bitmap = gifItems.get(0).getBitmap();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                Intent intent = new Intent(MakeGifActivity.this, EffectsActivity.class);
                intent.putExtra("image", byteArray);
                //intent.putExtra("path", selectedItemsArrayList.get(0));
                startActivityForResult(intent, GifsArtConst.EFFECTS_ACTIVITY_REQUEST_CODE);
            }
        });

        findViewById(R.id.add_clipart_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initMainView();
                if (mainView != null) {
                    mainView.addClipart(R.drawable.clipart_1);
                }
                container.addView(mainView);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GifsArtConst.SQUARE_FIT_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            switch (data.getIntExtra("square_mode", 1)) {
                case 1:
                    scale_type = GifsArtConst.MODE_ORIGINAL;
                    imageView.setScaleType(GPUImage.ScaleType.CENTER_INSIDE);
                    break;
                case 2:
                    scale_type = GifsArtConst.MODE_SQUARE;
                    imageView.setScaleType(GPUImage.ScaleType.CENTER_CROP);
                    break;
                case 3:
                    scale_type = GifsArtConst.MODE_SQUARE_FIT;
                    imageView.setScaleType(GPUImage.ScaleType.CENTER_INSIDE);
                    break;
                default:
                    break;
            }
        }
        if (requestCode == GifsArtConst.EDIT_FRAME_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {

        }
        if (requestCode == GifsArtConst.EFFECTS_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            String filterName = data.getStringExtra("filter");
            imageView.setFilter(GPUEffects.createFilterForType(MakeGifActivity.this, GPUEffects.FilterType.valueOf(filterName)));
            imageView.requestRender();
        }
        if (requestCode == GifsArtConst.MAIN_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {

            gifItems.remove(gifItems.size() - 1);

            if (data.getIntExtra(GifsArtConst.INDEX, 0) == GifsArtConst.GIPHY_TO_GIF_INDEX) {

                GifItem gifItem = new GifItem(GifUtils.getGifFrameDuration(data.getStringExtra("gif_path")), Type.GIF);
                gifItem.setBitmap(GifUtils.getGifFrames(data.getStringExtra("gif_path")).get(0));
                gifItem.setBitmaps(GifUtils.getGifFrames(data.getStringExtra("gif_path")));
                gifItems.add(gifItem);
            }
            if (data.getIntExtra(GifsArtConst.INDEX, 0) == GifsArtConst.FROM_GALLERY_TO_GIF_INDEX) {
                ArrayList<String> addedItemsArray = data.getStringArrayListExtra(GifsArtConst.IMAGE_PATHS);
                for (int i = 0; i < addedItemsArray.size(); i++) {

                    if (Utils.getMimeType(addedItemsArray.get(i)) == Type.IMAGE) {

                        Bitmap bitmap = ImageLoader.getInstance().loadImageSync(GifsArtConst.FILE_PREFIX + addedItemsArray.get(i));
                        bitmap = Utils.scaleCenterCrop(bitmap, GifsArtConst.FRAME_SIZE, GifsArtConst.FRAME_SIZE);

                        GifItem gifItem = new GifItem(GifsArtConst.IMAGE_FRAME_DURATION, Type.IMAGE);
                        gifItem.setBitmap(bitmap);
                        gifItems.add(gifItem);

                    } else if (Utils.getMimeType(addedItemsArray.get(i)) == Type.GIF) {

                        GifItem gifItem = new GifItem(GifUtils.getGifFrameDuration(addedItemsArray.get(i)), Type.GIF);
                        gifItem.setBitmap(GifUtils.getGifFrames(addedItemsArray.get(i)).get(0));
                        gifItem.setBitmaps(GifUtils.getGifFrames(addedItemsArray.get(i)));
                        gifItems.add(gifItem);

                    } else if (Utils.getMimeType(addedItemsArray.get(i)) == Type.VIDEO) {

                        ArrayList<Bitmap> bitmaps = new ArrayList<>();
                        videoPath = addedItemsArray.get(i);
                        File file = new File(Environment.getExternalStorageDirectory() + "/GifsArt/vid");
                        File[] files = file.listFiles();
                        for (int j = 0; j < files.length; j++) {

                            if (j % 3 != 0) {
                                ByteBuffer buffer = PhotoUtils.readBufferFromFile(files[j].getAbsolutePath(), PhotoUtils.checkBufferSize(videoPath, 2));
                                Bitmap bitmap = PhotoUtils.fromBufferToBitmap(PhotoUtils.checkFrameWidth(videoPath, 2), PhotoUtils.checkFrameHeight(videoPath, 2), buffer);
                                bitmaps.add(bitmap);
                            }

                        }
                        GifItem gifItem = new GifItem(Utils.checkVideoFrameDuration(videoPath, bitmaps.size()), Type.VIDEO);
                        gifItem.setBitmap(Utils.getVideoFirstFrame(videoPath));
                        gifItem.setBitmaps(bitmaps);
                        gifItems.add(gifItem);
                    }

                    selectedItemsArrayList.add(addedItemsArray.get(i));

                }
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
        Bitmap bm = Bitmap.createBitmap(GifsArtConst.FRAME_SIZE, GifsArtConst.FRAME_SIZE, Bitmap.Config.ARGB_8888);
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
            mainView.removeClipArt();
        }
    }

    public void addPlusButton() {
        GifItem gifItem = new GifItem();
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_menu_add);
        gifItem.setBitmap(bitmap);
        gifItems.add(gifItem);
    }

}
