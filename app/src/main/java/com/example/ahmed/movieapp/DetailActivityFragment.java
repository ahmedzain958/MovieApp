package com.example.ahmed.movieapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ahmed.movieapp.Model.FaviouriteDB;
import com.example.ahmed.movieapp.Model.FavouritedReviews;
import com.example.ahmed.movieapp.Model.FetchMoviesTask;
import com.example.ahmed.movieapp.Model.IAsyncResponse;
import com.example.ahmed.movieapp.Model.IVideosReviewsResponse;
import com.example.ahmed.movieapp.Model.MoviesClass;
import com.example.ahmed.movieapp.Model.MoviesDB;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class DetailActivityFragment extends Fragment implements IVideosReviewsResponse {
    Realm realm;
    Realm realmInsertReviews;
    static MoviesClass moviesClass;
    List<String> Reviews = new ArrayList<String>();
    List<String> VideosURL = new ArrayList<String>();

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Intent intent = getActivity().getIntent();

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w185/";
        realm = Realm.getDefaultInstance();
        //if (intent != null && intent.hasExtra(MoviesClass.class.getName())) {
        moviesClass = (MoviesClass) intent.getSerializableExtra(MoviesClass.class.getName());
        if (moviesClass == null) {
            Bundle b = getArguments();
            moviesClass = (MoviesClass) b.getSerializable(MoviesClass.class.getName());
        }
        ((TextView) rootView.findViewById(R.id.movie_name)).setText(moviesClass.original_title);
        ImageView detailmovieImage = (ImageView) rootView.findViewById(R.id.detailmovieImage);
        Picasso.with(getActivity()).load(IMAGE_BASE_URL + moviesClass.poster_path).into(detailmovieImage);
        ((TextView) rootView.findViewById(R.id.release_date)).setText(moviesClass.release_date.toString());
        ((TextView) rootView.findViewById(R.id.vote_average)).setText(moviesClass.vote_average + "/10");
        ((TextView) rootView.findViewById(R.id.Tvoverview)).setText(moviesClass.overview);
        final Button favourite = (Button) rootView.findViewById(R.id.favorite);


        FaviouriteDB isExistfaviouriteDB = realm.where(FaviouriteDB.class).equalTo("id", moviesClass.id).findFirst();
        if (isExistfaviouriteDB != null) {
            favourite.setText("UNFAVORITE");
            favourite.getBackground().setColorFilter(Color.CYAN, PorterDuff.Mode.MULTIPLY);
            favourite.setBackgroundColor(Color.CYAN);
        }
        getActivity().setTitle(moviesClass.original_title);


        //}
        final Button b = (Button) rootView.findViewById(R.id.favorite);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (b.getText().equals("FAVORITE")) {
                    //code to store movie data in database
                    b.setText("UNFAVORITE");
                    b.getBackground().setColorFilter(Color.CYAN, PorterDuff.Mode.MULTIPLY);
                    b.setBackgroundColor(Color.CYAN);
                    insertFavouriteMovie(moviesClass);
                } else {
                    b.setText("FAVORITE");
                    b.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                    b.setBackgroundColor(Color.GRAY);
                    deleteFavouriteMovie(moviesClass.id);
                }
            }
        });

        displayVideosAndReviews();
        return rootView;
    }

    private void displayVideosAndReviews() {
        FetchMoviesTask fetchMoviesTaskvideos = new FetchMoviesTask();
        fetchMoviesTaskvideos.setlistenerVideosReviews(this);
        fetchMoviesTaskvideos.execute("videos", String.valueOf(moviesClass.id));

        FetchMoviesTask fetchMoviesTaskreviews = new FetchMoviesTask();
        fetchMoviesTaskreviews.setlistenerVideosReviews(this);
        fetchMoviesTaskreviews.execute("reviews", String.valueOf(moviesClass.id));
    }

    public void insertFavouriteMovie(MoviesClass moviesClass) {
        final FaviouriteDB favouriteDB = new FaviouriteDB();
        favouriteDB.setId(moviesClass.id);
        favouriteDB.setOriginal_title(moviesClass.original_title);
        favouriteDB.setRelease_date(moviesClass.release_date);
        favouriteDB.setOverview(moviesClass.overview);
        favouriteDB.setPoster_path(moviesClass.poster_path);
        favouriteDB.setVote_average(moviesClass.vote_average);
        favouriteDB.setPopularity(moviesClass.popularity);
        final FavouritedReviews favouritedReview = new FavouritedReviews();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                realm.copyToRealmOrUpdate(favouriteDB);
            }
        });


    }

    public void deleteFavouriteMovie(long id) {
        final long deletedid = id;
        final RealmResults<FaviouriteDB> result = realm.where(FaviouriteDB.class).equalTo("id", deletedid).findAll();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                result.deleteAllFromRealm();
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onVideosReady(List<MoviesClass> listOfV) {
        if (listOfV != null) {
            for (String VideoUrl : listOfV.get(0).trailersURLs) {
                VideosURL.add("https://www.youtube.com/watch?v=" + VideoUrl);
            }
        }
        if (VideosURL.size() > 0) {
            Button[] btnVideo = new Button[VideosURL.size()];

            LinearLayout linear = (LinearLayout) getActivity().findViewById(R.id.linear);
            for (int i = 0; i < VideosURL.size(); i++) {
                btnVideo[i] = new Button(getActivity());
                btnVideo[i].setHeight(50);
                btnVideo[i].setWidth(50);
                btnVideo[i].setTag(VideosURL.get(i));
                btnVideo[i].setText("Trailer no.." + String.valueOf(i + 1));
                btnVideo[i].setOnClickListener(btnClicked);
                linear.addView(btnVideo[i]);
                View v = new View(getActivity());
                v.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        5
                ));
                v.setBackgroundColor(Color.parseColor("#B3B3B3"));
                linear.addView(v);
            }
        }
    }

    View.OnClickListener btnClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Object tag = v.getTag();
            String url = tag.toString();
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse(url)));
        }
    };

    @Override
    public void onReviewsReady(List<MoviesClass> listOfR) {
        if (listOfR != null) {
            for (String review : listOfR.get(0).Reviews) {
                Reviews.add(review);
            }
        }
        if (Reviews.size() > 0) {
            TextView[] txtReview = new TextView[Reviews.size()];

            LinearLayout linear = (LinearLayout) getActivity().findViewById(R.id.linearReviews);
            for (int i = 0; i < Reviews.size(); i++) {
                txtReview[i] = new TextView(getActivity());
                txtReview[i].setText(Reviews.get(0));
                linear.addView(txtReview[i]);
            }
        }
    }
}
