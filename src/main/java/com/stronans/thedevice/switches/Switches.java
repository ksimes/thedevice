package com.stronans.thedevice.switches;

import com.pi4j.io.gpio.GpioController;

import java.util.ArrayList;
import java.util.List;

import static com.stronans.thedevice.switches.SwitchName.*;

/**
 * Handles all of the wires on the Device
 * <p>
 * Created by S.King on 13/02/2017.
 */
public class Switches {
    private List<Switch> switchList = new ArrayList<>();
    private final GpioController gpio;

    private Switch create(SwitchName name, SwitchListener listener) {
        Switch aSwitch = new Switch(name, gpio);
        aSwitch.addListener(listener);

        return aSwitch;
    }

    public Switches(final GpioController gpio, SwitchListener listener) {
        this.gpio = gpio;

        switchList.add(create(ONE, listener));
        switchList.add(create(TWO, listener));
        switchList.add(create(THREE, listener));
    }
}
