package com.gifsart.studio.activity;

import android.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.gifsart.studio.R;

public class SquareFitActivity extends AppCompatActivity {

    private ImageView squareFitImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_square_fit);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().widthPixels);
        squareFitImageView = (ImageView) findViewById(R.id.square_fit_image_view);
        squareFitImageView.setLayoutParams(layoutParams);
        squareFitImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        squareFitImageView.setImageDrawable(getResources().getDrawable(R.drawable.car));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_square_fit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
