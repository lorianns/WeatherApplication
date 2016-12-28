package com.lorianns.mobile.weatherapplication.rest;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.lorianns.mobile.weatherapplication.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lorianns on 12/22/16.
 */

public class Utils {

    private static String LOG_TAG = Utils.class.getSimpleName();

    public static ArrayList<String>  getWeatherDataFromJson(String JSON) throws JSONException{
        JSONObject jsonObject = null;
        ArrayList<String> resultsArray = new ArrayList<>();
        try {
            jsonObject = new JSONObject(JSON);
            if (jsonObject != null && jsonObject.length() != 0) {

                JSONObject jsonObject1 = jsonObject.getJSONObject("forecast").getJSONObject("simpleforecast");
                JSONArray jsonArray = jsonObject1.getJSONArray("forecastday");

                for(int i=0; i<jsonArray.length();i++){

                    JSONObject dateObject = jsonArray.getJSONObject(i).getJSONObject("date");
                    String day = dateObject.getString("day");
                    String month = dateObject.getString("month");
                    String year = dateObject.getString("year");
                    String weekday_short = dateObject.getString("weekday_short");
                    String monthname_short = dateObject.getString("monthname_short");

                    JSONObject highObject = jsonArray.getJSONObject(i).getJSONObject("high");
                    String highCelsius = highObject.getString("celsius");
                    String highFahrenheit = highObject.getString("fahrenheit");

                    JSONObject lowObject = jsonArray.getJSONObject(i).getJSONObject("low");
                    String lowCelsius = lowObject.getString("celsius");
                    String lowFahrenheit = lowObject.getString("fahrenheit");

                    String conditions = jsonArray.getJSONObject(i).getString("conditions");

                    String result = weekday_short + ", " + monthname_short + " " + day +
                            " - " + conditions + " - " + highFahrenheit + "/" + lowFahrenheit
                            + " (" + (char) 0x00B0 +"F)"; //degree symbol (char) 0x00B0
                    resultsArray.add(result);
                }
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "String to JSON failed: " + e);
        }
        return resultsArray;
    }

    public static boolean isProbablyValidUSZipCode(CharSequence zip) {

        String[] patterns = {"#####", "#####-####", "##### ####", "#########"};
        try {
            for (String pattern : patterns) {
                if (checkAgainstPattern(zip, pattern))
                    return true;
            }
            return false;
        }
        catch (NullPointerException ignored) {
            return false;
        }
    }

    private static boolean checkAgainstPattern(CharSequence s, CharSequence pattern) {

        if (s.length() != pattern.length())
            return false;

        for (int i = 0; i < pattern.length(); i++) {
            char c = s.charAt(i);
            switch (pattern.charAt(i)) {
                case '#':
                    if (!Character.isDigit(c))
                        return false;

                    break;

                default:
                    if (c != pattern.charAt(i))
                        return false;
            }
        }
        return true;
    }

    public static String getPreferredLocation(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String location = prefs.getString(context.getString(R.string.pref_location_key),
                context.getString(R.string.pref_location_default));
        return location;
    }

    public static void setPreferredLocation(Context context, String newLocation){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putString(context.getString(R.string.pref_location_key), newLocation).apply();
    }

}
