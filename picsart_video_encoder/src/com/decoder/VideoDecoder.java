package com.decoder;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Tigran on 7/20/15.
 */
public class VideoDecoder {

    private static final String root = Environment.getExternalStorageDirectory().toString();

    private String inputFilePath = "";
    private String outputDirectory = "";

    private int frameSize;
    private Context context;
    private int frameCount;

    private ArrayList<String> savedFramePath = new ArrayList<>();
    private static OnDecodeFinishedListener onDecodeFinishedListener;

    public VideoDecoder(Context context, String inputFilePath, int frameCount, int frameSize, String outputDirectory) {

        this.context = context;
        this.inputFilePath = inputFilePath;
        this.frameCount = frameCount;
        this.frameSize = frameSize;
        this.outputDirectory = outputDirectory;

    }

    /**
     * extracting frames from video
     */
    public void extractVideoFrames() {
        new ExtractFrames().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void setOnDecodeFinishedListener(OnDecodeFinishedListener l) {
        onDecodeFinishedListener = l;
    }

    private class ExtractFrames extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... params) {

            ExtractMpegFrames extractMpegFrames = new ExtractMpegFrames();
            try {
                extractMpegFrames.extractMpegFrames(context, inputFilePath, frameCount, frameSize, outputDirectory);

            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            File[] files = new File(outputDirectory).listFiles();

            for (int i = 0; i < files.length; i++) {
                savedFramePath.add(files[i].getAbsolutePath());
            }
            onDecodeFinishedListener.onFinish(true);

        }
    }

    public interface OnDecodeFinishedListener {

        void onFinish(boolean isDone);

    }

}
