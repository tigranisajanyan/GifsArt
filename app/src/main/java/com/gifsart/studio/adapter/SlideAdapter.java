package com.gifsart.studio.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;


import com.gifsart.studio.R;
import com.gifsart.studio.activity.EditFrameActivity;
import com.gifsart.studio.helper.ItemTouchHelperAdapter;
import com.gifsart.studio.item.GifItem;
import com.gifsart.studio.utils.Type;
import com.gifsart.studio.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Tigran on 6/23/15.
 */
public class SlideAdapter extends RecyclerView.Adapter<SlideAdapter.ViewHolder> implements ItemTouchHelperAdapter {

    private ArrayList<GifItem> array;
    private Activity activity;

    public SlideAdapter(ArrayList<GifItem> arr, Activity activity) {

        array = arr;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(activity).inflate(R.layout.item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        try {
            holder.icon.setImageBitmap(Utils.scaleCenterCrop(array.get(position).getBitmap(), 400, 400));
            holder.icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("gagag", position + "");
                    Intent intent = new Intent(activity, EditFrameActivity.class);
                    activity.startActivityForResult(intent, 100);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
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
            icon.setScaleType(ImageView.ScaleType.CENTER_CROP);
            selected = (ImageView) itemView.findViewById(R.id.item_selected);
            selected.setVisibility(View.VISIBLE);
        }
    }

    public GifItem getItem(int i) {
        return array.get(i);
    }


}
