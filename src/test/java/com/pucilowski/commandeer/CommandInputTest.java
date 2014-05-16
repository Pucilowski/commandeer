package com.pucilowski.commandeer;

import com.pucilowski.commandeer.exception.CommandInputException;
import org.junit.Test;

import static org.junit.Assert.*;

public class CommandInputTest {

    public static final String FORMAT = "command|cmd <arg1> <arg2:int> [arg3:real]";

    @Test
    public void badInputTest() {
        Commandeer cmd = new Commandeer.Builder().create();
        cmd.registerCommand(FORMAT);

        final String[] inputs = {
                "",
                "cmd",
                "cm12d",
                "cmd red",
                "cmd 123",
                "cmd red 123 4.5",
        };

        String prefix = "!";

        for (int i = 0; i < inputs.length; i++) {
            String input = inputs[i];

            System.out.println("input: " + input);

            CommandInput cmdIn = testCommandParsing(cmd, input, prefix, false);
        }
    }

    @Test
    public void requiredArgumentsTest() {
        Commandeer cmd = new Commandeer.Builder().create();
        cmd.registerCommand(FORMAT);

        final String[] inputs = {
                "!cmd red 123",
                "!cmd red",
                "!cmd 123",
                "!cmd red 123 4.5",
        };
        final boolean[] valid = {true, false, false, true};

        for (int i = 0; i < inputs.length; i++) {
            String input = inputs[i];
            boolean v = valid[i];

            System.out.println("input: " + input);

            try {
                CommandInput cmdIn = cmd.parse(input, "!");

                System.out.println("result: " + cmdIn.getCommand().getFormat());

                if (!v) fail();
            } catch (CommandInputException e) {
                System.out.println("bad input: " + e.getMessage());

                if (v) fail();
            }
        }
    }

    @Test
    public void testCommandParsing() {
        final String format = "command|cmd <arg1:text> [arg2:int] [arg3:real]";

        //TODO some actual testing
        String[] inputs = {
                "!command some string",
                "!cmd \"some string\"",
                "!cmd string asdf",
                "!cmd \"some string\" 11",
                "!cmd string 11 2d",
                "!cmd string 11 \"1d 6h 15m 30s\"",
                "!cmd \"some string\" 11 \"12h 42s\"",
                "!cmd string 11 \"-3d\"",
        };

        Commandeer cmd = new Commandeer.Builder().create();
        cmd.registerCommand(format);

        System.out.println("format: " + format + "\n");
        for (String input : inputs) {

            //ParsedCommand in = testCommandParsing(cmd, input, "!", true);
/*
            if (parser.getError() == null) {
                System.out.println("parsed:\n" + parser.toString());
            } else {
                System.out.println("error: " + parser.getError());
            }
*/

            System.out.println();
        }
    }

    public CommandInput testCommandParsing(Commandeer cmd, String input, String prefix, boolean valid) {
        try {
            CommandInput in = cmd.parse(input, prefix);
            if (!valid) fail("command format '" + input + " should be invalid");
            return in;
        } catch (CommandInputException e) {
            //System.out.println("Exc: " + e.getMessage());
            if (valid) fail("\t'" + input + " should be valid");
        }

        return null;
    }

    public class MockArgValue {
        public String input;
        public Object value;

        public MockArgValue(String input, Object value) {
            this.input = input;
            this.value = value;
        }
    }

}