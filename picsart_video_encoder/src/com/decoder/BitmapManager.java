package com.decoder;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

public class BitmapManager {

	public static final String TAG = BitmapManager.class.getSimpleName();

	public static final boolean isDebug = true;

	private static int allocMemory = 0;

	private static ConcurrentHashMap<String, ArrayList<WeakReference<Bitmap>>> createdBitmaps = new ConcurrentHashMap<>();

	private static final Object lockObj = new Object();

	private static boolean isFakeDeallocatingAvailable = true;

	public static boolean isThrowOutOfMemory = false;

	private static void addToCreatedBitmaps(Bitmap bmp, String tag) {
		if (tag != null) {
			ArrayList<WeakReference<Bitmap>> tagedList = createdBitmaps.get(tag);
			if (tagedList != null)
				tagedList.add(new WeakReference<>(bmp));
			else {
				tagedList = new ArrayList<>();
				tagedList.add(new WeakReference<>(bmp));
				createdBitmaps.put(tag, tagedList);
			}

		}
	}

	public static void addBitmapByTag(Bitmap bmp, String tag, boolean addMemory) {
		if (bmp != null) {
			synchronized (lockObj) {
				if (addMemory) {
					addMemory(bmp.getRowBytes() * bmp.getHeight());
					if (isDebug) {
                        Log.d(TAG, "creating bitmap width:" + bmp.getWidth() + " height:" + bmp.getHeight() + " config:" + bmp.getConfig() + " memoryKB: " + getAllocMemoryInKb());
                    }
				}
				addToCreatedBitmaps(bmp, tag);
			}
		}
	}

	public static Bitmap createBitmap(int w, int h, Config config, String tag) {
		Bitmap bmp = null;
		try {
			bmp = Bitmap.createBitmap(w, h, config);
		} catch (OutOfMemoryError e) {
			Log.e(TAG, "OOM while creating bitmap");
			System.gc();
			try {
				bmp = Bitmap.createBitmap(w, h, config);
			} catch (OutOfMemoryError e2) {
				Log.e(TAG, "OOM while creating bitmap 2");
				if (isThrowOutOfMemory) {
					isThrowOutOfMemory = false;
					throw new OutOfMemoryError(e2.getMessage());
				}
			}
		}
		int size = 0;
		if (bmp != null) {
			addMemory(size = bmp.getRowBytes() * bmp.getHeight());
			if (isDebug) {
                Log.d(TAG, "creating bitmap width:" + bmp.getWidth() + " height:" + bmp.getHeight() + " config:" + bmp.getConfig() + " memoryKB: " + getAllocMemoryInKb());
            }
			addToCreatedBitmaps(bmp, tag);
		}
		deallocateMemory(size);
		return bmp;
	}

	public static Bitmap createBitmap(int w, int h, Config config) {
		return createBitmap(w, h, config, null);
	}

	public static Bitmap createBitmap(Bitmap source, int x, int y, int width, int height, Matrix m, boolean filter, String tag) {
		Bitmap bmp = null;
		try {
			bmp = Bitmap.createBitmap(source, x, y, width, height, m, filter);
		} catch (OutOfMemoryError e) {
			Log.e(TAG, "OOM while creating bitmap");
			System.gc();
			try {
				bmp = Bitmap.createBitmap(source, x, y, width, height, m, filter);
			} catch (OutOfMemoryError e2) {
				Log.e(TAG, "OOM while creating bitmap 2");
				if (isThrowOutOfMemory) {
					isThrowOutOfMemory = false;
					throw new OutOfMemoryError(e2.getMessage());
				}
			}
		}
		int size = 0;
		if (bmp != null) {
			addMemory(size = bmp.getRowBytes() * bmp.getHeight());
			if (isDebug) {
                Log.d(TAG, "creating bitmap width:" + bmp.getWidth() + " height:" + bmp.getHeight() + " config:" + bmp.getConfig() + " memoryKB: " + getAllocMemoryInKb());
            }
			addToCreatedBitmaps(bmp, tag);
		}
		deallocateMemory(size);
		return bmp;
	}

	public static Bitmap createBitmap(Bitmap source, int x, int y, int width, int height, Matrix m, boolean filter) {
		return createBitmap(source, x, y, width, height, m, filter, null);
	}

	public static Bitmap createBitmap(Bitmap source, int x, int y, int width, int height) {
		return createBitmap(source, x, y, width, height, null, false);
	}

	public static Bitmap createBitmap(Bitmap src) {
		return createBitmap(src, 0, 0, src.getWidth(), src.getHeight());
	}

