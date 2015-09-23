package com.socialin.android.photo.imgop;

import android.util.Log;

import java.nio.Buffer;
import java.nio.ByteBuffer;

public final class ImageOpCommon {
	
	public static boolean IS_COMMON_LIBRARY_LOADED = false;
	public static final String LIB_NAME = "imageopcommon";
	
	static {
		boolean isLibraryLoaded = false;
		
		try {
			System.loadLibrary(LIB_NAME);
			init();
			isLibraryLoaded = true;
		} catch (UnsatisfiedLinkError e) {
			try {
				System.load("/data/data/" + System.getProperty("packageName") + "/lib/" + LIB_NAME + ".so");
				init();
				isLibraryLoaded = true;
			} catch (UnsatisfiedLinkError e1) {
				Log.e(ImageOpCommon.class.getSimpleName(), "Can't load \"" + LIB_NAME + "\" library.");
				Log.e(ImageOpCommon.class.getSimpleName(), e1.getMessage());
			}
		}
		
		IS_COMMON_LIBRARY_LOADED = isLibraryLoaded;
		Log.e("ex1", "ImageOpCommon first load Library = " + IS_COMMON_LIBRARY_LOADED);
	}
	
	public static void initialize(){
		try {
			if(IS_COMMON_LIBRARY_LOADED){
				init();
			}
		} catch (UnsatisfiedLinkError e) {
			// TODO: handle exception
		}
	}

//	private static Context context;
//	private static String libDir = "lib";
//	private static String shortLibName;
//	private static String fullLibName;
//
//	static public boolean loadLibrary(String libName, Context ctx) {
//		context = ctx;
//		shortLibName = libName;
//		fullLibName = "lib" + libName + ".so";
//
//		try {
//			Log.d("SharedLibraryLoader", "Trying to load library");
//			System.loadLibrary(shortLibName);
//			init();
//			Log.d("SharedLibraryLoader", "Library was loaded from default location");
//			return true;
//		} catch (UnsatisfiedLinkError e) {
//			Log.d("SharedLibraryLoader", "Lib wasn't found at default location. Trying to find in application private storage");
//			String path = null;
//			path = findInAppStorage(fullLibName);
//			if (path != null) {
//				Log.d("SharedLibraryLoader", "Lib was found in application private storage. Loading lib...");
//				System.load(path);
//				init();
//				return true;
//			} else {
//				Log.d("SharedLibraryLoader", "Lib was not found in application private storage. Trying to find in apk...");
//				path = findInApkAndCopyToAppStorage(fullLibName);
//
//				if (path != null) {
//					Log.d("SharedLibraryLoader", "Lib was found in apk and copied to application private storage. Loading lib...");
//					System.load(path);
//					init();
//					return true;
//				} else {
//					Log.d("SharedLibraryLoader", "FAILED TO LOAD LIBRARY");
//					return false;
//				}
//			}
//		}
//	}
//
//	static private String findInAppStorage(String libName) {
//
//		Log.d("SharedLibraryLoader", "enter findInAppStorage()");
//		String basePath = context.getApplicationInfo().dataDir;
//		File dataDir = new File(basePath);
//
//		String[] listFiles;
//		String lib = null;
//		listFiles = dataDir.list();
//
//		for (int i = 0; i < listFiles.length; i++) {
//			lib = findInStorage(basePath + "/" + listFiles[i], libName);
//
//			if (lib != null) { return lib; }
//		}
//
//		Log.d("SharedLibraryLoader", "Lib wasn't found.");
//		return null;
//	}
//
//	static private String findInStorage(String path, String nameOfLib) {
//		File file = new File(path);
//		if (file.isDirectory()) {
//			Log.d("SharedLibraryLoader", "Strorage__dir: " + path + "/");
//			String[] list = file.list();
//			String target = null;
//			for (int i = 0; i < list.length; i++) {
//				target = findInStorage(path + "/" + list[i], nameOfLib);
//				if (target != null) { return target; }
//			}
//		} else {
//			Log.d("SharedLibraryLoader", "Strorage_file: " + path);
//			if (path.contains(nameOfLib)) {
//				Log.d("SharedLibraryLoader", "Lib was found in: " + path);
//				return path;
//			}
//		}
//		return null;
//	}
//
//	static private String findInApkAndCopyToAppStorage(String libName) {
//		Log.d("SharedLibraryLoader", "Enter findInApkAndCopyToStorage()");
//
//		// ---------------- ZIP - find path to .so inside .apk
//		// ------------------
//		String apkPath = context.getPackageResourcePath();
//		Log.d("SharedLibraryLoader", String.format("Path to Package resource is: %s", apkPath));
//
//		try {
//			ZipFile zf = new ZipFile(apkPath);
//
//			Enumeration<ZipEntry> zipFiles = (Enumeration<ZipEntry>) zf.entries();
//			ZipEntry soZipEntry = null;
//			ZipEntry tempZipEntry;
//			String tmpString;
//			for (; zipFiles.hasMoreElements();) {
//				tempZipEntry = zipFiles.nextElement();
//				tmpString = tempZipEntry.getName();
//
//				if (tmpString.contains(libName)) {
//					Log.d("SharedLibraryLoader", "Library " + fullLibName + " was found in: " + tmpString);
//					soZipEntry = tempZipEntry;
//				}
//			}
//
//			// ----------now copy library---------------
//			Log.d("SharedLibraryLoader", "soZipEntry = " + soZipEntry.toString());
//
//			if (soZipEntry != null) {
//				InputStream soInputStream = zf.getInputStream(soZipEntry);
//
//				File fileDir;
//				File soFile;
//				OutputStream outStream;
//				fileDir = context.getApplicationContext().getDir(libDir, Context.MODE_PRIVATE); // but
//																								// "app_lib"
//																								// was
//																								// created!
//				String fullSoFilePath = fileDir.getAbsolutePath() + "/" + libName;
//				Log.d("SharedLibraryLoader", "New libpath is " + fullSoFilePath);
//				soFile = new File(fullSoFilePath);
//
//				Log.d("SharedLibraryLoader", "Is file already exists? - " + soFile.exists());
//
//				outStream = new BufferedOutputStream(new FileOutputStream(soFile));
//
//				Log.d("SharedLibraryLoader", "Start copying library...");
//				byte[] byteArray = new byte[256];
//				int copiedBytes = 0;
//
//				while ((copiedBytes = soInputStream.read(byteArray)) != -1) {
//					outStream.write(byteArray, 0, copiedBytes);
//				}
//
//				Log.d("SharedLibraryLoader", "Finish copying library");
//				outStream.close();
//
//				soInputStream.close();
//				return fullSoFilePath;
//			} else {
//				Log.d("SharedLibraryLoader", "Library not Found in APK");
//				return null;
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//			return null;
//		}
//	}

