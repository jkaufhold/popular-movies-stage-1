package me.kaufhold.udacity.popularmovies;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import me.kaufhold.udacity.popularmovies.adapters.TrailersListAdapter;
import me.kaufhold.udacity.popularmovies.model.Movie;
import me.kaufhold.udacity.popularmovies.model.TrailersResultPage;
import me.kaufhold.udacity.popularmovies.rest.RetrofitFactory;
import me.kaufhold.udacity.popularmovies.rest.TheMovieDatabaseApi;
import retrofit2.Call;
import retrofit2.Response;

public class DetailsActivity extends AppCompatActivity {
    private static final String TAG = DetailsActivity.class.getSimpleName();

    private static final String MOVIE_ARGUMENT = "movie";

    private ImageView imageView;
    private TextView tvTitle;
    private TextView tvReleaseDate;
    private TextView tvPopularity;
    private TextView tvOverview;
    private RecyclerView rvTrailers;
    private Movie movie;

    private ProgressBar progressBar;
    private TrailersListAdapter adapter;
    public static final String TRAILERS = "trailers";

    public static Intent newIntent(Context context, Movie movie) {
        Intent intent = new Intent(context, DetailsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(MOVIE_ARGUMENT, movie);
        intent.putExtras(bundle);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

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

        tvTitle = findViewById(R.id.movie_details_title);
        tvReleaseDate = findViewById(R.id.release_date);
        tvPopularity = findViewById(R.id.movie_popularity);
        tvOverview = findViewById(R.id.movie_overview);
        imageView = findViewById(R.id.image_iv);
        rvTrailers = findViewById(R.id.rv_movies_trailers);
        progressBar = findViewById(R.id.details_progress_bar);

        if(bundle.containsKey(MOVIE_ARGUMENT)) {
            movie = bundle.getParcelable(MOVIE_ARGUMENT);
            populateUI(movie, savedInstanceState);
        }
    }

    private void populateUI(Movie movie, Bundle savedInstanceState) {
        if(movie == null) {
            closeOnError();
            return;
        }
        String posterPath = movie.getPosterPath();
        if(posterPath != null){
            Picasso.get().load(getString(R.string.image_base_url) + posterPath)
                    .into(imageView, new Callback() {
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
        tvTitle.setText(movie.getTitle());
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Berlin"));
        cal.setTime(movie.getReleaseDate());
        int year = cal.get(Calendar.YEAR);
        tvReleaseDate.setText(String.valueOf(year));
        tvPopularity.setText(getString(R.string.popularity_format, movie.getPopularity()));
        tvOverview.setText(movie.getOverview());

        RecyclerView trailersRecyclerView = findViewById(R.id.rv_movies_trailers);
        this.adapter = new TrailersListAdapter(this);
        trailersRecyclerView.setAdapter(adapter);
        if(savedInstanceState == null || !savedInstanceState.containsKey(TRAILERS)) {
            adapter.init(new ArrayList<>());
            new DetailsActivity.LoadTrailersListTask().execute();
        } else {
            adapter.init(savedInstanceState.getParcelableArrayList(TRAILERS));
        }
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(trailersRecyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        trailersRecyclerView.addItemDecoration(mDividerItemDecoration);
        trailersRecyclerView.setNestedScrollingEnabled(false);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        outState.putParcelableArrayList(TRAILERS, adapter.createTrailersArray());
        super.onSaveInstanceState(outState, outPersistentState);
    }
    

    @SuppressLint("StaticFieldLeak")
    public class LoadTrailersListTask extends AsyncTask<Void, Void, TrailersResultPage> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected TrailersResultPage doInBackground(Void... voids) {
            TrailersResultPage page = null;
            TheMovieDatabaseApi api = RetrofitFactory.createApi();
            Call<TrailersResultPage> trailers = api.loadMovieTrailers(movie.getId());
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
            progressBar.setVisibility(View.GONE);
            if (page == null) {
                closeOnError();
                return;
            }
            adapter.addTrailers(page.getResults());
        }
    }

    private void closeOnError() {
        Toast.makeText(this, R.string.no_trailers_error_message, Toast.LENGTH_LONG).show();
    }

    private void closeOnLoadError() {
        Toast.makeText(this, R.string.error_image_loading_message, Toast.LENGTH_LONG).show();
    }
}