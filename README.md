# Assignment 3
**Due by 11:59pm on Monday, 2/25/2019**

**Demo due by 11:59pm on Monday, 3/11/2019**

In this assignment, we'll adapt our weather app to gracefully deal with transitions in the activity lifecycle by incorporating an `AsyncTaskLoader`.  You'll also add some basic user preferences to the app.

There are a few different tasks associated with this assignment, described below.  This repository provides you with some starter code that implements the connected weather app from assignment 2, plus a few extra layout bells and whistles.

**NOTE: make sure to add your own API key in `OpenWeatherMapUtils.java` to make the app work.**

## 1. Use `AsyncTaskLoader` to load results

One thing you might notice is that when you do things like rotate your device when viewing the main activity, the activity is recreated, resulting in a new network call to fetch the same weather forecast data (you can know this is happening because the loading indicator will be displayed, indicating that the `AsyncTask` for fetching forecast data from OpenWeatherMap is running).

Your first task in this assignment is to fix this problem by replacing the app's current `AsyncTask` with an `AsyncTaskLoader`, which can better cope with activity lifecycle transitions because it lives beyond the lifecycle of the activity and can cache results and return them.

To replace the app's `AsyncTask` with an `AsyncTaskLoader`, you'll need to make its `MainActivity` class implement the `LoaderManager.LoaderCallbacks` interface by implementing the `onCreateLoader()`, `onFinishedLoading()`, and `onLoaderReset()` methods and moving functionality from the `AsyncTask` to the appropriate place among these methods.

One place to pay special attention to is `onCreateLoader()`.  From this method, you should return an instance of a subclass of `AsyncTaskLoader`.  Within this class, you should do the following things:

  * Implement the `onStartLoading()` and `loadInBackground()` methods to perform the correct portions of the call to the OpenWeatherMap API.

  * Cache results returned from the OpenWeatherMap API and deliver them when possible.

  * Include log statements that clearly indicate when your loader is fetching results from the OpenWeatherMap API and when it is delivering cached results.

As a result of these changes, you should see your app fetch results from the OpenWeatherMap API only one time through typical usage of the app, including through rotations of the phone and navigation around the app.

## 2. Add some basic user preferences to the app

When you run the version of the app provided in this repository, you'll probably notice a settings icon in the title bar of the main activity.  Your second task in this assignment is to create a new activity named `SettingsActivity` that implements a user preferences screen using a `PreferenceFragment`.  This activity should be launched when you click the settings icon in the main activity.

The preferences screen should allow the user to set the following preferences:

  * **Weather units** - The user should be allowed to select between "Imperial", "Metric", and "Kelvin", and the currently-selected value should be displayed as the summary for the preference.  See the OpenWeatherMap API documentation here for more info on how this preference value will be used: https://openweathermap.org/forecast5#data.

  * **Weather location** - The user should be allowed to enter an arbitrary location for which to fetch weather.  The currently-set value should be set as the summary for the preference.  You can specify any default location you'd like.  See the OpenWeatherMap API documentation here for more info on how this preference value will be used: https://openweathermap.org/forecast5#name5.

The settings of these preferences should affect the URL used to query the OpenWeatherMap API.  The app should be hooked up so that any change to the preferences results in the OpenWeatherMap API being re-queried and the new results being displayed.  Importantly, there are a couple places in the UI and elsewhere that will also need to be updated in response to a change in preferences:
  * The weather location displayed at the top of the main activity.
  * The units displayed in the forecast list and the forecast detail view.
  * The location displayed in the map when the corresponding action bar action is triggered.
  * The currently set forecast location in the text shared by the share action.

All of these values are currently taken from the class `WeatherPreferences`.

## Extra credit

You may have noticed that Android's `Loader` framework is newly deprecated, including the `AsyncTaskLoader`.  It is being replaced with a paradigm based on the [`ViewModel`](https://developer.android.com/topic/libraries/architecture/viewmodel), which is a class designed to store and manage UI-related data in an activity lifecycle-aware way, but that is lighter-weight and less strongly-tied to UI classes than loaders.

For up to 10 points of extra credit, replace the `AsyncTaskLoader` you implemented above with a `ViewModel`-based solution for connecting with the OpenWeatherMap API and managing the forecast data it returns.  Your `ViewModel` should use an `AsyncTask` for sending the HTTP request to the OpenWeatherMap API.  If you successfully use a `ViewModel` for managing forecast data, you will earn full credit for the first part of the assignment, even if your app doesn't use an `AsyncTaskLoader`.

## Submission

As usual, we'll be using GitHub Classroom for this assignment, and you will submit your assignment via GitHub. Make sure your completed files are committed and pushed by the assignment's deadline to the master branch of the GitHub repo that was created for you by GitHub Classroom. A good way to check whether your files are safely submitted is to look at the master branch your assignment repo on the github.com website (i.e. https://github.com/OSU-CS492-W18/assignment-3-YourGitHubUsername/). If your changes show up there, you can consider your files submitted.

## Grading criteria

This assignment is worth 100 points, broken down as follows:

  * 50 points: Uses `AsyncTaskLoader` and caches results
    * 30 points: uses `AsyncTaskLoader` instead of `AsyncTask` to perform communication with the OpenWeatherMap API
    * 15 points: caches results from the OpenWeatherMap API in the loader and delivers them when possible (should only need to make one network call)
    * 5 points: adds logging statements to demonstrate when the loader is fetching results and when it is delivering cached ones

  * 50 points: Implements user settings activity
    * 15 points: uses a preference fragment to allow the user to select units and forecast location
    * 5 points: summaries of both preferences reflect the current values of those preferences
    * 30 points: changing preferences results in new results being displayed and correct updates made to UI, as described above

  * Extra credit:
    * 10 points: Uses a `ViewModel` to fetch, store, and manage data from the OpenWeatherMap API instead of an `AsyncTaskLoader`
      * If you successfully use a `ViewModel` for managing forecast data, you will earn full credit for the first part of the assignment, even if your app doesn't use an `AsyncTaskLoader`.
