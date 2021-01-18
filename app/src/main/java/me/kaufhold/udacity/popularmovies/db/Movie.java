package me.kaufhold.udacity.popularmovies.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Movie {
    @PrimaryKey
    @NonNull
    private String id;
    @ColumnInfo(name="poster_path")
    private String posterPath;
    private String title;
    private Float popularity;
    private String overview;
    @ColumnInfo(name="release_date")
    private String releaseDate;

    public Movie(@NonNull String id, String posterPath, String title, Float popularity, String overview, String releaseDate) {
        this.id = id;
        this.posterPath = posterPath;
        this.title = title;
        this.popularity = popularity;
        this.overview = overview;
        this.releaseDate = releaseDate;
    }

    public me.kaufhold.udacity.popularmovies.model.Movie toModelMovie() {
        me.kaufhold.udacity.popularmovies.model.Movie movie = new me.kaufhold.udacity.popularmovies.model.Movie();
        movie.setId(Integer.valueOf(this.id));
        movie.setPosterPath(this.posterPath);
        movie.setTitle(this.title);
        movie.setPopularity(this.popularity);
        movie.setOverview(this.overview);
        movie.setReleaseDate(this.releaseDate);
        return movie;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getTitle() {
        return title;
    }

    public Float getPopularity() {
        return popularity;
    }

    public String getOverview() {
        return overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }
}
