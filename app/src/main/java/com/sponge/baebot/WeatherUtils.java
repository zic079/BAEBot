package com.sponge.baebot;


import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

public class WeatherUtils {

    // From tutorial, https://androstock.com/tutorials/create-a-weather-app-on-android-android-studio.html

    // check network
    public static boolean isNetworkAvailable(Context context)
    {
        return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
    }


    public static String excuteGet(String targetURL)
    {
        URL url;
        HttpURLConnection connection = null;
        try {
            //Create connection
            url = new URL(targetURL);
            Log.d("excuteGet", "URL: " + url);
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestProperty("content-type", "application/json;  charset=utf-8");
            connection.setRequestProperty("Content-Language", "en-US");
            connection.setRequestMethod("GET");
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(false);
            //connection.connect();


            // BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));


            InputStream is;
            int status = connection.getResponseCode();
            Log.d("excuteGet", "status: " + status);

            if (status != HttpURLConnection.HTTP_OK) {
                is = connection.getErrorStream();
            }
            else {
                Log.d("excuteGet", "connection: " + connection.getInputStream());
                is = connection.getInputStream();
            }

            BufferedReader rd = new BufferedReader(new InputStreamReader(is));

            String line;
            StringBuffer response = new StringBuffer();
            while((line = rd.readLine()) != null) {
                Log.d("excuteGet", "rd OK");
                response.append(line);
                response.append('\r');
            }
            rd.close();

            //Log.d("excuteGet", "excuteGet return: " + response.toString());
            return response.toString();
        } catch (Exception e) {
            Log.d("excuteGet","exception: " + e);
            return null;
        } finally {
            if(connection != null) {
                connection.disconnect();
            }
        }
    }


    // icon for different weather
    public static String setWeatherIcon(int actualId, long sunrise, long sunset){
        int id = actualId / 100;
        String icon = "";
        if(actualId == 800){
            long currentTime = new Date().getTime();
            if(currentTime>=sunrise && currentTime<sunset) {
                icon = "&#xf00d;";
            } else {
                icon = "&#xf02e;";
            }
        } else {
            switch(id) {
                case 2 : icon = "&#xf01e;";
                    break;
                case 3 : icon = "&#xf01c;";
                    break;
                case 7 : icon = "&#xf014;";
                    break;
                case 8 : icon = "&#xf013;";
                    break;
                case 6 : icon = "&#xf01b;";
                    break;
                case 5 : icon = "&#xf019;";
                    break;
            }
        }
        return icon;
    }

}
