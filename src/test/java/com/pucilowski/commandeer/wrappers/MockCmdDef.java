package com.pucilowski.commandeer.wrappers;

import com.pucilowski.commandeer.Commandeer;
import com.pucilowski.commandeer.command.Argument;
import com.pucilowski.commandeer.command.Command;

/**
* Created by martin on 16/05/14.
*/
public class MockCmdDef {
    Commandeer cmd = new Commandeer.Builder().create();

    public final String expectedFormat;

    public final Command command;
    public final Argument[] expectedArgs;

    public MockCmdDef(String name, MockArgDef... mockArgs) {
        expectedArgs = new Argument[mockArgs.length];

        StringBuilder sb = new StringBuilder();
        sb.append(name).append(" ");

        for (int i = 0; i < mockArgs.length; i++) {
            MockArgDef mockArg = mockArgs[i];
            sb.append(mockArg.expectedArg.toString());

            sb.append(" ");

            expectedArgs[i] = mockArg.expectedArg;
        }

        expectedFormat = sb.toString().trim();

        this.command = cmd.registerCommand(expectedFormat);
    }
}
