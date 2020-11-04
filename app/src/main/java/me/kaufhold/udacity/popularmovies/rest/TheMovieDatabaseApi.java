package me.kaufhold.udacity.popularmovies.rest;

import me.kaufhold.udacity.popularmovies.model.MovieResultPage;
import me.kaufhold.udacity.popularmovies.model.TrailersResultPage;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TheMovieDatabaseApi {
    String POPULAR = "popular";
    String TOP_RATED = "top_rated";

    @GET("movie/{type}")
    Call<MovieResultPage> loadMovies(@Path("type") String type, @Query("page") Integer page);

    @GET("movie/{id}/videos")
    Call<TrailersResultPage> loadMovieTrailers(@Path("id") String id);
}
