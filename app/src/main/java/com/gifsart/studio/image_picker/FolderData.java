package com.gifsart.studio.image_picker;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by AramNazaryan on 9/15/15.
 */
public class FolderData implements Parcelable {
	public String folderName;
	public String folderPath;
	public String folderIconPath;
	public ArrayList<ImageData> imagePaths = new ArrayList<>();
	public boolean isSocial = false;
	public int iconDrawableId = 0;
	public boolean isSelected = false;
//	public int photosCount = 0;


	public FolderData(){

	}

	protected FolderData(Parcel in) {
		folderName = in.readString();
		folderPath = in.readString();
		folderIconPath = in.readString();
		isSocial = in.readInt() == 1;
		iconDrawableId = in.readInt();
		isSelected = in.readInt() == 1;
		in.readTypedList(imagePaths, ImageData.CREATOR);

	}

	public static final Creator<FolderData> CREATOR = new Creator<FolderData>() {
		@Override
		public FolderData createFromParcel(Parcel in) {
			return new FolderData(in);
		}

		@Override
		public FolderData[] newArray(int size) {
			return new FolderData[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(folderName);
		dest.writeString(folderPath);
		dest.writeString(folderIconPath);
		dest.writeInt(isSocial ? 1 : 0);
		dest.writeInt(iconDrawableId);
		dest.writeInt(isSelected ? 1 : 0);
		dest.writeTypedList(imagePaths);
	}
}