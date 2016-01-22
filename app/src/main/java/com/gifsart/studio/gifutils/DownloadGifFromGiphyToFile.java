package com.gifsart.studio.gifutils;

import android.content.Context;
import android.os.AsyncTask;

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
    private String outputPath;
    private String downloadingFileUrl;

    private OnDownloaded onDownloaded;
    private HttpURLConnection ucon;
    private byte[] buffer;

    public DownloadGifFromGiphyToFile(Context context, String outputPath, String downloadingFileUrl) {
        this.context = context;
        this.outputPath = outputPath;
        this.downloadingFileUrl = downloadingFileUrl;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            URL url = new URL(downloadingFileUrl);
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
            FileOutputStream stream = new FileOutputStream(outputPath);
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
    }

    public void setOnDownloadedListener(OnDownloaded l) {
        onDownloaded = l;
    }

    public interface OnDownloaded {
        void onDownloaded(boolean isDownladed);
    }

}
