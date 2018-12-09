package com.sponge.baebot;

import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.Locale;

public class WeatherActivity extends AppCompatActivity {

    TextView cityField, detailsField, temperatureField, humidityField, weatherIcon;
    Typeface weatherFont;
    String city = "San Diego, US";          // set default city as san diego
    String OPEN_WEATHER_MAP_API = "85c4869d24fc29549362f8d8f172f5f0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        cityField = findViewById(R.id.cityValue);
        detailsField = findViewById(R.id.detailValue);
        temperatureField = findViewById(R.id.temperatureValue);
        humidityField =  findViewById(R.id.humidityValue);

        weatherIcon =  findViewById(R.id.weatherIcon);
        weatherFont = Typeface.createFromAsset(getAssets(),
                "fonts/weathericons-regular-webfont.ttf");
        weatherIcon.setTypeface(weatherFont);

        taskLoadUp(city);
    }

    public void taskLoadUp(String query) {
        if (WeatherUtils.isNetworkAvailable(getApplicationContext())) {
            DownloadWeather task = new DownloadWeather();
            task.execute(query);
        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection",
                    Toast.LENGTH_LONG).show();
        }
    }



    class DownloadWeather extends AsyncTask< String, Void, String > {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String...args) {
            Log.d("DownloadWeather", "Inside doInBackground - arg:" + args[0]);

            String xml = WeatherUtils.excuteGet(
                    "http://api.openweathermap.org/data/2.5/weather?q=" + args[0] +
                    "&units=metric&appid=" + OPEN_WEATHER_MAP_API);
            Log.d("DownloadWeather", "doInBackground: xml - " + xml);
            return xml;
        }

        @Override
        protected void onPostExecute(String xml) {
            try {
                JSONObject json = new JSONObject(xml);
                if (json != null) {
                    JSONObject details = json.getJSONArray("weather").getJSONObject(0);
                    JSONObject main = json.getJSONObject("main");
                    cityField.setText(json.getString("name").toUpperCase(Locale.US) +
                            ", " + json.getJSONObject("sys").getString("country"));
                    detailsField.setText(details.getString("description")
                            .toUpperCase(Locale.US));
                    temperatureField.setText(main.getInt("temp")*9/5+32 + "°");
                    humidityField.setText("Humidity: " + main.getString("humidity") + "%");
                    weatherIcon.setText(Html.fromHtml(
                            WeatherUtils.setWeatherIcon(details.getInt("id"),
                            json.getJSONObject("sys").getLong("sunrise") * 1000,
                            json.getJSONObject("sys").getLong("sunset") * 1000)));
                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Error, Check City",
                        Toast.LENGTH_SHORT).show();
            }

        }

    }
}

