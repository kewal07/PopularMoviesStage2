package com.udacity.popularmovies;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.widget.ImageView.ScaleType.CENTER_CROP;

// Main view Grid Adapter
final class GridViewAdapter extends BaseAdapter {
    private final Context context;
    private final String LOG_TAG = GridViewAdapter.class.getSimpleName();

    private final List<String> urls = new ArrayList<String>();
    String Movie_BASE_URL =
            "http://image.tmdb.org/t/p/w185/";
    String [] posters ;
    public GridViewAdapter(Context context,JSONArray movieArray)  {
        this.context = context;

        try {
           if(movieArray != null) {
                posters = new String[movieArray.length()];
                for (int i = 0; i < movieArray.length(); i++) {
                    JSONObject movie = movieArray.getJSONObject(i);
                    String path = movie.getString("poster_path");
                    posters[i] = Movie_BASE_URL.concat(path);
                }
            }

        }catch(JSONException e){

        }

        Collections.addAll(urls, posters);


    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {
//        SquaredImageView view = (SquaredImageView) convertView;
//        if (view == null) {
//            view = new SquaredImageView(context);
//            view.setScaleType(CENTER_CROP);
//        }
        View view = LayoutInflater.from(context).inflate(R.layout.grid_item_view, parent, false);

        ImageView grid_image = (ImageView) view.findViewById(R.id.grid_main_image);

        // Get the image URL for the current position.
        String url = getItem(position);

        // Trigger the download of the URL asynchronously into the image view.
        Picasso.with(context) //
                .load(url) //
                .placeholder(R.drawable.placeholder) //
                .error(R.drawable.error) //
                .fit() //
                .tag(context) //
                .into(grid_image);

        return view;
    }

    @Override public int getCount() {
        Log.v(LOG_TAG,"grid adapter count");
        Log.v(LOG_TAG,urls.size()+"");
        return urls.size();
    }

    @Override public String getItem(int position) {
        return urls.get(position);
    }

    @Override public long getItemId(int position) {
        return position;
    }

}
