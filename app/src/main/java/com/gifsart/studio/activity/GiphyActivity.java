package com.gifsart.studio.activity;

import android.app.SearchManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gifsart.studio.R;
import com.gifsart.studio.adapter.GiphyAdapter;
import com.gifsart.studio.item.GalleryItem;
import com.gifsart.studio.item.GiphyItem;
import com.gifsart.studio.utils.GifsArtConst;
import com.gifsart.studio.utils.SpacesItemDecoration;
import com.gifsart.studio.utils.Utils;
import com.melnykov.fab.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import pl.droidsonroids.gif.GifDrawable;

public class GiphyActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    private RecyclerView.ItemAnimator itemAnimator;
    private GiphyAdapter giphyAdapter;
    int offset = 0;

    private static final String root = Environment.getExternalStorageDirectory().toString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giphy);

        init();

    }

    public void init() {

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(getResources().getColor(R.color.blue));
        colors.add(getResources().getColor(R.color.pink));
        colors.add(getResources().getColor(R.color.yellow));
        colors.add(getResources().getColor(R.color.green));
        colors.add(getResources().getColor(R.color.orange));
        giphyAdapter = new GiphyAdapter(this, colors);

        recyclerView = (RecyclerView) findViewById(R.id.giphy_rec_view);
        gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        itemAnimator = new DefaultItemAnimator();

        recyclerView.setHasFixedSize(true);
        recyclerView.setClipToPadding(true);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setItemAnimator(itemAnimator);

        recyclerView.setAdapter(giphyAdapter);
        recyclerView.addItemDecoration(new SpacesItemDecoration(5));

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.attachToRecyclerView(recyclerView);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(GiphyActivity.this, "Gorisi tti arax", Toast.LENGTH_LONG).show();
                //recyclerView.smoothScrollToPosition(0);
            }
        });


        initGipfy();

    }

    private void initGipfy() {

        String url = GifsArtConst.GIPHY_URL + GifsArtConst.GIPHY_OFFSET + offset + GifsArtConst.GIPHY_LIMIT + GifsArtConst.GIPHY_API_KEY;
        RequestQueue queue = Volley.newRequestQueue(GiphyActivity.this);
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
    public boolean onOptionsItemSelected(final MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_done) {

            if (giphyAdapter.getSelectedPosition() > -1) {

                new MyTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            }
            Log.d("gagagagag", giphyAdapter.getSelectedPosition() + "");
            return true;
        }
        if (id == R.id.action_search) {

            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);

            if (null != searchView) {
                searchView.setSearchableInfo(searchManager
                        .getSearchableInfo(getComponentName()));
                searchView.setIconifiedByDefault(false);
            }

            SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
                public boolean onQueryTextChange(String newText) {
                    // This is your adapter that will be filtered
                    //Log.d("gagagaga1", newText);
                    return true;
                }

                public boolean onQueryTextSubmit(String query) {
                    // **Here you can get the value "query" which is entered in the search box.**
                    Log.d("gagagaga2", query);
                    searchView.setIconified(false);
                    searchView.clearFocus();

                    item.collapseActionView();
                    return false;
                }
            };
            searchView.setOnQueryTextListener(queryTextListener);

        }


        return super.onOptionsItemSelected(item);
    }


    class MyTask extends AsyncTask<Void, Void, Void> {

        GiphyItem giphyItem = giphyAdapter.getItem(giphyAdapter.getSelectedPosition());

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            Utils.downloadFile(root + "/ttt.gif", giphyItem.getGifUrl());

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            try {
                GifDrawable gifDrawable = new GifDrawable(root + "/ttt.gif");
                Log.d("gagagagag", gifDrawable.getNumberOfFrames() + "");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
