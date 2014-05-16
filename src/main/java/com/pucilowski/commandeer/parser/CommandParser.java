package com.pucilowski.commandeer.parser;

import com.pucilowski.commandeer.command.Argument;
import com.pucilowski.commandeer.command.Command;

/**
 * Created by martin on 16/05/14.
 */
public interface CommandParser {



    public String[] parseAliases(String format) ;

    public String serialize(Command arg);

}