	// ///////

	public static native void init();

	// fish eye default params
	public final static int DEFAULT_FISHEYE_CENTER_X_PERCENT = 50;
	public final static int DEFAULT_FISHEYE_CENTER_Y_PERCENT = 50;
	public final static int DEFAULT_FISHEYE_RADIUS_PERCENT = 100;

	// caricature default params
	public final static int DEFAULT_CARICATURE_CENTER_X_PERCENT = 50;
	public final static int DEFAULT_CARICATURE_CENTER_Y_PERCENT = 50;
	public final static int DEFAULT_CARICATURE_RADIUS_PERCENT = 100;

	// swirled default params
	public final static int DEFAULT_SWIRLED_CENTER_X_PERCENT = 50;
	public final static int DEFAULT_SWIRLED_CENTER_Y_PERCENT = 50;
	public final static int DEFAULT_SWIRLED_RADIUS_PERCENT = 100;
	public final static int DEFAULT_SWIRLED_ANGLE = 45;

	// ByteBuffer management in native code.
	/**
	 * Allocation ByteBuffer in native code (memory will not be counted in VM
	 * heap).
	 */
	static public native ByteBuffer allocNativeBuffer(long size);

	/** Free ByteBuffer allocated in native code. */
	static public native void freeNativeBuffer(ByteBuffer buffer);

	/**
	 * Free all ByteBuffers allocated in native code. In fact there can be
	 * maximum 3 buffers.
	 */
	static public native ByteBuffer freeAllNativeBuffers();

	public static native void shearFilter(int[] inPixels, int[] outPixels, int w, int h);

	public static native void shearFilter2(Buffer inPixels, Buffer outPixels, int w, int h);

	public static native void grayscale(Buffer inPixels, Buffer outPixels, int w, int h);

	public static native void grayscale2(Buffer inPixels, Buffer outPixels, int w, int h, int centerX, int centerY, int r);

