package com.gifsart.studio.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Environment;

import com.decoder.PhotoUtils;
import com.gifsart.studio.utils.FileCounterSingleton;
import com.gifsart.studio.utils.GifsArtConst;

import java.util.ArrayList;

/**
 * Created by Tigran on 10/28/15.
 */
public class BurstModeFramesSaving extends AsyncTask<Void, Integer, Void> {

    private Context context;
    private FramesSaved framesSaved;
    private ArrayList<byte[]> bytes = new ArrayList<>();
    boolean cameraIsFront = true;

    public BurstModeFramesSaving(Context context, boolean cameraIsFront, ArrayList<byte[]> bytes) {
        this.context = context;
        this.bytes = bytes;
        this.cameraIsFront = cameraIsFront;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params) {
        for (int i = 0; i < bytes.size(); i++) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes.get(i), 0, bytes.get(i).length);
            /*try {
                FileOutputStream fileOutputStream = new FileOutputStream(new File(Environment.getExternalStorageDirectory() + "/GifsArt/.video_frames/", "img_" + i + ".jpg"));
                bitmap = rotate(bitmap, 90);
                if (cameraIsFront) {
                    bitmap = rotate(bitmap, 180);
                }
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fileOutputStream);
                fileOutputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }*/
            bitmap = rotate(bitmap, 90);
            if (cameraIsFront) {
                bitmap = rotate(bitmap, 180);
            }
            PhotoUtils.saveRawBitmap(bitmap, Environment.getExternalStorageDirectory().getPath() + "/" + GifsArtConst.DIR_VIDEO_FRAMES + "/img_" + FileCounterSingleton.getInstance(context).increaseIndex());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
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
        void done(boolean done);
    }

    public void setFramesSavedListener(FramesSaved fs) {
        this.framesSaved = fs;
    }

}
