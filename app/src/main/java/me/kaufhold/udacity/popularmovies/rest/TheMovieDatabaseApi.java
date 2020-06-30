package me.kaufhold.udacity.popularmovies.rest;

import me.kaufhold.udacity.popularmovies.model.MovieResultPage;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TheMovieDatabaseApi {
    @GET("movie/{type}")
    Call<MovieResultPage> loadMovies(@Path("type") String type, @Query("page") Integer page);

    default Call<MovieResultPage> loadPopularMovies(@Query("page") Integer page) {
        return loadMovies("popular", page);
    }

    default Call<MovieResultPage> loadTopRated(@Query("page") Integer page) {
        return loadMovies("top_rated", page);
    }

    default Call<MovieResultPage> loadPopularMovies() {
        return loadPopularMovies(1);
    }

    default Call<MovieResultPage> loadTopRated() {
        return loadTopRated(1);
    }
}
