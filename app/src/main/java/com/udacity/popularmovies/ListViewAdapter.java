package com.udacity.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.widget.ImageView.ScaleType.CENTER_CROP;

// List view adapter for review and trailer list
public class ListViewAdapter implements ListAdapter {

    private static final String LOG_TAG = ListViewAdapter.class.getSimpleName();
    private final Context context;
    private final Activity mainAct;
    private final String fetchType;
    private final List<String[]> urls = new ArrayList<String[]>();
    String[] images;
    String[] text;
    String[] videoKeys;
    String[] object;
    public static final String YOUTUBE_THUMBNAIL_URL_BASE = "http://img.youtube.com/vi/";
    public static final String YOUTUBE_PLAYER_URL_BASE = "https://www.youtube.com/watch?v=";

    public ListViewAdapter(Activity activity, String result, String fetchType) {
        this.context = activity.getApplicationContext();
        this.mainAct = activity;
        this.fetchType = fetchType;
        try {
            JSONObject movieJson = new JSONObject(result);
            JSONArray movieArray = movieJson.getJSONArray("results");
            if(movieArray != null) {
                images = new String[movieArray.length()];
                text = new String[movieArray.length()];
                videoKeys = new String[movieArray.length()];
                for (int i = 0; i < movieArray.length(); i++) {
                    JSONObject movie = movieArray.getJSONObject(i);
                    videoKeys[i] = "";
                    if(fetchType.equals(mainAct.getResources().getString(R.string.trailers))) {
                        String videoKey = movie.getString("key");
                        videoKeys[i] = videoKey;
                        text[i] = movie.getString("name");
                        images[i] = YOUTUBE_THUMBNAIL_URL_BASE + videoKey + "/default.jpg";
                    }else{
                        text[i] = movie.getString("content");
                        images[i] = movie.getString("author");
                    }
                    urls.add(new String[]{videoKeys[i], images[i], text[i]});
                }
            }
        }catch(JSONException e){

        }
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getCount() {
        return urls.size();
    }

    @Override
    public Object getItem(int position) {
        return urls.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if(this.fetchType.equals(mainAct.getResources().getString(R.string.trailers))) {
            view = LayoutInflater.from(context).inflate(R.layout.list_item_trailer, parent, false);
        }else{
            view = LayoutInflater.from(context).inflate(R.layout.list_item_review, parent, false);
        }

        String url = null;
        String text = null;
        String author = null;

        // Get the image URL for the current position.
        object = (String[]) getItem(position);
        Log.v(LOG_TAG, object.toString());
        if(this.fetchType.equals(mainAct.getResources().getString(R.string.trailers))){
            url = object[1];
            text = object[2];
            ImageView trailerThumb = (ImageView) view.findViewById(R.id.trailer_thumbnail);
            // Trigger the download of the URL asynchronously into the image view.
            Picasso.with(context) //
                    .load(url) //
                    .placeholder(R.drawable.placeholder) //
                    .error(R.drawable.error) //
                    .fit() //
                    .tag(context) //
                    .into(trailerThumb);
            TextView trailerText = (TextView) view.findViewById(R.id.trailer_text);
            trailerText.setText(text);
//            view.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    String videoKey = object[0];
//                    if (!videoKey.equals("")) {
//                        String url = YOUTUBE_PLAYER_URL_BASE + videoKey;
//                        Log.v(LOG_TAG, url);
//                        mainAct.getApplicationContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
//                    }
//                }
//            });
        }else{
            author = object[1];
            text = object[2];
            TextView reviewAuthor = (TextView) view.findViewById(R.id.review_author);
            reviewAuthor.setText(author);
            TextView reviewContent = (TextView) view.findViewById(R.id.review_content);
            reviewContent.setText(text);
        }

        return view;
    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
