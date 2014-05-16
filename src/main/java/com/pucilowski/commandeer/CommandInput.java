package com.pucilowski.commandeer;

import com.pucilowski.commandeer.command.Argument;
import com.pucilowski.commandeer.command.Command;
import com.pucilowski.commandeer.command.DefaultTypes;
import com.pucilowski.commandeer.command.TypeParser;
import com.pucilowski.commandeer.exception.CommandInputException;
import com.pucilowski.commandeer.parser.InputPreParser;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by martin on 15/05/14.
 */
public class CommandInput {

    //private final Commandeer cmd;
    private final Command def;

    private final String alias;
    private final Map<String, Object> args;

    protected CommandInput(Command def, String alias,Map<String, Object> args ) {
        this.def = def;
        this.alias=alias;
        this.args=args;
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

    public Map<String, Object> getArgs() {
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
