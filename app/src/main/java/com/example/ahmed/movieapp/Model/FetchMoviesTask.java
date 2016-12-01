package com.example.ahmed.movieapp.Model;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ahmed.movieapp.DetailActivityFragment;
import com.example.ahmed.movieapp.MainActivityFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.PreferenceChangeListener;

/**
 * Created by Ahmed on 21/11/2016.
 */

public class FetchMoviesTask extends AsyncTask<String, Void, List<MoviesClass>> {
    IAsyncResponse IAsyncResponse;
    IVideosReviewsResponse IVideosReviewsResponse;
    static boolean topRated;
    static boolean mostPopular;
    static boolean favourite;
    public String Param;


    private final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w185/";
    ImageView img;
    TextView tv;

    public void setlistener(IAsyncResponse IAsyncResponse) {
        this.IAsyncResponse = IAsyncResponse;
    }

    public void setlistenerVideosReviews(IVideosReviewsResponse IvideosReviewsResponse) {
        this.IVideosReviewsResponse = IvideosReviewsResponse;
    }

    protected List<MoviesClass> doInBackground(String... params) {

        final String LOG_TAG = FetchMoviesTask.class.getSimpleName();
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String moviesJsonStr = null;


        final String API_key_value = "a61578705d9102588820549b4f02d691";
        try {
            String urlString = null;
            String urlString2 = null;
            Param = params[0];
            if (params[0].equals("")) {
                if (MainActivityFragment.prefs.getString("sortby", "popularity").equals("popularity")) {
                    urlString = "http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=" + API_key_value;
                } else if (MainActivityFragment.prefs.getString("sortby", "popularity").equals("rating")) {
                    urlString = "http://api.themoviedb.org/3/discover/movie?sort_by=vote_average.desc&vote_count.gte=500&api_key=" + API_key_value;
                }
            } else if (params[0].equals("videos")) {
                urlString = "http://api.themoviedb.org/3/movie/" + params[1] + "/videos?api_key=" + API_key_value;

            } else if (params[0].equals("reviews")) {
                urlString = "http://api.themoviedb.org/3/movie/" + params[1] + "/reviews?api_key=" + API_key_value;
            }
            Uri builtUri = Uri.parse(urlString)
                    .buildUpon()
                    .build();

            URL url = new URL(urlString);
            Log.v(LOG_TAG, "Built URI" + builtUri.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                moviesJsonStr = null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return null;

            }
            moviesJsonStr = buffer.toString();
            JSONObject jsonObject = new JSONObject();


        } catch (IOException e) {
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                }
            }
        }
        try {
            return getMoviesDataFromJson(moviesJsonStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<MoviesClass> movies) {
        if (movies != null) {
            if (Param.equals(""))
                IAsyncResponse.processFinish(movies);
            else if (Param.equals("videos"))
                IVideosReviewsResponse.onVideosReady(movies);
            else if (Param.equals("reviews"))
                IVideosReviewsResponse.onReviewsReady(movies);

        }
    }

    private List<MoviesClass> getMoviesDataFromJson(String moviesJsonStr)
            throws JSONException {

        ArrayList<MoviesClass> movies = new ArrayList<MoviesClass>();
        final String RESULTS = "results";
        JSONObject MoviesJson = new JSONObject(moviesJsonStr);
        mostPopular = MainActivityFragment.prefs.getString("sortby", "popularity").equals("popularity");
        topRated = MainActivityFragment.prefs.getString("sortby", "popularity").equals("rating");
        favourite = MainActivityFragment.prefs.getString("sortby", "popularity").equals("favorites");
        final String ID = "id";
        final String POSTER_PATH = "poster_path";
        final String ORIGINAL_TITLE = "original_title";
        final String RELEASE_DATE = "release_date";
        final String VOTE_AVERAGE = "vote_average";
        final String OVERVIEW = "overview";
        final String POPULARITY = "popularity";

        JSONArray moviesArray = MoviesJson.getJSONArray(RESULTS);
        if (Param.equals("")) {
            for (int i = 0; i < moviesArray.length(); i++) {
                JSONObject pageMovie = moviesArray.getJSONObject(i);
                MoviesClass movie = new MoviesClass();
                movie.id = pageMovie.getLong(ID);
                movie.original_title = pageMovie.getString(ORIGINAL_TITLE);
                movie.poster_path = pageMovie.getString(POSTER_PATH);
                movie.release_date = pageMovie.getString(RELEASE_DATE);
                movie.vote_average = pageMovie.getDouble(VOTE_AVERAGE);
                movie.overview = pageMovie.getString(OVERVIEW);
                movie.popularity = pageMovie.getDouble(POPULARITY);
                movies.add(i, movie);
            }
        } else if (Param.equals("videos")) {
            final String KEY = "key";
            List<String> URLs = new ArrayList<String>();
            for (int i = 0; i < moviesArray.length(); i++) {
                JSONObject pageMovie = moviesArray.getJSONObject(i);
                String URLofKeys = pageMovie.getString(KEY);
                URLs.add(URLofKeys);
            }
            MoviesClass movie = new MoviesClass();
            movie.trailersURLs = URLs;
            movies.add(movie);

        } else if (Param.equals("reviews")) {
            final String CONTENT = "content";
            List<String> Reviews = new ArrayList<String>();

            for (int i = 0; i < moviesArray.length(); i++) {
                JSONObject pageMovie = moviesArray.getJSONObject(i);
                String Review = pageMovie.getString(CONTENT);
                Reviews.add(Review);
            }
            MoviesClass movie = new MoviesClass();
            movie.Reviews = Reviews;
            movies.add(movie);
        }
        return movies;
    }
}

