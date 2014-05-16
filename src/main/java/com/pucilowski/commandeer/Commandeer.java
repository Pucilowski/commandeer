package com.pucilowski.commandeer;

import com.pucilowski.commandeer.callbacks.CommandError;
import com.pucilowski.commandeer.callbacks.CommandExecutor;
import com.pucilowski.commandeer.command.Argument;
import com.pucilowski.commandeer.command.Command;
import com.pucilowski.commandeer.command.DefaultTypes;
import com.pucilowski.commandeer.command.TypeParser;
import com.pucilowski.commandeer.exception.ClashingCommandAliasException;
import com.pucilowski.commandeer.exception.CommandFormatException;
import com.pucilowski.commandeer.exception.CommandInputException;
import com.pucilowski.commandeer.parser.ArgumentParser;
import com.pucilowski.commandeer.parser.CommandParser;
import com.pucilowski.commandeer.parser.InputParser;
import com.pucilowski.commandeer.parser.InputPreParser;
import com.pucilowski.commandeer.parser.impl.DefaultArgumentParser;
import com.pucilowski.commandeer.parser.impl.DefaultCommandParser;
import com.pucilowski.commandeer.parser.impl.DefaultInputParser;
import com.pucilowski.commandeer.parser.impl.DefaultInputPreParser;

import java.util.*;

/**
 * Created by martin on 15/05/14.
 */
public class Commandeer {

    private final CommandParser commandParser;
    private final ArgumentParser argumentParser;
    private final InputPreParser inputPreParser;
    private final InputParser inputParser;

    private final String defaultPrefix;
    private final Map<String, TypeParser> argTypes;
    private final CommandError onError;

    List<Command> cmds = new ArrayList<>();
    Map<String, Command> cmdsAliased = new TreeMap<>();

    private Commandeer(
            CommandParser commandParser, ArgumentParser argumentParser,
            InputPreParser inputPreParser, InputParser inputParser,
            String defaultPrefix, Map<String, TypeParser> argTypes, CommandError onError) {
        this.argTypes = argTypes;
        this.defaultPrefix = defaultPrefix;
        this.commandParser = commandParser;
        this.argumentParser = argumentParser;
        this.inputPreParser = inputPreParser;
        this.inputParser = inputParser;
        this.onError = onError;
    }

    public Command registerCommand(String format) {
        return registerCommand(format, null);
    }

    public Command registerCommand(String format, CommandExecutor executor) {
        List<String> parts = new ArrayList<>(Arrays.asList(format.split(" ")));
        if (parts.size() == 0) {
            throw new CommandFormatException("Command format not specified");
        }

        // aliases
        String aliasesPart = parts.remove(0);
        String[] aliases = commandParser.parseAliases(aliasesPart);
        if (aliases == null) throw new CommandFormatException("Failed to parse command aliases");
        for (String alias : aliases) {
            if (cmdsAliased.containsKey(alias)) {
                Command cmd = cmdsAliased.get(alias);
                throw new ClashingCommandAliasException("Command alias '" + alias + "' is in use by " + cmd.getFormat());
            }
        }


        TreeSet<String> argNames = new TreeSet<>();
        Argument[] arguments = new Argument[parts.size()];

        boolean pastRequired = false;
        for (int i = 0; i < parts.size(); i++) {
            String arg = parts.get(i);
            Argument argDef = argumentParser.parse(arg);

            if (argDef.isRequired()) {
                if (pastRequired)
                    throw new CommandFormatException("Optional arguments must follow required ones.");
            } else {
                pastRequired = true;
            }

            if (argNames.contains(argDef.getName())) {
                throw new CommandFormatException("Repeated argument name '" + argDef.getName() + "'. Argument names must be unique.");
            }

            arguments[i] = argDef;
            argNames.add(argDef.getName());

        }

        Command def = new Command(format, aliases, arguments, executor);

        cmds.add(def);
        for (String alias : aliases) {
            cmdsAliased.put(alias, def);
        }

        return def;
    }


    public CommandInput parse(String input, String prefix) throws CommandInputException {
        InputPreParser.PreParsed preParsed = inputPreParser.preparse(input, prefix);
        if (preParsed == null) {
            throw new CommandInputException("Could not preprocess input '" + input + "'");
        }

        String alias = preParsed.getAlias();

        Command def = cmdsAliased.get(alias);
        if (def == null) throw new CommandInputException("Not a valid command");

        Map<String, Object> argMap = inputParser.parseArguments(this, def, preParsed.getArgString());

        return new CommandInput(def, preParsed.getAlias(), argMap);
    }

    public CommandInput parse(String input) throws CommandInputException {
        return parse(input, defaultPrefix);
    }

    public void execute(String input, String prefix) {
        CommandInput in;
        try {
            in = parse(input, prefix);
            Command def = in.getCommand();
            CommandExecutor cb = def.getExecutor();
            if (cb != null) cb.execute(in);
        } catch (CommandInputException e) {
            onError.onError(input, e.getMessage());
        }
    }

    public void execute(String input) {
        execute(input, defaultPrefix);
    }

    public Map<String, TypeParser> getArgTypes() {
        return argTypes;
    }

    /**
     * Created by martin on 15/05/14.
     */
    public static class Builder {

        private CommandParser commandParser = new DefaultCommandParser();
        private ArgumentParser argumentParser = new DefaultArgumentParser();
        private InputPreParser inputPreParser = new DefaultInputPreParser();
        private InputParser inputParser = new DefaultInputParser();

        private String defaultPrefix = null;
        private Map<String, TypeParser> argTypes = new HashMap<>();
        private CommandError onError = (input, error) -> System.out.println("bad input: '" + error + "' (" + input + ")");

        public Builder() {
            argTypes.put("text", DefaultTypes.STRING);
            argTypes.put("int", DefaultTypes.INTEGER);
            argTypes.put("real", DefaultTypes.DOUBLE);
        }

        public Builder setCommandParser(CommandParser commandParser) {
            this.commandParser = commandParser;
            return this;
        }

        public Builder setArgumentParser(ArgumentParser argumentParser) {
            this.argumentParser = argumentParser;
            return this;
        }

        public Builder setInputPreParser(InputPreParser inputPreParser) {
            this.inputPreParser = inputPreParser;
            return this;
        }

        public Builder setInputParser(InputParser inputParser) {
            this.inputParser = inputParser;
            return this;
        }

        public Builder setDefaultPrefix(String defaultPrefix) {
            this.defaultPrefix = defaultPrefix;
            return this;
        }

        public Builder clearArgTypes() {
            argTypes.clear();
            return this;
        }

        public Builder setArgTypes(Map<String, TypeParser> argTypes) {
            this.argTypes = argTypes;
            return this;
        }

        public Builder addArgType(String name, TypeParser parser) {
            argTypes.put(name, parser);
            return this;
        }

        public Builder setOnError(CommandError onError) {
            this.onError = onError;
            return this;
        }

        public Commandeer create() {
            return new Commandeer(commandParser, argumentParser, inputPreParser, inputParser, defaultPrefix, argTypes, onError);
        }
    }


}