	public static native void oilFilter(int[] inPixels, int[] outPixels, int w, int h);

	public static native void oilFilter2(Buffer srcPixels, Buffer outPixels, int w, int h);
	public static native void oilFilter3(Buffer srcPixels, Buffer outPixels, int w, int h);
	public static native void oilFilter4(Buffer srcPixels, Buffer outPixels, int w, int h);

	public static native void meltFilter(Buffer inPixels, int w, int h);

	public static native void matteFilter(int[] inPixels, int[] outPixels, int w, int h, float gamma);

	public static native void negativeFilter(int[] inPixels, int w, int h);

	public static native void negativeFilter2(Buffer inPixels, int w, int h);

	public static native void gammaCorrection(Buffer inPixels, int w, int h, float gamma);

	public static native void solarizationFilter(Buffer inPixels, Buffer outPixels, int w, int h);

	public static native void reliefMapFilter(int[] inPixels, int w, int h);

	public static native void reliefMapFilter2(Buffer inPixels, int w, int h);

	public static native void shrink(int[] inPixels, int[] outPixels, int w, int h, float koef);

	public static native void caricature(int[] inPixels, int[] outPixels, int w, int h, int centerX, int centerY, int radius);

	public static native void caricature2(Buffer inPixels, Buffer outPixels, int w, int h, int centerX, int centerY, int radius);

	public static native void caricature3(Buffer inPixels, Buffer outPixels, int w, int h, int centerX, int centerY, int radius);

	public static native void fishEye(Buffer inPixels, Buffer outPixels, int w, int h, int centerX, int centerY, int radius);

	public static native void fishEye2(Buffer inPixels, Buffer outPixels, int w, int h, int centerX, int centerY, int radius);

	public static native void swirled(int[] inPixels, int[] outPixels, int w, int h, int centerX, int centerY, int radius, float angle);

	public static native void swirled2(Buffer inPixels, Buffer outPixels, int w, int h, int centerX, int centerY, int radius, float angle);

	public static native void cylinderMirror(int[] inPixels, int[] outPixels, int w, int h);

	public static native void cylinderMirror2(Buffer inPixels, Buffer outPixels, int w, int h,float scale);

	public static native void bathroom1(int[] inPixels, int w, int h);

	//public static native void bathroom12(Buffer inPixels, int w, int h);
	
	public static native void bathroom12(Buffer inPixels,Buffer outPixels, int w, int h ,float scale);

	public static native void bathroom2(int[] inPixels, int[] outPixels, int w, int h);

	public static native void bathroom22(Buffer inPixels, Buffer outPixels, int w, int h);

	public static native void bathroom23(Buffer inPixels, Buffer outPixels, int w, int h, int centerX, int centerY, int radius, float angle,float scale);

	public static native void blurFilter2(Buffer inPixels, Buffer outPixels, int w, int h, int n);

	public static native void blurFilter3(Buffer inPixels, Buffer outPixels, int w, int h, int n, int centerX, int centerY, int radius);

	public static native void pixelize2(Buffer inPixels, Buffer outPixels, int w, int h, int n,float scale);

	public static native void pixelize3(Buffer inPixels, Buffer outPixels, int w, int h, int n, int centerX, int centerY, int radius);

	public static native void convolution2(Buffer inPixels, Buffer outPixels, int w, int h, int[] filterMatrix, int koef, int bias);

	public static native void convolution3(Buffer inPixels, Buffer outPixels, int w, int h, int[] filterMatrix, int koef, int bias, int startX, int startY, int radius);

	public static native void vignette(Buffer inPixels, Buffer outPixels, int w, int h, int vignetteWidth, int fade);

	public static native void vignette2(Buffer inPixels, Buffer outPixels, int w, int h, int sizeX, int sizeY, float amount, float radius);

	public static native void outline(Buffer inPixels, Buffer outPixels, int w, int h);

	public static native void getCropRect(Buffer buffer8, int w, int h, int[] out_rect);

	public static native void getCropResult(Buffer originBuffer, Buffer maskBuffer, Buffer resultBuffer, int w, int h, int[] cropRect);

	// functions for color splash
	// start
	static public native double RGBTOHCL(int red, int green, int blue);

	static public native int getPixel(Buffer inPixels, int index);

	public static native void reverseBitmap(Buffer inPixels, int w, int h);

	public static native void flipHorizontal(Buffer inPixels, int w, int h);

