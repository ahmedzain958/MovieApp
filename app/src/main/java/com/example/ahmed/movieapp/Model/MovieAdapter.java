package com.example.ahmed.movieapp.Model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ahmed.movieapp.Model.MoviesClass;
import com.example.ahmed.movieapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MovieAdapter extends BaseAdapter {
    List<MoviesClass> moviesClass = new ArrayList<MoviesClass>();
    private Context mcontext;
    private int width;

    public MovieAdapter(Context Context, List<MoviesClass> mMovies, int width) {
        this.mcontext = Context;
        this.moviesClass = mMovies;
        this.width = width;
    }

    @Override
    public int getCount() {
        return moviesClass.size();
    }

    @Override
    public MoviesClass getItem(int i) {
        return moviesClass.get(i);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w185/";
        ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_movies, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Picasso.with(mcontext).load(IMAGE_BASE_URL + moviesClass.get(i).poster_path)
                .resize(width, (int) (width * 1.5)).into(viewHolder.ivMoviePoster);
        return convertView;
    }

    public class ViewHolder {
        ImageView ivMoviePoster;
        TextView tvMovieName;
        TextView movieName;
        ImageView detailmovieImage;

        public ViewHolder(View view) {
            ivMoviePoster = (ImageView) view.findViewById(R.id.movieImage);
            movieName = (TextView) view.findViewById(R.id.movie_name);
            detailmovieImage = (ImageView) view.findViewById(R.id.detailmovieImage);
        }

    }

}
