package com.gifsart.studio.gifutils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.gifsart.studio.activity.GifPreviewActivity;
import com.gifsart.studio.item.GifItem;
import com.gifsart.studio.utils.GifsArtConst;
import com.gifsart.studio.utils.Type;
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
    private ArrayList<GifItem> gifItems = new ArrayList<>();
    private ProgressDialog progressDialog;
    private static final String root = Environment.getExternalStorageDirectory().toString();
    int count = 0;
    private boolean doSquareFit = false;
    private int scaleType = 1;

    public SaveGIFAsyncTask(String outputDir, ArrayList<GifItem> gifItems, int scaleType, Activity activity) {
        this.outputDir = outputDir;
        this.gifItems = gifItems;
        this.activity = activity;
        this.scaleType = scaleType;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(activity);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(gifItems.size());
        progressDialog.setCancelable(false);
        progressDialog.show();
        for (int i = 0; i < gifItems.size(); i++) {
            for (int j = 0; j < gifItems.size(); j++) {
                if (gifItems.get(i).getBitmap().getWidth() != gifItems.get(j).getBitmap().getWidth() || gifItems.get(i).getBitmap().getHeight() != gifItems.get(j).getBitmap().getHeight()) {
                    doSquareFit = true;
                    break;
                }
            }
        }
    }

    @Override
    protected Void doInBackground(Void... params) {

        File outFile = new File(outputDir);
        try {

            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(outFile));
            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            AnimatedGifEncoder animatedGifEncoder = new AnimatedGifEncoder();
            animatedGifEncoder.setRepeat(0);
            animatedGifEncoder.start(bos);

            for (int i = 0; i < gifItems.size(); i++) {

                if (gifItems.get(i).getType() == Type.IMAGE) {
                    animatedGifEncoder.setDelay(gifItems.get(i).getDuraton());
                    Bitmap bitmap = gifItems.get(i).getBitmap();
                    if (doSquareFit) {
                        bitmap = Utils.squareFit(bitmap, GifsArtConst.FRAME_SIZE);
                    }
                    animatedGifEncoder.addFrame(bitmap);
                    publishProgress(i);
                    count++;
                    Log.d("gif_generator", "" + count);
                } else if (gifItems.get(i).getType() == Type.GIF) {
                    for (int j = 0; j < gifItems.get(i).getBitmaps().size(); j++) {
                        animatedGifEncoder.setDelay(gifItems.get(i).getDuraton());
                        Bitmap bitmap = gifItems.get(i).getBitmaps().get(j);
                        if (doSquareFit) {
                            bitmap = Utils.squareFit(bitmap, GifsArtConst.FRAME_SIZE);
                        }
                        animatedGifEncoder.addFrame(bitmap);
                        count++;
                        Log.d("gif_generator", "" + count);
                    }
                    publishProgress(i);
                } else if (gifItems.get(i).getType() == Type.VIDEO) {
                    for (int j = 0; j < gifItems.get(i).getBitmaps().size(); j++) {
                        animatedGifEncoder.setDelay(gifItems.get(i).getDuraton());
                        Bitmap bitmap = gifItems.get(i).getBitmaps().get(j);
                        if (doSquareFit) {
                            bitmap = Utils.squareFit(bitmap, GifsArtConst.FRAME_SIZE);
                        }
                        animatedGifEncoder.addFrame(bitmap);
                        count++;
                        Log.d("gif_generator", "" + count);
                    }
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
                Intent intent = new Intent(activity, GifPreviewActivity.class);
                intent.putExtra(GifPreviewActivity.EXTRA_GIF_PATH, outputDir);
                activity.startActivity(intent);
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
