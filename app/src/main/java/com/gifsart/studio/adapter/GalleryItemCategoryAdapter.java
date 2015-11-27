package com.gifsart.studio.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gifsart.studio.R;
import com.gifsart.studio.item.GalleryCategoryItem;

import java.util.ArrayList;

/**
 * Created by Tigran on 10/29/15.
 */
public class GalleryItemCategoryAdapter extends RecyclerView.Adapter<GalleryItemCategoryAdapter.ViewHolder> {

    private LayoutInflater inflater = null;
    private Context context;
    private ArrayList<GalleryCategoryItem> galleryCategoryItems = new ArrayList<>();

    public GalleryItemCategoryAdapter(ArrayList<GalleryCategoryItem> galleryCategoryItems, Context context) {
        this.context = context;
        this.galleryCategoryItems = galleryCategoryItems;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (inflater == null) {
            inflater = LayoutInflater.from(parent.getContext());
        }
        return new ViewHolder(inflater.inflate(R.layout.gallery_item_category, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (galleryCategoryItems.get(position).getCategoryCover() != "") {
            Glide.with(context).load(galleryCategoryItems.get(position).getCategoryCover()).asBitmap().fitCenter().into(holder.galleryCategoryItemImageView);
        } else if (galleryCategoryItems.get(position).getThumbnail() != null) {
            holder.galleryCategoryItemImageView.setImageBitmap(galleryCategoryItems.get(position).getThumbnail());
        }
        holder.galleryCategoryItemTitle.setText(galleryCategoryItems.get(position).getCategoryTitle());
        if (galleryCategoryItems.get(position).getCategoryItemsCount() > 0) {
            holder.galleryCategoryItemsCount.setVisibility(View.VISIBLE);
            holder.galleryCategoryItemsCount.setText("" + galleryCategoryItems.get(position).getCategoryItemsCount());
        }else {
            holder.galleryCategoryItemsCount.setVisibility(View.INVISIBLE);
        }
        if (galleryCategoryItems.get(position).isSelected()) {
            holder.galleryCategoryItemSelected.setVisibility(View.VISIBLE);
        } else {
            holder.galleryCategoryItemSelected.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return galleryCategoryItems.size();
    }


    public GalleryCategoryItem getItem(int position) {
        return galleryCategoryItems.get(position);

    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView galleryCategoryItemImageView;
        public TextView galleryCategoryItemTitle;
        public TextView galleryCategoryItemsCount;
        public ImageButton galleryCategoryItemSelected;

        public ViewHolder(View itemView) {
            super(itemView);

            galleryCategoryItemImageView = (ImageView) itemView.findViewById(R.id.gallery_item_category_image);
            galleryCategoryItemImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            galleryCategoryItemSelected = (ImageButton) itemView.findViewById(R.id.gallery_item_category_selected);
            galleryCategoryItemTitle = (TextView) itemView.findViewById(R.id.gallery_item_category_title);
            galleryCategoryItemsCount = (TextView) itemView.findViewById(R.id.gallery_item_category_items_count);
        }
    }

}
