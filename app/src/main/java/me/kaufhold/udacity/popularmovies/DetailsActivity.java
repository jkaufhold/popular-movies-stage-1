package me.kaufhold.udacity.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.TimeZone;

import me.kaufhold.udacity.popularmovies.adapters.MoviesListAdapter;
import me.kaufhold.udacity.popularmovies.model.Movie;

public class DetailsActivity extends AppCompatActivity {
    private static final String TAG = MoviesListAdapter.class.getSimpleName();

    private static final String MOVIE_ARGUMENT = "movie";

    private ImageView imageView;
    private TextView tvTitle;
    private TextView tvReleaseDate;
    private TextView tvPopularity;
    private TextView tvOverview;

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

        if(bundle.containsKey(MOVIE_ARGUMENT)) {
            populateUI(bundle.getParcelable(MOVIE_ARGUMENT));
        }
    }

    private void populateUI(Movie movie) {
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
    }

    private void closeOnError() {
        Toast.makeText(this, R.string.error_message, Toast.LENGTH_LONG).show();
    }

    private void closeOnLoadError() {
        Toast.makeText(this, R.string.error_image_loading_message, Toast.LENGTH_LONG).show();
    }
}