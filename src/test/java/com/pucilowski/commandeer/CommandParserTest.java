package com.pucilowski.commandeer;

import com.pucilowski.commandeer.command.CommandDef;
import org.junit.Test;

public class CommandParserTest {


    @Test
    public void testCommandParsing() {
        //TODO some actual testing

        CommandDef cmd = CommandBuilder.defineCommand("command|cmd <arg1:text> [arg2:int] [arg3:duration]");

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

        System.out.println("format: " + cmd.getFormat() + "\n");
        for (String input : inputs) {
            CommandParser parser = new CommandParser(cmd, input, "!");
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