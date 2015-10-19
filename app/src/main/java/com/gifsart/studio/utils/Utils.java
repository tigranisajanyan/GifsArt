package com.gifsart.studio.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.decoder.BitmapManager;
import com.decoder.PhotoUtils;
import com.gifsart.studio.gifutils.GifDecoder;
import com.gifsart.studio.item.GalleryItem;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;

import pl.droidsonroids.gif.GifDrawable;


public class Utils {

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {

        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(File file1,
                                                         int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file1.toString(), options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(file1.toString(), options);
    }

    public static double getBitmapWidth(Context context) {

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        double halfWidth = metrics.widthPixels / 3;

        return halfWidth;
    }

    public static double getBitmapHeight(Context context, String file) throws IOException {


        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file, bounds);
        int width = bounds.outWidth;
        int height = bounds.outHeight;

        ExifInterface exif = null;
        try {
            exif = new ExifInterface(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
        int orientation = orientString != null ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;

        int rotationAngle = 0;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;

        if (rotationAngle == 90 || rotationAngle == 270) {
            width = bounds.outHeight;
            height = bounds.outWidth;
        }

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        double halfWidth = metrics.widthPixels / 3;
        double a = width / halfWidth;
        double halfHeight = height / a;

        return halfHeight;
    }

    public static Bitmap scaleCenterCrop(Bitmap source, int newHeight, int newWidth) {
        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();

        // Compute the scaling factors to fit the new height and width, respectively.
        // To cover the final image, the final scaling will be the biggerF
        // of these two.
        float xScale = (float) newWidth / sourceWidth;
        float yScale = (float) newHeight / sourceHeight;
        float scale = Math.max(xScale, yScale);

        // Now get the size of the source bitmap when scaled
        float scaledWidth = scale * sourceWidth;
        float scaledHeight = scale * sourceHeight;

        // Let's find out the upper left coordinates if the scaled bitmap
        // should be centered in the new size give by the parameters
        float left = (newWidth - scaledWidth) / 2;
        float top = (newHeight - scaledHeight) / 2;

        // The target rectangle for the new, scaled version of the source bitmap will now
        // be
        RectF targetRect = new RectF(left, top, left + scaledWidth, top + scaledHeight);

        // Finally, we create a new bitmap of the specified size and draw our new,
        // scaled bitmap onto it.
        Bitmap dest = Bitmap.createBitmap(newWidth, newHeight, source.getConfig());
        Canvas canvas = new Canvas(dest);
        canvas.drawBitmap(source, null, targetRect, null);

        return dest;
    }


    public static void initImageLoader(Context context) {
        try {
            String CACHE_DIR = Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + "/.temp_tmp";
            new File(CACHE_DIR).mkdirs();

            File cacheDir = StorageUtils.getOwnCacheDirectory(context,
                    CACHE_DIR);

            DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                    .cacheOnDisc(true)
                    .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
                    .considerExifParams(true)
                    .cacheOnDisk(true)
                    .cacheInMemory(true)
                    .considerExifParams(true)
                    .decodingOptions(new BitmapFactory.Options())
                    .bitmapConfig(Bitmap.Config.RGB_565).build();

            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                    /*.memoryCacheExtraOptions(1024, 1024) // width, height
                    .diskCacheExtraOptions(1024, 1024, new BitmapProcessor() {
                        @Override
                        public Bitmap process(Bitmap bitmap) {
                            return null;
                        }
                    })
                    .threadPoolSize(3)
                    .threadPriority(Thread.MIN_PRIORITY + 2)*/
                    .denyCacheImageMultipleSizesInMemory()
                    .memoryCache(new UsingFreqLimitedMemoryCache(10 * 1024 * 1024)) // 3 Mb
                    .diskCache(new UnlimitedDiscCache(cacheDir))
                    .diskCacheFileNameGenerator(new HashCodeFileNameGenerator())
                    .imageDownloader(new BaseImageDownloader(context)) // connectTimeout (5 s), readTimeout (30 s)
                    .defaultDisplayImageOptions(defaultOptions)
                    .build();

            ImageLoader.getInstance().init(config);

        } catch (Exception e) {
        }
    }

    public static ArrayList<GalleryItem> getGalleryPhotos(Activity activity) {
        ArrayList<GalleryItem> galleryList = new ArrayList();

        try {
            final String[] columns = {MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media._ID};
            final String orderBy = MediaStore.Images.Media._ID;

            Cursor imagecursor = activity.managedQuery(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns,
                    null, null, orderBy);

            if (imagecursor != null && imagecursor.getCount() > 0) {

                while (imagecursor.moveToNext()) {
                    GalleryItem item = new GalleryItem();

                    int dataColumnIndex = imagecursor
                            .getColumnIndex(MediaStore.Images.Media.DATA);

                    File file = new File(imagecursor.getString(dataColumnIndex));
                    if (file.exists()) {
                        item.setImagePath(imagecursor.getString(dataColumnIndex));
                        /*if (Utils.getMimeType(gif_item.getImagePath()).toString().toLowerCase().contains("gif")) {
                            type = GalleryItem.Type.GIF;
                        }*/
                        //gif_item.setHeight((int) Utils.getBitmapHeight(activity, gif_item.getImagePath()));
                        //gif_item.setWidth((int) Utils.getBitmapWidth(activity));
                        galleryList.add(item);
                    }

                }
            }

            galleryList.add(new GalleryItem(Environment.getExternalStorageDirectory() + "/myvideo.mp4"));  /////

        } catch (Exception e) {
            e.printStackTrace();
        }

        // show newest photo at beginning of the list
        Collections.reverse(galleryList);
        return galleryList;
    }


    public static void clearDir(File dir) {
        try {
            File[] files = dir.listFiles();
            if (files != null)
                for (File f : files) {
                    if (f.isDirectory())
                        clearDir(f);
                    f.delete();
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void createDir(String fileName) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/" + fileName);
        if (!myDir.exists()) {
            myDir.mkdirs();
        } else {
            clearDir(myDir);
            File file = new File(myDir.toString());
            file.delete();
            myDir.mkdirs();
        }
    }

    public static String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] projection = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, projection, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                String realPath = cursor.getString(idx);
                cursor.close();
                return realPath;
            } else {
                return contentUri.getPath();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return contentUri.getPath();
        } finally {
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static byte[] fileToByteArray(String path) {
        File file = new File(path);
        int size = (int) file.length();
        byte[] bytes = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
            buf.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bytes;
    }

    public static float dpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * (metrics.densityDpi / 160f);
    }

    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * (metrics.densityDpi / 160f);
    }

    public static float convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }

