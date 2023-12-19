package io.domotik8s.model.bool;

import lombok.Data;

@Data
public class BooleanSemantic {

    public static enum Meaning {
        YES_NO, START_STOP, UP_DOWN;
    }

    private Meaning meaning;

    private boolean reversed = false;

}
