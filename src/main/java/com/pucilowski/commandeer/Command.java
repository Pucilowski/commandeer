package com.pucilowski.commandeer;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by martin on 15/05/14.
 */
public class Command {

    private final String alias;
    private final TreeMap<String, Object> args;

    public Command(String alias, TreeMap<String, Object> args) {
        this.alias = alias;
        this.args = args;
    }

    public String getAlias() {
        return alias;
    }

    public int countArgs() {
        return args.size();
    }

    public TreeMap<String, Object> getArgs() {
        return args;
    }

    public String getArg(String name) {
        return (String) args.get(name);
    }

    public int getArgAsInteger(String name) {
        return (Integer) args.get(name);
    }

    @Override
    public String toString() {
        return "Command{" +
                "alias='" + alias + '\'' +
                ", args=" + descriptiveArgMap() +
                '}';
    }

    private TreeMap<String, Object> descriptiveArgMap() {
        TreeMap<String, Object> types = new TreeMap<>();

        for (Map.Entry<String, Object> entry : args.entrySet()) {
            types.put(entry.getValue().getClass().getSimpleName() + " " + entry.getKey(), entry.getValue());
        }

        return types;
    }
}
