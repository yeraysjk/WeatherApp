package yeray.priede.weatherapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.FragmentContainerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private FragmentContainerView fragmentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Configuramos la Toolbar y la establecemos como ActionBar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fragmentContainer = findViewById(R.id.fragmentContainer);

        // Carga el fragmento de clima por defecto si es la primera vez
        if (savedInstanceState == null) {
            showMusicFragment();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Infla el menú desde res/menu/menu_main.xml
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.item_weather) {
            showWeatherFragment();
            return true;
        } else if (id == R.id.item_music) {
            showMusicFragment();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showWeatherFragment() {
        WeatherFragment weatherFragment = WeatherFragment.newInstance("Barcelona");
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, weatherFragment);
        transaction.commit();
    }

    private void showMusicFragment() {
        MusicFragment musicFragment = MusicFragment.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, musicFragment);
        transaction.commit();
    }
}
