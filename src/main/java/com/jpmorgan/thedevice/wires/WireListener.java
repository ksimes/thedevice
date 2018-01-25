package com.jpmorgan.thedevice.wires;

/**
 * Created by S.King on 15/02/2017.
 */
public interface WireListener {

    // When a wire is cut then the signal goes low.
    void signalLow(WireName name);
}
