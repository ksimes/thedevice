package com.stronans.thedevice.core;

/**
 * Heart of the Device. Statemachine which watches the buttons/Wires and Switches changing state during execution.
 * <p>
 * Created by S.King on 13/02/2017.
 */

import com.stronans.thedevice.buttons.ButtonListener;
import com.stronans.thedevice.buttons.ButtonName;
import com.stronans.thedevice.buttons.Buttons;
import com.stronans.thedevice.colours.CSVFileReader;
import com.stronans.thedevice.colours.ColourSequence;
import com.stronans.thedevice.colours.ColourSet;
import com.stronans.thedevice.switches.SwitchListener;
import com.stronans.thedevice.switches.SwitchName;
import com.stronans.thedevice.switches.Switches;
import com.stronans.thedevice.wires.WireListener;
import com.stronans.thedevice.wires.WireName;
import com.stronans.thedevice.wires.Wires;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.stronans.thedevice.core.State.*;
import static com.stronans.thedevice.wires.WireName.NONE;

public class StateMachine implements ButtonListener, SwitchListener, WireListener {
    /**
     * The <code>Logger</code> to be used.
     */
    private static Logger log = LogManager.getLogger(StateMachine.class);
    private static final String COUNTDOWN_NANO = "/dev/ttyUSB0";
    private static final String LED_NANO = "/dev/ttyUSB1";

    private static String NANO_MSG_HEADER = "{S ";
    private static int CONNECTION_SPEED = 115200;

    private static SerialCommsNew LEDNano;
    private static SerialCommsNew CountdownNano;

    private State state = RESTING;
    private int extraTime = 0;
    private int strikes = 0;

    private List<ColourSequence> colourSequences;
    private int colourStartPoint = 0;
    private int wiresCutPoint = 0;
    private Action currentSet[] = new Action[6];

    public StateMachine() {
        try {
            // Initialise GPIO connection
            // create gpio controller
            final GpioController gpio = GpioFactory.getInstance();

            // Setup communications with Nano devices
            LEDNano = new SerialCommsNew(LED_NANO, CONNECTION_SPEED);
            LEDNano.startComms();
            LEDNano.sendMessage("REST");

            CountdownNano = new SerialCommsNew(COUNTDOWN_NANO, CONNECTION_SPEED);
            CountdownNano.startComms();
            CountdownNano.sendMessage("WAIT");

            // Setup comms with BalloonPopper
            // Not required here as using a REST interface which is handled by a separate class.

            // Setup Wires (to be cut) treated as switches.
            Wires wires = new Wires(gpio, this);
            wires.setExpected(NONE);

            // Setup switches
            Switches switches = new Switches(gpio, this);

            // Setup buttons
            // Big Red button & Start Countdown button
            /*
             * The Start Countdown button has only one purpose and that is to start the countdown process.
             *
             */
            Buttons buttons = new Buttons(gpio, this);

            // Housekeeping at shutdown of program
            Runtime.getRuntime().addShutdownHook(new Thread() {
                                                     @Override
                                                     public void run() {
                                                         LEDNano.endComms();
                                                         CountdownNano.endComms();
                                                         gpio.shutdown();   // <--- implement this method call if you wish to terminate the Pi4J GPIO controller
                                                         log.info("Exiting program.");
                                                     }
                                                 }

            );

            // Read in the full colour set to choose a set of six to use during this run
            CSVFileReader file = new CSVFileReader();
            InputStream is = getClass().getResourceAsStream("/colourset.csv");
            colourSequences = file.read(is);

            log.info("Everything now setup and running");

        } catch (InterruptedException e) {
            log.error(" ==>> PROBLEMS WITH SERIAL COMMUNICATIONS: " + e.getMessage(), e);
//        } catch (IOException ioe) {
//            log.error(" ==>> PROBLEMS WITH CALLING SUDO SHUTDOWN: " + ioe.getMessage(), ioe);
        }
    }

    @Override
    // A button has been pressed
    public void buttonPressed(ButtonName name) {

        switch (name) {
            case BIG_RED:
                log.debug("Red button pressed");
                switch (state) {
                    case RESTING:
                        CountdownNano.sendMessage("ADD");
                        CountdownNano.sendMessage("2");
                        extraTime++;
                        log.debug("Extra time added");
                        break;

                    case EXPLODE:
                        Reset();
                        state = RESTING;
                        break;

                    case COUNTDOWN:
                        break;

                    case SAFE:
                        break;
                }

                break;

            case COUNTDOWN:
                log.debug("Countdown button pressed");
                switch (state) {
                    case RESTING:
                        strikes = 0;
                        CountdownNano.sendMessage("RESET " + extraTime);
                        log.debug("Start Countdown");
                        LEDNano.sendMessage("REST");

                        sendSequence(colourSequences.get(colourStartPoint));
                        state = COUNTDOWN;
                        break;

                    case SAFE:
                        CountdownNano.sendMessage("WAIT");
                        LEDNano.sendMessage("REST");
                        state = RESTING;
                        break;
                }
                break;
        }
    }

