package com.gifsart.studio.gifutils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;

import com.gifsart.studio.item.GalleryItem;
import com.gifsart.studio.utils.Utils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Tigran on 8/24/15.
 */
public class SaveGIFAsyncTask extends AsyncTask<Void, Integer, Void> {

    private Activity activity;
    private String outputDir;
    private int speed;
    private ArrayList<GalleryItem> bitmaps = new ArrayList<>();
    private ProgressDialog progressDialog;
    private static final String root = Environment.getExternalStorageDirectory().toString();

    public SaveGIFAsyncTask(String outputDir, ArrayList<GalleryItem> bitmaps, int speed, Activity activity) {
        this.outputDir = outputDir;
        this.bitmaps = bitmaps;
        this.activity = activity;
        this.speed = speed;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(activity);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(bitmaps.size());
        progressDialog.show();
    }

    @Override
    protected Void doInBackground(Void... params) {

        File outFile = new File(outputDir);
        try {

            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(outFile));
            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            AnimatedGifEncoder animatedGifEncoder = new AnimatedGifEncoder();
            animatedGifEncoder.setDelay(speed);
            animatedGifEncoder.setRepeat(0);
            animatedGifEncoder.start(bos);

            for (int i = 0; i < bitmaps.size(); i++) {
                if (bitmaps.get(i).isSeleted()) {
                    animatedGifEncoder.addFrame(bitmaps.get(i).getBitmap());
                    publishProgress(i);
                }
            }

            animatedGifEncoder.finish();
            bufferedOutputStream.write(bos.toByteArray());
            bufferedOutputStream.flush();
            bufferedOutputStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();

        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if (progressDialog != null) {
            progressDialog.setProgress(values[0] + 1);
        }
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        progressDialog.dismiss();
        AlertDialog.Builder gifSavedDialogBuilder = new AlertDialog.Builder(activity);
        gifSavedDialogBuilder.setTitle("GiFit");
        gifSavedDialogBuilder.setMessage("Gif saved successfully");
        gifSavedDialogBuilder.setPositiveButton("Preview", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Intent intent = new Intent(activity, GifViewActivity.class);
                //intent.putExtra(GifViewActivity.EXTRA_GIF_PATH, outputDir);
                //activity.startActivity(intent);
                activity.finish();
            }
        });
        gifSavedDialogBuilder.setNeutralButton("Share", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Utils.shareImage(activity, outputDir);
                dialog.dismiss();
                activity.finish();
            }
        });
        gifSavedDialogBuilder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                activity.finish();
            }
        });
        AlertDialog alertDialog = gifSavedDialogBuilder.create();
        alertDialog.show();


    }

}
