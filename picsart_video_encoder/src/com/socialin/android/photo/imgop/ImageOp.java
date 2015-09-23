package com.socialin.android.photo.imgop;

import android.graphics.Color;
import android.util.Log;

import java.nio.Buffer;
import java.nio.ByteBuffer;

/** Image operations. */
public class ImageOp {
    // Default parameters for "stenciler" effect.
    public static final double DEFAULT_STENCILER_FADE_BODERS = 1.0;
    public static final int    DEFAULT_STENCILER_MEDIAN = 3;
	public static final int    DEFAULT_STENCILER_FADE = 30;
    
    public static final double DEFAULT_STENCILER_GAMMA = 0.3;
    public static final int    DEFAULT_STENCILER_INTENSITY_SHIFT = 128;
    public static final int    DEFAULT_STENCILER_COLOR = 0xffbb5500;
    
    public static final double DEFAULT_STENCILER2_GAMMA = 2.55;
    public static final int    DEFAULT_STENCILER2_INTENSITY_SHIFT = 0;
    public static final int    DEFAULT_STENCILER2_COLOR = 0xff523c00;
    
    public static final double DEFAULT_STENCILER3_GAMMA = 1.5;
    public static final int    DEFAULT_STENCILER3_INTENSITY_SHIFT = 0;
    public static final int    DEFAULT_STENCILER3_COLOR = 0xff00086b;
    
    public static final double DEFAULT_STENCILER4_GAMMA = 2.55;
    public static final int    DEFAULT_STENCILER4_INTENSITY_SHIFT = 0;
    public static final int    DEFAULT_STENCILER4_COLOR = 0xff311800;
    
    public static final double DEFAULT_STENCILER5_GAMMA = 1.55;
    public static final int    DEFAULT_STENCILER5_INTENSITY_SHIFT = 0;
    public static final int    DEFAULT_STENCILER5_COLOR = 0xff39005a;
    
    public static final double DEFAULT_STENCILER6_GAMMA = 1.55;
    public static final int    DEFAULT_STENCILER6_INTENSITY_SHIFT = 0;
    public static final int    DEFAULT_STENCILER6_COLOR = 0xff522800;
    
    public static final double DEFAULT_STENCILER7_GAMMA = 1.55;
    public static final int    DEFAULT_STENCILER7_INTENSITY_SHIFT = 0;
    public static final int    DEFAULT_STENCILER7_COLOR = 0xff313431;
    
    public static final double DEFAULT_STENCILER8_GAMMA = 2.55;
    public static final int    DEFAULT_STENCILER8_INTENSITY_SHIFT = 0;
    public static final int    DEFAULT_STENCILER8_COLOR = 0xff7300ff;
    

    // Default parameters for "sketcher" effect.
    public static final int    DEFAULT_SKETCHER_DOG1 = 21;
    public static final int    DEFAULT_SKETCHER_DOG2 = 7;
    public static final int    DEFAULT_SKETCHER_LOWER_INTENSITY = 160;
    public static final int    DEFAULT_SKETCHER_UPPER_INTENSITY = 224;
    public static final int    DEFAULT_SKETCHER_MODE = 1;
    public static final int    SKETCHER_MODE_1 = 1;
    public static final int    SKETCHER_MODE_2 = 2;
	public static final int    DEFAULT_SKETCHER_FADE = 0;
    
    public static final int    DEFAULT_SKETCHER_COLOR11 = 0xff000000;
    public static final int    DEFAULT_SKETCHER_COLOR12 = 0xffffffff;
    
    public static final int    DEFAULT_SKETCHER_COLOR21 = 0x0000ff;
    public static final int    DEFAULT_SKETCHER_COLOR22 = 0x00ffff;


    // Default parameters for "cartoonizer" effect.
    public static final int    DEFAULT_CARTOONIZER_DOG1 = 21;
    public static final int    DEFAULT_CARTOONIZER_DOG2 = 7;
    public static final int    DEFAULT_CARTOONIZER_LOWER_INTENSITY = 190;
    public static final int    DEFAULT_CARTOONIZER_UPPER_INTENSITY = 240;
    public static final int    DEFAULT_CARTOONIZER_MEDIAN = 27;
    public static final int    DEFAULT_CARTOONIZER_FADE = 0;

    // Default parameters for "orton" effect.
    public static final int    DEFAULT_ORTON_BLUR = 0; //9;
    public static final int    DEFAULT_ORTON_LOWER_INTENSITY = 32;
    public static final int    DEFAULT_ORTON_UPPER_INTENSITY = 192;
    public static final int    DEFAULT_ORTON_FADE = 0;

    // Default parameters for "lomo" effect.
    public static final double DEFAULT_LOMO_CONTRAST = 0.0; //0.2;
    public static final double DEFAULT_LOMO_AMOUNT = 0.5;
    public static final double DEFAULT_LOMO_VIGNETTE = 0.25;
    public static final int    LOMO_MODE_552 = 552;
    public static final int    LOMO_MODE_255 = 255;
    public static final int    LOMO_MODE_252 = 252;
    public static final int    LOMO_MODE_011 =  11;
    public static final int    LOMO_MODE_101 = 101;
    public static final int    DEFAULT_LOMO_MODE = LOMO_MODE_255;
    
    
    // Default parameters for "lomo4" effect.
    public static final double DEFAULT_LOMO4_CONTRAST = 0.2;
    public static final double DEFAULT_LOMO4_AMOUNT = 0.5;
    public static final double DEFAULT_LOMO4_VIGNETTE = 0.23;
	public static final int    DEFAULT_LOMO_FADE = 0;

    // Default parameters for "vintage" effect.
    public static final double DEFAULT_VINTAGE_AMOUNT = 0.5;
    public static final int    VINTAGE_MODE_1 = 1;
    public static final int    VINTAGE_MODE_2 = 2;
    public static final int    VINTAGE_MODE_3 = 3;
    public static final int    VINTAGE_MODE_4 = 4;
    public static final int    DEFAULT_VINTAGE_MODE = VINTAGE_MODE_1;
	public static final int    DEFAULT_VINTAGE_FADE = 0;
    
    // Default parameters for "vintage23" effect.
    public static final double DEFAULT_VINTAGE2_AMOUNT = 0.4;

    // Default parameters for "CrossProcess" effect.
    public static final int    CROSSPROCESS_MODE_RED = 1;
    public static final int    CROSSPROCESS_MODE_GREEN = 2;
    public static final int    CROSSPROCESS_MODE_BLUE = 3;
    public static final int    DEFAULT_CROSSPROCESS_MODE = CROSSPROCESS_MODE_GREEN;
    public static final int    DEFAULT_CROSSPROCESS_FADE = 0;
    
    // Default parameters for "CrossProcessing" effect.
    public static final double DEFAULT_CROSSPROCESSING_CONTRAST = 0.0;  //0.1;
    public static final double DEFAULT_CROSSPROCESSING_AMOUNT = 0.5; //0.25;
    public static final double DEFAULT_CROSSPROCESSING_AMOUNT_BW = 0.14;
    public static final int    CROSSPROCESSING_MODE_1 = 1;
    public static final int    CROSSPROCESSING_MODE_2 = 2;
    public static final int    DEFAULT_CROSSPROCESSING_MODE = CROSSPROCESSING_MODE_1;
    public static final int    DEFAULT_CROSSPROCESSING_FADE = 0;
    
    // Default parameters for "B & W 5" effect.
    public static final double DEFAULT_BW5_CONTRAST = 0.0; 
    public static final double DEFAULT_BW5_AMOUNT = 0.14; 
    public static final int    DEFAULT_BW5_MODE = CROSSPROCESSING_MODE_1;
    
    // Default parameters for "B & W 6" effect.
    public static final double DEFAULT_BW6_AMOUNT = 0.5;
    public static final int    DEFAULT_BW6_MODE = VINTAGE_MODE_1;

    // Default parameters for "hdr" effect.
    public static final int    DEFAULT_HDR_BLUR = 25;
    public static final double DEFAULT_HDR_UNSHARP = 0.9;
    public static final double DEFAULT_HDR_SATURATION = 0.0;
    public static final int    HDR_MODE_1 = 1;
    public static final int    HDR_MODE_2 = 2;
    public static final int    DEFAULT_HDR_MODE = HDR_MODE_1;
    public static final int    DEFAULT_HDR_FADE = 0;

    // Default parameters for "redeye" effect.
    public static final double DEFAULT_REDEYE_AMOUNT = 1.0;
    public static final double DEFAULT_REDEYE_RELATIVE_RADIUS = 0.1;

    // Default parameters for "fattal" effect.
    public static final double DEFAULT_FATTAL_ALFA = 0.1;
    public static final double DEFAULT_FATTAL_BETA = 0.8;
    public static final double DEFAULT_FATTAL_SATURATION = 1.0;
    public static final double DEFAULT_FATTAL_NOISE = 0.0;
    public static final int    FATTAL_MODE_1 = 1;
    public static final int    FATTAL_MODE_2 = 2;
    public static final int    DEFAULT_FATTAL_MODE = FATTAL_MODE_1;
    public static final int    DEFAULT_FATTAL_FADE = 0;

    // Default parameters for "pencil" effect.
    public static final double DEFAULT_PENCIL_AMOUNT  = 0.15;
    public static final double DEFAULT_PENCIL_LENGTH  = 0.15;
    public static final double DEFAULT_PENCIL_DETAILS = 0.5;
    public static final int    DEFAULT_PENCIL_FADE = 0;

    // Default parameters for "holgaart" effect.
    public static final int DEFAULT_HOLGAART_LEFT  = 25;
    public static final int DEFAULT_HOLGAART_RIGHT = 25;
    public static final int DEFAULT_HOLGAART_VIGNETTE = 0;
    public static final int DEFAULT_HOLGAART_FADE = 0;

