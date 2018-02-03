package com.stronans.thedevice.wires;

import com.pi4j.io.gpio.Pin;

import static com.pi4j.io.gpio.RaspiPin.*;
import static com.pi4j.io.gpio.RaspiPin.GPIO_24;
import static com.pi4j.io.gpio.RaspiPin.GPIO_25;

/**
 * Created by S.King on 15/02/2017.
 */
public enum WireName {
    RED(GPIO_26),
    WHITE(GPIO_21),
    GREEN(GPIO_22),
    GREY(GPIO_23),
    BLUE(GPIO_24),
    ORANGE(GPIO_25),
    NONE(GPIO_00);

    private Pin gpioPin;

    private WireName(Pin numVal) {
        this.gpioPin = numVal;
    }

    public Pin getVal() {
        return gpioPin;
    }

}
