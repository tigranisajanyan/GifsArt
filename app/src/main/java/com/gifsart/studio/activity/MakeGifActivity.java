package com.gifsart.studio.activity;

import android.app.ProgressDialog;
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
import android.support.v7.widget.helper.ItemTouchHelper;
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
import com.gifsart.studio.adapter.EffectsAdapter;
import com.gifsart.studio.adapter.MasksAdapter;
import com.gifsart.studio.adapter.SlideAdapter;
import com.gifsart.studio.adapter.StickerAdapter;
import com.gifsart.studio.adapter.StickerCategoryAdapter;
import com.gifsart.studio.clipart.DrawClipArtOnMainFrames;
import com.gifsart.studio.clipart.MainView;
import com.gifsart.studio.effects.GPUEffects;
import com.gifsart.studio.effects.GPUImageFilterTools;
import com.gifsart.studio.gifutils.GifUtils;
import com.gifsart.studio.gifutils.Giphy;
import com.gifsart.studio.gifutils.SaveGIFAsyncTask;
import com.gifsart.studio.helper.RecyclerItemClickListener;
import com.gifsart.studio.item.GifItem;
import com.gifsart.studio.gifutils.GifImitation;
import com.gifsart.studio.item.GiphyItem;
import com.gifsart.studio.utils.AddMaskAsyncTask;
import com.gifsart.studio.utils.CheckSpaceSingleton;
import com.gifsart.studio.utils.GifsArtConst;
import com.gifsart.studio.utils.SpacesItemDecoration;
import com.gifsart.studio.utils.Type;
import com.gifsart.studio.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageView;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;


public class MakeGifActivity extends ActionBarActivity {

    private static final String root = Environment.getExternalStorageDirectory().toString();

    private GPUImageView mainFrameImageView;
    private SeekBar speedSeekBar;
    private RecyclerView framesRecyclerView;
    private ViewGroup mainFrameContainer;
    private MainView mainView;
    private GifImageView maskImageView;

    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private RecyclerView.ItemAnimator itemAnimator;

    private SlideAdapter slideAdapter;

    private ArrayList<String> selectedItemsArrayList = new ArrayList<>();
    private ArrayList<GifItem> gifItems = new ArrayList<>();

    private ArrayList<Integer> stickerResourceIds = new ArrayList<>();
    private ArrayList<Integer> categoryResourceIds = new ArrayList<>();
    private ArrayList<Integer> maskResourceIds = new ArrayList<>();

    private int gifSpeed = 10;
    private GifImitation gifImitation;

    private int square_fit_mode = GifsArtConst.FIT_MODE_ORIGINAL;

    private GPUImageFilter gpuImageFilter = new GPUImageFilter();
    private GPUImageFilterTools.FilterAdjuster mFilterAdjuster;
    private GPUEffects.FilterList filters = new GPUEffects.FilterList();

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private LinearLayout container;
    private int contentType = 0;

    private boolean containerIsOpened = false;
    private int selectedMaskPosition = 0;
    private int editedFramePosition;
    private int clipartCurrentCategoryPosition = 0;

