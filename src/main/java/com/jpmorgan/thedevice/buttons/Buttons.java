package com.jpmorgan.thedevice.buttons;

import com.pi4j.io.gpio.GpioController;

import java.util.ArrayList;
import java.util.List;

import static com.jpmorgan.thedevice.buttons.ButtonName.BIG_RED;
import static com.jpmorgan.thedevice.buttons.ButtonName.COUNTDOWN;

/**
 * Handles all of the wires on the Device
 * <p>
 * Created by S.King on 13/02/2017.
 */
public class Buttons {
    private List<Button> ButtonList = new ArrayList<>();
    private final GpioController gpio;

    private Button create(ButtonName name, ButtonListener listener) {
        Button button = new Button(name, gpio);
        button.addListener(listener);

        return button;
    }

    public Buttons(final GpioController gpio, ButtonListener listener) {
        this.gpio = gpio;

        ButtonList.add(create(BIG_RED, listener));
        ButtonList.add(create(COUNTDOWN, listener));
    }
}
