package com.jpmorgan.thedevice.buttons;

import com.pi4j.io.gpio.Pin;

import static com.pi4j.io.gpio.RaspiPin.*;

/**
 * Created by S.King on 15/02/2017.
 */
public enum ButtonName {
    BIG_RED(GPIO_04),
    COUNTDOWN(GPIO_05),
    NONE(GPIO_00);

    private Pin gpioPin;

    private ButtonName(Pin numVal) {
        this.gpioPin = numVal;
    }

    public Pin getVal() {
        return gpioPin;
    }
}
