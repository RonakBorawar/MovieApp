package com.mantralabs.rborawar.mymovie.Movies;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.mantralabs.rborawar.mymovie.database.MovieDatabase;
import com.mantralabs.rborawar.mymovie.model.Movie;

import java.util.List;

public class MoviesViewModel extends AndroidViewModel {

    private LiveData<List<Movie>> mAllFavoriteMovies;

    public MoviesViewModel(@NonNull Application application) {
        super(application);

        mAllFavoriteMovies = MovieDatabase.getInstance(application)
                .movieDao().getAllMovies();

    }

    public LiveData<List<Movie>> getAllFavoriteMovies() {
        return mAllFavoriteMovies;
    }
}
