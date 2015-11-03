package com.gifsart.studio.camera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Tigran on 10/28/15.
 */
public class BurstModeAper extends AsyncTask<Void, Integer, Void> {

    private FramesSaved framesSaved;
    private ArrayList<byte[]> bytes = new ArrayList<>();

    public BurstModeAper(ArrayList<byte[]> bytes) {
        this.bytes = bytes;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected Void doInBackground(Void... params) {
        for (int i = 0; i < bytes.size(); i++) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes.get(i), 0, bytes.get(i).length);
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(new File(Environment.getExternalStorageDirectory() + "/GifsArt/video_frames/", "img_" + i + ".jpg"));
                bitmap = rotate(bitmap, 90);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fileOutputStream);
                fileOutputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Log.d("gagaga", "done");
        framesSaved.done(true);

    }

    public Bitmap rotate(Bitmap bitmap, int degree) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Matrix mtx = new Matrix();
        mtx.postRotate(degree);

        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
    }

    public interface FramesSaved {
        public void done(boolean done);
    }

    public void setFramesSavedListener(FramesSaved fs) {
        this.framesSaved = fs;
    }

}
