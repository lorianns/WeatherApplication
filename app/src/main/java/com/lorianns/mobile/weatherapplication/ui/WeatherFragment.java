package com.lorianns.mobile.weatherapplication.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.lorianns.mobile.weatherapplication.DetailActivity;
import com.lorianns.mobile.weatherapplication.R;
import com.lorianns.mobile.weatherapplication.rest.FetchWeatherTask;
import com.lorianns.mobile.weatherapplication.rest.Utils;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class WeatherFragment extends Fragment implements FetchWeatherTask.WeatherFetchCallback{

    private ArrayAdapter<String> mForecastAdapter;
    private ArrayList<String> mWeatherList;
    private ProgressBar mProgressBar;

    public WeatherFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mWeatherList = new ArrayList<>();

        if (savedInstanceState != null && savedInstanceState.containsKey("ARRAY"))
            mWeatherList = savedInstanceState.getStringArrayList("ARRAY");

        mForecastAdapter =
                new ArrayAdapter<String>(
                        getActivity(),
                        R.layout.list_item_weather,
                        R.id.list_item_forecast_textview,
                        mWeatherList);

        View rootView =  inflater.inflate(R.layout.fragment_weather_list, container, false);

        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(mForecastAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String forecast = mForecastAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, forecast);
                startActivity(intent);
            }
        });

        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("ARRAY", mWeatherList);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.weatherfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            updateWeather();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateWeather() {
        FetchWeatherTask weatherTask = new FetchWeatherTask(getActivity(), mForecastAdapter);
        weatherTask.setWeatherFetchCallback(this);
        String location = Utils.getPreferredLocation(getActivity());
        weatherTask.execute(location);
    }

    public void onLocationChanged( ) {
        updateWeather();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        if(savedInstanceState == null)
            updateWeather();
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onFetchingData() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressBar.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onFetchCompleted(ArrayList<String> result) {
        mWeatherList = result;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onFetchError(String error) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }
}
