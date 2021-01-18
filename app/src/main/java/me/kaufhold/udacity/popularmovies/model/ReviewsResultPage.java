package me.kaufhold.udacity.popularmovies.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ReviewsResultPage {
    private Integer page;
    private ArrayList<Review> results;
    @SerializedName("total_pages")
    private Integer totalPages;
    @SerializedName("total_results")
    private Integer totalResults;

    public Integer getPage() {
        return page;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public ArrayList<Review> getResults() {
        return results;
    }
}
