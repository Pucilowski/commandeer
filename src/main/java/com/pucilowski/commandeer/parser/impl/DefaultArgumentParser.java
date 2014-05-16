package com.pucilowski.commandeer.parser.impl;

import com.pucilowski.commandeer.command.Argument;
import com.pucilowski.commandeer.exception.CommandFormatException;
import com.pucilowski.commandeer.parser.ArgumentParser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by martin on 16/05/14.
 */
public class DefaultArgumentParser implements ArgumentParser {

    //private final char[] ENCLOSURE_REQUIRED = {'<','>'};
    //private final char[] ENCLOSURE_OPTIONAL = {'[',']'};

    //(\w+)(:?(\w+)?)
    //(<|\[?)(\w+)(:?(\w+)?)(>|\]?)
    private static final Pattern ARG_PATTERN = Pattern.compile("(\\w+)(:?(\\w+)?)");
    private Argument parseArg(final String argDef) {
        char open = argDef.charAt(0);
        char close = argDef.charAt(argDef.length() - 1);
        boolean angle = open == '<' && close == '>';
        boolean square = open == '[' && close == ']';
        if (!angle && !square) {
            throw new CommandFormatException("Invalid argument definition: \"" + argDef + "\", argument has to be enclosed in <> or []");
        }

        String inner = argDef.substring(1, argDef.length() - 1);

        Matcher m = ARG_PATTERN.matcher(inner);
        if (!m.matches()) {
            throw new CommandFormatException("Invalid argument definition syntax: \"" + argDef + "\"");
        }

        String name = m.group(1);
        String type = m.group(3);

        return new Argument(this, name, type, angle);
    }

    @Override
    public Argument parse(String format) {
        return parseArg(format);
    }

    @Override
    public String serialize(Argument arg) {
        StringBuilder sb = new StringBuilder();

        if (arg.isRequired()) sb.append("<");
        else sb.append("[");

        sb.append(arg.getName());

        if (arg.getType() != null)
            sb.append(":").append(arg.getType());

        if (arg.isRequired()) sb.append(">");
        else sb.append("]");

        return sb.toString();
    }
}
