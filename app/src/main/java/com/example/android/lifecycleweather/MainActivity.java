package com.example.android.lifecycleweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.lifecycleweather.data.WeatherPreferences;
import com.example.android.lifecycleweather.utils.NetworkUtils;
import com.example.android.lifecycleweather.utils.OpenWeatherMapUtils;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements ForecastAdapter.OnForecastItemClickListener, LoaderManager.LoaderCallbacks<String> {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String OPEN_WEATHER_LIST_KEY = "OpenWeatherForecastList";
    private static  final String OPEN_WEATHER_FORECAST_KEY = "OpenWeatherForecast";
    private static final int OPEN_WEATHER_FORECAST_LOADER_ID = 0;

    private SharedPreferences mPreferences;
    private SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener;



    private ArrayList<OpenWeatherMapUtils.ForecastItem> mForecastItemList;

    private TextView mForecastLocationTV;
    private RecyclerView mForecastItemsRV;
    private ProgressBar mLoadingIndicatorPB;
    private TextView mLoadingErrorMessageTV;
    private ForecastAdapter mForecastAdapter;
    private TextView mLifecycleEventsTV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        sharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                mForecastLocationTV.setText(mPreferences.getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default)));
            }
        };
        mPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);

        mLifecycleEventsTV = (TextView)findViewById(R.id.tv_lifecycle_events);
        mForecastItemList = null;

        // Remove shadow under action bar.
        getSupportActionBar().setElevation(0);

        mForecastLocationTV = findViewById(R.id.tv_forecast_location);
        mForecastLocationTV.setText(WeatherPreferences.getDefaultForecastLocation());

        mLoadingIndicatorPB = findViewById(R.id.pb_loading_indicator);
        mLoadingErrorMessageTV = findViewById(R.id.tv_loading_error_message);
        mForecastItemsRV = findViewById(R.id.rv_forecast_items);

        mForecastAdapter = new ForecastAdapter(this);
        mForecastItemsRV.setAdapter(mForecastAdapter);
        mForecastItemsRV.setLayoutManager(new LinearLayoutManager(this));
        mForecastItemsRV.setHasFixedSize(true);

        loadForecast();

        getSupportLoaderManager().initLoader(OPEN_WEATHER_FORECAST_LOADER_ID, null, this);

        if(savedInstanceState != null && savedInstanceState.containsKey(OPEN_WEATHER_LIST_KEY)) {
            mForecastItemList = (ArrayList)savedInstanceState.getSerializable(OPEN_WEATHER_LIST_KEY);
            mForecastAdapter.updateForecastItems(mForecastItemList);
        }

        sharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
              loadForecast();
          }
        };

        mPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);

    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(mForecastItemList != null) {
            outState.putSerializable(OPEN_WEATHER_LIST_KEY, mForecastItemList);
        }
    }

    private void logAndDisplayLifecycleEvent(String lifecycleEvent) {
        Log.d(TAG, "Lifecycle Event: " + lifecycleEvent);
        mLifecycleEventsTV.append(lifecycleEvent + "\n");
    }

    @Override
    public void onForecastItemClick(OpenWeatherMapUtils.ForecastItem forecastItem) {
        Intent intent = new Intent(this, ForecastItemDetailActivity.class);
        intent.putExtra(OpenWeatherMapUtils.EXTRA_FORECAST_ITEM, forecastItem);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_location:
                showForecastLocation();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void loadForecast() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String unit = preferences.getString(getString(R.string.pref_unit_key), getString(R.string.pref_unit_default));
        
        Log.d(TAG, "units: " + unit);
        String location = preferences.getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default));
        WeatherPreferences.setForecastLocation(location);


        Log.d(TAG, "location: " + location);
        mForecastLocationTV.setText(WeatherPreferences.getDefaultForecastLocation());
        String openWeatherMapForecastURL = OpenWeatherMapUtils.buildForecastURL(
                location,
                unit
        );


        Log.d(TAG, "got forecast url: " + openWeatherMapForecastURL);
        Bundle args = new Bundle();
        args.putString(OPEN_WEATHER_FORECAST_KEY, openWeatherMapForecastURL);
        mLoadingIndicatorPB.setVisibility(View.VISIBLE);
        getSupportLoaderManager().restartLoader(OPEN_WEATHER_FORECAST_LOADER_ID, args, this);
    }


    public void showForecastLocation() {
        Uri geoUri = Uri.parse("geo:0,0").buildUpon()
                .appendQueryParameter("q", WeatherPreferences.getDefaultForecastLocation())
                .build();
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, geoUri);
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }
    }

    @Override
    public Loader<String> onCreateLoader(int id, @Nullable Bundle args) {
        String openWeatherMapForecastURL = null;
        if(args != null) {
            openWeatherMapForecastURL = args.getString(OPEN_WEATHER_FORECAST_KEY);
        }
        return new OpenWeatherForecastLoader(this, openWeatherMapForecastURL);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {
        mLoadingIndicatorPB.setVisibility(View.INVISIBLE);
        if(data != null) {
            mLoadingErrorMessageTV.setVisibility(View.INVISIBLE);
            mForecastItemsRV.setVisibility(View.VISIBLE);
            mForecastItemList = OpenWeatherMapUtils.parseForecastJSON(data);
            mForecastAdapter.updateForecastItems(mForecastItemList);
        }
        else {
            mForecastItemsRV.setVisibility(View.INVISIBLE);
            mLoadingErrorMessageTV.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {
        //Nothing to do!
    }
}
