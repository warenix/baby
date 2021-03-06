package org.dyndns.warenix.baby.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import org.dyndns.warenix.baby.R;
import org.dyndns.warenix.baby.chat.CommandServer;
import org.dyndns.warenix.baby.command.AbstractCommand;
import org.dyndns.warenix.baby.command.CommandProtocol;
import org.dyndns.warenix.baby.command.MusicPlayerCommand;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by warenix on 1/6/18.
 */

public class WebSocketCommandService extends Service {
    public static final String TAG = "WebSocketCommandService ";
    private final IBinder musicBind = new WebSocketCommandBinder();
    String CHANNEL_ID = "music_player";
    private MediaPlayer mp = new MediaPlayer();
    private CommandServer.CommandHandler mCommandHandler = new CommandServer.CommandHandler() {
        @Override
        public void handleCommand(AbstractCommand command) {
            if (command instanceof MusicPlayerCommand) {
                Log.d(TAG, "handle MusicPlayerCommand:" + ((MusicPlayerCommand) command).getCommand());


                MusicPlayerCommand musicPlayerCommand = (MusicPlayerCommand) command;
                try {
                    switch (musicPlayerCommand.getCommand()) {
                        case MusicPlayerCommand.COMMAND_PLAY_MUSIC: {
                            if (mp.isPlaying()) {
                                mp.stop();
                                mp.reset();
                            }

                            mp.setDataSource("/sdcard/Music/浪聲.m4a");
                            mp.prepare();
                            mp.start();

                            break;
                        }
                        case MusicPlayerCommand.COMMAND_STOP_MUSIC: {
                            mp.stop();
                            mp.reset();
                            break;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    };
    //
//    public WebSocketCommandService() {
//        super("WebSocketCommandService");
//    }
    private CommandServer chatServer;
    private WebSocketClient cc;
    private boolean mIsPlayingMusicOnRemote;


//    @Override
//    protected void onHandleIntent(@Nullable Intent intent) {
//        try {
//            setupWebSocketServer();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    private void setupWebSocketServer() throws IOException, InterruptedException {
        String[] args = new String[1];
        int port = 8765;
//        ChatServer.main(args);
        chatServer = new CommandServer(port);
        chatServer.setCommandHandler(mCommandHandler);
        chatServer.start();
        Log.d(TAG, String.format("started chat server at port[%d]", port));
    }

    public void onCreate() {
        //create the service
        super.onCreate();
        Log.d(TAG, "onCreate()");

        try {
            setupWebSocketServer();

            Context mContext = getApplicationContext();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createChannel();
            }
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(mContext, CHANNEL_ID);
            notificationBuilder
                    .setStyle(
                            new NotificationCompat.MessagingStyle("WebSocket")
                    )
                    .setColor(ContextCompat.getColor(mContext, R.color.colorAccent))
                    .setSmallIcon(R.drawable.ic_crying_baby)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setOnlyAlertOnce(true)
                    //.setContentIntent(createContentIntent())
                    .setContentTitle("album")
                    .setContentText("Artist")
                    .setSubText("Song")
//                    .setLargeIcon(MusicLibrary.getAlbumBitmap(mContext, description.getMediaId()))
                    .setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(
                            getApplicationContext(), PlaybackStateCompat.ACTION_STOP));

            final int id = 1;
            startForeground(id, notificationBuilder.build());
            Log.d(TAG, "started service in foreground");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void createChannel() {
        Context mContext = getApplicationContext();

        NotificationManager
                mNotificationManager =
                (NotificationManager) mContext
                        .getSystemService(Context.NOTIFICATION_SERVICE);
        // The id of the channel.
        String id = CHANNEL_ID;
        // The user-visible name of the channel.
        CharSequence name = "Media playback";
        // The user-visible description of the channel.
        String description = "Media playback controls";
        int importance = NotificationManager.IMPORTANCE_LOW;
        NotificationChannel mChannel = new NotificationChannel(id, name, importance);
        // Configure the notification channel.
        mChannel.setDescription(description);
        mChannel.setShowBadge(false);
        mChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        mNotificationManager.createNotificationChannel(mChannel);
    }


    //release resources when unbind
    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind()");

//        player.stop();
//        player.release();
        return false;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()");
        if (chatServer != null) {
            try {
                Log.d(TAG, "onDestroy() stopping command server");
                chatServer.stop();
                Log.d(TAG, "onDestroy() stopped command server");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        stopForeground(true);
    }

    public void connectRemoteServer(String serverIp, final ClientListener listener) throws URISyntaxException {
        int port = 8765;
        String fullRemoteUrl = "ws://" + serverIp + ":" + port;
        Log.d(TAG, "connectRemoteServer()" + fullRemoteUrl);

        cc = new WebSocketClient(new URI(fullRemoteUrl), new Draft_6455()) {

            @Override
            public void onMessage(String message) {
                Log.d(TAG, "cc.onMessage()" + message);
                if (listener != null) {
                    listener.onMessage(message);
                }
            }

            @Override
            public void onOpen(ServerHandshake handshake) {
                Log.d(TAG, "cc.onOpen() connected to remote server:" + getURI());
                if (listener != null) {
                    listener.onOpen(handshake);
                }
//                ta.append( "You are connected to ChatServer: " + getURI() + "\n" );
//                ta.setCaretPosition( ta.getDocument().getLength() );
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
//                ta.append( "You have been disconnected from: " + getURI() + "; Code: " + code + " " + reason + "\n" );
                Log.d(TAG, "onClose() disconnected from: " + getURI() + getURI() + "; Code: " + code + " " + reason);
                if (listener != null) {
                    listener.onClose(code, reason, remote);
                }
            }

            @Override
            public void onError(Exception ex) {
//                ta.append( "Exception occured ...\n" + ex + "\n" );
                ex.printStackTrace();
                if (listener != null) {
                    listener.onError(ex);
                }
            }
        };

        cc.connect();
    }

    public void disconnectRemoteServer() {
        if (cc != null && cc.isOpen()) {
            cc.close();
        }
    }

    public boolean isPlayingMusicOnRemote() {
        return mIsPlayingMusicOnRemote;
    }

    public void playMusicOnRemote() {
        MusicPlayerCommand command = new MusicPlayerCommand();
        command.setCommand(MusicPlayerCommand.COMMAND_PLAY_MUSIC);

        CommandProtocol commandProtocol = new CommandProtocol(command);
        cc.send(commandProtocol.toJsonString());

        mIsPlayingMusicOnRemote = true;
    }

    public void stopMusicOnRemote() {
        MusicPlayerCommand command = new MusicPlayerCommand();
        command.setCommand(MusicPlayerCommand.COMMAND_STOP_MUSIC);

        CommandProtocol commandProtocol = new CommandProtocol(command);
        cc.send(commandProtocol.toJsonString());

        mIsPlayingMusicOnRemote = false;
    }

    public interface ClientListener {
        void onOpen(ServerHandshake handshake);

        void onClose(int code, String reason, boolean remote);

        void onError(Exception ex);

        void onMessage(String message);
    }

    //binder
    public class WebSocketCommandBinder extends Binder {
        public WebSocketCommandService getService() {
            return WebSocketCommandService.this;
        }
    }

}
