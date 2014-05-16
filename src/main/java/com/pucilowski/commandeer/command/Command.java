package com.pucilowski.commandeer.command;

import com.pucilowski.commandeer.callbacks.CommandExecutor;

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

public class Command {

    private final String format;
    private final String[] aliases;
    private final Argument[] arguments;

    private final CommandExecutor executor;

    public Command(String format, String[] aliases, Argument[] arguments,
                   CommandExecutor executor) {
        this.format = format;
        this.aliases = aliases;
        this.arguments = arguments;
        this.executor = executor;
    }

    public Command(String format, String[] aliases, Argument[] arguments) {
        this(format, aliases, arguments, null);
    }


    public String getFormat() {
        return format;
    }

    public String[] getAliases() {
        return aliases;
    }

    public Argument[] getArguments() {
        return arguments;
    }

    public CommandExecutor getExecutor() {
        return executor;
    }

    @Override
    public String toString() {
        return "CommandDef{" +
                "format='" + format + '\'' +
                ", aliases=" + Arrays.toString(aliases) +
                ", arguments=" + Arrays.toString(arguments) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Command command = (Command) o;

        if (!format.equals(command.format)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return format.hashCode();
    }
}
