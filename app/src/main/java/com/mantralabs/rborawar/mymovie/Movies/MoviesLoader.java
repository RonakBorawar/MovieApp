package com.mantralabs.rborawar.mymovie.Movies;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.mantralabs.rborawar.mymovie.model.Movie;
import com.mantralabs.rborawar.mymovie.webservice.ThemoviedbApiClient;

import java.util.List;

public class MoviesLoader extends AsyncTaskLoader<List<Movie>> {

    private String mSortByParam;

    public MoviesLoader(Context context, String sortByParam) {
        super(context);
        mSortByParam = sortByParam;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Movie> loadInBackground() {
        return ThemoviedbApiClient.getAllMovies(mSortByParam);
    }
}
