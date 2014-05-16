package com.pucilowski.commandeer.parser.impl;

import com.pucilowski.commandeer.CommandInput;
import com.pucilowski.commandeer.Commandeer;
import com.pucilowski.commandeer.command.Argument;
import com.pucilowski.commandeer.command.Command;
import com.pucilowski.commandeer.command.DefaultTypes;
import com.pucilowski.commandeer.command.TypeParser;
import com.pucilowski.commandeer.exception.CommandInputException;
import com.pucilowski.commandeer.parser.InputParser;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by martin on 16/05/14.
 */
public class DefaultInputParser implements InputParser {

    @Override
    public Map<String, Object> parseArguments(Commandeer cmd, Command def, String argString) throws CommandInputException {
        Map<String, Object> argMap = new TreeMap<>();

        String[] inputArgs = new String[0];
        if (argString != null) inputArgs = tokenize(argString);

        for (int i = 0; i < def.getArguments().length; i++) {
            Argument argDef = def.getArguments()[i];

            String arg;
            try {
                arg = inputArgs[i];
            } catch (ArrayIndexOutOfBoundsException e) {
                if (argDef.isRequired()) {
                    String error = "Argument " + argDef.toString() + " is not optional.";
                    throw new CommandInputException(error);
                }
                break;
            }

            try {
                Object o = parseArgument(cmd, argDef, arg);
                argMap.put(argDef.getName(), o);
            } catch (RuntimeException e) {
                String error = "'" + arg + "' is not a valid argument value for " + argDef.getName() + ":" + argDef.getType() + " (" + e.toString() + ")";
                throw new CommandInputException(error);
            }
        }

        return argMap;
    }

    private Object parseArgument(Commandeer cmd, Argument argDef, String arg) {
        if (argDef.getType() == null) {
            return DefaultTypes.STRING.parse(arg);
        }

        TypeParser type = cmd.getArgTypes().get(argDef.getType());
        return type.parse(arg);
    }

    private static String[] tokenize(String text) {
        ArrayList<String> parts = new ArrayList<>();

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
}
