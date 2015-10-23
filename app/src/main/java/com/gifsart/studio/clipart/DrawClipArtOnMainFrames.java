package com.gifsart.studio.clipart;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.AsyncTask;

import com.gifsart.studio.item.GifItem;
import com.gifsart.studio.utils.Type;

import java.util.ArrayList;

/**
 * Created by Tigran on 9/14/15.
 */
public class DrawClipArtOnMainFrames extends AsyncTask<Void, Integer, Void> {

    private Context context;
    private MainView mainView;
    private ArrayList<GifItem> gifItems;
    private ProgressDialog progressDialog;
    private Clipart clipart;
    private ClipartsAreDrowned drowned;


    public DrawClipArtOnMainFrames(Context context, MainView mainView, ArrayList<GifItem> gifItems) {
        this.context = context;
        this.mainView = mainView;
        this.gifItems = gifItems;
    }

    @Override
    protected Void doInBackground(Void... params) {

        for (int i = 0; i < gifItems.size(); i++) {

            if (gifItems.get(i).getType() == Type.IMAGE) {
                drawClipart(gifItems.get(i).getBitmap());
            } else if (gifItems.get(i).getType() == Type.GIF) {
                for (int j = 0; j < gifItems.get(i).getBitmaps().size(); j++) {
                    drawClipart(gifItems.get(i).getBitmaps().get(j));
                }
            } else if (gifItems.get(i).getType() == Type.VIDEO) {
                for (int j = 0; j < gifItems.get(i).getBitmaps().size(); j++) {
                    drawClipart(gifItems.get(i).getBitmaps().get(j));
                }
            }
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        clipart = mainView.getClipartItem();
        progressDialog = new ProgressDialog(context);
        progressDialog.show();
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        progressDialog.dismiss();
        drowned.areDrowned(true);
    }

    public void drawClipart(Bitmap bitmap) {
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();

        if (clipart != null) {
            Matrix transformMatrix = new Matrix();
            transformMatrix.postRotate(clipart.getRotation(), clipart.getBitmap().getWidth() / 2, clipart.getBitmap().getHeight() / 2);
            transformMatrix.postTranslate(clipart.getX(), clipart.getY());
            transformMatrix.postScale(clipart.getScaleX(), clipart.getScaleY());
            canvas.drawBitmap(bitmap, 0, 0, paint);
            canvas.scale((float) Math.max(bitmap.getWidth(), bitmap.getHeight()) / mainView.getWidth(), (float) Math.max(bitmap.getWidth(), bitmap.getHeight()) / mainView.getWidth(), 0, 0);
            canvas.drawBitmap(clipart.getBitmap(), transformMatrix, paint);
        } else {
            canvas.drawBitmap(bitmap, 0, 0, paint);
        }
    }

    public void setDrownedListener(ClipartsAreDrowned drowned) {
        this.drowned = drowned;
    }

    public interface ClipartsAreDrowned {
        public void areDrowned(boolean done);
    }

}
