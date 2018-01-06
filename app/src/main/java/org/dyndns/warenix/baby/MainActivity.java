package org.dyndns.warenix.baby;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.dyndns.warenix.baby.service.FCMCommandService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupCommandService();
    }

    private static final String TOPIC_MUSIC_PLAYER = "topic__music_player";

    private void setupCommandService() {
        FCMCommandService.subscribeTopic(TOPIC_MUSIC_PLAYER);
    }
}
