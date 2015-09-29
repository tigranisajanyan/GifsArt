package com.gifsart.studio.gifutils;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gifsart.studio.item.GiphyItem;
import com.gifsart.studio.utils.GifsArtConst;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Tigran on 9/25/15.
 */
public class Giphy {

    private ProgressDialog progressDialog;
    private Context context;
    private String tag = GifsArtConst.GIPHY_TAG;
    private int offset;
    private int limit = 30;
    private ArrayList<GiphyItem> giphyItems = new ArrayList<>();
    private GiphyListener giphyListener;

    public Giphy() {

    }

    public Giphy(Context context, String tag, int offset, int limit) {

        this.context = context;
        if (tag != "" || tag != null) {
            this.tag = tag;
        }
        this.offset = offset;
        if (limit > 0) {
            this.limit = limit;
        }

    }

    public void requestGiphy() {

        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("please wait");
        progressDialog.show();
        String url = GifsArtConst.GIPHY_URL + tag + GifsArtConst.GIPHY_OFFSET + offset + GifsArtConst.GIPHY_LIMIT + limit + GifsArtConst.GIPHY_API_KEY;
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
                                giphyItem.setOriginalGifUrl(jsonArray.getJSONObject(i).getJSONObject("images").getJSONObject(GifsArtConst.GIPHY_SIZE_ORIGINAL).getString("url"));
                                giphyItem.setGifHeight(jsonArray.getJSONObject(i).getJSONObject("images").getJSONObject(GifsArtConst.GIPHY_SIZE_DOWNSAMPLED).getInt("height"));
                                giphyItem.setGifWidth(jsonArray.getJSONObject(i).getJSONObject("images").getJSONObject(GifsArtConst.GIPHY_SIZE_DOWNSAMPLED).getInt("width"));
                                giphyItems.add(giphyItem);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        giphyListener.onGiphyDownloadFinished(giphyItems);
                        progressDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(GifsArtConst.GIFSART_LOG, error + "");
            }
        });
        queue.add(stringRequest);
    }

    public void setOnDownloadedListener(GiphyListener l) {
        giphyListener = l;
    }


    public interface GiphyListener {
        void onGiphyDownloadFinished(ArrayList<GiphyItem> items);
    }

}
