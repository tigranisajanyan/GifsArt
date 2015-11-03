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
import com.gifsart.studio.item.ShareGifItem;

import java.util.ArrayList;

/**
 * Created by Tigran on 10/30/15.
 */
public class ShareGifAdapter extends RecyclerView.Adapter<ShareGifAdapter.ViewHolder> {

    private LayoutInflater inflater = null;
    private Context context;
    private ArrayList<ShareGifItem> shareGifItems = new ArrayList<>();

    public ShareGifAdapter(ArrayList<ShareGifItem> shareGifItems, Context context) {
        this.context = context;
        this.shareGifItems = shareGifItems;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (inflater == null) {
            inflater = LayoutInflater.from(parent.getContext());
        }
        return new ViewHolder(inflater.inflate(R.layout.share_gif_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Glide.with(context).load(shareGifItems.get(position).getResourceId()).fitCenter().into(holder.shareGifItemCover);
        holder.shareGifItemTitle.setText(shareGifItems.get(position).getItemTitle());
    }

    @Override
    public int getItemCount() {
        return shareGifItems.size();
    }


    public ShareGifItem getItem(int position) {
        return shareGifItems.get(position);

    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView shareGifItemCover;
        public TextView shareGifItemTitle;

        public ViewHolder(View itemView) {
            super(itemView);

            shareGifItemCover = (ImageView) itemView.findViewById(R.id.share_gif_item_image);
            shareGifItemTitle = (TextView) itemView.findViewById(R.id.sharegif_item_title);
        }
    }

}
