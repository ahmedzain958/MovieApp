package com.example.ahmed.movieapp.Model;

import java.io.Serializable;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Ahmed on 12/11/2016.
 */
public class MoviesClass implements Serializable {

    public long id;
    public String original_title;
    public String poster_path;
    public String release_date;
    public double vote_average;
    public String overview;
    public double popularity;
    public Map Trailers = new HashMap();

    public List<String> trailersURLs;
    public List<String> Reviews;

}
