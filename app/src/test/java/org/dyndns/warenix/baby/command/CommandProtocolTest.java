package org.dyndns.warenix.baby.command;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

/**
 * Created by warenix on 1/6/18.
 */

public class CommandProtocolTest {

    @Test
    public void parseForCommand() {
        CommandProtocol.registerCommand(HelloCommand.TYPE, HelloCommand.class);
        CommandProtocol.registerCommand(MusicPlayerCommand.TYPE, MusicPlayerCommand.class);

        String jsonString;
        AbstractCommand command;

        jsonString = "{\"type\":\"Hello\",\"body\":{\"message\":\"This is a test\"}}";
        command = CommandProtocol.parseForCommand(jsonString);
        assertThat(command, instanceOf(HelloCommand.class));

        jsonString = "{\"type\":\"MusicPlayer\",\"body\":{\"command\":\"Play\"}}";
        command = CommandProtocol.parseForCommand(jsonString);
        assertThat(command, instanceOf(MusicPlayerCommand.class));

    }
}
