package com.pucilowski.commandeer;

import com.pucilowski.commandeer.exception.CommandInputException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Created by martin on 15/05/14.
 */
public class Example {
    //define the format of the command
    private static final String CMD =
            "cmd <arg1:text> [arg2:int]";
    private static final String CMD2 =
            "command2|cmd2 <arg1:text> <arg2:int> [arg3:real] [arg4:time]";

    Commandeer cmd;

    public Example() {
        //construct a new commandeer instance
        cmd = new Commandeer.Builder()
                .setDefaultPrefix("!") //default input prefix
                .addArgType("time", input -> { // adding new type 'time'
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                    try {
                        return sdf.parse(input);
                    } catch (ParseException e) {
                        throw new RuntimeException(e.getMessage());
                    }
                })
                .setOnError((input, error) -> //will be called when given bad input
                        System.out.println("error (callback): " + error + ", input: " + input))
                .create();

        //register command with callback
        cmd.registerCommand(CMD, (cmdIn) ->
                System.out.println("cmdIn (callback): " + cmdIn));
        //or one without
        cmd.registerCommand(CMD2);
    }

    public void demo() {
        final String[] input = {
                "!cmd red",
                "!cmd2 red 42 3.141 22:52:11"};
        final String[] badInput = {
                "!cmd red black",
                "!cmd2 red 42 3.141 water"};

        //simply call execute to parse and execute appropriate callback if possible
        //otherwise Commandeer.onError will be called with what went wrong
        cmd.execute(input[0]);
        cmd.execute(badInput[0]);

        //or process command input and any errors yourself (throws CommandInputException)
        processInput(input[1]);
        processInput(badInput[1]);
    }

    public void processInput(String input) {
        try {
            CommandInput cmdIn = cmd.parse(input);

            //check out what's what
            System.out.println("cmdIn: " + cmdIn.toString());

            // the specific alias that was used
            String alias = cmdIn.getAlias();
            //a map of the argument names and typed values
            Map<String, Object> args = cmdIn.getArgs();

            //argument values by name
            String arg1 = cmdIn.getArgAsString("arg1");
            if (cmdIn.hasArg("arg2"))
                cmdIn.getArgAsInteger("arg2");

        } catch (CommandInputException e) {
            System.out.println("error: " + e.getMessage() + ", input: " + input);
        }
    }

    public static void main(String[] args) {
        Example ex = new Example();
        ex.demo();
    }

    public void demos() {
        final String[] inputs = {
                "command",
                "!command",
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

        System.out.println("format: " + CMD);
        for (String input : inputs) {
            System.out.println("input: " + input);
            //parseInput(input);
        }
    }
}
