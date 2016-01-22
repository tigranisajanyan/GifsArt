package com.gifsart.studio.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.decoder.PhotoUtils;
import com.gifsart.studio.R;
import com.gifsart.studio.activity.MainActivity;
import com.gifsart.studio.item.GifItem;
import com.gifsart.studio.utils.GifsArtConst;
import com.gifsart.studio.utils.Type;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * Created by Tigran on 6/23/15.
 */
public class SlideAdapter extends RecyclerView.Adapter<SlideAdapter.ViewHolder> {//implements ItemTouchHelperAdapter {

    private ArrayList<GifItem> array;
    private Activity activity;
    private Context context;

    public SlideAdapter(ArrayList<GifItem> arr, Activity activity, Context context) {
        array = arr;
        this.context = context;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(activity).inflate(R.layout.gif_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        if (array.get(position).getFilePath() != null) {
            holder.mainFrameImage.setImageBitmap(PhotoUtils.loadRawBitmap(array.get(position).getFilePath()));
        }
        if (array.get(position).getType() == Type.GIF) {
            holder.imageType.setImageDrawable(activity.getResources().getDrawable(R.drawable.gif_icon));
        } else if (array.get(position).getType() == Type.VIDEO) {
            holder.imageType.setImageDrawable(activity.getResources().getDrawable(R.drawable.video_icon));
        } else if (array.get(position).getType() == Type.IMAGE || array.get(position).getType() == Type.IMAGE) {
            holder.imageType.setImageBitmap(null);
        }

        holder.mainFrameImage.setScaleType((position == array.size() - 1) ? ImageView.ScaleType.CENTER_INSIDE : ImageView.ScaleType.CENTER_CROP);

        if (position == array.size() - 1) {
            holder.mainFrameImage.setImageDrawable(activity.getResources().getDrawable(R.drawable.add_icon));
            holder.mainFrameImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, MainActivity.class);
                    activity.startActivityForResult(intent, GifsArtConst.REQUEST_CODE_MAIN_ACTIVITY);
                    SharedPreferences sharedPreferences = context.getSharedPreferences(GifsArtConst.SHARED_PREFERENCES, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(GifsArtConst.SHARED_PREFERENCES_IS_OPENED, true);
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

                    //Intent intent = new Intent(activity, EditFrameActivity.class);
                    //intent.putExtra(GifsArtConst.INTENT_IMAGE_BITMAP, byteArray);
                    //activity.startActivityForResult(intent, GifsArtConst.REQUEST_CODE_EDIT_FRAME_ACTIVITY);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return array.size();
    }

    /*@Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if (fromPosition != array.size() - 1) {
            if (toPosition != array.size() - 1) {
                Collections.swap(array, fromPosition, toPosition);
                notifyItemMoved(fromPosition, toPosition);
            }
        }
        return true;
    }

    @Override
    public void onItemDismiss(int position) {

        if (position != array.size() - 1 && array.size() > 2) {
            array.remove(position);
            //animatrRemoveImpl(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, array.size());
        }
    }*/

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView mainFrameImage;
        private ImageView imageType;

        public ViewHolder(View itemView) {
            super(itemView);

            mainFrameImage = (ImageView) itemView.findViewById(R.id.image_item);

            imageType = (ImageView) itemView.findViewById(R.id.gif_item_type);
        }
    }

    public GifItem getItem(int i) {
        return array.get(i);
    }

}
