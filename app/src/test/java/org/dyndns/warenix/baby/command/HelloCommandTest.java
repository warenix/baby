package org.dyndns.warenix.baby.command;

import com.google.gson.Gson;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by warenix on 1/6/18.
 */

public class HelloCommandTest {
    private final static String sMessage = "This is a test";
    private final static String sExpectedJsonString = "{\"message\":\"This is a test\"}";

    @Test
    public void command_ToJson_ReturnsTrue() {
        HelloCommand command = new HelloCommand();
        command.setMessage(sMessage);
        String jsonString = command.toJsonString();

        assertThat(jsonString, is(sExpectedJsonString));
    }

    @Test
    public void command_FromJson_ReturnsTrue() {
        HelloCommand command = new Gson().fromJson(sExpectedJsonString, HelloCommand.class);

        HelloCommand expected = new HelloCommand();
        expected.setMessage(sMessage);

        assertThat(command, is(expected));
    }

}
