package yeray.priede.weatherapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class WeatherFragment extends Fragment {
    private static final String ARG_CITY = "city";
    private String cityName;

    private TextView textViewCity, textViewTemperature, textViewDescription;
    private TextView textViewHumidity, textViewPressure, textViewWind, textViewSunrise, textViewSunset;
    private ImageView imageViewWeather;
    private TextView textViewDateTime;


    public WeatherFragment() {}

    public static WeatherFragment newInstance(String city) {
        WeatherFragment fragment = new WeatherFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CITY, city);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            cityName = getArguments().getString(ARG_CITY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather, container, false);

        textViewCity = view.findViewById(R.id.textViewCity);
        textViewTemperature = view.findViewById(R.id.textViewTemperature);
        textViewDescription = view.findViewById(R.id.textViewDescription);
        textViewHumidity = view.findViewById(R.id.textViewHumidity);
        textViewPressure = view.findViewById(R.id.textViewPressure);
        textViewWind = view.findViewById(R.id.textViewWind);
        textViewSunrise = view.findViewById(R.id.textViewSunrise);
        textViewSunset = view.findViewById(R.id.textViewSunset);
        imageViewWeather = view.findViewById(R.id.imageViewWeather);
        textViewDateTime = view.findViewById(R.id.textViewDateTime);


        textViewCity.setText(cityName);
        fetchWeatherData();

        return view;
    }

    private void fetchWeatherData() {
        String apiKey = "5b53e2223479fb3892bc0c13c7f30131";
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + cityName + "&appid=" + apiKey + "&units=metric&lang=es";

        new Thread(() -> {
            try {
                URL weatherUrl = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) weatherUrl.openConnection();
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    JSONObject jsonObject = new JSONObject(response.toString());

                    // 🚨 Comprova si OpenWeatherMap retorna un error 🚨
                    if (jsonObject.has("cod") && jsonObject.getInt("cod") != 200) {
                        getActivity().runOnUiThread(() -> showCityNotFound());
                        return; // 👈 Evita cridar updateUI() amb dades incorrectes
                    }

                    getActivity().runOnUiThread(() -> updateUI(response.toString()));
                } else {
                    getActivity().runOnUiThread(() -> showCityNotFound());
                }
            } catch (Exception e) {
                e.printStackTrace();
                getActivity().runOnUiThread(() -> showCityNotFound());
            }
        }).start();
    }
    private void updateUI(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONObject main = jsonObject.getJSONObject("main");
            JSONObject weather = jsonObject.getJSONArray("weather").getJSONObject(0);
            JSONObject wind = jsonObject.getJSONObject("wind");
            JSONObject sys = jsonObject.getJSONObject("sys");

            double temperature = main.getDouble("temp");
            String description = weather.getString("description");
            int humidity = main.getInt("humidity");
            int pressure = main.getInt("pressure");
            double windSpeed = wind.getDouble("speed");

            long sunriseTimestamp = sys.getLong("sunrise");
            long sunsetTimestamp = sys.getLong("sunset");



// Obtenir la data i hora actuals segons el fus horari local
            long currentTime = System.currentTimeMillis() / 1000; // Temps en segons

            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, d 'de' MMMM 'de' yyyy - HH:mm", new Locale("es", "ES"));
            sdf.setTimeZone(TimeZone.getDefault()); // Fus horari local
            String dateTimeString = sdf.format(new Date(currentTime * 1000L));

            textViewDateTime.setText(dateTimeString);


            textViewDateTime.setText(dateTimeString); // 💡 Mostra data i hora
            textViewTemperature.setText(temperature + "°C");
            textViewDescription.setText(description);
            textViewHumidity.setText("Humedad: " + humidity + "%");
            textViewPressure.setText("Presión: " + pressure + " hPa");
            textViewWind.setText("Viento: " + windSpeed + " m/s");

            SimpleDateFormat hourFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            String sunriseTime = hourFormat.format(new Date(sunriseTimestamp * 1000));
            String sunsetTime = hourFormat.format(new Date(sunsetTimestamp * 1000));

            textViewSunrise.setText("Salida del sol: " + sunriseTime);
            textViewSunset.setText("Puesta del sol: " + sunsetTime);
            imageViewWeather.setVisibility(View.VISIBLE);

            String iconCode = weather.getString("icon");
            String iconUrl = "https://openweathermap.org/img/wn/" + iconCode + "@2x.png";
            Glide.with(requireContext()).load(iconUrl).into(imageViewWeather);
        } catch (JSONException e) {
            e.printStackTrace();
            showCityNotFound();
        }
    }


    // 💡 Mostra un missatge quan la ciutat no existeix 💡
    private void showCityNotFound() {
        textViewCity.setText("Ciutat no trobada");
        textViewTemperature.setText("--°C");
        textViewDescription.setText("No hi ha dades");
        textViewHumidity.setText("");
        textViewPressure.setText("");
        textViewWind.setText("");
        textViewSunrise.setText("");
        textViewSunset.setText("");
        imageViewWeather.setVisibility(View.GONE);
    }
}


