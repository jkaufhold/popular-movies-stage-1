package me.kaufhold.udacity.popularmovies;

import android.app.Application;

import me.kaufhold.udacity.popularmovies.rest.RetrofitFactory;

public class PopularMoviesApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        RetrofitFactory.initialize(this, "en-US");
    }
}
