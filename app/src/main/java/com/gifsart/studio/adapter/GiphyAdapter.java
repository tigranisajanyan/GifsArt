package com.gifsart.studio.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.gifsart.studio.R;
import com.gifsart.studio.item.GiphyItem;

import java.util.ArrayList;

/**
 * Created by Tigran on 9/8/15.
 */
public class GiphyAdapter extends RecyclerView.Adapter<GiphyAdapter.ViewHolder> {

    private ArrayList<GiphyItem> items = new ArrayList<>();
    private LayoutInflater inflater = null;
    private Context context;

    public GiphyAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (inflater == null) {
            inflater = LayoutInflater.from(parent.getContext());
        }
        return new ViewHolder(inflater.inflate(R.layout.giphy_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Uri uri = Uri.parse(items.get(position).getGifUrl());
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(uri)
                .setAutoPlayAnimations(true).build();
        holder.simpleDraweeView.setController(controller);

    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(GiphyItem item) {
        items.add(item);
    }

    public void clear() {
        items.clear();
    }

    public GiphyItem getItem(int position) {
        return items.get(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public SimpleDraweeView simpleDraweeView = null;

        public ViewHolder(View itemView) {
            super(itemView);
            simpleDraweeView = (SimpleDraweeView) itemView;
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(context.getResources().getDisplayMetrics().widthPixels / 2, context.getResources().getDisplayMetrics().widthPixels / 2);
            simpleDraweeView.setLayoutParams(layoutParams);
        }
    }

}
