package com.pucilowski.commandeer;

import com.pucilowski.commandeer.annotations.Cmd;
import com.pucilowski.commandeer.annotations.Param;
import com.pucilowski.commandeer.callbacks.CommandError;
import com.pucilowski.commandeer.callbacks.CommandExecutor;
import com.pucilowski.commandeer.exception.InvalidCommandException;
import com.pucilowski.commandeer.processing.impl.DefaultInputParser;
import com.pucilowski.commandeer.structure.*;
import com.pucilowski.commandeer.exception.ClashingCommandAliasException;
import com.pucilowski.commandeer.exception.CommandFormatException;
import com.pucilowski.commandeer.exception.CommandInputException;
import com.pucilowski.commandeer.processing.CommandParser;
import com.pucilowski.commandeer.processing.InputParser;
import com.pucilowski.commandeer.processing.impl.DefaultCommandParser;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by martin on 15/05/14.
 */
public class Commandeer {

    private final CommandParser cmdParser;
    private final InputParser inputParser;

    private final String defaultPrefix;
    private final Map<String, TypeParser> argTypes;
    private final CommandError onError;

    List<Command> cmds = new ArrayList<>();
    Map<String, Command> cmdsAliased = new TreeMap<>();

    private Commandeer(
            CommandParser cmdParser, InputParser inputParser,
            String defaultPrefix, Map<String, TypeParser> argTypes, CommandError onError) {
        this.argTypes = argTypes;
        this.defaultPrefix = defaultPrefix;
        this.cmdParser = cmdParser;
        this.inputParser = inputParser;
        this.onError = onError;
    }

    //extract annotated commands
    public void extractCommands(Object object) {
        Class cls = object.getClass();

        for (Method method : cls.getMethods()) {
            if (!method.isAnnotationPresent(Cmd.class)) continue;

            extractCommand(object, method);
        }
    }

    public Command extractCommand(Object object, Method method) {
        Cmd l = method.getAnnotation(Cmd.class);

        String[] aliases = l.value();

        java.lang.reflect.Parameter[] parameters = method.getParameters();
        Parameter[] args = new Parameter[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            java.lang.reflect.Parameter p = parameters[i];

            Class paramType = p.getType();

            String name = p.getName();
            String type = getArgumentType(this, paramType);
            Object def = null;

            if (p.isAnnotationPresent(Param.class)) {
                Param param = p.getAnnotation(Param.class);
                name = param.value();

                String defaultArg = param.def();
                if (defaultArg.length() > 0) {
                    TypeParser tp = getTypeParser(this, paramType);
                    if (tp == null) {
                        throw new CommandFormatException("No type parser available for '" + paramType + "'");
                    }

                    try {
                        def = tp.parse(defaultArg);
                    } catch (Exception e) {
                        throw new CommandFormatException("Cannot parse default argument '" + name + "' of type '" + paramType + "' (" + e.getMessage() + ")");
                    }
                }
            }


            args[i] = new Parameter(name, type, def != null, def);
        }

        return addCommand(aliases, args, new JavaExecutor(object, method));
    }

