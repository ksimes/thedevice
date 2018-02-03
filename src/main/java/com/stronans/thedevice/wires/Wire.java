package com.stronans.thedevice.wires;

import com.stronans.thedevice.buttons.Button;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * A single wire to detect an individual wire being cut.
 * <p>
 * Created by S.King on 15/02/2017.
 */
public class Wire {
    /**
     * The <code>Logger</code> to be used.
     */
    private static Logger log = LogManager.getLogger(Button.class);
    private WireName gpioPin;
    private List<WireListener> listeners = new ArrayList<>();

    public Wire(WireName gpioPin, final GpioController gpio) {

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
                log.trace(" --> GPIO PIN STATE CHANGE: " + event.getPin() + " = " + event.getState());
            }
        });
    }

    public boolean addListener(WireListener listener) {
        boolean result = false;

        listeners.add(listener);

        return result;
    }

    private void notifyListeners(WireName gpioPin) {
        if (!listeners.isEmpty()) {
            for (WireListener listener : listeners) {
                listener.wireCut(gpioPin);
            }
        }
    }
}

