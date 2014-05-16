package com.pucilowski.commandeer.processing;

import com.pucilowski.commandeer.Commandeer;
import com.pucilowski.commandeer.exception.CommandInputException;
import com.pucilowski.commandeer.structure.Command;
import com.pucilowski.commandeer.structure.Parameter;

import java.util.Map;

/**
 * Created by martin on 16/05/14.
 */
public interface InputParser {

    PreParsed preParse(String input, String prefix);

    Map<String, Object> parseArguments(Commandeer cmd, Command def, String argString) throws CommandInputException;

    Object parseArgument(Commandeer cmd, Parameter argDef, String arg);

    public static class PreParsed {
        String alias;
        String argString;

        public PreParsed(String alias, String argString) {
            this.alias = alias;
            this.argString = argString;
        }

        public String getAlias() {
            return alias;
        }

        public String getArgString() {
            return argString;
        }
    }
}
