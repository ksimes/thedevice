#include <Wire.h> // Enable this line if using Arduino Uno, Mega, etc.
#include <Adafruit_GFX.h>
#include "Adafruit_LEDBackpack.h"

#include "messages.h"

#define VERSION "1.1"
// Serial comms speed
#define SERIAL_SPEED 115200

#define BASE_TIME 30

#define RESTING 0
#define COUNTDOWN 1
#define SAFE 2
#define EXPLODE 3
#define ADD 4
#define BUZZ 5

Adafruit_7segment matrix = Adafruit_7segment();
int mins = BASE_TIME;
int secs = 00;
int countup = 0;
int extra = 0;
int delayTime = 1000;
unsigned int state = RESTING;
boolean drawDots = false;

const long speed = 19200;

// Message processor comming in from Rasp Pi
Messages *messages;

const String MSG_HEADER = "{S ";
const int MSG_HEADER_SIZE = MSG_HEADER.length();

const int buzzer = 6;        // buzzer to arduino pin 6

void setup() {
  Serial.begin(SERIAL_SPEED);
  Serial.print("Countdown Controller version ");
  Serial.println(VERSION);

  messages = new Messages();
  matrix.begin(0x70);
  matrix.setBrightness(2);

  pinMode(buzzer, OUTPUT); // Set buzzer - pin 6 as an output
}

int getNumber(String data)
{
  int result = 0;
  char carray[10];
  data.toCharArray(carray, sizeof(carray));
  result = atoi(carray);

  return result;
}

void buzz() {
  // turn Sound on:
  tone(buzzer, 1000); // Send 1KHz sound signal...
  delay(500);
  tone(buzzer, 3000);
  delay(500);
  tone(buzzer, 2000);
  delay(1000);
  noTone(buzzer);     // Stop sound...
}

void buzz2() {
  tone(buzzer, 2000);
  delay(500);
  noTone(buzzer);     // Stop sound...
}

/* Has the other machine sent a message? */
unsigned int readStatus(unsigned int currentState)
{
  unsigned int result = currentState;
  String msg = messages->read(false);

  if (msg.length() > 0) {
    if (( msg.length() > MSG_HEADER_SIZE) && msg.startsWith(MSG_HEADER) && msg.endsWith("}")) {
      Serial.println("Echo : " + msg);

      String status = msg.substring(MSG_HEADER_SIZE);

      status.trim();  // Trim off white space from both ends.

      // Available status values coming from other machine
      // EXPLODE goto RESTING
      // MISTAKE divide delay by 2 and continue with COUNTDOWN
      // RESET set delay to 1 sec, counters back to 15 mins, goto COUNTDOWN

      if (status.startsWith("E")) {           // EXPLODE
        Serial.println(MSG_HEADER + "Explode }");
        result = EXPLODE;
      }
      else if (status.startsWith("M")) {      // MISTAKE
        delayTime = delayTime / 2;
        result = currentState;
        //        Serial.println("Mistake received");
      }
      else if (status.startsWith("S")) {      // SAFE
        //        Serial.println("Safe received");
        result = SAFE;
      }
      else if (status.startsWith("U")) {      // SUBTRACT
        //        Serial.println("Safe received");
        result = SAFE;
      }
      else if (status.startsWith("A") && (state == RESTING || state == ADD)) {      // EXTRA TIME
        //        Serial.println("add extra received");
        result = ADD;
        extra++;
      }
      else if (status.startsWith("W")) {      // WAITING
        //        Serial.println("Waiting received");
        result = RESTING;
        extra = 0;
      }
      else if (status.startsWith("B")) {      // LONG BUZZ
        //        Serial.println("Buzz received");
        buzz();
        result = currentState;
      }
      else if (status.startsWith("2")) {      // SHORT BUZZ
        //        Serial.println("Buzz received");
        buzz2();
        result = currentState;
      }
      else if (status.startsWith("R")) {      // RESET COUNT
        String numb = status.substring(status.indexOf(" ") + 1);
        int count = getNumber(numb);

        //        Serial.println("Reset received with count = " + String(count));
        mins = BASE_TIME + count;
        secs = 00;
        countup = 0;
        delayTime = 1000;
        drawDots = false;
        result = COUNTDOWN;
      }
    }
  }

  return result;
}

void loop() {
  state = readStatus(state);

  switch (state)
  {
    case RESTING :
      matrix.blinkRate(3);
      matrix.print(10000, DEC);
      matrix.writeDisplay();
      break;

    case SAFE :
      matrix.blinkRate(1);
      matrix.print(0x5AFE, HEX);
      matrix.writeDisplay();
      break;

    case ADD :
      matrix.blinkRate(0);
      matrix.print(extra, DEC);
      matrix.writeDisplay();
      break;

    case EXPLODE :
      matrix.blinkRate(2);
      matrix.print(0xDEAD, HEX);
      matrix.writeDisplay();
      break;

    case COUNTDOWN :
      matrix.blinkRate(0);
      if (mins == 0 && secs == 0) {
        delayTime = 1000; // Set back to once a second.

        countup--;
        matrix.println(countup);
        matrix.drawColon(false);
      }
      else {
        // Set the "time" display
        if (secs == 0) {
          secs = 59;
          mins--;
        }
        else {
          secs--;
        }

        int value = (mins * 100) + secs;
        matrix.println(value);
        matrix.drawColon(drawDots);

        if (drawDots) {
          drawDots = true;
        } else {
          drawDots = false;
        }
      }

      matrix.writeDisplay();
      delay(delayTime);

      if (countup == -5) {
        Serial.println(MSG_HEADER + "EXPLODE}");
        state = EXPLODE;
      }
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
