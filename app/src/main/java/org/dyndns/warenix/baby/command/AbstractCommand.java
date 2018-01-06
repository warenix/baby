package org.dyndns.warenix.baby.command;

import com.google.gson.Gson;

/**
 * Created by warenix on 1/6/18.
 */

public class AbstractCommand {

    public String toJsonString() {
        return new Gson().toJson(this);
    }
}
