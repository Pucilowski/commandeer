package com.pucilowski.commandeer.parser;

import com.pucilowski.commandeer.command.Argument;

/**
 * Created by martin on 16/05/14.
 */
public interface ArgumentParser {

    public Argument parse(String format) ;

    public String serialize(Argument arg);

}
