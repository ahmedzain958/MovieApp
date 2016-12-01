package com.example.ahmed.movieapp.Model;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Ahmed on 18/11/2016.
 */
public class MoviesDB extends RealmObject {
    @PrimaryKey
    private long id;
    private String original_title;
    private String poster_path;
    private String release_date;
    private double vote_average;
    private String overview;
    private double popularity;
    private boolean isMostPopular=false;
    private boolean isTopRated=false;
    private boolean isFavourite=false;

    public void setMostPopular(boolean mostPopular) {
        isMostPopular = mostPopular;
    }

    public void setTopRated(boolean topRated) {
        isTopRated = topRated;
    }

    public boolean getIsTopRated() {

        return isTopRated;
    }

    public boolean getIsMostPopular() {
        return isMostPopular;
    }

    public void setisFavourite(boolean Favourite) {
        isFavourite = Favourite;
    }

    public boolean getisFavourite() {

        return isFavourite;
    }
    public void setId(long id) {
        this.id = id;
    }

    public void setOriginal_title(String original_title) {
        this.original_title = original_title;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public void setVote_average(double vote_average) {
        this.vote_average = vote_average;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public long getId() {
        return id;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public String getRelease_date() {
        return release_date;
    }

    public double getVote_average() {
        return vote_average;
    }

    public String getOverview() {
        return overview;
    }

    public double getPopularity() {
        return popularity;
    }
}
