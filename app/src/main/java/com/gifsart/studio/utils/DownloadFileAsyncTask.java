package com.gifsart.studio.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.gifsart.studio.R;
import com.gifsart.studio.item.GiphyItem;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import pl.droidsonroids.gif.GifDrawable;

/**
 * Created by Tigran on 9/25/15.
 */
public class DownloadFileAsyncTask extends AsyncTask<Void, Integer, Void> {

    private Context context;
    private String outputFile;
    private GiphyItem giphyItem;
    private String fileUrl;
    private static final String root = Environment.getExternalStorageDirectory().toString();

    private ProgressDialog progressDialog;

    private OnDownloaded onDownloaded;

    public DownloadFileAsyncTask(Context context, String outputFile, GiphyItem giphyItem) {
        this.context = context;
        this.outputFile = outputFile;
        this.giphyItem = giphyItem;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please Wait!!!");
        progressDialog.setCancelable(false);
        progressDialog.show();

    }

    @Override
    protected Void doInBackground(Void... params) {

        try {
            URL url = new URL(giphyItem.getOriginalGifUrl());

            URLConnection ucon = url.openConnection();
            ucon.setReadTimeout(5000);
            ucon.setConnectTimeout(10000);

            InputStream is = ucon.getInputStream();
            BufferedInputStream inStream = new BufferedInputStream(is, 1024 * 5);

            File file = new File(outputFile);

            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();

            FileOutputStream outStream = new FileOutputStream(file);
            byte[] buff = new byte[5 * 1024];

            int len;
            while ((len = inStream.read(buff)) != -1) {
                outStream.write(buff, 0, len);
            }

            outStream.flush();
            outStream.close();
            inStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);

        GifDrawable gifDrawable = null;
        try {
            gifDrawable = new GifDrawable(outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("gagagagag", gifDrawable.getNumberOfFrames() + "");
        onDownloaded.onDownloaded(true);
        progressDialog.dismiss();

    }

    public void setOnDownloadedListener(OnDownloaded l) {
        onDownloaded = l;
    }


    public interface OnDownloaded {
        void onDownloaded(boolean isDownloded);
    }

}
