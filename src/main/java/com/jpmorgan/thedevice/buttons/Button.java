package com.jpmorgan.thedevice.buttons;

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
 * A single button to detect a button press.
 * <p>
 * Created by S.King on 15/02/2017.
 */
public class Button {
    /**
     * The <code>Logger</code> to be used.
     */
    private static Logger log = LogManager.getLogger(Button.class);
    private ButtonName gpioPin;
    private List<ButtonListener> listeners = new ArrayList<>();

    public Button(ButtonName gpioPin, final GpioController gpio) {

        this.gpioPin = gpioPin;

        // provision gpio pin as an input pin with its internal pull down resistor enabled
        final GpioPinDigitalInput thisPin = gpio.provisionDigitalInputPin(gpioPin.getVal(), PinPullResistance.PULL_DOWN);

        // set shutdown state for this input pin
        thisPin.setShutdownOptions(true);

        // create and register gpio pin listener
        thisPin.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                // Button goes high when it has been pressed
                if (event.getState().isHigh()) {
                    notifyListeners(gpioPin);
                }

                // display pin state on console
                log.trace(" --> GPIO PIN STATE CHANGE: " + event.getPin() + " = " + event.getState());
            }
        });
    }

    boolean addListener(ButtonListener listener) {
        boolean result = false;

        listeners.add(listener);

        return result;
    }

    private void notifyListeners(ButtonName gpioPin) {
        log.trace(" Notify : " + gpioPin.toString());

        if (!listeners.isEmpty()) {
            for (ButtonListener listener : listeners) {
                listener.signal(gpioPin);
            }
        }
    }
}

