package com.stronans.thedevice.switches;

/**
 * Created by S.King on 15/02/2017.
 */
public interface SwitchListener {

    // When a Switch is thrown then the signal goes high.
    void switchThrown(SwitchName name);
}
