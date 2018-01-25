package com.jpmorgan.thedevice.switches;

import com.pi4j.io.gpio.Pin;

import static com.pi4j.io.gpio.RaspiPin.*;

/**
 * Created by S.King on 15/02/2017.
 */
public enum SwitchName {
    ONE(GPIO_27),
    TWO(GPIO_28),
    THREE(GPIO_29),
    NONE(GPIO_00);

    private Pin gpioPin;

    private SwitchName(Pin numVal) {
        this.gpioPin = numVal;
    }

    public Pin getVal() {
        return gpioPin;
    }
}
