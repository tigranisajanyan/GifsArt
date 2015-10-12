package com.gifsart.studio.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gifsart.studio.R;
import com.gifsart.studio.effects.GPUEffects;

import jp.co.cyberagent.android.gpuimage.GPUImageView;

/**
 * Created by Tigran on 10/7/15.
 */
public class EffectsAdapter extends RecyclerView.Adapter<EffectsAdapter.ViewHolder> {

    GPUEffects.FilterList filterList = new GPUEffects.FilterList();
    private Bitmap bitmap;
    private LayoutInflater inflater = null;
    private Context context;

    public EffectsAdapter(Bitmap bitmap, GPUEffects.FilterList filterList, Context context) {
        this.filterList = filterList;
        this.bitmap = bitmap;
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

        holder.effectImageView.setFilter(GPUEffects.createFilterForType(filterList.filters.get(position)));
        holder.effectImageView.requestRender();

        holder.textView.setText(filterList.names.get(position));

    }

    @Override
    public int getItemCount() {
        return filterList.names.size();
    }

    public GPUEffects.FilterType getItem(int position) {
        return filterList.filters.get(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public GPUImageView effectImageView;
        public TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);

            effectImageView = (GPUImageView) itemView.findViewById(R.id.effect_image_view);
            effectImageView.setImage(bitmap);

            textView = (TextView) itemView.findViewById(R.id.effect_text_view);
        }
    }

}
