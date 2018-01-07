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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.dyndns.warenix.baby.service.FCMCommandService;
import org.dyndns.warenix.baby.service.WebSocketCommandService;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URISyntaxException;
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
    private Button mConnectRemoteServerButton;
    private TextView mRemoteServerConnectionStatusText;
    // ViewModel
    private boolean mIsConnectedRemoteConnection;
    private WebSocketCommandService.ClientListener mRemoteServerListener = new WebSocketCommandService.ClientListener() {
        @Override
        public void onOpen(ServerHandshake handshake) {
            mIsConnectedRemoteConnection = true;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onConnectedRemoteConnectionChanged();
                }
            });
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
            mIsConnectedRemoteConnection = false;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onConnectedRemoteConnectionChanged();
                }
            });
        }

        @Override
        public void onError(Exception ex) {

        }

        @Override
        public void onMessage(String message) {

        }
    };
    private Button mToggleMusicBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupUI();
        setupCommandService();

        // restore state
        onConnectedRemoteConnectionChanged();
    }

    private void setupUI() {
        final TextView serverIpText = findViewById(R.id.txt_serverip);
        mConnectRemoteServerButton = findViewById(R.id.btn_connect);
        mConnectRemoteServerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mIsConnectedRemoteConnection) {
                    // disconnect
                    musicSrv.disconnectRemoteServer();
                } else {
                    // connect
                    String serverIp = serverIpText.getText().toString().trim();
                    try {
                        musicSrv.connectRemoteServer(serverIp, mRemoteServerListener);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

        mToggleMusicBtn = findViewById(R.id.btn_togglemusic);
        mToggleMusicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (musicSrv.isPlayingMusicOnRemote()) {
                    musicSrv.stopMusicOnRemote();
                } else {
                    musicSrv.playMusicOnRemote();
                }

                updateViewPlayMusicOnRemote();
            }
        });

        mRemoteServerConnectionStatusText = findViewById(R.id.txt_remoteconnectionstatus);
    }

    private void updateViewPlayMusicOnRemote() {
        mToggleMusicBtn.setText(musicSrv.isPlayingMusicOnRemote() ? "Stop" : "Play");
    }

    private void updateRemoteServerConnectionStatusText() {
        mRemoteServerConnectionStatusText.setText(mIsConnectedRemoteConnection ? "Connected" : "Idle");
        mConnectRemoteServerButton.setText(mIsConnectedRemoteConnection ? "Disconnect" : "Connect");
    }

    private void onConnectedRemoteConnectionChanged() {
        updateRemoteServerConnectionStatusText();
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
