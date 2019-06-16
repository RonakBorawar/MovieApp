package com.mantralabs.rborawar.mymovie;

import android.app.LoaderManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Configuration;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mantralabs.rborawar.mymovie.Movies.MoviesAdapter;
import com.mantralabs.rborawar.mymovie.Movies.MoviesLoader;
import com.mantralabs.rborawar.mymovie.Movies.MoviesViewModel;
import com.mantralabs.rborawar.mymovie.model.Movie;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.mantralabs.rborawar.mymovie.webservice.NetworkUtils.checkForNetworkStatus;

public class MoviesActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<Movie>>, MoviesAdapter.MovieAdapterOnClickHandler {

    private static final String LOG_TAG = MoviesActivity.class.getName();

    private int sortBy;
    private static final String MOVIE_LIST_STATE_KEY = "MOVIE_LIST_STATE";
    private static final String SORT_BY_KEY = "SORT_BY";
    private static Parcelable mMovieListState;

    private static final int MOVIE_LOADER_ID = 100;
    private static final String POPULAR_SORT = "popular";
    private static final String TOP_RATED_SORT = "top_rated";

    private static final int FAVORITES = R.id.action_favorites;
    private static final int POPULAR = R.id.action_sort_by_popular_movies;
    private static final int TOP_RATED = R.id.action_sort_by_top_rated;

    private GridLayoutManager mGridLayoutManager;
    private MoviesAdapter mMoviesAdapter;

    @BindView(R.id.rv_movie_list) RecyclerView mRecyclerView;
    @BindView(R.id.tv_empty_view) TextView mErrorMessageDisplay;
    @BindView(R.id.pb_loading_indicator) ProgressBar mLoadingIndicator;

    private MoviesViewModel mMovieViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);

        ButterKnife.bind(this);

        int spanCount = getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_LANDSCAPE ? 4 : 2;

        mGridLayoutManager = new GridLayoutManager(this, spanCount);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setItemViewCacheSize(20);
        mRecyclerView.setDrawingCacheEnabled(true);
        mRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mMoviesAdapter = new MoviesAdapter( this);
        mMoviesAdapter.setContext(getApplicationContext());

        mRecyclerView.setAdapter(mMoviesAdapter);

        if (savedInstanceState != null) {
            sortBy = savedInstanceState.getInt(SORT_BY_KEY);
        }
        else if(sortBy == 0) {
            sortBy = POPULAR;
        }

        mMovieViewModel = ViewModelProviders.of(this).get(MoviesViewModel.class);

        if (sortBy == FAVORITES) {
            initViewModel();
        }
        else {
            populateMovieList();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(SORT_BY_KEY, sortBy);

        mMovieListState = mGridLayoutManager.onSaveInstanceState();
        outState.putParcelable(MOVIE_LIST_STATE_KEY, mMovieListState);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        sortBy = savedInstanceState.getInt(SORT_BY_KEY);
        mMovieListState = savedInstanceState.getParcelable(MOVIE_LIST_STATE_KEY);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (sortBy == FAVORITES) {
            mLoadingIndicator.setVisibility(View.GONE);
            initViewModel();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mMovieListState != null) {
            mGridLayoutManager.onRestoreInstanceState(mMovieListState);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        sortBy = item.getItemId();
        switch (sortBy) {
            case POPULAR:
                break;
            case TOP_RATED:
                break;
            case FAVORITES:
                break;
        }

        resetAdapter();
        if (sortBy == FAVORITES) {
            initViewModel();
        } else {
            getLoaderManager().restartLoader(MOVIE_LOADER_ID, null, this);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(Movie movie) {
        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtra(MovieDetailActivity.MOVIE_BUNDLE_KEY, movie);
        startActivity(intent);

    }

    private void initViewModel() {
        mMovieViewModel.getAllFavoriteMovies().observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> movies) {

                mMoviesAdapter.setMovieList(movies);

                if (movies == null) {
                    showErrorMessage();
                }
                else if (movies.size() == 0) {
                    showEmptyState();
                }
                else {
                    displaySortMessage();
                }
            }
        });
    }

    private void populateMovieList() {

        LoaderManager.LoaderCallbacks<List<Movie>> callback = MoviesActivity.this;

        boolean isConnected = checkForNetworkStatus(this);
        if (isConnected) {
            getLoaderManager().initLoader(MOVIE_LOADER_ID, null, callback);
        }
        else {
            showErrorMessage();
        }

    }

    private void resetAdapter() {
        mMoviesAdapter.setMovieList(new ArrayList<Movie>());
        mRecyclerView.setAdapter(mMoviesAdapter);
    }

    @Override
    public Loader<List<Movie>> onCreateLoader(int i, Bundle bundle) {

        String sort = "";

        if(sortBy == POPULAR) {
            sort = POPULAR_SORT;
        }
        else if (sortBy == TOP_RATED) {
            sort = TOP_RATED_SORT;
        }

        return new MoviesLoader(this, sort);

    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> movies) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);

        if(sortBy != FAVORITES) {
            mMoviesAdapter.setMovieList(movies);
        }

        if(movies == null) {
            showErrorMessage();
        }
        else if (movies.size() == 0) {
            showEmptyState();
        }
        else {
            displayMovies();
            displaySortMessage();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {
        resetAdapter();
    }

    private void displayMovies() {
        mRecyclerView.setVisibility(View.VISIBLE);
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
    }

    private void displaySortMessage() {
        String msg = "";
        switch (sortBy) {
            case POPULAR:
                msg = "Popular Movies";
                break;
            case TOP_RATED:
                msg = "Top Rated Movies";
                break;
            case FAVORITES:
                msg = "Favorite Movies";
        }
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void showEmptyState() {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
        mErrorMessageDisplay.setText(R.string.no_movies);
    }

    private void showErrorMessage() {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
        mErrorMessageDisplay.setText(R.string.no_connection);
    }
}
