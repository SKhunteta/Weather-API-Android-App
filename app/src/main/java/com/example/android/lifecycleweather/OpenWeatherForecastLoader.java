package com.example.android.lifecycleweather;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.example.android.lifecycleweather.utils.NetworkUtils;

import java.io.IOException;

public class OpenWeatherForecastLoader extends AsyncTaskLoader<String> {

    private static final String TAG = OpenWeatherForecastLoader.class.getSimpleName();
    private String mOpenWeatherForecastJSON;
    private String mURL;

    OpenWeatherForecastLoader(Context context, String url) {
        super(context);
        mURL = url;
    }

    @Override
    protected void onStartLoading() {
        if(mURL != null) {
            if(mOpenWeatherForecastJSON != null) {
                Log.d(TAG,"Delivering cached forecasts");
                deliverResult(mOpenWeatherForecastJSON);
            }
            else {
                forceLoad();
            }
        }
    }

    @Nullable
    @Override
    public String loadInBackground() {
        if(mURL != null) {
            String forecastItem = null;
            try {
                Log.d(TAG, "Loading forecast from OpenWeatherMap with URL: " + mURL);
                forecastItem = NetworkUtils.doHTTPGet(mURL);
            }
            catch(IOException e) {
                e.printStackTrace();
            }
            return forecastItem;
        }
        else {
            return null;
        }
    }

    @Override
    public void deliverResult(@Nullable String data) {
        mOpenWeatherForecastJSON = data;
        super.deliverResult(data);
    }
}