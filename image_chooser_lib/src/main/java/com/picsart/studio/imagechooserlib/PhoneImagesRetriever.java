package com.picsart.studio.imagechooserlib;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;

import com.picsart.studio.imagechooserlib.items.FolderData;
import com.picsart.studio.imagechooserlib.items.ImageData;
import com.picsart.studio.imagechooserlib.listeners.OnImagesRetrievedListener;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by AramNazaryan on 9/2/15.
 */
public class PhoneImagesRetriever {
	private LoaderManager loaderManager = null;
	private Context context = null;

	public PhoneImagesRetriever(LoaderManager loaderManager, Context context){
		this.loaderManager = loaderManager;
		this.context = context;
	}


	public void retrieveImages(final OnImagesRetrievedListener listener) {
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
			final String orderBy = MediaStore.Images.Media.DATE_MODIFIED+" DESC";
			return new CursorLoader(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy);
		}
		return null;
	}

	private void loadFinished(Cursor imagecursor, OnImagesRetrievedListener listener) {
		if (imagecursor == null) {
			return;
		}
		int count = imagecursor.getCount();
		ArrayList<ImageData> result = new ArrayList();
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
			result.add(imageData);
		}

		if (listener != null) {
			listener.onImagesRetrieved(result);
		}
	}

	public static ArrayList<FolderData> getUniqueFolders(ArrayList<ImageData> imagesArray) {
		ArrayList<String> uniqueFolderPaths = new ArrayList<>();
		ArrayList<FolderData> uniqueFoldersData = new ArrayList<>();
		for(ImageData data : imagesArray) {
			System.out.println("aaaaa "+data.getImagePath()+" f = "+data.getFolderPath() );
			if (!uniqueFolderPaths.contains(data.getFolderPath())) {
				FolderData folderData = new FolderData();
				folderData.folderPath = data.getFolderPath();
				folderData.folderName = data.getFolderPath().substring(data.getFolderPath().lastIndexOf("/") + 1);
				folderData.folderDescription = "x images";
				folderData.folderIconPath = data.getThumbnailPath();
				uniqueFolderPaths.add(folderData.folderPath);
				uniqueFoldersData.add(folderData);
			}
		}

		return uniqueFoldersData;
	}


}
