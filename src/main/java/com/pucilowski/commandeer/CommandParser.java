package com.pucilowski.commandeer;

import com.pucilowski.commandeer.command.CommandDef;
import com.pucilowski.commandeer.command.TypeDefs;
import com.pucilowski.commandeer.command.ArgumentDef;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by martin on 15/05/14.
 */
public class CommandParser {

    private final CommandDef def;
    private final String input;
    private final String prefix;

    private String name;
    private String argsString;

    private String error;
    Command command;

    public CommandParser(CommandDef def, String input, String prefix) {
        this.def = def;
        this.input = input;
        this.prefix = prefix;
    }

    public CommandParser(CommandDef def, String input) {
        this(def, input, null);
    }

    public boolean matchCommand() {
        if (prefix != null && !input.startsWith(prefix)) {
            return false;
        }

        int start = prefix != null ? prefix.length() : 0;
        String line = input.substring(start);

        String[] parts = line.split(" ");
        name = parts[0];
        argsString = line.substring(name.length() + 1);

        return matchCommandAlias(name);

    }

    public boolean parseCommand() {
        if (name == null) {
            if (!matchCommand()) {
                error = "Input command '" + input + "' does not match command " + def.getFormat() + " prefixed with: " + prefix;
                return false;
            }
        }

        String[] args = tokenize(argsString);
        HashMap<String, Object> argMap = new HashMap<>();

        for (int i = 0; i < def.getArguments().length; i++) {
            ArgumentDef argDef = def.getArguments()[i];

            String arg;
            try {
                arg = args[i];
            } catch (ArrayIndexOutOfBoundsException e) {
                if (argDef.isRequired()) {
                    error = "Argument "+argDef.toString()+" is not optional.";
                    return false;
                }
                break;
            }

            try {
                Object o = parseArgument(argDef, arg);
                argMap.put(argDef.getName(), o);
            } catch (RuntimeException e) {
                //error = "Cannot accept '" + arg + "' as value for argument " + argDef.getName() + ":" + argDef.getType();
                error = "Cannot accept '" + arg + "' as value for argument " + argDef.getName() + ":" + argDef.getType();
                return false;
            }
        }

        command = new Command(name, argMap);
        return true;
    }


    private Object parseArgument(ArgumentDef argDef, String arg) {
        TypeDefs.TypeParser type = TypeDefs.DEFAULT_TYPES.get(argDef.getType());

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
