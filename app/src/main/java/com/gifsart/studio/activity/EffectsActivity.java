package com.gifsart.studio.activity;

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
import com.gifsart.studio.utils.GPUImageFilterTools;
import com.gifsart.studio.utils.SpacesItemDecoration;
import com.gifsart.studio.utils.Utils;

import java.util.ArrayList;

import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageView;

public class EffectsActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    private GPUImageView mGPUImageView;
    private RecyclerView recyclerView;
    private SeekBar seekBar;

    private LinearLayoutManager linearLayoutManager;
    private RecyclerView.ItemAnimator itemAnimator;
    private Bitmap originalBitmap;
    private EffectsAdapter effectsAdapter;

    private GPUImageFilter mFilter;
    private GPUImageFilterTools.FilterAdjuster mFilterAdjuster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_effects);

        originalBitmap = BitmapFactory.decodeByteArray(getIntent().getByteArrayExtra("image"), 0, getIntent().getByteArrayExtra("image").length);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().widthPixels);

        mGPUImageView = (GPUImageView) findViewById(R.id.gpu_image_view);
        mGPUImageView.setLayoutParams(layoutParams);
        mGPUImageView.setImage(originalBitmap);

        recyclerView = (RecyclerView) findViewById(R.id.effects_rec_view);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        itemAnimator = new DefaultItemAnimator();

        recyclerView.setHasFixedSize(true);
        recyclerView.setClipToPadding(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(itemAnimator);
        recyclerView.addItemDecoration(new SpacesItemDecoration((int) Utils.dpToPixel(2, this)));

        ((SeekBar) findViewById(R.id.opacity_seek_bar)).setOnSeekBarChangeListener(this);

        ArrayList<String> strings = new ArrayList<>();
        strings.add("");
        strings.add("");
        strings.add("");
        strings.add("");
        strings.add("");
        effectsAdapter = new EffectsAdapter(originalBitmap, strings, this);

        recyclerView.setAdapter(effectsAdapter);

        seekBar = (SeekBar) findViewById(R.id.opacity_seek_bar);

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
            setResult(RESULT_OK);
            finish();
            /*GPUImageFilterTools.showDialog(this, new GPUImageFilterTools.OnGpuImageFilterChosenListener() {

                @Override
                public void onGpuImageFilterChosenListener(final GPUImageFilter filter) {
                    switchFilterTo(filter);
                    mGPUImageView.requestRender();
                }

            });*/
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void switchFilterTo(final GPUImageFilter filter) {
        if (mFilter == null
                || (filter != null && !mFilter.getClass().equals(filter.getClass()))) {
            mFilter = filter;
            mGPUImageView.setFilter(mFilter);
            mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(mFilter);

            findViewById(R.id.opacity_seek_bar).setVisibility(
                    mFilterAdjuster.canAdjust() ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (mFilterAdjuster != null) {
            mFilterAdjuster.adjust(progress);
        }
        mGPUImageView.requestRender();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