    // Default parameters for "face detector".
    public static final double  DEFAULT_FACEDETECTOR_SCALE_FACTOR  = 1.1;
    public static final boolean DEFAULT_FACEDETECTOR_DO_CANNY_PRUNING = true;
    public static final boolean DEFAULT_FACEDETECTOR_FIND_BIGGEST_OBJECT = true;
    
    
    //Default parameters for "blur bw" effect.
    public static final int    DEFAULT_BLUR_BW_BLUR = 40;
    public static final int    DEFAULT_BLUR_BW_LOWER_INTENSITY = 40;
    public static final int    DEFAULT_BLUR_BW_UPPER_INTENSITY = 240;
    
    //Default parameters for "blur sepia" effect.
    public static final int    DEFAULT_BLUR_SEPIA_BLUR = 45;
    public static final int    DEFAULT_BLUR_SEPIA_LOWER_INTENSITY = 0;
    public static final int    DEFAULT_BLUR_SEPIA_UPPER_INTENSITY = 200;
    
    
    // Default parameters for "bw" effect.
    public static final int BW_MODE_STD   = 0;
    public static final int BW_MODE_RED   = 1;
    public static final int BW_MODE_GREEN = 2;
    public static final int BW_MODE_BLUE  = 3;
    public static final int DEFAULT_BW_MODE = BW_MODE_STD;
    public static final int DEFAULT_BW_FADE = 0;

    // Default parameters for "soften" effect.
    public static final int DEFAULT_SOFTEN_BLUR = 31;
    public static final int DEFAULT_SOFTEN_FADE = 20;

    // Default parameters for "focal soften" effect.
    public static final int DEFAULT_FOCALSOFTEN_FADE = 20;
    public static final int DEFAULT_FOCALSOFTEN_BLUR = 31;
    public static final int DEFAULT_FOCALSOFTEN_RADIUS = 31;
    public static final int DEFAULT_FOCALSOFTEN_HARDNESS = 100;
    
    
    // Default parameters for "comic boom" effect.
    public static final int DEFAULT_COMICBOOM_LINES = 80;
    public static final int DEFAULT_COMICBOOM_BRIGHTNESS = 80;
    public static final int DEFAULT_COMICBOOM_FADE = 0;
    
    // Default parameters for "neon cola" effect.
    public static final int DEFAULT_NEONCOLA_LINES = 50;
    public static final int DEFAULT_NEONCOLA_BRIGHTNESS = 50;
    public static final int DEFAULT_NEONCOLA_CONTRAST = 100;
    public static final int DEFAULT_NEONCOLA_FADE = 0;
    
    // Default parameters for "acquarello" effect.
    public static final int DEFAULT_ACQUARELLO_LINES = 65;
    public static final int DEFAULT_ACQUARELLO_BRIGHTNESS = 65;
    public static final int DEFAULT_ACQUARELLO_CONTRAST = 70;
    public static final int DEFAULT_ACQUARELLO_FADE = 0;
    
    // Default parameters for "sketch up" effect.
    public static final int DEFAULT_SKETCHUP_LINES = 40;
    public static final int DEFAULT_SKETCHUP_BRIGHTNESS = 50;
    public static final int DEFAULT_SKETCHUP_CONTRAST = 50;
    public static final int DEFAULT_SKETCHUP_FADE = 0;
    // Default parameters for "con tours" effect.
    public static final int DEFAULT_CONTOURS_LINES = 50;
    public static final int DEFAULT_CONTOURS_BRIGHTNESS = 50;
    public static final int DEFAULT_CONTOURS_CONTRAST = 50;
    public static final int DEFAULT_CONTOURS_FADE = 0;
    
    // Default parameters for "bleaching" effect.
    public static final int DEFAULT_BLEACHING_LINES = 50;
    public static final int DEFAULT_BLEACHING_BRIGHTNESS = 100;
    public static final int DEFAULT_BLEACHING_CONTRAST = 35;
    public static final int DEFAULT_BLEACHING_FADE = 0;
    
    // Default parameters for "granny's paper" effect.
    public static final int DEFAULT_GRANNYSPAPER_LINES = 25;
    public static final int DEFAULT_GRANNYSPAPER_BRIGHTNESS = 100;
    public static final int DEFAULT_GRANNYSPAPER_CONTRAST = 100;
    public static final int DEFAULT_GRANNYSPAPER_FADE = 0;
    
    // Default parameters for "pastel perfect" effect.
    public static final int DEFAULT_PASTELPERFECT_LINES = 50;
    public static final int DEFAULT_PASTELPERFECT_BRIGHTNESS = 50;
    public static final int DEFAULT_PASTELPERFECT_CONTRAST = 50;
    public static final int DEFAULT_PASTELPERFECT_FADE = 0;
    
    // Default parameters for "smart blur" effect.
    public static final int DEFAULT_SMARTBLUR_MAXBLUR = 15;
    public static final int DEFAULT_SMARTBLUR_AMOUNT = 60;
    public static final int DEFAULT_SMARTBLUR_FADE = 0;

    // Default parameters for "holgaart2" effect.
    public static final int DEFAULT_HOLGAART2_LEFT  = 3;
    public static final int DEFAULT_HOLGAART2_RIGHT = 3;
    public static final int DEFAULT_HOLGAART2_FADE  = 0;
    
 // Default parameters for "sunless tan" effect.
    public static final int SUNLESSTAN_MODE_1 = 1;
    public static final int SUNLESSTAN_MODE_2 = 2;
    public static final int SUNLESSTAN_MODE_3 = 3;
    public static final int SUNLESSTAN_MODE_4 = 4;
    public static final int SUNLESSTAN_MODE_5 = 5;
    public static final int DEFAULT_SUNLESSTAN_MODE = SUNLESSTAN_MODE_3;
    public static final int DEFAULT_SUNLESSTAN_FADE = 20;
    
 // Default parameters for "blemish fix" effect.
    public static final int BLEMISHFIX_METHOD_1 = 1;
    public static final int BLEMISHFIX_METHOD_2 = 2;
    public static final int DEFAULT_BLEMISHFIX_AMOUNT = 70;
    public static final int DEFAULT_BLEMISHFIX_METHOD = BLEMISHFIX_METHOD_1;
    
    // Default parameters for "united colors" effect.
    public static final int UNITEDCOLORS_MODE_STD   = 0;
    public static final int UNITEDCOLORS_MODE_RED   = 1;
    public static final int UNITEDCOLORS_MODE_GREEN = 2;
    public static final int UNITEDCOLORS_MODE_BLUE  = 3;
    public static final int DEFAULT_UNITEDCOLORS_FADE = 20;
    public static final int DEFAULT_UNITEDCOLORS_POPART_FADE = 60;
    public static final int UNITEDCOLORS_PRESET1_MODE = UNITEDCOLORS_MODE_RED;
    public static final int UNITEDCOLORS_PRESET1_COLOR1 = 0xfff700;
    public static final int UNITEDCOLORS_PRESET1_COLOR2 = 0xff0000;
    public static final int UNITEDCOLORS_PRESET2_MODE = UNITEDCOLORS_MODE_GREEN;
    public static final int UNITEDCOLORS_PRESET2_COLOR1 = 0xf3ff00;
    public static final int UNITEDCOLORS_PRESET2_COLOR2 = 0x0084ff;
    public static final int UNITEDCOLORS_PRESET3_MODE = UNITEDCOLORS_MODE_BLUE;
    public static final int UNITEDCOLORS_PRESET3_COLOR1 = 0xbcff6f;
    public static final int UNITEDCOLORS_PRESET3_COLOR2 = 0xad3232;
    public static final int UNITEDCOLORS_PRESET4_MODE = UNITEDCOLORS_MODE_STD;
    public static final int UNITEDCOLORS_PRESET4_COLOR1 = 0xffa1db;
    public static final int UNITEDCOLORS_PRESET4_COLOR2 = 0x1b6826;

    /*// Default parameters for "vignette" effect.
    public static final int DEFAULT_VIGNETTE_COLOR  = Color.WHITE;
    public static final int DEFAULT_VIGNETTE_AMOUNT = 50;
    public static final int DEFAULT_VIGNETTE_FADE   = 0;*/
    
 // Default parameters for "vignette" effect.
    public static final int VIGNETTE_ALGORITHM_SQRT = 1;
    public static final int VIGNETTE_ALGORITHM_LINEAR = 2;
    public static final int VIGNETTE_ALGORITHM_SQUARE = 3;
    public static final int VIGNETTE_ALGORITHM_CUBE = 4;
    public static final int VIGNETTE_ALGORITHM_SIN = 5;
    public static final int DEFAULT_VIGNETTE_COLOR  = Color.WHITE;
    public static final int DEFAULT_VIGNETTE_RADIUS = 25;
    public static final int DEFAULT_VIGNETTE_ALGORITHM = VIGNETTE_ALGORITHM_SIN;
    public static final int DEFAULT_VIGNETTE_FADE   = 0;
    
 // Default parameters for "halftone dots" effect.
    public static final int DEFAULT_HALFTONE_DOTS_SIZE  = 20;
    public static final int DEFAULT_HALFTONE_DOTS_CONTRAST = 50;
    public static final int DEFAULT_HALFTONE_DOTS_FADE   = 0;
    
    // Default parameters for "motion blur" effect.
    public static final int DEFAULT_MOTIONBLUR_ANGLE		= 40;
    public static final int DEFAULT_MOTIONBLUR_DISTANCE		= 20;
    public static final int DEFAULT_MOTIONBLUR_HUE			= 100;
    public static final int DEFAULT_MOTIONBLUR_LIGHTNESS	= 50;
    public static final int DEFAULT_MOTIONBLUR_SATURATION	= 30;
    public static final int DEFAULT_MOTIONBLUR_NOISE		= 10;
    public static final int DEFAULT_MOTIONBLUR_FADE		 	= 0	;
    public static final boolean DEFAULT_MOTIONBLUR_COLORIZE	 	= true;
    
