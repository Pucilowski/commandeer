package com.pucilowski.commandeer.command;

import java.util.Arrays;

/**
 * Created by martin on 15/05/14.
 */

/*
format:
cmd|command <arg1> [arg2]

or typed:
cmd|command <arg1:int> [arg2:string]
 */

public class CommandDef {

    private final String format;
    private final String[] aliases;
    private final ArgumentDef[] arguments;

    public CommandDef(String format, String[] aliases, ArgumentDef[] arguments) {
        this.format = format;
        this.aliases = aliases;
        this.arguments = arguments;
    }


    public String getFormat() {
        return format;
    }

    public String[] getAliases() {
        return aliases;
    }

    public ArgumentDef[] getArguments() {
        return arguments;
    }

    @Override
    public String toString() {
        return "CommandDef{" +
                "format='" + format + '\'' +
                ", aliases=" + Arrays.toString(aliases) +
                ", arguments=" + Arrays.toString(arguments) +
                '}';
    }
}
