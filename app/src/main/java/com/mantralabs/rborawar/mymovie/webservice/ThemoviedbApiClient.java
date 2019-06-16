package com.mantralabs.rborawar.mymovie.webservice;

import android.net.Uri;

import com.mantralabs.rborawar.mymovie.model.Movie;

import java.util.List;

public class ThemoviedbApiClient {

    private static final String LOG_TAG = ThemoviedbApiClient.class.getSimpleName();

    private static final String MOVIE_API_KEY = "fa8bc645d0714c33bf8c9f82933e35a8";
    private static final String REQUEST_URL = "https://api.themoviedb.org/3/movie/";

    public static List<Movie> getAllMovies(String sortBy) {
        Uri.Builder uri = createMovieRequestUri(sortBy);
        String jsonResponse = NetworkUtils.getJsonResponse(uri);
        return ThemoviedbApiUtils.extractMovieFeatureFromJSON(jsonResponse);
    }

    public static Uri.Builder createMovieRequestUri(String sortBy) {

        Uri baseUri = Uri.parse(REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendPath(sortBy);
        uriBuilder.appendQueryParameter("api_key", MOVIE_API_KEY);

        return uriBuilder;
    }
}
