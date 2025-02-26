package yeray.priede.weatherapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private EditText editTextCity;
    private Button buttonSearch;
    private FragmentContainerView fragmentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextCity = findViewById(R.id.editTextCity);
        buttonSearch = findViewById(R.id.buttonSearch);
        fragmentContainer = findViewById(R.id.fragmentContainer);

        buttonSearch.setOnClickListener(v -> {
            String city = editTextCity.getText().toString().trim();
            if (!city.isEmpty()) {
                showWeatherFragment(city);
            } else {
                Toast.makeText(MainActivity.this, "Ingrese una ciudad", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showWeatherFragment(String city) {
        WeatherFragment weatherFragment = WeatherFragment.newInstance(city);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, weatherFragment)
                .commit();

        fragmentContainer.setVisibility(View.VISIBLE);
    }
}
