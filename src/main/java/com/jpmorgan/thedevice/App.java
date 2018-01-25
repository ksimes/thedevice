package com.jpmorgan.thedevice;

import com.jpmorgan.thedevice.core.StateMachine;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Hello world!
 *
 */
public class App 
{
    /**
     * The <code>Logger</code> to be used.
     */
    private static Logger log = LogManager.getLogger(App.class);

    public static void main( String[] args )
    {
        StateMachine stateMachine = new StateMachine();
        stateMachine.startSequence();
    }
}
