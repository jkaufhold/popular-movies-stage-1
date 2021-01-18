package me.kaufhold.udacity.popularmovies.model;

import java.util.ArrayList;

public class TrailersResultPage {
    private String id;
    private ArrayList<Trailer> results;

    public String getId(){ return id; }
    public void setId(String id) {this.id = id; }
    public ArrayList<Trailer> getResults() {
        return results;
    }
}
