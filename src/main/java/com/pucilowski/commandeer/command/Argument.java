package com.pucilowski.commandeer.command;

import com.pucilowski.commandeer.parser.ArgumentParser;

/**
 * Created by martin on 15/05/14.
 */
public class Argument {

    private final ArgumentParser parser;

    private final String name;
    private final String type;
    private final boolean required;

    public Argument(ArgumentParser parser, String name, String type, boolean required) {
        this.parser=parser;
        this.name = name;
        this.type = type;
        this.required = required;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Argument argument = (Argument) o;

        if (required != argument.required) return false;
        if (!name.equals(argument.name)) return false;
        if (type != null ? !type.equals(argument.type) : argument.type != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (required ? 1 : 0);
        return result;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public boolean isRequired() {
        return required;
    }

    @Override
    public String toString() {
        return parser.serialize(this);
    }
}
