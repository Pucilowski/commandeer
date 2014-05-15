package com.pucilowski.commandeer.exception;

/**
 * Created by martin on 15/05/14.
 */
public class MalformedCommandFormatException extends RuntimeException {
    public MalformedCommandFormatException(String message) {
        super(message);
    }
}
