package com.gifsart.studio.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.gifsart.studio.R;
import com.gifsart.studio.social.Photo;
import com.gifsart.studio.social.UserContraller;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Tigran on 12/1/15.
 */
public class ProfileUserPhotosAdapter extends RecyclerView.Adapter<ProfileUserPhotosAdapter.ViewHolder> {

    private ArrayList<Photo> userPhotos = new ArrayList<>();
    private ArrayList<Integer> colors = new ArrayList<>();

    public static ArrayList<String> deletedItems = new ArrayList<>();
    private LayoutInflater inflater = null;
    private Context context;
    private int offset = 0;
    private int limit = 30;
    private Random random = new Random();

    public ProfileUserPhotosAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (inflater == null) {
            inflater = LayoutInflater.from(parent.getContext());
        }
        colors.add(ContextCompat.getColor(context, R.color.blue));
        colors.add(ContextCompat.getColor(context, R.color.pink));
        colors.add(ContextCompat.getColor(context, R.color.yellow));
        colors.add(ContextCompat.getColor(context, R.color.green));
        colors.add(ContextCompat.getColor(context, R.color.orange));
        return new ViewHolder(inflater.inflate(R.layout.profile_photos_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.userPhotoImageView.setBackgroundColor(colors.get(random.nextInt(colors.size())));
        /*DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(Uri.parse(userPhotos.get(position).getUrl() + "?r240x240f5"))
                .setAutoPlayAnimations(true).build();
        holder.userPhotoImageView.setController(controller);*/
        Glide.with(context).load(Uri.parse(userPhotos.get(position).getUrl() + "?r240x240f5")).asGif().override(300, 300).centerCrop().into(holder.userPhotoImageView);
        if (userPhotos.get(position).getIsPublic()) {
            holder.privateImageView.setVisibility(View.GONE);
        } else {
            holder.privateImageView.setVisibility(View.VISIBLE);
        }
        if (position + 1 == limit + offset) {
            offset = offset + limit;
            final UserContraller userContraller = new UserContraller(context);
            userContraller.setOnRequestReadyListener(new UserContraller.UserRequest() {
                @Override
                public void onRequestReady(int requestNumber, String messege) {
                    userPhotos.addAll(userContraller.getUserPhotos());
                    notifyDataSetChanged();
                }
            });
            userContraller.requestUserPhotos(UserContraller.readUserFromFile(context).getKey(), offset, limit);
        }
    }

    @Override
    public int getItemCount() {
        return userPhotos.size();
    }

    public void addItems(ArrayList<Photo> userPhotos) {
        removeAllItems();
        this.userPhotos = userPhotos;
        checkDeletedItem();
        notifyDataSetChanged();
    }

    public void removeAllItems() {
        this.userPhotos.removeAll(userPhotos);
        offset = 0;
        limit = 30;
        notifyDataSetChanged();
    }

    public Photo getItem(int position) {
        return userPhotos.get(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView userPhotoImageView;
        public ImageView privateImageView;

        public ViewHolder(View itemView) {
            super(itemView);

            int imageSize = context.getResources().getDisplayMetrics().widthPixels / 2;
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(imageSize, imageSize);
            userPhotoImageView = (ImageView) itemView.findViewById(R.id.user_profile_photos_image_view);
            privateImageView = (ImageView) itemView.findViewById(R.id.private_image_view);
            userPhotoImageView.setLayoutParams(layoutParams);
        }
    }

    private void checkDeletedItem() {
        if (deletedItems.size() > 0) {
            for (int i = 0; i < userPhotos.size(); i++) {
                for (int j = 0; j < deletedItems.size(); j++) {
                    if (userPhotos.get(i).getId().equals(deletedItems.get(j))) {
                        userPhotos.remove(i);
                        break;
                    }
                }
            }
        }
    }

}