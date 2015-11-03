package com.gifsart.studio.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.gifsart.studio.R;
import com.gifsart.studio.activity.GiphyActivity;
import com.gifsart.studio.activity.ShootingGifActivity;
import com.gifsart.studio.item.GalleryItem;
import com.gifsart.studio.utils.CheckSpaceSingleton;
import com.gifsart.studio.utils.GifsArtConst;
import com.gifsart.studio.utils.Type;
import com.gifsart.studio.utils.Utils;

import java.util.ArrayList;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    private ArrayList<GalleryItem> array;
    private Activity activity;

    private ArrayList<GalleryItem> selected = new ArrayList<>();
    private int imageSize;

    public GalleryAdapter(ArrayList<GalleryItem> arr, Activity activity, int imageSize) {
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
            holder.mainFrameImageView.setScaleType(ImageView.ScaleType.CENTER);
            holder.mainFrameImageView.setImageBitmap(array.get(0).getBitmap());
            holder.mainFrameImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, ShootingGifActivity.class);
                    if (activity.getSharedPreferences(GifsArtConst.SHARED_PREFERENCES, Context.MODE_PRIVATE).getBoolean(GifsArtConst.SHARED_PREFERENCES_IS_OPENED, false)) {
                        activity.startActivityForResult(intent, GifsArtConst.REQUEST_CODE_SHOOTING_GIF_REOPENED);
                    } else {
                        activity.startActivity(intent);
                    }
                }
            });
        } else if (position == 1) {
            activity.setResult(Activity.RESULT_OK);
            Glide.clear(holder.mainFrameImageView);
            array.get(position).setIsSeleted(false);
            holder.fileTypeImageView.setImageBitmap(null);
            holder.textView.setVisibility(View.GONE);
            holder.mainFrameImageView.setScaleType(ImageView.ScaleType.CENTER);
            holder.mainFrameImageView.setImageBitmap(array.get(1).getBitmap());
            holder.mainFrameImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deselectAll();
                    Intent intent = new Intent(activity, GiphyActivity.class);
                    if (activity.getSharedPreferences(GifsArtConst.SHARED_PREFERENCES, Context.MODE_PRIVATE).getBoolean(GifsArtConst.SHARED_PREFERENCES_IS_OPENED, false)) {
                        activity.startActivityForResult(intent, GifsArtConst.REQUEST_CODE_GIPHY_REOPENED);
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
                        CheckSpaceSingleton.getInstance().removeAllocatedSpace(array.get(position).getFilePath());
                        array.get(position).setIsSeleted(false);
                        updateSelecetedItems(position);
                        selected.remove(array.get(position));
                        ((TextView) activity.findViewById(R.id.maic_activity_toolbar_selected_text)).setText(getSelected().size() + " Selected");
                        if (getSelected().size() < 1) {
                            ((TextView) activity.findViewById(R.id.maic_activity_toolbar_selected_text)).setText("");
                            ((TextView) activity.findViewById(R.id.main_activity_toolbar_cancel)).setText("Cancel");
                            ((Button) activity.findViewById(R.id.main_activity_toolbar_done)).setTextColor(activity.getResources().getColor(R.color.font_main_color));
                        }

                    } else {
                        if (CheckSpaceSingleton.getInstance().haveEnoughSpace(array.get(position).getFilePath())) {
                            CheckSpaceSingleton.getInstance().addAllocatedSpace(array.get(position).getFilePath());
                            array.get(position).setIsSeleted(true);
                            selected.add(array.get(position));
                            ((TextView) activity.findViewById(R.id.maic_activity_toolbar_selected_text)).setText(getSelected().size() + " Selected");
                            notifyItemChanged(position);
                        } else {
                            Toast.makeText(activity, "No Enough Space", Toast.LENGTH_SHORT).show();
                        }
                    }

                    holder.textView.setSelected(array
                            .get(position).isSeleted());
                }
            });

            ((Button) activity.findViewById(R.id.main_activity_toolbar_done)).setTextColor(((selected.size() > 0) ? activity.getResources().getColor(R.color.pink) : activity.getResources().getColor(R.color.font_main_color)));
            ((Button) activity.findViewById(R.id.main_activity_toolbar_cancel)).setText(((selected.size() > 0) ? "Deselect" : "Cancel"));

            try {
                Glide.with(activity).load(array.get(position).getFilePath()).asBitmap().centerCrop().into(holder.mainFrameImageView);
                holder.textView
                        .setSelected(array.get(position).isSeleted());

            } catch (Exception e) {
                e.printStackTrace();
            }
            if (Utils.getMimeType(array.get(position).getFilePath()) != null && Utils.getMimeType(array.get(position).getFilePath()) == Type.GIF) {
                holder.fileTypeImageView.setImageDrawable(activity.getResources().getDrawable(R.drawable.gif_icon));
            } else if (Utils.getMimeType(array.get(position).getFilePath()) != null && Utils.getMimeType(array.get(position).getFilePath()) == Type.VIDEO) {
                Glide.with(activity).load(array.get(position).getFilePath()).into(holder.mainFrameImageView);
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
        private TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);

            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(imageSize, imageSize);

            viewGroup = (ViewGroup) itemView.findViewById(R.id.container);
            viewGroup.setLayoutParams(layoutParams);

            mainFrameImageView = (ImageView) itemView.findViewById(R.id.gallery_image_item);
            fileTypeImageView = (ImageView) itemView.findViewById(R.id.file_type_image_view);

            textView = (TextView) itemView.findViewById(R.id.txt);
            textView.setVisibility(View.GONE);

        }
    }

    public GalleryItem getItem(int i) {
        return array.get(i);
    }

    public void setArray(ArrayList<GalleryItem> array) {
        this.array = array;
        notifyDataSetChanged();
    }

    public ArrayList<String> getSelected() {
        ArrayList<String> arrayList = new ArrayList<>();
        for (int i = 0; i < selected.size(); i++) {
            if (selected.get(i).isSeleted()) {
                arrayList.add(selected.get(i).getFilePath());
            }
        }
        return arrayList;
    }

    public void deselectAll() {
        if (selected.size() > 0) {
            for (int i = 0; i < array.size(); i++) {
                if (array.get(i).isSeleted() == true) {
                    array.get(i).setIsSeleted(false);
                }
            }
            selected.clear();
            ((TextView) activity.findViewById(R.id.maic_activity_toolbar_selected_text)).setText("");
            ((TextView) activity.findViewById(R.id.main_activity_toolbar_cancel)).setText("Cancel");
            notifyDataSetChanged();
        }
    }

    public void updateSelecetedItems(int deselectedItemPos) {
        for (int i = selected.indexOf(array.get(deselectedItemPos)); i < selected.size(); i++) {
            notifyItemChanged(array.indexOf(selected.get(i)));
        }
    }

}
