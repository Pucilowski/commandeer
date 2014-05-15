package com.pucilowski.commandeer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Created by martin on 15/05/14.
 */
public class Example {
    //define the format of the command
    private static final String COMMAND =
            "command|cmd <arg1:text> <arg2:int> [arg3:real] [arg4:time]";

    Commandeer cmd;

    public Example() {
        //use builder or simply new Commandeer() to use defaults
        cmd = new CommandeerBuilder()
                .setPrefix("!") //default input prefix
                .addType("time", input -> { // adding new type 'time'
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                    try {
                        return sdf.parse(input);
                    } catch (ParseException e) {
                        throw new RuntimeException(e.toString());
                    }
                })
                .create();

        //register commands
        cmd.addCommand(COMMAND);
    }

    public static void main(String[] args) {
        new Example().demo();
    }

    public void demo() {
        final String[] inputs = {
                "command",
                "!command red green",
                "!command \"red green\"",
                "!command \"red green\" blue",
                "!cmd red green",
                "!cmd red 3.141",
                "!cmd red 42",
                "!cmd red 42 3.141",
                "!cmd red 42 3.141 water",
                "!cmd red 42 3.141 22:52:11"
        };

        System.out.println("format: " + COMMAND);
        for (String input : inputs) {
            System.out.println("input: " + input);
            process(input);
        }
    }

    public void process(String input) {
        CommandParser parser = cmd.parse(input);

        if(parser == null) {
            System.out.println("\tnot a command");
            return;
        }

        String error;
        if ((error = parser.getError()) != null) {
            //tells us why the input was not valid
            System.out.println("\terror: " + error);
        } else {
            //get instance of the parsed user input
            Command command = parser.getCommand();

            //check out what's what
            System.out.println("\tresult: " + command.toString());

            // the specific alias that was used
            String alias = command.getAlias();
            //a map of the argument names and typed values
            Map<String, Object> args = command.getArgs();

            //argument values by name
            String arg1 = command.getArgAsString("arg1");

            if (command.hasArg("arg2")) {
                int arg2 = command.getArgAsInteger("arg2");
            }

            if (command.hasArg("arg3")) {
                double arg3 = command.getArgAsDouble("arg3");
            }

            if (command.hasArg("arg4")) {
                Date arg4 = (Date) command.getArg("arg4");
            }
        }
    }
}
