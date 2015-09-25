package com.picsart.studio.imagechooserlib.items;

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
	public String folderDescription;
	public ArrayList<String> imagePaths = new ArrayList<>();


	public FolderData(){

	}

	protected FolderData(Parcel in) {
		folderName = in.readString();
		folderPath = in.readString();
		folderIconPath = in.readString();
		folderDescription = in.readString();
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
		dest.writeString(folderDescription);
	}
}
