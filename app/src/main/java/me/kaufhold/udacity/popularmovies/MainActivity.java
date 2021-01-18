package me.kaufhold.udacity.popularmovies;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import me.kaufhold.udacity.popularmovies.adapters.PageLoader;
import me.kaufhold.udacity.popularmovies.adapters.MoviesListAdapter;
import me.kaufhold.udacity.popularmovies.databinding.ActivityMainBinding;
import me.kaufhold.udacity.popularmovies.db.FavoriteMoviesDB;
import me.kaufhold.udacity.popularmovies.model.Movie;
import me.kaufhold.udacity.popularmovies.model.MovieResultPage;
import me.kaufhold.udacity.popularmovies.rest.RetrofitFactory;
import me.kaufhold.udacity.popularmovies.rest.TheMovieDatabaseApi;
import retrofit2.Call;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements PageLoader {
    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String MOVIES_PAGE = "movies_page";

    private SharedPreferences sharedPreferences;
    private MoviesListAdapter adapter;
    private Integer loadingPage = null;

    private MovieResultPage page;
    private ArrayList<Movie> dbMovies;
    private FavoriteMoviesDB moviesDB;

    private ActivityMainBinding activityMainBinding;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        moviesDB = FavoriteMoviesDB.getInstance(this.getApplicationContext());
        LiveData<List<me.kaufhold.udacity.popularmovies.db.Movie>> rawDBMovies = moviesDB.movieDAO().loadMovies();
        rawDBMovies.observe(this, dbMoviesList -> dbMovies = dbMoviesList.stream().map(me.kaufhold.udacity.popularmovies.db.Movie::toModelMovie).collect(Collectors.toCollection(ArrayList::new)));

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        RecyclerView moviesRecyclerView = findViewById(R.id.rv_movies_list);
        moviesRecyclerView.setHasFixedSize(true);
        this.adapter = new MoviesListAdapter(this);
        moviesRecyclerView.setAdapter(adapter);
        if(savedInstanceState == null || !savedInstanceState.containsKey(MOVIES_PAGE)) {
            adapter.init(new ArrayList<>(), 0, TheMovieDatabaseApi.DEFAULT_MAX_MOVIES_PAGES_COUNT, this);
            loadPage(1);
        } else {
            page = savedInstanceState.getParcelable(MOVIES_PAGE);
            if(page == null){
                closeOnError();
                return;
            }
            adapter.init(page.getResults(), page.getPage(), page.getTotalPages(), this);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable(MOVIES_PAGE, page);
        super.onSaveInstanceState(outState);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void loadNextPage(int page) {
        loadPage(page + 1);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void loadPage(int page) {
        if(loadingPage == null) {
            loadingPage = page;
            new LoadMovieListTask().execute();
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class LoadMovieListTask extends AsyncTask<Void, Void, MovieResultPage> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Objects.requireNonNull(activityMainBinding.progressBar).setVisibility(View.VISIBLE);
        }

        @Override
        protected MovieResultPage doInBackground(Void... voids) {
            String sortOrder = sharedPreferences.getString(getString(R.string.sort_order_key),
                                                           getString(R.string.sort_order_default));
            String favoritesSortOrder = getString(R.string.sort_order_favorites);
            if(sortOrder.equals(favoritesSortOrder)) {
                page = getMovieResultPageFromDB();
                return page;
            }

            TheMovieDatabaseApi api = RetrofitFactory.createApi();
            Call<MovieResultPage> movies = api.loadMovies(sortOrder, loadingPage);
            if(movies == null) {
                return null;
            }
            try {
                Response<MovieResultPage> apiResponse = movies.execute();
                if(apiResponse.isSuccessful()) {
                    page = apiResponse.body();
                }
            } catch (IOException e) {
                Log.e(TAG, "Request movie list failed.", e);
            }
            return page;
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected void onPostExecute(MovieResultPage resultPage) {
            Objects.requireNonNull(activityMainBinding.progressBar).setVisibility(View.GONE);
            loadingPage = null;
            if (resultPage == null) {
                page = getMovieResultPageFromDB();
            } else {
                page = resultPage;
            }
            adapter.addMoviePage(page);
        }

        private MovieResultPage getMovieResultPageFromDB(){
            if(dbMovies == null){
                List<me.kaufhold.udacity.popularmovies.db.Movie> rawDBMovies = moviesDB.movieDAO().loadMoviesList();
                if(rawDBMovies == null || rawDBMovies.isEmpty()) {
                    return null;
                }
                dbMovies = rawDBMovies.stream().map(me.kaufhold.udacity.popularmovies.db.Movie::toModelMovie).collect(Collectors.toCollection(ArrayList::new));
            }
            int totalPages = (dbMovies.size()/TheMovieDatabaseApi.MAX_MOVIES_COUNT_PER_PAGE)+1;
            return new MovieResultPage(1, totalPages, dbMovies);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void closeOnError() {
        Toast.makeText(this, R.string.no_movies_error_message, Toast.LENGTH_LONG).show();
    }
}