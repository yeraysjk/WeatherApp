package yeray.priede.weatherapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherFragment extends Fragment {
    private static final String ARG_CITY = "city";
    private String cityName;

    private TextView textViewCity, textViewTemperature, textViewDescription;
    private ImageView imageViewWeather;

    public WeatherFragment() { }

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
        imageViewWeather = view.findViewById(R.id.imageViewWeather);

        textViewCity.setText(cityName);
        fetchWeatherData();

        return view;
    }

    private void fetchWeatherData() {
        String apiKey = "5b53e2223479fb3892bc0c13c7f30131";
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + cityName + "&appid=" + apiKey + "&units=metric";

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

                    getActivity().runOnUiThread(() -> updateUI(response.toString()));
                }
            } catch (Exception e) {
                e.printStackTrace();
                getActivity().runOnUiThread(() -> textViewTemperature.setText("Error"));
            }
        }).start();
    }

    private void updateUI(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONObject main = jsonObject.getJSONObject("main");
            JSONObject weather = jsonObject.getJSONArray("weather").getJSONObject(0);

            double temperature = main.getDouble("temp");
            String description = weather.getString("description");

            textViewTemperature.setText(temperature + "°C");
            textViewDescription.setText(description);

            // Cambiar icono según el clima
            String icon = weather.getString("icon");
            int iconResId = getResources().getIdentifier("icon_" + icon, "drawable", getActivity().getPackageName());
            imageViewWeather.setImageResource(iconResId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
