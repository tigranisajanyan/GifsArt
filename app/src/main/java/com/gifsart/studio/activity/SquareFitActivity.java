package com.gifsart.studio.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.gifsart.studio.R;
import com.gifsart.studio.utils.GifsArtConst;
import com.gifsart.studio.utils.Utils;

public class SquareFitActivity extends AppCompatActivity {

    private ImageView squareFitImageView;
    private Bitmap originalBitmap;
    private int square_fit_mode = GifsArtConst.MODE_ORIGINAL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_square_fit);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().widthPixels);
        squareFitImageView = (ImageView) findViewById(R.id.square_fit_image_view);
        squareFitImageView.setLayoutParams(layoutParams);

        byte[] byteArray = getIntent().getByteArrayExtra("image");
        originalBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        square_fit_mode = getIntent().getIntExtra("square_mode", 1);
        squareFitImageView.setImageBitmap(originalBitmap);
        switch (square_fit_mode) {
            case 1:
                squareFitImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                findViewById(R.id.original_fit_button).setBackgroundColor(Color.BLUE);
                findViewById(R.id.square_fit_button).setBackgroundColor(Color.DKGRAY);
                findViewById(R.id.square_button).setBackgroundColor(Color.DKGRAY);
                break;
            case 2:
                squareFitImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                findViewById(R.id.square_button).setBackgroundColor(Color.BLUE);
                findViewById(R.id.square_fit_button).setBackgroundColor(Color.DKGRAY);
                findViewById(R.id.original_fit_button).setBackgroundColor(Color.DKGRAY);
                break;
            case 3:
                squareFitImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                findViewById(R.id.square_fit_button).setBackgroundColor(Color.BLUE);
                findViewById(R.id.square_button).setBackgroundColor(Color.DKGRAY);
                findViewById(R.id.original_fit_button).setBackgroundColor(Color.DKGRAY);
                break;

            default:
                break;
        }

        findViewById(R.id.original_fit_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                squareFitImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                squareFitImageView.setImageBitmap(originalBitmap);
                square_fit_mode = GifsArtConst.MODE_ORIGINAL;

                findViewById(R.id.original_fit_button).setBackgroundColor(Color.BLUE);
                findViewById(R.id.square_fit_button).setBackgroundColor(Color.DKGRAY);
                findViewById(R.id.square_button).setBackgroundColor(Color.DKGRAY);
            }
        });

        findViewById(R.id.square_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                squareFitImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                squareFitImageView.setImageBitmap(originalBitmap);
                square_fit_mode = GifsArtConst.MODE_SQUARE;

                findViewById(R.id.square_button).setBackgroundColor(Color.BLUE);
                findViewById(R.id.square_fit_button).setBackgroundColor(Color.DKGRAY);
                findViewById(R.id.original_fit_button).setBackgroundColor(Color.DKGRAY);
            }
        });

        findViewById(R.id.square_fit_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                squareFitImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                squareFitImageView.setImageBitmap(originalBitmap);
                square_fit_mode = GifsArtConst.MODE_SQUARE_FIT;

                findViewById(R.id.square_fit_button).setBackgroundColor(Color.BLUE);
                findViewById(R.id.square_button).setBackgroundColor(Color.DKGRAY);
                findViewById(R.id.original_fit_button).setBackgroundColor(Color.DKGRAY);
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
            Intent data = new Intent().putExtra("square_mode", square_fit_mode);
            setResult(RESULT_OK, data);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
