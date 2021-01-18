package me.kaufhold.udacity.popularmovies.db;

import android.content.Context;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Movie.class}, version = 1, exportSchema = false)
public abstract class FavoriteMoviesDB extends RoomDatabase {

    private static final String LOG_TAG = FavoriteMoviesDB.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "favorite_movies";
    private static FavoriteMoviesDB sInstance;

    public static FavoriteMoviesDB getInstance(Context context) {
        if(sInstance == null) {
            synchronized (LOCK){
                Log.d(LOG_TAG, "Creating db.");
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        FavoriteMoviesDB.class, FavoriteMoviesDB.DATABASE_NAME)
                        .build();
            }
        }
        Log.d(LOG_TAG, "Getting db instance.");
        return sInstance;
    }

    public abstract MovieDAO movieDAO();
}
