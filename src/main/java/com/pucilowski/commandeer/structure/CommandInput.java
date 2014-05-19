package com.pucilowski.commandeer.structure;

import java.util.Map;

/**
 * Created by martin on 15/05/14.
 */
public class CommandInput {

    private final Command def;

    private final String alias;
    private final Map<String, Object> args;

    public CommandInput(Command def, String alias, Map<String, Object> args) {
        this.def = def;
        this.alias = alias;
        this.args = args;
    }

    public Command getCommand() {
        return def;
    }

    public String getAlias() {
        return alias;
    }

    public int countArgs() {
        return args.size();
    }

    public Map<String, Object> getArgumentMap() {
        return args;
    }

    public boolean hasArgument(String name) {
        return args.containsKey(name);
    }

    /**
     * Returns the named parameter value
     * @param name Parameter name
     * @return Argument
     */
    public Object getArgument(String name) {
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
        StringBuilder sb = new StringBuilder();

        sb.append(alias);

        for (Map.Entry<String, Object> entry : args.entrySet()) {
            sb.append(", ");

            String name = entry.getKey();
            Object value = entry.getValue();
            String type = value.getClass().getSimpleName();

            sb.append(name).append(": ").append(value).append(" (").append(type).append(")");
        }

        return sb.toString();
    }
}
