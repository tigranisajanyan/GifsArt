package com.gifsart.studio.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;


import com.gifsart.studio.R;
import com.gifsart.studio.activity.GiphyActivity;
import com.gifsart.studio.activity.ShootingGifActivity;
import com.gifsart.studio.item.GalleryItem;
import com.gifsart.studio.utils.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    public static final String FILE_PREFIX = "file://";
    private ArrayList<GalleryItem> array;
    private Context context;
    private ActionBar actionBar;

    private ArrayList<GalleryItem> selected = new ArrayList<>();
    private int imageSize;

    public GalleryAdapter(ArrayList<GalleryItem> arr, Context c, int imageSize, ActionBar actionBar) {

        this.actionBar = actionBar;
        this.array = arr;
        this.context = c;
        this.imageSize = imageSize;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.gallery_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        if (position != 0 && position != 1) {
            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (array.get(position).isSeleted()) {
                        array.get(position).setIsSeleted(false);
                        selected.remove(array.get(position));
                        actionBar.setTitle(getSelected().size() + " Selected");
                        if (getSelected().size() < 1) {
                            actionBar.setTitle("GifTest");
                        }

                    } else {
                        array.get(position).setIsSeleted(true);
                        selected.add(array.get(position));
                        actionBar.setTitle(getSelected().size() + " Selected");
                    }

                    holder.select.setSelected(array
                            .get(position).isSeleted());

                }
            });

            try {

                //ImageLoader.getInstance().cancelDisplayTask(holder.image);
                ImageLoader.getInstance().displayImage(FILE_PREFIX + array.get(position).getImagePath()
                        , holder.image, new SimpleImageLoadingListener() {

                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        holder.image.setImageBitmap(null);
                        super.onLoadingStarted(imageUri, view);
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

                        holder.image.setImageBitmap(loadedImage);
                        super.onLoadingComplete(imageUri, view, loadedImage);
                    }
                });

                holder.select
                        .setSelected(array.get(position).isSeleted());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (position == 0) {
            holder.image.setScaleType(ImageView.ScaleType.CENTER);
            holder.image.setImageBitmap(array.get(0).getBitmap());
            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(context, "" + position, Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(context, ShootingGifActivity.class);
                    context.startActivity(intent);
                }
            });
        }
        if (position == 1) {
            holder.image.setScaleType(ImageView.ScaleType.CENTER);

            holder.image.setImageBitmap(array.get(1).getBitmap());
            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(context, "" + position, Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(context, GiphyActivity.class);
                    context.startActivity(intent);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return array.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView image;
        private ImageView isVideo;
        private ImageView select;

        public ViewHolder(View itemView) {
            super(itemView);

            image = (ImageView) itemView.findViewById(R.id.gallery_image_item);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(imageSize, imageSize);
            image.setLayoutParams(layoutParams);
            isVideo = (ImageView) itemView.findViewById(R.id.is_video);
            select = (ImageView) itemView.findViewById(R.id.gallery_item_selected);
            select.setVisibility(View.VISIBLE);
            isVideo.setVisibility(View.GONE);
        }
    }

    public GalleryItem getItem(int i) {
        return array.get(i);
    }

    public ArrayList<String> getSelected() {
        ArrayList<String> arrayList = new ArrayList<>();
        for (int i = 0; i < selected.size(); i++) {
            if (selected.get(i).isSeleted()) {
                arrayList.add(selected.get(i).getImagePath());
            }
        }
        return arrayList;
    }

}
