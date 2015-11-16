package com.gifsart.studio.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.decoder.VideoDecoder;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.gifsart.studio.R;
import com.gifsart.studio.adapter.GalleryAdapter;
import com.gifsart.studio.adapter.GalleryItemCategoryAdapter;
import com.gifsart.studio.helper.RecyclerItemClickListener;
import com.gifsart.studio.item.GalleryCategoryItem;
import com.gifsart.studio.item.GalleryItem;
import com.gifsart.studio.utils.AnimatedProgressDialog;
import com.gifsart.studio.utils.GifsArtConst;
import com.gifsart.studio.utils.SpacesItemDecoration;
import com.gifsart.studio.utils.Type;
import com.gifsart.studio.utils.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView galleryItemsRecyclerView;
    private RecyclerView galleryCategoryRecyclerView;
    private ProgressBar progressBar;
    private ViewGroup container;

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

        container = (ViewGroup) findViewById(R.id.main_activity_container);
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

        sharedPreferences = getApplicationContext().getSharedPreferences(GifsArtConst.SHARED_PREFERENCES, MODE_PRIVATE);
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
                galleryAdapter.deselectAll();
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

        /*GalleryItem galleryItem1 = new GalleryItem(BitmapFactory.decodeResource(getResources(), R.drawable.camera_icon));
        galleryItem1.setIsSeleted(false);
        GalleryItem galleryItem2 = new GalleryItem(BitmapFactory.decodeResource(getResources(), R.drawable.giphy_icon));
        galleryItem2.setIsSeleted(false);

        imageItemsArrayList.add(galleryItem1);
        imageItemsArrayList.add(galleryItem2);

        PhoneImagesRetriever retriever = new PhoneImagesRetriever(getLoaderManager(), getApplicationContext());
        retriever.retrieveImages(new OnImagesRetrievedListener() {
            @Override
            public void onImagesRetrieved(ArrayList<ImageData> data) {
                for (int i = 0; i < data.size(); i++) {

                    imageItemsArrayList.add(new GalleryItem(data.get(i).getFilePath()));
                }
                galleryAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }
        });*/

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

            GalleryItem galleryItem1 = new GalleryItem(BitmapFactory.decodeResource(getResources(), R.drawable.camera_icon));
            galleryItem1.setIsSeleted(false);
            galleryItem1.setType(Type.NONE);
            GalleryItem galleryItem2 = new GalleryItem(BitmapFactory.decodeResource(getResources(), R.drawable.giphy_icon));
            galleryItem2.setIsSeleted(false);
            galleryItem2.setType(Type.NONE);

            imageItemsArrayList.add(galleryItem1);
            imageItemsArrayList.add(galleryItem2);
            imageItemsArrayList.addAll(Utils.getGalleryPhotos(MainActivity.this));

            videoItemsArrayList.add(galleryItem1);
            videoItemsArrayList.add(galleryItem2);
            videoItemsArrayList.addAll(Utils.getGalleryVideos(MainActivity.this));

            if (imageItemsArrayList.size() > 2) {
                galleryCategoryItems.add(new GalleryCategoryItem(imageItemsArrayList.get(2).getFilePath(), "Images", imageItemsArrayList.size() - 2, true));
            }
            if (videoItemsArrayList.size() > 2) {
                galleryCategoryItems.add(new GalleryCategoryItem(videoItemsArrayList.get(2).getFilePath(), "Videos", videoItemsArrayList.size() - 2, false));
            }

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

}
