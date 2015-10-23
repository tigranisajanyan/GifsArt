package com.gifsart.studio.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.gifsart.studio.R;
import com.gifsart.studio.gifutils.Giphy;
import com.gifsart.studio.item.GiphyItem;
import com.gifsart.studio.utils.GifsArtConst;
import com.gifsart.studio.utils.Utils;

import java.util.ArrayList;


/**
 * Created by Tigran on 10/14/15.
 */
public class StickerAdapter extends RecyclerView.Adapter<StickerAdapter.ViewHolder> {

    private ArrayList<GiphyItem> giphyItems = new ArrayList<>();
    private ArrayList<Integer> resourceItems = new ArrayList<>();
    private LayoutInflater inflater = null;
    private Context context;
    private boolean fromGiphy = false;
    private int offset = 0;

    public StickerAdapter(boolean fromGiphy, Context context) {
        this.fromGiphy = fromGiphy;
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
        if (!fromGiphy) {
            holder.stickerImageView.setImageResource(resourceItems.get(position));
        } else {

            if (position + 1 == GifsArtConst.GIPHY_LIMIT_COUNT + offset) {
                if (Utils.haveNetworkConnection(context)) {
                    offset = offset + GifsArtConst.GIPHY_LIMIT_COUNT;

                    Giphy giphy = new Giphy(context, GifsArtConst.GIPHY_TAG, true, offset, GifsArtConst.GIPHY_LIMIT_COUNT);
                    giphy.requestGiphy();
                    giphy.setOnDownloadedListener(new Giphy.GiphyListener() {
                        @Override
                        public void onGiphyDownloadFinished(ArrayList<GiphyItem> items) {
                            giphyItems.addAll(items);
                            notifyDataSetChanged();
                        }
                    });
                } else {
                    Toast.makeText(context, "No Wifi Connection", Toast.LENGTH_SHORT).show();
                }
            }

            Uri uri = Uri.parse((giphyItems.get(position)).getGifUrl());
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setUri(uri)
                    .setAutoPlayAnimations(true).build();
            holder.stickerImageView.setController(controller);
        }
    }

    @Override
    public int getItemCount() {
        if (fromGiphy) {
            return giphyItems.size();
        } else {
            return resourceItems.size();
        }
    }

    public void addGiphyItems(ArrayList<GiphyItem> giphyItems) {
        fromGiphy = true;
        this.giphyItems.addAll(giphyItems);
        notifyDataSetChanged();
    }

    public void addResourceItems(ArrayList<Integer> resourceItems) {
        fromGiphy = false;
        this.resourceItems.addAll(resourceItems);
        notifyDataSetChanged();
    }

    public void clearGiphyItems() {
        this.giphyItems.clear();
        notifyDataSetChanged();
    }

    public void clearResourceItems() {
        this.resourceItems.clear();
        notifyDataSetChanged();
    }

    public Object getItem(int position) {
        if (fromGiphy) {
            return giphyItems.get(position);
        } else {
            return resourceItems.get(position);
        }

    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public SimpleDraweeView stickerImageView;

        public ViewHolder(View itemView) {
            super(itemView);

            stickerImageView = (SimpleDraweeView) itemView.findViewById(R.id.stiker_image_view);
        }
    }

}