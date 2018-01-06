package org.dyndns.warenix.baby.chat;

import android.util.Log;

import org.dyndns.warenix.baby.command.AbstractCommand;
import org.dyndns.warenix.baby.command.CommandProtocol;
import org.dyndns.warenix.baby.command.HelloCommand;
import org.dyndns.warenix.baby.command.MusicPlayerCommand;
import org.java_websocket.WebSocket;

import java.net.UnknownHostException;
import java.nio.ByteBuffer;

/**
 * Created by warenix on 1/6/18.
 */

public class CommandServer extends ChatServer {
    public static final String TAG = "CommandServer";

    static {
        CommandProtocol.registerCommand(HelloCommand.TYPE, HelloCommand.class);
        CommandProtocol.registerCommand(MusicPlayerCommand.TYPE, MusicPlayerCommand.class);
    }

    private CommandHandler mCommandHandler;

    public CommandServer(int port) throws UnknownHostException {
        super(port);
    }

    public void setCommandHandler(CommandHandler handler) {
        mCommandHandler = handler;

    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        broadcast(message);
        Log.d(TAG, conn + ": " + message);

        AbstractCommand command = CommandProtocol.parseForCommand(message);
        if (command != null && mCommandHandler != null) {
            mCommandHandler.handleCommand(command);
        }
    }

    @Override
    public void onMessage(WebSocket conn, ByteBuffer message) {
        broadcast(message.array());
        Log.d(TAG, conn + ": " + message);
    }

    public interface CommandHandler {

        void handleCommand(AbstractCommand command);
    }
}
