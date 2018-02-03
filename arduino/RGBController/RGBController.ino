#include "Tlc5940.h"
#include "messages.h"

#define VERSION "1.1"
// Serial comms speed
#define SERIAL_SPEED 115200
/*
   Basic Pin setup:
   ------------                                  ---u----
   ARDUINO   13|-> SCLK (pin 25)           OUT1 |1     28| OUT channel 0
             12|                           OUT2 |2     27|-> GND (VPRG)
             11|-> SIN (pin 26)            OUT3 |3     26|-> SIN (pin 11)
             10|-> BLANK (pin 23)          OUT4 |4     25|-> SCLK (pin 13)
              9|-> XLAT (pin 24)             .  |5     24|-> XLAT (pin 9)
              8|                             .  |6     23|-> BLANK (pin 10)
              7|                             .  |7     22|-> GND
              6|                             .  |8     21|-> VCC (+5V)
              5|                             .  |9     20|-> 2K Resistor -> GND
              4|                             .  |10    19|-> +5V (DCPRG)
              3|-> GSCLK (pin 18)            .  |11    18|-> GSCLK (pin 3)
              2|                             .  |12    17|-> SOUT
              1|                             .  |13    16|-> XERR
              0|                           OUT14|14    15| OUT channel 15
   ------------                                  --------

   -  Put the longer leg (anode) of the LEDs in the +5V and the shorter leg
        (cathode) in OUT(0-15).
   -  +5V from Arduino -> TLC pin 21 and 19     (VCC and DCPRG)
   -  GND from Arduino -> TLC pin 22 and 27     (GND and VPRG)
   -  digital 3        -> TLC pin 18            (GSCLK)
   -  digital 9        -> TLC pin 24            (XLAT)
   -  digital 10       -> TLC pin 23            (BLANK)
   -  digital 11       -> TLC pin 26            (SIN)
   -  digital 13       -> TLC pin 25            (SCLK)
   -  The 2K resistor between TLC pin 20 and GND will let ~20mA through each
      LED.  To be precise, it's I = 39.06 / R (in ohms).  This doesn't depend
      on the LED driving voltage.
   - (Optional): put a pull-up resistor (~10k) between +5V and BLANK so that
                 all the LEDs will turn off when the Arduino is reset.

   If you are daisy-chaining more than one TLC, connect the SOUT of the first
   TLC to the SIN of the next.  All the other pins should just be connected
   together:
       BLANK on Arduino -> BLANK of TLC1 -> BLANK of TLC2 -> ...
       XLAT on Arduino  -> XLAT of TLC1  -> XLAT of TLC2  -> ...
   The one exception is that each TLC needs it's own resistor between pin 20
   and GND.

   This library uses the PWM output ability of digital pins 3, 9, 10, and 11.
   Do not use analogWrite(...) on these pins.

*/
#define FULL_ON 4095
#define HALF_ON 2048
#define QUARTER_ON 1024
#define EIGHTH_ON 512
#define OFF 0

#define RED_LED 0
#define BLUE_LED 1
#define GREEN_LED 2

#define LED1 0
#define LED2 3
#define LED3 6
#define LED4 9
#define LED5 12

#define DELAY 1000
#define HALF_DELAY 500

#define START_PIN 0
#define END_PIN 2

// Default colours
#define White 0xffffff
#define Red 0xff0000
#define Green 0x00ff00
#define Blue 0x0000ff
#define Yellow 0xffff00
#define Magenta 0xff007f
#define Cyan 0x00ffff

// Colour range
#define LightBlue 0x009cce
#define Purple 0xC231E1
#define Mustard 0xffce31
#define DarkOrange 0xe1632e
#define DarkRed 0xce0063
#define MossGreen 0x008000
#define LightGreen 0xc0dcc0
#define Aquamarine 0x008080
#define TwilightBlue 0x6666cc
#define TwilightViolet 0x9966cd
#define SkyBlue 0x00ccff
#define SoftPink 0xff9999
#define PowderBlue 0xccccff
#define RegalRed 0xcc3366
#define RedBrown 0xcc6633
#define Plum 0x660066
#define KentuckyGreen 0x339966
#define IceBlue 0x99ffff
#define GrassGreen 0x009933
#define ForestGreen 0x006633
#define ElectricBlue 0x6666ff
#define EasterPurple 0xcc66ff
#define StandardBlue 0x336699
#define DustyRose 0xcc6699
#define DesertBlue 0x336699
#define ArmyGreen 0x669966
#define AutumnOrange 0xff6633
#define AvocadoGreen 0x669933
#define BabyBlue 0x6699ff
#define BananaYellow 0xcccc33
#define BrickRed 0xcc3300
#define Brown 0x996633
#define Crimson 0x993366
#define OceanGreen 0x996633
#define Walnut 0x663300
#define Amber 0xFFBF00
#define BrightGreen 0x66FF00

