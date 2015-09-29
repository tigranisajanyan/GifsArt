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

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Tigran on 6/23/15.
 */
public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> implements ItemTouchHelperAdapter {

    private ArrayList<GalleryItem> array;
    private ArrayList<String> selectedItems = new ArrayList<>();
    private Context context;

    public Adapter(ArrayList<GalleryItem> arr, ArrayList<String> selectedItems, Context c) {

        this.selectedItems = selectedItems;
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

        //if (array.get(position).getType() == GalleryItem.Type.IMAGE) {
            holder.icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (array.get(position).isSeleted() && getSelected().size() > 1) {
                        array.get(position).setIsSeleted(false);

                    } else {
                        array.get(position).setIsSeleted(true);
                    }

                    holder.selected.setSelected(array
                            .get(position).isSeleted());
                }
            });


            try {
                holder.icon.setImageBitmap(array.get(position).getBitmap());

                holder.selected
                        .setSelected(array.get(position).isSeleted());

            } catch (Exception e) {
                e.printStackTrace();
            }
        //}
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

    public String getItem(int i) {
        return array.get(i).getImagePath();
    }

    public ArrayList<Bitmap> getSelected() {
        ArrayList<Bitmap> arrayList = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            if (array.get(i).isSeleted() == true) {
                arrayList.add(array.get(i).getBitmap());

            }
        }
        return arrayList;
    }

}
