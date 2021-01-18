package me.kaufhold.udacity.popularmovies.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MovieDAO {

    @Query("SELECT * FROM movie")
    LiveData<List<Movie>> loadMovies();

    @Query("SELECT * FROM movie")
    List<Movie> loadMoviesList();

    @Query("SELECT * FROM movie WHERE id = :movie_id")
    Movie loadMovieWith(String movie_id);

    @Insert
    void insertMovie(Movie movie);

    @Delete
    void deleteMovie(Movie movie);
}
