package com.pucilowski.commandeer;

import com.pucilowski.commandeer.command.CommandDef;
import org.junit.Test;
import static org.junit.Assert.*;

public class CommandParserTest {

    public static final String FORMAT = "command|cmd <arg1:text> <arg2:int> [arg3:real]";

    @Test
    public void commandMatchPrefixedTest() {

    }

    @Test
    public void requiredArgumentsTest() {
        Commandeer cmd = new Commandeer();
        CommandDef def = cmd.addCommand(FORMAT);

        final String[] inputs = {
                "!cmd string 123",
                "!cmd string",
                "!cmd 123",
                "!cmd string 123 4.5",
        };
        final boolean[] valid = {true,false,false,true};

        for (int i = 0; i < inputs.length; i++) {
            String input = inputs[i];
            boolean v = valid[i];

            CommandParser parser = cmd.parse(input, "!");
            parser.parseCommand();

            System.out.println("input: " + input);
            System.out.println("error: " + parser.getError());

            if ((parser.getError() != null) == v) {
                System.out.println(parser.getCommandDef().getFormat() +" : " + parser.getError());
                fail();
            }
        }

    }

    @Test
    public void exampleCommands() {
        final String[] inputs = {
                "!command some string",
                "!command \"some string\"",
                "!cmd string word",
                "!cmd string 10",
                "!cmd string 5.4 10",
                "!cmd string 10 4.4"
        };

        Commandeer cmd = new Commandeer();
        CommandDef def = cmd.addCommand("command|cmd <arg1:text> [arg2:int] [arg3:real]");

        System.out.println("format: " + def.getFormat() + "\n");

        for (String input : inputs) {
            CommandParser parser = cmd.parse(input, "!");
            parser.parseCommand();

            System.out.println("input: " + input);

            if (parser.getError() == null) {
                Command c = parser.getCommand();

                System.out.println("\tresult: " + c.toString());
            } else {
                System.out.println("\terror: " + parser.getError());
            }
        }
    }



    @Test
    public void testCommandParsing() {
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

        Commandeer cmd = new Commandeer();
        CommandDef def = cmd.addCommand("command|cmd <arg1:text> [arg2:int] [arg3:duration]");

        System.out.println("format: " + def.getFormat() + "\n");
        for (String input : inputs) {
            CommandParser parser = cmd.parse(input, "!");
            parser.parseCommand();

            System.out.println("input: " + input);

            if (parser.getError() == null) {
                Command c = parser.getCommand();
                System.out.println("parsed:\n" + c.toString());
            } else {
                System.out.println("error: " + parser.getError());
            }

            System.out.println();
        }
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