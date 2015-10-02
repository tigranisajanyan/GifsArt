package com.gifsart.studio.clipart;


import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.gifsart.studio.R;
import com.gifsart.studio.utils.Utils;


public class ClipartActivity extends ActionBarActivity implements OnVideoActionFinishListener {

    private final String TAG = ClipartActivity.class.getSimpleName();
    private final int[] clipartList = new int[]{
            R.drawable.clipart_1,
            R.drawable.clipart_2,
            R.drawable.clipart_3,
            R.drawable.clipart_4,
            R.drawable.clipart_5,
            R.drawable.clipart_6,
            R.drawable.clipart_7,
            R.drawable.clipart_8
    };

    private MainView mainView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String realPath = getIntent().getStringExtra("image_path");
//        String message = realPath;
//        File imgFile = new File(realPath);
//        Log.i(TAG, " imgFile= " + imgFile.getAbsolutePath());
//        if (imgFile.exists()) {
//            Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
//            Log.i(TAG, "bitmap= " + bitmap.getWidth() + "x" + bitmap.getHeight());
//            message += "\n bitmap= " + bitmap.getWidth() + "x" + bitmap.getHeight();
//        }
        Log.i(TAG, realPath);

        initView(realPath);
        intiCliparts();
    }


    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getTitle() == "apply") {
            //TODO do action on all frames
        }
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("apply");
        menu.getItem(0).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    private void initView(String path) {
        mainView = new MainView(this, path, false);
        mainView.setId(R.id.mainViewId);
        ViewGroup container = (ViewGroup) findViewById(R.id.main_view_container);
        container.removeAllViews();
        container.addView(mainView);
    }

    private void intiCliparts() {
        LinearLayout container = (LinearLayout) findViewById(R.id.container);
        container.removeAllViews();

        for (int i = 0; i < clipartList.length; i++) {
            //TODO replace to LinearHorizontalRecyclerView
            ImageView imgView = new ImageView(this);
            int size = (int) Utils.dpToPixel(35, this);
            int margin = (int) Utils.dpToPixel(7, this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(size, size);
            layoutParams.setMargins(margin, margin, margin, margin);
            imgView.setLayoutParams(layoutParams);
            imgView.setBackgroundResource(clipartList[i]);

            final int position = i;
            imgView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addClipart(position);
                }
            });

            container.addView(imgView);
        }

        container.setVisibility(View.VISIBLE);
    }


    private void addClipart(int position) {
        if (mainView != null) {
            mainView.addClipart(clipartList[position]);
        }
    }

    @Override
    public void onSuccess() {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onFailure() {
        setResult(RESULT_CANCELED);
        finish();
    }

}
