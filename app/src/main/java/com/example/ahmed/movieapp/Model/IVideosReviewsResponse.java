package com.example.ahmed.movieapp.Model;

import java.util.List;

public interface IVideosReviewsResponse {
    void onVideosReady(List<MoviesClass> listOfV);
    void onReviewsReady(List<MoviesClass> listOfR);

}