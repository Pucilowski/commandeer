package com.pucilowski.commandeer.structure;

import com.pucilowski.commandeer.CommandInput;
import com.pucilowski.commandeer.callbacks.InputListener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by martin on 16/05/14.
 */
public class JavaExecutor implements InputListener {

    private final Object object;
    private final Method method;

    public JavaExecutor(Object object, Method method) {
        this.method = method;
        this.object = object;
    }

    @Override
    public void execute(CommandInput cmdIn) {
        Parameter[] params = cmdIn.getCommand().getParameters();

        Object[] args = new Object[params.length];
        for (int i = 0; i < params.length; i++) {
            Parameter param = params[i];

            if (cmdIn.hasArgument(param.getName())) {
                args[i] = cmdIn.getArgument(param.getName());
            } else {
                args[i] = cmdIn.getCommand().getParameters()[i].getDefault();
            }
        }

        try {
            method.invoke(object, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
