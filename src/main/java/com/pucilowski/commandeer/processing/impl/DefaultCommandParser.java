package com.pucilowski.commandeer.processing.impl;

import com.pucilowski.commandeer.exception.CommandFormatException;
import com.pucilowski.commandeer.processing.CommandParser;
import com.pucilowski.commandeer.structure.Command;
import com.pucilowski.commandeer.structure.Parameter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by martin on 16/05/14.
 */
public class DefaultCommandParser implements CommandParser {

    @Override
    public Command parseCommand(String format) {
        return null;
    }

    @Override
    public String formatCommand(Command cmd) {
        StringBuilder sb = new StringBuilder();

        String aliases = formatAliases(cmd.getAliases());
        sb.append(aliases);

        for (Parameter param : cmd.getParameters()) {
            sb.append(formatParameter(param)).append(" ");
        }

        sb.trimToSize();

        return sb.toString();
    }

    @Override
    public String[] parseAliases(String format) {
        String[] aliases = format.split("\\|");

        for (String cmd : aliases) {
            if (cmd.matches(".*\\W.*")) {
                throw new CommandFormatException("'" + format + "' is not a valid command alias");
            }
        }

        return aliases;
    }

    @Override
    public String formatAliases(String[] aliases) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < aliases.length; i++) {
            sb.append(aliases[i])
                    .append(i < aliases.length - 1 ? "|" : " ");
        }

        return sb.toString();
    }


    //private final char[] ENCLOSURE_REQUIRED = {'<','>'};
    //private final char[] ENCLOSURE_OPTIONAL = {'[',']'};
    //(\w+)(:?(\w+)?)
    //(<|\[?)(\w+)(:?(\w+)?)(>|\]?)
    private static final Pattern PARAM_PATTERN = Pattern.compile("(\\w+)(:?(\\w+)?)");
    @Override
    public Parameter parseParameter(final String format) {
        char open = format.charAt(0);
        char close = format.charAt(format.length() - 1);
        boolean angle = open == '<' && close == '>';
        boolean square = open == '[' && close == ']';
        if (!angle && !square) {
            throw new CommandFormatException("Invalid argument definition: \"" + format + "\", argument has to be enclosed in <> or []");
        }

        String inner = format.substring(1, format.length() - 1);

        Matcher m = PARAM_PATTERN.matcher(inner);
        if (!m.matches()) {
            throw new CommandFormatException("Invalid argument definition syntax: \"" + format + "\"");
        }

        String name = m.group(1);
        String type = m.group(3);

        return new Parameter(name, type, !angle);
    }

    @Override
    public String formatParameter(Parameter param) {
        StringBuilder sb = new StringBuilder();

        sb.append(param.isOptional() ? "[" : "<");

        sb.append(param.getName());

        if (param.getType() != null)
            sb.append(":").append(param.getType());

        sb.append(param.isOptional() ? "]" : ">");

        return sb.toString();
    }


}
