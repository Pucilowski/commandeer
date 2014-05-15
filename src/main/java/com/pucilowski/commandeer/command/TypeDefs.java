package com.pucilowski.commandeer.command;

import java.util.HashMap;

/**
 * Created by martin on 15/05/14.
 */
public class TypeDefs {

    public static final TypeParser<String> STRING =  input -> input;
    public static final TypeParser<Integer> INTEGER =  Integer::parseInt;
    public static final TypeParser<Double> DOUBLE =  Double::parseDouble;

    public static final HashMap<String, TypeParser> DEFAULT_TYPES = new HashMap<>();

    static {
        DEFAULT_TYPES.put("text", STRING);
        DEFAULT_TYPES.put("int", INTEGER);
        DEFAULT_TYPES.put("real", DOUBLE);

        /*DEFAULT_TYPES.put("duration", input -> {
            input  = input.replaceAll("\\s", "");

            PeriodParser parser = new PeriodFormatterBuilder()
                    .appendDays().appendSuffix("d")
                    .appendHours().appendSuffix("h")
                    .appendMinutes().appendSuffix("m")
                    .appendSeconds().appendSuffix("s")
                    .toParser();

            //Period d = parser.parsePeriod(s);

            MutablePeriod period = new MutablePeriod();
            parser.parseInto(period, input, 0, Locale.getDefault());

            return period.toDurationFrom(new DateTime(0)).getMillis();
        });*/
    }

    /**
     * Created by martin on 15/05/14.
     */
    public static interface TypeParser<E> {
        public E parse(String input);
    }
}
