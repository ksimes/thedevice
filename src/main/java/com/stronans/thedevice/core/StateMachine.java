package com.stronans.thedevice.core;

/**
 * Heart of the Device. Statemachine which watches the buttons/Wires and Switches changing state during execution.
 * <p>
 * Created by S.King on 13/02/2017.
 */

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.stronans.thedevice.core.State.*;
import static com.stronans.thedevice.wires.WireName.NONE;

@Component
@RestController
public class StateMachine implements ButtonListener, SwitchListener, WireListener, MessageListener {
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
    private static Wires wires;

    private State state = RESTING;
    private int extraTime = 0;
    private int strikes = 0;
    private int successes = 0;

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
            LEDNano.addListener(this);
            LEDNano.startComms();
            LEDNano.sendMessage("REST");

            CountdownNano = new SerialCommsNew(COUNTDOWN_NANO, CONNECTION_SPEED);
            CountdownNano.addListener(this);
            CountdownNano.startComms();
            CountdownNano.sendMessage("WAIT");

            // Setup comms with BalloonPopper
            // Not required here as using a REST interface which is handled by a separate class.

            // Setup Wires (to be cut) treated as switches.
            wires = new Wires(gpio, this);
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
                        reset("Reset from Explosion");
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
                        startCountdown("Countdown started from button");
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

    private void startCountdownAt(int time, String logMessage) {
        strikes = 0;
        CountdownNano.sendMessage("RESET " + (time - 5));
        log.info(logMessage);
        LEDNano.sendMessage("REST");

        sendSequence(colourSequences.get(colourStartPoint));
        state = COUNTDOWN;
    }

    private void startCountdown(String logMessage) {
        strikes = 0;
        CountdownNano.sendMessage("RESET " + extraTime);
        log.info(logMessage);
        LEDNano.sendMessage("REST");

        sendSequence(colourSequences.get(colourStartPoint));
        state = COUNTDOWN;
    }

    private void startCountdownWith10(String logMessage) {
        startCountdownAt(11, logMessage);
    }

    private void startCountdownWith15(String logMessage) {
        startCountdownAt(16, logMessage);
    }

    private void startCountdownWith20(String logMessage) {
        startCountdownAt(21, logMessage);
    }

    private void startCountdownWith25(String logMessage) {
        startCountdownAt(26, logMessage);
    }

    private void startCountdownWith30(String logMessage) {
        startCountdownAt(31, logMessage);
        // strikes = 0;
        // CountdownNano.sendMessage("RESET " + 26);
        // log.info(logMessage);
        // LEDNano.sendMessage("REST");

        // sendSequence(colourSequences.get(colourStartPoint));
        // state = COUNTDOWN;
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
                    successes++;
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
//                if(wiresCutPoint == 6) {
                if (successes == 3) {
                    CountdownNano.sendMessage("SAFE");
                    LEDNano.sendMessage("SAFE");
                    state = SAFE;
                } else {
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

        for (ColourSet colour : item.getSequence()) {
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

    private void explode() {
        log.info("State EXPLODE");
        state = EXPLODE;
        LEDNano.sendMessage("EXPLODE");
        CountdownNano.sendMessage("EXPLODE");
//        SendMsgToPopper.Send(SendMsgToPopper.Action.POP);
        // Select new set of colours
        selectSetOfColourSequences();
        wait(5);
//        SendMsgToPopper.Send(SendMsgToPopper.Action.RESET);
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

    @Override
    public void messageReceived() {
        try {
            String message = "";
            if (!CountdownNano.messages().isEmpty()) {
                message = CountdownNano.messages().take();
                log.info("Msg from Countdown Nano :" + message);
                processMessage(message, CountdownNano.getPort());
            }

            if (!LEDNano.messages().isEmpty()) {
                message = LEDNano.messages().take();
                log.info("Msg from LED Nano :" + message);
                processMessage(message, LEDNano.getPort());
            }
        } catch (InterruptedException e) {
            log.error(" Interrupt during sleep : " + e.getMessage(), e);
        }
    }

    @RequestMapping("/device/start")
    public void startSequence() {
        log.info("Start selected from REST");
        selectSetOfColourSequences();
        startCountdown("Countdown started from REST");
    }

    @RequestMapping("/device/start10")
    public void startSequenceWith10() {
        log.info("Start 10 selected from REST");
        selectSetOfColourSequences();
        startCountdownWith10("Countdown 10 started from REST");
    }

    @RequestMapping("/device/start15")
    public void startSequenceWith15() {
        log.info("Start 15 selected from REST");
        selectSetOfColourSequences();
        startCountdownWith15("Countdown 15 started from REST");
    }

    @RequestMapping("/device/start20")
    public void startSequenceWith20() {
        log.info("Start 20 selected from REST");
        selectSetOfColourSequences();
        startCountdownWith20("Countdown 20 started from REST");
    }

    @RequestMapping("/device/start25")
    public void startSequenceWith25() {
        log.info("Start 25 selected from REST");
        selectSetOfColourSequences();
        startCountdownWith25("Countdown 25 started from REST");
    }

    @RequestMapping("/device/start30")
    public void startSequenceWith30() {
        log.info("Start 30 selected from REST");
        selectSetOfColourSequences();
        startCountdownWith30("Countdown 30 started from REST");
    }

    @RequestMapping("/device/explode")
    public void deviceExplode() {
        explode();
    }

    @RequestMapping("/device/reset")
    public void resetFromWeb(String logMessage) {
        reset("Reset from REST message");
    }

    public void reset(String logMessage) {
        log.info(logMessage);
        CountdownNano.sendMessage("WAIT");
        LEDNano.sendMessage("REST");
        selectSetOfColourSequences();

        extraTime = 0;
        strikes = 0;

        state = RESTING;
    }
}
