package com.gifsart.studio.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.gifsart.studio.R;
import com.gifsart.studio.activity.EditFrameActivity;
import com.gifsart.studio.activity.MainActivity;
import com.gifsart.studio.helper.ItemTouchHelperAdapter;
import com.gifsart.studio.item.GifItem;
import com.gifsart.studio.utils.GifsArtConst;
import com.gifsart.studio.utils.Type;
import com.gifsart.studio.utils.Utils;

import java.io.ByteArrayOutputStream;
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
        View v = LayoutInflater.from(activity).inflate(R.layout.gif_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {


        try {
            holder.mainFrameImage.setImageBitmap(Utils.scaleCenterCrop(array.get(position).getBitmap(), 400, 400));

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (array.get(position).getType() == Type.GIF) {
            holder.imageType.setImageDrawable(activity.getResources().getDrawable(R.drawable.gif_icon));
            Log.d("fafaa", position + "");
        } else if (array.get(position).getType() == Type.VIDEO) {
            holder.imageType.setImageDrawable(activity.getResources().getDrawable(R.drawable.video_icon));
        } else if (array.get(position).getType() == Type.IMAGE) {
            holder.imageType.setImageBitmap(null);
        }
        if (position == array.size() - 1) {
            holder.mainFrameImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, MainActivity.class);
                    activity.startActivityForResult(intent, GifsArtConst.MAIN_ACTIVITY_REQUEST_CODE);
                    SharedPreferences sharedPreferences = activity.getSharedPreferences(GifsArtConst.SHARED_PREFERENCES, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("is_opened", true);
                    editor.commit();
                }
            });
        } else {
            holder.mainFrameImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    Bitmap bitmap = array.get(position).getBitmap();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();

                    Intent intent = new Intent(activity, EditFrameActivity.class);
                    intent.putExtra("image", byteArray);
                    activity.startActivityForResult(intent, GifsArtConst.EDIT_FRAME_ACTIVITY_REQUEST_CODE);
                }
            });
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

        private ImageView mainFrameImage;
        private ImageView imageType;
        private ImageView selected;

        public ViewHolder(View itemView) {
            super(itemView);

            mainFrameImage = (ImageView) itemView.findViewById(R.id.image_item);
            mainFrameImage.setScaleType(ImageView.ScaleType.CENTER_CROP);

            imageType = (ImageView) itemView.findViewById(R.id.gif_item_type);
        }
    }

    public GifItem getItem(int i) {
        return array.get(i);
    }


}
