package com.example.ahmed.movieapp;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.ahmed.movieapp.Model.FaviouriteDB;
import com.example.ahmed.movieapp.Model.IAsyncResponse;
import com.example.ahmed.movieapp.Model.FetchMoviesTask;
import com.example.ahmed.movieapp.Model.IntentListener;
import com.example.ahmed.movieapp.Model.MovieAdapter;
import com.example.ahmed.movieapp.Model.MoviesClass;
import com.example.ahmed.movieapp.Model.MoviesDB;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;


public class MainActivityFragment extends Fragment implements IAsyncResponse {
    static GridView MoviesGridView;
    static int width;
    Realm realm;
    static MovieAdapter movieAdapter;
    MovieAdapter movieAdapterFavourite;
    List<MoviesClass> returnedListOfMovies;
    public static PreferenceChangeListener listener;
    public static SharedPreferences prefs;
    static boolean sortByFavorites;
    static boolean sortByPop = true;
    public static int GrdViewPosition = 0;

    public MainActivityFragment() {
    }


    private IntentListener mIntentListener;

    void setmIntentListener(IntentListener intentListener) {
        this.mIntentListener = intentListener;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        if (MainActivity.TABLET) {
            width = size.x / 6;
        } else width = size.x / 3;
        MoviesGridView = (GridView) rootView.findViewById(R.id.MoviesGridView);
        MoviesGridView.setColumnWidth(width);
        realm = Realm.getDefaultInstance();
        if (savedInstanceState != null) {
            int myInt = savedInstanceState.getInt("GVpostion");
            MoviesGridView.smoothScrollToPosition(myInt);
        }
        MoviesGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView,
                                    View view, int position, long l) {
                MoviesClass movie;
                if (!prefs.getString("sortby", "popularity").equals("favorites")) {
                    movie = movieAdapter.getItem(position);
                    //intent.putExtra(MoviesClass.class.getName(), movie);
                    //tablet
                   // Bundle extras = this.getExtras();
                } else {
                    movie = movieAdapterFavourite.getItem(position);
                    //intent.putExtra(MoviesClass.class.getName(), movie);
                }
                mIntentListener.setSelectedMovie(movie);
            }
        });
        return rootView;
    }

    public boolean isFavouritedMovie(long id) {
        FaviouriteDB isExistfaviouriteDB = realm.where(FaviouriteDB.class).equalTo("id", id).findFirst();
        if (isExistfaviouriteDB != null) {
            return true;
        }
        return false;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MoviesGridView.setSelection(GrdViewPosition);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        int gridViewPosition = MoviesGridView.getFirstVisiblePosition();
        outState.putInt("GVpostion", gridViewPosition);
        GrdViewPosition = gridViewPosition;
    }


    @Override
    public void onPause() {
        super.onPause();
        GrdViewPosition = MoviesGridView.getFirstVisiblePosition();
        MoviesGridView.setSelection(GrdViewPosition);

    }

    @Override
    public void onResume() {
        super.onResume();
        MoviesGridView.setSelection(GrdViewPosition);
        MoviesGridView.smoothScrollToPosition(GrdViewPosition);
    }

    @Override
    public void onStart() {
        super.onStart();
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        listener = new PreferenceChangeListener();
        prefs.registerOnSharedPreferenceChangeListener(listener);

        if (prefs.getString("sortby", "popularity").equals("popularity")) {
            getActivity().setTitle("Most Popular Movies");
            sortByPop = true;
            sortByFavorites = false;
        } else if (prefs.getString("sortby", "popularity").equals("rating")) {
            getActivity().setTitle("Highest Rated Movies");
            sortByPop = false;
            sortByFavorites = false;
        } else if (prefs.getString("sortby", "popularity").equals("favorites")) {
            getActivity().setTitle("Favorited Movies");
            sortByPop = false;
            sortByFavorites = true;
        }
        if (isNetworkAvailable()) {
            if (!prefs.getString("sortby", "popularity").equals("favorites")) {
                FetchMoviesTask fetchMoviesTask = new FetchMoviesTask();
                fetchMoviesTask.setlistener(this);
                fetchMoviesTask.execute("", "");
            } else {
                readFromRealmFavourite();
            }
        } else {
            Toast.makeText(getActivity(), "You are offline", Toast.LENGTH_SHORT).show();
        }
    }

    public void readFromRealmFavourite() {
        RealmQuery<FaviouriteDB> dblistOfMovies1 = realm.where(FaviouriteDB.class);
        RealmResults<FaviouriteDB> dblistOfMovies = dblistOfMovies1.findAll();
        ArrayList<MoviesClass> listOfMovieClass = new ArrayList<MoviesClass>(dblistOfMovies.size());
        for (int i = 0; i < dblistOfMovies.size(); i++) {
            MoviesClass movieToInflate = new MoviesClass();
            movieToInflate.id = dblistOfMovies.get(i).getId();
            movieToInflate.release_date = dblistOfMovies.get(i).getRelease_date();
            movieToInflate.original_title = dblistOfMovies.get(i).getOriginal_title();
            movieToInflate.overview = dblistOfMovies.get(i).getOverview();
            movieToInflate.poster_path = dblistOfMovies.get(i).getPoster_path();
            movieToInflate.vote_average = dblistOfMovies.get(i).getVote_average();
            movieToInflate.popularity = dblistOfMovies.get(i).getPopularity();
            listOfMovieClass.add(movieToInflate);
        }

        movieAdapterFavourite = new MovieAdapter(getActivity(), listOfMovieClass, width);
        MoviesGridView.setAdapter(movieAdapterFavourite);

    }


    @Override
    public void processFinish(List<MoviesClass> listOfMovies) {
        movieAdapter = new MovieAdapter(getActivity(), listOfMovies, width);
        MoviesGridView.setAdapter(movieAdapter);
        returnedListOfMovies = listOfMovies;
    }

    private class PreferenceChangeListener implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            MoviesGridView.setAdapter(null);
            onStart();
        }

    }

    public void insertIntoRealmCaseOnlineMode(boolean sortByPop, boolean sortByFavorites) {
        //delete
        if (!sortByFavorites) {

            realm.beginTransaction();
            final RealmResults<MoviesDB> result = (sortByPop)
                    ? realm.where(MoviesDB.class)
                    .equalTo("isMostPopular", true)
                    .equalTo("isTopRated", false)
                    .equalTo("isFavourite", false)
                    .findAll()
                    : realm.where(MoviesDB.class)
                    .equalTo("isTopRated", true)
                    .equalTo("isMostPopular", false)
                    .equalTo("isFavourite", false)
                    .findAll();

            realm.commitTransaction();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    result.deleteAllFromRealm();
                }
            });
        }
        final MoviesDB moviesDB = new MoviesDB();
        if (returnedListOfMovies != null && returnedListOfMovies.size() > 0) {
            for (MoviesClass obj : returnedListOfMovies) {
                moviesDB.setId(obj.id);
                moviesDB.setOriginal_title(obj.original_title);
                moviesDB.setRelease_date(obj.release_date);
                moviesDB.setOverview(obj.overview);
                moviesDB.setPoster_path(obj.poster_path);
                moviesDB.setVote_average(obj.vote_average);
                moviesDB.setPopularity(obj.popularity);

                MoviesDB first = realm.where(MoviesDB.class).equalTo("id", obj.id).findFirst();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.filtering_menu, menu);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.top_rated) {
            getActivity().setTitle("Highest Rated Movies");
            sortByPop = false;
            sortByFavorites = false;
        } else if (id == R.id.popular) {
            getActivity().setTitle("Most Popular Movies");
            sortByPop = true;
            sortByFavorites = false;
        }
        if (isNetworkAvailable()) {

            MoviesGridView.setVisibility(GridView.VISIBLE);
            FetchMoviesTask fetchMoviesTask = new FetchMoviesTask();
            fetchMoviesTask.setlistener(this);
            fetchMoviesTask.execute("", "");
            insertIntoRealmCaseOnlineMode(sortByPop, sortByFavorites);
        } else {
        }
        return super.onOptionsItemSelected(item);
    }


}
