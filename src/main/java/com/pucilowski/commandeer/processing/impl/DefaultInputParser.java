package com.pucilowski.commandeer.processing.impl;

import com.pucilowski.commandeer.Commandeer;
import com.pucilowski.commandeer.exception.CommandInputException;
import com.pucilowski.commandeer.processing.InputParser;
import com.pucilowski.commandeer.structure.Command;
import com.pucilowski.commandeer.structure.DefaultTypes;
import com.pucilowski.commandeer.structure.Parameter;
import com.pucilowski.commandeer.structure.TypeParser;

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
    public PreParsed preParse(String input, String prefix) {
        String line = minusPrefix(input, prefix);
        if(line==null) return null;

        String[] parts = line.split(" ");

        String alias = parts[0];

        String argsString;
        try {
            argsString = line.substring(alias.length() + 1);
        } catch (RuntimeException e) {
            argsString = null;
        }

        return new PreParsed(alias, argsString);
    }


    @Override
    public Map<String, Object> parseArguments(Commandeer cmd, Command def, String argString) throws CommandInputException {
        Map<String, Object> argMap = new TreeMap<>();

        String[] inputArgs = new String[0];
        if (argString != null) inputArgs = tokenize(argString);

        for (int i = 0; i < def.getParameters().length; i++) {
            Parameter argDef = def.getParameters()[i];

            String arg;
            try {
                arg = inputArgs[i];
            } catch (ArrayIndexOutOfBoundsException e) {
                if (!argDef.isOptional()) {
                    String error = "Argument " + argDef.toString() + " is not optional.";
                    throw new CommandInputException(def, error);
                }
                break;
            }

            try {
                Object o = parseArgument(cmd, argDef, arg);
                argMap.put(argDef.getName(), o);
            } catch (RuntimeException e) {
                String error = "'" + arg + "' is not a valid argument value for " + argDef.getName() + ":" + argDef.getType() + " (" + e.toString() + ")";
                throw new CommandInputException(def, error);
            }
        }

        return argMap;
    }

    @Override
    public Object parseArgument(Commandeer cmd, Parameter param, String arg) {
        if (param.getType() == null) {
            return DefaultTypes.STRING.parse(arg);
        }

        TypeParser type = cmd.getArgTypes().get(param.getType());
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




    public static String minusPrefix(String input, String prefix) {
        if (prefix == null) return input;

        if (input.startsWith(prefix)) {
            int start = prefix.length();
            return input.substring(start);
        }

        return null;
    }




}
