package com.pucilowski.commandeer;

import com.pucilowski.commandeer.command.CommandDef;
import com.pucilowski.commandeer.command.DefaultTypes;
import com.pucilowski.commandeer.command.ArgumentDef;
import com.pucilowski.commandeer.command.TypeParser;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by martin on 15/05/14.
 */
public class CommandParser {

    private final Commandeer cmd;
    private final CommandDef def;
    private final String input;
    private final String prefix;

    private String name;
    private String argsString;
    private final TreeMap<String, Object> argMap = new TreeMap<>();

    private String error;
    private Command command;

    protected CommandParser(Commandeer cmd, CommandDef def, String input, String prefix) {
        this.cmd = cmd;
        this.def = def;
        this.input = input;
        this.prefix = prefix;
    }

    public CommandDef getCommandDef() {
        return def;
    }

    public boolean matchCommand() {
        if (prefix != null && !input.startsWith(prefix)) {
            return false;
        }

        int start = prefix != null ? prefix.length() : 0;
        String line = input.substring(start);

        String[] parts = line.split(" ");
        name = parts[0];

        try {
            argsString = line.substring(name.length() + 1);
        } catch (RuntimeException e) {
            argsString = null;
        }

        return matchCommandAlias(name);

    }

    public boolean parseCommand() {
        if (name == null) {
            if (!matchCommand()) {
                error = "Input command '" + input + "' does not match command " + def.getFormat() + " prefixed with: " + prefix;
                return false;
            }
        }

        if (argsString != null) {
            parseArguments();
        }

        if (error != null) return false;

        command = new Command(name, argMap);
        return true;
    }

    private void parseArguments() {
        String[] args = tokenize(argsString);

        for (int i = 0; i < def.getArguments().length; i++) {
            ArgumentDef argDef = def.getArguments()[i];

            String arg;
            try {
                arg = args[i];
            } catch (ArrayIndexOutOfBoundsException e) {
                if (argDef.isRequired()) {
                    error = "Argument " + argDef.toString() + " is not optional.";
                }
                return;
            }

            try {
                Object o = parseArgument(argDef, arg);
                argMap.put(argDef.getName(), o);
            } catch (RuntimeException e) {
                error = "'" + arg + "' is not a valid argument value for " + argDef.getName() + ":" + argDef.getType() + " (" + e.toString() + ")";
                return;
            }
        }
    }

    private Object parseArgument(ArgumentDef argDef, String arg) {
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


    public String getError() {
        return error;
    }

    public Command getCommand() {
        if (command == null) parseCommand();

        return command;
    }
}
