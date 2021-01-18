package me.kaufhold.udacity.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class MovieResultPage implements Parcelable {
    private Integer page;
    @SerializedName("total_pages")
    private Integer totalPages;
    private ArrayList<Movie> results;

    public MovieResultPage(Integer page, Integer totalPages, ArrayList<Movie> results) {
        this.page = page;
        this.totalPages = totalPages;
        this.results = results;
    }

    public MovieResultPage(Parcel in) {
        this.page = in.readInt();
        this.totalPages = in.readInt();
        this.results = in.readArrayList(Movie.class.getClassLoader());
    }

    public static final Creator<MovieResultPage> CREATOR = new Creator<MovieResultPage>() {
        @Override
        public MovieResultPage createFromParcel(Parcel in) {
            return new MovieResultPage(in);
        }

        @Override
        public MovieResultPage[] newArray(int size) {
            return new MovieResultPage[size];
        }
    };

    public Integer getPage() {
        return page;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public ArrayList<Movie> getResults() {
        return results;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (page == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(page);
        }
        if (totalPages == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(totalPages);
        }
        dest.writeTypedList(results);
    }
}