// State machine position
#define RESTING 1
#define SAFE 2
#define SEQUENCE 3
#define EXPLODE 4

#define COLOURSIZE 44

long allColours[COLOURSIZE] = {
  White,    Red,        Green,      Blue,         Yellow,   Magenta,
  LightBlue, Purple,     Mustard,    DarkOrange,   DarkRed,  MossGreen,
  Cyan,     LightGreen, Aquamarine, TwilightBlue, TwilightViolet, SkyBlue,
  SoftPink, PowderBlue, RegalRed,   RedBrown,     Plum,     KentuckyGreen,
  IceBlue,  GrassGreen, ForestGreen, ElectricBlue, EasterPurple, StandardBlue,
  DustyRose, DesertBlue, ArmyGreen, AutumnOrange, AvocadoGreen, BabyBlue,
  BananaYellow, BrickRed, Brown,    Crimson,      OceanGreen,   Walnut,
  Amber, BrightGreen,
};

const String MSG_HEADER = "{S ";
const int MSG_HEADER_SIZE = MSG_HEADER.length();

// Changing state of this machine
int state = RESTING;

// Message processor comming in from Rasp Pi
Messages *messages;

void setup()
{
  /* Call Tlc.init() to setup the tlc.
     You can optionally pass an initial PWM value (0 - 4095) for all channels.*/
  Tlc.init();
  Serial.begin(SERIAL_SPEED);
  Serial.print("RGB Controller version ");
  Serial.println(VERSION);

  messages = new Messages();

  clearLEDs();
  //    testSequence();
}

void clearLEDs()
{
  Tlc.clear();
  for (int channel = 0; channel < NUM_TLCS * 16; channel ++) {
    Tlc.set(channel, 0);
  }
  /* Tlc.update() sends the data to the TLCs.  This is when the LEDs will actually change. */
  Tlc.update();
}

void testSequence()
{
  for (int i = LED1; i <= LED4; i += 3) {
    Tlc.clear();
    delay(HALF_DELAY);

    setLED(i, Red, FULL_ON);
    Tlc.update();
    delay(HALF_DELAY);

    setLED(i, Green, FULL_ON);
    Tlc.update();
    delay(HALF_DELAY);

    setLED(i, Blue, FULL_ON);
    Tlc.update();
    delay(HALF_DELAY);

    setLED(i, White, FULL_ON);
    Tlc.update();
    delay(HALF_DELAY);

    setLED(i, Yellow, FULL_ON);
    Tlc.update();
    delay(HALF_DELAY);

    Tlc.clear();
    Tlc.update();
    delay(HALF_DELAY);
  }

  for (int i = LED5; i < LED5 + 3; i ++) {
    Tlc.clear();
    Tlc.update();
    delay(10);
    Tlc.set(i, FULL_ON);
    Tlc.update();
    delay(HALF_DELAY);
  }

  Tlc.clear();
  Tlc.update();
  delay(DELAY * 2);
}

void setLED(int LED, long colour, int brightness)
{
  int red = (colour & 0xFF0000) >> 16;
  int green = (colour & 0x00FF00) >> 8;
  int blue = (colour & 0x0000FF);

  Tlc.set(LED + RED_LED, map(red, 0, 0xff, 0, brightness));
  Tlc.set(LED + GREEN_LED, map(green, 0, 0xff, 0, brightness));
  Tlc.set(LED + BLUE_LED, map(blue, 0, 0xff, 0, brightness));
}

long getLong(String info)
{
  long result = 0;
  char carray[20];

  //  Serial.println("info [" + info + "]");

  info.toCharArray(carray, sizeof(carray));
  result = atol(carray);

  return result;
}