    // Default parameters for "Tranquil" effect.
    public static final int DEFAULT_TRANQUIL_CONTRAST	 	= 50;
    public static final int DEFAULT_TRANQUIL_BRIGHTNESS	 	= 50;
    public static final int DEFAULT_TRANQUIL_FADE		 	= 0	;
    
 // Default parameters for "Posterize" effect.
    public static final int DEFAULT_POSTERIZE_COLORS	 	= 8 ;
    public static final int DEFAULT_POSTERIZE_DETAILS	 	= 80;
    public static final int DEFAULT_POSTERIZE_FADE			= 0	;
    
    // Default parameters for "Dusk" effect.
    public static final int DEFAULT_DUSK_FADE				= 0	;
    
    // Default parameters for "YesterColor" effect.
    public static final int DEFAULT_YESTERCOLOR_COLOR 		= Color.WHITE & 0x00FFFFFF;
    public static final int DEFAULT_YESTERCOLOR_COLOR_ORIG 	= Color.WHITE;
    public static final int DEFAULT_YESTERCOLOR_FADE		= 0	;
    
    
    // Default parameters for eye replace effect
    public static final int DEFAULT_EYE_REPLACE_HUE = 240;
    public static final int DEFAULT_EYE_REPLACE_DELTA_SATURATION = 0;
    //public static final int DEFAULT_EYE_REPLACE_DELTA_LIGTHNESS = 0;*/
    public static final int DEFAULT_EYE_REPLACE_COLOR =0xff0000ff;
    
    //tweet whiten default params
    public static final int DEFAULT_TWEET_WHITEN = 35;
    
 // Default parameters for "Cinerama" effect.
    public static final int DEFAULT_CINERAMA_FADE			= 0	;
    
    // Resizing method.
    public static final int RESIZE_NN = 0;
    public static final int RESIZE_LINEAR = 1;
    public static final int RESIZE_CUBIC = 2;
    public static final int RESIZE_AREA = 3;
    public static final int RESIZE_LANCZOS = 4;
    
    
    //matrix type effects params
    public final static int DEFAULT_SATURATION = 50;
    public final static int MAX_SATURATION = 100;

    public static final int DEFAULT_BRIGHTNESS = 100;
    public static final int MAX_BRIGHTNESS = 255;

    public static final int DEFAULT_CONTRAST = 50;
    public static final int MAX_CONTRAST = 100;
    
    public static final int DEFAULT_BLACK_AND_WHITE = 100;
    public static final int MAX_BLACK_AND_WHITE = 255;
	
    public static final int DEFAULT_HUE = 100;
    public static final int MAX_HUE = 255;
	
    public static final int DEFAULT_INVERT = 100;
    public static final int MAX_INVERT = 255;
    
    
    //pixel type effects params
    public static final int DEFAULT_PIXELIZE = 6;
    public static final int MAX_PIXELIZE = 20;
    
    
 // Default parameters for "focal zoom" effect.
    public static final int DEFAULT_FOCALZOOM_FADE = 0;
    public static final int DEFAULT_FOCALZOOM_BLUR = 70;
    public static final int DEFAULT_FOCALZOOM_RADIUS = 25;
    public static final int DEFAULT_FOCALZOOM_HARDNESS = 70;
    public static final int DEFAULT_FOCAL_ZOOM_BLENDMODE = 0;
    
    public static final int FOCALZOOM_BLENDMODE_NORMAL = 0;
    public static final int FOCALZOOM_BLENDMODE_MULTIPLY = 1;
    public static final int FOCALZOOM_BLENDMODE_SCREEN = 2;
    public static final int FOCALZOOM_BLENDMODE_OVERLAY = 3;
    public static final int FOCALZOOM_BLENDMODE_DARKEN = 4;
    public static final int FOCALZOOM_BLENDMODE_LIGHTEN = 5;
    public static final int FOCALZOOM_BLENDMODE_COLORDODGE = 6;
    public static final int FOCALZOOM_BLENDMODE_COLORBURN = 7;
    public static final int FOCALZOOM_BLENDMODE_SOFTLIGHT = 8;
    public static final int FOCALZOOM_BLENDMODE_HARDLIGHT = 9;
    public static final int FOCALZOOM_BLENDMODE_DIFFERENCE = 10;
    public static final int FOCALZOOM_BLENDMODE_EXCLUSION = 11;
    public static final int FOCALZOOM_BLENDMODE_PLUSDARKER = 26;
    public static final int FOCALZOOM_BLENDMODE_PLUSLIGHTER = 27;
    public static final int FOCALZOOM_BLENDMODES[] = {
    	FOCALZOOM_BLENDMODE_NORMAL,
    	FOCALZOOM_BLENDMODE_MULTIPLY,
    	FOCALZOOM_BLENDMODE_SCREEN,
    	FOCALZOOM_BLENDMODE_OVERLAY,
    	FOCALZOOM_BLENDMODE_DARKEN,
    	FOCALZOOM_BLENDMODE_LIGHTEN,
    	FOCALZOOM_BLENDMODE_COLORDODGE,
    	//FOCALZOOM_BLENDMODE_COLORBURN,
    	//FOCALZOOM_BLENDMODE_SOFTLIGHT,
    	//FOCALZOOM_BLENDMODE_HARDLIGHT,
    	FOCALZOOM_BLENDMODE_DIFFERENCE,
    	FOCALZOOM_BLENDMODE_EXCLUSION,
    	//FOCALZOOM_BLENDMODE_PLUSDARKER,
    	//FOCALZOOM_BLENDMODE_PLUSLIGHTER
    	};
    public static final String[] FOCALZOOM_BLENDMODE_NAMES = {
    	"Normal",
    	"Multiply",
    	"Screen",
    	"Overlay",
    	"Darken",
    	"Lighten",
    	"Color Dodge",
//    	"Color burn",
//    	"Soft light",
//    	"Hard light",
    	"Difference",
    	"Exclusion"
//    	"Plus darker",
//    	"Plus lighter"
    	};
    
    


 // Stenciler effect.
    /** Apply "stenciler" effect for buffer 8888 and store result in another buffer 8888.
     *  All buffers should be allocated "directly" before calling this method.
     */
    public static native void stenciler4buf(Buffer inPixels, Buffer outPixels, int in_width, int in_height, int out_width, int out_height,
        double fade_borders, int median_width, int intensity_shift, double gamma, int color, Buffer background, int bg_width, int bg_height, int fade, boolean interruptable, int instanceID);
    /** Apply "stenciler" effect for path and store result in another path on file system. */
    public static native boolean stenciler4path(String in_path, String out_path, int out_width, int out_height,
        double fade_borders, int median_width, int intensity_shift, double gamma, int color, String bg_path, int fade, boolean interruptable, int instanceID);
    /** Apply "stenciler" effect for path and store result in buffer 8888.
     *  Output buffer should be allocated "directly" before calling this method.
     */
    public static native void stenciler4mix(String in_path, Buffer out_pixels, int out_width, int out_height,
        double fade_borders, int median_width, int intensity_shift, double gamma, int color, String bg_path, int fade, boolean interruptable, int instanceID);

    // Cartoonizer effect.
    /** Apply "cartoonizer" effect for buffer 8888 and store result in another buffer 8888.
     *  All buffers should be allocated "directly" before calling this method.
     */
    public static native void cartoonizer4buf(Buffer inPixels, Buffer outPixels, int in_width, int in_height, int out_width, int out_height,
        int dog1, int dog2, int lower_intensity, int upper_intensity, int median_width, int fade, boolean interruptable, int instanceID);
    /** Apply "cartoonizer" effect for path and store result in another path on file system. */
    public static native boolean cartoonizer4path(String in_path, String out_path, int out_width, int out_height,
        int dog1, int dog2, int lower_intensity, int upper_intensity, int median_width, int fade, boolean interruptable, int instanceID);
    /** Apply "cartoonizer" effect for path and store result in buffer 8888.
     *  Output buffer should be allocated "directly" before calling this method.
     */
    public static native void cartoonizer4mix(String in_path, Buffer out_pixels, int out_width, int out_height,
        int dog1, int dog2, int lower_intensity, int upper_intensity, int median_width, int fade, boolean interruptable, int instanceID);

    // Sketcher effect.
    /** Apply "sketcher" effect for buffer 8888 and store result in another buffer 8888.
     *  All buffers should be allocated "directly" before calling this method.
     */
    public static native void sketcher4buf(Buffer inPixels, Buffer outPixels, int in_width, int in_height, int out_width, int out_height,
        int dog1, int dog2, int lower_intensity, int upper_intensity, int color1, int color2, int mode, int fade, boolean interruptable, int instanceID);
    /** Apply "sketcher" effect for path and store result in another path on file system. */
    public static native boolean sketcher4path(String in_path, String out_path, int out_width, int out_height,
        int dog1, int dog2, int lower_intensity, int upper_intensity, int color1, int color2, int mode, int fade, boolean interruptable, int instanceID);
    /** Apply "sketcher" effect for path and store result in buffer 8888.
     *  Output buffer should be allocated "directly" before calling this method.
     */
    public static native void sketcher4mix(String in_path, Buffer out_pixels, int out_width, int out_height,
        int dog1, int dog2, int lower_intensity, int upper_intensity, int color1, int color2, int mode, int fade, boolean interruptable, int instanceID);

    // Orton effect.
    /** Apply "Orton" effect for buffer 8888 and store result in another buffer 8888.
     *  All buffers should be allocated "directly" before calling this method.
     */
    public static native void orton4buf(Buffer inPixels, Buffer outPixels, int in_width, int in_height, int out_width, int out_height,
        int blur_width, int lower_intensity, int upper_intensity, int fade, boolean interruptable, int instanceID);
    /** Apply "Orton" effect for path and store result in another path on file system. */
    public static native boolean orton4path(String in_path, String out_path, int out_width, int out_height,
        int blur_width, int lower_intensity, int upper_intensity, int fade, boolean interruptable, int instanceID);
    /** Apply "Orton" effect for path and store result in buffer 8888.
     *  Output buffer should be allocated "directly" before calling this method.
     */
    public static native void orton4mix(String in_path, Buffer out_pixels, int out_width, int out_height,
        int blur_width, int lower_intensity, int upper_intensity, int fade, boolean interruptable, int instanceID);

