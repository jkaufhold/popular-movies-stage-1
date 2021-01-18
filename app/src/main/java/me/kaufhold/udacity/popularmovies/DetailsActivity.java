package me.kaufhold.udacity.popularmovies;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DividerItemDecoration;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

import me.kaufhold.udacity.popularmovies.adapters.PageLoader;
import me.kaufhold.udacity.popularmovies.adapters.ReviewsListAdapter;
import me.kaufhold.udacity.popularmovies.adapters.TrailersListAdapter;
import me.kaufhold.udacity.popularmovies.databinding.ActivityDetailsBinding;
import me.kaufhold.udacity.popularmovies.db.FavoriteMoviesDB;
import me.kaufhold.udacity.popularmovies.model.Movie;
import me.kaufhold.udacity.popularmovies.model.ReviewsResultPage;
import me.kaufhold.udacity.popularmovies.model.TrailersResultPage;
import me.kaufhold.udacity.popularmovies.rest.RetrofitFactory;
import me.kaufhold.udacity.popularmovies.rest.TheMovieDatabaseApi;
import retrofit2.Call;
import retrofit2.Response;

public class DetailsActivity extends AppCompatActivity implements PageLoader {
    private static final String TAG = DetailsActivity.class.getSimpleName();
    private static final String IS_FAVORITE = "is_favorite";
    private static final String MOVIE = "movie";
    public static final String TRAILERS = "trailers";
    public static final String REVIEWS = "reviews";
    public static final String PAGE = "page";
    public static final String MAX_PAGE = "maxPage";

    private ActivityDetailsBinding activityDetailsBinding;

    private Integer loadingPage = null;
    private Movie movie;
    private TrailersListAdapter adapter;
    private ReviewsListAdapter reviews_adapter;
    private FavoriteMoviesDB moviesDB;
    boolean isFavoriteMovie = false;

    public static Intent newIntent(Context context, Movie movie) {
        Intent intent = new Intent(context, DetailsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(MOVIE, movie);
        intent.putExtras(bundle);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityDetailsBinding = DataBindingUtil.setContentView(this, R.layout.activity_details);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(this.getIntent() == null) {
            closeOnError();
        }
        Bundle bundle = this.getIntent().getExtras();
        if(bundle == null) {
            closeOnError();
            return;
        }

        moviesDB = FavoriteMoviesDB.getInstance(this.getApplicationContext());
        if(bundle.containsKey(MOVIE)) {
            movie = bundle.getParcelable(MOVIE);
            if (movie == null) {
                closeOnError();
                return;
            }
        }

        if(savedInstanceState != null && savedInstanceState.containsKey(IS_FAVORITE)) {
            isFavoriteMovie = savedInstanceState.getBoolean(IS_FAVORITE);
            populateFavoriteButton();
        } else {
            Thread thread = new Thread(() -> {
                me.kaufhold.udacity.popularmovies.db.Movie selectResult = moviesDB.movieDAO().loadMovieWith(movie.getId().toString());
                if (selectResult != null) {
                    isFavoriteMovie = true;
                }
                populateFavoriteButton();
            });
            thread.start();
        }

        populateUI(savedInstanceState);
    }

    private void populateUI(Bundle savedInstanceState) {
        if(movie == null) {
            if(savedInstanceState != null && savedInstanceState.containsKey(MOVIE)) {
                movie = savedInstanceState.getParcelable(MOVIE);
                if(movie == null){
                    closeOnError();
                    return;
                }
            } else {
                closeOnError();
                return;
            }
        }
        String posterPath = movie.getPosterPath();
        if(posterPath != null){
            Picasso.get().load(getString(R.string.image_base_url) + posterPath)
                    .into(activityDetailsBinding.movieImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            Log.d(TAG, "Image loaded");
                        }

                        @Override
                        public void onError(Exception e) {
                            closeOnLoadError();
                            Log.e(TAG, "ERROR while loading the image");
                        }
                    });
        }
        activityDetailsBinding.movieDetailsTitle.setText(movie.getTitle());
        activityDetailsBinding.releaseDate.setText(movie.getReleaseDate());
        activityDetailsBinding.moviePopularity.setText(getString(R.string.popularity_format, movie.getPopularity()));
        activityDetailsBinding.movieOverview.setText(movie.getOverview());

        this.adapter = new TrailersListAdapter(this);
        activityDetailsBinding.rvMoviesTrailers.setAdapter(adapter);
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(activityDetailsBinding.rvMoviesTrailers.getContext(),
                DividerItemDecoration.VERTICAL);
        activityDetailsBinding.rvMoviesTrailers.addItemDecoration(mDividerItemDecoration);
        activityDetailsBinding.rvMoviesTrailers.setNestedScrollingEnabled(false);

        this.reviews_adapter = new ReviewsListAdapter(this);
        activityDetailsBinding.rvMoviesReviews.setAdapter(reviews_adapter);
        DividerItemDecoration mDivider = new DividerItemDecoration(activityDetailsBinding.rvMoviesReviews.getContext(),
                DividerItemDecoration.VERTICAL);
        activityDetailsBinding.rvMoviesReviews.addItemDecoration(mDivider);
        activityDetailsBinding.rvMoviesReviews.setNestedScrollingEnabled(false);

