package com.gifsart.studio.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.gifsart.studio.R;
import com.gifsart.studio.item.GiphyItem;
import com.gifsart.studio.utils.GifsArtConst;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Tigran on 9/8/15.
 */
public class GiphyAdapter extends RecyclerView.Adapter<GiphyAdapter.ViewHolder> {

    private ArrayList<GiphyItem> giphyItems = new ArrayList<>();
    private ArrayList<Integer> colors = new ArrayList<>();
    private LayoutInflater inflater = null;
    private Context context;
    private int limit = 30;
    private int offset = 0;
    private int selectedPosition = -1;
    Random random = new Random();

    public GiphyAdapter(Context context, ArrayList<Integer> colors) {
        this.context = context;
        this.colors = colors;
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
        holder.simpleDraweeView.setBackgroundColor(colors.get(random.nextInt(colors.size())));
        Uri uri = Uri.parse(giphyItems.get(position).getGifUrl());
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(uri)
                .setAutoPlayAnimations(true).build();
        holder.simpleDraweeView.setController(controller);
        if (position + 1 == limit + offset) {
            offset = offset + limit;
            initGipfy(offset);
        }
        holder.simpleDraweeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (selectedPosition > -1) {
                    giphyItems.get(selectedPosition).setIsSelected(false);
                }
                if (selectedPosition != position) {
                    giphyItems.get(position).setIsSelected(true);
                    notifyItemChanged(selectedPosition);
                    notifyItemChanged(position);
                    selectedPosition = position;
                } else {
                    notifyItemChanged(selectedPosition);
                    notifyItemChanged(position);
                    selectedPosition = position;
                    selectedPosition = -1;
                }
            }
        });

        if (giphyItems.get(position).isSelected() == true) {
            holder.corner.setVisibility(View.VISIBLE);
        } else {
            holder.corner.setVisibility(View.GONE);
        }

    }


    @Override
    public int getItemCount() {
        return giphyItems.size();
    }

    public void addItem(GiphyItem item) {
        giphyItems.add(item);
    }

    public void clear() {
        giphyItems.clear();
    }

    public GiphyItem getItem(int position) {
        return giphyItems.get(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public SimpleDraweeView simpleDraweeView = null;
        public ImageView corner;

        public ViewHolder(View itemView) {
            super(itemView);
            simpleDraweeView = (SimpleDraweeView) itemView.findViewById(R.id.my_image_view);
            corner = (ImageView) itemView.findViewById(R.id.giphy_corner);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(context.getResources().getDisplayMetrics().widthPixels / 2, context.getResources().getDisplayMetrics().widthPixels / 2);
            simpleDraweeView.setLayoutParams(layoutParams);
            corner.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, context.getResources().getDisplayMetrics().widthPixels / 2));
            corner.setVisibility(View.GONE);
        }
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    private void initGipfy(int o) {

        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("please wait");
        progressDialog.show();
        String url = GifsArtConst.GIPHY_URL + GifsArtConst.GIPHY_OFFSET + o + GifsArtConst.GIPHY_LIMIT + GifsArtConst.GIPHY_API_KEY;
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        JSONObject jsonObject = null;
                        JSONArray jsonArray = null;
                        try {
                            jsonObject = new JSONObject(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            jsonArray = jsonObject.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                GiphyItem giphyItem = new GiphyItem();
                                giphyItem.setGifUrl(jsonArray.getJSONObject(i).getJSONObject("images").getJSONObject(GifsArtConst.GIPHY_SIZE_DOWNSAMPLED).getString("url"));
                                giphyItem.setGifHeight(jsonArray.getJSONObject(i).getJSONObject("images").getJSONObject(GifsArtConst.GIPHY_SIZE_DOWNSAMPLED).getInt("height"));
                                giphyItem.setGifWidth(jsonArray.getJSONObject(i).getJSONObject("images").getJSONObject(GifsArtConst.GIPHY_SIZE_DOWNSAMPLED).getInt("width"));
                                addItem(giphyItem);
                                notifyDataSetChanged();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        notifyDataSetChanged();
                        progressDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(GifsArtConst.GIFIT_LOG, error + "");
            }
        });
        queue.add(stringRequest);
    }

}
