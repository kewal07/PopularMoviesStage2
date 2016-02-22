package com.udacity.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

// Fetch reviews -- reviews trailers -- videos
public class FetchMovieExtras extends AsyncTask<String[], Void, String> {

    private static final String LOG_TAG = FetchMovieExtras.class.getSimpleName();
    private String MOVIE_BASE_DET_URL = "http://api.themoviedb.org/3/movie/";
    private String apiResult;
    Activity mainAct;
    private String fetchType;
    ListView lv = null;
    public static final String YOUTUBE_PLAYER_URL_BASE = "https://www.youtube.com/watch?v=";
    Context context;

    public FetchMovieExtras(Activity a)
    {
        mainAct = a;
        context = a.getApplicationContext();
    }

    @Override
    protected String doInBackground(String[]... params) {
        Log.d(LOG_TAG,"get extra detail");
        String[] paramList = params[0];
        String movieId = paramList[0];
        this.fetchType = paramList[1];
        String urlS = MOVIE_BASE_DET_URL + movieId + "/" + this.fetchType;

        Log.v(LOG_TAG,urlS);
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        URL url = null;
        try {
            final String APPID_PARAM = "api_key";

            Uri builtUri = Uri.parse(urlS).buildUpon()
                    .appendQueryParameter(APPID_PARAM, mainAct.getResources().getString(R.string.api_key))
                    .build();

            url = new URL(builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {

                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            this.apiResult = buffer.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {

                }
            }
        }
        return this.apiResult;
    }

    @Override
    protected void onPostExecute(String  result) {
        this.apiResult=result;
        if (this.fetchType.equals(this.mainAct.getResources().getString(R.string.trailers))) {
            lv = (ListView) this.mainAct.findViewById(R.id.trailers);
        }else{
            lv = (ListView) this.mainAct.findViewById(R.id.reviews);
        }
        lv.setAdapter(new ListViewAdapter(this.mainAct, result, this.fetchType));
        if (this.fetchType.equals(this.mainAct.getResources().getString(R.string.trailers))) {
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View v,
                                        int position, long id) {
//                android.os.Debug.waitForDebugger();
//                    Log.v(LOG_TAG, "Clicked Trailer");
//                    Log.v(LOG_TAG, position + "");
                    String[] object = (String[]) parent.getItemAtPosition(position);
                    String videoKey = object[0];
                    if (!videoKey.equals("")) {
                        String url = YOUTUBE_PLAYER_URL_BASE + videoKey;
                        Log.v(LOG_TAG, url);
                        mainAct.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    }
                }
            });
        }
    }
}
