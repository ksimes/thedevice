package com.jpmorgan.thedevice.colours;

import com.jpmorgan.thedevice.wires.WireName;

import java.util.Arrays;

/**
 * Simple POJO for processing of colour sequences on th device.
 * <p>
 * Created by S.King on 15/02/2017.
 */
public class ColourSequence {
    private ColourSet[] sequence = new ColourSet[4];
    private WireName decode;
    private int aSwitch;

    public ColourSequence(ColourSet one, ColourSet two, ColourSet three, ColourSet four, WireName decode, int aSwitch) {
        this.sequence[0] = one;
        this.sequence[1] = two;
        this.sequence[2] = three;
        this.sequence[3] = four;
        this.decode = decode;
        this.aSwitch = aSwitch;
    }

    public ColourSet[] getSequence() {
        return sequence;
    }

    public WireName getDecode() {
        return decode;
    }

    public int theSwitch() {
        return aSwitch;
    }

    @Override
    public String toString() {
        return "ColourSequence{" +
                "sequence=" + Arrays.toString(sequence) +
                ", decode=" + decode +
                ", aSwitch=" + aSwitch +
                '}';
    }
}
