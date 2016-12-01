package com.example.ahmed.movieapp.Model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Ahmed on 29/11/2016.
 */
public class FavouritedReviews extends RealmObject {
    //private long id;
    private String Review;
    private long FavouriteMovieid;


    public void setFavouriteMovieid(long FavouriteMovieid) {
        this.FavouriteMovieid = FavouriteMovieid;
    }

    public void setReview(String Review) {
        this.Review = Review;
    }


    public long getFavouriteMovieid() {
        return FavouriteMovieid;
    }

    public String getReview() {
        return Review;
    }
}


