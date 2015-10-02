package com.gifsart.studio.clipart.util;

import android.util.Log;

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

	// ///////

	public static native void init();


	// ByteBuffer management in native code.
	/**
	 * Allocation ByteBuffer in native code (memory will not be counted in VM
	 * heap).
	 */
	static public native ByteBuffer allocNativeBuffer(long size);

	/** Free ByteBuffer allocated in native code. */
	static public native void freeNativeBuffer(ByteBuffer buffer);

	public static native void deallocMemory(boolean isDebug);


}