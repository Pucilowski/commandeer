package com.pucilowski.commandeer.structure;

import java.util.HashMap;

/**
 * Created by martin on 15/05/14.
 */
public class DefaultTypes {

    public static final TypeParser<String> STRING = new TypeParser<String>(String.class) {
        @Override
        public String parse(String input) {
            return input;
        }
    };

    public static final TypeParser<Integer> INTEGER =  new TypeParser<Integer>(Integer.class) {
        @Override
        public Integer parse(String input) {
            return Integer.parseInt(input);
        }
    };
    public static final TypeParser<Double> DOUBLE =  new TypeParser<Double>(Double.class) {
        @Override
        public Double parse(String input) {
            return Double.parseDouble(input);
        }
    };

    //public static final TypeParser<String> STRING =  input -> input;
    //public static final TypeParser<Integer> INTEGER =  Integer::parseInt;
    //public static final TypeParser<Double> DOUBLE =  Double::parseDouble;

    static {
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

}