        if(savedInstanceState == null || !savedInstanceState.containsKey(TRAILERS)) {
            adapter.init(new ArrayList<>());
            new DetailsActivity.LoadTrailersListTask().execute();
            reviews_adapter.init(new ArrayList<>(), 0, Integer.MAX_VALUE, this);
            loadPage(1);
        } else {
            adapter.init(savedInstanceState.getParcelableArrayList(TRAILERS));
            reviews_adapter.init(savedInstanceState.getParcelableArrayList(REVIEWS), savedInstanceState.getInt(PAGE), savedInstanceState.getInt(MAX_PAGE), this);
        }
    }

    private void populateFavoriteButton() {
        if (isFavoriteMovie) {
            activityDetailsBinding.favoriteButton.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), android.R.drawable.btn_star_big_on));
        } else {
            activityDetailsBinding.favoriteButton.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), android.R.drawable.btn_star_big_off));
        }
        me.kaufhold.udacity.popularmovies.db.Movie dbMovie = new me.kaufhold.udacity.popularmovies.db.Movie(movie.getId().toString(), movie.getPosterPath(), movie.getTitle(), movie.getPopularity(), movie.getOverview(), movie.getReleaseDate());
        activityDetailsBinding.favoriteButton.setOnClickListener(view -> {
            if (isFavoriteMovie) {
                activityDetailsBinding.favoriteButton.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), android.R.drawable.btn_star_big_off));
                Thread thread = new Thread(() -> moviesDB.movieDAO().deleteMovie(dbMovie));
                thread.start();
            } else {
                activityDetailsBinding.favoriteButton.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), android.R.drawable.btn_star_big_on));
                Thread thread = new Thread(() -> moviesDB.movieDAO().insertMovie(dbMovie));
                thread.start();
            }
            isFavoriteMovie = !isFavoriteMovie;
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        outState.putBoolean(IS_FAVORITE, isFavoriteMovie);
        outState.putParcelable(MOVIE, movie);
        outState.putParcelableArrayList(TRAILERS, adapter.createTrailersArray());
        outState.putParcelableArrayList(REVIEWS, reviews_adapter.createReviewsArray());
        super.onSaveInstanceState(outState, outPersistentState);
    }
    

    @SuppressLint("StaticFieldLeak")
    public class LoadTrailersListTask extends AsyncTask<Void, Void, TrailersResultPage> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            activityDetailsBinding.detailsProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected TrailersResultPage doInBackground(Void... voids) {
            TrailersResultPage page = null;
            TheMovieDatabaseApi api = RetrofitFactory.createApi();
            Call<TrailersResultPage> trailers = api.loadMovieTrailers(movie.getId().toString());
            if(trailers == null) {
                return null;
            }
            try {
                Response<TrailersResultPage> apiResponse = trailers.execute();
                if(apiResponse.isSuccessful()) {
                    page = apiResponse.body();
                }
            } catch (IOException e) {
                Log.e(TAG, "Request trailers list failed.", e);
            }
            return page;
        }

        @Override
        protected void onPostExecute(TrailersResultPage page) {
            activityDetailsBinding.detailsProgressBar.setVisibility(View.GONE);
            if (page == null) {
                closeOnError();
                return;
            }
            adapter.addTrailers(page.getResults());
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class LoadReviewsTask extends AsyncTask<Void, Void, ReviewsResultPage> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            activityDetailsBinding.detailsProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected ReviewsResultPage doInBackground(Void... voids) {
            ReviewsResultPage page = null;
            TheMovieDatabaseApi api = RetrofitFactory.createApi();
            Call<ReviewsResultPage> reviews = api.loadMovieReviews(movie.getId().toString(), loadingPage);
            if(reviews == null) {
                return null;
            }
            try {
                Response<ReviewsResultPage> apiResponse = reviews.execute();
                if(apiResponse.isSuccessful()) {
                    page = apiResponse.body();
                }
            } catch (IOException e) {
                Log.e(TAG, "Request movie list failed.", e);
            }
            return page;
        }

        @Override
        protected void onPostExecute(ReviewsResultPage page) {
            activityDetailsBinding.detailsProgressBar.setVisibility(View.GONE);
            if (page == null) {
                closeOnError();
                return;
            } else {
                if(page.getResults() != null && page.getResults().size() == 0) {
                    activityDetailsBinding.reviewsLabel.setVisibility(View.GONE);
                }
            }
            reviews_adapter.addReviewPage(page);
        }
    }

    @Override
    public void loadNextPage(int page) {
        loadPage(page + 1);
    }

    public void loadPage(int page) {
        if(loadingPage == null) {
            loadingPage = page;
            new LoadReviewsTask().execute();
        }
    }

    private void closeOnError() {
        Toast.makeText(this, R.string.no_trailers_error_message, Toast.LENGTH_LONG).show();
    }

    private void closeOnLoadError() {
        Toast.makeText(this, R.string.error_image_loading_message, Toast.LENGTH_LONG).show();
    }
}