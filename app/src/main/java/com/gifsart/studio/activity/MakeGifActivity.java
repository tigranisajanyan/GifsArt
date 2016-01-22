package com.gifsart.studio.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.decoder.PhotoUtils;
import com.gifsart.studio.R;
import com.gifsart.studio.adapter.ClipartCategoryAdapter;
import com.gifsart.studio.adapter.EffectsAdapter;
import com.gifsart.studio.adapter.MasksAdapter;
import com.gifsart.studio.adapter.SlideAdapter;
import com.gifsart.studio.adapter.StickerAdapter;
import com.gifsart.studio.clipart.ClipartRes;
import com.gifsart.studio.clipart.MainView;
import com.gifsart.studio.effects.ApplyGifEffect;
import com.gifsart.studio.effects.GPUEffects;
import com.gifsart.studio.effects.GPUImageFilterTools;
import com.gifsart.studio.gifutils.GifImitation;
import com.gifsart.studio.gifutils.GifUtils;
import com.gifsart.studio.gifutils.GiphyApiRequest;
import com.gifsart.studio.gifutils.SaveGifBolts;
import com.gifsart.studio.helper.RecyclerItemClickListener;
import com.gifsart.studio.item.GifItem;
import com.gifsart.studio.item.GiphyItem;
import com.gifsart.studio.social.UserContraller;
import com.gifsart.studio.utils.AnimatedProgressDialog;
import com.gifsart.studio.utils.CheckFreeSpaceSingleton;
import com.gifsart.studio.utils.FileCounterSingleton;
import com.gifsart.studio.utils.GifsArtConst;
import com.gifsart.studio.utils.MaskRes;
import com.gifsart.studio.utils.RequestCode;
import com.gifsart.studio.utils.SpacesItemDecoration;
import com.gifsart.studio.utils.SquareFitMode;
import com.gifsart.studio.utils.Type;
import com.gifsart.studio.utils.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import bolts.Continuation;
import bolts.Task;
import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageView;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;


public class MakeGifActivity extends ActionBarActivity {

    private static final String LOG_TAG = MakeGifActivity.class.getSimpleName();
    private static final String root = Environment.getExternalStorageDirectory().toString();

    private ArrayList<String> selectedItemsArrayList = new ArrayList<>();
    private ArrayList<GifItem> gifItems = new ArrayList<>();

    private GPUImageView mainFrameImageView;
    private SeekBar speedSeekBar;
    private RecyclerView framesRecyclerView;
    private ViewGroup mainFrameContainer;
    private MainView mainView;
    private GifImageView maskImageView;
    private LinearLayout container;

    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private RecyclerView.ItemAnimator itemAnimator;
    private SlideAdapter slideAdapter;
    private GifImitation gifImitation;

    private RequestCode requestCode;
    private SquareFitMode square_fit_mode = SquareFitMode.FIT_MODE_SQUARE;

    private GPUImageFilter gpuImageFilter = new GPUImageFilter();
    private GPUImageFilterTools.FilterAdjuster mFilterAdjuster;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private boolean containerIsOpened = false;
    private boolean saveInstanceWorked = false;

