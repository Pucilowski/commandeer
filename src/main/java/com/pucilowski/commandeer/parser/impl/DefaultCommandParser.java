package com.pucilowski.commandeer.parser.impl;

import com.pucilowski.commandeer.command.Command;
import com.pucilowski.commandeer.exception.CommandFormatException;
import com.pucilowski.commandeer.parser.CommandParser;

/**
 * Created by martin on 16/05/14.
 */
public class DefaultCommandParser implements CommandParser {

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
    public String serialize(Command arg) {
        return null;
    }
}
