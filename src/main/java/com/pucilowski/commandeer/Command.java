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

    public boolean hasArg(String name) {
        return args.containsKey(name);
    }

    public Object getArg(String name) {
        return args.get(name);
    }

    public String getArgAsString(String name) {
        return (String) args.get(name);
    }

    public Integer getArgAsInteger(String name) {
        return (Integer) args.get(name);
    }

    public double getArgAsDouble(String name) {
        return (Double) args.get(name);
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
            String type = entry.getValue().getClass().getSimpleName();
            types.put(entry.getKey() + " (" + type + ")", entry.getValue());
        }

        return types;
    }
}
