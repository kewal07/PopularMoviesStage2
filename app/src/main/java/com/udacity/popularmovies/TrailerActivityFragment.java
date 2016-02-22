package com.udacity.popularmovies;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * A placeholder fragment containing a simple view.
 */
public class TrailerActivityFragment extends Fragment {

    private final String LOG_TAG = TrailerActivityFragment.class.getSimpleName();
    public static final String YOUTUBE_PLAYER_URL_BASE = "https://www.youtube.com/watch?v=";
    Activity act;

    public TrailerActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        act = getActivity();
        View view = inflater.inflate(R.layout.fragment_trailer, container, false);
        String movieId = getActivity().getIntent().getStringExtra("movieId");
        FetchMovieExtras fetchExtra = new FetchMovieExtras(getActivity());
        String[] paramsExtra = new String[]{movieId, getResources().getString(R.string.trailers)};
        fetchExtra.execute(paramsExtra);
        return view;
    }
}
