# README #

### What is this project for? ###

"The Device" emulates a bomb with a countdown timer and a set of wires which must be cut to "defuse" it. 
It is composed of a state machine which communicated with two Arduino Nano devices, one of these is 
handing the display of 4 RGB LEDs which are used to indicate a sequence which should be selected 
in order to select a coloured wire to cut and another which keeps a consistent countdown 
for an initial starting figure of what appears to be minutes and seconds.

Originally developed for CDX 2017.
  
* This is version 2.0 of the device for CDX 2018

version 2 loses the big red button which was used for adding minutes to the timer manually.
This is now controlled by a REST endpoint. Also the start countdown button is no longer connected.
Again this is controlled by a REST endpoint.
  

### How do I get set up? ###

- Summary of set up
- Configuration

  - GPIO pins are setup in the Names classes, SwitchName, WireName and ButtonName.
  - Choice of 6 Colours for the wires is pulled from the CSV file in the resouces folder.
    This is loaded as an array and 6 contiguous values are selected. 
- Dependencies.
  - Defined in the POM file.

- Database configuration
  - None
- How to run tests
- Deployment instructions
  - mvn

### Contribution guidelines ###

- Writing tests
- Code review
- Other guidelines

Connections
-----------

Raspberry Pi II pinouts used: 

![alt text](./pinouts.png "Raspberry Pi II pinouts used")



### Who do I talk to? ###

* simon.king@stronans.com
