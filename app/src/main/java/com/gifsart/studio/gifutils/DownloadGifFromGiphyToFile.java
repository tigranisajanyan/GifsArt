package com.gifsart.studio.gifutils;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

import com.gifsart.studio.item.GiphyItem;
import com.gifsart.studio.utils.AnimatedProgressDialog;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Tigran on 11/11/15.
 */
public class DownloadGifFromGiphyToFile extends AsyncTask<Void, Integer, Boolean> {

    private Context context;
    private GiphyItem giphyItem;

    private AnimatedProgressDialog progressDialog;

    private OnDownloaded onDownloaded;
    private HttpURLConnection ucon;
    private byte[] buffer;

    public DownloadGifFromGiphyToFile(Context context, GiphyItem giphyItem) {
        this.context = context;
        this.giphyItem = giphyItem;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new AnimatedProgressDialog(context);
        progressDialog.setCancelable(false);
        //progressDialog.show();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            URL url = new URL(giphyItem.getOriginalGifUrl());
            ucon = (HttpURLConnection) url.openConnection();
            InputStream is = ucon.getInputStream();
            ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
            int bufferSize = 1024;
            buffer = new byte[bufferSize];
            int len;
            while ((len = is.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }
            buffer = byteBuffer.toByteArray();
            FileOutputStream stream = new FileOutputStream(Environment.getExternalStorageDirectory() + "/ttt.gif");
            stream.write(byteBuffer.toByteArray());

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        onDownloaded.onDownloaded(result);
        progressDialog.dismiss();

    }

    public void setOnDownloadedListener(OnDownloaded l) {
        onDownloaded = l;
    }

    public interface OnDownloaded {
        void onDownloaded(boolean isDownladed);
    }

}
