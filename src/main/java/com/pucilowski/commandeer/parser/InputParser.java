package com.pucilowski.commandeer.parser;

import com.pucilowski.commandeer.CommandInput;
import com.pucilowski.commandeer.Commandeer;
import com.pucilowski.commandeer.command.Argument;
import com.pucilowski.commandeer.command.Command;
import com.pucilowski.commandeer.exception.CommandInputException;

import java.util.Map;

/**
 * Created by martin on 16/05/14.
 */
public interface InputParser {


    Map<String, Object> parseArguments(Commandeer cmd, Command def, String argString) throws CommandInputException;

    Object parseArgument(Commandeer cmd, Argument argDef, String arg);
}
