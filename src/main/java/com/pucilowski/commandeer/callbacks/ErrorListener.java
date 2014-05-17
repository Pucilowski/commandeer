package com.pucilowski.commandeer.callbacks;

import com.pucilowski.commandeer.structure.Command;

/**
 * Created by martin on 15/05/14.
 */
public interface ErrorListener {
    public void onError(Command def, String input, String error);
}
