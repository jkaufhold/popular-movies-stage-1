package me.kaufhold.udacity.popularmovies.db;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

@Database(entities = {Movie.class}, version = 1, exportSchema = false)
@TypeConverters(DateConverter.class)
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

    @Override
    protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration config) {
        // TODO finish implementation
        return null;
    }

    @Override
    protected InvalidationTracker createInvalidationTracker() {
        // TODO finish implementation
        return null;
    }

    @Override
    public void clearAllTables() {

    }
}
