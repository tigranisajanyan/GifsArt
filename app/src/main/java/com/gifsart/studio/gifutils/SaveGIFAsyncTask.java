package com.gifsart.studio.gifutils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;

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

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageView;

/**
 * Created by Tigran on 8/24/15.
 */
public class SaveGIFAsyncTask extends AsyncTask<Void, Integer, Void> {

    private Activity activity;
    private String outputDir;
    private ArrayList<GifItem> gifItems = new ArrayList<>();
    private ProgressDialog progressDialog;

    private GPUImageView gpuImageView;
    private GPUImageFilter gpuImageFilter;

    int num = 0;

    private int squareFitMode = 1;

    public SaveGIFAsyncTask(String outputDir, ArrayList<GifItem> gifItems, int squareFitMode, GPUImageView gpuImageView, GPUImageFilter gpuImageFilter, Activity activity) {
        this.outputDir = outputDir;
        this.gifItems = gifItems;
        this.gpuImageView = gpuImageView;
        this.gpuImageFilter = gpuImageFilter;
        this.activity = activity;
        this.squareFitMode = squareFitMode;

        progressDialog = new ProgressDialog(activity);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(checkGifItemsFramesCount());
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        applyEffect(gpuImageFilter);
        checkSquareFitMode();
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
                    addGifFrame(animatedGifEncoder, gifItems.get(i).getBitmap(), squareFitMode, gifItems.get(i).getCurrentDuration());
                    publishProgress(num);
                    num++;
                } else if (gifItems.get(i).getType() == Type.GIF) {
                    for (int j = 0; j < gifItems.get(i).getBitmaps().size(); j++) {
                        addGifFrame(animatedGifEncoder, gifItems.get(i).getBitmaps().get(j), squareFitMode, gifItems.get(i).getCurrentDuration());
                        publishProgress(num);
                        num++;
                    }
                } else if (gifItems.get(i).getType() == Type.VIDEO) {
                    for (int j = 0; j < gifItems.get(i).getBitmaps().size(); j++) {
                        addGifFrame(animatedGifEncoder, gifItems.get(i).getBitmaps().get(j), squareFitMode, gifItems.get(i).getCurrentDuration());
                        publishProgress(num);
                        num++;
                    }
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
            progressDialog.setProgress(values[0]);
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

    public class ApplyEffects extends AsyncTask<Void,Void,Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }
    }

    public void applyEffect(final GPUImageFilter gpuImageFilter) {
        GPUImage gpuImage = new GPUImage(activity);
        gpuImage.setFilter(gpuImageFilter);
        for (int i = 0; i < gifItems.size(); i++) {
            if (gifItems.get(i).getType() == Type.IMAGE) {
                gpuImage.setImage(gifItems.get(i).getBitmap());
                gifItems.get(i).setBitmap(gpuImage.getBitmapWithFilterApplied());
            } else {
                for (int j = 0; j < gifItems.get(i).getBitmaps().size(); j++) {
                    gpuImage.setImage(gifItems.get(i).getBitmaps().get(j));
                    gifItems.get(i).getBitmaps().set(j, gpuImage.getBitmapWithFilterApplied());
                }
            }
        }
    }

    public int checkGifItemsFramesCount() {
        int count = 0;
        for (int i = 0; i < gifItems.size(); i++) {
            if (gifItems.get(i).getType() == Type.IMAGE) {
                count++;
            } else {
                count += gifItems.get(i).getBitmaps().size();
            }
        }
        return count;
    }

    // if all gif items doesn't have same width and height , square fit mode will be square fit
    public void checkSquareFitMode() {
        if (squareFitMode == GifsArtConst.FIT_MODE_ORIGINAL) {
            for (int i = 0; i < gifItems.size(); i++) {
                for (int j = 0; j < gifItems.size(); j++) {
                    if (gifItems.get(i).getBitmap().getWidth() != gifItems.get(j).getBitmap().getWidth() || gifItems.get(i).getBitmap().getHeight() != gifItems.get(j).getBitmap().getHeight()) {
                        squareFitMode = GifsArtConst.FIT_MODE_SQUARE_FIT;
                        break;
                    }
                }
            }
        }
    }

    public void addGifFrame(AnimatedGifEncoder animatedGifEncoder, Bitmap bitmap, int squareFitMode, int duration) {
        animatedGifEncoder.setDelay(duration);
        if (squareFitMode == GifsArtConst.FIT_MODE_SQUARE_FIT) {
            bitmap = Utils.squareFit(bitmap, GifsArtConst.GIF_FRAME_SIZE);
        } else if (squareFitMode == GifsArtConst.FIT_MODE_SQUARE) {
            bitmap = Utils.scaleCenterCrop(bitmap, GifsArtConst.GIF_FRAME_SIZE, GifsArtConst.GIF_FRAME_SIZE);
        }
        animatedGifEncoder.addFrame(bitmap);
    }

}
