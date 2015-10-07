package com.gifsart.studio.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.gifsart.studio.R;
import com.gifsart.studio.activity.EffectsActivity;
import com.gifsart.studio.gifutils.Giphy;
import com.gifsart.studio.item.GiphyItem;
import com.gifsart.studio.textart.TextArtView;
import com.gifsart.studio.utils.Utils;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Tigran on 10/7/15.
 */
public class EffectsAdapter extends RecyclerView.Adapter<EffectsAdapter.ViewHolder> {

    private ArrayList<String> effectItemsArrayList = new ArrayList<>();
    private Bitmap bitmap;
    private LayoutInflater inflater = null;
    private Context context;

    public EffectsAdapter(Bitmap bitmap, ArrayList<String> effectItemsArrayList, Context context) {
        this.effectItemsArrayList = effectItemsArrayList;
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
        holder.effectImageView.setImageBitmap(bitmap);
    }


    @Override
    public int getItemCount() {
        return effectItemsArrayList.size();
    }

    public String getItem(int position) {
        return effectItemsArrayList.get(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView effectImageView;
        public TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(context.getResources().getDisplayMetrics().widthPixels / 2, context.getResources().getDisplayMetrics().widthPixels / 2);

            effectImageView = (ImageView) itemView.findViewById(R.id.effect_image_view);
            effectImageView.setLayoutParams(layoutParams);

            textView = (TextView) itemView.findViewById(R.id.effect_text_view);
        }
    }

}
