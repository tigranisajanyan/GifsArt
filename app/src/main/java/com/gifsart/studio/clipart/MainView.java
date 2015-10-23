package com.gifsart.studio.clipart;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;


import com.gifsart.studio.clipart.view.MultitouchHandler;
import com.gifsart.studio.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class MainView extends View {

    private Handler handler = new Handler();
    private Bitmap origBitmap;

    private ArrayList<ClipartView> clipartViewArrayList = new ArrayList<>();
    private int currentPosition;

    private ClipartView clipartView;
    private RectF onDrawRect = new RectF();
    private RectF savedRect = new RectF();
    private RectF bitmapRect = new RectF();
    private Canvas savedCanvas;
    private Paint bitmapPaint = new Paint(Paint.FILTER_BITMAP_FLAG);

    private float scaleFactor = 1f;
    public float currentZoom = 1f;

    private int currentWidth = 0;
    private int currentHeight = 0;

    private int viewWidth = 0;
    private int viewHeight = 0;
    private boolean isDefaluts = true;

    public int origWidth = 0;
    public int origHeight = 0;

    public int scaledWidth = 0;
    public int scaledHeight = 0;

    public int left = 0;
    public int top = 0;

    private int imgWidth;
    private int imgHeight;

    boolean itemAction = false;
    boolean zoomAction = false;
    long multitouchTimer = 0;
    long multitouchTimerStart = 0;

    private float maxZoom = 20f;
    private float minZoom = 0.7f;

    private final Queue<Runnable> sizeChangedActioQueue = new LinkedList<Runnable>();


    DisplayMetrics metrics = getResources().getDisplayMetrics();
    int displayWidth = metrics.widthPixels;
    int displayHeight = metrics.heightPixels;

    public MainView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MainView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MainView(Context context) {
        super(context);
    }

    public MainView(Context context, Bitmap bitmap) {
        super(context);

        origBitmap = bitmap;
        this.imgWidth = bitmap.getWidth();
        this.imgHeight = bitmap.getHeight();
        left = displayWidth / 2 - imgWidth / 2;
        top = displayHeight / 2 - imgHeight / 2;

    }

    public MainView(Context context, String path, boolean userBytesData) {
        super(context);

        if (!TextUtils.isEmpty(path)) {
            File imgFile = new File(path);
            if (imgFile.exists()) {
                if (origBitmap != null) {
                    origBitmap.recycle();
                    origBitmap = null;
                }

                final Bitmap tempBitmap;
                if (userBytesData) {
                    tempBitmap = Utils.readBitmapFromBufferFile(getContext(), path);
                } else {
                    tempBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                }

                if (tempBitmap != null) {
                    origBitmap = tempBitmap.copy(Bitmap.Config.ARGB_8888, true);
                }

                if (origBitmap != null) {
                    this.imgWidth = origBitmap.getWidth();
                    this.imgHeight = origBitmap.getHeight();
                    System.out.println("MainView::  bitmap= " + origBitmap.getWidth() + "x" + origBitmap.getHeight());
                }

                if (tempBitmap != null && !tempBitmap.isRecycled()) {
                    tempBitmap.recycle();
                }
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        viewWidth = w;
        viewHeight = h;

        float oldScaleFactor = scaleFactor;
        int oldLeft = left;
        int oldTop = top;

        initView(isDefaluts, w, h);

        sizeChanged(w, h, oldScaleFactor, oldLeft, oldTop);
        isDefaluts = false;
        while (!sizeChangedActioQueue.isEmpty()) {
            sizeChangedActioQueue.poll().run();
        }
    }

    public void sizeChanged(int viewWidth, int viewHeight, float oldScaleFactor, int oldLeft, int oldTop) {
        initImageData();
        if (clipartView != null) {
            clipartView.sizeChanged(left, top, oldLeft, oldTop, scaleFactor / oldScaleFactor, currentZoom);
        }
        invalidate();
    }

    private void initImageData() {
        int oldW = currentWidth;
        int oldH = currentHeight;

        currentWidth = (int) (scaledWidth * currentZoom);
        currentHeight = (int) (scaledHeight * currentZoom);

        left = left + (oldW - currentWidth) / 2;
        top = top + (oldH - currentHeight) / 2;

        onDrawRect.set(left, top, currentWidth + left, currentHeight + top);
    }


    private void initView(boolean initDefaluts, int viewWidth, int viewHeight) {
        if (origBitmap == null) {
            if (getContext() instanceof ClipartActivity) {
                ((ClipartActivity) getContext()).finish();
            }
            return;
        }

        origWidth = origBitmap.getWidth();
        origHeight = origBitmap.getHeight();

        scaleFactor = Math.min((float) viewWidth / (float) origWidth, (float) viewHeight / (float) origHeight);

        scaledWidth = (int) (origWidth * scaleFactor);
        scaledHeight = (int) (origHeight * scaleFactor);
        if (initDefaluts) {
            initDefaultProperties();
        } else {
            initProperties();
        }

        savedCanvas = new Canvas(origBitmap);
        bitmapRect.set(0, 0, origWidth, origHeight);
    }

    public void initDefaultProperties() {
        currentZoom = 1f;

        currentWidth = scaledWidth;
        currentHeight = scaledHeight;

        left = (viewWidth - scaledWidth) / 2;
        top = (viewHeight - scaledHeight) / 2;

        onDrawRect.set(left, top, scaledWidth + left, scaledHeight + top);
        savedRect.set(onDrawRect);
        invalidate();
    }

    private void initProperties() {
        currentWidth = scaledWidth;
        currentHeight = scaledHeight;

        left = (viewWidth - scaledWidth) / 2;
        top = (viewHeight - scaledHeight) / 2;

        onDrawRect.set(left, top, scaledWidth + left, scaledHeight + top);
        savedRect.set(onDrawRect);
        invalidate();
    }

    public ClipartView addClipart(int clipartResId) {
        if (clipartResId == -1) {
            return null;
        }
        if (clipartView != null) {
            clipartView.cleanBitmaps();
        }
        clipartView = new ClipartView(getContext(), clipartResId, this, 0);

        Runnable initializePosition = new Runnable() {
            @Override
            public void run() {
                clipartView.initSizeParams(viewWidth, viewHeight);
            }
        };

        if (viewWidth != 0 && viewHeight != 0) {
            initializePosition.run();
        } else {
            sizeChangedActioQueue.add(initializePosition);
        }

        clipartViewArrayList.add(clipartView);
        Log.d("gagaga", clipartViewArrayList.size() + "");

        invalidate();
        return clipartView;
//        clipartBitmap = BitmapFactory.decodeResource(getContext().getResources(), clipartResId);
//        invalidate();
    }

    public void removeClipArt() {
        if (clipartView != null) {
            clipartView = null;
        }
        invalidate();
    }

//    public void saveItemsToBitmap() {
//        if (savedCanvas != null) {
//            drawClipart(savedCanvas);
//        }
//    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (origBitmap == null || origBitmap.isRecycled()) {
            return;
        }
        canvas.drawBitmap(origBitmap, null, onDrawRect, bitmapPaint);
//        drawClipart(canvas);

        if (clipartView != null) {
            clipartView.draw(canvas);
        }
    }

//    private void drawClipart(Canvas canvas) {
//        if (clipartBitmap != null && !clipartBitmap.isRecycled()) {
//
//            canvas.save();
//            canvas.scale(0.5F, 0.5F, onDrawRect.centerX(), onDrawRect.centerY());
//            canvas.translate(0, 0);
//            canvas.drawBitmap(clipartBitmap, onDrawRect.centerX() / 2, onDrawRect.centerY() / 2, bitmapPaint);
//            canvas.restore();
//        }
//    }

    public Clipart getClipartItem() {
        if (clipartView != null) {
//            Bitmap clipartBitmap = clipartView.getBitmap();
//            Bitmap resultBitmap = Bitmap.createBitmap(origBitmap.getWidth(), origBitmap.getHeight(), Bitmap.Config.ARGB_8888);
//            Canvas canvas = new Canvas(resultBitmap);
//            clipartView.draw(canvas);
//            clipartView.draw(canvas, onImgRect.centerX(), onImgRect.centerY(), scaleFactor, currentZoom, 0);
            WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x;
            int height = size.y;
            Clipart clipart = new Clipart(clipartView.getBitmap(), (int) (clipartView.getClipartUnscaledX() / clipartView.getScaleX()), (int) (clipartView.getClipartUnscaledY() / clipartView.getScaleY()));
            clipart.setScaleX(clipartView.getScaleX());
            clipart.setScaleY(clipartView.getScaleY());
            clipart.setRotation(clipartView.getRotation());
            return clipart;
        }
//        return new Clipart(clipartBitmap, (int) onDrawRect.centerX(), (int) onDrawRect.centerY());
        return null;
    }

    public Bitmap getOriginBitmapCopy() {
        try {
            return Bitmap.createBitmap(origBitmap, 0, 0, origBitmap.getWidth(), origBitmap.getHeight());
        } catch (OutOfMemoryError e) {
            return null;
        }
    }

    public Bitmap getOrigBitmap() {
        return origBitmap;
    }

    public RectF getOnDrawRect() {
        return onDrawRect;
    }

    ///////////////Touch events

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final float x = event.getX();
        final float y = event.getY();
        int eventAction = event.getAction();

        if (eventAction == MotionEvent.ACTION_DOWN) {
            multitouchTimerStart = System.currentTimeMillis();
            zoomAction = false;
            itemAction = false;
        }

        if (clipartView != null && clipartView.isDrawHandle()) {
            clipartView.onTouchEvent(event);

            if (clipartView.pinchOutOfBounds && MultitouchHandler.getInstance().handlePitchZoom(event, onDrawRect, savedRect, maxZoom, minZoom)) {
                updateZoomDragParams(maxZoom);
//                if (ItemContext.getContext().getSelectedItem().isActive()) {
//                clearDraw();
//                drawItemsOnMBitmap(true);
                //TODO
//                }
            }
        }

//        if (currentImageAction == Graphics.ACTION_DRAG) {
//            dragImage(eventAction, x, y);
//        }

        switch (eventAction) {
            case MotionEvent.ACTION_DOWN:
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!zoomAction) {
                            touchDown(x, y);
                            invalidate();
                        }
                    }
                }, 70);
                break;

            case MotionEvent.ACTION_MOVE:
                if (itemAction && clipartView != null) {
                    clipartView.touch_move(x, y);

                }
                break;

            case MotionEvent.ACTION_UP:
                if (itemAction && clipartView != null) {
                    clipartView.touch_up();
                    itemAction = false;
                }

                break;
        }// end action switch

        invalidate();

        return true;
    }// end onTouch


    public void updateZoomDragParams(float maxZoom) {

        float zoom = (onDrawRect.width() + 0.5f) / savedRect.width();
        if (zoom > maxZoom) {
            return;
        }

        currentWidth = (int) (onDrawRect.width() + 0.5f);
        currentHeight = (int) (onDrawRect.height() + 0.5f);
        currentZoom = currentWidth / savedRect.width();

        left = (int) (onDrawRect.left + 0.5f);
        top = (int) (onDrawRect.top + 0.5f);
    }

    private boolean touchDown(float x, float y) {
        if (clipartView != null) {
            itemAction = clipartView.touch_down(x, y);
            return true;
        }

        return false;
    }

}
