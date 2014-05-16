package com.pucilowski.commandeer.exception;

import com.pucilowski.commandeer.structure.Command;

/**
 * Created by martin on 16/05/14.
 */
public class CommandInputException extends Exception {
    private final Command def;

    public CommandInputException(Command def, String message) {
        super(message);
        this.def = def;
    }

    public Command getCommand() {
        return def;
    }
}
