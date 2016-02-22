package com.udacity.popularmovies;

import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.udacity.popularmovies.data.MovieContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    String movieId;
    Activity thisAct;
    String title;
    ImageButton starButton;
    ContentValues values;
    private final String LOG_TAG = DetailActivityFragment.class.getSimpleName();

    public DetailActivityFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        TextView titleView,userRatingView,plotView,releaseDateView,nameView;
        ImageView imageView;

        thisAct = getActivity();
        title = getActivity().getIntent().getStringExtra("title");
        String image = getActivity().getIntent().getStringExtra("image");
        String plot = getActivity().getIntent().getStringExtra("plot");
        String userRating = getActivity().getIntent().getStringExtra("userRating");
        String releaseDate = getActivity().getIntent().getStringExtra("releaseDate");
        movieId = getActivity().getIntent().getStringExtra("movieId");
        titleView = (TextView) view.findViewById(R.id.title);
        imageView = (ImageView) view.findViewById(R.id.image);
        userRatingView = (TextView) view.findViewById(R.id.userRating);
        plotView = (TextView) view.findViewById(R.id.plot);
        releaseDateView = (TextView) view.findViewById(R.id.releaseDate);
        nameView = (TextView) view.findViewById(R.id.name);
        nameView.setText(title);

        values = new ContentValues();

        values.put(MovieContract.Movie.COLUMN_TITLE, title);
        values.put(MovieContract.Movie.COLUMN_POSTER_URL, image);
        values.put(MovieContract.Movie.COLUMN_PLOT, plot);
        values.put(MovieContract.Movie.COLUMN_RATING, userRating);
        values.put(MovieContract.Movie.COLUMN_RELEASE_DATE, releaseDate);
        values.put(MovieContract.Movie.COLUMN_MOVIE_ID, movieId);


        titleView.setText(Html.fromHtml("<br/><br/>" + title));
        userRatingView.setText(Html.fromHtml("User Rating :" + userRating + "/10"));
        releaseDateView.setText(Html.fromHtml("Release Date : " + releaseDate));
        plotView.setText(Html.fromHtml(" Summary: \n " + plot));
        Picasso.with(getActivity()) //
                .load(image) //
                .placeholder(R.drawable.placeholder) //
                .error(R.drawable.error) //
                .fit() //
                .into(imageView);

        starButton = (ImageButton) view.findViewById(R.id.favorite_button);
        Cursor c = getActivity().getContentResolver().query(MovieContract.Movie.CONTENT_URI,
                new String[]{MovieContract.Movie.COLUMN_MOVIE_ID},
                MovieContract.Movie.COLUMN_MOVIE_ID + "= ? ",
                new String[]{movieId},
                null);
        if (c.getCount() > 0) {
            starButton.setImageResource(android.support.v7.appcompat.R.drawable.abc_btn_rating_star_on_mtrl_alpha);
        }else{
            starButton.setImageResource(android.support.v7.appcompat.R.drawable.abc_btn_rating_star_off_mtrl_alpha);
        }
        Button trailerBut = (Button) view.findViewById(R.id.trailer_button);
        trailerBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(thisAct.getApplicationContext(), TrailerActivity.class);
                intent.putExtras(thisAct.getIntent().getExtras());
                intent.putExtra("movieId", movieId);
                thisAct.startActivity(intent);
            }
        });
        Button reviewBut = (Button) view.findViewById(R.id.review_button);
        reviewBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(thisAct.getApplicationContext(), ReviewActivity.class);
                intent.putExtras(thisAct.getIntent().getExtras());
                intent.putExtra("movieId", movieId);
                thisAct.startActivity(intent);
            }
        });
        starButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor c = thisAct.getApplicationContext().getContentResolver().query(MovieContract.Movie.CONTENT_URI,
                        new String[]{MovieContract.Movie.COLUMN_MOVIE_ID},
                        MovieContract.Movie.COLUMN_MOVIE_ID + "= ? ",
                        new String[]{movieId},
                        null);
                if (c.getCount() > 0) {
                    int rowDeleted = thisAct.getApplicationContext().getContentResolver().delete(MovieContract.Movie.CONTENT_URI, MovieContract.Movie.COLUMN_MOVIE_ID + "= ?", new String[]{movieId});
                    if (rowDeleted > 0) {
                        Toast.makeText(thisAct.getApplicationContext(), "Removed  " + title + " from favourite", Toast.LENGTH_SHORT).show();
                        starButton.setImageResource(android.support.v7.appcompat.R.drawable.abc_btn_rating_star_off_mtrl_alpha);
                    }
                    Log.d(LOG_TAG, "Row deleted: " + rowDeleted);
                }else{
                    Uri rowUri;
                    rowUri = thisAct.getApplicationContext().getContentResolver().insert(MovieContract.Movie.CONTENT_URI, values);
                    long rowId = ContentUris.parseId(rowUri);
                    if (rowId > 0) {
                        Toast.makeText(thisAct.getApplicationContext(), "Favourite  " + title, Toast.LENGTH_SHORT).show();
                        starButton.setImageResource(android.support.v7.appcompat.R.drawable.abc_btn_rating_star_on_mtrl_alpha);
                    }
                    Log.d(LOG_TAG, "New row id inserted via provider: " + rowId);
                }
            }
        });
        return view;
    }
}
