package org.dyndns.warenix.baby.command;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Created by warenix on 1/6/18.
 */

public class MusicPlayerCommand extends AbstractCommand {
    /**
     * Play current music
     */
    public static final int COMMAND_PLAY_MUSIC = 1;
    /**
     * Stop current music
     */
    public static final int COMMAND_STOP_MUSIC = 2;
    public
    static final String TYPE = "MusicPlayer";
    private int command;


    public int getCommand() {
        return this.command;
    }

    public void setCommand(@Command int command) {
        this.command = command;
    }

    @Retention(SOURCE)
    @IntDef({COMMAND_PLAY_MUSIC, COMMAND_STOP_MUSIC})
    public @interface Command {
    }
}
