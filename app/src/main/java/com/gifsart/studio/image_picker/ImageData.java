package com.gifsart.studio.image_picker;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.io.File;

/**
 * Created by AramNazaryan on 9/2/15.
 */
public class ImageData implements Parcelable{
	private String imagePath;
	private String thumbnailPath;
	private int orientation;
	private String folderPath;
	private boolean selected = false;

	public ImageData(){}
	protected ImageData(Parcel in) {
		imagePath = in.readString();
		thumbnailPath = in.readString();
		orientation = in.readInt();
		folderPath = in.readString();
		selected = in.readInt() == 1;
	}

	public static final Creator<ImageData> CREATOR = new Parcelable.Creator<ImageData>() {
		@Override
		public ImageData createFromParcel(Parcel in) {
			return new ImageData(in);
		}

		@Override
		public ImageData[] newArray(int size) {
			return new ImageData[size];
		}
	};

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

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(imagePath);
		dest.writeString(thumbnailPath);
		dest.writeInt(orientation);
		dest.writeString(folderPath);
		dest.writeInt(selected ? 1 : 0);
	}
}