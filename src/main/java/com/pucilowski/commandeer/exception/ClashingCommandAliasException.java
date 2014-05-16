package com.pucilowski.commandeer.exception;

/**
 * Created by martin on 15/05/14.
 */
public class ClashingCommandAliasException extends RuntimeException {
    public ClashingCommandAliasException(String message) {
        super(message);
    }
}