    // Lomo effect.
    /** Apply "LOMO" effect for buffer 8888 and store result in another buffer 8888.
     *  All buffers should be allocated "directly" before calling this method.
     */
    public static native void lomo4buf(Buffer inPixels, Buffer outPixels, int in_width, int in_height, int out_width, int out_height,
        double vignette, double contrast, double amount, int mode, int fade, boolean interruptable, int instanceID);
    /** Apply "LOMO" effect for path and store result in another path on file system. */
    public static native boolean lomo4path(String in_path, String out_path, int out_width, int out_height,
        double vignette, double contrast, double amount, int mode, int fade, boolean interruptable, int instanceID);
    /** Apply "LOMO" effect for path and store result in buffer 8888.
     *  Output buffer should be allocated "directly" before calling this method.
     */
    public static native void lomo4mix(String in_path, Buffer out_pixels, int out_width, int out_height,
        double vignette, double contrast, double amount, int mode, int fade, boolean interruptable, int instanceID);

    // Vintage effect.
    /** Apply "Vintage colors" effect for buffer 8888 and store result in another buffer 8888.
     *  All buffers should be allocated "directly" before calling this method.
     */
    public static native void vintage4buf(Buffer inPixels, Buffer outPixels, int in_width, int in_height, int out_width, int out_height,
        double amount, int mode, int fade, boolean interruptable, int instanceID);
    /** Apply "Vintage colors" effect for path and store result in another path on file system. */
    public static native boolean vintage4path(String in_path, String out_path, int out_width, int out_height,
        double amount, int mode, int fade, boolean interruptable, int instanceID);
    /** Apply "Vintage colors" effect for path and store result in buffer 8888.
     *  Output buffer should be allocated "directly" before calling this method.
     */
    public static native void vintage4mix(String in_path, Buffer out_pixels, int out_width, int out_height,
        double amount, int mode, int fade, boolean interruptable, int instanceID);

    // CrossProcessing effect.
    /** Apply "CrossProcessing" effect for buffer 8888 and store result in another buffer 8888.
     *  All buffers should be allocated "directly" before calling this method.
     */
    public static native void crossprocessing4buf(Buffer inPixels, Buffer outPixels, int in_width, int in_height, int out_width, int out_height,
        double contrast, double amount, int mode, int fade, boolean interruptable, int instanceID);
    /** Apply "CrossProcessing" effect for path and store result in another path on file system. */
    public static native boolean crossprocessing4path(String in_path, String out_path, int out_width, int out_height,
        double contrast, double amount, int mode, int fade, boolean interruptable, int instanceID);
    /** Apply "CrossProcessing" effect for path and store result in buffer 8888.
     *  Output buffer should be allocated "directly" before calling this method.
     */
    public static native void crossprocessing4mix(String in_path, Buffer out_pixels, int out_width, int out_height,
        double contrast, double amount, int mode, int fade, boolean interruptable, int instanceID);

    // CrossProcess effect.
    /** Apply "CrossProcess" effect for buffer 8888 and store result in another buffer 8888.
     *  All buffers should be allocated "directly" before calling this method.
     */
    public static native void crossprocess4buf(Buffer inPixels, Buffer outPixels, int in_width, int in_height, int out_width, int out_height,
        int mode, int fade, boolean interruptable, int instanceID);
    /** Apply "CrossProcess" effect for path and store result in another path on file system. */
    public static native boolean crossprocess4path(String in_path, String out_path, int out_width, int out_height,
        int mode, int fade, boolean interruptable, int instanceID);
    /** Apply "CrossProcess" effect for path and store result in buffer 8888.
     *  Output buffer should be allocated "directly" before calling this method.
     */
    public static native void crossprocess4mix(String in_path, Buffer out_pixels, int out_width, int out_height,
        int mode, int fade, boolean interruptable, int instanceID);

    // HDR effect.
    /** Apply "HDR" effect for buffer 8888 and store result in another buffer 8888.
     *  All buffers should be allocated "directly" before calling this method.
     */
    public static native void hdr4buf(Buffer inPixels, Buffer outPixels, int in_width, int in_height, int out_width, int out_height,
        int blur, double unsharp, double saturation, int mode, int fade, boolean interruptable, int instanceID);
    /** Apply "HDR" effect for path and store result in another path on file system. */
    public static native boolean hdr4path(String in_path, String out_path, int out_width, int out_height,
        int blur, double unsharp, double saturation, int mode, int fade, boolean interruptable, int instanceID);
    /** Apply "HDR" effect for path and store result in buffer 8888.
     *  Output buffer should be allocated "directly" before calling this method.
     */
    public static native void hdr4mix(String in_path, Buffer out_pixels, int out_width, int out_height,
        int blur, double unsharp, double saturation, int mode, int fade, boolean interruptable, int instanceID);

    // Red eye removal effect.
    /** Apply "Red eye removal" effect for buffer 8888 and store result in another buffer 8888.
     *  All buffers should be allocated "directly" before calling this method.
     */
    public static native void redeyeremoval4buf(Buffer inPixels, Buffer outPixels, int in_width, int in_height, int out_width, int out_height,
        int x, int y, double relative_radius, double amount);
    /** Apply "HDR" effect for path and store result in another path on file system. */
    public static native boolean redeyeremoval4path(String in_path, String out_path, int out_width, int out_height,
        int x, int y, double relative_radius, double amount);
    /** Apply "HDR" effect for path and store result in buffer 8888.
     *  Output buffer should be allocated "directly" before calling this method.
     */
    public static native void redeyeremoval4mix(String in_path, Buffer out_pixels, int out_width, int out_height,
        int x, int y, double relative_radius, double amount);

    // Fattal effect.
    /** Apply "Fattal" effect for buffer 8888 and store result in another buffer 8888.
     *  All buffers should be allocated "directly" before calling this method.
     */
    public static native void fattal4buf(Buffer inPixels, Buffer outPixels, int in_width, int in_height, int out_width, int out_height,
        double alfa, double beta, double saturation, double noise, int mode, int internal_width, int internal_height, int fade, boolean interruptable, int instanceID);
    /** Apply "Fattal" effect for path and store result in another path on file system. */
    public static native boolean fattal4path(String in_path, String out_path, int out_width, int out_height,
        double alfa, double beta, double saturation, double noise, int mode, int internal_width, int internal_height, int fade, boolean interruptable, int instanceID);
    /** Apply "Fattal" effect for path and store result in buffer 8888.
     *  Output buffer should be allocated "directly" before calling this method.
     */
    public static native void fattal4mix(String in_path, Buffer out_pixels, int out_width, int out_height,
        double alfa, double beta, double saturation, double noise, int mode, int internal_width, int internal_height, int fade, boolean interruptable, int instanceID);

    // Pencil effect.
    /** Apply "Pencil" effect for buffer 8888 and store result in another buffer 8888.
     *  All buffers should be allocated "directly" before calling this method.
     */
    public static native void pencil4buf(Buffer inPixels, Buffer outPixels, int in_width, int in_height, int out_width, int out_height,
        double amount, double length, double details, int fade, boolean interruptable, int instanceID);
    /** Apply "Pencil" effect for path and store result in another path on file system. */
    public static native boolean pencil4path(String in_path, String out_path, int out_width, int out_height,
        double amount, double length, double details, int fade, boolean interruptable, int instanceID);
    /** Apply "Pencil" effect for path and store result in buffer 8888.
     *  Output buffer should be allocated "directly" before calling this method.
     */
    public static native void pencil4mix(String in_path, Buffer out_pixels, int out_width, int out_height,
        double amount, double length, double details, int fade, boolean interruptable, int instanceID);

    // Holgaart effect.
    /** Apply "Holgaart" effect for buffer 8888 and store result in another buffer 8888.
     *  All buffers should be allocated "directly" before calling this method.
     */
    public static native void holgaart4buf(Buffer inPixels, Buffer outPixels, int in_width, int in_height, int out_width, int out_height,
        int left, int right, int vignette, Buffer overlay, int bg_width, int bg_height, int fade, boolean interruptable, int instanceID);
    /** Apply "Holgaart" effect for path and store result in another path on file system. */
    public static native boolean holgaart4path(String in_path, String out_path, int out_width, int out_height,
        int left, int right, int vignette, String overlay_path, int fade, boolean interruptable, int instanceID);
    /** Apply "Holgaart" effect for path and store result in buffer 8888.
     *  Output buffer should be allocated "directly" before calling this method.
     */
    public static native void holgaart4mix(String in_path, Buffer out_pixels, int out_width, int out_height,
        int left, int right, int vignette, String overlay_path, int fade, boolean interruptable, int instanceID);

    // Face detector.
    /** Apply "Face detector" for buffer 8888 and store result in another buffer 8888.
     *  All buffers should be allocated "directly" before calling this method.
     */
    public static native boolean initfacedetector(String cascade_path);
    public static native void facedetector4buf(Buffer inPixels, Buffer outPixels, int in_width, int in_height, int out_width, int out_height,
        double scale_factor, boolean do_canny_pruning, boolean find_biggest_object);
    /** Apply "Face detector" effect for path and store result in another path on file system. */
    public static native boolean facedetector4path(String in_path, String out_path, int out_width, int out_height,
        double scale_factor, boolean do_canny_pruning, boolean find_biggest_object);
    /** Apply "Face detector" effect for path and store result in buffer 8888.
     *  Output buffer should be allocated "directly" before calling this method.
     */
    public static native void facedetector4mix(String in_path, Buffer out_pixels, int out_width, int out_height,
        double scale_factor, boolean do_canny_pruning, boolean find_biggest_object);

