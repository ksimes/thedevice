#include <ESP8266WiFi.h>
#include <Servo.h>

#define SERVO_PIN 15

const char* ssid     = "TALKTALK-66430C";
const char* password = "WHBQTGAQ";

WiFiServer server(80);  // Setup server to recieve messages
Servo myservo;          // create servo object to control a servo
String request = "";

void setup()
{
  Serial.begin(115200);
  Serial.println();

  Serial.printf("Connecting to %s network", ssid);
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED)
  {
    delay(500);
    Serial.print(".");
  }
  Serial.println(" connected");

  server.begin();
  Serial.printf("Web server started, open %s in a web browser\n", WiFi.localIP().toString().c_str());

}

void positionServo(int angle) {
  myservo.attach(SERVO_PIN); // attaches the servo on pin SERVO_PIN to the servo object.
  myservo.write(angle);      // tell servo to go to angle position.
  delay(1000);
  myservo.detach();          // DSetach to stop buzzing noise.
}

// prepare a web page to be send to a client (web browser)
String prepareHtmlPage()
{
  String htmlPage =
    String("HTTP/1.1 200 OK\r\n") +
    "Content-Type: text/html\r\n" +
    "Connection: close\r\n" +  // the connection will be closed after completion of the response
    "\r\n" +
    "<!DOCTYPE HTML>" +
    "<html><H1>" +
    "GO AWAY!!" +
    "<H1></html>" +
    "\r\n";
  return htmlPage;
}

String returnOK()
{
  String htmlResult = String("HTTP/1.1 200 OK\r\n");
  return htmlResult;
}

String processRequest(String msg) {
  String result = "";

  String firstLine = msg.substring(0, msg.indexOf('\n'));
  firstLine.trim();
  //  Serial.println("Firstline : [" + firstLine + "]");

  int firstSpace = firstLine.indexOf(' ');
  String verb = firstLine.substring(0, firstSpace);
  verb.trim();
  Serial.println("Verb : [" + verb + "]");

  String request = firstLine.substring(' ', firstSpace + 1);
  request.trim();
  Serial.println("Request : [" + request + "]");

  if (verb == "POST") {
    if (request.startsWith("/balloon/pop"))    {
      Serial.println("Pop");
      positionServo(180);
      result = returnOK();
    }
    else if (request.startsWith("/balloon/reset")) {
      Serial.println("Reset");
      positionServo(20);
      result = returnOK();
    }
    else {
      result = prepareHtmlPage();
    }
  }
  else {
    result = prepareHtmlPage();
  }

  return result;
}

void loop()
{
  WiFiClient client = server.available();
  // wait for a client (web browser) to connect
  if (client)
  {
    Serial.println("\n[Client connected]");
    request = "";
    while (client.connected())
    {
      // read line by line what the client (web browser) is requesting
      if (client.available())
      {
        String line = client.readStringUntil('\r');
        Serial.print("Line : " + line);
        // wait for end of client's request, that is marked with an empty line
        if (line.length() == 1 && line[0] == '\n')
        {
          Serial.println("request recieved : [" + request + "]");
          client.println(processRequest(request));
          break;
        }
        else {
          request += line;
        }
      }
    }
    delay(1); // give the web browser time to receive the data

    // close the connection:
    client.stop();
    Serial.println("[Client disconnected]");
  }
}
