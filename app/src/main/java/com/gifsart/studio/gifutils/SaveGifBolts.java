package com.gifsart.studio.gifutils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Environment;
import android.util.Log;

import com.gifsart.studio.activity.MakeGifActivity;
import com.gifsart.studio.clipart.Clipart;
import com.gifsart.studio.clipart.MainView;
import com.gifsart.studio.effects.GPUEffects;
import com.gifsart.studio.item.GifItem;
import com.gifsart.studio.utils.GifsArtConst;
import com.gifsart.studio.utils.Type;
import com.gifsart.studio.utils.Utils;
import com.picsart.studio.gifencoder.GifEncoder;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Callable;

import bolts.Task;
import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageView;

/**
 * Created by Tigran on 11/3/15.
 */
public class SaveGifBolts {

    private static ArrayList<Bitmap> bitmapArrayList;
    private static int pos = 0;
    private static int size = 0;

    public static Task<Void> doSquareFitTask(final MakeGifActivity.SquareFitMode squareFitMode, final ArrayList<GifItem> gifItems) {
        return Task.callInBackground(new Callable<Void>() {
            @Override
            public Void call() throws IOException {
                for (int i = 0; i < gifItems.size(); i++) {
                    if (gifItems.get(i).getType() == Type.IMAGE) {
                        gifItems.get(i).setBitmap(doSquareFit(squareFitMode, gifItems.get(i).getBitmap()));
                    } else if (gifItems.get(i).getType() == Type.GIF) {
                        for (int j = 0; j < gifItems.get(i).getBitmaps().size(); j++) {
                            gifItems.get(i).getBitmaps().set(j, doSquareFit(squareFitMode, gifItems.get(i).getBitmaps().get(j)));
                        }
                    } else if (gifItems.get(i).getType() == Type.VIDEO) {
                        for (int j = 0; j < gifItems.get(i).getBitmaps().size(); j++) {
                            gifItems.get(i).getBitmaps().set(j, doSquareFit(squareFitMode, gifItems.get(i).getBitmaps().get(j)));
                        }
                    }
                }
                return null;
            }
        });
    }

    private static Bitmap doSquareFit(MakeGifActivity.SquareFitMode squareFitMode, Bitmap bitmap) {
        if (squareFitMode == MakeGifActivity.SquareFitMode.FIT_MODE_SQUARE_FIT) {
            return Utils.squareFit(bitmap, GifsArtConst.GIF_FRAME_SIZE);
        } else if (squareFitMode == MakeGifActivity.SquareFitMode.FIT_MODE_SQUARE) {
            return Utils.scaleCenterCrop(bitmap, GifsArtConst.GIF_FRAME_SIZE, GifsArtConst.GIF_FRAME_SIZE);
        }
        return bitmap;
    }

    public static MakeGifActivity.SquareFitMode checkSquareFitMode(ArrayList<GifItem> gifItems, MakeGifActivity.SquareFitMode squareFitMode) {
        if (squareFitMode == MakeGifActivity.SquareFitMode.FIT_MODE_ORIGINAL) {
            for (int i = 0; i < gifItems.size(); i++) {
                for (int j = 0; j < gifItems.size(); j++) {
                    if (gifItems.get(i).getBitmap().getWidth() != gifItems.get(j).getBitmap().getWidth() || gifItems.get(i).getBitmap().getHeight() != gifItems.get(j).getBitmap().getHeight()) {
                        return MakeGifActivity.SquareFitMode.FIT_MODE_SQUARE_FIT;
                    }
                }
            }
        }
        return squareFitMode;
    }

    public static Task<Void> addFramesToGifTask(final String fileName, final ArrayList<GifItem> gifItems) {
        return Task.callInBackground(new Callable<Void>() {
            @Override
            public Void call() throws IOException {
                File outFile = new File(fileName);
                try {
                    BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(outFile));
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();

                    AnimatedGifEncoder animatedGifEncoder = new AnimatedGifEncoder();
                    animatedGifEncoder.setRepeat(0);
                    animatedGifEncoder.setQuality(256);
                    animatedGifEncoder.start(bos);

                    for (int i = 0; i < gifItems.size(); i++) {

                        if (gifItems.get(i).getType() == Type.IMAGE) {
                            addGifFrame(animatedGifEncoder, gifItems.get(i).getBitmap(), gifItems.get(i).getCurrentDuration());
                        } else if (gifItems.get(i).getType() == Type.GIF) {
                            for (int j = 0; j < gifItems.get(i).getBitmaps().size(); j++) {
                                addGifFrame(animatedGifEncoder, gifItems.get(i).getBitmaps().get(j), gifItems.get(i).getCurrentDuration());
                            }
                        } else if (gifItems.get(i).getType() == Type.VIDEO) {
                            for (int j = 0; j < gifItems.get(i).getBitmaps().size(); j++) {
                                addGifFrame(animatedGifEncoder, gifItems.get(i).getBitmaps().get(j), gifItems.get(i).getCurrentDuration());
                            }
                        }
                    }
                    animatedGifEncoder.finish();
                    bufferedOutputStream.write(bos.toByteArray());
                    bufferedOutputStream.flush();
                    bufferedOutputStream.close();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                /*GifEncoder gifEncoder = new GifEncoder();
                gifEncoder.init(Environment.getExternalStorageDirectory() + "/test.gif", 500, 500, 256, 10, 100);
                int[] pixels = new int[500 * 500];
                for (int i = 0; i < gifItems.size() - 1; i++) {
                    gifItems.get(i).getBitmap().getPixels(pixels, 0, 500, 0, 0, 500, 500);
                    gifEncoder.addFrame(pixels);
                }
                gifEncoder.close();*/
                return null;
            }
        });
    }

