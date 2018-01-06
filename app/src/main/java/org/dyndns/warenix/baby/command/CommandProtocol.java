package org.dyndns.warenix.baby.command;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by warenix on 1/6/18.
 */

public class CommandProtocol {
    private static Map<String, Class> sCommandMap = new HashMap();
    protected String type;
    protected Object body;

    public CommandProtocol(String type) {
        this.type = type;
    }

    public static void registerCommand(@NonNull String key, @NonNull Class cls) {
        sCommandMap.put(key, cls);
    }

    /**
     * Parse json string for registered {@link CommandProtocol} dynamically
     *
     * @param jsonString
     * @return
     */
    public static AbstractCommand parseForCommand(@NonNull String jsonString) {
        Gson gson = new Gson();
        CommandProtocol command = gson.fromJson(jsonString, CommandProtocol.class);

        Type type;
        for (Map.Entry<String, Class> entry : sCommandMap.entrySet()) {
            if (command.type.equals(entry.getKey())) {
                type = TypeToken.get(entry.getValue()).getType();
                return gson.fromJson(gson.toJson(command.body), type);
            }
        }

        return null;
    }

    public String toJsonString() {
        return new Gson().toJson(this);
    }
}
