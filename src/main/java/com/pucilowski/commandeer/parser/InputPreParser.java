package com.pucilowski.commandeer.parser;

/**
 * Created by martin on 16/05/14.
 */
public interface InputPreParser {

    PreParsed preParse(String input, String prefix);

    public static class PreParsed {
        String alias;
        String argString;

        public PreParsed(String alias, String argString) {
            this.alias = alias;
            this.argString = argString;
        }

        public String getAlias() {
            return alias;
        }

        public String getArgString() {
            return argString;
        }
    }
}