	public static native void getPowerOfTwoSizeBuffer(Buffer origBuffer, Buffer powerOfTwoSizeBuffer, int origW, int origH, int powerOfTwoSize, int pixelValue);

	static public native void changeRedEyeRegions(Buffer inPixels, Buffer outPixels, int centerX, int centerY, int radius, int rectLeft, int rectsTop, int rectWidth, int rectsHeight, int width, int height);

	static public native void copyCirclePixels(Buffer inPixels, Buffer outPixels, int centerX, int centerY, int radius, int rectLeft, int rectsTop, int rectWidth, int rectsHeight, int width, int height);

	static public native void resizeBuffer(Buffer origBuffer, Buffer scaledBuffer, int origWidth, int origHeight, int scaledWidth, int scaledHeight);

	static public native void getGrayScaleBuffer(Buffer origBuffer, Buffer grayScaleBuffer, int width, int height);

	static public native void effectBlend(Buffer origBuffer, Buffer effectBuffer, Buffer maskBuffer, Buffer resultBuffer, int fade, int width, int height);
	static public native void effectBlend1effectAlpha(Buffer origBuffer, Buffer effectBuffer, Buffer maskBuffer, int fade, int width, int height);
	static public native void effectBlend1OrigAlpha(Buffer origBuffer, Buffer effectBuffer, Buffer maskBuffer, int fade, int width, int height);
	static public native void effectBlendWithAlpha(Buffer origBuffer, Buffer effectBuffer, Buffer maskBuffer, int alpha, int width, int height);
	static public native void effectBlendWithStrength(Buffer origBuffer, Buffer effectBuffer, int fade, int width, int height);
	static public native void effectBlendWithStrength1(Buffer origBuffer, Buffer effectBuffer,Buffer resultBuffer, int fade, int width, int height);

	static public native void customBlend(Buffer srcBuffer, Buffer dstBuffer, Buffer resultBuffer, int width, int height, int blendMode);
	static public native void customOverlayBlend(Buffer srcBuffer, Buffer dstBuffer, int width, int height);

	public static native void getHSLColorPickerImage(Buffer inBuffer, int width, int height);

	public static native void changebufferHSL(Buffer inBuffer, Buffer outBuffer, int width, int height, int replaceHue, int replaceDeltaSaturation, int fade);

	public static native void changebufferHSLWithCurrentSaturationAndHue(Buffer inBuffer, Buffer outBuffer, int width, int height, int replaceHue, int replaceSaturationPercent, int fade);

	public static native void changeCircleHSL(Buffer inBuffer, Buffer outBuffer, int width, int height, int centerX, int centerY, int radius, int rectLeft, int rectTop, int rectWidth, int rectHeight, int replaceHue, int replaceDeltaSaturation);

	public static native void tweethWhiten(Buffer inBuffer, Buffer outBuffer, int width, int height, int fade);

	public static native void changeCirclesHSL(Buffer inBuffer, Buffer outBuffer, int width, int height, int[] centerX, int[] centerY, int[] radius, int[] rectLeft, int[] rectTop, int[] rectWidth, int[] rectHeight, int circlesCount, int replaceHue, int replaceDeltaSaturation);

	public static native void yuv420sp2rgb(Buffer in, int width, int height, int textureSize, Buffer out);

	public static native void customCartoon(Buffer in, int width, int height, Buffer scaledIn, int scaledWidth, int scaledHeight, Buffer out, int fade);

	public static native void grafit(Buffer in, Buffer out, int width, int height, int fade);

	public static native void deallocMemory(boolean isDebug);

	public static native void changeBufferAlphaChannel(Buffer buffer, int capacity);
	public static native void changeBufferAlphaChannel1(Buffer inBuffer, Buffer outBuffer, int capacity);
	public static native void changeBufferAlphaChannelWithAlpha(Buffer inBuffer, int capacity,int alpha);

	public static native void rotateBuffer(Buffer inBuffer, Buffer outBuffer, int width, int height, int rotation);

	public static native void changeBufferWithMask(Buffer effectBuffer, Buffer maskBuffer, Buffer changeBuffer, int width, int height);

	public static native void sepia(Buffer inPixels, Buffer outPixels, int w, int h);
	
	public final static int MIRROR_FLIP_MODE_HORIZONTAL = 0;
	public final static int MIRROR_FLIP_MODE_VERTICAL = 1;
	public final static int MIRROR_MODE1 = 0;
	public final static int MIRROR_MODE2 = 1;
	
