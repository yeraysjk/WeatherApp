package yeray.priede.weatherapp;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class MusicFragment extends Fragment {

    private MediaPlayer mediaPlayer;
    private Button playButton, pauseButton, stopButton;

    public MusicFragment() {}

    public static MusicFragment newInstance() {
        return new MusicFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music, container, false);

        // Inicializar botones
        playButton = view.findViewById(R.id.buttonPlayMusic);
        pauseButton = view.findViewById(R.id.buttonPauseMusic);
        stopButton = view.findViewById(R.id.buttonStopMusic);

        // Listeners para los botones
        playButton.setOnClickListener(v -> playMusic());
        pauseButton.setOnClickListener(v -> pauseMusic());
        stopButton.setOnClickListener(v -> stopMusic());

        return view;
    }

    private void playMusic() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(getContext(), R.raw.music);
            mediaPlayer.setOnCompletionListener(mp -> stopMusic());
        }
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            Toast.makeText(getContext(), "Reproduciendo música...", Toast.LENGTH_SHORT).show();
        }
    }

    private void pauseMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            Toast.makeText(getContext(), "Música en pausa", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            Toast.makeText(getContext(), "Música detenida", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
