package com.gifsart.studio.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.decoder.PhotoUtils;
import com.gifsart.studio.utils.AnimatedProgressDialog;
import com.gifsart.studio.utils.FileCounterSingleton;
import com.gifsart.studio.utils.GifsArtConst;

/**
 * Created by Tigran on 1/13/16.
 */
public class SaveVideoFrames extends AsyncTask<Void, Void, Void> {


    public AnimatedProgressDialog animatedProgressDialog;
    public IsSaved isSaved;
    public Context context;

    public SaveVideoFrames(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        animatedProgressDialog = new AnimatedProgressDialog(context);
        //animatedProgressDialog.show();
        Log.d("gagag", "start rendering");

    }

    @Override
    protected Void doInBackground(Void... params) {
        for (int i = 0; i < CameraPreview.datas.size(); i++) {
            if (isCancelled()) return null;
            String path = Environment.getExternalStorageDirectory().getPath() + "/" + GifsArtConst.DIR_VIDEO_FRAMES + "/img_" + FileCounterSingleton.getInstance(context).increaseIndex();
            Bitmap bitmap = BitmapFactory.decodeByteArray(CameraPreview.datas.get(i), 0, CameraPreview.datas.get(i).length);

            Matrix matrix = new Matrix();
            matrix.postRotate(90);

            Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

            /*OutputStream imagefile = null;
            try {
                imagefile = new FileOutputStream(path);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, imagefile);*/
            PhotoUtils.saveRawBitmap(rotatedBitmap, path);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        isSaved.framesAreSaved(true);
        CameraPreview.datas.clear();
        animatedProgressDialog.dismiss();
    }

    public interface IsSaved {
        void framesAreSaved(boolean done);
    }

    public void setOnFramesSavedListener(IsSaved saved) {
        isSaved = saved;
    }
}