static long colourValues[4] = {0, 0, 0, 0};

void processSequence(String sequence) {

  sequence.trim();

  for (int index = 0; index < 4; index++) {
    int nextSpace = sequence.indexOf(" ");
    String numb;
    if (nextSpace == -1) {  // No space found
      numb = sequence.substring(0);
    }
    else {
      numb = sequence.substring(0, nextSpace + 1);
    }
    colourValues[index] = getLong(numb);
    sequence = sequence.substring(nextSpace + 1);
    //    Serial.println("sequence [" + sequence + "]");
  }

  //  for (int i = 0; i < 4; i++) {
  //    Serial.print("Value [");
  //    Serial.print(colourValues[i]);
  //    Serial.println("]");
  //  }
}

int refreshCounter = 0;

void displaySequence() {
  if (refreshCounter == 10) {
    Tlc.clear();
    Tlc.update();
    int index = 0;
    delay(50);

    for (int i = LED1; i <= LED4; i += 3) {
      setLED(i, colourValues[index], FULL_ON);

      //      Serial.print("Display [");
      //      Serial.print(colourValues[index]);
      //      Serial.println("]");

      Tlc.update();
      delay(DELAY);

      Tlc.clear();
      Tlc.update();
      delay(HALF_DELAY);
      index++;
    }

    refreshCounter = 0;
  }

  refreshCounter ++;
  delay(DELAY);
}

int getNumber(String data)
{
  int result = 0;
  char carray[10];
  data.toCharArray(carray, sizeof(carray));
  result = atoi(carray);

  return result;
}

/* Has the other machine sent a message? */
int readStatus(int currentState)
{
  int result = currentState;

  String msg = messages->read(false);

  if (msg.length() > 0) {
    if ((msg.length() > MSG_HEADER_SIZE) && msg.startsWith(MSG_HEADER) && msg.endsWith("}")) {

      String body = msg.substring(MSG_HEADER_SIZE);
      body.trim();  // Trim off white space from both ends.

      //      Serial.println("Body = [" + body + "]");

      // Available status values coming from other machine
      if (body.startsWith("E")) {           // EXPLODE
        result = EXPLODE;
      }
      else if (body.startsWith("S")) {      // SAFE
        result = SAFE;
      }
      else if (body.startsWith("Q")) {      // SEQUENCE
        String extra = body.substring(1);
        processSequence(extra);   // Pass in string minus the "Q"
        clearLEDs();
        refreshCounter = 0;
        result = SEQUENCE;
      }
      else if (body.startsWith("R")) {      // RESTING
        result = RESTING;
      }
      else if (body.startsWith("B")) {      // Set switch
        String numb = body.substring(body.indexOf(" ") + 1);
        int light = getNumber(numb);

        //        Serial.println("Switch received with value = " + String(light));
        if (light == 0) {
          for (int i = LED5; i < LED5 + 3; i ++) {
            Tlc.set(i, OFF);
          }
        }
        else {
          Tlc.set(LED5 + light - 1, FULL_ON);
        }
      }
    }
  }

  return result;
}

void sweepSequence()
{
  for (int i = LED1; i <= LED4; i += 3) {
    int randNumber = random(COLOURSIZE);
    setLED(i, allColours[randNumber], EIGHTH_ON);
  }

  Tlc.update();
  delay(DELAY / 2);
}

void flashing(long colour)
{
  for (int i = LED1; i <= LED4; i += 3) {
    setLED(i, colour, FULL_ON);
  }
  Tlc.update();
  delay(DELAY);

  clearLEDs();
  delay(DELAY);
}

void loop()
{
  state = readStatus(state);

  switch (state)
  {
    case RESTING :
      sweepSequence();
      break;

    case SAFE :
      flashing(White);
      break;

    case SEQUENCE :
      displaySequence();
      break;

    case EXPLODE :
      flashing(Red);
      break;
  }
}

/*
  SerialEvent occurs whenever a new data comes in the
  hardware serial RX.  This routine is run between each
  time loop() runs, so using delay inside loop can delay
  response.  Multiple bytes of data may be available.
*/
void serialEvent() {
  messages->anySerialEvent();
}