    // Black & white.
    /** Apply "B&W" effect for buffer 8888 and store result in another buffer 8888.
     *  All buffers should be allocated "directly" before calling this method.
     */
    public static native void bw4buf(Buffer inPixels, Buffer outPixels, int in_width, int in_height, int out_width, int out_height,
        int mode, int fade, boolean interruptable, int instanceID);
    /** Apply "B&W" effect for path and store result in another path on file system. */
    public static native boolean bw4path(String in_path, String out_path, int out_width, int out_height,
        int mode, int fade, boolean interruptable, int instanceID);
    /** Apply "B&W" effect for path and store result in buffer 8888.
     *  Output buffer should be allocated "directly" before calling this method.
     */
    public static native void bw4mix(String in_path, Buffer out_pixels, int out_width, int out_height,
        int mode, int fade, boolean interruptable, int instanceID);


    // Soften.
    /** Apply "Soften" effect for buffer 8888 and store result in another buffer 8888.
     *  All buffers should be allocated "directly" before calling this method.
     */
    public static native void soften4buf(Buffer inPixels, Buffer outPixels, int in_width, int in_height, int out_width, int out_height,
        int blur, int fade, boolean interruptable, int instanceID);
    /** Apply "Soften" effect for path and store result in another path on file system. */
    public static native boolean soften4path(String in_path, String out_path, int out_width, int out_height,
        int blur, int fade, boolean interruptable, int instanceID);
    /** Apply "Soften" effect for path and store result in buffer 8888.
     *  Output buffer should be allocated "directly" before calling this method.
     */
    public static native void soften4mix(String in_path, Buffer out_pixels, int out_width, int out_height,
        int blur, int fade, boolean interruptable, int instanceID);


    // Focal soften.
    /** Apply "Focal soften" effect for buffer 8888 and store result in another buffer 8888.
     *  All buffers should be allocated "directly" before calling this method.
     */
    public static native void focalsoften4buf(Buffer inPixels, Buffer outPixels, int in_width, int in_height, int out_width, int out_height,
        int blur, int fade, int x, int y, int radius, int hardness, int mode, boolean interruptable, int instanceID);
    /** Apply "Focal soften" effect for path and store result in another path on file system. */
    public static native boolean focalsoften4path(String in_path, String out_path, int out_width, int out_height,
        int blur, int fade, int x, int y, int radius, int hardness, int mode, boolean interruptable, int instanceID);
    /** Apply "Focal soften" effect for path and store result in buffer 8888.
     *  Output buffer should be allocated "directly" before calling this method.
     */
    public static native void focalsoften4mix(String in_path, Buffer out_pixels, int out_width, int out_height,
        int blur, int fade, int x, int y, int radius, int hardness, int mode, boolean interruptable, int instanceID);


    // Focal zoom.
    /** Apply "Focal zoom" effect for buffer 8888 and store result in another buffer 8888.
     *  All buffers should be allocated "directly" before calling this method.
     */
    public static native void focalzoom4buf(Buffer inPixels, Buffer outPixels, int in_width, int in_height, int out_width, int out_height,
    		int blur, int center_x, int center_y, int radius, int hardness, int blend_mode, int fade, boolean interruptable, int instanceID);
    /** Apply "Focal zoom" effect for path and store result in another path on file system. */
    public static native boolean focalzoom4path(String in_path, String out_path, int out_width, int out_height,
    		int blur, int center_x, int center_y, int radius, int hardness, int blend_mode, int fade, boolean interruptable, int instanceID);
    /** Apply "Focal zoom" effect for path and store result in buffer 8888.
     *  Output buffer should be allocated "directly" before calling this method.
     */
    public static native void focalzoom4mix(String in_path, Buffer out_pixels, int out_width, int out_height,
    		int blur, int center_x, int center_y, int radius, int hardness, int blend_mode, int fade, boolean interruptable, int instanceID);

    
    
    // Comic boom.
    /** Apply "Comic boom" effect for buffer 8888 and store result in another buffer 8888.
     *  All buffers should be allocated "directly" before calling this method.
     */
    public static native void comicboom4buf(Buffer inPixels, Buffer outPixels, int in_width, int in_height, int out_width, int out_height,
        Buffer overlay, int overlay_width, int overlay_height, int lines, int brightness, int fade, boolean interruptable, int instanceID);
    /** Apply "Comic boom" effect for path and store result in another path on file system. */
    public static native boolean comicboom4path(String in_path, String out_path, int out_width, int out_height,
        String overlay_path, int lines, int brightness, int fade, boolean interruptable, int instanceID);
    /** Apply "Comic boom" effect for path and store result in buffer 8888.
     *  Output buffer should be allocated "directly" before calling this method.
     */
    public static native void comicboom4mix(String in_path, Buffer out_pixels, int out_width, int out_height,
        String overlay_path, int lines, int brightness, int fade, boolean interruptable, int instanceID);



    // Neon cola.
    /** Apply "Neon cola" effect for buffer 8888 and store result in another buffer 8888.
     *  All buffers should be allocated "directly" before calling this method.
     */
    public static native void neoncola4buf(Buffer inPixels, Buffer outPixels, int in_width, int in_height, int out_width, int out_height,
        int lines, int brightness, int contrast, int fade, boolean interruptable, int instanceID);
    /** Apply "Neon cola" effect for path and store result in another path on file system. */
    public static native boolean neoncola4path(String in_path, String out_path, int out_width, int out_height,
        int lines, int brightness, int contrast, int fade, boolean interruptable, int instanceID);
    /** Apply "Neon cola" effect for path and store result in buffer 8888.
     *  Output buffer should be allocated "directly" before calling this method.
     */
    public static native void neoncola4mix(String in_path, Buffer out_pixels, int out_width, int out_height,
        int lines, int brightness, int contrast, int fade, boolean interruptable, int instanceID);



    // Acquarello.
    /** Apply "Acquarello" effect for buffer 8888 and store result in another buffer 8888.
     *  All buffers should be allocated "directly" before calling this method.
     */
    public static native void acquarello4buf(Buffer inPixels, Buffer outPixels, int in_width, int in_height, int out_width, int out_height,
        int lines, int brightness, int contrast, int fade, boolean interruptable, int instanceID);
    /** Apply "Acquarello" effect for path and store result in another path on file system. */
    public static native boolean acquarello4path(String in_path, String out_path, int out_width, int out_height,
        int lines, int brightness, int contrast, int fade, boolean interruptable, int instanceID);
    /** Apply "Acquarello" effect for path and store result in buffer 8888.
     *  Output buffer should be allocated "directly" before calling this method.
     */
    public static native void acquarello4mix(String in_path, Buffer out_pixels, int out_width, int out_height,
        int lines, int brightness, int contrast, int fade, boolean interruptable, int instanceID);



    // SketchUp.
    /** Apply "SketchUp" effect for buffer 8888 and store result in another buffer 8888.
     *  All buffers should be allocated "directly" before calling this method.
     */
    public static native void sketchup4buf(Buffer inPixels, Buffer outPixels, int in_width, int in_height, int out_width, int out_height,
        Buffer overlay, int overlay_width, int overlay_height, int lines, int brightness, int contrast, int fade, boolean interruptable, int instanceID);
    /** Apply "SketchUp" effect for path and store result in another path on file system. */
    public static native boolean sketchup4path(String in_path, String out_path, int out_width, int out_height,
        String overlay_path, int lines, int brightness, int contrast, int fade, boolean interruptable, int instanceID);
    /** Apply "SketchUp" effect for path and store result in buffer 8888.
     *  Output buffer should be allocated "directly" before calling this method.
     */
    public static native void sketchup4mix(String in_path, Buffer out_pixels, int out_width, int out_height,
        String overlay_path, int lines, int brightness, int contrast, int fade, boolean interruptable, int instanceID);



    // ConTours.
    /** Apply "ConTours" effect for buffer 8888 and store result in another buffer 8888.
     *  All buffers should be allocated "directly" before calling this method.
     */
    public static native void contours4buf(Buffer inPixels, Buffer outPixels, int in_width, int in_height, int out_width, int out_height,
        int lines, int brightness, int contrast, int fade, boolean interruptable, int instanceID);
    /** Apply "ConTours" effect for path and store result in another path on file system. */
    public static native boolean contours4path(String in_path, String out_path, int out_width, int out_height,
        int lines, int brightness, int contrast, int fade, boolean interruptable, int instanceID);
    /** Apply "ConTours" effect for path and store result in buffer 8888.
     *  Output buffer should be allocated "directly" before calling this method.
     */
    public static native void contours4mix(String in_path, Buffer out_pixels, int out_width, int out_height,
        int lines, int brightness, int contrast, int fade, boolean interruptable, int instanceID);



    // Bleaching.
    /** Apply "Bleaching" effect for buffer 8888 and store result in another buffer 8888.
     *  All buffers should be allocated "directly" before calling this method.
     */
    public static native void bleaching4buf(Buffer inPixels, Buffer outPixels, int in_width, int in_height, int out_width, int out_height,
        Buffer overlay, int overlay_width, int overlay_height, int lines, int brightness, int contrast, int fade, boolean interruptable, int instanceID);
    /** Apply "Bleaching" effect for path and store result in another path on file system. */
    public static native boolean bleaching4path(String in_path, String out_path, int out_width, int out_height,
        String overlay_path, int lines, int brightness, int contrast, int fade, boolean interruptable, int instanceID);
    /** Apply "Bleaching" effect for path and store result in buffer 8888.
     *  Output buffer should be allocated "directly" before calling this method.
     */
    public static native void bleaching4mix(String in_path, Buffer out_pixels, int out_width, int out_height,
        String overlay_path, int lines, int brightness, int contrast, int fade, boolean interruptable, int instanceID);



