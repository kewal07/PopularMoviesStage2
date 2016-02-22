package com.udacity.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// DB Helper
public class MovieHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "movie.db";

    public MovieHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private static final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieContract.Movie.TABLE_NAME + " (" +
            MovieContract.Movie._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            MovieContract.Movie.COLUMN_TITLE + " TEXT NOT NULL, " +
            MovieContract.Movie.COLUMN_POSTER_URL + " TEXT NOT NULL, " +
            MovieContract.Movie.COLUMN_PLOT + " TEXT NOT NULL, " +
            MovieContract.Movie.COLUMN_RATING + " TEXT NOT NULL, " +
            MovieContract.Movie.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
            MovieContract.Movie.COLUMN_MOVIE_ID + " TEXT NOT NULL, " +
            "UNIQUE (" + MovieContract.Movie.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.Movie.TABLE_NAME);
        onCreate(db);
    }
}
