package com.gifsart.studio.image_picker;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;


import com.gifsart.studio.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by AramNazaryan on 9/2/15.
 */
public class PhoneImagesRetriever {
    private LoaderManager loaderManager = null;
    private Context context = null;
    private static PhoneImagesRetriever thisInstance;
    private ArrayList<ImageData> images = null;

    private PhoneImagesRetriever(LoaderManager loaderManager, Context context) {
        this.loaderManager = loaderManager;
        this.context = context;
    }


    public static PhoneImagesRetriever getInstance(LoaderManager loaderManager, Context context) {
        if (thisInstance == null) {
            thisInstance = new PhoneImagesRetriever(loaderManager, context);
        }
        thisInstance.loaderManager = loaderManager;
        thisInstance.context = context;
        return thisInstance;
    }

    public void retrieveImages(boolean forceUpdate, final OnImagesRetrievedListener listener) {
        if (images != null && !forceUpdate) {
            listener.onImagesRetrieved((ArrayList<ImageData>) images.clone());
            return;
        }

        loaderManager.initLoader(0, null, new LoaderManager.LoaderCallbacks<Cursor>() {

            @Override
            public Loader<Cursor> onCreateLoader(int loaderID, Bundle arg1) {
                return createLoader(loaderID, arg1);
            }

            @Override
            public void onLoadFinished(Loader<Cursor> arg0, Cursor imagecursor) {
                loadFinished(imagecursor, listener);
            }

            @Override
            public void onLoaderReset(Loader<Cursor> arg0) {
                // do nothing
            }
        });
    }

    private CursorLoader createLoader(int loaderID, Bundle arg1) {
        if (loaderID == 0) {
            // Returns a new CursorLoader
            final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID, MediaStore.Images.Thumbnails.DATA, MediaStore.Images.Media.ORIENTATION};
            final String orderBy = MediaStore.Images.Media.DATE_MODIFIED + " DESC";
            return new CursorLoader(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy);
        }
        return null;
    }

    private void loadFinished(Cursor imagecursor, OnImagesRetrievedListener listener) {
        if (imagecursor == null) {
            return;
        }
        int count = imagecursor.getCount();
        images = new ArrayList();
        for (int i = 0; i < count; i++) {
            imagecursor.moveToPosition(i);
            int orientationId = imagecursor.getColumnIndex(MediaStore.Images.Media.ORIENTATION);
            int dataColumnIndex = imagecursor.getColumnIndex(MediaStore.Images.Media.DATA);
            int thumbnailId = imagecursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA);
            String path = imagecursor.getString(dataColumnIndex);
            if (path == null || !path.contains("/")) {
                continue;
            }
            ImageData imageData = new ImageData();
            imageData.setImagePath(path);
            imageData.setOrientation(imagecursor.getInt(orientationId));
            imageData.setThumbnailPath(imagecursor.getString(thumbnailId));
            images.add(imageData);
        }


        if (listener != null) {
            listener.onImagesRetrieved(images);
        }
    }

    public static ArrayList<FolderData> getUniqueFolders(ArrayList<ImageData> imagesArray, Context context) {
        ArrayList<FolderData> uniqueFoldersList = new ArrayList<>();
        HashMap<String, FolderData> uniqueFoldersData = new HashMap<>();

        FolderData recentData = new FolderData();
        recentData.isSocial = false;
        recentData.folderName = "Recent";//context.getString(R.string.gen_recent);
        recentData.folderPath = ImagePickerConstants.FOLDER_RECENT;
        if (imagesArray != null) {
            recentData.imagePaths.addAll(imagesArray);
        }
        recentData.folderIconPath = (imagesArray != null && imagesArray.size() > 0) ? imagesArray.get(0).getThumbnailPath() : null;
        uniqueFoldersData.put(recentData.folderPath, recentData);
        uniqueFoldersList.add(recentData);
        if (imagesArray == null) {
            return uniqueFoldersList;
        }

        for (ImageData data : imagesArray) {
            if (!uniqueFoldersData.containsKey(data.getFolderPath())) {
                FolderData folderData = new FolderData();
                folderData.folderPath = data.getFolderPath();
                folderData.folderName = data.getFolderPath().substring(data.getFolderPath().lastIndexOf("/") + 1);
                folderData.folderIconPath = data.getThumbnailPath();
                folderData.imagePaths.add(data);
                uniqueFoldersList.add(folderData);
                uniqueFoldersData.put(folderData.folderPath, folderData);
            } else {
                FolderData folderData = uniqueFoldersData.get(data.getFolderPath());
                folderData.imagePaths.add(data);
            }
        }

        return uniqueFoldersList;
    }

    public ArrayList<ImageData> getImagesByFolderPath(String folderPath) {
        ArrayList<ImageData> result = new ArrayList<>();
        if (images == null) {
            return result;
        }
        if (ImagePickerConstants.FOLDER_RECENT.equals(folderPath)) {
            return (ArrayList<ImageData>) images.clone();
        }
        for (ImageData image : images) {
            if (folderPath.equals(image.getFolderPath())) {
                result.add(image);
            }
        }

        return result;
    }


}