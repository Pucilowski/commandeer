package com.pucilowski.commandeer;

import com.pucilowski.commandeer.command.Argument;
import com.pucilowski.commandeer.command.Command;
import com.pucilowski.commandeer.command.TypeParser;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by martin on 15/05/14.
 */
public class ParsedCommand {

    private final Commandeer cmd;
    private final Command def;
    private final String input;
    private final String prefix;

    private String alias;
    private String argsString;


    private String error;
    //private Command command;

    private final TreeMap<String, Object> args = new TreeMap<>();

    protected ParsedCommand(Commandeer cmd, Command def, String input, String prefix) {
        this.cmd = cmd;
        this.def = def;
        this.input = input;
        this.prefix = prefix;
    }

    public Command getCommandDef() {
        return def;
    }

    private boolean aliasMatch = false;

    public boolean isAliasMatch() {
        return aliasMatch;
    }

    private boolean matchAlias() {
        if (prefix != null && !input.startsWith(prefix)) {
            return false;
        }

        int start = prefix != null ? prefix.length() : 0;
        String line = input.substring(start);

        String[] parts = line.split(" ");
        alias = parts[0];

        try {
            argsString = line.substring(alias.length() + 1);
        } catch (RuntimeException e) {
            argsString = null;
        }

        return (aliasMatch = matchCommandAlias(alias));
    }


    public boolean parseCommand() {
        if (!matchAlias()) {
            error = "Input command '" + input + "' does not match command " + def.getFormat() + " prefixed with: " + prefix;
            return false;
        }

        parseArguments();

        if (error != null) return false;

        //command = new Command(alias, args);
        return true;
    }

    private void parseArguments() {
        String[] inputArgs = new String[0];
        if (argsString != null) inputArgs = tokenize(argsString);

        for (int i = 0; i < def.getArguments().length; i++) {
            Argument argDef = def.getArguments()[i];

            String arg;
            try {
                arg = inputArgs[i];
            } catch (ArrayIndexOutOfBoundsException e) {
                if (argDef.isRequired()) {
                    error = "Argument " + argDef.toString() + " is not optional.";
                }
                return;
            }

            try {
                Object o = parseArgument(argDef, arg);
                args.put(argDef.getName(), o);
            } catch (RuntimeException e) {
                error = "'" + arg + "' is not a valid argument value for " + argDef.getName() + ":" + argDef.getType() + " (" + e.toString() + ")";
                return;
            }
        }
    }

    private Object parseArgument(Argument argDef, String arg) {
        //if(cmd.argTypes==null) throw new NullPointerException();

        TypeParser type = cmd.argTypes.get(argDef.getType());

        return type.parse(arg);
    }

    private boolean matchCommandAlias(String command) {
        for (String alias : def.getAliases()) {
            if (command.equals(alias)) return true;
        }
        return false;
    }


    private static String[] tokenize(String text) {
        ArrayList<String> parts = new ArrayList<String>();

        String regex = "\"([^\"]*)\"|(\\S+)";

        Matcher m = Pattern.compile(regex).matcher(text);
        while (m.find()) {
            if (m.group(1) != null) {
                // System.out.println("Quoted [" + m.group(1) + "]");
                parts.add(m.group(1));
            } else {
                //System.out.println("Plain [" + m.group(2) + "]");
                parts.add(m.group(2));
            }
        }

        return parts.toArray(new String[parts.size()]);
    }

    //command
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

    public String getError() {
        return error;
    }

}
