package com.gifsart.studio.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.gifsart.studio.R;
import com.gifsart.studio.adapter.StickerAdapter;
import com.gifsart.studio.adapter.StickerCategoryAdapter;
import com.gifsart.studio.clipart.MainView;
import com.gifsart.studio.helper.RecyclerItemClickListener;
import com.gifsart.studio.utils.GifsArtConst;
import com.gifsart.studio.utils.SpacesItemDecoration;
import com.gifsart.studio.utils.Utils;

import java.util.ArrayList;

public class ClipArtActivity extends AppCompatActivity {

    private ImageView imageView;
    private RecyclerView stickerRecyclerView;
    private RecyclerView categoryRecyclerView;
    private ViewGroup container;
    private MainView mainView;

    private GridLayoutManager gridLayoutManager;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView.ItemAnimator itemAnimator;

    private StickerAdapter stickerAdapter;
    private StickerCategoryAdapter stickerCategoryAdapter;

    private Bitmap originalBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clip_art);

        imageView = (ImageView) findViewById(R.id.clipart_image_view);
        stickerRecyclerView = (RecyclerView) findViewById(R.id.clipart_rec_view);
        categoryRecyclerView = (RecyclerView) findViewById(R.id.sticker_category_rec_view);
        container = (ViewGroup) findViewById(R.id.clipart_main_view_container);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().widthPixels);
        container.setLayoutParams(layoutParams);

        byte[] byteArray = getIntent().getByteArrayExtra(GifsArtConst.INTENT_IMAGE_BITMAP);
        originalBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        imageView.setImageBitmap(originalBitmap);

        gridLayoutManager = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        itemAnimator = new DefaultItemAnimator();

        stickerRecyclerView.setHasFixedSize(true);
        stickerRecyclerView.setClipToPadding(true);
        stickerRecyclerView.setLayoutManager(gridLayoutManager);
        stickerRecyclerView.setItemAnimator(itemAnimator);
        stickerRecyclerView.addItemDecoration(new SpacesItemDecoration((int) Utils.dpToPixel(5, this)));

        categoryRecyclerView.setHasFixedSize(true);
        categoryRecyclerView.setClipToPadding(true);
        categoryRecyclerView.setLayoutManager(linearLayoutManager);
        categoryRecyclerView.setItemAnimator(itemAnimator);

        final ArrayList<Integer> integers = new ArrayList<>();
        integers.add(R.drawable.clipart_1);
        integers.add(R.drawable.clipart_2);
        integers.add(R.drawable.clipart_3);
        integers.add(R.drawable.clipart_4);
        integers.add(R.drawable.clipart_5);
        integers.add(R.drawable.clipart_6);
        integers.add(R.drawable.clipart_7);
        integers.add(R.drawable.clipart_8);

        stickerAdapter = new StickerAdapter(integers, this);
        stickerRecyclerView.setAdapter(stickerAdapter);

        ArrayList<Integer> integers1 = new ArrayList<>();
        integers1.add(R.drawable.giphy_icon);
        integers1.add(R.drawable.clipart_6);
        integers1.add(R.drawable.clipart_7);
        integers1.add(R.drawable.clipart_8);

        stickerCategoryAdapter = new StickerCategoryAdapter(integers1, this);
        categoryRecyclerView.setAdapter(stickerCategoryAdapter);


        stickerRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                initMainView();
                if (mainView != null) {
                    mainView.addClipart(integers.get(position));
                }
                container.addView(mainView);

            }
        }));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_frame, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_apply) {
            saveClipart();
            return true;
        }

        return super.onOptionsItemSelected(item);
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

}
