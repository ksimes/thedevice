package com.stronans.thedevice.wires;

import com.pi4j.io.gpio.GpioController;

import java.util.ArrayList;
import java.util.List;

import static com.stronans.thedevice.wires.WireName.*;

/**
 * Handles all of the wires on the Device
 * <p>
 * Created by S.King on 13/02/2017.
 */
public class Wires {
    private List<Wire> WireList = new ArrayList<>();
    private final GpioController gpio;
    private WireName expected;

    private Wire create(WireName name, WireListener listener) {
        Wire wire = new Wire(name, gpio);
        wire.addListener(listener);

        return wire;
    }

    public synchronized void setExpected(WireName name) {
        expected = name;
    }

    public Wires(final GpioController gpio, WireListener listener) {
        this.gpio = gpio;

        WireList.add(create(RED, listener));
        WireList.add(create(WHITE, listener));
        WireList.add(create(GREEN, listener));
        WireList.add(create(GREY, listener));
        WireList.add(create(BLUE, listener));
        WireList.add(create(ORANGE, listener));
    }
}
