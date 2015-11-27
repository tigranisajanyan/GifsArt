package com.gifsart.studio.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.decoder.VideoDecoder;
import com.facebook.FacebookSdk;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.gifsart.studio.R;
import com.gifsart.studio.adapter.GalleryAdapter;
import com.gifsart.studio.adapter.GalleryItemCategoryAdapter;
import com.gifsart.studio.camera.BurstModeFramesSaving;
import com.gifsart.studio.camera.CameraPreview;
import com.gifsart.studio.helper.RecyclerItemClickListener;
import com.gifsart.studio.item.GalleryCategoryItem;
import com.gifsart.studio.item.GalleryItem;
import com.gifsart.studio.social.UploadImageToPicsart;
import com.gifsart.studio.utils.AnimatedProgressDialog;
import com.gifsart.studio.utils.CheckFreeSpaceSingleton;
import com.gifsart.studio.utils.GifsArtConst;
import com.gifsart.studio.utils.SpacesItemDecoration;
import com.gifsart.studio.utils.Type;
import com.gifsart.studio.utils.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView galleryItemsRecyclerView;
    private RecyclerView galleryCategoryRecyclerView;
    private ProgressBar progressBar;
    private ViewGroup container;
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
    private MediaRecorder mediaRecorder;
    private ProgressBar captureCicrleButtonProgressBar;

    private ImageButton captureButton, screenModeBtn, switchCameraButton;

    private LinearLayout cameraPreviewLayout;
    private boolean cameraFront = false;
    private boolean recording = false;

    private ArrayList<Integer> burstModeCounts = new ArrayList();

    private ArrayList<byte[]> burstModeFrameBytes = new ArrayList<>();

    private VideoCaptureCountDownTimer videoCaptureCountDownTimer;

    public String[] flashLightModes = new String[]{
            Camera.Parameters.FLASH_MODE_OFF,
            Camera.Parameters.FLASH_MODE_TORCH,
            Camera.Parameters.FLASH_MODE_AUTO
    };

    private String flashMode = flashLightModes[0];
    private int burstMode = 5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        FacebookSdk.sdkInitialize(getApplicationContext());
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

        context = this;
        sharedPreferences = getApplicationContext().getSharedPreferences(GifsArtConst.SHARED_PREFERENCES, MODE_PRIVATE);

        burstModeCounts.add(5);
        burstModeCounts.add(10);
        burstModeCounts.add(15);

        init();
        initShooting();

    }

    public void init() {

        container = (ViewGroup) findViewById(R.id.main_activity_container);
        galleryContainer = (ViewGroup) findViewById(R.id.gallery_container);
        ViewGroup.LayoutParams layoutParams = galleryContainer.getLayoutParams();
        layoutParams.height = getResources().getDisplayMetrics().heightPixels / 2;
        galleryContainer.setLayoutParams(layoutParams);

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
                //galleryAdapter.deselectAll();
                /*String path = imageItemsArrayList.get(10).getFilePath();
                UploadImageToPicsart uploadImageToPicsart = new UploadImageToPicsart(path, UploadImageToPicsart.PHOTO_IS.AVATAR);
                uploadImageToPicsart.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);*/
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);

            }
        });

        findViewById(R.id.main_activity_toolbar_done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (galleryAdapter.getSelected().size() > 0) {

                    final AnimatedProgressDialog progressDialog = new AnimatedProgressDialog(MainActivity.this);
                    progressDialog.setCancelable(false);
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
                galleryCategoryItems.add(new GalleryCategoryItem(imageItemsArrayList.get(2).getFilePath(), "Images", imageItemsArrayList.size(), true));
            }
            if (videoItemsArrayList.size() > 1) {
                galleryCategoryItems.add(new GalleryCategoryItem(videoItemsArrayList.get(2).getFilePath(), "Videos", videoItemsArrayList.size(), false));
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
        if (!hasCamera(context)) {
            Toast toast = Toast.makeText(context, "Sorry, your phone does not have a camera!", Toast.LENGTH_LONG);
            toast.show();
            finish();
        }
        if (camera == null) {
            // if the front facing camera does not exist
            if (findFrontFacingCamera() < 0) {

                Toast.makeText(this, "No front facing camera found.", Toast.LENGTH_LONG).show();
                switchCameraButton.setVisibility(View.GONE);
            }
            camera = Camera.open(findBackFacingCamera());
            cameraPreview.refreshCamera(camera);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
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
        File file = new File(GifsArtConst.VIDEOS_DECODED_FRAMES_DIR);
        file.mkdirs();

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
        findViewById(R.id.main_activity_toolbar_done).setVisibility(View.INVISIBLE);
        ViewGroup.LayoutParams params = container.getLayoutParams();
        int[] location = new int[2];
        findViewById(R.id.main_activity_toolbar).getLocationOnScreen(location);
        params.height = (int) (getResources().getDisplayMetrics().heightPixels - location[1] - findViewById(R.id.main_activity_toolbar).getHeight());
        container.setLayoutParams(params);

        containerIsOpened = true;
        com.gifsart.studio.utils.AnimationUtils.rotateAnimation(findViewById(R.id.main_activity_up_down_image), 0, 180);
        TranslateAnimation anim = new TranslateAnimation(0, 0, getResources().getDisplayMetrics().heightPixels, findViewById(R.id.main_activity_toolbar).getBottom());
        anim.setDuration(200);
        anim.setFillAfter(false);
        container.setVisibility(View.VISIBLE);
        findViewById(R.id.category_rec_view).setVisibility(View.VISIBLE);
        container.startAnimation(anim);
    }

    public void slideDownContainer() {
        containerIsOpened = false;
        findViewById(R.id.main_activity_toolbar_cancel).setVisibility(View.VISIBLE);
        findViewById(R.id.main_activity_toolbar_done).setVisibility(View.VISIBLE);
        com.gifsart.studio.utils.AnimationUtils.rotateAnimation(findViewById(R.id.main_activity_up_down_image), 180, 0);
        TranslateAnimation anim = new TranslateAnimation(0, 0, findViewById(R.id.main_activity_toolbar).getBottom(), getResources().getDisplayMetrics().heightPixels);
        anim.setDuration(200);
        anim.setFillAfter(true);
        container.setVisibility(View.VISIBLE);
        container.startAnimation(anim);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                container.setVisibility(View.GONE);
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
     */

    private void initShooting() {
        cameraPreviewLayout = (LinearLayout) findViewById(R.id.camera_preview);
        cameraPreview = new CameraPreview(context, camera);

        captureButton = (ImageButton) findViewById(R.id.button_capture);
        captureButton.setOnTouchListener(captrureListener);
        captureButton.setOnLongClickListener(onLongClickListener);

        switchCameraButton = (ImageButton) findViewById(R.id.switch_camera_button);
        switchCameraButton.setOnClickListener(switchCameraListener);

        cameraPreviewLayout.addView(cameraPreview);

        screenModeBtn = (ImageButton) findViewById(R.id.screen_mode);

        captureCicrleButtonProgressBar = (ProgressBar) findViewById(R.id.circle_progress_bar);

        findViewById(R.id.burst_mode_image_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                burstMode = burstModeCounts.get((burstModeCounts.indexOf(burstMode) + 1) % burstModeCounts.size());
                ((TextView) findViewById(R.id.burst_mode_count)).setText("x" + burstMode);

            }
        });

        findViewById(R.id.flash_light_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!cameraFront) {
                    setNextFlashLightMode(flashMode, camera);
                }
            }
        });

        findViewById(R.id.capture_button_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int a = findViewById(R.id.gallery_container).getTop();
                int b = findViewById(R.id.main_activity_toolbar).getBottom() + getResources().getDisplayMetrics().widthPixels * 4 / 3;
                Log.d("gaga", findViewById(R.id.main_activity_toolbar).getBottom() + "");
                Log.d("gaga", a + "/" + b);
                TranslateAnimation anim = new TranslateAnimation(0, 0, a, 100);
                anim.setDuration(2000);
                anim.setFillAfter(true);
                findViewById(R.id.gallery_container).startAnimation(anim);
            }
        });
    }

    private int findFrontFacingCamera() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                cameraFront = true;
                break;
            }
        }
        return cameraId;
    }

    private int findBackFacingCamera() {
        int cameraId = -1;
        // Search for the corner facing camera
        // get the number of cameras
        int numberOfCameras = Camera.getNumberOfCameras();
        // for every camera check
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i;
                cameraFront = false;
                break;
            }
        }
        return cameraId;
    }

    private void releaseMediaRecorder() {
        if (mediaRecorder != null) {//TODO add other expression (&&)
            mediaRecorder.reset(); // clear recorder configuration
            mediaRecorder.release(); // release the recorder object
            mediaRecorder = null;
            camera.lock(); // lock camera for later use
        }
    }

    private boolean prepareMediaRecorder() {
        File file = new File(GifsArtConst.SHOOTING_VIDEO_OUTPUT_DIR, GifsArtConst.VIDEO_NAME);
        camera.unlock();

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setCamera(camera);
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
        if (profile.videoFrameHeight > 720) {
            mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_720P));
        } else {
            mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
        }
        mediaRecorder.setOrientationHint(GifsArtConst.VIDEO_OUTPUT_ORIENTATION);
        mediaRecorder.setOutputFile(file.getAbsolutePath());
        mediaRecorder.setMaxDuration(GifsArtConst.VIDEO_MAX_DURATION); // Set max duration 30 sec.
        mediaRecorder.setMaxFileSize(GifsArtConst.VIDEO_FILE_MAX_SIZE); // Set max file size 40M

        try {
            mediaRecorder.prepare();
        } catch (IllegalStateException e) {
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            releaseMediaRecorder();
            return false;
        }
        return true;
    }

    public boolean hasCamera(Context context) {
        // check if the device has camera
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }

    public void chooseCamera() {
        // if the camera preview is the front
        if (cameraFront) {
            int cameraId = findBackFacingCamera();
            if (cameraId >= 0) {
                // open the backFacingCamera
                // set a picture callback
                // refresh the preview

                camera = Camera.open(cameraId);
                // mPicture = getPictureCallback();
                cameraPreview.refreshCamera(camera);
            }
        } else {
            int cameraId = findFrontFacingCamera();
            if (cameraId >= 0) {
                // open the backFacingCamera
                // set a picture callback
                // refresh the preview

                camera = Camera.open(cameraId);
                // mPicture = getPictureCallback();
                cameraPreview.refreshCamera(camera);
            }
        }
    }

    private void releaseCamera() {
        // stop and release camera
        if (camera != null) {
            camera.release();
            camera = null;
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
                    boolean finish = false;
                    if (recording) {
                        try {
                            mediaRecorder.stop(); // stop the recording
                        } catch (Exception e) {
                            // when mediaRecorder haven't prepared yet, will thraw an exaption which we catch here
                            finish = true;
                            recording = false;
                            cameraPreview.refreshCamera(camera);
                            videoCaptureCountDownTimer.cancel();
                            captureCicrleButtonProgressBar.setVisibility(View.GONE);
                        }
                        if (!finish) {
                            captureButton.setOnTouchListener(null);
                            captureButton.setOnLongClickListener(null);
                            releaseMediaRecorder(); // release the MediaRecorder object
                            Toast.makeText(MainActivity.this, "Video captured!", Toast.LENGTH_LONG).show();
                            recording = false;
                            videoCaptureCountDownTimer.cancel();
                            captureCicrleButtonProgressBar.setVisibility(View.INVISIBLE);
                            if (CheckFreeSpaceSingleton.getInstance().haveEnoughSpace(GifsArtConst.SHOOTING_VIDEO_OUTPUT_DIR + "/" + GifsArtConst.VIDEO_NAME)) {
                                saveCapturedVideoFrames();
                            } else {
                                setResult(RESULT_CANCELED);
                                finish();
                                Toast.makeText(context, "No enough space", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        findViewById(R.id.burst_mode_image_container).setOnClickListener(null);
                        captureButton.setOnTouchListener(null);
                        captureButton.setOnLongClickListener(null);
                        int n = burstMode;
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
            if (!prepareMediaRecorder()) {
                Toast.makeText(MainActivity.this, "Fail in prepareMediaRecorder()!\n - Ended -", Toast.LENGTH_LONG).show();
                finish();
            }
            mediaRecorder.start();
            recording = true;
            videoCaptureCountDownTimer = new VideoCaptureCountDownTimer(7100, 1000);
            videoCaptureCountDownTimer.start();
            captureCicrleButtonProgressBar.setProgress(0);
            captureCicrleButtonProgressBar.setVisibility(View.VISIBLE);
            return false;
        }
    };

    public void saveCapturedVideoFrames() {
        final AnimatedProgressDialog animatedProgressDialog = new AnimatedProgressDialog(MainActivity.this);
        animatedProgressDialog.setCancelable(false);
        animatedProgressDialog.show();
        VideoDecoder videoDecoder = new VideoDecoder(MainActivity.this, GifsArtConst.SHOOTING_VIDEO_OUTPUT_DIR + "/" + GifsArtConst.VIDEO_NAME, Integer.MAX_VALUE, GifsArtConst.VIDEO_FRAME_SCALE_SIZE, GifsArtConst.VIDEOS_DECODED_FRAMES_DIR);
        videoDecoder.extractVideoFrames();
        videoDecoder.setOnDecodeFinishedListener(new VideoDecoder.OnDecodeFinishedListener() {
            @Override
            public void onFinish(boolean isDone) {
                if (!sharedPreferences.getBoolean(GifsArtConst.SHARED_PREFERENCES_IS_OPENED, false)) {
                    sendCapturedVideoFramesWithIntent(new Intent(MainActivity.this, MakeGifActivity.class));
                } else {
                    sendCapturedVideoFramesWithIntent(new Intent());
                }
                File file = new File(GifsArtConst.VIDEOS_DECODED_FRAMES_DIR);
                CheckFreeSpaceSingleton.getInstance().addAllocatedSpaceInt(file.listFiles().length * 2 / 3);
                animatedProgressDialog.dismiss();
                finish();
            }
        });
    }

    public void saveBurstModeFrames() {
        final AnimatedProgressDialog animatedProgressDialog = new AnimatedProgressDialog(MainActivity.this);
        animatedProgressDialog.setCancelable(false);
        animatedProgressDialog.show();
        BurstModeFramesSaving burstModeFramesSaving = new BurstModeFramesSaving(burstModeFrameBytes);
        burstModeFramesSaving.setFramesSavedListener(new BurstModeFramesSaving.FramesSaved() {
            @Override
            public void done(boolean done) {
                if (done) {
                    ArrayList<String> strings = new ArrayList<>();
                    for (int i = 0; i < burstMode; i++) {
                        strings.add(Environment.getExternalStorageDirectory() + "/GifsArt/video_frames/img_" + i + ".jpg");
                    }
                    if (!sharedPreferences.getBoolean(GifsArtConst.SHARED_PREFERENCES_IS_OPENED, false)) {
                        Intent intent = new Intent(MainActivity.this, MakeGifActivity.class);
                        intent.putExtra(GifsArtConst.INTENT_ACTIVITY_INDEX, GifsArtConst.INDEX_FROM_GALLERY_TO_GIF);
                        intent.putStringArrayListExtra(GifsArtConst.INTENT_DECODED_IMAGE_PATHS, strings);
                        intent.putExtra(GifsArtConst.INTENT_CAMERA_BURST_MODE, true);
                        intent.putExtra(GifsArtConst.INTENT_FRONT_CAMERA, cameraFront);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent();
                        intent.putExtra(GifsArtConst.INTENT_ACTIVITY_INDEX, GifsArtConst.INDEX_FROM_GALLERY_TO_GIF);
                        intent.putStringArrayListExtra(GifsArtConst.INTENT_DECODED_IMAGE_PATHS, strings);
                        intent.putExtra(GifsArtConst.INTENT_CAMERA_BURST_MODE, true);
                        intent.putExtra(GifsArtConst.INTENT_FRONT_CAMERA, cameraFront);
                        setResult(RESULT_OK, intent);
                    }
                    CheckFreeSpaceSingleton.getInstance().addAllocatedSpaceInt(burstMode);
                    animatedProgressDialog.dismiss();
                    finish();
                }
            }
        });
        burstModeFramesSaving.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void sendCapturedVideoFramesWithIntent(Intent intent) {
        intent.putExtra(GifsArtConst.INTENT_ACTIVITY_INDEX, GifsArtConst.INDEX_SHOOT_GIF);
        intent.putExtra(GifsArtConst.INTENT_FRONT_CAMERA, cameraFront);
        intent.putExtra(GifsArtConst.INTENT_VIDEO_FRAME_SCALE_SIZE, GifsArtConst.VIDEO_FRAME_SCALE_SIZE);
        intent.putExtra(GifsArtConst.INTENT_VIDEO_PATH, GifsArtConst.SHOOTING_VIDEO_OUTPUT_DIR + "/" + GifsArtConst.VIDEO_NAME);
        intent.putExtra(GifsArtConst.INTENT_CAMERA_BURST_MODE, false);
        if (!sharedPreferences.getBoolean(GifsArtConst.SHARED_PREFERENCES_IS_OPENED, false)) {
            startActivity(intent);
        } else {
            setResult(RESULT_OK, intent);
        }
    }

    public void burstModeRecursion(final int n) {
        if (n == 0) {
            findViewById(R.id.burst_counter).setVisibility(View.INVISIBLE);
            if (CheckFreeSpaceSingleton.getInstance().haveEnoughSpaceInt(burstMode)) {
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

    public void setNextFlashLightMode(String currentFlashMode, Camera camera) {
        Camera.Parameters cameraParams = camera.getParameters();
        ImageButton flashLightSwitchButton = (ImageButton) findViewById(R.id.flash_light_button);
        for (int i = 0; i < flashLightModes.length; i++) {
            if (currentFlashMode.equals(flashLightModes[i])) {
                int nextIndex = (i + 1) % flashLightModes.length;
                flashMode = flashLightModes[nextIndex];
                cameraParams.setFlashMode(flashMode);
                camera.setParameters(cameraParams);
                switch (nextIndex) {
                    case 0:
                        flashLightSwitchButton.setImageDrawable(getResources().getDrawable(R.drawable.flash_light_off));
                        break;
                    case 1:
                        flashLightSwitchButton.setImageDrawable(getResources().getDrawable(R.drawable.flash_light_on));
                        break;
                    case 2:
                        flashLightSwitchButton.setImageDrawable(getResources().getDrawable(R.drawable.flash_light_auto));
                        break;
                    default:
                        break;
                }
            }
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
            Log.d("gagag", "" + millisUntilFinished);
            if (millisUntilFinished <= 2000) {
                if (recording) {
                    try {
                        mediaRecorder.stop(); // stop the recording
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    captureButton.setOnTouchListener(null);
                    captureButton.setOnLongClickListener(null);
                    releaseMediaRecorder(); // release the MediaRecorder object
                    recording = false;
                    captureCicrleButtonProgressBar.setVisibility(View.INVISIBLE);
                    if (CheckFreeSpaceSingleton.getInstance().haveEnoughSpace(GifsArtConst.SHOOTING_VIDEO_OUTPUT_DIR + "/" + GifsArtConst.VIDEO_NAME)) {
                        saveCapturedVideoFrames();
                    } else {
                        setResult(RESULT_CANCELED);
                        finish();
                        Toast.makeText(context, "No enough space", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

}
