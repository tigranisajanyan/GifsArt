package com.gifsart.studio.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.gifsart.studio.R;
import com.gifsart.studio.helper.ItemTouchHelperAdapter;
import com.gifsart.studio.item.GalleryItem;
import com.gifsart.studio.item.MakeGifItem;
import com.gifsart.studio.utils.Type;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Tigran on 6/23/15.
 */
public class SlideAdapter extends RecyclerView.Adapter<SlideAdapter.ViewHolder> implements ItemTouchHelperAdapter {

    private ArrayList<MakeGifItem> array;
    private Context context;

    public SlideAdapter(ArrayList<MakeGifItem> arr, Context c) {

        array = arr;
        context = c;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        if (array.get(position).getType() == Type.IMAGE) {

            try {
                holder.icon.setImageBitmap(array.get(position).getBitmap());

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (array.get(position).getType() == Type.GIF) {
            try {
                holder.icon.setImageBitmap(array.get(position).getBitmap());

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (array.get(position).getType() == Type.VIDEO) {
            try {
                holder.icon.setImageBitmap(array.get(position).getBitmap());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        return array.size();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(array, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {

        array.remove(position);
        //animatrRemoveImpl(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, array.size());
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView icon;
        private ImageView selected;

        public ViewHolder(View itemView) {
            super(itemView);

            icon = (ImageView) itemView.findViewById(R.id.image_item);
            selected = (ImageView) itemView.findViewById(R.id.item_selected);
            selected.setVisibility(View.VISIBLE);
        }
    }

    public MakeGifItem getItem(int i) {
        return array.get(i);
    }


}
