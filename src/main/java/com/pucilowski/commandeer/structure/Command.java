package com.pucilowski.commandeer.structure;

import com.pucilowski.commandeer.CommandInput;
import com.pucilowski.commandeer.callbacks.CommandExecutor;
import com.pucilowski.commandeer.processing.CommandParser;

import java.util.Arrays;

/**
 * Created by martin on 15/05/14.
 */

public class Command {

    private final String[] aliases;
    private final Parameter[] parameters;
    private final CommandExecutor executor;

    public Command(String[] aliases, Parameter[] parameters,
                   CommandExecutor executor) {
        this.aliases = aliases;
        this.parameters = parameters;
        this.executor = executor;
    }

    public String[] getAliases() {
        return aliases;
    }

    public Parameter[] getParameters() {
        return parameters;
    }

    public CommandExecutor getExecutor() {
        return executor;
    }

    public void execute(CommandInput cmdIn) {
        executor.execute(cmdIn);
    }

    public String toString(CommandParser cmdParser) {
        return cmdParser.formatCommand(this);
    }

    @Override
    public String toString() {
        return "CommandDef{" +
                "aliases=" + Arrays.toString(aliases) +
                ", arguments=" + Arrays.toString(parameters) +
                '}';
    }
}
