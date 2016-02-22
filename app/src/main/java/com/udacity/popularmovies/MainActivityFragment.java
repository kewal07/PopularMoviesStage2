package com.udacity.popularmovies;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private static String LOG_TAG = MainActivityFragment.class.getSimpleName();

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(LOG_TAG,"main fragment present");
        FetchMovies movieTask = new FetchMovies(getActivity());
        movieTask.execute(new String[]{"popularity.desc", ""});
        View view = inflater.inflate(R.layout.fragment_main, container, false);
//        FrameLayout fragment_detail = (FrameLayout)view.findViewById(R.id.fragment_detail);
        if(view.findViewById(R.id.fragment_detail) != null){
            Log.v(LOG_TAG,"detail fragment present");
        }
        return view;
    }
}
