package com.gifsart.studio.effects;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.gifsart.studio.adapter.EffectsAdapter;
import com.gifsart.studio.utils.GifsArtConst;
import com.gifsart.studio.utils.Utils;

import jp.co.cyberagent.android.gpuimage.GPUImage;

/**
 * Created by Tigran on 11/5/15.
 */
public class ApplyGifEffect extends AsyncTask<Void, Bitmap, Void> {

    private Context context;
    private EffectsAdapter effectsAdapter;
    private Bitmap bitmap;
    private GPUEffects.FilterList filters;

    public ApplyGifEffect(Bitmap firstFrame, GPUEffects.FilterList filters, EffectsAdapter effectsAdapter, Context context) {
        this.context = context;
        this.effectsAdapter = effectsAdapter;
        this.bitmap = firstFrame;
        this.filters = filters;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected Void doInBackground(Void... params) {
        GPUImage gpuImage = new GPUImage(context);
        gpuImage.setImage(bitmap);
        for (int i = 0; i < filters.filters.size(); i++) {
            gpuImage.setFilter(GPUEffects.createFilterForType(filters.filters.get(i)));
            publishProgress(Utils.scaleCenterCrop(gpuImage.getBitmapWithFilterApplied(), GifsArtConst.GIF_FRAME_SIZE, GifsArtConst.GIF_FRAME_SIZE));
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
