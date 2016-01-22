package com.gifsart.studio.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Environment;
import android.os.StatFs;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.util.Log;

import com.nostra13.universalimageloader.utils.L;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

public class PicsartContext {

	private static final String TAG = PicsartContext.class.getSimpleName();
    public final static int MILLION_BASE2 = 1024 * 1024;

	public static final MemoryType memoryType;
	
	private static final String CONFIG_FILE_NAME = "config1.conf";

	static {
		int memory = (int) (Runtime.getRuntime().maxMemory() / 1024 / 1024);
		memoryType = MemoryType.getMemoryType(memory);
		L.d(TAG, "Memory type is " + memoryType);
	}

	public static final String PICSART_GOO_PACKAGE = "com.picsart.goo";
	public static final String PICSART_KALEIDOSCOPE_PACKAGE = "com.picsart.kaleidoscope";

	public static final String PICSART_GOO_START_ACTIVITY = "com.photo.picsingoo.FirstActivity";
	public static final String PICSART_KALEIDOSCOPE_START_ACTIVITY = "com.photo.kaleidoscope.KaleidoscopeActivity";

	public static final boolean IS_LOAD_BUFFER = true;

	public static final int DIALOG_ERROR_NETWORK = 111;

	public static final int PHOTO_FROM_CAMERA = 1;
	public static final int PHOTO_FROM_ALBUM = 2;
	public static final int PHOTO_FROM_SOCIAL = 3;
	public static final int DRAW_BLANK = 4;

	public static final int FROM_DRAWING = 1;
	public static final int FROM_PHOTO = 2;
	public static final int FROM_CAMERA = 4;
	public static final int FROM_COLLAGE = 5;

	public static final int ITEM_CLIPART = 1;
	public static final int ITEM_FRAME = 2;
	public static final int ITEM_COLLAGE_BG = 3;
	public static final int ITEM_COLLAGE_FRAME = 4;
	public static final int ITEM_MASKS = 5;
	public static final int ITEM_TEXTART = 6;
	

	public static final CpuInfo CPU = getCpuInfo();

	public static class CpuInfo {
		final public int procCount;
		final ProcessorName procName;
		final int revision;

		CpuInfo(int procCount, ProcessorName name, int revision) {
			this.procCount = procCount;
			this.procName = name;
			this.revision = revision;
		}

		@Override
		public String toString() {
			return procName + " cores : " + procCount + " rev " + revision;
		}
	}

	private static enum ProcessorName {
		ARMV6, ARMV7, UNKNOWN;

		static ProcessorName fromString(String str) {
			if (TextUtils.isEmpty(str)) return UNKNOWN;

			if (str.equalsIgnoreCase("6")) { return ARMV6; }

			if (str.equalsIgnoreCase("7")) { return ARMV7; }

			return UNKNOWN;
		}
	}

	public enum MemoryType {
		LOW(24,640, 1, 1, 450, 3, 5, 1024), NORMAL(32,1024, 1, 1, 640, 4, 5, 1024), NORMAL_PLUS(64,1200, 1, 1, 640, 6, 5, 1200), HIGH(128,2048, 3, 4, 1024, 10, 7, 2048), XHIGH(256,3200, 3, 6, 1024, 10, 10, 2048), XXHIGH(512,3200, 5, 8, 2048, 10, 10, 2048);

        private final int memSize;
        private final int optimalImageSize;
        private final int recomendedImageSizeMegapixel;
        private final int maxImageSizeMegapixel;
//		private final int maxImageSize;
//		private final int maxDrawingSize;
		private final int maxCollageImage;
		private final int maxAddPhotoCount;
		private final int maxDrawingLayersCount;
		private final int maxCollageSaveSize;

        public int getRecomendedImageSizeMegapixel() {
            return recomendedImageSizeMegapixel;
        }

        public int getRecomendedImageSizePixel() {
            return recomendedImageSizeMegapixel * MILLION_BASE2;
        }

        public int getMaxImageSizeMegapixel() {
            return maxImageSizeMegapixel;
        }

        public int getMaxImageSizePixel() {
            return maxImageSizeMegapixel * MILLION_BASE2;
        }

        public int getSize() {
            return memSize;
        }

        public int getOptimalImageSize(){
            return optimalImageSize;
        }


//		public int getDrawingMaxSize() {
//			return maxDrawingSize;
//		}

		public int getCollageImageMaxSize() {
			return maxCollageImage;
		}

		public int getAddPhotoMaxCount() {
			return maxAddPhotoCount;
		}
		
		public int getMaxDrawingLayersCount() {
			return maxDrawingLayersCount;
		}

        public int getMaxCollageSaveSize() {
			return maxCollageSaveSize;
		}

