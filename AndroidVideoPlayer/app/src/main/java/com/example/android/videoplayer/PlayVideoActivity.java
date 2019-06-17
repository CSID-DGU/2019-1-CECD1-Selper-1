package com.example.android.videoplayer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.VideoView;
import android.widget.MediaController;

public class PlayVideoActivity extends AppCompatActivity {

    Intent intent;
    String path;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);

        intent = getIntent();
        path = intent.getStringExtra("FilePath");
        final VideoView videoView = (VideoView)findViewById(R.id.videoView);
        videoView.setVideoPath(path);
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        mediaController.setPadding(0, 0, 0, 80);
        videoView.setMediaController(mediaController);
        videoView.start();
    }
}
