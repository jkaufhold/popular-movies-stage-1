package me.kaufhold.udacity.popularmovies.rest;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.Objects;

import me.kaufhold.udacity.popularmovies.R;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitFactory {
    private static Retrofit INSTANCE;

    public static void initialize(Context context, String language) {
        String apiKey = context.getResources().getString(R.string.themoviedatabase_api_key);

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder().addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request request = chain.request();

                HttpUrl url = request.url().newBuilder()
                        .addQueryParameter("api_key", apiKey)
                        .addQueryParameter("language", language)
                        .build();
                request = request.newBuilder().url(url).build();

                Request newRequest = request.newBuilder().url(url).build();
                return chain.proceed(newRequest);
            }
        }).build();

        INSTANCE = new Retrofit.Builder()
                .baseUrl(context.getString(R.string.tmdb_base_url))
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    public static <T> T create(Class<T> type) {
        return createInstance().create(type);
    }

    public static TheMovieDatabaseApi createApi() {
        return create(TheMovieDatabaseApi.class);
    }

    public static Retrofit createInstance() {
        return Objects.requireNonNull(INSTANCE, "RestService has not been initialized.");
    }
}