    private RequestCode requestCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_gif);

        categoryResourceIds.add(R.drawable.giphy_icon);
        categoryResourceIds.add(R.drawable.clipart_6);

        stickerResourceIds.add(R.drawable.clipart_1);
        stickerResourceIds.add(R.drawable.clipart_2);
        stickerResourceIds.add(R.drawable.clipart_3);
        stickerResourceIds.add(R.drawable.clipart_4);
        stickerResourceIds.add(R.drawable.clipart_5);
        stickerResourceIds.add(R.drawable.clipart_6);
        stickerResourceIds.add(R.drawable.clipart_7);
        stickerResourceIds.add(R.drawable.clipart_8);

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

        maskResourceIds.add(R.drawable.none_icon);
        maskResourceIds.add(R.raw.mask1);
        maskResourceIds.add(R.raw.mask2);
        maskResourceIds.add(R.raw.mask3);
        maskResourceIds.add(R.raw.mask4);
        maskResourceIds.add(R.raw.mask5);
        maskResourceIds.add(R.raw.mask6);
        maskResourceIds.add(R.raw.mask7);
        maskResourceIds.add(R.raw.mask8);
        maskResourceIds.add(R.raw.mask9);
        maskResourceIds.add(R.raw.mask10);
        maskResourceIds.add(R.raw.mask11);
        maskResourceIds.add(R.raw.mask12);
        maskResourceIds.add(R.raw.mask13);
        maskResourceIds.add(R.raw.mask14);
        maskResourceIds.add(R.raw.mask15);
        maskResourceIds.add(R.raw.mask16);
        maskResourceIds.add(R.raw.mask17);

        new LoadFramesAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    private void init() {

        sharedPreferences = getSharedPreferences(GifsArtConst.SHARED_PREFERENCES, MODE_PRIVATE);
        editor = sharedPreferences.edit();

        container = (LinearLayout) findViewById(R.id.container);
        mainFrameImageView = (GPUImageView) findViewById(R.id.image_view);
        mainFrameContainer = (ViewGroup) findViewById(R.id.main_frame_container);

        ViewGroup.LayoutParams layoutParams = mainFrameContainer.getLayoutParams();
        layoutParams.width = getResources().getDisplayMetrics().widthPixels;
        layoutParams.height = getResources().getDisplayMetrics().widthPixels;

        mainFrameContainer.setLayoutParams(layoutParams);
        mainFrameImageView.setScaleType(GPUImage.ScaleType.CENTER_INSIDE);


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
                    requestCode = RequestCode.EDIT_TEXT;
                    gifImitation.showCurrentPosition(position);
                    setContainerLayout(R.layout.edit_frame_layout);
                    initEditFrameLayout();
                } else {
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

        initMainView();

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
                requestCode = RequestCode.SELECT_SQUARE_FIT;
                setContainerLayout(R.layout.square_fit_layout);
                initSquareFitLayout();
            }
        });

        findViewById(R.id.add_effects_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestCode = RequestCode.SELECT_EFFECTS;
                setContainerLayout(R.layout.effects_layout);
                initEffectsLayout();
            }
        });

        findViewById(R.id.add_clipart_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestCode = RequestCode.SELECT_CLIPART;
                setContainerLayout(R.layout.clip_art_layout);
                initClipArtLayout();
            }
        });

        findViewById(R.id.mask_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestCode = RequestCode.SELECT_MASKS;
                setContainerLayout(R.layout.mask_layout);
                initMaskLayout();
            }
        });

        findViewById(R.id.make_gif_activity_toolbar_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (containerIsOpened) {
                    if (requestCode == RequestCode.EDIT_TEXT) {
                        gifImitation.showAllPositions();
                        addPlusButton();
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
                    slideDownContainer();
                } else {
                    CheckSpaceSingleton.getInstance().removeAllSpace();
                    gifItems.remove(gifItems.size() - 1);
                    gifImitation.cancel(true);

                    DrawClipArtOnMainFrames drawClipArtOnMainFrames = new DrawClipArtOnMainFrames(MakeGifActivity.this, mainView, gifItems);
                    drawClipArtOnMainFrames.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    drawClipArtOnMainFrames.setDrownedListener(new DrawClipArtOnMainFrames.ClipartsAreDrowned() {
                        @Override
                        public void areDrowned(boolean done) {
                            if (done) {
                                if (selectedMaskPosition != 0) {
                                    AddMaskAsyncTask addMaskAsyncTask = new AddMaskAsyncTask(gifItems, maskResourceIds.get(selectedMaskPosition), MakeGifActivity.this);
                                    addMaskAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                    addMaskAsyncTask.setMergeGifsListener(new AddMaskAsyncTask.MergeGifs() {
                                        @Override
                                        public void Merged(boolean done) {
                                            if (done) {
                                                final SaveGIFAsyncTask saveGIFAsyncTask = new SaveGIFAsyncTask(root + GifsArtConst.SLASH + GifsArtConst.GIF_NAME, gifItems, square_fit_mode, mainFrameImageView, gpuImageFilter, MakeGifActivity.this);
                                                saveGIFAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                            }
                                        }
                                    });
                                } else {
                                    final SaveGIFAsyncTask saveGIFAsyncTask = new SaveGIFAsyncTask(root + GifsArtConst.SLASH + GifsArtConst.GIF_NAME, gifItems, square_fit_mode, mainFrameImageView, gpuImageFilter, MakeGifActivity.this);
                                    saveGIFAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                }
                            }
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
        if (requestCode == GifsArtConst.REQUEST_CODE_MAIN_ACTIVITY && resultCode == RESULT_OK) {
            new AddFramesAsyncTask().execute(data);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gifImitation.cancel(true);
        editor.clear();
        editor.commit();
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

        container.findViewById(R.id.original_fit_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainFrameImageView.setScaleType(GPUImage.ScaleType.CENTER_INSIDE);
                square_fit_mode = GifsArtConst.FIT_MODE_ORIGINAL;
            }
        });

        container.findViewById(R.id.square_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainFrameImageView.setScaleType(GPUImage.ScaleType.CENTER_CROP);
                square_fit_mode = GifsArtConst.FIT_MODE_SQUARE;
            }
        });

        container.findViewById(R.id.square_fit_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainFrameImageView.setScaleType(GPUImage.ScaleType.CENTER_INSIDE);
                square_fit_mode = GifsArtConst.FIT_MODE_SQUARE_FIT;
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

        EffectsAdapter effectsAdapter = new EffectsAdapter(filters, this);
        new ApplyEffectsLayoutEffects().execute(effectsAdapter);

        recyclerView.setAdapter(effectsAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                switchFilterTo(GPUEffects.createFilterForType(filters.filters.get(position)));
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

        final StickerCategoryAdapter stickerCategoryAdapter = new StickerCategoryAdapter(categoryResourceIds, this);
        categoryRecyclerView.setAdapter(stickerCategoryAdapter);

        if (Utils.haveNetworkConnection(this)) {
            final Giphy giphy = new Giphy(MakeGifActivity.this, GifsArtConst.GIPHY_TAG, true, 0, GifsArtConst.GIPHY_LIMIT_COUNT);
            giphy.requestGiphy();
            giphy.setOnDownloadedListener(new Giphy.GiphyListener() {
                @Override
                public void onGiphyDownloadFinished(ArrayList<GiphyItem> items) {
                    stickerAdapter.addGiphyItems(items);
                }
            });
        }

        stickerRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (clipartCurrentCategoryPosition > 0) {
                    if (mainView != null) {
                        mainView.addClipart(stickerResourceIds.get(position));
                    }
                }
            }
        }));

        categoryRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position == 0 && Utils.haveNetworkConnection(MakeGifActivity.this)) {
                    clipartCurrentCategoryPosition = 0;
                    stickerAdapter.clearGiphyItems();
                    Giphy giphy = new Giphy(MakeGifActivity.this, GifsArtConst.GIPHY_TAG, true, 0, GifsArtConst.GIPHY_LIMIT_COUNT);
                    giphy.requestGiphy();
                    giphy.setOnDownloadedListener(new Giphy.GiphyListener() {
                        @Override
                        public void onGiphyDownloadFinished(ArrayList<GiphyItem> items) {
                            stickerAdapter.addGiphyItems(items);
                        }
                    });
                } else {
                    clipartCurrentCategoryPosition = position;
                    stickerAdapter.clearResourceItems();
                    stickerAdapter.addResourceItems(stickerResourceIds);
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

        MasksAdapter masksAdapter = new MasksAdapter(maskResourceIds, this);
        masksRecyclerView.setAdapter(masksAdapter);

        final SeekBar opacitiySeekBar = ((SeekBar) findViewById(R.id.opacity_seek_bar));
        opacitiySeekBar.setMax(255);
        opacitiySeekBar.setProgress(255);
        opacitiySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                maskImageView.setAlpha(progress);
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
                        GifDrawable gifDrawable = new GifDrawable(getResources(), maskResourceIds.get(position));
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

    public void addImageItem(String path, ArrayList<GifItem> gifItems) {

        Bitmap bitmap = null;
        try {
            bitmap = Glide.with(this).load(path).asBitmap().into(GifsArtConst.GIF_FRAME_SIZE, GifsArtConst.GIF_FRAME_SIZE).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        GifItem gifItem = new GifItem(GifsArtConst.IMAGE_FRAME_DURATION, Type.IMAGE);
        gifItem.setCurrentDuration(GifsArtConst.IMAGE_FRAME_DURATION);
        gifItem.setBitmap(bitmap);
        gifItems.add(gifItem);
    }

    public void addGifItem(String path, ArrayList<GifItem> gifItems) {
        GifItem gifItem = new GifItem(GifUtils.getGifFrameDuration(path), Type.GIF);
        gifItem.setCurrentDuration(GifUtils.getGifFrameDuration(path));
        gifItem.setBitmap(GifUtils.getGifFrames(path).get(0));
        gifItem.setBitmaps(GifUtils.getGifFrames(path));
        gifItems.add(gifItem);
    }

    public void addVideoItem(String path, Intent intent, ArrayList<GifItem> gifItems) {
        ArrayList<Bitmap> bitmaps = new ArrayList<>();
        int scaleSize = intent.getIntExtra(GifsArtConst.INTENT_VIDEO_FRAME_SCALE_SIZE, GifsArtConst.VIDEO_FRAME_SCALE_SIZE);
        File file = new File(GifsArtConst.VIDEOS_DECODED_FRAMES_DIR);
        File[] files = file.listFiles();
        for (int j = 0; j < files.length; j++) {

            if (j % 3 != 0) {
                ByteBuffer buffer = PhotoUtils.readBufferFromFile(files[j].getAbsolutePath(), PhotoUtils.checkBufferSize(path, scaleSize));
                Bitmap bitmap = PhotoUtils.fromBufferToBitmap(PhotoUtils.checkFrameWidth(path, scaleSize), PhotoUtils.checkFrameHeight(path, scaleSize), buffer);
                bitmaps.add(bitmap);
            }

        }
        GifItem gifItem = new GifItem(Utils.checkVideoFrameDuration(path, bitmaps.size()), Type.VIDEO);
        gifItem.setCurrentDuration(Utils.checkVideoFrameDuration(path, bitmaps.size()));
        gifItem.setBitmap(Utils.getVideoFirstFrame(path));
        gifItem.setBitmaps(bitmaps);
        gifItems.add(gifItem);
    }

    // When first time MakeGifActivity opens should do this
    class LoadFramesAsyncTask extends AsyncTask<Void, Void, Void> {
        ProgressDialog progressDialog = new ProgressDialog(MakeGifActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            contentType = getIntent().getIntExtra(GifsArtConst.INTENT_ACTIVITY_INDEX, 0);
            if (contentType == GifsArtConst.INDEX_FROM_GALLERY_TO_GIF) {
                selectedItemsArrayList = getIntent().getStringArrayListExtra(GifsArtConst.INTENT_DECODED_IMAGE_PATHS);
                for (int i = 0; i < selectedItemsArrayList.size(); i++) {
                    if (Utils.getMimeType(selectedItemsArrayList.get(i)) == Type.IMAGE) {
                        addImageItem(selectedItemsArrayList.get(i), gifItems);
                    } else if (Utils.getMimeType(selectedItemsArrayList.get(i)) == Type.GIF) {
                        addGifItem(selectedItemsArrayList.get(i), gifItems);
                    } else if (Utils.getMimeType(selectedItemsArrayList.get(i)) == Type.VIDEO) {
                        addVideoItem(selectedItemsArrayList.get(i), getIntent(), gifItems);
                    }
                }
            }
            // Checking index, if selected item is from giphy activity will enter this scope
            if (contentType == GifsArtConst.INDEX_GIPHY_TO_GIF) {
                addGifItem(getIntent().getStringExtra(GifsArtConst.INTENT_GIF_PATH), gifItems);
            }
            // Checking index, if selected item is a video from shooting gif activity will enter this scope
            if (contentType == GifsArtConst.INDEX_SHOOT_GIF) {
                addVideoItem(getIntent().getStringExtra(GifsArtConst.INTENT_VIDEO_PATH), getIntent(), gifItems);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            addPlusButton();

            init();
            progressDialog.dismiss();
            Log.d("gaggagagag", "Frames Count: " + CheckSpaceSingleton.getInstance().getAllocatedSpace());
        }
    }

    // When adding new items to gif should do this
    class AddFramesAsyncTask extends AsyncTask<Intent, Void, Void> {
        ProgressDialog progressDialog = new ProgressDialog(MakeGifActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
            gifImitation.onPause();
            gifItems.remove(gifItems.size() - 1);
        }

        @Override
        protected Void doInBackground(Intent... params) {
            if (params[0].getIntExtra(GifsArtConst.INTENT_ACTIVITY_INDEX, 0) == GifsArtConst.INDEX_FROM_GALLERY_TO_GIF) {
                ArrayList<String> addedItemsArray = params[0].getStringArrayListExtra(GifsArtConst.INTENT_DECODED_IMAGE_PATHS);
                for (int i = 0; i < addedItemsArray.size(); i++) {

                    if (Utils.getMimeType(addedItemsArray.get(i)) == Type.IMAGE) {
                        addImageItem(addedItemsArray.get(i), gifItems);
                    } else if (Utils.getMimeType(addedItemsArray.get(i)) == Type.GIF) {
                        addGifItem(addedItemsArray.get(i), gifItems);
                    } else if (Utils.getMimeType(addedItemsArray.get(i)) == Type.VIDEO) {
                        addVideoItem(addedItemsArray.get(i), params[0], gifItems);
                    }
                    selectedItemsArrayList.add(addedItemsArray.get(i));
                }
            }
            if (params[0].getIntExtra(GifsArtConst.INTENT_ACTIVITY_INDEX, 0) == GifsArtConst.INDEX_GIPHY_TO_GIF) {
                addGifItem(params[0].getStringExtra(GifsArtConst.INTENT_GIF_PATH), gifItems);
            }
            if (params[0].getIntExtra(GifsArtConst.INTENT_ACTIVITY_INDEX, 0) == GifsArtConst.INDEX_SHOOT_GIF) {
                addVideoItem(params[0].getStringExtra(GifsArtConst.INTENT_VIDEO_PATH), params[0], gifItems);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            addPlusButton();
            slideAdapter.notifyDataSetChanged();
            progressDialog.dismiss();
            gifImitation.onResume();
            Log.d("gaggagagag", "Frames Count: " + CheckSpaceSingleton.getInstance().getAllocatedSpace());
        }
    }

    // Adding EffectsAdapter items for each effect
    class ApplyEffectsLayoutEffects extends AsyncTask<EffectsAdapter, Bitmap, Void> {
        EffectsAdapter effectsAdapter;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(EffectsAdapter... params) {
            effectsAdapter = params[0];
            GPUImage gpuImage = new GPUImage(MakeGifActivity.this);
            gpuImage.setImage(gifItems.get(0).getBitmap());
            for (int i = 0; i < filters.filters.size(); i++) {
                gpuImage.setFilter(GPUEffects.createFilterForType(filters.filters.get(i)));
                publishProgress(Utils.scaleCenterCrop(gpuImage.getBitmapWithFilterApplied(), GifsArtConst.GIF_FRAME_SIZE, GifsArtConst.GIF_FRAME_SIZE));
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Bitmap... values) {
            super.onProgressUpdate(values);
            effectsAdapter.addItem(values[0]);
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

        }
    }

    // Set container layout content
    public void setContainerLayout(int resourceId) {
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

    // For each layout separate request code
    public enum RequestCode {

        SELECT_SQUARE_FIT,
        SELECT_EFFECTS,
        SELECT_CLIPART,
        SELECT_MASKS,
        SELECT_TEXT,
        EDIT_TEXT;

        public static RequestCode fromInt(int val) {
            RequestCode[] codes = values();

            if (val < 0 || val >= codes.length) {
                return null;
            } else {
                return values()[val];
            }
        }

        public int toInt() {
            return ordinal();
        }
    }

}
