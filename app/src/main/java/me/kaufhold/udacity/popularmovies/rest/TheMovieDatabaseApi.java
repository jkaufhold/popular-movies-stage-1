package me.kaufhold.udacity.popularmovies.rest;

import me.kaufhold.udacity.popularmovies.model.MovieResultPage;
import me.kaufhold.udacity.popularmovies.model.ReviewsResultPage;
import me.kaufhold.udacity.popularmovies.model.TrailersResultPage;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TheMovieDatabaseApi {
    int MAX_MOVIES_COUNT_PER_PAGE = 20;
    int DEFAULT_MAX_MOVIES_PAGES_COUNT = 1000;

    @GET("movie/{type}")
    Call<MovieResultPage> loadMovies(@Path("type") String type, @Query("page") Integer page); //, @Query("region") String country_code

    @GET("movie/{id}/videos")
    Call<TrailersResultPage> loadMovieTrailers(@Path("id") String id);

    @GET("movie/{id}/reviews")
    Call<ReviewsResultPage> loadMovieReviews(@Path("id") String id, @Query("page") Integer page);
}
