package com.pucilowski.commandeer;

import com.pucilowski.commandeer.exception.CommandInputException;
import com.pucilowski.commandeer.exception.InvalidCommandException;
import org.junit.Test;

import static org.junit.Assert.*;


//TODO actual tests
public class CommandInputTest {

    public static final String FORMAT = "command|cmd <arg1> <arg2:int> [arg3:double]";

    @Test
    public void badInputTest() {
        Commandeer cmd = new Commandeer.Builder().create();
        cmd.defineCommand(FORMAT);

        final String[] inputs = {
                "",
                "cmd",
                "cm12d",
                "cmd|",
                "!cmd red",
                "!cmd red 4.5",
        };

        String prefix = "!";

        for (String input : inputs) {
            testCommandParsing(cmd, input, prefix, false);
        }
    }

    @Test
    public void requiredArgumentsTest() {
        Commandeer cmd = new Commandeer.Builder().create();
        cmd.defineCommand(FORMAT);

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
                CommandInput cmdIn = cmd.parseInput(input, "!");
                System.out.println("result: " + cmdIn.getCommand());

                if (!v) fail();
            } catch (CommandInputException | InvalidCommandException e) {
                if (v) fail();
            }
        }
    }

    public CommandInput testCommandParsing(Commandeer cmd, String input, String prefix, boolean valid) {
        try {
            CommandInput in = cmd.parseInput(input, prefix);
            if (!valid) fail("command format '" + input + " should be invalid");
            return in;
        } catch (CommandInputException | InvalidCommandException e) {
            if (valid) fail("\t'" + input + " should be valid");
        }

        return null;
    }
}