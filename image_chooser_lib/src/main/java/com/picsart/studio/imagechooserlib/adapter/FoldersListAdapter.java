package com.picsart.studio.imagechooserlib.adapter;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.picsart.studio.imagechooserlib.R;
import com.picsart.studio.imagechooserlib.items.FolderData;

import java.util.ArrayList;

/**
 * Created by AramNazaryan on 9/15/15.
 */
public class FoldersListAdapter extends RecyclerView.Adapter<FoldersListAdapter.ViewHolder>{

	private ArrayList<FolderData> items = new ArrayList<>();
	private LayoutInflater inflater = null;
	private ImageLoader imageLoader = null;


	public FoldersListAdapter() {
		imageLoader = ImageLoader.getInstance();
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		if (inflater == null) {
			inflater = LayoutInflater.from(parent.getContext());
		}
		View itemView = inflater.inflate(R.layout.folders_list_item_layout, parent, false);

		return new ViewHolder(itemView);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		FolderData folderData = items.get(position);
		holder.folderName.setText(folderData.folderName);
		holder.folderDescription.setText(folderData.folderDescription);
		imageLoader.displayImage("file://"+folderData.folderIconPath, holder.folderIcon);
	}

	@Override
	public int getItemCount() {
		return items.size();
	}

	public void add(FolderData item) {
		items.add(item);
	}


	public FolderData getItem(int position) {
		return items.get(position);
	}

	class ViewHolder extends RecyclerView.ViewHolder{

		public ImageView folderIcon;
		public TextView folderName;
		public TextView folderDescription;

		public ViewHolder(View itemView) {
			super(itemView);
			folderDescription = (TextView) itemView.findViewById(R.id.folder_description);
			folderName = (TextView) itemView.findViewById(R.id.folder_name);
			folderIcon = (ImageView) itemView.findViewById(R.id.folder_preview_imageview);
		}
	}
}
