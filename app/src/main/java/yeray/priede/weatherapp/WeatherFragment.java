package yeray.priede.weatherapp;

import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherFragment extends Fragment {

    private EditText editTextCity;
    private Button buttonSearch;
    private TextView textViewWeather;

    // Reemplaza con tu API key válida de OpenWeatherMap
    private final String API_KEY = "5b53e2223479fb3892bc0c13c7f30131";

    public WeatherFragment() {
        // Constructor público requerido
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather, container, false);
        editTextCity = view.findViewById(R.id.editTextCity);
        buttonSearch = view.findViewById(R.id.buttonSearch);
        textViewWeather = view.findViewById(R.id.textViewWeather);

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = editTextCity.getText().toString().trim();
                if (!city.isEmpty()) {
                    new FetchWeatherTask().execute(city);
                } else {
                    textViewWeather.setText("Ingresa una ciudad");
                }
            }
        });
        return view;
    }

    // Tarea asíncrona para obtener datos del clima según la ciudad ingresada
    private class FetchWeatherTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String city = params[0];
            String result = "";
            try {
                // Se agrega &lang=es para obtener la respuesta en español
                String urlString = "https://api.openweathermap.org/data/2.5/weather?q="
                        + city + "&appid=" + API_KEY + "&units=metric&lang=es";

                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(connection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    reader.close();
                    result = stringBuilder.toString();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.isEmpty()) {
                textViewWeather.setText("Error al obtener datos del clima.");
                return;
            }
            try {
                JSONObject jsonObject = new JSONObject(s);
                if (jsonObject.has("main")) {
                    JSONObject main = jsonObject.getJSONObject("main");
                    double temperature = main.getDouble("temp");
                    textViewWeather.setText("Temperatura: " + temperature + "°C");
                } else if(jsonObject.has("message")) {
                    // Si la respuesta contiene un mensaje de error (por ejemplo, ciudad no encontrada)
                    String message = jsonObject.getString("message");
                    textViewWeather.setText("Error: " + message);
                } else {
                    textViewWeather.setText("Ciudad no encontrada o error en la respuesta.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                textViewWeather.setText("Error al procesar datos del clima.");
            }
        }
    }
}
