package com.gifsart.studio.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.gifsart.studio.R;
import com.gifsart.studio.effects.GPUEffects;
import com.gifsart.studio.effects.GPUImageFilterTools;
import com.gifsart.studio.utils.GifsArtConst;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageView;

public class SquareFitActivity extends AppCompatActivity {

    private GPUImageView squareFitImageView;
    private Bitmap originalBitmap;
    private int square_fit_mode = GifsArtConst.FIT_MODE_ORIGINAL;

    private GPUImageFilter mFilter;
    private GPUImageFilterTools.FilterAdjuster mFilterAdjuster;

    String filterName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_square_fit);

        originalBitmap = BitmapFactory.decodeByteArray(getIntent().getByteArrayExtra(GifsArtConst.INTENT_IMAGE_BITMAP), 0, getIntent().getByteArrayExtra(GifsArtConst.INTENT_IMAGE_BITMAP).length);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().widthPixels);

        square_fit_mode = getIntent().getIntExtra(GifsArtConst.INTENT_SQUARE_FIT_MODE, 1);
        filterName = getIntent().getStringExtra(GifsArtConst.INTENT_EFFECT_FILTER);
        mFilter = GPUEffects.createFilterForType(GPUEffects.FilterType.valueOf(filterName));
        mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(mFilter);

        squareFitImageView = (GPUImageView) findViewById(R.id.square_fit_image_view);
        squareFitImageView.setLayoutParams(layoutParams);

        switch (square_fit_mode) {
            case 1:
                squareFitImageView.setScaleType(GPUImage.ScaleType.CENTER_INSIDE);
                break;
            case 2:
                squareFitImageView.setScaleType(GPUImage.ScaleType.CENTER_CROP);
                break;
            case 3:
                squareFitImageView.setScaleType(GPUImage.ScaleType.CENTER_INSIDE);
                break;

            default:
                break;
        }

        squareFitImageView.setImage(originalBitmap);
        squareFitImageView.setFilter(mFilter);

        findViewById(R.id.original_fit_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                squareFitImageView.setScaleType(GPUImage.ScaleType.CENTER_INSIDE);
                squareFitImageView.setImage(originalBitmap);
                square_fit_mode = GifsArtConst.FIT_MODE_ORIGINAL;
            }
        });

        findViewById(R.id.square_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                squareFitImageView.setScaleType(GPUImage.ScaleType.CENTER_CROP);
                squareFitImageView.setImage(originalBitmap);
                square_fit_mode = GifsArtConst.FIT_MODE_SQUARE;
            }
        });

        findViewById(R.id.square_fit_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                squareFitImageView.setScaleType(GPUImage.ScaleType.CENTER_INSIDE);
                squareFitImageView.setImage(originalBitmap);
                square_fit_mode = GifsArtConst.FIT_MODE_SQUARE_FIT;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_square_fit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_apply) {
            Intent data = new Intent().putExtra(GifsArtConst.INTENT_SQUARE_FIT_MODE, square_fit_mode);
            setResult(RESULT_OK, data);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
