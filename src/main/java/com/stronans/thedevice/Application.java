package com.stronans.thedevice;

import com.stronans.thedevice.core.StateMachine;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The Device is a state machine to communicate to two Arduino Nano devices, one handing the display of 4 RGB LEDs
 * which indicate a sequence which should be selectedin order to select a wire to cut and another which keeps a
 * consistent countdown of time for an initial starting figure of minutes and seconds.
 *
 */

@SpringBootApplication
@EnableAutoConfiguration
public class Application {
    /**
     * The <code>Logger</code> to be used.
     */
    private static Logger log = LogManager.getLogger(Application.class);

    public static void main(String[] args) {
        log.info("Starting the Device..");
        SpringApplication.run(Application.class, args);
    }
}
