package com.gifsart.studio.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.decoder.VideoDecoder;
import com.facebook.FacebookSdk;
import com.gifsart.studio.R;
import com.gifsart.studio.adapter.GalleryAdapter;
import com.gifsart.studio.adapter.GalleryItemCategoryAdapter;
import com.gifsart.studio.camera.BurstModeFramesSaving;
import com.gifsart.studio.camera.CameraHelper;
import com.gifsart.studio.camera.CameraPreview;
import com.gifsart.studio.camera.SaveVideoFrames;
import com.gifsart.studio.helper.RecyclerItemClickListener;
import com.gifsart.studio.item.GalleryCategoryItem;
import com.gifsart.studio.item.GalleryItem;
import com.gifsart.studio.utils.AnimatedProgressDialog;
import com.gifsart.studio.utils.CheckFreeSpaceSingleton;
import com.gifsart.studio.utils.GifsArtConst;
import com.gifsart.studio.utils.SpacesItemDecoration;
import com.gifsart.studio.utils.Type;
import com.gifsart.studio.utils.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private RecyclerView galleryItemsRecyclerView;
    private RecyclerView galleryCategoryRecyclerView;
    private ProgressBar progressBar;
    private ViewGroup categoryContainer;
    private ViewGroup galleryContainer;

    private GridLayoutManager gridLayoutManager;
    private RecyclerView.ItemAnimator itemAnimator;

    private GalleryAdapter galleryAdapter;
    private GalleryItemCategoryAdapter galleryItemCategoryAdapter;

    private ArrayList<GalleryItem> imageItemsArrayList = new ArrayList<>();
    private ArrayList<GalleryItem> videoItemsArrayList = new ArrayList<>();
    private ArrayList<GalleryCategoryItem> galleryCategoryItems = new ArrayList<>();

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private boolean containerIsOpened = false;
    private int currentCategoryPosition = 0;

    private Context context;
    private Camera camera;
    private CameraPreview cameraPreview;
    private ProgressBar captureCicrleButtonProgressBar;

    private ImageButton captureButton, switchCameraButton;

    private RelativeLayout cameraPreviewLayout;
    private boolean cameraFront = false;
    private boolean recording = false;

    private ArrayList<byte[]> burstModeFrameBytes = new ArrayList<>();

    private VideoCaptureCountDownTimer videoCaptureCountDownTimer;


    private boolean aspectRatio = true;
    int coverSize = 0;

    private SlidingUpPanelLayout slidingUpPanelLayout;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_CALENDAR
    };

    AnimatedProgressDialog animatedProgressDialog1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        FacebookSdk.sdkInitialize(getApplicationContext());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        verifyStoragePermissions(this);

        Utils.initImageLoader(getApplicationContext());

        ImageLoader.getInstance().clearMemoryCache();
        ImageLoader.getInstance().clearDiskCache();

        context = this;
        sharedPreferences = getApplicationContext().getSharedPreferences(GifsArtConst.SHARED_PREFERENCES, MODE_PRIVATE);

        if (!sharedPreferences.getBoolean(GifsArtConst.SHARED_PREFERENCES_IS_OPENED, false)) {
            Utils.createDir(GifsArtConst.MY_DIR);
            Utils.createDir(GifsArtConst.DIR_GIPHY);
            Utils.createDir(GifsArtConst.DIR_VIDEO_FRAMES);
            Utils.createDir(GifsArtConst.DIR_EDIT_FRAMES);
        }

        init();
        initShooting();

    }

    public void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_CALENDAR);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    public void init() {

        //long heapSize = Runtime.getRuntime().maxMemory();
        //Toast.makeText(this, "" + heapSize, Toast.LENGTH_LONG).show();

        categoryContainer = (ViewGroup) findViewById(R.id.category_container);
        galleryContainer = (ViewGroup) findViewById(R.id.gallery_container);
        //ViewGroup.LayoutParams layoutParams = galleryContainer.getLayoutParams();
        //layoutParams.height = getResources().getDisplayMetrics().heightPixels / 2;
        //galleryContainer.setLayoutParams(layoutParams);
        galleryContainer.setTop(getResources().getDisplayMetrics().heightPixels / 2);

        galleryAdapter = new GalleryAdapter(imageItemsArrayList, this, (int) Utils.getBitmapWidth(this));

        galleryItemsRecyclerView = (RecyclerView) findViewById(R.id.gallery_rec_view);
        progressBar = (ProgressBar) findViewById(R.id.main_activity_progress_bar);
        gridLayoutManager = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        itemAnimator = new DefaultItemAnimator();

        galleryItemsRecyclerView.setHasFixedSize(true);
        galleryItemsRecyclerView.setClipToPadding(true);
        galleryItemsRecyclerView.setLayoutManager(gridLayoutManager);
        galleryItemsRecyclerView.setItemAnimator(itemAnimator);

        galleryItemsRecyclerView.setAdapter(galleryAdapter);
        galleryItemsRecyclerView.addItemDecoration(new SpacesItemDecoration((int) Utils.dpToPixel(2, this)));

        editor = sharedPreferences.edit();

        galleryCategoryRecyclerView = (RecyclerView) findViewById(R.id.category_rec_view);
        galleryCategoryRecyclerView.addItemDecoration(new SpacesItemDecoration((int) Utils.dpToPixel(4, this)));
        galleryCategoryRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false));

        galleryItemCategoryAdapter = new GalleryItemCategoryAdapter(galleryCategoryItems, MainActivity.this);

        galleryCategoryRecyclerView.setAdapter(galleryItemCategoryAdapter);

        galleryCategoryRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                galleryCategoryItems.get(currentCategoryPosition).setIsSelected(false);
                galleryCategoryItems.get(position).setIsSelected(true);
                currentCategoryPosition = position;
                switch (currentCategoryPosition) {
                    case 0:
                        galleryAdapter.setArray(imageItemsArrayList);
                        ((TextView) findViewById(R.id.main_activity_category_title)).setText(galleryCategoryItems.get(currentCategoryPosition).getCategoryTitle());
                        break;
                    case 1:
                        galleryAdapter.setArray(videoItemsArrayList);
                        ((TextView) findViewById(R.id.main_activity_category_title)).setText(galleryCategoryItems.get(currentCategoryPosition).getCategoryTitle());
                        break;
                    case 2:
                        Intent intent = new Intent(MainActivity.this, GiphyActivity.class);
                        if (getSharedPreferences(GifsArtConst.SHARED_PREFERENCES, Context.MODE_PRIVATE).getBoolean(GifsArtConst.SHARED_PREFERENCES_IS_OPENED, false)) {
                            startActivityForResult(intent, GifsArtConst.REQUEST_CODE_SHOOTING_GIF_REOPENED);
                        } else {
                            startActivity(intent);
                        }
                    default:
                        break;
                }
                galleryItemCategoryAdapter.notifyDataSetChanged();
                slideDownContainer();
            }
        }));

        new InitGalleryItems().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        findViewById(R.id.main_activity_toolbar_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
                overridePendingTransition(R.transition.pull_in_left, R.transition.push_out_right);
            }
        });

        findViewById(R.id.main_activity_toolbar_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (galleryAdapter.getSelected().size() > 0) {
                    final AnimatedProgressDialog progressDialog = new AnimatedProgressDialog(MainActivity.this);
                    progressDialog.show();
                    boolean hasVideo = false;
                    ArrayList<String> arrayList = galleryAdapter.getSelected();

                    // if MainActivity is opened for the first time will do this
                    if (!sharedPreferences.getBoolean(GifsArtConst.SHARED_PREFERENCES_IS_OPENED, false)) {
                        for (int i = 0; i < arrayList.size(); i++) {
                            if (Utils.getMimeType(arrayList.get(i)) != null && Utils.getMimeType(arrayList.get(i)) == Type.VIDEO) {
                                sendIntentWithVideo(new Intent(MainActivity.this, MakeGifActivity.class), arrayList.get(i), galleryAdapter, progressDialog, false);
                                hasVideo = true;
                            }
                        }
                        if (!hasVideo) {
                            sendIntentWithoutVideo(new Intent(MainActivity.this, MakeGifActivity.class), galleryAdapter, progressDialog, false);
                        }
                    } else {
                        // if MainActivity is reopened will do this
                        for (int i = 0; i < arrayList.size(); i++) {
                            if (Utils.getMimeType(arrayList.get(i)) != null && Utils.getMimeType(arrayList.get(i)) == Type.VIDEO) {
                                sendIntentWithVideo(new Intent(), arrayList.get(i), galleryAdapter, progressDialog, true);
                                hasVideo = true;
                            }
                        }
                        if (!hasVideo) {
                            sendIntentWithoutVideo(new Intent(), galleryAdapter, progressDialog, true);
                        }
                    }
                } else {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.no_images_selected), Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.main_activity_category_change_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (containerIsOpened) {
                    slideDownContainer();
                } else {
                    setContainerLayout();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GifsArtConst.REQUEST_CODE_SHOOTING_GIF_REOPENED) {
            if (resultCode == RESULT_OK) {
                if (data.getBooleanExtra(GifsArtConst.INTENT_CAMERA_BURST_MODE, false)) {
                    Intent intent = new Intent();
                    intent.putExtra(GifsArtConst.INTENT_ACTIVITY_INDEX, data.getIntExtra(GifsArtConst.INTENT_ACTIVITY_INDEX, GifsArtConst.INDEX_FROM_GALLERY_TO_GIF));
                    intent.putExtra(GifsArtConst.INTENT_FRONT_CAMERA, data.getBooleanExtra(GifsArtConst.INTENT_FRONT_CAMERA, false));
                    intent.putStringArrayListExtra(GifsArtConst.INTENT_DECODED_IMAGE_PATHS, data.getStringArrayListExtra(GifsArtConst.INTENT_DECODED_IMAGE_PATHS));
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    Intent intent = new Intent();
                    intent.putExtra(GifsArtConst.INTENT_ACTIVITY_INDEX, data.getIntExtra(GifsArtConst.INTENT_ACTIVITY_INDEX, GifsArtConst.INDEX_SHOOT_GIF));
                    intent.putExtra(GifsArtConst.INTENT_VIDEO_FRAME_SCALE_SIZE, GifsArtConst.VIDEO_FRAME_SCALE_SIZE);
                    intent.putExtra(GifsArtConst.INTENT_VIDEO_PATH, data.getStringExtra(GifsArtConst.INTENT_VIDEO_PATH));
                    intent.putExtra(GifsArtConst.INTENT_FRONT_CAMERA, data.getBooleanExtra(GifsArtConst.INTENT_FRONT_CAMERA, false));
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        }
        if (requestCode == GifsArtConst.REQUEST_CODE_GIPHY_REOPENED && resultCode == RESULT_OK) {
            Intent intent = new Intent();
            intent.putExtra(GifsArtConst.INTENT_ACTIVITY_INDEX, GifsArtConst.INDEX_GIPHY_TO_GIF);
            intent.putExtra(GifsArtConst.INTENT_GIF_PATH, data.getStringExtra(GifsArtConst.INTENT_GIF_PATH));
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    //Loading all gallery items to recyclerview with AsyncTask
    class InitGalleryItems extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            imageItemsArrayList.addAll(Utils.getGalleryPhotos(MainActivity.this));
            videoItemsArrayList.addAll(Utils.getGalleryVideos(MainActivity.this));

            if (imageItemsArrayList.size() > 1) {
                galleryCategoryItems.add(new GalleryCategoryItem(imageItemsArrayList.get(0).getFilePath(), "Images", imageItemsArrayList.size(), true));
            }
            if (videoItemsArrayList.size() > 1) {
                galleryCategoryItems.add(new GalleryCategoryItem(videoItemsArrayList.get(0).getFilePath(), "Videos", videoItemsArrayList.size(), false));
            }

            GalleryCategoryItem galleryCategoryItem = new GalleryCategoryItem();
            galleryCategoryItem.setThumbnail(BitmapFactory.decodeResource(getResources(), R.drawable.giphy_icon));
            galleryCategoryItem.setIsSelected(false);
            galleryCategoryItem.setCategoryItemsCount(-1);
            galleryCategoryItem.setCategoryTitle("Giphy");
            galleryCategoryItems.add(galleryCategoryItem);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            galleryAdapter.notifyDataSetChanged();
            galleryItemCategoryAdapter.notifyDataSetChanged();
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
        if (!CameraHelper.hasCamera(context)) {
            Toast toast = Toast.makeText(context, "Sorry, your phone does not have a camera!", Toast.LENGTH_LONG);
            toast.show();
            finish();
        }
        if (camera == null) {
            // if the front facing camera does not exist
            if (CameraHelper.findFrontFacingCamera() < 0) {

                Toast.makeText(this, "No front facing camera found.", Toast.LENGTH_LONG).show();
                switchCameraButton.setVisibility(View.GONE);
            }
            camera = Camera.open(CameraHelper.findBackFacingCamera());
            cameraPreview.refreshCamera(camera);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "onPause");
        releaseCamera();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        updateAfterRestart();
    }


    private void releaseCamera() {
        // stop and release camera
        try {
            if (camera != null) {
                cameraPreview.stopRecord();
                camera.stopPreview();
                camera.setPreviewCallback(null);
                //cameraPreview.getHolder().removeCallback(cameraPreview);
                camera.release();
                camera = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendIntentWithoutVideo(Intent intent, GalleryAdapter galleryAdapter, AnimatedProgressDialog progressDialog, boolean isOpened) {
        intent.putExtra(GifsArtConst.INTENT_ACTIVITY_INDEX, GifsArtConst.INDEX_FROM_GALLERY_TO_GIF);
        intent.putStringArrayListExtra(GifsArtConst.INTENT_DECODED_IMAGE_PATHS, galleryAdapter.getSelected());
        galleryAdapter.deselectAll();
        progressDialog.dismiss();
        if (isOpened) {
            setResult(RESULT_OK, intent);
            finish();
        } else {
            startActivity(intent);
        }
    }

    public boolean sendIntentWithVideo(final Intent intent, String path, final GalleryAdapter galleryAdapter, final AnimatedProgressDialog progressDialog, final boolean isOpened) {
        VideoDecoder videoDecoder = new VideoDecoder(MainActivity.this, path, Integer.MAX_VALUE, GifsArtConst.VIDEO_FRAME_SCALE_SIZE, GifsArtConst.VIDEOS_DECODED_FRAMES_DIR);
        videoDecoder.extractVideoFrames();
        videoDecoder.setOnDecodeFinishedListener(new VideoDecoder.OnDecodeFinishedListener() {
            @Override
            public void onFinish(boolean isDone) {
                intent.putExtra(GifsArtConst.INTENT_ACTIVITY_INDEX, GifsArtConst.INDEX_FROM_GALLERY_TO_GIF);
                intent.putExtra(GifsArtConst.INTENT_VIDEO_FRAME_SCALE_SIZE, GifsArtConst.VIDEO_FRAME_SCALE_SIZE);
                intent.putStringArrayListExtra(GifsArtConst.INTENT_DECODED_IMAGE_PATHS, galleryAdapter.getSelected());
                intent.putExtra(GifsArtConst.INTENT_DECODED_IMAGES_OUTPUT_DIR, GifsArtConst.VIDEOS_DECODED_FRAMES_DIR);

                galleryAdapter.deselectAll();
                progressDialog.dismiss();

                if (isOpened) {
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    startActivity(intent);
                }
            }
        });
        return true;
    }

    public void setContainerLayout() {
        findViewById(R.id.main_activity_toolbar_cancel).setVisibility(View.INVISIBLE);
        findViewById(R.id.main_activity_toolbar_next).setVisibility(View.INVISIBLE);
        ViewGroup.LayoutParams params = categoryContainer.getLayoutParams();
        int[] location = new int[2];
        findViewById(R.id.main_activity_toolbar).getLocationOnScreen(location);
        params.height = (int) (getResources().getDisplayMetrics().heightPixels - location[1] - findViewById(R.id.main_activity_toolbar).getHeight());
        categoryContainer.setLayoutParams(params);

        containerIsOpened = true;
        com.gifsart.studio.utils.AnimationUtils.rotateAnimation(findViewById(R.id.main_activity_up_down_image), 0, 180);
        TranslateAnimation anim = new TranslateAnimation(0, 0, getResources().getDisplayMetrics().heightPixels, findViewById(R.id.main_activity_toolbar).getBottom());
        anim.setDuration(200);
        anim.setFillAfter(false);
        categoryContainer.setVisibility(View.VISIBLE);
        findViewById(R.id.category_rec_view).setVisibility(View.VISIBLE);
        categoryContainer.startAnimation(anim);
    }

    public void slideDownContainer() {
        containerIsOpened = false;
        findViewById(R.id.main_activity_toolbar_cancel).setVisibility(View.VISIBLE);
        findViewById(R.id.main_activity_toolbar_next).setVisibility(View.VISIBLE);
        com.gifsart.studio.utils.AnimationUtils.rotateAnimation(findViewById(R.id.main_activity_up_down_image), 180, 0);
        TranslateAnimation anim = new TranslateAnimation(0, 0, findViewById(R.id.main_activity_toolbar).getBottom(), getResources().getDisplayMetrics().heightPixels);
        anim.setDuration(200);
        anim.setFillAfter(true);
        categoryContainer.setVisibility(View.VISIBLE);
        categoryContainer.startAnimation(anim);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                categoryContainer.setVisibility(View.GONE);
                findViewById(R.id.category_rec_view).setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    /**
     *
     *
     *
     *
     *
     *
     *
     *
     *
     */

    private void initShooting() {

        animatedProgressDialog1 = new AnimatedProgressDialog(this);
        cameraPreviewLayout = (RelativeLayout) findViewById(R.id.camera_preview);
        cameraPreview = new CameraPreview(context, camera);

        captureButton = (ImageButton) findViewById(R.id.button_capture);
        captureButton.setOnTouchListener(captrureListener);
        captureButton.setOnLongClickListener(onLongClickListener);

        switchCameraButton = (ImageButton) findViewById(R.id.switch_camera_button);
        switchCameraButton.setOnClickListener(switchCameraListener);

        cameraPreviewLayout.addView(cameraPreview);

        captureCicrleButtonProgressBar = (ProgressBar) findViewById(R.id.circle_progress_bar);

        findViewById(R.id.burst_mode_image_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //burstMode = burstModeCounts.get((burstModeCounts.indexOf(burstMode) + 1) % burstModeCounts.size());
                //((TextView) findViewById(R.id.burst_mode_count)).setText("x" + burstMode);
                CameraHelper.setNextBurstMode(MainActivity.this);
            }
        });

        findViewById(R.id.flash_light_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!cameraFront) {
                    CameraHelper.setNextFlashLightMode(MainActivity.this, camera);
                }
            }
        });

        findViewById(R.id.aspect_ratio).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (coverSize == 0) {
                    int x = getResources().getDisplayMetrics().heightPixels - slidingUpPanelLayout.getTop() - (getResources().getDisplayMetrics().widthPixels * 4 / 3) + findViewById(R.id.capture_container).getHeight();
                    slidingUpPanelLayout.setPanelHeight(x);
                }

                if (aspectRatio) {
                    ((TextView) findViewById(R.id.aspect_ratio)).setText(getString(R.string.aspect_ratio_1x1));
                    slidingUpPanelLayout.setAnchorPoint(0.4f);
                    slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
                    aspectRatio = false;
                } else {
                    ((TextView) findViewById(R.id.aspect_ratio)).setText(getString(R.string.aspect_ratio_3x4));
                    slidingUpPanelLayout.setAnchorPoint(1.0f);
                    slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                    aspectRatio = true;
                }
            }
        });

        slidingUpPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        slidingUpPanelLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                if (slidingUpPanelLayout.getAnchorPoint() == 1.0f) {
                    //findViewById(R.id.capture_container).setVisibility(View.GONE);
                    //findViewById(R.id.invis).setVisibility(View.VISIBLE);
                    slidingUpPanelLayout.setAnchorPoint(0.5f);
                    slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
                } else if (slidingUpPanelLayout.getAnchorPoint() == 0.5f) {
                    slidingUpPanelLayout.setAnchorPoint(0.3f);
                    slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
                } else if (slidingUpPanelLayout.getAnchorPoint() == 0.3f) {
                    slidingUpPanelLayout.setAnchorPoint(0.5f);
                    slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
                }
            }

            @Override
            public void onPanelCollapsed(View panel) {
                Log.d("gag2", panel.getTop() + "");
            }

            @Override
            public void onPanelExpanded(View panel) {

            }

            @Override
            public void onPanelAnchored(View panel) {
                Log.d("gag4", panel.getTop() + "");
            }

            @Override
            public void onPanelHidden(View panel) {
                Log.d("gag5", panel.getTop() + "");
            }
        });

    }

    public void chooseCamera() {
        // if the camera preview is the front
        if (cameraFront) {
            int cameraId = CameraHelper.findBackFacingCamera();
            if (cameraId >= 0) {
                // open the backFacingCamera
                // set a picture callback
                // refresh the preview

                cameraFront = false;
                camera = Camera.open(cameraId);
                // mPicture = getPictureCallback();
                cameraPreview.refreshCamera(camera);
            }
        } else {
            int cameraId = CameraHelper.findFrontFacingCamera();
            if (cameraId >= 0) {
                // open the backFacingCamera
                // set a picture callback
                // refresh the preview

                cameraFront = true;
                camera = Camera.open(cameraId);
                // mPicture = getPictureCallback();
                cameraPreview.refreshCamera(camera);
            }
        }
    }

    View.OnClickListener switchCameraListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // get the number of cameras
            if (!recording) {
                int camerasNumber = Camera.getNumberOfCameras();
                if (camerasNumber > 1) {
                    // release the old camera instance
                    // switch camera, from the front and the corner and vice versa
                    releaseCamera();
                    chooseCamera();
                } else {
                    Toast toast = Toast.makeText(context, "Sorry, your phone has only one camera!", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        }
    };

    View.OnTouchListener captrureListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    break;
                case MotionEvent.ACTION_UP:
                    if (recording) {
                        captureButton.setOnTouchListener(null);
                        captureButton.setOnLongClickListener(null);
                        visibilitySwitcher(false);
                        cameraPreview.stopRecord();
                        animatedProgressDialog1.show();
                        recording = false;
                        videoCaptureCountDownTimer.cancel();
                        captureCicrleButtonProgressBar.setVisibility(View.INVISIBLE);

                        /*if (CheckFreeSpaceSingleton.getInstance().haveEnoughSpace(GifsArtConst.SHOOTING_VIDEO_OUTPUT_DIR + "/" + GifsArtConst.VIDEO_NAME)) {
                            saveCapturedVideoFrames();
                        } else {
                            setResult(RESULT_CANCELED);
                            finish();
                            Toast.makeText(context, "No enough space", Toast.LENGTH_SHORT).show();
                        }*/

                    } else {
                        //Utils.clearDir(new File(Environment.getExternalStorageDirectory() + "/" + GifsArtConst.DIR_VIDEO_FRAMES));
                        findViewById(R.id.burst_mode_image_container).setOnClickListener(null);
                        captureButton.setOnTouchListener(null);
                        captureButton.setOnLongClickListener(null);
                        int n = CameraHelper.getCurrentBurstMode();
                        visibilitySwitcher(true);
                        burstModeRecursion(n);
                    }
                default:
                    return false;
            }
            return false;
        }
    };

    View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            //Utils.clearDir(new File(Environment.getExternalStorageDirectory() + "/" + GifsArtConst.DIR_VIDEO_FRAMES));
            recording = true;
            visibilitySwitcher(true);
            cameraPreview.setRenderingListener(new CameraPreview.StartRendering() {
                @Override
                public void toStart(boolean start) {
                    SaveVideoFrames saveVideoFrames = new SaveVideoFrames(context, cameraFront);
                    saveVideoFrames.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    saveVideoFrames.setOnFramesSavedListener(new SaveVideoFrames.FramesAreSaved() {
                        @Override
                        public void framesAreSaved(boolean done) {
                            animatedProgressDialog1.dismiss();
                            if (!sharedPreferences.getBoolean(GifsArtConst.SHARED_PREFERENCES_IS_OPENED, false)) {
                                sendCapturedVideoFramesWithIntent(new Intent(MainActivity.this, MakeGifActivity.class));
                            } else {
                                sendCapturedVideoFramesWithIntent(new Intent());
                            }
                        }
                    });
                }
            });
            cameraPreview.startRecord();
            videoCaptureCountDownTimer = new VideoCaptureCountDownTimer(7100, 1000);
            videoCaptureCountDownTimer.start();
            captureCicrleButtonProgressBar.setProgress(0);
            captureCicrleButtonProgressBar.setVisibility(View.VISIBLE);
            return false;
        }
    };

    public void saveBurstModeFrames() {
        final AnimatedProgressDialog animatedProgressDialog = new AnimatedProgressDialog(MainActivity.this);
        animatedProgressDialog.show();
        BurstModeFramesSaving burstModeFramesSaving = new BurstModeFramesSaving(this, cameraFront, burstModeFrameBytes);
        burstModeFramesSaving.setFramesSavedListener(new BurstModeFramesSaving.FramesAreSaved() {
            @Override
            public void framesAreSaved(boolean done) {
                if (done) {
                    ArrayList<String> strings = new ArrayList<>();
                    for (int i = 0; i < CameraHelper.getCurrentBurstMode(); i++) {
                        strings.add(Environment.getExternalStorageDirectory() + "/" + GifsArtConst.DIR_VIDEO_FRAMES + "/img_" + i);
                    }
                    if (!sharedPreferences.getBoolean(GifsArtConst.SHARED_PREFERENCES_IS_OPENED, false)) {
                        Intent intent = new Intent(MainActivity.this, MakeGifActivity.class);
                        intent.putExtra(GifsArtConst.INTENT_ACTIVITY_INDEX, GifsArtConst.INDEX_SHOOT_GIF);
                        intent.putExtra(GifsArtConst.INTENT_CAMERA_BURST_MODE, true);
                        intent.putExtra(GifsArtConst.INTENT_FRONT_CAMERA, cameraFront);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent();
                        intent.putExtra(GifsArtConst.INTENT_ACTIVITY_INDEX, GifsArtConst.INDEX_SHOOT_GIF);
                        intent.putExtra(GifsArtConst.INTENT_CAMERA_BURST_MODE, true);
                        intent.putExtra(GifsArtConst.INTENT_FRONT_CAMERA, cameraFront);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                    CheckFreeSpaceSingleton.getInstance().addAllocatedSpaceInt(CameraHelper.getCurrentBurstMode());
                    animatedProgressDialog.dismiss();
                }
            }
        });
        burstModeFramesSaving.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void sendCapturedVideoFramesWithIntent(Intent intent) {
        intent.putExtra(GifsArtConst.INTENT_ACTIVITY_INDEX, GifsArtConst.INDEX_SHOOT_GIF);
        intent.putExtra(GifsArtConst.INTENT_FRONT_CAMERA, cameraFront);
        intent.putExtra(GifsArtConst.INTENT_CAMERA_BURST_MODE, false);
        if (!sharedPreferences.getBoolean(GifsArtConst.SHARED_PREFERENCES_IS_OPENED, false)) {
            startActivity(intent);
        } else {
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    public void burstModeRecursion(final int n) {
        if (n == 0) {
            findViewById(R.id.burst_counter).setVisibility(View.INVISIBLE);
            visibilitySwitcher(false);
            if (CheckFreeSpaceSingleton.getInstance().haveEnoughSpaceInt(CameraHelper.getCurrentBurstMode())) {
                saveBurstModeFrames();
            } else {
                setResult(RESULT_CANCELED);
                finish();
                Toast.makeText(MainActivity.this, "No enough space", Toast.LENGTH_SHORT).show();
            }
            return;
        } else {
            camera.takePicture(null, null, new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] data, Camera camera) {
                    camera.startPreview();
                    burstModeFrameBytes.add(data);
                    ((TextView) findViewById(R.id.burst_counter)).setText(n + "");
                    burstModeRecursion(n - 1);
                }
            });
            camera.startPreview();
        }
    }


    public class VideoCaptureCountDownTimer extends CountDownTimer {
        public VideoCaptureCountDownTimer(long startTime, long interval) {
            super(startTime, interval);
        }

        @Override
        public void onFinish() {
        }

        @Override
        public void onTick(long millisUntilFinished) {
            ((TextView) findViewById(R.id.capture_time)).setText("00:0" + ((millisUntilFinished / 1000) - 1));
            Log.d(LOG_TAG, "time: " + millisUntilFinished);
            if (millisUntilFinished <= 2000) {
                if (recording) {
                    captureButton.setOnTouchListener(null);
                    captureButton.setOnLongClickListener(null);
                    recording = false;
                    cameraPreview.stopRecord();
                    animatedProgressDialog1.show();
                    captureCicrleButtonProgressBar.setVisibility(View.INVISIBLE);

                    /*if (!sharedPreferences.getBoolean(GifsArtConst.SHARED_PREFERENCES_IS_OPENED, false)) {
                        sendCapturedVideoFramesWithIntent(new Intent(MainActivity.this, MakeGifActivity.class));
                    } else {
                        sendCapturedVideoFramesWithIntent(new Intent());
                    }*/
                    //if (CheckFreeSpaceSingleton.getInstance().haveEnoughSpace(GifsArtConst.SHOOTING_VIDEO_OUTPUT_DIR + "/" + GifsArtConst.VIDEO_NAME)) {
                    //saveCapturedVideoFrames();
                    /*} else {
                        setResult(RESULT_CANCELED);
                        finish();
                        Toast.makeText(context, "No enough space", Toast.LENGTH_SHORT).show();
                    }*/
                }
            }
        }
    }

    private void updateAfterRestart() {
        cameraFront = false;
        captureButton.setOnTouchListener(captrureListener);
        captureButton.setOnLongClickListener(onLongClickListener);
        currentCategoryPosition = 0;
        CameraHelper.resetBurstMode(MainActivity.this);
        ((TextView) findViewById(R.id.capture_time)).setText("00:06");
        visibilitySwitcher(false);
    }

    private void visibilitySwitcher(boolean isBurstMode) {
        if (isBurstMode) {
            findViewById(R.id.burst_mode_image_container).setVisibility(View.INVISIBLE);
            findViewById(R.id.flash_light_button).setVisibility(View.INVISIBLE);
            findViewById(R.id.aspect_ratio).setVisibility(View.INVISIBLE);
            switchCameraButton.setVisibility(View.INVISIBLE);
            if (recording) {
                findViewById(R.id.capture_time).setVisibility(View.VISIBLE);
            }
        } else {
            switchCameraButton.setVisibility(View.VISIBLE);
            findViewById(R.id.burst_mode_image_container).setVisibility(View.VISIBLE);
            findViewById(R.id.flash_light_button).setVisibility(View.VISIBLE);
            findViewById(R.id.aspect_ratio).setVisibility(View.VISIBLE);
            findViewById(R.id.capture_time).setVisibility(View.INVISIBLE);
        }
    }

}