	public static native void mirrorWithFlip(Buffer inPixels, Buffer outPixels, int w, int h,int flipMode,int mode,int percentOffset);
	public static native void swirled3(Buffer inPixels, Buffer outPixels, int w, int h,int centerX,int centerY,int radius,float angle);
	public static native void lightCross(Buffer pixels, int w,int h,float brightness,float contrast,float fade);
	public static native void changeHueSaturation(Buffer inBuffer,int width,int height,int replaceHue,int replaceDeltaSaturation,int fade);
	public static native void blend(Buffer srcBuffer,Buffer maskBuffer,int width,int height,int fade,int blendMode);
	public static native void cameraBlend(Buffer origBuffer,Buffer effectBuffer,int fade,int width,int height);
	
	public static native void centerCropBlend(Buffer srcBuffer,int width,int height,Buffer maskBuffer,int maskWidth,int maskHeight,int blendMode,int fade,boolean invert);
	
	public static int CHANGE_CHANNEL_RED = 0;
	public static int CHANGE_CHANNEL_GREEN = 1;
	public static int CHANGE_CHANNEL_BLUE = 2;
	public static int CHANGE_CHANNEL_ALL = 3;
	
	public static native void changeChannelsWithCurve(Buffer srcBuffer,Buffer dstBuffer,Buffer curveBuffer,boolean useCurveBuffer,
			int[] rgbCurveValues,int[] rCurveValues,int[] gCurveValues,int[] bCurveValues,int width,int height,int curveWidth,int curveHeight,int curveIndex);
	
	public static native void colorBalance(Buffer srcBuffer,Buffer dstBuffer,int width,int height,
			float redS1,float redS2,float greenS1,float greenS2,float blueS1,float blueS2);
	public static native void histoEqualizationLuma(Buffer srcBuffer,Buffer dstBuffer,int width,int height);
	public static native void histoEqualization(Buffer srcBuffer,Buffer dstBuffer,int width,int height);
	public static native void contrastStretch(Buffer srcBuffer,Buffer dstBuffer,int width,int height);
	
	public static native void circularBlur(Buffer srcBuffer,Buffer dstBuffer,int width,int height,float blureSize,float blurMinSize,float blurMaxSize,float centerXInPercent,float centerYInPercent);
	public static native void lensBlur(Buffer inBuffer,Buffer outBuffer,int width,int height);
	public static native void waterFilter(Buffer inBuffer,Buffer outBuffer,int width,int height,int centerXPercent,int centerYPercent,int radiusPercent,float waveBloom,float phase);
	public static native void bumpFilter(Buffer inBuffer,Buffer outBuffer,int width,int height);
	
	public static native void smearFilter(Buffer inBuffer,Buffer outBuffer,int width,int height,int shape,int distance,float angle,float scale);
	public static native void shapeBlur(Buffer inBuffer,Buffer outBuffer,int width,int height,int shapeType,float bloom,int tollerance);
	public static native void sharpen(Buffer inBuffer,Buffer outBuffer,int width,int height,int fade);
	public static native void colorLevels(Buffer inBuffer,Buffer outBuffer,int width,int height,
			int lowIn,int highIn,int lowOut,int highOut,int gamma);
	
	public static native void desaturate(Buffer inBuffer,Buffer outBuffer,int width,int height,int desaturate,
			int lowIn,int highIn,int lowOut,int highOut,float gamma);
	public static native int moveHue(int color,int hue);
	public static native void changeContrast(Buffer inBuffer,Buffer outBuffer,int width,int height,int contrast);
	
	public static native void colorBalanceNew(Buffer inBuffer,Buffer outBuffer,int width,int height, int shCR,int shMG,int shYB
			,int mtCR,int mtMG,int mtYB
			,int hlCR,int hlMG,int hlYB);
	public static native int clearWithMask(Buffer oldBuffer, Buffer newBuffer, Buffer maskBuffer,int w,int h);
	public static native void invertRedMask(Buffer maskBuffer,int w,int h);
	public static native void invertAlphaChannel(Buffer maskBuffer,int w,int h);
	public static native void addOverBufferWithMask(Buffer srcBuffer, Buffer overBuffer,Buffer maskBuffer,int w,int h);


    public static native void changeBufferAlphaWithSrc(Buffer srcBuffer, Buffer dstBuffer,
            int width, int height);
}