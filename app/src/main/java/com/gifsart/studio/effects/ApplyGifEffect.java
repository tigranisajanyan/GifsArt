package com.gifsart.studio.effects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.gifsart.studio.R;
import com.gifsart.studio.adapter.EffectsAdapter;
import com.gifsart.studio.utils.Utils;

import jp.co.cyberagent.android.gpuimage.GPUImage;

/**
 * Created by Tigran on 11/5/15.
 */
public class ApplyGifEffect extends AsyncTask<Void, Bitmap, Void> {

    private Context context;
    private EffectsAdapter effectsAdapter;
    private GPUImage gpuImage;

    public ApplyGifEffect(EffectsAdapter effectsAdapter, Context context) {
        this.context = context;
        this.effectsAdapter = effectsAdapter;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        gpuImage = new GPUImage(context);
        gpuImage.setImage(Utils.scaleCenterCrop(BitmapFactory.decodeResource(context.getResources(), R.drawable.effect1), 150, 150));
    }

    @Override
    protected Void doInBackground(Void... params) {
        for (int i = 0; i < GPUEffects.FilterType.values().length; i++) {
            gpuImage.setFilter(GPUEffects.createFilterForType(context, GPUEffects.FilterType.fromInt(i)));
            publishProgress(gpuImage.getBitmapWithFilterApplied());
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Bitmap... values) {
        super.onProgressUpdate(values);
        effectsAdapter.addItem(values[0]);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }

}
