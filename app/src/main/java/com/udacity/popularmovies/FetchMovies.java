package com.udacity.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.udacity.popularmovies.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

// Fetch all Movies
public class FetchMovies extends AsyncTask<String[], Void, String> {

    String forecastJsonStr = null;
    GridView gv;
    String apiResult;
    Activity mainAct;
    String movieId;
    String queryParameter;
    JSONArray movieArray = null;

    public FetchMovies(Activity a)
    {
        mainAct = a;
    }

    @Override
    protected String  doInBackground(String[]... params) {



        String[] queryParameters = params[0];
        queryParameter = queryParameters[0];
        movieId = queryParameters[1];
        SharedPreferences sharedPreferences = mainAct.getSharedPreferences("UdacityMovies", Context.MODE_PRIVATE);
        if(isOnline())
            queryParameter = sharedPreferences.getString("sequence",mainAct.getResources().getString(R.string.popular_query));
        else {
            queryParameter = mainAct.getResources().getString(R.string.fav_query);
            sharedPreferences.edit().putString("sequence",mainAct.getResources().getString(R.string.fav_query)).apply();
        }

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
            final String Movie_BASE_URL =
                    "http://api.themoviedb.org/3/discover/movie?";
            final String APPID_PARAM = "api_key";
            final String key =mainAct.getResources().getString(R.string.api_key);
            final String q = "sort_by";
            final String query = queryParameter;

            if(queryParameter.equals("my_constant_favorite")){
            }else {
                Uri builtUri = Uri.parse(Movie_BASE_URL).buildUpon()
                        .appendQueryParameter(q, query)
                        .appendQueryParameter(APPID_PARAM, key)
                        .build();

                URL url = new URL(builtUri.toString());

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
                forecastJsonStr = buffer.toString();
            }


        } catch (IOException e) {
            return null;
        } finally {
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
        return forecastJsonStr;
    }

    @Override
    protected void onPostExecute(String  result) {
//        android.os.Debug.waitForDebugger();
        apiResult=result;
        if(queryParameter.equals(mainAct.getResources().getString(R.string.popular_query))){
            ((AppCompatActivity) mainAct).getSupportActionBar().setTitle(mainAct.getResources().getString(R.string.action_popularity));
        }
        else if(queryParameter.equals(mainAct.getResources().getString(R.string.rating_query))){
            ((AppCompatActivity) mainAct).getSupportActionBar().setTitle(mainAct.getResources().getString(R.string.action_vote_average));
        }else if(queryParameter.equals(mainAct.getResources().getString(R.string.fav_query))){
            ((AppCompatActivity) mainAct).getSupportActionBar().setTitle(mainAct.getResources().getString(R.string.action_favorite));
        }
        if(queryParameter.equals(mainAct.getResources().getString(R.string.fav_query))){
            if(isOnline())
                Toast.makeText(mainAct.getApplicationContext(), "No Internet Connectivty", Toast.LENGTH_SHORT).show();
            movieArray = getDetailForDB();
        }
        else {
            try {
                JSONObject movieJson = new JSONObject(result);
                movieArray = movieJson.getJSONArray("results");
            } catch (JSONException e) {
            }
        }
        gv = (GridView) mainAct.findViewById(R.id.grid_view);
        if(gv == null){
            startDetail(movieId,movieArray);
        }
        else{
            gv.setAdapter(new GridViewAdapter(mainAct, movieArray));
            gv.setOnScrollListener(new ScrollListener(mainAct));
            gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v,
                                        int position, long id) {
                    String poster_path = parent.getItemAtPosition(position).toString().split("185/")[1];
                    startDetail(poster_path,movieArray);
                }
            });
        }
    }

    void startDetail(String poster_path, JSONArray movieArray){
        try {
            int index = 0;
            for (int i = 0; i < movieArray.length(); i++) {
                JSONObject movieItem = movieArray.getJSONObject(i);
                if (movieItem.getString("poster_path").equals(poster_path) || movieItem.getString("id").equals(poster_path)) {
                    index = i;
                    break;
                }
            }
            JSONObject movieDetails = movieArray.getJSONObject(index);
            String title = movieDetails.getString("original_title");
            String Movie_BASE_URL = "http://image.tmdb.org/t/p/w185/";
            String image = Movie_BASE_URL.concat(movieDetails.getString("poster_path"));
            String plot = movieDetails.getString("overview");
            String userRating = movieDetails.getString("vote_average");
            String releaseDate = movieDetails.getString("release_date");
            Intent intent = new Intent(mainAct.getApplicationContext(), DetailActivity.class);
            intent.putExtra("title", title);
            intent.putExtra("image", image);
            intent.putExtra("plot", plot);
            intent.putExtra("userRating", userRating);
            intent.putExtra("releaseDate", releaseDate);
            intent.putExtra("movieId", movieDetails.getString("id"));
            //Start details activity
            mainAct.startActivity(intent);
        } catch (JSONException e) {
        }
    }

    JSONArray getDetailForDB(){
        Cursor cursor = mainAct.getApplicationContext().getContentResolver().query(MovieContract.Movie.CONTENT_URI, null, null, null, null);
        JSONArray selectedArray = new JSONArray();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                JSONObject movieJson = new JSONObject();
                try {
                    movieJson.put("poster_path", cursor.getString(cursor.getColumnIndex(MovieContract.Movie.COLUMN_POSTER_URL)));
                    movieJson.put("original_title", cursor.getString(cursor.getColumnIndex(MovieContract.Movie.COLUMN_TITLE)));
                    movieJson.put("overview", cursor.getString(cursor.getColumnIndex(MovieContract.Movie.COLUMN_PLOT)));
                    movieJson.put("vote_average", cursor.getString(cursor.getColumnIndex(MovieContract.Movie.COLUMN_RATING)));
                    movieJson.put("release_date", cursor.getString(cursor.getColumnIndex(MovieContract.Movie.COLUMN_RELEASE_DATE)));
                    movieJson.put("id", cursor.getString(cursor.getColumnIndex(MovieContract.Movie.COLUMN_MOVIE_ID)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                selectedArray.put(movieJson);
            }
        }
        return selectedArray;
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) mainAct.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
