package me.kaufhold.udacity.popularmovies.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;

import java.util.Date;

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
    private Date releaseDate;

    public Movie(@NonNull String id, String posterPath, String title, Float popularity, String overview, Date releaseDate) {
        this.id = id;
        this.posterPath = posterPath;
        this.title = title;
        this.popularity = popularity;
        this.overview = overview;
        // long tmpReleaseDate = releaseDate;
        // this.releaseDate = tmpReleaseDate == -1 ? null : new Date(tmpReleaseDate);
        this.releaseDate = releaseDate;
    }

    @NonNull
    public String getId() {
        return id;
    }

    @NonNull
    public void setId(String id) {
        this.id = id;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Float getPopularity() {
        return popularity;
    }

    public void setPopularity(Float popularity) {
        this.popularity = popularity;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }
}