    // Granny's Paper.
    /** Apply "Granny's Paper" effect for buffer 8888 and store result in another buffer 8888.
     *  All buffers should be allocated "directly" before calling this method.
     */
    public static native void grannyspaper4buf(Buffer inPixels, Buffer outPixels, int in_width, int in_height, int out_width, int out_height,
        Buffer overlay1, int overlay1_width, int overlay1_height, Buffer overlay2, int overlay2_width, int overlay2_height,
        int lines, int brightness, int contrast, int fade, boolean interruptable, int instanceID);
    /** Apply "Granny's Paper" effect for path and store result in another path on file system. */
    public static native boolean grannyspaper4path(String in_path, String out_path, int out_width, int out_height,
        String overlay1_path, String overlay2_path, int lines, int brightness, int contrast, int fade, boolean interruptable, int instanceID);
    /** Apply "Granny's Paper" effect for path and store result in buffer 8888.
     *  Output buffer should be allocated "directly" before calling this method.
     */
    public static native void grannyspaper4mix(String in_path, Buffer out_pixels, int out_width, int out_height,
        String overlay1_path, String overlay2_path, int lines, int brightness, int contrast, int fade, boolean interruptable, int instanceID);



    // Pastel Perfect.
    /** Apply "Pastel Perfect" effect for buffer 8888 and store result in another buffer 8888.
     *  All buffers should be allocated "directly" before calling this method.
     */
    public static native void pastelperfect4buf(Buffer inPixels, Buffer outPixels, int in_width, int in_height, int out_width, int out_height,
        Buffer overlay, int overlay_width, int overlay_height, int lines, int brightness, int contrast, int fade, boolean interruptable, int instanceID);
    /** Apply "Pastel Perfect" effect for path and store result in another path on file system. */
    public static native boolean pastelperfect4path(String in_path, String out_path, int out_width, int out_height,
        String overlay_path, int lines, int brightness, int contrast, int fade, boolean interruptable, int instanceID);
    /** Apply "Pastel Perfect" effect for path and store result in buffer 8888.
     *  Output buffer should be allocated "directly" before calling this method.
     */
    public static native void pastelperfect4mix(String in_path, Buffer out_pixels, int out_width, int out_height,
        String overlay_path, int lines, int brightness, int contrast, int fade, boolean interruptable, int instanceID);



    // Smart blur.
    /** Apply "Smart blur" effect for buffer 8888 and store result in another buffer 8888.
     *  All buffers should be allocated "directly" before calling this method.
     */
    public static native void smartblur4buf(Buffer inPixels, Buffer outPixels, int in_width, int in_height, int out_width, int out_height,
        int max_blur, int amount, int fade, boolean interruptable, int instanceID);
    /** Apply "Smart blur" effect for path and store result in another path on file system. */
    public static native boolean smartblur4path(String in_path, String out_path, int out_width, int out_height,
        int max_blur, int amount, int fade, boolean interruptable, int instanceID);
    /** Apply "Smart blur" effect for path and store result in buffer 8888.
     *  Output buffer should be allocated "directly" before calling this method.
     */
    public static native void smartblur4mix(String in_path, Buffer out_pixels, int out_width, int out_height,
        int max_blur, int amount, int fade, boolean interruptable, int instanceID);

    
    
    // Holgaart1 effect.
    /** Apply "Holgaart1" effect for buffer 8888 and store result in another buffer 8888.
     *  All buffers should be allocated "directly" before calling this method.
     */
    public static native void holgaart14buf(Buffer inPixels, Buffer outPixels, int in_width, int in_height, int out_width, int out_height,
        int left, int right, int fade, boolean interruptable, int instanceID);
    /** Apply "Holgaart1" effect for path and store result in another path on file system. */
    public static native boolean holgaart14path(String in_path, String out_path, int out_width, int out_height,
        int left, int right, int fade, boolean interruptable, int instanceID);
    /** Apply "Holgaart1" effect for path and store result in buffer 8888.
     *  Output buffer should be allocated "directly" before calling this method.
     */
    public static native void holgaart14mix(String in_path, Buffer out_pixels, int out_width, int out_height,
        int left, int right, int fade, boolean interruptable, int instanceID);



    // Sunless Tan effect.
    /** Apply "Sunless Tan" effect for buffer 8888 and store result in another buffer 8888.
     *  All buffers should be allocated "directly" before calling this method.
     */
    public static native void sunlesstan4buf(Buffer inPixels, Buffer outPixels, int in_width, int in_height, int out_width, int out_height,
        int mode, int fade, boolean interruptable, int instanceID);
    /** Apply "Sunless Tan" effect for path and store result in another path on file system. */
    public static native boolean sunlesstan4path(String in_path, String out_path, int out_width, int out_height,
        int mode, int fade, boolean interruptable, int instanceID);
    /** Apply "Sunless Tan" effect for path and store result in buffer 8888.
     *  Output buffer should be allocated "directly" before calling this method.
     */
    public static native void sunlesstan4mix(String in_path, Buffer out_pixels, int out_width, int out_height,
        int mode, int fade, boolean interruptable, int instanceID);



    // Blemish Fix effect.
    /** Apply "Blemish Fix" effect for buffer 8888 and store result in another buffer 8888.
     *  All buffers should be allocated "directly" before calling this method.
     */
    public static native void blemishfix4buf(Buffer inPixels, Buffer outPixels, int in_width, int in_height, int out_width, int out_height,
        int x, int y, int r, int amount, int method, boolean interruptable, int instanceID);
    /** Apply "Blemish Fix" effect for path and store result in another path on file system. */
    public static native boolean blemishfix4path(String in_path, String out_path, int out_width, int out_height,
        int x, int y, int r, int amount, int method, boolean interruptable, int instanceID);
    /** Apply "Blemish Fix" effect for path and store result in buffer 8888.
     *  Output buffer should be allocated "directly" before calling this method.
     */
    public static native void blemishfix4mix(String in_path, Buffer out_pixels, int out_width, int out_height,
        int x, int y, int r, int amount, int method, boolean interruptable, int instanceID);



    // United colors effect.
    /** Apply "United colors" effect for buffer 8888 and store result in another buffer 8888.
     *  All buffers should be allocated "directly" before calling this method.
     */
    public static native void unitedcolors4buf(Buffer inPixels, Buffer outPixels, int in_width, int in_height, int out_width, int out_height,
        int mode, int color1, int color2, int fade, boolean interruptable, int instanceID);
    /** Apply "Blemish Fix" effect for path and store result in another path on file system. */
    public static native boolean unitedcolors4path(String in_path, String out_path, int out_width, int out_height,
        int mode, int color1, int color2, int fade, boolean interruptable, int instanceID);
    /** Apply "Blemish Fix" effect for path and store result in buffer 8888.
     *  Output buffer should be allocated "directly" before calling this method.
     */
    public static native void unitedcolors4mix(String in_path, Buffer out_pixels, int out_width, int out_height,
        int mode, int color1, int color2, int fade, boolean interruptable, int instanceID);



    // Vignette effect.
    /** Apply "Vignette" effect for buffer 8888 and store result in another buffer 8888.
     *  All buffers should be allocated "directly" before calling this method.
     */
    public static native void vignette4buf(Buffer inPixels, Buffer outPixels, int in_width, int in_height, int out_width, int out_height,
        int color, int radius, int algorithm, int fade, boolean interruptable, int instanceID);
    /** Apply "Vignette" effect for path and store result in another path on file system. */
    public static native boolean vignette4path(String in_path, String out_path, int out_width, int out_height,
        int color, int radius, int algorithm, int fade, boolean interruptable, int instanceID);
    /** Apply "Vignette" effect for path and store result in buffer 8888.
     *  Output buffer should be allocated "directly" before calling this method.
     */
    public static native void vignette4mix(String in_path, Buffer out_pixels, int out_width, int out_height,
        int color, int radius, int algorithm, int fade, boolean interruptable, int instanceID);

    // Halftone dots effect.
    /** Apply "Halftone dots" effect for buffer 8888 and store result in another buffer 8888.
     *  All buffers should be allocated "directly" before calling this method.
     */
    public static native void halftonedots4buf(Buffer inPixels, Buffer outPixels, int in_width, int in_height, int out_width, int out_height,
    		int radius, int contrast, int fade, boolean interruptable, int instanceID);
    /** Apply "Halftone dots" effect for path and store result in another path on file system. */
    public static native boolean halftonedots4path(String in_path, String out_path, int out_width, int out_height,
    		int radius, int contrast, int fade, boolean interruptable, int instanceID);
    /** Apply "Halftone dots" effect for path and store result in buffer 8888.
     *  Output buffer should be allocated "directly" before calling this method.
     */
    public static native void halftonedots4mix(String in_path, Buffer out_pixels, int out_width, int out_height,
    		int radius, int contrast, int fade, boolean interruptable, int instanceID);

    // Motion blur effect.
    /** Apply "Motion blur" effect for buffer 8888 and store result in another buffer 8888.
     *  All buffers should be allocated "directly" before calling this method.
     */
    public static native void motionblur4buf(Buffer inPixels, Buffer outPixels, int in_width, int in_height, int out_width, int out_height,
    		int distance, int angle, boolean colorize, int h, int l, int s, int noise, int fade, boolean interruptable, int instanceID);
    /** Apply "Motion blur" effect for path and store result in another path on file system. */
    public static native boolean motionblur4path(String in_path, String out_path, int out_width, int out_height,
    		int distance, int angle, boolean colorize, int h, int l, int s, int noise, int fade, boolean interruptable, int instanceID);
    /** Apply "Motion blur" effect for path and store result in buffer 8888.
     *  Output buffer should be allocated "directly" before calling this method.
     */
    public static native void motionblur4mix(String in_path, Buffer out_pixels, int out_width, int out_height,
    		int distance, int angle, boolean colorize, int h, int l, int s, int noise, int fade, boolean interruptable, int instanceID);
    
