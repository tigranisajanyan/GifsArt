package com.picsart.studio.imagechooserlib.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.picsart.studio.imagechooserlib.R;
import com.picsart.studio.imagechooserlib.items.ImageData;

import java.util.ArrayList;

/**
 * Created by AramNazaryan on 9/2/15.
 */
public class ImagesGridAdapter extends RecyclerView.Adapter<ImagesGridAdapter.ViewHolder> {

	private ArrayList<ImageData> items = new ArrayList<>();
	private ImageLoader imageLoader = null;
	private int imageSize = 0;
	private LayoutInflater inflater = null;
	private DisplayImageOptions displayImageOptions = null;

	public ImagesGridAdapter(float imageSize ) {
		imageLoader = ImageLoader.getInstance();
		FadeInBitmapDisplayer displayer = new FadeInBitmapDisplayer(0, true, true, false);
		displayImageOptions = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).displayer(displayer).build();
		this.imageSize = Math.round(imageSize);
	}
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
		if (inflater == null) {
			inflater = LayoutInflater.from(viewGroup.getContext());
		}
		View view = inflater.inflate(R.layout.image_item_layout, viewGroup, false);
//		ImageView view = new ImageView(viewGroup.getContext());
		view.setLayoutParams(new ViewGroup.LayoutParams(imageSize, imageSize));
//		view.setScaleType(ImageView.ScaleType.CENTER_CROP);
		ViewHolder viewHolder = new ViewHolder(view);
		return viewHolder;
	}

	@Override
	public int getItemCount() {
		return items.size();
	}

	@Override
	public void onBindViewHolder(final ViewHolder viewHolder, final int i) {

		viewHolder.imageView.post(new Runnable() {
			@Override
			public void run() {
				imageLoader.cancelDisplayTask(viewHolder.imageView);
				viewHolder.imageView.setImageBitmap(null);
				imageLoader.displayImage("file://" + items.get(i).getImagePath(), viewHolder.imageView, displayImageOptions);
				viewHolder.frameLayout.setForeground(items.get(i).isSelected() ? viewHolder.frameLayout.getContext().getResources().getDrawable(R.drawable.abc_btn_check_to_on_mtrl_000) : null);
			}
		});
	}



	class ViewHolder extends RecyclerView.ViewHolder {
		ImageView imageView = null;
		FrameLayout frameLayout = null;
		public ViewHolder(View itemView) {
			super(itemView);
			this.imageView = (ImageView) itemView.findViewById(R.id.image_grid_imageview);
			this.frameLayout = (FrameLayout) itemView;
		}
	}

	public void addItem(ImageData item) {
		items.add(item);
	}

	public void clear() {
		items.clear();
	}

	public ImageData getItem(int position) {
		return items.get(position);
	}

	public ArrayList<ImageData> getItems() {
		return items;
	}
}