    private static void addGifFrame(AnimatedGifEncoder animatedGifEncoder, Bitmap bitmap, int duration) {
        animatedGifEncoder.setDelay(duration);
        animatedGifEncoder.addFrame(bitmap);
    }

    public static Task<Void> applyEffect(final ArrayList<GifItem> gifItems, final GPUImageFilter gpuImageFilter, final Context context) {
        return Task.callInBackground(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                if (gpuImageFilter != GPUEffects.createFilterForType(GPUEffects.FilterType.NONE)) {
                    GPUImage gpuImage = new GPUImage(context);
                    gpuImage.setFilter(gpuImageFilter);
                    for (int i = 0; i < gifItems.size(); i++) {
                        if (gifItems.get(i).getType() == Type.IMAGE) {
                            gpuImage.setImage(gifItems.get(i).getBitmap());
                            gifItems.get(i).setBitmap(gpuImage.getBitmapWithFilterApplied());
                        } else {
                            for (int j = 0; j < gifItems.get(i).getBitmaps().size(); j++) {
                                gpuImage.setImage(gifItems.get(i).getBitmaps().get(j));
                                gifItems.get(i).getBitmaps().set(j, gpuImage.getBitmapWithFilterApplied());
                            }
                        }
                    }
                }
                return null;
            }
        });
    }

    public static Task<Void> setClipartsOnGifTask(final ArrayList<GifItem> gifItems, final MainView mainView) {
        return Task.callInBackground(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                for (int i = 0; i < gifItems.size(); i++) {

                    if (gifItems.get(i).getType() == Type.IMAGE) {
                        drawClipart(mainView, gifItems.get(i).getBitmap());
                    } else if (gifItems.get(i).getType() == Type.GIF) {
                        for (int j = 0; j < gifItems.get(i).getBitmaps().size(); j++) {
                            drawClipart(mainView, gifItems.get(i).getBitmaps().get(j));
                        }
                    } else if (gifItems.get(i).getType() == Type.VIDEO) {
                        for (int j = 0; j < gifItems.get(i).getBitmaps().size(); j++) {
                            drawClipart(mainView, gifItems.get(i).getBitmaps().get(j));
                        }
                    }
                }
                return null;
            }
        });
    }

    private static void drawClipart(MainView mainView, Bitmap bitmap) {
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();

        if (mainView.getClipartItem() != null) {
            Matrix transformMatrix = new Matrix();
            transformMatrix.postRotate(mainView.getClipartItem().getRotation(), mainView.getClipartItem().getBitmap().getWidth() / 2, mainView.getClipartItem().getBitmap().getHeight() / 2);
            transformMatrix.postTranslate(mainView.getClipartItem().getX(), mainView.getClipartItem().getY());
            transformMatrix.postScale(mainView.getClipartItem().getScaleX(), mainView.getClipartItem().getScaleY());
            canvas.drawBitmap(bitmap, 0, 0, paint);
            canvas.scale((float) Math.max(bitmap.getWidth(), bitmap.getHeight()) / mainView.getWidth(), (float) Math.max(bitmap.getWidth(), bitmap.getHeight()) / mainView.getWidth(), 0, 0);
            canvas.drawBitmap(mainView.getClipartItem().getBitmap(), transformMatrix, paint);
        } else {
            canvas.drawBitmap(bitmap, 0, 0, paint);
        }
    }

    public static Task<Void> addMaskToGifTask(final ArrayList<GifItem> gifItems, final int resourceId, final int maskTransparency, final Context context) {
        return Task.callInBackground(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                bitmapArrayList = GifUtils.getGifFramesFromResources(context, resourceId);
                size = bitmapArrayList.size();

                for (int i = 0; i < gifItems.size(); i++) {
                    if (gifItems.get(i).getType() == Type.IMAGE) {
                        drawMask(gifItems.get(i).getBitmap(), bitmapArrayList.get(pos % size), maskTransparency);
                        pos++;
                    } else if (gifItems.get(i).getType() == Type.GIF) {
                        for (int j = 0; j < gifItems.get(i).getBitmaps().size(); j++) {
                            drawMask(gifItems.get(i).getBitmaps().get(j), bitmapArrayList.get(pos % size), maskTransparency);
                            pos++;
                        }
                    } else if (gifItems.get(i).getType() == Type.VIDEO) {
                        for (int j = 0; j < gifItems.get(i).getBitmaps().size(); j++) {
                            drawMask(gifItems.get(i).getBitmaps().get(j), bitmapArrayList.get(pos % size), maskTransparency);
                            pos++;
                        }
                    }
                }
                return null;
            }
        });
    }

    private static void drawMask(Bitmap mainFrame, Bitmap maskFrame, int maskTransparency) {
        Canvas canvas = new Canvas(mainFrame);
        Paint paint = new Paint();
        paint.setAlpha(maskTransparency);
        Rect originalRect = new Rect(0, 0, maskFrame.getWidth(), maskFrame.getHeight());
        Rect newRect = new Rect(0, 0, canvas.getWidth(), canvas.getHeight());
        canvas.drawBitmap(maskFrame, originalRect, newRect, paint);
    }

}
