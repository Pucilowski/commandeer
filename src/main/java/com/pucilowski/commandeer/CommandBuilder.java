package com.pucilowski.commandeer;

import com.pucilowski.commandeer.exception.MalformedCommandFormatException;
import com.pucilowski.commandeer.command.ArgumentDef;
import com.pucilowski.commandeer.command.CommandDef;

import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by martin on 15/05/14.
 */
public class CommandBuilder {

    public static CommandDef defineCommand(String format) {
        String[] names = null;

        String[] parts = format.split(" ");
        if (parts.length == 0) {
            throw new MalformedCommandFormatException("Command format not specified");
        }

        TreeSet<String> argNames = new TreeSet<>();
        ArgumentDef[] arguments = new ArgumentDef[parts.length - 1];

        boolean pastRequired = false;
        for (int i = 0; i < parts.length; i++) {
            if (i == 0) {
                names = parseNames(format, parts[0]);
                continue;
            }
            ArgumentDef argDef = parseArg(parts[i]);

            if (argDef.isRequired()) {
                if (pastRequired)
                    throw new MalformedCommandFormatException("Optional arguments must follow required ones.");
            } else {
                pastRequired = true;
            }

            if(argNames.contains(argDef.getName())) {
                throw new MalformedCommandFormatException("Repeated argument name '"+argDef.getName()+"'. Argument names must be unique.");
            }

            arguments[i - 1] = argDef;
            argNames.add(argDef.getName());
        }

        if (names == null) throw new MalformedCommandFormatException("Failed to parse command aliases");

        return new CommandDef(format, names, arguments);
    }


    private static String[] parseNames(String format, String cmdDef) {
        String[] cmdsDef = cmdDef.split("\\|");
        String[] names = new String[cmdsDef.length];

        for (int i = 0; i < cmdsDef.length; i++) {
            String cmd = cmdsDef[i];
            if (cmd.matches(".*\\W.*")) {
                //throw new MalformedCommandFormat("Invalid command name definition syntax: \"" + format + "\", invalid command name definition on cmd: " + cmd);
                throw new MalformedCommandFormatException("'" + cmdDef + "' is not a valid command alias");
            }
            names[i] = cmd;
        }
        return names;
    }


    //(\w+)(:?(\w+)?)
    //(<|\[?)(\w+)(:?(\w+)?)(>|\]?)
    private static final Pattern ARG_PATTERN = Pattern.compile("(\\w+)(:?(\\w+)?)");

    private static ArgumentDef parseArg(final String argDef) {
        char open = argDef.charAt(0);
        char close = argDef.charAt(argDef.length() - 1);
        boolean crocs = open == '<' && close == '>';
        boolean square = open == '[' && close == ']';
        if (!crocs && !square) {
            throw new MalformedCommandFormatException("Invalid argument definition: \"" + argDef + "\", argument has to be enclosed in <> or []");
        }

        String inner = argDef.substring(1, argDef.length() - 1);

        Matcher m = ARG_PATTERN.matcher(inner);
        if (!m.matches()) {
            throw new MalformedCommandFormatException("Invalid argument definition syntax: \"" + argDef + "\"");
        }

        String name = m.group(1);
        String type = m.group(3);
        if (type == null) type = "string";

        return new ArgumentDef(name, type, crocs);
    }

}
