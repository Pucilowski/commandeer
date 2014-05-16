package com.pucilowski.commandeer.structure;

/**
 * Created by martin on 15/05/14.
 */
public class Parameter {

    private final String name;
    private final String type;
    private final boolean optional;
    private final Object def;

    public Parameter(String name, String type, boolean optional, Object def) {
        this.name = name;
        this.type = type;
        this.optional = optional;
        this.def = def;
    }

    public Parameter(String name, String type, boolean optional) {
        this(name, type, optional, null);
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public boolean isOptional() {
        return optional;
    }

    public Object getDefault() {
        return def;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Parameter parameter = (Parameter) o;

        if (!name.equals(parameter.name)) return false;
        if (type != null ? !type.equals(parameter.type) : parameter.type != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Parameter{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", required=" + optional +
                ", def=" + def +
                '}';
    }
}
