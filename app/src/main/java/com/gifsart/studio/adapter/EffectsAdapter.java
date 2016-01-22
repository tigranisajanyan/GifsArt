package com.gifsart.studio.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gifsart.studio.R;
import com.gifsart.studio.effects.GPUEffects;

import java.util.ArrayList;

/**
 * Created by Tigran on 10/7/15.
 */
public class EffectsAdapter extends RecyclerView.Adapter<EffectsAdapter.ViewHolder> {

    private ArrayList<Bitmap> imageBitmaps = new ArrayList<>();
    private LayoutInflater inflater = null;
    private Context context;

    public EffectsAdapter(Context context) {
        this.context = context;
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
        holder.effectImageView.setImageBitmap(imageBitmaps.get(position));
        holder.textView.setText(GPUEffects.FilterType.fromInt(position).name());

    }

    public void addItem(Bitmap bitmap) {
        imageBitmaps.add(bitmap);
        notifyDataSetChanged();
    }

    public void addAll(ArrayList<Bitmap> bitmaps) {
        imageBitmaps.addAll(bitmaps);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return imageBitmaps.size();
    }

    public Bitmap getItem(int position) {
        return imageBitmaps.get(position);
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
