package com.pucilowski.commandeer;

import com.pucilowski.commandeer.command.CommandDef;

import java.util.Map;

/**
 * Created by martin on 15/05/14.
 */
public class Example {
    //define the format of the command
    public static final String CMD = "command|cmd <arg1:text> [arg2:int] [arg3:real]";

    //create a command definition using a builder
    CommandBuilder builder = new CommandBuilder();
    CommandDef cmdDef = builder
            .defineCommand("command|cmd <arg1:text> [arg2:int] [arg3:real]");

    public void process(String input) {
        CommandParser parser = new CommandParser(cmdDef, input);
        parser.parseCommand();

        String error;
        if ((error = parser.getError()) != null) {
            //tells us why the input was not valid
            System.out.println("Error: " + error);
        } else {
            //get instance of the parsed user input
            Command command = parser.getCommand();

            // the specific alias that was used
            String alias = command.getAlias();
            //a map of the argument values, addressed by argument name
            Map<String, Object> args = command.getArgs();

            //argument values by name
            String arg1 = command.getArg("arg1");
            int arg2 = command.getArgAsInteger("arg2");
            double arg3 = command.getArgAsDouble("arg3");
        }
    }
}
