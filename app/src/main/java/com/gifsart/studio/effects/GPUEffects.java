package com.gifsart.studio.effects;

import android.content.Context;

import java.util.LinkedList;
import java.util.List;

import jp.co.cyberagent.android.gpuimage.GPUImageAddBlendFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageBrightnessFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageColorInvertFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageContrastFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageEmbossFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageGammaFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageGrayscaleFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageHueFilter;
import jp.co.cyberagent.android.gpuimage.GPUImagePosterizeFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageScreenBlendFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSepiaFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSharpenFilter;

/**
 * Created by Tigran on 10/8/15.
 */
public class GPUEffects {


    public enum FilterType {
        NONE, CONTRAST, GRAYSCALE, SHARPEN, SEPIA, EMBOSS, POSTERIZE, GAMMA, BRIGHTNESS, INVERT, HUE, BLEND_1, BLEND_2;

        public static FilterType fromInt(int val) {
            FilterType[] codes = values();

            if (val < 0 || val >= codes.length) {
                return null;
            } else {
                return values()[val];
            }
        }

        public int toInt() {
            return ordinal();
        }
    }

    public static class FilterList {
        public List<String> names = new LinkedList();
        public List<FilterType> filters = new LinkedList();

        public void addFilter(final String name, final FilterType filter) {
            names.add(name);
            filters.add(filter);
        }
    }

    public static GPUImageFilter createFilterForType(Context context, final FilterType type) {
        switch (type) {
            case NONE:
                return new GPUImageFilter();
            case CONTRAST:
                return new GPUImageContrastFilter(2.0f);
            case GAMMA:
                return new GPUImageGammaFilter(2.0f);
            case INVERT:
                return new GPUImageColorInvertFilter();
            case HUE:
                return new GPUImageHueFilter(90.0f);
            case BRIGHTNESS:
                return new GPUImageBrightnessFilter(1.5f);
            case GRAYSCALE:
                return new GPUImageGrayscaleFilter();
            case SEPIA:
                return new GPUImageSepiaFilter();
            case SHARPEN:
                GPUImageSharpenFilter sharpness = new GPUImageSharpenFilter();
                sharpness.setSharpness(2.0f);
                return sharpness;
            case EMBOSS:
                return new GPUImageEmbossFilter();
            case POSTERIZE:
                return new GPUImagePosterizeFilter();
            case BLEND_1:
                return GPUImageFilterTools.createBlendFilter(context, GPUImageScreenBlendFilter.class);
            case BLEND_2:
                return GPUImageFilterTools.createBlendFilter(context, GPUImageAddBlendFilter.class);
            default:
                throw new IllegalStateException("No filter of that type!");
        }
    }

}
