package com.gifsart.studio.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.gifsart.studio.R;
import com.gifsart.studio.utils.GifsArtConst;

public class EditFrameActivity extends AppCompatActivity {

    private ImageView editImageView;
    private Bitmap originalBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_frame);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().widthPixels);
        editImageView = (ImageView) findViewById(R.id.edited_frame_image_view);
        editImageView.setLayoutParams(layoutParams);

        byte[] byteArray = getIntent().getByteArrayExtra(GifsArtConst.INTENT_IMAGE_BITMAP);
        originalBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        editImageView.setImageBitmap(originalBitmap);

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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
