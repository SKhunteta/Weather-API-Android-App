package com.example.android.lifecycleweather.data;

public class WeatherPreferences {
    private static String DEFAULT_FORECAST_LOCATION = "Portland,OR,US";
    private static final String DEFAULT_TEMPERATURE_UNITS = "imperial";
    private static  String DEFAULT_TEMPERATURE_UNITS_ABBR = "F";

    public static String getDefaultForecastLocation() {
        return DEFAULT_FORECAST_LOCATION;
    }

    public static String getDefaultTemperatureUnits() {
        return DEFAULT_TEMPERATURE_UNITS;
    }

    public static String getDefaultTemperatureUnitsAbbr() {
        return DEFAULT_TEMPERATURE_UNITS_ABBR;
    }

    public static void setForecastLocation(String location) {
        DEFAULT_FORECAST_LOCATION = location;
    }
}
