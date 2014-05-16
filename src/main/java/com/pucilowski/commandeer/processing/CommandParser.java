package com.pucilowski.commandeer.processing;

import com.pucilowski.commandeer.Commandeer;
import com.pucilowski.commandeer.structure.Command;
import com.pucilowski.commandeer.structure.Parameter;

/**
 * Created by martin on 16/05/14.
 */
public interface CommandParser {

    Command parseCommand(String format);

    String formatCommand(Command arg);

    String[] parseAliases(String format);

    Parameter parseParameter(String format);

    String formatParameter(Parameter param);


}