		private MemoryType(int memSize,int optimalImageSize, int recomendedImageSizeMegapixel, int maxImageSizeMegapixel, int maxCollageImage, int maxAddPhotoCount, int maxDrawingLayersCount, int maxCollageSaveSize) {
			this.memSize = memSize;
            this.optimalImageSize = optimalImageSize;
            this.recomendedImageSizeMegapixel = recomendedImageSizeMegapixel;
            this.maxImageSizeMegapixel = maxImageSizeMegapixel;
//            this.maxImageSize = maxImageSize;
//			this.maxDrawingSize = maxDrawingSize;
			this.maxCollageImage = maxCollageImage;
			this.maxAddPhotoCount = maxAddPhotoCount;
			this.maxDrawingLayersCount = maxDrawingLayersCount;
            this.maxCollageSaveSize = maxCollageSaveSize;
		}

		public static MemoryType getMemoryType(int mem) {
			MemoryType[] values = values();
			MemoryType closest = values[0];

			int delta = Math.abs(values[0].getSize() - mem);
			int minDelta = delta;

			for (MemoryType memType : values) {
				if ((delta = Math.abs(memType.getSize() - mem)) < minDelta) {
					minDelta = delta;
					closest = memType;
				}
			}

			return closest;
		}
	}

	private static CpuInfo getCpuInfo() {
		try {
			BufferedReader reader = null;
			final StringBuilder builder = new StringBuilder();

			try {
				reader = new BufferedReader(new FileReader("/proc/cpuinfo"));
				String line = null;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
			} catch (IOException ex) {
				// cannot read /proc/cpuinfo
				return new CpuInfo(Runtime.getRuntime().availableProcessors(), ProcessorName.UNKNOWN, 0);
			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (IOException e) {
                        Log.d(TAG, e.getMessage());
					}
				}
			}

			String result = builder.toString();

			result = result.replaceAll("\t", "").replaceAll(" ", "");

			// parsing
			String cpuArchitecture = "";

			int archIndex = result.indexOf("CPUarchitecture:");

			if (archIndex != -1) {
				cpuArchitecture = result.substring(archIndex + "CPUarchitecture:".length(), archIndex + "CPUarchitecture:".length() + 1);
			}

			int cpuRevision = 0;

			int revisionIndex = result.indexOf("CPUrevision:");

			if (revisionIndex != -1) {
				cpuRevision = Integer.parseInt(result.substring(revisionIndex + "CPUrevision:".length(), revisionIndex + "CPUrevision:".length() + 1));
			}
			
			int coreCount = 0;
			
			int processorIndex = result.indexOf("processor:");

			while (processorIndex != -1) {
				++coreCount;
				processorIndex = result.indexOf("processor:", processorIndex + "processor:".length());
			}
			
			if (coreCount < 1) {
				coreCount = Runtime.getRuntime().availableProcessors();
			}

			return new CpuInfo(coreCount, ProcessorName.fromString(cpuArchitecture), cpuRevision);
		} catch (Exception e) {
            Log.d(TAG, e.getMessage());
			return new CpuInfo(Runtime.getRuntime().availableProcessors(), ProcessorName.UNKNOWN, 0);
		}
	}

