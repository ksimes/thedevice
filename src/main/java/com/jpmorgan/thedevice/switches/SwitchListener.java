package com.jpmorgan.thedevice.switches;

/**
 * Created by S.King on 15/02/2017.
 */
public interface SwitchListener {

    // When a Switch is thrown then the signal goes high.
    void signalHigh(SwitchName name);
}