	public static Bitmap createScaledBitmap(Bitmap src, int dstWidth, int dstHeight, boolean filter, String tag) {
		Bitmap bmp = null;
		try {
			bmp = Bitmap.createScaledBitmap(src, dstWidth, dstHeight, filter);
		} catch (OutOfMemoryError e) {
			Log.e(TAG, "OOM while creating bitmap");
			System.gc();
			try {
				bmp = Bitmap.createScaledBitmap(src, dstWidth, dstHeight, filter);
			} catch (OutOfMemoryError e2) {
				Log.e(TAG, "OOM while creating bitmap 2");
				if (isThrowOutOfMemory) {
					isThrowOutOfMemory = false;
					throw new OutOfMemoryError(e2.getMessage());
				}
			}
		}
		int size = 0;
		if (bmp != null) {
			addMemory(size = bmp.getRowBytes() * bmp.getHeight());
			if (isDebug) {
                Log.d(TAG, "creating bitmap width:" + bmp.getWidth() + " height:" + bmp.getHeight() + " config:" + bmp.getConfig() + " memoryKB: " + getAllocMemoryInKb());
            }
			addToCreatedBitmaps(bmp, tag);
		}
		deallocateMemory(size);
		return bmp;
	}

	public static Bitmap createScaledBitmap(Bitmap src, int dstWidth, int dstHeight, boolean filter) {
		return createScaledBitmap(src, dstWidth, dstHeight, filter, null);
	}

	public static Bitmap copy(Bitmap src, boolean isMutable, Config config) {
		return copy(src, config, isMutable, null);
	}

	public static Bitmap copy(Bitmap src, Config config, boolean isMutable, String tag) {
		Bitmap bmp = null;
		try {
			bmp = src.copy(config, isMutable);
		} catch (OutOfMemoryError e) {
			Log.e(TAG, "OOM while copying bitmap");
			System.gc();
			try {
				bmp = src.copy(config, isMutable);
			} catch (OutOfMemoryError e2) {
				Log.e(TAG, "OOM while creating bitmap 2");
				if (isThrowOutOfMemory) {
					isThrowOutOfMemory = false;
					throw new OutOfMemoryError(e2.getMessage());
				}
			}
		}
		int size = 0;
		if (bmp != null) {
			addMemory(size = bmp.getRowBytes() * bmp.getHeight());
			if (isDebug) {
                Log.d(TAG, "copying bitmap width:" + bmp.getWidth() + " height:" + bmp.getHeight() + " config:" + bmp.getConfig() + " memoryKB: " + getAllocMemoryInKb());
            }
			addToCreatedBitmaps(bmp, tag);
		}
		deallocateMemory(size);
		return bmp;
	}

	public static Bitmap copy(Bitmap src, Config config, boolean isMutable) {
		return copy(src, config, isMutable, null);
	}

	public static Bitmap decodeFile(String path) {
		return decodeFile(path, null);
	}

	public static Bitmap decodeFile(String path, Options opts, String tag) {
		Bitmap bmp = null;
		try {
			bmp = BitmapFactory.decodeFile(path, opts);
		} catch (OutOfMemoryError e) {
			Log.e(TAG, "OOM while loading bitmap path: " + path);
			System.gc();
			try {
				bmp = BitmapFactory.decodeFile(path, opts);
			} catch (OutOfMemoryError e2) {
				Log.e(TAG, "OOM while creating bitmap 2");
				if (isThrowOutOfMemory) {
					isThrowOutOfMemory = false;
					throw new OutOfMemoryError(e2.getMessage());
				}
			}
		}
		int size = 0;
		if (bmp != null && (opts == null || !opts.inJustDecodeBounds)) {
			addMemory(size = bmp.getRowBytes() * bmp.getHeight());
			if (isDebug) {
                Log.d(TAG, "loadBitmap from path: " + path + " width: " + bmp.getWidth() + " memoryKB: " + getAllocMemoryInKb() + " height: " + bmp.getHeight() + getOptionsString(opts));
            }
			addToCreatedBitmaps(bmp, tag);
		}
		deallocateMemory(size);
		return bmp;
	}

	public static Bitmap decodeFile(String path, Options opts) {
		return decodeFile(path, opts, null);
	}

	public static Bitmap decodeByteArray(byte[] data, int offset, int length) {
		return decodeByteArray(data, offset, length, null);
	}

	public static Bitmap decodeByteArray(byte[] data, int offset, int length, Options opts) {
		return decodeByteArray(data, offset, length, opts, null);
	}

