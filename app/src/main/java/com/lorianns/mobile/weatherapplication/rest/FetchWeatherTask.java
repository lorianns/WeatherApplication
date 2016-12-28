package com.lorianns.mobile.weatherapplication.rest;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.lorianns.mobile.weatherapplication.BuildConfig;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by lorianns on 12/22/16.
 */

public class FetchWeatherTask extends AsyncTask<String, Void, ArrayList<String>> {

    private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

    private ArrayAdapter<String> mWeatherAdapter;
    private final Context mContext;
    private WeatherFetchCallback mCallback;

    public interface WeatherFetchCallback {
        /**
         * WeatherFragment for when a fetch is request.
         */
        public void onFetchingData();
        public void onFetchCompleted(ArrayList<String> result);
        public void onFetchError(String error);
    }

    public void setWeatherFetchCallback(WeatherFetchCallback callback){
        mCallback = callback;
    }

    public FetchWeatherTask(Context context, ArrayAdapter<String> weatherAdapter) {
        mContext = context;
        mWeatherAdapter = weatherAdapter;
    }

    @Override
    protected ArrayList<String> doInBackground(String... params) {
        if (params.length == 0) {
            return null;
        }
        String locationQuery = params[0];

        mCallback.onFetchingData();

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String forecastJsonStr = null;

         try {
            final String FORECAST_BASE_URL =
                    "http://api.wunderground.com/api/"+BuildConfig.OPEN_WEATHER_UNDERGROUND_API_KEY
                            +"/forecast10day/q/" + locationQuery + ".json";

             URL url = new URL(FORECAST_BASE_URL);

            // Create the request to wunderground, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.
                return null;
            }
            forecastJsonStr = buffer.toString();
        } catch (IOException e) {
             mCallback.onFetchError(e.getMessage());
            Log.e(LOG_TAG, "Error ", e);
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    mCallback.onFetchError(e.getMessage());
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        try {
            return Utils.getWeatherDataFromJson(forecastJsonStr);
        } catch (JSONException e) {
            mCallback.onFetchError(e.getMessage());
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<String> result) {
        mCallback.onFetchCompleted(result);
        if (result != null && mWeatherAdapter != null) {
            mWeatherAdapter.clear();
            for(String dayForecastStr : result) {
                mWeatherAdapter.add(dayForecastStr);
            }
        }
    }
}
