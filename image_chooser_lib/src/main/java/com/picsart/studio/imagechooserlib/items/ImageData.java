package com.picsart.studio.imagechooserlib.items;

import android.text.TextUtils;

import java.io.File;

/**
 * Created by AramNazaryan on 9/2/15.
 */
public class ImageData {
	private String imagePath;
	private String thumbnailPath;
	private int orientation;
	private String folderPath;
	private boolean selected = false;

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
		this.folderPath = null;
		if (!TextUtils.isEmpty(imagePath)) {
			if (imagePath.contains(File.separator)) {
				setFolderPath(imagePath.substring(0, imagePath.lastIndexOf(File.separator)));
			}
		}
	}

	public String getThumbnailPath() {
		return thumbnailPath;
	}

	public void setThumbnailPath(String thumbnailPath) {
		this.thumbnailPath = thumbnailPath;
	}

	public int getOrientation() {
		return orientation;
	}

	public void setOrientation(int orientation) {
		this.orientation = orientation;
	}

	public String getFolderPath() {
		return folderPath;
	}

	public void setFolderPath(String folderPath) {
		this.folderPath = folderPath;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public boolean isSelected() {
		return selected;
	}
}
