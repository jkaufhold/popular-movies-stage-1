package me.kaufhold.udacity.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Movie implements Parcelable {
    private String id;
    @SerializedName("poster_path")
    private String posterPath;
    private String title;
    private Float popularity;
    private String overview;
    @SerializedName("release_date")
    private Date releaseDate;

    public Movie() {

    }

    protected Movie(Parcel in) {
        this.id = in.readString();
        this.posterPath = in.readString();
        this.title = in.readString();
        this.popularity = (Float) in.readValue(Float.class.getClassLoader());
        this.overview = in.readString();
        long tmpReleaseDate = in.readLong();
        this.releaseDate = tmpReleaseDate == -1 ? null : new Date(tmpReleaseDate);
    }

    public String getId() {
        return id;
    }

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.posterPath);
        dest.writeString(this.title);
        dest.writeValue(this.popularity);
        dest.writeString(this.overview);
        dest.writeLong(this.releaseDate != null ? this.releaseDate.getTime() : -1);
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