    private int gifSpeed = 10;
    private int selectedMaskPosition = 0;
    private int editedFramePosition;
    private int clipartCurrentCategoryPosition = 0;
    private int maskTransparencyLevel = 255;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_gif);

        sharedPreferences = getSharedPreferences(GifsArtConst.SHARED_PREFERENCES, MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if (savedInstanceState != null) {
            Log.d(LOG_TAG, "dooo");
            saveInstanceWorked = savedInstanceState.getBoolean("save_instance_is_opened");
            gifItems = savedInstanceState.getParcelableArrayList("gif_items");
        } else {
            new LoadFramesAsyncTask().execute(getIntent());
        }
        Log.d(LOG_TAG, "onCreate: " + gifItems.size());

    }

    private void init() {

        container = (LinearLayout) findViewById(R.id.container);
        mainFrameImageView = (GPUImageView) findViewById(R.id.image_view);
        mainFrameContainer = (ViewGroup) findViewById(R.id.main_frame_container);
        mainFrameContainer.setDrawingCacheEnabled(true);
        mainFrameContainer.buildDrawingCache();

        ViewGroup.LayoutParams layoutParams = mainFrameContainer.getLayoutParams();
        layoutParams.width = getResources().getDisplayMetrics().widthPixels;
        layoutParams.height = getResources().getDisplayMetrics().widthPixels;

        mainFrameContainer.setLayoutParams(layoutParams);
        mainFrameImageView.setScaleType(GPUImage.ScaleType.CENTER_CROP);

        maskImageView = new GifImageView(this);
        mainFrameContainer.addView(maskImageView);

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

        framesRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position != slideAdapter.getItemCount() - 1) {
                    editedFramePosition = position;
                    gifImitation.showCurrentPosition(position);
                    setContainerLayout(R.layout.edit_frame_layout, RequestCode.EDIT_FRAME);
                    initEditFrameLayout();
                } else {
                    gifImitation.cancel();
                    gifImitation.cancel(true);
                    Intent intent = new Intent(MakeGifActivity.this, MainActivity.class);
                    MakeGifActivity.this.startActivityForResult(intent, GifsArtConst.REQUEST_CODE_MAIN_ACTIVITY);
                    SharedPreferences sharedPreferences = MakeGifActivity.this.getSharedPreferences(GifsArtConst.SHARED_PREFERENCES, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(GifsArtConst.SHARED_PREFERENCES_IS_OPENED, true);
                    editor.commit();
                }
            }
        }));

        /*ItemTouchHelper.Callback callback =
                new SimpleItemTouchHelperCallback(slideAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(framesRecyclerView);*/

        mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(gpuImageFilter);
        addPlusButton();

        gifImitation = new GifImitation(MakeGifActivity.this, mainFrameImageView, gifItems, gifSpeed);
        gifImitation.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


        mainFrameContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (containerIsOpened) {
                    slideDownContainer();
                }
            }
        });

        initMainView();

        speedSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                gifSpeed = (20 - progress);
                gifImitation.changeDuration(gifSpeed);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        final ImageButton playPauseButton = (ImageButton) findViewById(R.id.play_button);
        playPauseButton.setSelected(true);
        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playPauseButton.isSelected()) {
                    gifImitation.onPause();
                    playPauseButton.setSelected(false);
                } else {
                    gifImitation.onResume();
                    playPauseButton.setSelected(true);
                }
            }
        });

        findViewById(R.id.square_fit_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContainerLayout(R.layout.square_fit_layout, RequestCode.SELECT_SQUARE_FIT);
                initSquareFitLayout();
            }
        });

        findViewById(R.id.add_effects_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContainerLayout(R.layout.effects_layout, RequestCode.SELECT_EFFECTS);
                initEffectsLayout();
            }
        });

        findViewById(R.id.add_clipart_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContainerLayout(R.layout.clip_art_layout, RequestCode.SELECT_CLIPART);
                initClipArtLayout();
            }
        });

        findViewById(R.id.mask_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContainerLayout(R.layout.mask_layout, RequestCode.SELECT_MASKS);
                initMaskLayout();
            }
        });

        findViewById(R.id.add_text_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*View convertView = getLayoutInflater().inflate(R.layout.text_list_item, mainFrameContainer, false);
                TextArtView item = (TextArtView) convertView;
                item.initParams(TextArtStyle.getPreviewStyleObj(false, ""), "gagag");
                mainFrameContainer.addView(item);*/

            }
        });

        findViewById(R.id.make_gif_activity_toolbar_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (containerIsOpened) {
                    if (requestCode == RequestCode.EDIT_FRAME) {
                        gifImitation.showAllPositions();
                        addPlusButton();
                    }

                    if (requestCode == RequestCode.SELECT_SQUARE_FIT) {
                    }

                    if (mainView != null && requestCode == RequestCode.SELECT_CLIPART) {
                        mainView.removeClipArt();
                    }

                    slideDownContainer();
                } else {
                    android.app.AlertDialog.Builder gifSavedDialogBuilder = new android.app.AlertDialog.Builder(MakeGifActivity.this);
                    gifSavedDialogBuilder.setMessage("Do you really want?");
                    gifSavedDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            CheckFreeSpaceSingleton.getInstance().clearAllocatedSpace();
                            gifImitation.cancel(true);
                            finish();
                        }
                    });
                    gifSavedDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    android.app.AlertDialog alertDialog = gifSavedDialogBuilder.create();
                    alertDialog.show();
                }
            }
        });

        findViewById(R.id.make_gif_activity_toolbar_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (containerIsOpened) {
                    if (requestCode == RequestCode.EDIT_FRAME) {
                        addPlusButton();
                    }
                    slideDownContainer();
                } else {
                    CheckFreeSpaceSingleton.getInstance().clearAllocatedSpace();
                    gifItems.remove(gifItems.size() - 1);
                    gifImitation.cancel(true);
                    mainFrameImageView.getGPUImage().deleteImage();

                    final AnimatedProgressDialog animatedProgressDialog = new AnimatedProgressDialog(MakeGifActivity.this);
                    animatedProgressDialog.setCancelable(false);
                    animatedProgressDialog.show();

                    //square_fit_mode = SaveGifBolts.checkSquareFitMode(gifItems, square_fit_mode); //// TODO: 1/18/16 check all frames width and height for square fit mode
                    SaveGifBolts.doSquareFitTask(square_fit_mode, gifItems).continueWithTask(new Continuation<Void, Task<Void>>() {
                        @Override
                        public Task<Void> then(final Task<Void> task) throws Exception {
                            Log.d(LOG_TAG, "step: 1");
                            return SaveGifBolts.applyEffect(gifItems, gpuImageFilter, MakeGifActivity.this);
                        }
                    }).continueWithTask(new Continuation<Void, Task<Void>>() {
                        @Override
                        public Task<Void> then(Task<Void> task) throws Exception {
                            Log.d(LOG_TAG, "step: 2");
                            if (mainView.getClipartItem() == null) {
                                return null;
                            }
                            return SaveGifBolts.setClipartsOnGifTask(gifItems, mainView);
                        }
                    }).continueWithTask(new Continuation<Void, Task<Void>>() {
                        @Override
                        public Task<Void> then(Task<Void> task) throws Exception {
                            Log.d(LOG_TAG, "step: 3");
                            if (selectedMaskPosition < 1) {
                                return null;
                            }
                            return SaveGifBolts.addMaskToGifTask(gifItems, MaskRes.maskResourceIds[selectedMaskPosition], maskTransparencyLevel, MakeGifActivity.this);
                        }
                    }).continueWithTask(new Continuation<Void, Task<Void>>() {
                        @Override
                        public Task<Void> then(Task<Void> task) throws Exception {
                            Log.d(LOG_TAG, "step: 4");
                            return SaveGifBolts.addFramesToGifTask(root + "/test.gif", gifItems);
                        }
                    }).onSuccessTask(new Continuation<Void, Task<Void>>() {
                        @Override
                        public Task<Void> then(Task<Void> task) throws Exception {
                            animatedProgressDialog.dismiss();
                            Intent intent = null;
                            if (UserContraller.readUserFromFile(MakeGifActivity.this) != null) {
                                intent = new Intent(MakeGifActivity.this, PicsArtShareActivity.class);
                            } else {
                                intent = new Intent(MakeGifActivity.this, ShareGifActivity.class);
                            }
                            intent.putExtra("saved_file_path", root + "/test.gif");
                            startActivity(intent);
                            finish();
                            return null;
                        }
                    });
                }
            }
        });
    }

    public void initMainView() {
        Bitmap bm = Bitmap.createBitmap(GifsArtConst.GIF_FRAME_SIZE, GifsArtConst.GIF_FRAME_SIZE, Bitmap.Config.ARGB_8888);
        bm.eraseColor(Color.TRANSPARENT);
        Bitmap mutableBitmap = bm.copy(Bitmap.Config.ARGB_8888, true);
        mainView = new MainView(this, mutableBitmap);
        mainView.setId(R.id.mainViewId);
        mainFrameContainer.addView(mainView);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(LOG_TAG, "onActivityResult");
        if (requestCode == GifsArtConst.REQUEST_CODE_MAIN_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                new LoadFramesAsyncTask().execute(data);
            } else {
                gifImitation.onResume();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "onResume");
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        gifItems.remove(gifItems.size() - 1);
        gifImitation.cancel();
        gifImitation.cancel(true);
        savedInstanceState.putBoolean("save_instance_is_opened", true);
        saveInstanceWorked = true;
        savedInstanceState.putParcelableArrayList("gif_items", gifItems);
        Log.d(LOG_TAG, "onSaveInstanceState");
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!saveInstanceWorked) {
            CheckFreeSpaceSingleton.getInstance().clearAllocatedSpace();
            editor.putBoolean(GifsArtConst.SHARED_PREFERENCES_IS_OPENED, false);
            editor.commit();
        }
        gifImitation.cancel();
        gifImitation.cancel(true);
        Log.d(LOG_TAG, "onDestroy");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(LOG_TAG, "onRestart");
        init();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d(LOG_TAG, "onBackPressed");
        //if (!sharedPreferences.getBoolean(GifsArtConst.SHARED_PREFERENCES_IS_OPENED, false)) {
        CheckFreeSpaceSingleton.getInstance().clearAllocatedSpace();
        gifImitation.cancel();
        //gifItems.clear();
        //}
    }

    /*public void saveClipart() {
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
    }*/

    //  This method is adding framesRecyclerView last item(button),
    //  wich type is none and it's for adding new gifitems from gallery, shooting gif and/or giphy activities
    public void addPlusButton() {
        GifItem gifItem = new GifItem();
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.add_icon);
        gifItem.setBitmap(bitmap);
        gifItems.add(gifItem);
    }

    public void initSquareFitLayout() {
        SquareFitMode.switchSquareFitMode(this, square_fit_mode);
        container.findViewById(R.id.original_fit_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (square_fit_mode != SquareFitMode.FIT_MODE_ORIGINAL) {
                    mainFrameImageView.setScaleType(GPUImage.ScaleType.CENTER_INSIDE);
                    square_fit_mode = SquareFitMode.FIT_MODE_ORIGINAL;
                    SquareFitMode.switchSquareFitMode(MakeGifActivity.this, square_fit_mode);
                }
            }
        });

        container.findViewById(R.id.square_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (square_fit_mode != SquareFitMode.FIT_MODE_SQUARE) {
                    mainFrameImageView.setScaleType(GPUImage.ScaleType.CENTER_CROP);
                    square_fit_mode = SquareFitMode.FIT_MODE_SQUARE;
                    SquareFitMode.switchSquareFitMode(MakeGifActivity.this, square_fit_mode);
                }
            }
        });

        container.findViewById(R.id.square_fit_button_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (square_fit_mode != SquareFitMode.FIT_MODE_SQUARE_FIT) {
                    mainFrameImageView.setScaleType(GPUImage.ScaleType.CENTER_INSIDE);
                    square_fit_mode = SquareFitMode.FIT_MODE_SQUARE_FIT;
                    SquareFitMode.switchSquareFitMode(MakeGifActivity.this, square_fit_mode);
                }
            }
        });
    }

    public void initEffectsLayout() {

        RecyclerView recyclerView = (RecyclerView) container.findViewById(R.id.effects_rec_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        recyclerView.setHasFixedSize(true);
        recyclerView.setClipToPadding(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(itemAnimator);
        recyclerView.addItemDecoration(new SpacesItemDecoration((int) Utils.dpToPixel(2, this)));

        EffectsAdapter effectsAdapter = new EffectsAdapter(this);

        ApplyGifEffect applyGifEffect = new ApplyGifEffect(effectsAdapter, MakeGifActivity.this);
        applyGifEffect.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        recyclerView.setAdapter(effectsAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ((SeekBar) container.findViewById(R.id.opacity_seek_bar)).setProgress(100);
                switchFilterTo(GPUEffects.createFilterForType(MakeGifActivity.this, GPUEffects.FilterType.fromInt(position)));
            }
        }));

        container.findViewById(R.id.opacity_seek_bar).setVisibility(
                mFilterAdjuster.canAdjust() ? View.VISIBLE : View.INVISIBLE);

        ((SeekBar) container.findViewById(R.id.opacity_seek_bar)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mFilterAdjuster != null) {
                    mFilterAdjuster.adjust(progress);
                }
                mainFrameImageView.requestRender();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void initClipArtLayout() {

        final RecyclerView stickerRecyclerView = (RecyclerView) container.findViewById(R.id.clipart_rec_view);
        final RecyclerView categoryRecyclerView = (RecyclerView) container.findViewById(R.id.sticker_category_rec_view);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        stickerRecyclerView.setHasFixedSize(true);
        stickerRecyclerView.setClipToPadding(true);
        stickerRecyclerView.setLayoutManager(gridLayoutManager);
        stickerRecyclerView.setItemAnimator(itemAnimator);
        stickerRecyclerView.addItemDecoration(new SpacesItemDecoration((int) Utils.dpToPixel(5, this)));

        categoryRecyclerView.setHasFixedSize(true);
        categoryRecyclerView.setClipToPadding(true);
        categoryRecyclerView.setLayoutManager(linearLayoutManager);
        categoryRecyclerView.setItemAnimator(itemAnimator);

        final StickerAdapter stickerAdapter = new StickerAdapter(true, MakeGifActivity.this);
        stickerRecyclerView.setAdapter(stickerAdapter);

        final ClipartCategoryAdapter clipartCategoryAdapter = new ClipartCategoryAdapter(this);
        categoryRecyclerView.setAdapter(clipartCategoryAdapter);

        if (Utils.haveNetworkConnection(this)) {
            final GiphyApiRequest giphyApiRequest = new GiphyApiRequest(MakeGifActivity.this, GifsArtConst.GIPHY_TAG, true, 0, GifsArtConst.GIPHY_LIMIT_COUNT);
            giphyApiRequest.requestGiphy();
            giphyApiRequest.setOnDownloadedListener(new GiphyApiRequest.GiphyListener() {
                @Override
                public void onGiphyDownloadFinished(ArrayList<GiphyItem> items) {
                    stickerAdapter.addGiphyItems(items);
                }
            });
        }

        stickerRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                if (clipartCurrentCategoryPosition > 0) {
                    if (mainView != null) {
                        mainView.addClipart(ClipartRes.stickerResourceIds[position]);
                    }
                } else {
                    final Bitmap[] bitmap = {null};
                    final Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            bitmap[0] = ImageLoader.getInstance().loadImageSync(((GiphyItem) stickerAdapter.getItem(position)).getDownsampledGifUrl());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (mainView != null) {
                                        mainView.addClipart(bitmap[0]);
                                    }
                                }
                            });
                        }
                    };
                    Thread thread = new Thread(runnable);
                    thread.start();
                }
            }
        }));

        categoryRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position == 0 && Utils.haveNetworkConnection(MakeGifActivity.this)) {
                    clipartCurrentCategoryPosition = 0;
                    stickerAdapter.clearGiphyItems();
                    GiphyApiRequest giphyApiRequest = new GiphyApiRequest(MakeGifActivity.this, GifsArtConst.GIPHY_TAG, true, 0, GifsArtConst.GIPHY_LIMIT_COUNT);
                    giphyApiRequest.requestGiphy();
                    giphyApiRequest.setOnDownloadedListener(new GiphyApiRequest.GiphyListener() {
                        @Override
                        public void onGiphyDownloadFinished(ArrayList<GiphyItem> items) {
                            stickerAdapter.addGiphyItems(items);
                        }
                    });
                } else {
                    clipartCurrentCategoryPosition = position;
                    stickerAdapter.clearResourceItems();
                    stickerAdapter.addResourceItems(ClipartRes.stickerResourceIds);
                }
            }
        }));

        findViewById(R.id.sticker_search_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MakeGifActivity.this, GiphyStickerActivity.class);
                startActivityForResult(intent, 666);
            }
        });
    }

    public void initEditFrameLayout() {
        gifItems.remove(gifItems.size() - 1);
        final TextView gifItemsCountTextView = ((TextView) findViewById(R.id.gifitems_count_text_view));
        gifItemsCountTextView.setText((editedFramePosition + 1) + " of " + gifItems.size());

        findViewById(R.id.left_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editedFramePosition > 0) {
                    editedFramePosition -= 1;
                    gifImitation.showCurrentPosition(editedFramePosition);
                    gifItemsCountTextView.setText((editedFramePosition + 1) + " of " + gifItems.size());
                }
            }
        });
        findViewById(R.id.right_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editedFramePosition < gifItems.size() - 1) {
                    editedFramePosition += 1;
                    gifImitation.showCurrentPosition(editedFramePosition);
                    gifItemsCountTextView.setText((editedFramePosition + 1) + " of " + gifItems.size());
                }
            }
        });

        findViewById(R.id.duplicate_frame_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gifItems.get(editedFramePosition).getType() == Type.IMAGE) {
                    gifItems.add(editedFramePosition + 1, gifItems.get(editedFramePosition));
                    gifImitation.showAllPositions();
                    addPlusButton();
                    slideAdapter.notifyDataSetChanged();
                    slideDownContainer();
                }
            }
        });
    }

    public void initMaskLayout() {

        final RecyclerView masksRecyclerView = (RecyclerView) container.findViewById(R.id.masks_rec_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        masksRecyclerView.setHasFixedSize(true);
        masksRecyclerView.setClipToPadding(true);
        masksRecyclerView.setLayoutManager(linearLayoutManager);
        masksRecyclerView.setItemAnimator(itemAnimator);
        masksRecyclerView.addItemDecoration(new SpacesItemDecoration((int) Utils.dpToPixel(2, this)));

        MasksAdapter masksAdapter = new MasksAdapter(this);
        masksRecyclerView.setAdapter(masksAdapter);

        final SeekBar opacitiySeekBar = ((SeekBar) findViewById(R.id.opacity_seek_bar));
        opacitiySeekBar.setMax(255);
        opacitiySeekBar.setProgress(255);
        opacitiySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                maskImageView.setAlpha(progress);
                maskTransparencyLevel = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        masksRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                selectedMaskPosition = position;
                if (position == 0) {
                    maskImageView.setImageDrawable(null);
                } else {
                    try {
                        GifDrawable gifDrawable = new GifDrawable(getResources(), MaskRes.maskResourceIds[position]);
                        maskImageView.setImageDrawable(gifDrawable);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }));
    }

    private void switchFilterTo(final GPUImageFilter filter) {
        if (gpuImageFilter == null
                || (filter != null && !gpuImageFilter.getClass().equals(filter.getClass()))) {
            gpuImageFilter = filter;
            mainFrameImageView.setFilter(gpuImageFilter);
            mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(gpuImageFilter);

            findViewById(R.id.opacity_seek_bar).setVisibility(
                    mFilterAdjuster.canAdjust() ? View.VISIBLE : View.INVISIBLE);
        }
    }

    public void addImageItem(String path, boolean cameraFront, ArrayList<GifItem> gifItems) {

        Bitmap bitmap = null;
        try {
            bitmap = Glide.with(this).asBitmap().load(path).into(500, 500).get(); // todo depricated
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        /*Glide.with(this).asBitmap().load(path).listener(null).apply(RequestOptions.noTransform()).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
                if (bitmap != null && !bitmap.isRecycled()) {
                }
            }
        });*/

        String newPath = Environment.getExternalStorageDirectory().getPath() + "/" + GifsArtConst.DIR_VIDEO_FRAMES + "/img_" + FileCounterSingleton.getInstance(this).increaseIndex();
        PhotoUtils.saveRawBitmap(bitmap, newPath);

        GifItem gifItem = new GifItem(GifsArtConst.IMAGE_FRAME_DURATION, Type.IMAGE);
        gifItem.setCurrentDuration(GifsArtConst.IMAGE_FRAME_DURATION);
        gifItem.setBitmap(bitmap);
        gifItem.setFilePath(newPath);
        gifItems.add(gifItem);

    }

    public void addGifItem(String path, ArrayList<GifItem> gifItems) {
        GifItem gifItem = new GifItem(GifUtils.getGifFrameDuration(path), Type.GIF);
        gifItem.setCurrentDuration(GifUtils.getGifFrameDuration(path));
        gifItem.setBitmap(GifUtils.getGifFrames(path).get(0));
        ArrayList<Bitmap> bitmaps = GifUtils.getGifFrames(path);
        ArrayList<String> strings = new ArrayList<>();
        for (int i = 0; i < GifUtils.getGifFramesCount(path); i++) {
            String filePath = Environment.getExternalStorageDirectory() + GifsArtConst.SLASH + GifsArtConst.DIR_VIDEO_FRAMES + "/img_" + FileCounterSingleton.getInstance(this).increaseIndex();
            PhotoUtils.saveRawBitmap(bitmaps.get(i), filePath);
            strings.add(filePath);
        }
        gifItem.setFilePath(strings.get(0));
        gifItem.setFilePaths(strings);
        bitmaps.clear();
        gifItems.add(gifItem);
    }

    public void addCameraItem(String path, boolean cameraFront, boolean isBurst, ArrayList<GifItem> gifItems) {
        ArrayList<String> strings = new ArrayList<>();
        File file = new File(path);
        File[] files = file.listFiles();
        for (int j = 0; j < files.length; j++) {
            //Bitmap bitmap = PhotoUtils.loadRawBitmap(files[j].getAbsolutePath());
            /*try {
                Glide.get(this).clearDiskCache();
                bitmap = Glide.with(this).load(files[j].getAbsolutePath()).asBitmap().into(500, 500).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }*/
            //Bitmap bitmap = ImageLoader.getInstance().loadImageSync(FILE_PREFIX + files[j].getAbsolutePath());
            /*if (cameraFront && !isBurst) {
                bitmap = Utils.rotateBitmap(bitmap, 180);
            }*/
            strings.add(files[j].getAbsolutePath());

        }
        GifItem gifItem = new GifItem(40, Type.VIDEO);
        gifItem.setCurrentDuration(40);
        //gifItem.setBitmap(bitmaps.get(0));e
        //gifItem.setBitmaps(bitmaps);
        gifItem.setFilePath(strings.get(0));
        gifItem.setFilePaths(strings);
        gifItems.add(gifItem);
    }

    public void addVideoItem(String path, Intent intent, ArrayList<GifItem> gifItems) {
        ArrayList<String> strings = new ArrayList<>();
        File file = new File(GifsArtConst.VIDEOS_DECODED_FRAMES_DIR);
        File[] files = file.listFiles();
        for (int j = 0; j < files.length; j++) {
            /*if (j % 3 != 0) {
                ByteBuffer buffer = PhotoUtils.readBufferFromFile(files[j].getAbsolutePath(), PhotoUtils.checkBufferSize(path, scaleSize));
                Bitmap bitmap = PhotoUtils.fromBufferToBitmap(PhotoUtils.checkFrameWidth(path, scaleSize), PhotoUtils.checkFrameHeight(path, scaleSize), buffer);
                if (cameraFront) {
                    bitmap = Utils.rotateBitmap(bitmap, 180);
                }
                bitmaps.add(bitmap);
            }*/
            strings.add(files[j].getAbsolutePath());
        }
        GifItem gifItem = new GifItem(Utils.checkVideoFrameDuration(path, strings.size()), Type.VIDEO);
        gifItem.setCurrentDuration(Utils.checkVideoFrameDuration(path, strings.size()));
        //gifItem.setBitmap(bitmaps.get(0));
        gifItem.setFilePath(strings.get(0));
        gifItem.setFilePaths(strings);
        gifItems.add(gifItem);
    }


    class LoadFramesAsyncTask extends AsyncTask<Intent, Void, Void> {
        AnimatedProgressDialog animatedProgressDialog = new AnimatedProgressDialog(MakeGifActivity.this);
        int contentType = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            animatedProgressDialog.setCancelable(false);
            animatedProgressDialog.show();
            /*try {
                ImageLoader.getInstance().clearMemoryCache();
                ImageLoader.getInstance().clearDiskCache();
            } catch (Exception e) {
                Utils.initImageLoader(getApplicationContext());
//                ImageLoader.getInstance().clearMemoryCache();
//                ImageLoader.getInstance().clearDiskCache();
            }*/
        }

        @Override
        protected Void doInBackground(Intent... params) {
            Intent data = params[0];
            contentType = data.getIntExtra(GifsArtConst.INTENT_ACTIVITY_INDEX, 0);
            if (contentType == GifsArtConst.INDEX_FROM_GALLERY_TO_GIF) {
                selectedItemsArrayList = data.getStringArrayListExtra(GifsArtConst.INTENT_DECODED_IMAGE_PATHS);
                for (int i = 0; i < selectedItemsArrayList.size(); i++) {
                    if (Utils.getMimeType(selectedItemsArrayList.get(i)) == Type.IMAGE) {
                        addImageItem(selectedItemsArrayList.get(i), data.getBooleanExtra(GifsArtConst.INTENT_FRONT_CAMERA, false), gifItems);
                    } else if (Utils.getMimeType(selectedItemsArrayList.get(i)) == Type.GIF) {
                        addGifItem(selectedItemsArrayList.get(i), gifItems);
                    } else if (Utils.getMimeType(selectedItemsArrayList.get(i)) == Type.VIDEO) {
                        addVideoItem(selectedItemsArrayList.get(i), data, gifItems);
                    }
                }
            }
            // Checking index, if selected item is from giphy activity will enter this scope
            if (contentType == GifsArtConst.INDEX_GIPHY_TO_GIF) {
                //addGifItem(getIntent().getStringExtra(GifsArtConst.INTENT_GIF_PATH), gifItems);
                addGifItem(Environment.getExternalStorageDirectory() + "/ttt.gif", gifItems);
            }
            // Checking index, if selected item is a video from shooting gif activity will enter this scope
            if (contentType == GifsArtConst.INDEX_SHOOT_GIF) {
                addCameraItem(Environment.getExternalStorageDirectory() + "/" + GifsArtConst.DIR_VIDEO_FRAMES, data.getBooleanExtra(GifsArtConst.INTENT_FRONT_CAMERA, false), data.getBooleanExtra(GifsArtConst.INTENT_CAMERA_BURST_MODE, false), gifItems);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            init();
            animatedProgressDialog.dismiss();
            Log.d(LOG_TAG, "Frames Count: " + CheckFreeSpaceSingleton.getInstance().getAllocatedSpace());
        }
    }

    // Set container layout content
    public void setContainerLayout(int resourceId, RequestCode requestCode) {
        this.requestCode = requestCode;
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(resourceId, null, false);

        ViewGroup.LayoutParams params = container.getLayoutParams();
        params.height = (int) (findViewById(R.id.buttons_container).getBottom() - mainFrameContainer.getBottom());

        container.removeAllViews();
        container.addView(view);
        container.setLayoutParams(params);
        TranslateAnimation anim = new TranslateAnimation(0, 0, Utils.convertDpToPixel(mainFrameContainer.getBottom(), getApplicationContext()), 0);
        anim.setDuration(500);
        anim.setFillAfter(true);
        container.setVisibility(View.VISIBLE);
        container.startAnimation(anim);
        containerIsOpened = true;
        ((Button) findViewById(R.id.make_gif_activity_toolbar_next)).setText("Apply");
    }

    // Layout Container slide down animation
    public void slideDownContainer() {
        ((Button) findViewById(R.id.make_gif_activity_toolbar_next)).setText("Next");
        TranslateAnimation anim = new TranslateAnimation(0, 0, 0, Utils.convertDpToPixel(mainFrameContainer.getBottom(), getApplicationContext()));
        anim.setDuration(500);
        anim.setFillAfter(false);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                container.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        container.setVisibility(View.VISIBLE);
        container.startAnimation(anim);
        containerIsOpened = false;
    }

}
