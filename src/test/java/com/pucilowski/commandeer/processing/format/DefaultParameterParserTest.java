package com.pucilowski.commandeer.processing.format;

import com.pucilowski.commandeer.Commandeer;
import com.pucilowski.commandeer.structure.Parameter;
import com.pucilowski.commandeer.processing.CommandParser;
import com.pucilowski.commandeer.processing.impl.DefaultCommandParser;
import com.pucilowski.commandeer.wrappers.MockArgDef;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


/**
 * Created by martin on 16/05/14.
 */
public class DefaultParameterParserTest {

    static final Commandeer cmd = new Commandeer.Builder().create();

    static final CommandParser pp = new DefaultCommandParser();

    public static final MockArgDef ARG = new MockArgDef( "<arg1>", "arg1", null, false);
    public static final MockArgDef ARG_TYPED = new MockArgDef( "<arg1:int>", "arg1", "int", false);
    public static final MockArgDef ARG_OPTIONAL = new MockArgDef("[arg1]", "arg1", null, true);
    public static final MockArgDef ARG_OPTIONAL_TYPED = new MockArgDef("[arg1:int]", "arg1", "int", true);

    public static final MockArgDef ARG2 = new MockArgDef("<arg2>", "arg2", null, true);
    public static final MockArgDef ARG2_TYPED = new MockArgDef("<arg2:int>", "arg2", "int", false);
    public static final MockArgDef ARG2_OPTIONAL = new MockArgDef("[arg2]", "arg2", null, true);
    public static final MockArgDef ARG2_OPTIONAL_TYPED = new MockArgDef("[arg2:int]", "arg2", "int", true);

    MockArgDef[] mockArgDefs() {
        return new MockArgDef[]{
                ARG, ARG_TYPED,
                ARG_OPTIONAL, ARG_OPTIONAL_TYPED,
        };
    }

    @Test
    public void testArgumentParsing() {
        CommandParser parser = new DefaultCommandParser();

        for (MockArgDef mockArg : mockArgDefs()) {
            Parameter arg = parser.parseParameter(mockArg.expectedFormat);

            assertEquals(mockArg.expectedArg, arg);
        }
    }

    @Test
    public void testArgumentSerialization() {
        for (MockArgDef mockArg : mockArgDefs()) {
            String format = pp.formatParameter(mockArg.expectedArg);

            assertEquals(mockArg.expectedFormat, format);
        }
    }


}
