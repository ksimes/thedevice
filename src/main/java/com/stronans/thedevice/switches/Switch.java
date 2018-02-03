package com.stronans.thedevice.switches;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

import java.util.ArrayList;
import java.util.List;

/**
 * A single wire to detect the wire being cut.
 * <p>
 * Created by S.King on 15/02/2017.
 */
public class Switch {
    private SwitchName gpioPin;
    private List<SwitchListener> listeners = new ArrayList<>();

    public Switch(SwitchName gpioPin, final GpioController gpio) {

        this.gpioPin = gpioPin;

        // provision gpio pin as an input pin with its internal pull down resistor enabled
        final GpioPinDigitalInput thisPin = gpio.provisionDigitalInputPin(gpioPin.getVal(), PinPullResistance.PULL_DOWN);

        // set shutdown state for this input pin
        thisPin.setShutdownOptions(true);

        // create and register gpio pin listener
        thisPin.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                // Wire goes low as it has been cut
                if (event.getState().isLow()) {
                    notifyListeners(gpioPin);
                }

                // display pin state on console
                System.out.println(" --> GPIO PIN STATE CHANGE: " + event.getPin() + " = " + event.getState());

            }
        });
    }

    public boolean addListener(SwitchListener listener) {
        boolean result = false;

        listeners.add(listener);

        return result;
    }

    private void notifyListeners(SwitchName gpioPin) {
        if (!listeners.isEmpty()) {
            for (SwitchListener listener : listeners) {
                listener.switchThrown(gpioPin);
            }
        }
    }
}

