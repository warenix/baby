package org.dyndns.warenix.baby.command;

/**
 * Created by warenix on 1/1/18.
 */

public class HelloCommand extends AbstractCommand {
    public static final String TYPE = "Hello";
    private String message;

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object obj) {
        return message.equals(((HelloCommand) obj).message);
    }

}
