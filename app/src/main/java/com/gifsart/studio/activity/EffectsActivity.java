package com.gifsart.studio.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.gifsart.studio.R;
import com.gifsart.studio.adapter.EffectsAdapter;
import com.gifsart.studio.effects.GPUEffects;
import com.gifsart.studio.effects.GPUImageFilterTools;
import com.gifsart.studio.helper.RecyclerItemClickListener;
import com.gifsart.studio.utils.GifsArtConst;
import com.gifsart.studio.utils.SpacesItemDecoration;
import com.gifsart.studio.utils.Utils;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageView;

public class EffectsActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    private GPUImageView gpuImageView;
    private RecyclerView recyclerView;

    private LinearLayoutManager linearLayoutManager;
    private RecyclerView.ItemAnimator itemAnimator;
    private Bitmap originalBitmap;
    private EffectsAdapter effectsAdapter;

    private GPUImageFilter mFilter;
    private GPUImageFilterTools.FilterAdjuster mFilterAdjuster;
    private String filterName;
    private int square_fit_mode = GifsArtConst.FIT_MODE_ORIGINAL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_effects);

        final GPUEffects.FilterList filters = new GPUEffects.FilterList();
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

        originalBitmap = BitmapFactory.decodeByteArray(getIntent().getByteArrayExtra(GifsArtConst.INTENT_IMAGE_BITMAP), 0, getIntent().getByteArrayExtra(GifsArtConst.INTENT_IMAGE_BITMAP).length);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().widthPixels);

        square_fit_mode = getIntent().getIntExtra(GifsArtConst.INTENT_SQUARE_FIT_MODE, 1);
        filterName = getIntent().getStringExtra(GifsArtConst.INTENT_EFFECT_FILTER);
        mFilter = GPUEffects.createFilterForType(GPUEffects.FilterType.valueOf(filterName));
        mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(mFilter);

        gpuImageView = (GPUImageView) findViewById(R.id.gpu_image_view);
        gpuImageView.setLayoutParams(layoutParams);

        switch (square_fit_mode) {
            case 1:
                gpuImageView.setScaleType(GPUImage.ScaleType.CENTER_INSIDE);
                break;
            case 2:
                gpuImageView.setScaleType(GPUImage.ScaleType.CENTER_CROP);
                break;
            case 3:
                gpuImageView.setScaleType(GPUImage.ScaleType.CENTER_INSIDE);
                break;

            default:
                break;
        }

        gpuImageView.setFilter(mFilter);
        gpuImageView.setImage(originalBitmap);

        recyclerView = (RecyclerView) findViewById(R.id.effects_rec_view);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        itemAnimator = new DefaultItemAnimator();

        recyclerView.setHasFixedSize(true);
        recyclerView.setClipToPadding(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(itemAnimator);
        recyclerView.addItemDecoration(new SpacesItemDecoration((int) Utils.dpToPixel(2, this)));

        effectsAdapter = new EffectsAdapter(originalBitmap, filters, this);

        recyclerView.setAdapter(effectsAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                filterName = filters.filters.get(position).name();
                switchFilterTo(GPUEffects.createFilterForType(filters.filters.get(position)));

            }
        }));

        findViewById(R.id.opacity_seek_bar).setVisibility(
                mFilterAdjuster.canAdjust() ? View.VISIBLE : View.GONE);
        ((SeekBar) findViewById(R.id.opacity_seek_bar)).setOnSeekBarChangeListener(this);

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

            Intent intent = new Intent();
            intent.putExtra(GifsArtConst.INTENT_EFFECT_FILTER, filterName);
            setResult(RESULT_OK, intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (mFilterAdjuster != null) {
            mFilterAdjuster.adjust(progress);
        }
        gpuImageView.requestRender();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private void switchFilterTo(final GPUImageFilter filter) {
        if (mFilter == null
                || (filter != null && !mFilter.getClass().equals(filter.getClass()))) {
            mFilter = filter;
            gpuImageView.setFilter(mFilter);
            mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(mFilter);

            findViewById(R.id.opacity_seek_bar).setVisibility(
                    mFilterAdjuster.canAdjust() ? View.VISIBLE : View.GONE);
        }
    }

}
