package com.stronans.thedevice.core;

import com.pi4j.io.serial.Serial;
import com.pi4j.io.serial.SerialDataEvent;
import com.pi4j.io.serial.SerialDataEventListener;
import com.pi4j.io.serial.SerialFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Serial communications.
 * Created by S.King on 21/05/2016.
 */
public class SerialComms {
    public static String NANO_MSG_HEADER = "{S ";
    public static int speed = 19200;

    /**
     * The <code>Logger</code> to be used.
     */
    private static Logger log = LogManager.getLogger(SerialComms.class);
    private final Serial serial;
    private String comPort = Serial.DEFAULT_COM_PORT;
    private String message;
    private ArrayBlockingQueue<String> messages = new ArrayBlockingQueue<>(20);

    public SerialComms(String port) throws InterruptedException {

        if (port != null) {
            comPort = port;
        }
        // create an instance of the serial communications class
        serial = SerialFactory.createInstance();

        // create and register the serial data listener
        serial.addListener(new SerialDataEventListener() {
            @Override
            public void dataReceived(SerialDataEvent event) {
                try {
                    message = event.getString(Charset.defaultCharset());
                } catch (IOException e) {
                    log.error("Cannot convert text from : " + comPort + " : " + e.getMessage(), e);
                }
                log.trace("Received message : " + message);

                if (message.endsWith("\n")) {
                    messages.add(message.substring(0, message.indexOf('\n')));
                }
            }
        });
    }

    public SerialComms() throws InterruptedException {
        this(null);
    }

    public String getPort() {
        return comPort;
    }

    public ArrayBlockingQueue<String> messages() {
        return messages;
    }

    public void sendMessage(String message) {
        try {
            message = NANO_MSG_HEADER + message + "}\n";
            log.trace("Message Sent : [" + message + "] port :" + comPort);
            serial.write(message);
        } catch (IOException ex) {
            log.error(" ==>> SERIAL WRITE FAILED on port : " + comPort + " : " + ex.getMessage(), ex);
        }
    }

    public void startComms() {
        try {
            // open the default serial port provided on the GPIO header
            serial.open(comPort, speed);
        } catch (IOException ex) {
            log.error(" ==>> SERIAL SETUP FAILED on port : " + comPort + " : " + ex.getMessage(), ex);
        }
    }

    public void endComms() {
        try {
            // open the default serial port provided on the GPIO header
            if (serial.isOpen()) {
                serial.close();
            }
        } catch (IOException ex) {
            log.error(" ==>> SERIAL SHUTDOWN FAILED on port : " + comPort + " : " + ex.getMessage(), ex);
        }
    }
}