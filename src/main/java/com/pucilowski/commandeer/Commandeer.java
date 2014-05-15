package com.pucilowski.commandeer;

import com.pucilowski.commandeer.command.ArgumentDef;
import com.pucilowski.commandeer.command.CommandDef;
import com.pucilowski.commandeer.command.DefaultTypes;
import com.pucilowski.commandeer.command.TypeParser;
import com.pucilowski.commandeer.exception.MalformedCommandFormatException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by martin on 15/05/14.
 */
public class Commandeer {

    public final Map<String, TypeParser> argTypes;
    private final String prefix;

    ArrayList<CommandDef> commands = new ArrayList<>();

    public Commandeer() {
        this(null, null);
    }

    public Commandeer(Map<String, TypeParser> argTypes) {
        this(argTypes, null);
    }

    public Commandeer(Map<String, TypeParser> newArgTypes, String prefix) {
        if (newArgTypes == null) {
            this.argTypes = new HashMap<>();
            this.argTypes.put("text", DefaultTypes.STRING);
            this.argTypes.put("int", DefaultTypes.INTEGER);
            this.argTypes.put("real", DefaultTypes.DOUBLE);
        } else
            this.argTypes = newArgTypes;

        this.prefix = prefix;
    }

    public CommandDef addCommand(String format) {
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

            if (argNames.contains(argDef.getName())) {
                throw new MalformedCommandFormatException("Repeated argument name '" + argDef.getName() + "'. Argument names must be unique.");
            }

            arguments[i - 1] = argDef;
            argNames.add(argDef.getName());
        }

        if (names == null) throw new MalformedCommandFormatException("Failed to parse command aliases");

        CommandDef def = new CommandDef(format, names, arguments);
        commands.add(def);
        return def;
    }

    public ParsedCommand parse(String input, String prefix) {
        for (CommandDef def : commands) {
            ParsedCommand parser = new ParsedCommand(this, def, input, prefix);
            parser.parseCommand();
            if (parser.isAliasMatch()) {
                return parser;
            }
        }

        return null;
    }

    public ParsedCommand parse(String input) {
        return parse(input, prefix);
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
        boolean angle = open == '<' && close == '>';
        boolean square = open == '[' && close == ']';
        if (!angle && !square) {
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

        return new ArgumentDef(name, type, angle);
    }


}
