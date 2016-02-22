package com.udacity.popularmovies;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A placeholder fragment containing a simple view.
 */
public class ReviewActivityFragment extends Fragment {

    public ReviewActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_review, container, false);
        String movieId = getActivity().getIntent().getStringExtra("movieId");
        FetchMovieExtras fetchExtra = new FetchMovieExtras(getActivity());
        String[] paramsExtra = new String[]{movieId, getResources().getString(R.string.reviews)};
        fetchExtra.execute(paramsExtra);
        return view;
    }
}
