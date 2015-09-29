package com.gifsart.studio.utils;

import android.content.Context;
import android.os.AsyncTask;

/**
 * Created by Tigran on 9/28/15.
 */
public class Timer extends AsyncTask<Void, Integer, Void> {

    private int counter = 0;
    private boolean startStop = false;
    private OnUpdate update;

    public Timer(int counter) {
        this.counter = counter;
    }

    @Override
    protected void onPostExecute(Void o) {
        super.onPostExecute(o);
        startStop = false;
    }

    @Override
    protected Void doInBackground(Void... params) {

        while (startStop) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            publishProgress(counter + 100);
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        startStop = true;

    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        counter = values[0];
        update.onUpdated(counter);
    }

    public void setOnDownloadedListener(OnUpdate l) {
        update = l;
    }


    public interface OnUpdate {
        void onUpdated(int cnt);
    }

}
