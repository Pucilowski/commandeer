package com.pucilowski.commandeer.parser.impl;

import com.pucilowski.commandeer.parser.InputPreParser;

/**
 * Created by martin on 16/05/14.
 */
public class DefaultInputPreParser implements InputPreParser {

    public static String minusPrefix(String input, String prefix) {
        if (prefix == null) return input;

        if (input.startsWith(prefix)) {
            int start = prefix.length();
            return input.substring(start);
        }

        return null;
    }

    @Override
    public PreParsed preparse(String input, String prefix) {
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


}
