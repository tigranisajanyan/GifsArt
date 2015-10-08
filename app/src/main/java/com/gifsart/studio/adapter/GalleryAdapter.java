package com.gifsart.studio.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.gifsart.studio.R;
import com.gifsart.studio.activity.GiphyActivity;
import com.gifsart.studio.activity.ShootingGifActivity;
import com.gifsart.studio.item.GalleryItem;
import com.gifsart.studio.utils.GifsArtConst;
import com.gifsart.studio.utils.Type;
import com.gifsart.studio.utils.Utils;

import java.util.ArrayList;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    public static final String FILE_PREFIX = "file://";
    private ArrayList<GalleryItem> array;
    private Activity activity;
    private ActionBar actionBar;
    int count = 0;

    private ArrayList<GalleryItem> selected = new ArrayList<>();
    private int imageSize;

    public GalleryAdapter(ArrayList<GalleryItem> arr, Activity activity, int imageSize, ActionBar actionBar) {

        this.actionBar = actionBar;
        this.array = arr;
        this.activity = activity;
        this.imageSize = imageSize;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(activity).inflate(R.layout.gallery_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        if (position == 0) {
            Glide.clear(holder.mainFrameImageView);
            array.get(position).setIsSeleted(false);
            holder.fileTypeImageView.setImageBitmap(null);
            holder.textView.setVisibility(View.GONE);
            //holder.select.setVisibility(View.GONE);
            holder.mainFrameImageView.setScaleType(ImageView.ScaleType.CENTER);
            holder.mainFrameImageView.setImageBitmap(array.get(0).getBitmap());
            holder.mainFrameImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, ShootingGifActivity.class);
                    activity.startActivity(intent);
                    if (activity.getSharedPreferences(GifsArtConst.SHARED_PREFERENCES, Context.MODE_PRIVATE).getBoolean("is_op", false)) {
                        activity.finish();
                    }
                }
            });
        } else if (position == 1) {
            activity.setResult(Activity.RESULT_OK);
            Glide.clear(holder.mainFrameImageView);
            array.get(position).setIsSeleted(false);
            holder.fileTypeImageView.setImageBitmap(null);
            holder.textView.setVisibility(View.GONE);
            //holder.select.setVisibility(View.GONE);
            holder.mainFrameImageView.setScaleType(ImageView.ScaleType.CENTER);
            holder.mainFrameImageView.setImageBitmap(array.get(1).getBitmap());
            holder.mainFrameImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, GiphyActivity.class);
                    if (activity.getSharedPreferences(GifsArtConst.SHARED_PREFERENCES, Context.MODE_PRIVATE).getBoolean("is_op", false)) {
                        activity.startActivityForResult(intent, 111);
                    } else {
                        activity.startActivity(intent);
                    }
                }
            });


        } else {
            holder.mainFrameImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (array.get(position).isSeleted()) {
                        array.get(position).setIsSeleted(false);
                        updateSelecetedItems(position);
                        selected.remove(array.get(position));
                        actionBar.setTitle(getSelected().size() + " Selected");
                        if (getSelected().size() < 1) {
                            actionBar.setTitle("GifTest");
                        }

                    } else {
                        array.get(position).setIsSeleted(true);
                        selected.add(array.get(position));
                        actionBar.setTitle(getSelected().size() + " Selected");
                        notifyItemChanged(position);
                    }

                    holder.select.setSelected(array
                            .get(position).isSeleted());
                }
            });

            try {
                Glide.with(activity).load(array.get(position).getImagePath()).asBitmap().centerCrop().into(holder.mainFrameImageView);
                holder.select
                        .setSelected(array.get(position).isSeleted());

            } catch (Exception e) {
                e.printStackTrace();
            }
            if (Utils.getMimeType(array.get(position).getImagePath()) != null && Utils.getMimeType(array.get(position).getImagePath()) == Type.GIF) {
                holder.fileTypeImageView.setImageDrawable(activity.getResources().getDrawable(R.drawable.gif_icon));
            } else if (Utils.getMimeType(array.get(position).getImagePath()) != null && Utils.getMimeType(array.get(position).getImagePath()) == Type.VIDEO) {
                Glide.with(activity).load(array.get(position).getImagePath()).into(holder.mainFrameImageView);
                holder.fileTypeImageView.setImageDrawable(activity.getResources().getDrawable(R.drawable.video_icon));
            } else {
                holder.fileTypeImageView.setImageBitmap(null);
            }

            if (array.get(position).isSeleted()) {
                holder.textView.setVisibility(View.VISIBLE);
                holder.textView.setText(selected.indexOf(array.get(position)) + 1 + "");
            } else {
                holder.textView.setVisibility(View.GONE);
            }

        }
    }

    @Override
    public int getItemCount() {
        return array.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ViewGroup viewGroup;
        private ImageView mainFrameImageView;
        private ImageView fileTypeImageView;
        private ImageView select;
        private TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);

            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(imageSize, imageSize);

            viewGroup = (ViewGroup) itemView.findViewById(R.id.container);
            viewGroup.setLayoutParams(layoutParams);

            mainFrameImageView = (ImageView) itemView.findViewById(R.id.gallery_image_item);

            fileTypeImageView = (ImageView) itemView.findViewById(R.id.file_type_image_view);

            select = (ImageView) itemView.findViewById(R.id.gallery_item_selected);
            select.setVisibility(View.VISIBLE);

            textView = (TextView) itemView.findViewById(R.id.txt);
            textView.setVisibility(View.GONE);

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

    public void deselectAll() {
        for (int i = 0; i < array.size(); i++) {
            if (array.get(i).isSeleted() == true) {
                array.get(i).setIsSeleted(false);
            }
        }
        selected.clear();
        actionBar.setTitle("GifTest");
        notifyDataSetChanged();
    }

    public void updateSelecetedItems(int deselectedItemPos) {
        for (int i = selected.indexOf(array.get(deselectedItemPos)); i < selected.size(); i++) {
            notifyItemChanged(array.indexOf(selected.get(i)));
        }
    }

}
