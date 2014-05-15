package com.pucilowski.commandeer;

import com.pucilowski.commandeer.command.DefaultTypes;
import com.pucilowski.commandeer.command.TypeParser;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by martin on 15/05/14.
 */
public class CommandeerBuilder {

    private String prefix = null;
    Map<String, TypeParser> argTypes = new HashMap<>();

    public CommandeerBuilder() {
        argTypes.put("text", DefaultTypes.STRING);
        argTypes.put("int", DefaultTypes.INTEGER);
        argTypes.put("real", DefaultTypes.DOUBLE);
    }

    public CommandeerBuilder clearArgTypes() {
        argTypes.clear();
        return this;
    }

    public CommandeerBuilder addType(String name, TypeParser parser) {
        argTypes.put(name, parser);
        return this;
    }

    public CommandeerBuilder setPrefix(String prefix) {
        this.prefix=prefix;
        return this;
    }

    public Commandeer create() {
        return new Commandeer(argTypes, prefix);
    }
}
