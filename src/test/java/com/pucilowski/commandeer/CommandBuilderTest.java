package com.pucilowski.commandeer;

import com.pucilowski.commandeer.command.ArgumentDef;
import com.pucilowski.commandeer.command.CommandDef;
import com.pucilowski.commandeer.exception.MalformedCommandFormatException;
import org.junit.Assert;
import org.junit.Test;

public class CommandBuilderTest {

    CommandBuilder builder = new CommandBuilder();

    public static final String CMD_NAME = "command";

    // mock arguments
    public static final MockArgDef ARG = new MockArgDef("<arg1>", "arg1", "text", true);
    public static final MockArgDef ARG_TYPED = new MockArgDef("<arg1:int>", "arg1", "int", true);
    public static final MockArgDef ARG_OPTIONAL = new MockArgDef("[arg1]", "arg1", "text", false);
    public static final MockArgDef ARG_OPTIONAL_TYPED = new MockArgDef("[arg1:int]", "arg1", "int", false);

    public static final MockArgDef ARG2 = new MockArgDef("<arg2>", "arg2", "text", true);
    public static final MockArgDef ARG2_TYPED = new MockArgDef("<arg2:int>", "arg2", "int", true);
    public static final MockArgDef ARG2_OPTIONAL = new MockArgDef("[arg2]", "arg2", "text", false);
    public static final MockArgDef ARG2_OPTIONAL_TYPED = new MockArgDef("[arg2:int]", "arg2", "int", false);


    private MockCmdDef[] commandParseTests() {
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
    }

    @Test
    public void validCommandAliasesTest() {
        String[] names = {"cmd", "command|cmd", "cmd/", "command/|cmd", "command|cmd/"};
        boolean[] valid = {true, true, false, false, false };

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
        CommandBuilder builder = new CommandBuilder();

        try {
            builder.defineCommand(format);
            if (!valid) Assert.fail("command format '" + format + " should be invalid");
        } catch (MalformedCommandFormatException e) {
            //System.out.println("Exc: " + e.getMessage());
            if (valid) Assert.fail("command format '" + format + " should be valid");
        }
    }

    @Test
    public void formatParsingTest() {
        MockCmdDef[] tests = commandParseTests();

        for (MockCmdDef test : tests) {
            Assert.assertEquals(test.expectedFormat, test.command.getFormat());
            Assert.assertArrayEquals(test.expectedArgs, test.command.getArguments());

/*            for (int i = 0; i < test.expectedArgs.length; i++) {
                ArgumentDef ex = test.expectedArgs[i];
                ArgumentDef arg = test.command.getArguments()[i];

                System.out.println("ex: " + ex.toString() + ", arg: " + arg.toString());
            }*/
        }
    }



    public class MockCmdDef {
        private final String expectedFormat;

        private final CommandDef command;
        private final ArgumentDef[] expectedArgs;

        public MockCmdDef(String name, MockArgDef... mockArgs) {
            expectedArgs = new ArgumentDef[mockArgs.length];

            StringBuilder sb = new StringBuilder();
            sb.append(name).append(" ");

            for (int i = 0; i < mockArgs.length; i++) {
                MockArgDef mockArg = mockArgs[i];
                sb.append(mockArg.argDef.toString());

                sb.append(" ");

                expectedArgs[i] = mockArg.argDef;
            }

            expectedFormat = sb.toString().trim();

            this.command = builder.defineCommand(expectedFormat);
        }
    }


    /**
     * Created by martin on 15/05/14.
     */
    public static class MockArgDef {
        public final String format;
        public final String name;
        public final String type;
        public final boolean required;

        public final ArgumentDef argDef;

        public MockArgDef(String format, String name, String type, boolean required) {
            this.format = format;
            this.name = name;
            this.type = type;
            this.required = required;

            argDef = new ArgumentDef(name, type, required);
        }
    }
}