    // Tranquil effect.
    /** Apply "Tranquil" effect for buffer 8888 and store result in another buffer 8888.
     *  All buffers should be allocated "directly" before calling this method.
     */
    public static native void tranquil4buf(Buffer inPixels, Buffer outPixels, int in_width, int in_height, int out_width, int out_height,
    		int contrast, int brightness, int fade, boolean interruptable, int instanceID);
    /** Apply "Tranquil" effect for path and store result in another path on file system. */
    public static native boolean tranquil4path(String in_path, String out_path, int out_width, int out_height,
    		int contrast, int brightness, int fade, boolean interruptable, int instanceID);
    /** Apply "Tranquil" effect for path and store result in buffer 8888.
     *  Output buffer should be allocated "directly" before calling this method.
     */
    public static native void tranquil4mix(String in_path, Buffer out_pixels, int out_width, int out_height,
    		int contrast, int brightness, int fade, boolean interruptable, int instanceID);
    
    // Posterize effect.
    /** Apply "Posterize" effect for buffer 8888 and store result in another buffer 8888.
     *  All buffers should be allocated "directly" before calling this method.
     */
    public static native void posterize4buf(Buffer inPixels, Buffer outPixels, int in_width, int in_height, int out_width, int out_height,
    		int colors, int details, int fade, boolean interruptable, int instanceID);
    /** Apply "Posterize" effect for path and store result in another path on file system. */
    public static native boolean posterize4path(String in_path, String out_path, int out_width, int out_height,
    		int colors, int details, int fade, boolean interruptable, int instanceID);
    /** Apply "Posterize" effect for path and store result in buffer 8888.
     *  Output buffer should be allocated "directly" before calling this method.
     */
    public static native void posterize4mix(String in_path, Buffer out_pixels, int out_width, int out_height,
    		int colors, int details, int fade, boolean interruptable, int instanceID);
    
    // Dusk effect.
    /** Apply "Dusk" effect for buffer 8888 and store result in another buffer 8888.
     *  All buffers should be allocated "directly" before calling this method.
     */
    public static native void dusk4buf(Buffer inPixels, Buffer outPixels, int in_width, int in_height, int out_width, int out_height,
    		int fade, boolean interruptable, int instanceID);
    /** Apply "Dusk" effect for path and store result in another path on file system. */
    public static native boolean dusk4path(String in_path, String out_path, int out_width, int out_height,
    		int fade, boolean interruptable, int instanceID);
    /** Apply "Dusk" effect for path and store result in buffer 8888.
     *  Output buffer should be allocated "directly" before calling this method.
     */
    public static native void dusk4mix(String in_path, Buffer out_pixels, int out_width, int out_height,
    		int fade, boolean interruptable, int instanceID);
    
    // YesterColor effect.
    /** Apply "YesterColor" effect for buffer 8888 and store result in another buffer 8888.
     *  All buffers should be allocated "directly" before calling this method.
     */
    public static native void yestercolor4buf(Buffer inPixels, Buffer outPixels, int in_width, int in_height, int out_width, int out_height,
    		int color, int fade, boolean interruptable, int instanceID);
    /** Apply "YesterColor" effect for path and store result in another path on file system. */
    public static native boolean yestercolor4path(String in_path, String out_path, int out_width, int out_height,
    		int color, int fade, boolean interruptable, int instanceID);
    /** Apply "YesterColor" effect for path and store result in buffer 8888.
     *  Output buffer should be allocated "directly" before calling this method.
     */
    public static native void yestercolor4mix(String in_path, Buffer out_pixels, int out_width, int out_height,
    		int color, int fade, boolean interruptable, int instanceID);
    
    // Cinerama effect.
    /** Apply "Cinerama" effect for buffer 8888 and store result in another buffer 8888.
     *  All buffers should be allocated "directly" before calling this method.
     */
    public static native void cinerama4buf(Buffer inPixels, Buffer outPixels, int in_width, int in_height, int out_width, int out_height,
    		int fade, boolean interruptable, int instanceID);
    /** Apply "Cinerama" effect for path and store result in another path on file system. */
    public static native boolean cinerama4path(String in_path, String out_path, int out_width, int out_height,
    		int fade, boolean interruptable, int instanceID);
    /** Apply "Cinerama" effect for path and store result in buffer 8888.
     *  Output buffer should be allocated "directly" before calling this method.
     */
    public static native void cinerama4mix(String in_path, Buffer out_pixels, int out_width, int out_height,
    		int fade, boolean interruptable, int instanceID);
    
    // Custom Enhance effect.
    /** Apply "Custom Enhance" effect for buffer 8888 and store result in another buffer 8888.
     *  All buffers should be allocated "directly" before calling this method.
     */
    public static native void customenhance4buf(Buffer inPixels, Buffer outPixels, int in_width, int in_height, int out_width, int out_height,
            float hist_s, float saturation, int fade, boolean interruptable, int instanceID);
    /** Apply "Custom Enhance" effect for path and store result in another path on file system. */
    public static native boolean customenhance4path(String in_path, String out_path, int out_width, int out_height,
            float hist_s, float saturation, int fade, boolean interruptable, int instanceID);
    /** Apply "Custom Enhance" effect for path and store result in buffer 8888.
     *  Output buffer should be allocated "directly" before calling this method.
     */
    public static native void customenhance4mix(String in_path, Buffer out_pixels, int out_width, int out_height,
            float hist_s, float saturation, int fade, boolean interruptable, int instanceID);
    
 // Sharpen Dodger effect.
    /** Apply "Sharpen Dodger" effect for buffer 8888 and store result in another buffer 8888.
     *  All buffers should be allocated "directly" before calling this method.
     */
    public static native void sharpendodger4buf(Buffer inPixels, Buffer outPixels, int in_width, int in_height, int out_width, int out_height,
            int fade, boolean interruptable, int instanceID);
    /** Apply "Sharpen Dodger" effect for path and store result in another path on file system. */
    public static native boolean sharpendodger4path(String in_path, String out_path, int out_width, int out_height,
            int fade, boolean interruptable, int instanceID);
    /** Apply "Sharpen Dodger" effect for path and store result in buffer 8888.
     *  Output buffer should be allocated "directly" before calling this method.
     */
    public static native void sharpendodger4mix(String in_path, Buffer out_pixels, int out_width, int out_height,
            int fade, boolean interruptable, int instanceID);
    
    // Vintage Ivory effect.
    /** Apply "Vintage Ivory" effect for buffer 8888 and store result in another buffer 8888.
     *  All buffers should be allocated "directly" before calling this method.
     */
    public static native void vintageivory4buf(Buffer inPixels, Buffer outPixels, int in_width, int in_height, int out_width, int out_height,
            int fade, boolean interruptable, int instanceID);
    /** Apply "Vintage Ivory" effect for path and store result in another path on file system. */
    public static native boolean vintageivory4path(String in_path, String out_path, int out_width, int out_height,
            int fade, boolean interruptable, int instanceID);
    /** Apply "Vintage Ivory" effect for path and store result in buffer 8888.
     *  Output buffer should be allocated "directly" before calling this method.
     */
    public static native void vintageivory4mix(String in_path, Buffer out_pixels, int out_width, int out_height,
            int fade, boolean interruptable, int instanceID);


    // Pyramid Noise Reduction effect.
    /** Apply "Pyramid Noise Reduction" effect for buffer 8888 and store result in another buffer 8888.
     *  All buffers should be allocated "directly" before calling this method.
     */
    public static native void pyramidnoisereduction4buf(Buffer inPixels, Buffer outPixels, int in_width, int in_height, int out_width, int out_height,
                                                        float lumo_sigma, float chromo_sigma, float details, float contrast, float saturation, int fade, boolean interruptable, int instanceID);
    /** Apply "Pyramid Noise Reduction" effect for path and store result in another path on file system. */
    public static native boolean pyramidnoisereduction4path(String in_path, String out_path, int out_width, int out_height,
                                                            float lumo_sigma, float chromo_sigma, float details, float contrast, float saturation, int fade, boolean interruptable, int instanceID);
    /** Apply "Pyramid Noise Reduction" effect for path and store result in buffer 8888.
     *  Output buffer should be allocated "directly" before calling this method.
     */
    public static native void pyramidnoisereduction4mix(String in_path, Buffer out_pixels, int out_width, int out_height,
                                                        float lumo_sigma, float chromo_sigma, float details, float contrast, float saturation, int fade, boolean interruptable, int instanceID);

    // Seafoam Light Cross effect.
    /** Apply "Seafoam Light Cross" effect for buffer 8888 and store result in another buffer 8888.
     *  All buffers should be allocated "directly" before calling this method.
     */
    public static native void seafoamlightcross4buf(Buffer inPixels, Buffer outPixels, int in_width, int in_height, int out_width, int out_height,
                                                    int fade, boolean interruptable, int instanceID);
    /** Apply "Seafoam Light Cross" effect for path and store result in another path on file system. */
    public static native boolean seafoamlightcross4path(String in_path, String out_path, int out_width, int out_height,
                                                        int fade, boolean interruptable, int instanceID);
    /** Apply "Seafoam Light Cross" effect for path and store result in buffer 8888.
     *  Output buffer should be allocated "directly" before calling this method.
     */
    public static native void seafoamlightcross4mix(String in_path, Buffer out_pixels, int out_width, int out_height,
                                                    int fade, boolean interruptable, int instanceID);

    // Warming Amber effect.
    /** Apply "Warming Amber" effect for buffer 8888 and store result in another buffer 8888.
     *  All buffers should be allocated "directly" before calling this method.
     */
    public static native void warmingamber4buf(Buffer inPixels, Buffer outPixels, int in_width, int in_height, int out_width, int out_height,
                                               int fade, boolean interruptable, int instanceID);
    /** Apply "Warming Amber" effect for path and store result in another path on file system. */
    public static native boolean warmingamber4path(String in_path, String out_path, int out_width, int out_height,
                                                   int fade, boolean interruptable, int instanceID);
    /** Apply "Warming Amber" effect for path and store result in buffer 8888.
     *  Output buffer should be allocated "directly" before calling this method.
     */
    public static native void warmingamber4mix(String in_path, Buffer out_pixels, int out_width, int out_height,
                                               int fade, boolean interruptable, int instanceID);

