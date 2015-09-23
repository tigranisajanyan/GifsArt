package com.gifsart.studio.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gifsart.studio.R;
import com.gifsart.studio.adapter.GiphyAdapter;
import com.gifsart.studio.item.GiphyItem;
import com.gifsart.studio.utils.GifsArtConst;
import com.gifsart.studio.utils.SpacesItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class GiphyActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    private RecyclerView.ItemAnimator itemAnimator;
    private GiphyAdapter giphyAdapter;
    private ArrayList<GiphyItem> customGalleryArrayList = new ArrayList<>();
    private ArrayList<String> giphyItemUrls = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giphy);

        init();

    }

    public void init() {

        giphyAdapter = new GiphyAdapter(this);

        recyclerView = (RecyclerView) findViewById(R.id.giphy_rec_view);
        gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        itemAnimator = new DefaultItemAnimator();

        recyclerView.setHasFixedSize(true);
        recyclerView.setClipToPadding(true);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setItemAnimator(itemAnimator);

        recyclerView.setAdapter(giphyAdapter);
        recyclerView.addItemDecoration(new SpacesItemDecoration(7));

        initGipfy();

    }

    private void initGipfy() {

        RequestQueue queue = Volley.newRequestQueue(GiphyActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, GifsArtConst.GIPHY_URL,
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
                                giphyItem.setGifUrl(jsonArray.getJSONObject(i).getJSONObject("images").getJSONObject(GifsArtConst.GIPHY_SIZE_PREVIEW).getString("url"));
                                giphyItem.setGifHeight(jsonArray.getJSONObject(i).getJSONObject("images").getJSONObject(GifsArtConst.GIPHY_SIZE_PREVIEW).getInt("height"));
                                giphyItem.setGifWidth(jsonArray.getJSONObject(i).getJSONObject("images").getJSONObject(GifsArtConst.GIPHY_SIZE_PREVIEW).getInt("width"));
                                giphyAdapter.addItem(giphyItem);
                                giphyAdapter.notifyDataSetChanged();
                                //Log.d(GifItConst.GIFIT_LOG, giphyItemUrls.get(i) + "");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        giphyAdapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(GifsArtConst.GIFIT_LOG, error + "");
            }
        });
        queue.add(stringRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_giphy, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
