package org.dyndns.warenix.baby.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import org.dyndns.warenix.baby.chat.CommandServer;
import org.dyndns.warenix.baby.command.AbstractCommand;
import org.dyndns.warenix.baby.command.MusicPlayerCommand;

import java.io.IOException;

/**
 * Created by warenix on 1/6/18.
 */

public class WebSocketCommandService extends IntentService {
    public static final String TAG = "WebSocketCommandService ";
    private CommandServer.CommandHandler mCommandHandler = new CommandServer.CommandHandler() {
        @Override
        public void handleCommand(AbstractCommand command) {
            if (command instanceof MusicPlayerCommand) {
                Log.d(TAG, "handle MusicPlayerCommand:" + ((MusicPlayerCommand) command).getCommand());
            }
        }
    };

    public WebSocketCommandService() {
        super("WebSocketCommandService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        try {
            setupWebSocketServer();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void setupWebSocketServer() throws IOException, InterruptedException {
        String[] args = new String[1];
        int port = 8765;
//        ChatServer.main(args);
        CommandServer chatServer = new CommandServer(port);
        chatServer.setCommandHandler(mCommandHandler);
        chatServer.start();
        Log.d(TAG, String.format("started chat server at port[%d]", port));
    }
}
