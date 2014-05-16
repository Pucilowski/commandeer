package com.pucilowski.commandeer.parser;

import com.pucilowski.commandeer.command.Argument;
import com.pucilowski.commandeer.wrappers.MockArgDef;
import com.pucilowski.commandeer.parser.impl.DefaultArgumentParser;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertArrayEquals;


/**
 * Created by martin on 16/05/14.
 */
public class DefaultArgumentParserTest {

    static final ArgumentParser argumentParser = new DefaultArgumentParser();

    public static final MockArgDef ARG = new MockArgDef(argumentParser, "<arg1>", "arg1", null, true);
    public static final MockArgDef ARG_TYPED = new MockArgDef(argumentParser, "<arg1:int>", "arg1", "int", true);
    public static final MockArgDef ARG_OPTIONAL = new MockArgDef(argumentParser, "[arg1]", "arg1", null, false);
    public static final MockArgDef ARG_OPTIONAL_TYPED = new MockArgDef(argumentParser, "[arg1:int]", "arg1", "int", false);

    public static final MockArgDef ARG2 = new MockArgDef(argumentParser, "<arg2>", "arg2", null, true);
    public static final MockArgDef ARG2_TYPED = new MockArgDef(argumentParser, "<arg2:int>", "arg2", "int", true);
    public static final MockArgDef ARG2_OPTIONAL = new MockArgDef(argumentParser, "[arg2]", "arg2", null, false);
    public static final MockArgDef ARG2_OPTIONAL_TYPED = new MockArgDef(argumentParser, "[arg2:int]", "arg2", "int", false);

    MockArgDef[] mockArgDefs() {
        return new MockArgDef[]{
                ARG, ARG_TYPED,
                ARG_OPTIONAL, ARG_OPTIONAL_TYPED,
        };
    }

    @Test
    public void testArgumentParsing() {
        DefaultArgumentParser parser = new DefaultArgumentParser();

        for (MockArgDef mockArg : mockArgDefs()) {
            Argument arg = parser.parse(mockArg.expectedFormat);

            assertEquals(mockArg.expectedArg, arg);
        }
    }

    @Test
    public void testArgumentSerialization() {
        DefaultArgumentParser parser = new DefaultArgumentParser();

        for (MockArgDef mockArg : mockArgDefs()) {
            String format = parser.serialize(mockArg.expectedArg);

            assertEquals(mockArg.expectedFormat, format);
        }
    }


}
