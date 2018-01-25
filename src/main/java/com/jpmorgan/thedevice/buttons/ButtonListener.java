package com.jpmorgan.thedevice.buttons;

/**
 * Created by S.King on 15/02/2017.
 */
public interface ButtonListener {

    // When a Button is pressed then the signal goes high?
    void signal(ButtonName name);
}