//	public static ImageSize getRecommendedSizeByMemory(ImageSize imageSize) {
//		final float scale = Math.min(1, (float) memoryType.getImageMaxSize() / imageSize.maxSize);
//
//		return new ImageSize((int) (imageSize.width * scale), (int) (imageSize.height * scale));
//	}
//
//	public static ImageSize getRecommendedSizeByCpu(ImageSize imageSize) {
//		final float scale = (float) CPU.recommendedImageSizeMegapixel / imageSize.maxSize;
//
//		return new ImageSize((int) (imageSize.width * scale), (int) (imageSize.height * scale));
//	}
	
	private static int userSelectedMaxSizePixel = memoryType.getRecomendedImageSizePixel();

    public static int getMaxImageSizePixel() {
        return userSelectedMaxSizePixel;
    }

    public static int getMaxImageSizeMegapixel() {
        return userSelectedMaxSizePixel / MILLION_BASE2;
    }

    public static synchronized int updateAndGetMaxImageSize(Context context) {
		userSelectedMaxSizePixel = readMaxImageSizePixel(context);
		return userSelectedMaxSizePixel;
	}
	
    public static synchronized void setMaxImageSizePixel(Context context, int sizePixel) {
        final File fileDir = context.getExternalCacheDir();
        final File configFile = new File(fileDir, CONFIG_FILE_NAME);

        DataOutputStream stream = null;
        try {
            stream = new DataOutputStream(new FileOutputStream(configFile));
            stream.writeInt(sizePixel);
        } catch (IOException ignored) {

        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    Log.d(TAG,e.getMessage());
                }
            }
        }

        userSelectedMaxSizePixel = sizePixel;
    }

    private static synchronized int readMaxImageSizePixel(Context context) {

		int readValue = memoryType.getRecomendedImageSizePixel();
		DataInputStream stream = null;
		try {
            final File fileDir = context.getExternalCacheDir();
            final File configFile = new File(fileDir, CONFIG_FILE_NAME);
			stream = new DataInputStream(new FileInputStream(configFile));
			readValue = stream.readInt();
		} catch (Exception e) {
            setMaxImageSizePixel(context, readValue);
        }
        finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
                    Log.d(TAG,e.getMessage());
				}
			}
		}
		
		return readValue;
	}

    public static Point getCameraPicturePreferSize() {

        int imageMaxSize =  PicsartContext.memoryType.getOptimalImageSize();

        int pictureMaxWidth = 1024;
        int pictureMaxHeight = 768;

        switch (imageMaxSize) {
            case 1600:
                pictureMaxWidth = 1600;
                pictureMaxHeight = 1200;
                break;

            case 2048:
                pictureMaxWidth = 2048;
                pictureMaxHeight = 1536;
                break;
            case 3200:
                pictureMaxWidth = 3200;
                pictureMaxHeight = 2400;
                break;
        }

        return new Point(pictureMaxWidth,pictureMaxHeight);
    }

    public static Point getCameraPictureMinSize() {
        int imageMaxSize =  PicsartContext.memoryType.getOptimalImageSize();

        int pictureMinWidth = 1024;
        int pictureMinHeight = 768;

        switch (imageMaxSize) {
            case 2048:
                pictureMinWidth = 1600;
                pictureMinHeight = 1200;
                break;
            case 3200:
                pictureMinWidth = 2048;
                pictureMinHeight = 1536;
                break;
        }

        return new Point(pictureMinWidth,pictureMinHeight);
    }

    public static Point getCameraPictureMaxSize(){
        int imageMaxSize =  PicsartContext.memoryType.getOptimalImageSize();

        int pictureMaxWidth = 1024;
        int pictureMaxHeight = 768;

        switch (imageMaxSize) {
            case 1600:
                pictureMaxWidth = 1600;
                pictureMaxHeight = 1200;
                break;

            case 2048:
                pictureMaxWidth = 2048;
                pictureMaxHeight = 1536;
                break;
            case 3200:
                pictureMaxWidth = 3200;
                pictureMaxHeight = 2400;
                break;
        }

        return new Point(pictureMaxWidth,pictureMaxHeight);
    }

    public static SpannableString getDeviceData(Context context) {
        PackageManager pm = context.getPackageManager();
        PackageInfo pi;
        try {
            pi = pm.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            Log.d(TAG,e.getMessage());
            return null;
        }

        String deviceData =  "\tVersion Name: " + pi.versionName +
                "\n\tModel: " + android.os.Build.MODEL +
                "\n\tRelease: " + android.os.Build.VERSION.RELEASE +
                "\n\tMaxMem: " + (Runtime.getRuntime().maxMemory() / 1048576L) +
                "\n\tHeap: " + (Runtime.getRuntime().totalMemory() / 1048576L) +
                "\n\tPhoneStorage: " + getAvailableAppDataSpaceMgbytes(context) +
                "\n\tSDcardAvailable: " + checkSDcardavailible() +
                "\n\tSDcard: " + getAvailableSdcardMgbytes() +
                "\n\tProcInfo: " + CPU +
                "\n\tRecomendedPhotoSizeMegapixel: " + memoryType.getRecomendedImageSizeMegapixel() +
                "\n\n";
        SpannableString styledText = new SpannableString(deviceData);
        styledText.setSpan(new RelativeSizeSpan(0.5f), 0, deviceData.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return styledText;
    }

	public static long getAvailableAppDataSpaceMgbytes(Context context) {
		if (context.getFilesDir() == null)
			return -1;

		StatFs stat = new StatFs(context.getFilesDir().getPath());
		long bytesAvailable = (long) stat.getBlockSize() * (long) stat.getAvailableBlocks();
		long megAvailable = bytesAvailable / 1048576;
		return megAvailable;
		// L.d(LOG_TAG,
		// "onCreate() - InternalStorageAvailableSizeInMB - ",megAvailable);
	}

	public static boolean checkSDcardavailible() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}

	public static long getAvailableSdcardMgbytes() {
		StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
		long bytesAvailable = (long) stat.getBlockSize() * (long) stat.getAvailableBlocks();
		long megAvailable = bytesAvailable / 1048576;
		return megAvailable;
		// L.d(TAG, "onCreate() - SDcardAvailableSizeInMB - ",megAvailable);
	}
}