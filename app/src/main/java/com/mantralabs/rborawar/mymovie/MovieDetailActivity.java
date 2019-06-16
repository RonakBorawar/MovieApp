package com.mantralabs.rborawar.mymovie;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.mantralabs.rborawar.mymovie.MovieDetail.AppExecutors;
import com.mantralabs.rborawar.mymovie.MovieDetail.MovieDetailViewModel;
import com.mantralabs.rborawar.mymovie.MovieDetail.MovieDetailViewModelFactory;
import com.mantralabs.rborawar.mymovie.database.MovieDatabase;
import com.mantralabs.rborawar.mymovie.model.Movie;
import com.squareup.picasso.Picasso;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MovieDetailActivity extends AppCompatActivity {

    private static final String LOG_TAG = MovieDetailActivity.class.getSimpleName();

    protected static final String MOVIE_BUNDLE_KEY = "MOVIE_KEY";

    private static final String MOVIE_STATE_KEY = "MOVIE_STATE_KEY";
    private static final String SCROLL_POSITION_KEY = "SCROLL_POSITION_KEY";

    private Boolean mIsFavorite;
    private static int[] scrollPositions;

    @BindView(R.id.iv_movie_backdrop) ImageView mBackdrop;
    @BindView(R.id.iv_movie_poster_thumbnail) ImageView mPosterThumbnail;
    @BindView(R.id.tv_rating) TextView mMovieRating;
    @BindView(R.id.tv_date_released) TextView mDateReleased;
    @BindView(R.id.tv_synopsis) TextView mSinopsys;
    @BindView(R.id.fab_favorites) FloatingActionButton mAddFavoritesButton;
    @BindView(R.id.sv_movie_detail) ScrollView mScrollView;

    private Movie mMovie;

    private MovieDatabase mDb;

    private AppExecutors mTask;
    private MovieDetailViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        ButterKnife.bind(this);

        mTask = AppExecutors.getInstance();
        mDb = MovieDatabase.getInstance(getApplicationContext());

        if(savedInstanceState != null) {
            mMovie = savedInstanceState.getParcelable(MOVIE_BUNDLE_KEY);
            mIsFavorite = savedInstanceState.getBoolean(MOVIE_STATE_KEY);
        }
        else {
            mMovie = getIntent().getParcelableExtra(MOVIE_BUNDLE_KEY);
            mIsFavorite = false;
        }

        initViewModel();
        setColorFavoriteButton();
        populateMovieUI();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(MOVIE_BUNDLE_KEY, mMovie);
        outState.putBoolean(MOVIE_STATE_KEY, mIsFavorite);

        scrollPositions = new int[] {mScrollView.getScrollX(), mScrollView.getScrollX()};
        outState.putIntArray(SCROLL_POSITION_KEY, scrollPositions);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mMovie = savedInstanceState.getParcelable(MOVIE_BUNDLE_KEY);
        mIsFavorite = savedInstanceState.getBoolean(MOVIE_STATE_KEY);
        scrollPositions = savedInstanceState.getIntArray(SCROLL_POSITION_KEY);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void populateMovieUI(){

        setTitle(mMovie.getTitle());
        Picasso.with(this).load(mMovie.getBackdropPath()).into(mBackdrop);
        Picasso.with(this).load(mMovie.getPosterPath()).into(mPosterThumbnail);
        mMovieRating.setText(Double.toString(mMovie.getRating()) + "/10");
        mDateReleased.setText(mMovie.getReleaseDate().split("-")[0]);
        mSinopsys.setText(mMovie.getSynopsis());
    }

    private void initViewModel() {

        if (mMovie == null) return;

        MovieDetailViewModelFactory factory =
                new MovieDetailViewModelFactory(MovieDatabase.getInstance(MovieDetailActivity.this), mMovie.getMovieId());
        mViewModel = ViewModelProviders.of(this, factory).get(MovieDetailViewModel.class);
        mViewModel.getMovie().observe(this, new Observer<Movie>() {
            @Override
            public void onChanged(@Nullable Movie movie) {
                if(movie != null) {
                    mIsFavorite = true;
                    setColorFavoriteButton();
                }
            }
        });
    }

    @OnClick(R.id.fab_favorites)
    public void addOrRemoveFavorites() {

        AppExecutors task = AppExecutors.getInstance();

        if (mIsFavorite) {
            mIsFavorite = false;
            setColorFavoriteButton();

            task.diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    mDb.movieDao().delete(mMovie);
                }
            });
            Toast.makeText(MovieDetailActivity.this,
                    "Successfully removed from favorites", Toast.LENGTH_SHORT).show();
        }
        else {
            mIsFavorite = true;
            setColorFavoriteButton();

            task.diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    mDb.movieDao().insert(mMovie);
                }
            });
            Toast.makeText(MovieDetailActivity.this,
                    "Successfully added to favorites", Toast.LENGTH_SHORT).show();
        }
    }

    private void setColorFavoriteButton() {
        if (mIsFavorite) {
            mAddFavoritesButton.setColorFilter(ResourcesCompat.getColor(getResources(), R.color.redFavorite, null));
        }
        else {
            mAddFavoritesButton.setColorFilter(Color.WHITE);
            mAddFavoritesButton.setImageResource(R.drawable.ic_favorite_black_24dp);
        }
    }
}