    // Effect #7 effect.
    /** Apply "Effect #7" effect for buffer 8888 and store result in another buffer 8888.
     *  All buffers should be allocated "directly" before calling this method.
     */
    public static native void effect74buf(Buffer inPixels, Buffer outPixels, int in_width, int in_height, int out_width, int out_height,
                                          int fade, boolean interruptable, int instanceID);
    /** Apply "Effect #7" effect for path and store result in another path on file system. */
    public static native boolean effect74path(String in_path, String out_path, int out_width, int out_height,
                                              int fade, boolean interruptable, int instanceID);
    /** Apply "Effect #7" effect for path and store result in buffer 8888.
     *  Output buffer should be allocated "directly" before calling this method.
     */
    public static native void effect74mix(String in_path, Buffer out_pixels, int out_width, int out_height,
                                          int fade, boolean interruptable, int instanceID);

    // Effect #12 effect.
    /** Apply "Effect #12" effect for buffer 8888 and store result in another buffer 8888.
     *  All buffers should be allocated "directly" before calling this method.
     */
    public static native void effect124buf(Buffer inPixels, Buffer outPixels, int in_width, int in_height, int out_width, int out_height,
                                           int fade, boolean interruptable, int instanceID);
    /** Apply "Effect #12" effect for path and store result in another path on file system. */
    public static native boolean effect124path(String in_path, String out_path, int out_width, int out_height,
                                               int fade, boolean interruptable, int instanceID);
    /** Apply "Effect #12" effect for path and store result in buffer 8888.
     *  Output buffer should be allocated "directly" before calling this method.
     */
    public static native void effect124mix(String in_path, Buffer out_pixels, int out_width, int out_height,
                                           int fade, boolean interruptable, int instanceID);

    // Effect #13 effect.
    /** Apply "Effect #13" effect for buffer 8888 and store result in another buffer 8888.
     *  All buffers should be allocated "directly" before calling this method.
     */
    public static native void effect134buf(Buffer inPixels, Buffer outPixels, int in_width, int in_height, int out_width, int out_height,
                                           int fade, boolean interruptable, int instanceID);
    /** Apply "Effect #13" effect for path and store result in another path on file system. */
    public static native boolean effect134path(String in_path, String out_path, int out_width, int out_height,
                                               int fade, boolean interruptable, int instanceID);
    /** Apply "Effect #13" effect for path and store result in buffer 8888.
     *  Output buffer should be allocated "directly" before calling this method.
     */
    public static native void effect134mix(String in_path, Buffer out_pixels, int out_width, int out_height,
                                           int fade, boolean interruptable, int instanceID);

    // Effect #14 effect.
    /** Apply "Effect #14" effect for buffer 8888 and store result in another buffer 8888.
     *  All buffers should be allocated "directly" before calling this method.
     */
    public static native void effect144buf(Buffer inPixels, Buffer outPixels, int in_width, int in_height, int out_width, int out_height,
                                           int fade, boolean interruptable, int instanceID);
    /** Apply "Effect #14" effect for path and store result in another path on file system. */
    public static native boolean effect144path(String in_path, String out_path, int out_width, int out_height,
                                               int fade, boolean interruptable, int instanceID);
    /** Apply "Effect #14" effect for path and store result in buffer 8888.
     *  Output buffer should be allocated "directly" before calling this method.
     */
    public static native void effect144mix(String in_path, Buffer out_pixels, int out_width, int out_height,
                                           int fade, boolean interruptable, int instanceID);

    
    // Interrupt effects.
    /** Empty object for synchronization. */
    private static Object mSynchObj = new Object();
    /** Initialize interrupt flags array. */
    private static native void initInterruptFlags();
    /** Obtain free effect instance ID and lock corresponding interrupt flag. */
    private static native int obtainEffectInstanceIdentifier();
    /** Synchronized wrapper for obtainEffectInstanceIdentifier. */
    public static int obtainEffectInstanceID() {
        synchronized (mSynchObj) {
            return obtainEffectInstanceIdentifier();
        }
    }
    /** Release effect instance ID. */
    private static native void releaseEffectInstanceIdentifier(int id);
    /** Synchronized wrapper for releaseEffectInstanceIdentifier. */
    public static void releaseEffectInstanceID(int id) {
        synchronized (mSynchObj) {
            releaseEffectInstanceIdentifier(id);
        }
    }
    /** Interrupt effect instance. */
    public static native void interruptEffectInstance(int id);


    // ByteBuffer management in native code.
    /** Allocation ByteBuffer in native code (memory will not be counted in VM heap). */
    static public ByteBuffer allocNativeBuffer(long size){
    	return ImageOpCommon.allocNativeBuffer(size);
    }
    /** Free ByteBuffer allocated in native code. */
    static public void freeNativeBuffer(ByteBuffer buffer){
    	ImageOpCommon.freeNativeBuffer(buffer);
    }



    // Blending.
    /** Apply blending for buffers 8888 and mask 8888 (null is acceptable) and then store result in another buffer 8888.
     *  All buffers should be allocated "directly" before calling this method.
     */
    public static native void blend4buf(Buffer buf1, Buffer buf2, Buffer mask, int fade, int width, int height, Buffer result, int result_width, int result_height);
    /** Apply blending for paths using mask (null is acceptable) and store result in another path on file system. */
    public static native boolean blend4path(String in_path1, String in_path2, String in_mask, int fade, String out_path, int out_width, int out_height);
    /** Apply blending for path using mask (null is acceptable) and store result in buffer 8888.
     *  Output buffer should be allocated "directly" before calling this method.
     */
    public static native void blend4mix(String in_path1, String in_path2, String in_mask, int fade, Buffer result, int result_width, int result_height);
    /** Apply blending for buffers 8888 and mask 8888 (null is acceptable) and then store result in path on file system. */
    public static native boolean blend4mix2(Buffer buf1, Buffer buf2, Buffer mask, int fade, int width, int height, String result_path, int result_width, int result_height);


    // Auxiliary (may be used for brush and free crop).
    public static native void invertColorInplace(Buffer source, int length);
    public static native void fill8(Buffer buffer8, int w, int h, int color);
    public static native void drawLine8(ByteBuffer buffer8, int w, int h, int color, int width, int x1, int y1, int x2, int y2);
    public static native void applyMask8ForBuffer8888(ByteBuffer buffer8888, ByteBuffer mask8, int w, int h);
    public static native void getBoundingBox8(ByteBuffer buffer8, int w, int h, int[] out_rect);
    public static native void getSubBuffer(ByteBuffer buffer8888, ByteBuffer subbuffer8888, int w, int h, int[] in_rect);
    public static native void getSubBuffer8(ByteBuffer buffer8, ByteBuffer subbuffer8, int w, int h, int[] in_rect);
    
    public static native void copyPartBuffer888(ByteBuffer srcBuffer, ByteBuffer dstBuffer,int srcW, int srcH,int dstW, int dstH,
    		int[] srcbuffer_origin,int[] dstbuffer_origin,int partW,int partH);



    /** Load image from file and resize if necessary.
     * @param path path to image file in file system
     * @param max_size max allowed value for width or height. Use zero or negative value to load image in original size
     * @param method resize method used in case resizing is required
     * @param rotation not supported yet
     * @param out_width_height output integer array which will contain width and height of loaded image. Array must be allocated before function is called and contain 2 elements
     * 
     * @return ByteBuffer which contain pixel data in 8888 format. Be aware about memory leaks: buffer allocated in native memory must be released by freeNativeBuffer function.
     */
    static public ByteBuffer load(String path, int max_size, int method, int rotation, int[] out_width_height) {
    	return load(path, max_size, method, rotation, out_width_height, true);
    }
    
    /** Load image from file, resize and premultiply if necessary.
     * @param path path to image file in file system
     * @param max_size max allowed value for width or height. Use zero or negative value to load image in original size
     * @param method resize method used in case resizing is required
     * @param rotation not supported yet
     * @param out_width_height output integer array which will contain width and height of loaded image. Array must be allocated before function is called and contain 2 elements
     * @param premultiply flag
     * 
     * @return ByteBuffer which contain pixel data in 8888 format. Be aware about memory leaks: buffer allocated in native memory must be released by freeNativeBuffer function.
     */
    static public native ByteBuffer load(String path, int max_size, int method, int rotation, int[] out_width_height, boolean premultiply);

    /** Load image from file as single-channel image and resize if necessary.
     * @param path path to image file in file system
     * @param max_size max allowed value for width or height. Use zero or negative value to load image in original size
     * @param method resize method used in case resizing is required
     * @param rotation not supported yet
     * @param out_width_height output integer array which will contain width and height of loaded image. Array must be allocated before function is called and contain 2 elements
     * 
     * @return ByteBuffer which contain pixel data in 8-bit format. Be aware about memory leaks: buffer allocated in native memory must be released by freeNativeBuffer function.
     */
    static public native ByteBuffer loadGray(String path, int max_size, int method, int rotation, int[] out_width_height);

    /** Resize buffer 8888. */
    static public native void resize(ByteBuffer src, int src_width, int src_height, ByteBuffer dst, int dst_width, int dst_height, int method);

    public final static String LIB_NAME = "imageop";
    
    public static boolean isLibLoaded = false;
    
    // Loading library.
	static {
		try {
			System.loadLibrary(LIB_NAME);
			initInterruptFlags();
			isLibLoaded = true;
		} catch (UnsatisfiedLinkError e) {
			try {
				System.load("/data/data/" + System.getProperty("packageName") + "/lib/" + LIB_NAME + ".so");
				initInterruptFlags();
				isLibLoaded = true;
			} catch (UnsatisfiedLinkError e1) {
				Log.e(ImageOpCommon.class.getSimpleName(), "Can't load \"imageop\" library.");
				Log.e(ImageOpCommon.class.getSimpleName(), e1.getMessage());
			}

		}
		//Log.e("ex1", "ImageOp library loaded = "+isLibLoaded);
	}
	
	public static void initialize(){
		try {
			if(isLibLoaded){
				initInterruptFlags();
			}
		} catch (UnsatisfiedLinkError e) {
		}
	}
}

