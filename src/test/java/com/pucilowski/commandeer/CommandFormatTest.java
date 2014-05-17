package com.pucilowski.commandeer;

import com.pucilowski.commandeer.exception.CommandFormatException;
import com.pucilowski.commandeer.processing.CommandParser;
import com.pucilowski.commandeer.processing.impl.DefaultCommandParser;
import com.pucilowski.commandeer.wrappers.MockArgDef;
import org.junit.Test;

import static org.junit.Assert.fail;

public class CommandFormatTest {

    public static final String CMD_NAME = "command";

    static final Commandeer cmd = new Commandeer.Builder().create();

    static final CommandParser pp = new DefaultCommandParser();

    public static final MockArgDef ARG = new MockArgDef("<arg1>", "arg1", null, true);
    public static final MockArgDef ARG_TYPED = new MockArgDef("<arg1:int>", "arg1", "int", true);
    public static final MockArgDef ARG_OPTIONAL = new MockArgDef("[arg1]", "arg1", null, false);
    public static final MockArgDef ARG_OPTIONAL_TYPED = new MockArgDef("[arg1:int]", "arg1", "int", false);

    public static final MockArgDef ARG2 = new MockArgDef("<arg2>", "arg2", null, true);
    public static final MockArgDef ARG2_TYPED = new MockArgDef("<arg2:int>", "arg2", "int", true);
    public static final MockArgDef ARG2_OPTIONAL = new MockArgDef("[arg2]", "arg2", null, false);
    public static final MockArgDef ARG2_OPTIONAL_TYPED = new MockArgDef("[arg2:int]", "arg2", "int", false);


   /* private MockCmdDef[] commandParseTests() {
        return new MockCmdDef[]{
                new MockCmdDef(CMD_NAME),
                new MockCmdDef(CMD_NAME, ARG),
                new MockCmdDef(CMD_NAME, ARG, ARG2),
                new MockCmdDef(CMD_NAME, ARG_TYPED),
                new MockCmdDef(CMD_NAME, ARG_TYPED, ARG2_TYPED),
                new MockCmdDef(CMD_NAME, ARG_OPTIONAL),
                new MockCmdDef(CMD_NAME, ARG_OPTIONAL_TYPED),
                new MockCmdDef(CMD_NAME, ARG, ARG2_OPTIONAL),
                new MockCmdDef(CMD_NAME, ARG_TYPED, ARG2_OPTIONAL_TYPED),
        };
    }*/

    @Test
    public void validCommandAliasesTest() {
        String[] names = {"cmd", "command|cmd", "cmd/", "command/|cmd", "command|cmd/"};
        boolean[] valid = {true, true, false, false, false};

        for (int i = 0; i < names.length; i++) {
            String cmd = names[i];
            boolean v = valid[i];

            testCommandFormat(cmd, v);
        }
    }

    @Test
    public void uniqueArgumentNamesTest() {
        String[] names = {"cmd <arg1> <arg2>", "cmd <arg1> <arg1>"};
        boolean[] valid = {true, false};

        for (int i = 0; i < names.length; i++) {
            String cmd = names[i];
            boolean v = valid[i];

            testCommandFormat(cmd, v);
        }
    }

    @Test
    public void optionalArgumentsLastTest() {
        String[] names = {
                "cmd <arg1> <arg2>",
                "cmd <arg1> [arg2]",
                "cmd [arg1] <arg2>"};
        boolean[] valid = {true, true, false};

        for (int i = 0; i < names.length; i++) {
            String cmd = names[i];
            boolean v = valid[i];

            testCommandFormat(cmd, v);
        }
    }

    public void testCommandFormat(String format, boolean valid) {
        Commandeer cmd = new Commandeer.Builder().create();

        try {
            cmd.defineCommand(format);
            if (!valid) fail("command format '" + format + " should be invalid");
        } catch (CommandFormatException e) {
            //System.out.println("Exc: " + e.getMessage());
            if (valid) fail("command format '" + format + " should be valid");
        }
    }

/*    @Test
    public void formatParsingTest() {
        MockCmdDef[] tests = commandParseTests();

        for (MockCmdDef test : tests) {
            assertEquals(test.expectedFormat, test.command.getFormat());
            assertArrayEquals(test.expectedArgs, test.command.getParameters());

*//*            for (int i = 0; i < test.expectedArgs.length; i++) {
                ArgumentDef ex = test.expectedArgs[i];
                ArgumentDef arg = test.doCommand.getParameters()[i];

                System.out.println("ex: " + ex.toString() + ", arg: " + arg.toString());
            }*//*
        }
    }*/


}