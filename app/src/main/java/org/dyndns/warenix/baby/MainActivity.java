package org.dyndns.warenix.baby;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.dyndns.warenix.baby.service.FCMCommandService;
import org.dyndns.warenix.baby.service.WebSocketCommandService;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TOPIC_MUSIC_PLAYER = "topic__music_player";
    private WebSocketCommandService musicSrv;
    private boolean musicBound = false;
    private String TAG = "MainActivity";
    //connect to the service
    private ServiceConnection musicConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ArrayList<Song> songList = new ArrayList<>();
            songList.add(new Song());
            WebSocketCommandService.WebSocketCommandBinder binder = (WebSocketCommandService.WebSocketCommandBinder) service;
            //get service
            musicSrv = binder.getService();
            //pass list
//            musicSrv.setList(songList);
            musicBound = true;
            Log.d(TAG, "WebSocketCommandService is bound");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };
    private Intent playIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupCommandService();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (playIntent == null) {
            playIntent = new Intent(this, WebSocketCommandService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
//            startService(playIntent);
            ContextCompat.startForegroundService(getApplicationContext(), playIntent);
        }
    }

    private void setupCommandService() {
//        setupFCMCommandService();
//        setupWebSocketCommandService();
    }

//    private void setupWebSocketCommandService() {
//        Intent intent = new Intent(this, WebSocketCommandService.class);
//        startService(intent);
//    }


    @Override
    protected void onDestroy() {
        if (musicBound) {
            unbindService(musicConnection);
            musicBound = false;
        }

        super.onDestroy();
    }

    private void setupFCMCommandService() {
        FCMCommandService.subscribeTopic(TOPIC_MUSIC_PLAYER);
    }
}
