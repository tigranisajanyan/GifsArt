package com.gifsart.studio.gifutils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;

import com.gifsart.studio.activity.GifPreviewActivity;
import com.gifsart.studio.item.MakeGifItem;
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
    private ArrayList<MakeGifItem> makeGifItems = new ArrayList<>();
    private ProgressDialog progressDialog;
    private static final String root = Environment.getExternalStorageDirectory().toString();

    public SaveGIFAsyncTask(String outputDir, ArrayList<MakeGifItem> makeGifItems, Activity activity) {
        this.outputDir = outputDir;
        this.makeGifItems = makeGifItems;
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(activity);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(makeGifItems.size());
        progressDialog.show();
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

            for (int i = 0; i < makeGifItems.size(); i++) {

                if (makeGifItems.get(i).getType() == Type.IMAGE) {
                    animatedGifEncoder.setDelay(makeGifItems.get(i).getDuraton());
                    animatedGifEncoder.addFrame(Utils.squareFit(makeGifItems.get(i).getBitmap(), GifsArtConst.FRAME_SIZE));
                    publishProgress(i);
                } else if (makeGifItems.get(i).getType() == Type.GIF) {
                    for (int j = 0; j < makeGifItems.get(i).getBitmaps().size(); j++) {
                        animatedGifEncoder.setDelay(makeGifItems.get(i).getDuraton());
                        animatedGifEncoder.addFrame(Utils.squareFit(makeGifItems.get(i).getBitmaps().get(j), GifsArtConst.FRAME_SIZE));
                    }
                    publishProgress(i);
                } else if (makeGifItems.get(i).getType() == Type.VIDEO) {
                    for (int j = 0; j < makeGifItems.get(i).getBitmaps().size(); j++) {
                        animatedGifEncoder.setDelay(makeGifItems.get(i).getDuraton());
                        animatedGifEncoder.addFrame(Utils.squareFit(makeGifItems.get(i).getBitmaps().get(j), GifsArtConst.FRAME_SIZE));
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
