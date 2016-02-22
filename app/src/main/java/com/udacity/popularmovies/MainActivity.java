package com.udacity.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSharedPreferences = getSharedPreferences("UdacityMovies",Context.MODE_PRIVATE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_vote_average) {
            FetchMovies movieTask = new FetchMovies(this);
            movieTask.execute(new String[]{"vote_average.desc", ""});
            mSharedPreferences.edit().putString("sequence","vote_average.desc").apply();
            return true;
        }
        if (id == R.id.action_popularity) {
            mSharedPreferences.edit().putString("sequence","popularity.desc").apply();
            FetchMovies movieTask = new FetchMovies(this);
            movieTask.execute(new String[]{"popularity.desc", ""});
            return true;
        }
        if (id == R.id.action_favorite) {
            FetchMovies movieTask = new FetchMovies(this);
            mSharedPreferences.edit().putString("sequence","my_constant_favorite").apply();
            movieTask.execute(new String[]{"popularity.desc", "my_constant_favorite"});
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