    public static void getMidPoint(PointF lineStart, PointF lineEnd, PointF outPoint) {
        outPoint.set((lineStart.x + lineEnd.x) / 2, (lineStart.y + lineEnd.y) / 2);
    }

    public static float getAngleBetweenLines(PointF lineStart1, PointF lineEnd1, PointF lineStart2, PointF lineEnd2) {
        float dx1 = lineStart1.x - lineEnd1.x;
        float dy1 = lineStart1.y - lineEnd1.y;

        float dx2 = lineStart2.x - lineEnd2.x;
        float dy2 = lineStart2.y - lineEnd2.y;

        double radians = Math.atan2(dy2, dx2) - Math.atan2(dy1, dx1);

        return (float) Math.toDegrees(radians);
    }

    public static Bitmap readBitmapFromBufferFile(Context context, String bytesFilePath) {
        if (!TextUtils.isEmpty(bytesFilePath)) {
            SharedPreferences sharedPreferences = context.getSharedPreferences("pics_art_video_editor", Context.MODE_PRIVATE);
            int bufferSize = sharedPreferences.getInt("buffer_size", 0);
            int width = sharedPreferences.getInt("frame_width", 0);
            int height = sharedPreferences.getInt("frame_height", 0);
            ByteBuffer buffer = PhotoUtils.readBufferFromFile(bytesFilePath, bufferSize);
            return PhotoUtils.fromBufferToBitmap(width, height, buffer);
        }
        return null;
    }

    public static void shareImage(Activity activity, String outputDir) {
        Intent share = new Intent(Intent.ACTION_SEND);

        // If you want to share a png image only, you can do:
        // setType("image/png"); OR for jpeg: setType("image/jpeg");
        share.setType("image/*");

        // Make sure you put example png image named myImage.png in your
        // directory
        String imagePath = outputDir;

        File imageFileToShare = new File(imagePath);

        Uri uri = Uri.fromFile(imageFileToShare);
        share.putExtra(Intent.EXTRA_STREAM, uri);

        activity.startActivity(Intent.createChooser(share, "Share Image!"));
    }

    public static Type getMimeType(String url) {
        String stringType = null;
        Type type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            stringType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        if (stringType != null) {
            if (stringType.toLowerCase().contains("image") && !stringType.toLowerCase().contains("gif")) {
                type = Type.IMAGE;
            } else if (stringType.toLowerCase().contains("gif")) {
                type = Type.GIF;
            } else if (stringType.toLowerCase().contains("video")) {
                type = Type.VIDEO;
            }
        } else {
            type = Type.IMAGE;
        }

        return type;
    }

    public static boolean haveNetworkConnection(Context context) {
        ConnectivityManager connectivityMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connectivityMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = connectivityMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        // Check if wifi or mobile network is available or not. If any of them is
        // available or connected then it will return true, otherwise false;
        if (wifi != null) {
            if (wifi.isConnected()) {
                return true;
            }
        }
        if (mobile != null) {
            if (mobile.isConnected()) {
                return true;
            }
        }
        return false;
    }

    public static Bitmap squareFit(Bitmap bitmap, int size) {
        Bitmap bmOverlay = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        bmOverlay.setDensity(bitmap.getDensity());
        bmOverlay.eraseColor(Color.WHITE);

        Canvas canvas = new Canvas(bmOverlay);
        if (bitmap.getWidth() > bitmap.getHeight()) {
            Bitmap bitmap1 = Bitmap.createScaledBitmap(bitmap, size, (size * bitmap.getHeight()) / bitmap.getWidth(), false);
            canvas.drawBitmap(bitmap1, 0, (size - bitmap1.getHeight()) / 2, new Paint());
        } else {
            Bitmap bitmap1 = Bitmap.createScaledBitmap(bitmap, (size * bitmap.getWidth()) / bitmap.getHeight(), size, false);
            canvas.drawBitmap(bitmap1, (size - bitmap1.getWidth()) / 2, 0, new Paint());
        }
        return bmOverlay;
    }

    public static int checkVideoFrameDuration(String videoPath, int frameCount) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(videoPath);

        int duration = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
        return duration / frameCount;
    }

    public static Bitmap getVideoFirstFrame(String path) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(path);
        Bitmap b = mmr.getFrameAtTime(100000, MediaMetadataRetriever.OPTION_CLOSEST); // frame at 100 mls
        return b;
    }

    public static byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

}