    @Override
    public void switchThrown(SwitchName name) {
        log.debug("Switch thrown");

        switch (state) {
            case RESTING:
                break;

            case EXPLODE:
                break;

            case COUNTDOWN:
                break;

            case SAFE:
                break;
        }
    }

    @Override
    public void wireCut(WireName name) {
        log.debug("Wire Cut");

        switch (state) {
            case RESTING:
                explode();
                break;

            case COUNTDOWN:
                ColourSequence item = colourSequences.get(colourStartPoint);
                log.debug("Wire cut : " + name + "    expected wire : " + item.getDecode());

                if (name.equals(item.getDecode())) {
                    CountdownNano.sendMessage("2");
                    log.debug("Success");
                } else {
                    // Allowed 3 mistakes then explodes
                    log.debug("Mistake");
                    strikes++;
                    if (strikes < 3) {
                        CountdownNano.sendMessage("BUZZ");
                        CountdownNano.sendMessage("MISTAKE");
                    } else {
                        explode();
                    }
                }
                colourStartPoint++;
                wiresCutPoint++;

                // Cut all six wires without 3 mistakes then safe
                if(wiresCutPoint == 6) {
                    CountdownNano.sendMessage("SAFE");
                    LEDNano.sendMessage("SAFE");
                    state = SAFE;
                }
                else {
                    sendSequence(colourSequences.get(colourStartPoint));
                    showSequence();
                }
                break;

            case EXPLODE:
                break;

            case SAFE:
                break;
        }
    }

    private void sendSequence(ColourSequence item) {
        String msg = "Q ";

        for(ColourSet colour : item.getSequence()) {
            msg += colour.getVal() + " ";
        }

        msg = msg.trim();

        log.debug("SeQuence Msg : [" + msg + "]");

        LEDNano.sendMessage(msg);
    }

    private void wait(int seconds) {
        // block the current thread for the payload duration
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            log.warn(seconds + " second sleep interrupted: " + e.getMessage(), e);
        }
    }

    private void Reset() {
        CountdownNano.sendMessage("WAIT");
        LEDNano.sendMessage("REST");
        selectSetOfColourSequences();

        extraTime = 0;
        strikes = 0;
    }

    private void explode() {
        state = EXPLODE;
        LEDNano.sendMessage("EXPLODE");
        CountdownNano.sendMessage("EXPLODE");
        SendMsgToPopper.Send(SendMsgToPopper.Action.POP);
        // Select new set of colours
        selectSetOfColourSequences();
        wait(5);
        SendMsgToPopper.Send(SendMsgToPopper.Action.RESET);
        extraTime = 0;
        strikes = 0;
    }

    private void processMessage(String message, String port) {

        message = message.trim();
        log.info("Serial input [" + message + "]   Port :" + port);

        if (message.startsWith(NANO_MSG_HEADER) && message.endsWith("}")) {
            int h_index = message.indexOf(" ");       // Is there anything else in the string?
            if (h_index != -1) {
                log.info("Message [" + message + "]");

                String substring = message.substring(h_index);
                substring = substring.trim();

                log.info("substring [" + substring + "]");
                // Explode
                if (substring.startsWith("E")) {
                    log.info("Explode received");
                    if (state != EXPLODE) {
                        explode();
                    }
                } else if (substring.startsWith("S")) {
                    log.info("Safe received");
                    state = SAFE;
                }
            }
        }
    }

    private void showSequence() {
        ColourSequence item = colourSequences.get(colourStartPoint);
        log.debug("Expected wire : " + item.getDecode());
        log.debug("Colour sequence: " + item.toString());
    }

    private void selectSetOfColourSequences() {
        Random random = new Random();
        int startPoint = 0;

        while (startPoint == 0 && startPoint < colourSequences.size() - 6) {
            startPoint = random.nextInt(colourSequences.size() - 1) + 1;
        }

        wiresCutPoint = 0;
        for (int i = 0; i < 6; i++) {
            currentSet[i] = Action.WAITING;
        }

        colourStartPoint = startPoint;
        showSequence();
    }

    public void startSequence() {
        selectSetOfColourSequences();

        // keep program running until user aborts (CTRL-C)
        try {
            while (true) {
                Thread.sleep(100);
                String message = "";
                if(!CountdownNano.messages().isEmpty()) {
                    message = CountdownNano.messages().take();
                    processMessage(message, CountdownNano.getPort());
                }

                if(!LEDNano.messages().isEmpty()) {
                    message = LEDNano.messages().take();
                    processMessage(message, LEDNano.getPort());
                }
            }
        } catch (InterruptedException e) {
            log.error(" Interrupt during sleep : " + e.getMessage(), e);
        }
    }
}
