package com.gifsart.studio.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gifsart.studio.R;
import com.gifsart.studio.effects.GPUEffects;

import java.util.ArrayList;

/**
 * Created by Tigran on 10/7/15.
 */
public class EffectsAdapter extends RecyclerView.Adapter<EffectsAdapter.ViewHolder> {

    private GPUEffects.FilterList filters;
    private ArrayList<Bitmap> imagePath = new ArrayList<>();
    private LayoutInflater inflater = null;
    private Context context;

    public EffectsAdapter(GPUEffects.FilterList filters, Context context) {
        this.context = context;
        this.filters = filters;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (inflater == null) {
            inflater = LayoutInflater.from(parent.getContext());
        }
        return new ViewHolder(inflater.inflate(R.layout.effects_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.effectImageView.setImageBitmap(imagePath.get(position));
        holder.textView.setText(filters.names.get(position));

    }

    public void addItem(Bitmap path) {
        imagePath.add(path);
        notifyDataSetChanged();
    }

    public void addAll(ArrayList<Bitmap> bitmaps) {
        imagePath.addAll(bitmaps);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return imagePath.size();
    }

    public Bitmap getItem(int position) {
        return imagePath.get(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView effectImageView;
        public TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);

            effectImageView = (ImageView) itemView.findViewById(R.id.effect_image_view);

            textView = (TextView) itemView.findViewById(R.id.effect_text_view);
        }
    }

}
