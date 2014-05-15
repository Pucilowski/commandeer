package com.pucilowski.commandeer.command;

/**
 * Created by martin on 15/05/14.
 */
public interface TypeParser<E> {
    public E parse(String input);
}
