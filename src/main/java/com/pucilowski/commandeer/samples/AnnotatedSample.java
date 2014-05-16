package com.pucilowski.commandeer.samples;

import com.pucilowski.commandeer.Commandeer;
import com.pucilowski.commandeer.annotations.Cmd;
import com.pucilowski.commandeer.annotations.Param;
import com.pucilowski.commandeer.structure.TypeParser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by martin on 16/05/14.
 */

public class AnnotatedSample {

    Commandeer cmd;

    public AnnotatedSample() {
        cmd = new Commandeer.Builder()
                .setDefaultPrefix("!")
                .setOnError((def, input, error)
                        -> System.out.println("\terror: " + error + ", input: " + input))
                .addArgType("time", new TypeParser<Date>(Date.class) {
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

        cmd.extractCommands(this);

        System.out.println("format: " + CMD);
        for (String input : inputs) {
            System.out.println("input: " + input);
            cmd.execute(input);
        }
    }

    @Cmd({"command", "cmd"})
    public void doCommand(String arg1, Integer arg2,
                          @Param(name = "three", def = "4.11") Double arg3,
                          @Param(name = "four", def = "12:11:30") Date arg4) {
        System.out.println("\tcmd: " + arg1 + ", " + arg2 + ", " + arg3 + ", " + arg4);
    }

    @Cmd({"command2", "cmd2"})
    public void doCommand2(@Param(name="one") String str,
                           @Param(name = "two", def = "123") Integer integer) {
        System.out.println("\tcmd2: " + str + ", " + integer);
    }


    final static String[] inputs = {
            "command", "!command", "!command red green",
            "!command \"red green\"", "!command \"red green\" blue",
            "!cmd red green", "!cmd red 3.141",
            "!cmd red 42", "!cmd red 42 3.141",
            "!cmd red 42 3.141 water", "!cmd red 42 3.141 22:52:11",
            "!cmd2 red", "!cmd2 red green", "!cmd2 red 123",
    };


    public static void main(String[] args) {
        new AnnotatedSample();
    }

    private static final String CMD =
            "cmd <arg1:text> [arg2:int]";
    private static final String CMD2 =
            "command2|cmd2 <arg1:text> <arg2:int> [arg3:real] [arg4:time]";

}
