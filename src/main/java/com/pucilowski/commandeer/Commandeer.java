package com.pucilowski.commandeer;

import com.pucilowski.commandeer.annotations.Cmd;
import com.pucilowski.commandeer.annotations.Param;
import com.pucilowski.commandeer.callbacks.ErrorListener;
import com.pucilowski.commandeer.callbacks.InputListener;
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
    private final Map<String, TypeParser> types;
    private final ErrorListener errorListener;

    private final List<Command> cmds = new ArrayList<>();
    private final Map<String, Command> cmdsAliased = new TreeMap<>();

    private Commandeer(CommandParser cmdParser, InputParser inputParser,
                       String defaultPrefix, Map<String, TypeParser> types,
                       ErrorListener errorListener) {
        this.types = types;
        this.defaultPrefix = defaultPrefix;
        this.cmdParser = cmdParser;
        this.inputParser = inputParser;
        this.errorListener = errorListener;
    }

    /**
     * Registers methods annotated with @see Cmd as commands.
     *
     * @param object The Java object to look for commands in.
     */
    public void extractCommands(Object object) {
        Class cls = object.getClass();

        for (Method method : cls.getMethods()) {
            if (!method.isAnnotationPresent(Cmd.class)) continue;

            extractCommand(object, method);
        }
    }

    private Command extractCommand(Object object, Method method) {
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
                name = param.name();

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

    private static TypeParser getTypeParser(Commandeer cmd, Class p) {
        for (Map.Entry<String, TypeParser> entry : cmd.getTypes().entrySet()) {
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

    private static String getArgumentType(Commandeer cmd, Class p) {
        for (Map.Entry<String, TypeParser> entry : cmd.getTypes().entrySet()) {
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

    /**
     * Registers command by parsing a signature string.
     *
     * @param format   Command signature string
     * @param executor Callback method
     * @return The parsed command
     */
    public Command defineCommand(String format, InputListener executor) {
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

    /**
     * Registers command with no callback
     *
     * @param format Command signature string
     * @return The parsed command
     */
    public Command defineCommand(String format) {
        return defineCommand(format, null);
    }

    /**
     * Validates the command components and assembles the final object.
     * Also checks if command is valid in the context of Commandeer instance, i.e. no alias clashes.
     * Adds command starts listening for it if successful.
     *
     * @param aliases    Names by which command can be triggered
     * @param parameters Command parameters
     * @param exec       Callback method
     * @return The fully assembled command
     */
    public Command addCommand(String[] aliases, Parameter[] parameters, InputListener exec) {
        TreeSet<String> argNames = new TreeSet<>();

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

    /**
     * Returns all registered commands
     *
     * @return Commands
     */
    public Command[] getCommands() {
        return cmds.toArray(new Command[cmds.size()]);
    }

    /**
     * Returns command corresponding to given alias
     *
     * @param alias Command alias
     * @return The command
     */
    public Command getCommand(String alias) {
        return cmdsAliased.get(alias);
    }

    /**
     * Takes string command input and determines which command it should call.
     * Parses arguments into their appropriate types and makes sure all required
     * ones are present. If successful returns a CommandInput instance which can be
     * queried for parameter values.
     *
     * @param input  Input to run as a command
     * @param prefix If the command is prefixed with anything.
     * @return The parsed command input
     * @throws CommandInputException
     * @throws InvalidCommandException
     */
    public CommandInput parseInput(String input, String prefix) throws CommandInputException, InvalidCommandException {
        InputParser.PreParsed preParsed = inputParser.preParse(input, prefix);
        if (preParsed == null)
            throw new InvalidCommandException("Invalid command input: '" + input + "'");

        String alias = preParsed.getAlias();
        Command def = cmdsAliased.get(alias);
        if (def == null) throw new InvalidCommandException("Invalid command input: '" + input + "'");

        Map<String, Object> argMap = inputParser.parseArguments(this, def, preParsed.getArgString());
        return new CommandInput(def, preParsed.getAlias(), argMap);
    }

    /**
     * Calls @see parseInput with defaultPrefix.
     *
     * @param input Input to run as a command
     * @return The parsed command input
     * @throws CommandInputException
     * @throws InvalidCommandException
     */
    public CommandInput parseInput(String input) throws CommandInputException, InvalidCommandException {
        return parseInput(input, defaultPrefix);
    }

    /**
     * Takes input and uses @see parseInput to process.
     * Upon bad command input will call the error callback
     * (set using @see Commandeer.Builder#setErrorListener)
     * If input is valid will execute the relevant callback
     * or Java method.
     *
     * @param input  Input to run as a command
     * @param prefix If the command is prefixed with anything.
     */
    public void execute(String input, String prefix) {
        CommandInput in;
        try {
            in = parseInput(input, prefix);
            Command def = in.getCommand();
            InputListener cb = def.getExecutor();
            if (cb != null) cb.execute(in);
        } catch (InvalidCommandException e) {
            errorListener.onError(null, input, e.getMessage());
        } catch (CommandInputException e) {
            InputParser.PreParsed preParsed = inputParser.preParse(input, prefix);
            String alias = preParsed.getAlias();

            Command def = cmdsAliased.get(alias);
            errorListener.onError(def, input, e.getMessage());
        }
    }

    /**
     * Calls @see execute with defaultPrefix.
     *
     * @param input Input to run as a command
     */
    public void execute(String input) {
        execute(input, defaultPrefix);
    }

    /**
     * Returns commandParser instance. Used to parse text command
     * signatures into Command objects and vice versa.
     *
     * @return parser
     * @see com.pucilowski.commandeer.processing.CommandParser
     * @see com.pucilowski.commandeer.structure.Command
     */
    public CommandParser getCommandParser() {
        return cmdParser;
    }

    /**
     * Returns inputParser instance. Used to parse text command
     * input into CommandInput object and vice versa.
     *
     * @return parser
     * @see com.pucilowski.commandeer.processing.InputParser
     * @see com.pucilowski.commandeer.structure.CommandInput
     */
    public InputParser getInputParser() {
        return inputParser;
    }

    public Map<String, TypeParser> getTypes() {
        return types;
    }


    /**
     * Created by martin on 15/05/14.
     */
    public static class Builder {
        private CommandParser commandParser = new DefaultCommandParser();
        private InputParser inputParser = new DefaultInputParser();

        private String defaultPrefix = null;
        private Map<String, TypeParser> argTypes = new HashMap<>();
        private ErrorListener error = (def, input, error) -> System.out.println("bad input: '" + error + "' (" + input + ")");

        public Builder() {
            argTypes.put("text", DefaultTypes.STRING);
            argTypes.put("int", DefaultTypes.INTEGER);
            argTypes.put("double", DefaultTypes.DOUBLE);
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

        public Builder clearTypes() {
            argTypes.clear();
            return this;
        }

        public Builder setTypes(Map<String, TypeParser> argTypes) {
            this.argTypes = argTypes;
            return this;
        }

        public Builder addType(String name, TypeParser parser) {
            argTypes.put(name, parser);
            return this;
        }

        public Builder setErrorListener(ErrorListener error) {
            this.error = error;
            return this;
        }

        public Commandeer create() {
            return new Commandeer(commandParser, inputParser, defaultPrefix, argTypes, error);
        }
    }


}
