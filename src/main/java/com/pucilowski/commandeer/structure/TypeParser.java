package com.pucilowski.commandeer.structure;

/**
 * Created by martin on 15/05/14.
 */
public abstract class TypeParser<E> {

    public Class type;

    public TypeParser(Class<E> type) {
        this.type = type;
    }

    public Class getType() {
        return type;
    }

    public abstract E parse(String input);
}