	public static Bitmap decodeByteArray(byte[] data, int offset, int length, Options opts, String tag) {
		Bitmap bmp = null;
		try {
			bmp = BitmapFactory.decodeByteArray(data, offset, length, opts);
		} catch (OutOfMemoryError e) {
			Log.e(TAG, "OOM while loading bitmap from bytearray");
			System.gc();
			try {
				bmp = BitmapFactory.decodeByteArray(data, offset, length, opts);
			} catch (OutOfMemoryError e2) {
				Log.e(TAG, "OOM while creating bitmap 2");
				if (isThrowOutOfMemory) {
					isThrowOutOfMemory = false;
					throw new OutOfMemoryError(e2.getMessage());
				}
			}
		}

		int size = 0;

		if (bmp != null && (opts == null || !opts.inJustDecodeBounds)) {
			addMemory(size = bmp.getRowBytes() * bmp.getHeight());
			if (isDebug) {
                Log.d(TAG, "loadBitmap from byte array width: " + bmp.getWidth() + " memoryKB: " + getAllocMemoryInKb() + " height: " + bmp.getHeight() + getOptionsString(opts));
            }
			addToCreatedBitmaps(bmp, tag);
		}

		deallocateMemory(size);
		return bmp;
	}

	public static Bitmap decodeResource(Resources res, int id) {
		return decodeResource(res, id, null);
	}

	public static Bitmap decodeResource(Resources res, int id, Options opts) {
		return decodeResource(res, id, opts, null);
	}

	public static Bitmap decodeResource(Resources res, int id, Options opts, String tag) {
		Bitmap bmp = null;

		try {
			bmp = BitmapFactory.decodeResource(res, id, opts);
		} catch (OutOfMemoryError e) {
			Log.e(TAG, "OOM while loading bitmap from resource , total allocated memory == " + getAllocMemoryInKb());
			System.gc();
			try {
				bmp = BitmapFactory.decodeResource(res, id, opts);
			} catch (OutOfMemoryError e2) {
				Log.e(TAG, "OOM while creating bitmap 2");
				if (isThrowOutOfMemory) {
					isThrowOutOfMemory = false;
					throw new OutOfMemoryError(e2.getMessage());
				}
			}
		}

		int size = 0;

		if (bmp != null && (opts == null || !opts.inJustDecodeBounds)) {
			addMemory(size = bmp.getRowBytes() * bmp.getHeight());
			if (isDebug) {
				if (opts == null)
					Log.d(TAG, "decodeResource from stream width:" + bmp.getWidth() + " height:" + bmp.getHeight() + " memoryKB: " + getAllocMemoryInKb());
				else {
					Log.d(TAG, "decodeResource from stream width:" + bmp.getWidth() + " height:" + bmp.getHeight() + " memoryKB: " + getAllocMemoryInKb() + getOptionsString(opts));
				}
			}
			addToCreatedBitmaps(bmp, tag);
		}
		deallocateMemory(size);
		return bmp;
	}

	public static Bitmap decodeStream(InputStream is) {
		return decodeStream(is, null, null);
	}

	public static Bitmap decodeStream(InputStream is, String tag) {
		return decodeStream(is, null, null, tag);
	}

	public static Bitmap decodeStream(InputStream is, Rect padding, Options opts) {
		return decodeStream(is, padding, opts, null);
	}

	public static Bitmap decodeStream(InputStream is, Rect padding, Options opts, String tag) {
		Bitmap bmp = null;
		try {
			bmp = BitmapFactory.decodeStream(is, padding, opts);
		} catch (OutOfMemoryError e) {
			Log.e(TAG, "OOM while decoding bitmap from stream" + " memoryKB: " + getAllocMemoryInKb());
			System.gc();
			try {
				bmp = BitmapFactory.decodeStream(is, padding, opts);
			} catch (OutOfMemoryError e2) {
				Log.e(TAG, "OOM while creating bitmap 2");
				if (isThrowOutOfMemory) {
					isThrowOutOfMemory = false;
					throw new OutOfMemoryError(e2.getMessage());
				}
			}
		}

		int size = 0;

		if (bmp != null && (opts == null || !opts.inJustDecodeBounds)) {
			addMemory(size = bmp.getRowBytes() * bmp.getHeight());
			if (isDebug) {
				if (opts == null)
					Log.d(TAG, "decodeStream from stream width:" + bmp.getWidth() + " height:" + bmp.getHeight() + " memoryKB: " + getAllocMemoryInKb());
				else {
					Log.d(TAG, "decodeStream from stream width:" + bmp.getWidth() + " height:" + bmp.getHeight() + " memoryKB: " + getAllocMemoryInKb() + getOptionsString(opts));
				}
			}
			addToCreatedBitmaps(bmp, tag);
		}
		deallocateMemory(size);
		return bmp;
	}

