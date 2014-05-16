package com.pucilowski.commandeer.parser;

/**
 * Created by martin on 16/05/14.
 */
public interface InputPreParser {

    PreParsed preparse(String input, String prefix);

    public static class PreParsed {
        String alias;
        String argString;

        String[] args;

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
