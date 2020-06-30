package me.kaufhold.udacity.popularmovies;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;

import me.kaufhold.udacity.popularmovies.adapters.MoviePageLoader;
import me.kaufhold.udacity.popularmovies.adapters.MoviesListAdapter;
import me.kaufhold.udacity.popularmovies.model.MovieResultPage;
import me.kaufhold.udacity.popularmovies.rest.RetrofitFactory;
import me.kaufhold.udacity.popularmovies.rest.TheMovieDatabaseApi;
import retrofit2.Call;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements MoviePageLoader {
    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String MOVIES = "movies";
    public static final String PAGE = "page";
    public static final String MAX_PAGE = "maxPage";

    private ProgressBar progressBar;
    private SharedPreferences sharedPreferences;
    private MoviesListAdapter adapter;
    private Integer loadingPage = null;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        progressBar = findViewById(R.id.progress_bar);

        RecyclerView moviesRecyclerView = findViewById(R.id.rv_movies_list);
        moviesRecyclerView.setHasFixedSize(true);
        this.adapter = new MoviesListAdapter(this.getApplicationContext());
        moviesRecyclerView.setAdapter(adapter);
        if(savedInstanceState == null || !savedInstanceState.containsKey(MOVIES)) {
            loadPage(1);
            adapter.init(new ArrayList<>(), 0, Integer.MAX_VALUE, this);
        } else {
            adapter.init(savedInstanceState.getParcelableArrayList(MOVIES), savedInstanceState.getInt(PAGE), savedInstanceState.getInt(MAX_PAGE), this);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        outState.putParcelableArrayList(MOVIES, adapter.createMoviesArray());
        super.onSaveInstanceState(outState, outPersistentState);
    }

    private void closeOnError() {
        Toast.makeText(this, R.string.error_message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void loadNextPage(int page) {
        loadPage(page + 1);
    }

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
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected MovieResultPage doInBackground(Void... voids) {
            MovieResultPage page = null;
            TheMovieDatabaseApi api = RetrofitFactory.createApi();
            String default_sort_order = getString(R.string.sort_order_default);
            String sortOrder = sharedPreferences.getString(getString(R.string.sort_order_key), default_sort_order);
            Call<MovieResultPage> movies;
            if(sortOrder.equals(default_sort_order)) {
                movies = api.loadPopularMovies(loadingPage);
            } else {
                movies = api.loadTopRated(loadingPage);
            }
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

        @Override
        protected void onPostExecute(MovieResultPage page) {
            loadingPage = null;
            progressBar.setVisibility(View.GONE);
            if (page == null) {
                closeOnError();
                return;
            }
            adapter.addMoviePage(page);
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
}