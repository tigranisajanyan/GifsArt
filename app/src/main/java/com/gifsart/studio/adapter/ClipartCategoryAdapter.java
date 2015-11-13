package com.gifsart.studio.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.gifsart.studio.R;
import com.gifsart.studio.clipart.ClipartRes;

/**
 * Created by Tigran on 10/14/15.
 */
public class ClipartCategoryAdapter extends RecyclerView.Adapter<ClipartCategoryAdapter.ViewHolder> {

    private int[] resourceIds;
    private LayoutInflater inflater = null;
    private Context context;

    public ClipartCategoryAdapter(Context context) {
        this.resourceIds = ClipartRes.clipartCategoryIcons;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (inflater == null) {
            inflater = LayoutInflater.from(parent.getContext());
        }
        return new ViewHolder(inflater.inflate(R.layout.sticker_category_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.stickerImageView.setImageResource(resourceIds[position]);
    }

    @Override
    public int getItemCount() {
        return resourceIds.length;
    }

    public int getItem(int position) {
        return resourceIds[position];
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView stickerImageView;

        public ViewHolder(View itemView) {
            super(itemView);

            stickerImageView = (ImageView) itemView.findViewById(R.id.stiker_category_image_view);
            stickerImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }
    }

}
