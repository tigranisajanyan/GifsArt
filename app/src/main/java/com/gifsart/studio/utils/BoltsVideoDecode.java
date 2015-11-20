package com.gifsart.studio.utils;

import android.content.Context;

import com.decoder.VideoDecoder;
import java.util.concurrent.Callable;

import bolts.Task;

/**
 * Created by Tigran on 11/18/15.
 */
public class BoltsVideoDecode {

    public static Task<Void> decodeVideo(final String path, final String outpuPath, final Context context) {
        return Task.callInBackground(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                VideoDecoder videoDecoder = new VideoDecoder(context, path, Integer.MAX_VALUE, GifsArtConst.VIDEO_FRAME_SCALE_SIZE, outpuPath);
                videoDecoder.extractVideoFrames();
                videoDecoder.setOnDecodeFinishedListener(new VideoDecoder.OnDecodeFinishedListener() {
                    @Override
                    public void onFinish(boolean isDone) {
                        /*if (isDone) {
                            tcs.setResult(isDone);
                        } else {
                            tcs.setError(new Exception("decode isn't done"));
                        }*/
                    }
                });
                return null;
            }
        });
    }
}
