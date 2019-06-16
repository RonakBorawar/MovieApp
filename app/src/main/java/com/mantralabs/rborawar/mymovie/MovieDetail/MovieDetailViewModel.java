package com.mantralabs.rborawar.mymovie.MovieDetail;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.mantralabs.rborawar.mymovie.database.MovieDatabase;
import com.mantralabs.rborawar.mymovie.model.Movie;

public class MovieDetailViewModel extends ViewModel {

    private LiveData<Movie> mMovie;

    public MovieDetailViewModel(MovieDatabase database, int movieId) {
        mMovie = database.movieDao().getMovieById(movieId);
    }

    public LiveData<Movie> getMovie() {
        return mMovie;
    }

}