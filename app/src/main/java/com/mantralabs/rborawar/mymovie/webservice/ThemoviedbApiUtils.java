package com.mantralabs.rborawar.mymovie.webservice;

import android.text.TextUtils;
import android.util.Log;

import com.mantralabs.rborawar.mymovie.model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ThemoviedbApiUtils {

    private static final String LOG_TAG = ThemoviedbApiUtils.class.getSimpleName();

    private ThemoviedbApiUtils() {
    }

    public static List<Movie> extractMovieFeatureFromJSON(String movieJSON) {

        List<Movie> movies = new ArrayList<>();

        if(TextUtils.isEmpty(movieJSON)) return movies;


        try {
            JSONObject response = new JSONObject(movieJSON);
            JSONArray results = response.getJSONArray("results");

            for (int i = 0; i < results.length(); i++) {

                JSONObject movieJson = results.getJSONObject(i);

                int id = movieJson.getInt("id");
                String title = movieJson.getString("original_title");
                String posterPath = buildImagePath(movieJson.getString("poster_path"));
                String backDropPath = buildImagePath(movieJson.getString("backdrop_path"));
                String synopsis = movieJson.getString("overview");
                double rating = movieJson.getDouble("vote_average");
                String releaseDate = movieJson.getString("release_date");

                movies.add(new Movie(id, title, posterPath, backDropPath, synopsis, rating, releaseDate));

            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Unable to parse movie JSON response", e);
        }

        return movies;
    }

    private static String buildImagePath(String moviePath) {
        return "http://image.tmdb.org/t/p/" + "w500/" + moviePath;
    }
}
