package com.pucilowski.commandeer.wrappers;

import com.pucilowski.commandeer.command.Argument;
import com.pucilowski.commandeer.parser.ArgumentParser;

/**
 * Created by martin on 15/05/14.
 */
public class MockArgDef {

    ArgumentParser parser;

    public final String expectedFormat;

    public final String name;
    public final String type;
    public final boolean required;

    public final Argument expectedArg;

    public MockArgDef(ArgumentParser parser, String format, String name, String type, boolean required) {
        this.parser=parser;
        this.expectedFormat = format;

        this.name = name;
        this.type = type;
        this.required = required;

        expectedArg = new Argument(parser, name, type, required);
    }

    public static MockArgDef[] mockArgs() {
        return new MockArgDef[]{};
    }
}
