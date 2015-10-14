package com.gifsart.studio.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.gifsart.studio.R;

import java.util.ArrayList;


/**
 * Created by Tigran on 10/14/15.
 */
public class StickerAdapter extends RecyclerView.Adapter<StickerAdapter.ViewHolder> {

    private ArrayList<Integer> resourceIds = new ArrayList<>();
    private LayoutInflater inflater = null;
    private Context context;

    public StickerAdapter(ArrayList<Integer> resourceIds, Context context) {
        this.resourceIds = resourceIds;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (inflater == null) {
            inflater = LayoutInflater.from(parent.getContext());
        }
        return new ViewHolder(inflater.inflate(R.layout.sticker_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.stickerImageView.setImageResource(resourceIds.get(position));

    }

    @Override
    public int getItemCount() {
        return resourceIds.size();
    }

    public int getItem(int position) {
        return resourceIds.get(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView stickerImageView;

        public ViewHolder(View itemView) {
            super(itemView);

            stickerImageView = (ImageView) itemView.findViewById(R.id.stiker_image_view);
            stickerImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }
    }

}