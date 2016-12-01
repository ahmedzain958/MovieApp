package com.example.ahmed.movieapp;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;

import com.example.ahmed.movieapp.Model.IntentListener;
import com.example.ahmed.movieapp.Model.MoviesClass;


public class MainActivity extends ActionBarActivity implements IntentListener {

    public static boolean TABLET = false;

    public boolean isTablet(Context context) {
        boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
        boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
        return (xlarge || large);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        MainActivityFragment mMainActivityFragment = new MainActivityFragment();
        mMainActivityFragment.setmIntentListener(this);
        if (null != findViewById(R.id.flDetail))
            TABLET = true;

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.flMain, mMainActivityFragment, "")
                .commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setSelectedMovie(MoviesClass movie) {
        if (!TABLET) {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra(MoviesClass.class.getName(), movie);
            startActivity(intent);
        } else {
            DetailActivityFragment mDetailFragment = new DetailActivityFragment();
            Bundle extra = new Bundle();
            extra.putSerializable(MoviesClass.class.getName(), movie);
            mDetailFragment.setArguments(extra);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.flDetail, mDetailFragment, "")
                    .commit();
        }
    }
}