    public static TypeParser getTypeParser(Commandeer cmd, Class p) {
        for (Map.Entry<String, TypeParser> entry : cmd.getArgTypes().entrySet()) {
            TypeParser tp = entry.getValue();

            try {
                Method m = tp.getClass().getMethod("getType");
                Class c = (Class) m.invoke(tp);

                if (c.equals(p)) {
                    return entry.getValue();
                }
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static String getArgumentType(Commandeer cmd, Class p) {
        for (Map.Entry<String, TypeParser> entry : cmd.getArgTypes().entrySet()) {
            TypeParser tp = entry.getValue();

            try {
                Method m = tp.getClass().getMethod("getType");
                Class c = (Class) m.invoke(tp);

                if (c.equals(p)) {
                    return entry.getKey();
                }
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    //text defined commands
    public Command parseCommand(String format, CommandExecutor executor) {
        List<String> parts = new ArrayList<>(Arrays.asList(format.split(" ")));
        if (parts.size() == 0) {
            throw new CommandFormatException("Command format not specified");
        }

        // aliases
        String aliasesPart = parts.remove(0);
        String[] aliases = cmdParser.parseAliases(aliasesPart);
        if (aliases == null) throw new CommandFormatException("Failed to parse command aliases");
        for (String alias : aliases) {
            if (cmdsAliased.containsKey(alias)) {
                Command cmd = cmdsAliased.get(alias);
                throw new ClashingCommandAliasException("Command alias '" + alias + "' is in use by " + cmd.toString());
            }
        }

        TreeSet<String> argNames = new TreeSet<>();
        Parameter[] parameters = new Parameter[parts.size()];

        boolean pastRequired = false;
        for (int i = 0; i < parts.size(); i++) {
            String arg = parts.get(i);
            Parameter argDef = cmdParser.parseParameter(arg);

            if (!argDef.isOptional()) {
                if (pastRequired)
                    throw new CommandFormatException("Optional arguments must follow required ones.");
            } else {
                pastRequired = true;
            }

            if (argNames.contains(argDef.getName())) {
                throw new CommandFormatException("Repeated argument name '" + argDef.getName() + "'. Argument names must be unique.");
            }

            parameters[i] = argDef;
            argNames.add(argDef.getName());
        }

        return addCommand(aliases, parameters, executor);
    }

    public Command addCommand(String[] aliases, Parameter[] parameters, CommandExecutor exec) {
        TreeSet<String> argNames = new TreeSet<>();

        // aliases
        if (aliases == null) throw new CommandFormatException("Failed to parse command aliases");
        for (String alias : aliases) {
            if (cmdsAliased.containsKey(alias)) {
                Command cmd = cmdsAliased.get(alias);
                throw new ClashingCommandAliasException("Command alias '" + alias + "' is in use by " + cmd.toString());
            }
        }


        boolean pastRequired = false;
        for (Parameter argDef : parameters) {
            if (!argDef.isOptional()) {
                if (pastRequired)
                    throw new CommandFormatException("Optional arguments must follow required ones.");
            } else {
                pastRequired = true;
            }

            if (argNames.contains(argDef.getName())) {
                throw new CommandFormatException("Repeated argument name '" + argDef.getName() + "'. Argument names must be unique.");
            }

            argNames.add(argDef.getName());
        }

        Command def = new Command(aliases, parameters, exec);

        cmds.add(def);
        for (String alias : def.getAliases()) {
            cmdsAliased.put(alias, def);
        }

        return def;
    }

    public Command parseCommand(String format) {
        return parseCommand(format, null);
    }

    public CommandInput parseInput(String input, String prefix) throws CommandInputException, InvalidCommandException {
        InputParser.PreParsed preParsed = inputParser.preParse(input, prefix);
        if (preParsed == null) {
            throw new InvalidCommandException("Invalid command input: '" + input + "'");
        }

        String alias = preParsed.getAlias();

        Command def = cmdsAliased.get(alias);
        if (def == null) throw new InvalidCommandException("Invalid command input: '" + input + "'");

        Map<String, Object> argMap = inputParser.parseArguments(this, def, preParsed.getArgString());

        return new CommandInput(def, preParsed.getAlias(), argMap);
    }

    public CommandInput parse(String input) throws CommandInputException, InvalidCommandException {
        return parseInput(input, defaultPrefix);
    }

    public void execute(String input, String prefix) {
        CommandInput in;
        try {
            in = parseInput(input, prefix);
            Command def = in.getCommand();
            CommandExecutor cb = def.getExecutor();
            if (cb != null) cb.execute(in);
        } catch (InvalidCommandException e) {
            onError.onError(null, input, e.getMessage());
        } catch (CommandInputException e) {
            InputParser.PreParsed preParsed = inputParser.preParse(input, prefix);
            String alias = preParsed.getAlias();

            Command def = cmdsAliased.get(alias);
            onError.onError(def, input, e.getMessage());
        }
    }

    public void execute(String input) {
        execute(input, defaultPrefix);
    }

    public CommandParser getCmdParser() {
        return cmdParser;
    }

    public InputParser getInputParser() {
        return inputParser;
    }

    public Map<String, TypeParser> getArgTypes() {
        return argTypes;
    }


    /**
     * Created by martin on 15/05/14.
     */
    public static class Builder {
        private CommandParser commandParser = new DefaultCommandParser();

        private InputParser inputParser = new DefaultInputParser();

        private String defaultPrefix = null;
        private Map<String, TypeParser> argTypes = new HashMap<>();
        private CommandError onError = (def, input, error) -> System.out.println("bad input: '" + error + "' (" + input + ")");

        public Builder() {
            argTypes.put("text", DefaultTypes.STRING);
            argTypes.put("int", DefaultTypes.INTEGER);
            argTypes.put("real", DefaultTypes.DOUBLE);
        }

        public Builder setCommandParser(CommandParser commandParser) {
            this.commandParser = commandParser;
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
            return new Commandeer(commandParser, inputParser, defaultPrefix, argTypes, onError);
        }
    }


}
