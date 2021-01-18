package me.kaufhold.udacity.popularmovies.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.LinkedList;
import java.util.List;

import me.kaufhold.udacity.popularmovies.DetailsActivity;
import me.kaufhold.udacity.popularmovies.R;
import me.kaufhold.udacity.popularmovies.model.Movie;
import me.kaufhold.udacity.popularmovies.model.MovieResultPage;

public class MoviesListAdapter extends RecyclerView.Adapter<MoviesListAdapter.MoviesListHolder> {
    private static final String TAG = MoviesListAdapter.class.getSimpleName();
    private Context context;
    private List<Movie> movies = new LinkedList<>();
    private int currentPage = -1;
    private int maxPage = Integer.MAX_VALUE;
    private PageLoader pageLoader;

    public MoviesListAdapter(Context applicationContext) {
        this.context = applicationContext;
    }

    public void init(List<Movie> movies, int currentPage, int maxPage, PageLoader pageLoader) {
        this.movies = movies;
        this.currentPage = currentPage;
        this.maxPage = maxPage;
        this.pageLoader = pageLoader;
    }

    @NonNull
    @Override
    public MoviesListAdapter.MoviesListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(this.context);
        View movie_item_view = inflater.inflate(R.layout.movies_item, parent, false);
        return new MoviesListHolder(movie_item_view);
    }

    @Override
    public void onBindViewHolder(@NonNull MoviesListAdapter.MoviesListHolder holder, int position) {
        if(movies == null){
            return;
        }
        if(position == movies.size() - 1 && currentPage > 0 && currentPage + 1 <= maxPage) {
            pageLoader.loadNextPage(currentPage);
        }
        Movie movie = movies.get(position);
        if(movie == null){
            return;
        }
        holder.setMovie(movie);
        if(movie.getPosterPath() != null) {
            Picasso.get().load(context.getString(R.string.image_base_url) + movie.getPosterPath())
                    .into(holder.movie_item_view, new Callback() {
                        @Override
                        public void onSuccess() {
                            Log.d(TAG, "Image loaded");
                        }
                        @Override
                        public void onError(Exception e) {
                            closeOnLoadError();
                            Log.e(TAG, "ERROR while loading the image", e);
                        }
                    });
        }
    }

    @Override
    public int getItemCount() {
        int count = 0;
        if(movies != null) {
            count = this.movies.size();
        }
        return count;
    }

    public void addMoviePage(MovieResultPage page) {
        if(page == null) {
            closeOnError();
            return;
        }
        currentPage = page.getPage();
        maxPage = page.getTotalPages();
        addMovies(page.getResults());
    }

    public class MoviesListHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected ImageView movie_item_view;
        private Movie movie;

        public MoviesListHolder(@NonNull View item_view) {
            super(item_view);
            movie_item_view = item_view.findViewById(R.id.iv_movie);
            movie_item_view.setOnClickListener(this);
        }

        @Override
        public void onClick(View movie_image_view) {
            context.startActivity(DetailsActivity.newIntent(context, movie));
        }

        public void setMovie(Movie movie) {
            this.movie = movie;
        }
    }

    public void addMovies(List<Movie> movies) {
        if(this.movies == null){
            closeOnError();
            return;
        }
        this.movies.addAll(movies);
        notifyDataSetChanged();
    }

    private void closeOnError() {
        Toast.makeText(this.context, R.string.no_movies_error_message, Toast.LENGTH_LONG).show();
    }

    private void closeOnLoadError() {
        Toast.makeText(this.context, R.string.error_image_loading_message, Toast.LENGTH_LONG).show();
    }
}
