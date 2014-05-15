package com.pucilowski.commandeer;

import java.util.HashMap;

/**
 * Created by martin on 15/05/14.
 */
public class Command {

    private final String alias;
    private final HashMap<String, Object> args;

    public Command(String alias, HashMap<String, Object> args) {
        this.alias = alias;
        this.args = args;
    }

    public String getAlias() {
        return alias;
    }

    public int countArgs() {
        return args.size();
    }

    public HashMap<String, Object> getArgs() {
        return args;
    }

    public String getArg(String name) {
        return (String) args.get(name);
    }

    public int getArgInteger(String name) {
        return (Integer) args.get(name);
    }

    @Override
    public String toString() {
        return "Command{" +
                "alias='" + alias + '\'' +
                ", args=" + args +
                '}';
    }
}
