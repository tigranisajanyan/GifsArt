package com.gifsart.studio.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gifsart.studio.R;
import com.gifsart.studio.utils.MaskRes;

/**
 * Created by Tigran on 10/22/15.
 */
public class MasksAdapter extends RecyclerView.Adapter<MasksAdapter.ViewHolder> {

    private int[] maskResourceIds;
    private LayoutInflater inflater = null;
    private Context context;

    public MasksAdapter(Context context) {
        this.context = context;
        this.maskResourceIds = MaskRes.maskResourceIds;
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

        Glide.with(context).asBitmap().load(maskResourceIds[position]).into(holder.maskImageView);

    }

    public void addItem(int resourceId) {
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return maskResourceIds.length;
    }

    public int getItem(int position) {
        return maskResourceIds[position];
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView maskImageView;
        public TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);

            maskImageView = (ImageView) itemView.findViewById(R.id.effect_image_view);

            textView = (TextView) itemView.findViewById(R.id.effect_text_view);
            textView.setVisibility(View.INVISIBLE);
        }
    }

}