	// public static final Bitmap decodeStream(InputStream is, Rect padding,
	// Options opts, String tag) {
	// return decodeStream(is, padding, opts, tag);
	// }

	// public static final Bitmap decodeStream(InputStream is, Rect padding,
	// Options opts) {
	// return decodeStream(is, padding, opts, null);
	// }

	public static String getOptionsString(Options opts) {
		String result = " opts is null";
		if (opts != null) {
            result = " inSampleSize:" + opts.inSampleSize + " outWidth:" + opts.outWidth + " outHeight:" + opts.outHeight + " inDither:" + opts.inDither + " inJustDecodeBounds:" + opts.inJustDecodeBounds + " inPurgeable:" + opts.inPurgeable + " inPreferredConfig:" + opts.inPreferredConfig + " mCancel:" + opts.mCancel;
        }
		return result;
	}

	public static void freeResources(Activity activity) {
		View root = activity.getWindow().getDecorView().findViewById(android.R.id.content);
		if (root != null) {
            freeViewTreeResources(root);
            removeNullRecords();
        }
	}

	public static void freeViewTreeResources(View v) {
		if (v != null) {
            freeViewResources(v);
            if (!(v instanceof ViewGroup)) {
                return;
            }
            ViewGroup vg = (ViewGroup) v;
            int count = vg.getChildCount();
            for (int i = 0; i < count; i++) {
                View curView = vg.getChildAt(i);
                if (curView instanceof ViewGroup) {
                    freeViewTreeResources(curView);
                } else {
                    freeViewResources(curView);
                }
            }
        }
	}

	public static void freeViewResources(View v) {
		if (v != null) {
            v.destroyDrawingCache();
            Drawable background = v.getBackground();
            if (background != null) background.setCallback(null);
            if (v instanceof ImageView) {
                ImageView iv = (ImageView) v;
                Drawable drbl = iv.getDrawable();
                if (drbl != null) {
                    drbl.setCallback(null);
                }
                iv.setImageDrawable(null);
            }
        }
	}

	public static void recycleByTag(String tag) {
		ArrayList<WeakReference<Bitmap>> bitmaps = createdBitmaps.get(tag);
		if (bitmaps != null) {
			if (isDebug) Log.d(TAG, "recycleByTag , bitmaps size:" + bitmaps.size());
            for (WeakReference<Bitmap> bitmap : bitmaps) {
                if (bitmap != null) {
                    Bitmap cur = bitmap.get();
                    recycle(cur, tag);
                }

            }
			createdBitmaps.remove(tag);
		}
	}

	public static boolean recycle(Bitmap bmp, String tag) {
		if (bmp == null) {
			if (isDebug) {
				Log.d(TAG, "bmp is null while recycling " + " tag: " + tag);
			}
			return false;
		}
		if (bmp.isRecycled()) {
			if (isDebug) {
				Log.d(TAG, "bmp is already recycled " + " tag: " + tag);
			}
			return false;
		}

		addMemory(-(bmp.getRowBytes() * bmp.getHeight()));
		if (isDebug) {
            Log.d(TAG, "bmp is recycled width: " + bmp.getWidth() + " height: " + bmp.getHeight() + " memoryKB: " + getAllocMemoryInKb() + " tag: " + tag);
        }
		bmp.recycle();
		return true;
	}

	public static void removeNullRecords() {
		Enumeration<String> keys = createdBitmaps.keys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			ArrayList<WeakReference<Bitmap>> bitmaps = createdBitmaps.get(key);
            Iterator<WeakReference<Bitmap>> it = bitmaps.iterator();
            while (it.hasNext()) {
                WeakReference<Bitmap> bmpRef = it.next();
                if (bmpRef != null) {
                    Bitmap cur = bmpRef.get();
                    if (cur == null) {
                        it.remove();
                    }
                }
            }
		}
	}

	public static boolean recycle(Bitmap bmp) {
		return recycle(bmp, null);
	}

	public static float getAllocMemoryInKb() {
		return (float) allocMemory / 1024.0f;
	}

	private static void addMemory(int val) {
		synchronized (lockObj) {
			allocMemory += val;
		}
	}

	public static void deallocateMemory(long size) {
		if (Build.VERSION.SDK_INT < 10 && isFakeDeallocatingAvailable) {

		}
	}

}
