package com.pucilowski.commandeer.examples;

import com.pucilowski.commandeer.Commandeer;
import com.pucilowski.commandeer.annotations.Cmd;
import com.pucilowski.commandeer.annotations.Param;
import com.pucilowski.commandeer.structure.Command;
import com.pucilowski.commandeer.structure.TypeParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by martin on 16/05/14.
 */

public class CliExample {

    Commandeer cmd;

    public CliExample() {
        cmd = new Commandeer.Builder()
                .setErrorListener((def, input, error)
                        -> System.out.println("\tinvalid input: " + error))
                .addType("time", new TypeParser<Date>(Date.class) {
                    @Override
                    public Date parse(String input) {// adding new type 'time'
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                        try {
                            return sdf.parse(input);
                        } catch (ParseException e) {
                            throw new RuntimeException(e.toString());
                        }
                    }
                })
                .create();

        //add commands from annotations
        cmd.extractCommands(this);
        //Command def = cmd.getCommand("cmd");

        System.out.println("Running Commandeer command-line input example!");
        System.out.println("Here are the commands:");
        for(Command c : cmd.getCommands()) {
            System.out.println(cmd.getCommandParser().formatCommand(c));
        }

        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                cmd.execute(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Cmd({"command", "cmd"})
    public void doCommand(String arg1, Integer arg2,
                          @Param(name = "three", def = "4.11") Double arg3,
                          @Param(name = "four", def = "12:11:30") Date arg4) {
        System.out.println("\tcmd: " + arg1 + ", " + arg2 + ", " + arg3 + ", " + arg4);
    }

    @Cmd({"command2", "cmd2"})
    public void doCommand2(@Param(name = "one") String str,
                           @Param(name = "two", def = "123") Integer integer) {
        System.out.println("\tcmd2: " + str + ", " + integer);
    }

    public static void main(String[] args) {
        new CliExample();
    }
}
