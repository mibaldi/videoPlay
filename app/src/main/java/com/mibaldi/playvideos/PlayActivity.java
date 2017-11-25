package com.mibaldi.playvideos;

import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

public class PlayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        Bundle extras = getIntent().getExtras();
        VideoView videoReproductor = (VideoView)findViewById(R.id.video_reproductor);
        if (extras != null){
            String uri = extras.getString("uri");
            if (uri != null && !uri.isEmpty()) {

                // Se crean los controles multimedia.
                MediaController mediaController = new MediaController(this);
                // Asigna los controles multimedia a la VideoView.
                videoReproductor.setMediaController(mediaController);

                try {
                    videoReproductor.setVideoURI(Uri.parse(uri));
                    // Se asigna el foco a la VideoView.
                    videoReproductor.requestFocus();
                    videoReproductor.start();
                } catch (Exception e) {
                    Log.e("Error", e.getMessage());
                    e.printStackTrace();
                }

            }
        }
    }

}
