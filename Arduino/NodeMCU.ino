#include <SoftwareSerial.h>
#include <ArduinoJson.h>
#include <ESP8266WiFi.h>
#include <PubSubClient.h>
SoftwareSerial s(D6, D5); // (Rx, Tx)
//#include <Adafruit_SSD1306.h>

//Wifi & MQTT server credentials
const char *ssid = "wifiname";
const char *password = "wifipassword";
const char *mqttServer = "mqttserverip";
const int mqttPort = 1883;
const char *mqttUser = "mattusername";
const char *mqttPassword = "mqttPassword";

WiFiClient espClient;
PubSubClient client(espClient);

void reconnect()
{
  // Loop until we're reconnected
  while (!client.connected())
  {
    Serial.print("Attempting MQTT connection...");
    // Create a random client ID
    String clientId = "ESP8266Client-";
    clientId += String(random(0xffff), HEX);
    // Attempt to connect
    if (client.connect(clientId.c_str(), mqttUser, mqttPassword))
    {
      Serial.println("connected");
      // Once connected, publish an announcement...
      client.publish("outTopic", "hello world");
      // ... and resubscribe
      client.subscribe("Limit"); //Subscribe to limit topic to get new limit if it has been updated
      client.subscribe("State");
      client.subscribe("Clear");
      client.subscribe("Message");

    }
    else
    {
      Serial.print("failed, rc=");
      Serial.print(client.state());
      Serial.println(" try again in 5 seconds");
      // Wait 5 seconds before retrying
      delay(5000);
    }
  }
}

void setup()
{
  //Initialize Serial
  Serial.begin(9600);
  //Initialize Wifi Credentials
  WiFi.mode(WIFI_STA);
  WiFi.begin(ssid, password);
  //Connect to Wifi
  while (WiFi.status() != WL_CONNECTED)
  {
    delay(500);
    Serial.println("Connecting to WiFi..");
  }
  Serial.println("Connected to the WiFi network");
  //Connect to MQTT Server
  client.setServer(mqttServer, mqttPort);
  client.setCallback(callback);
  //Check the Connection
  while (!client.connected())
  {
    Serial.println("Connecting to MQTT...");
    if (client.connect("ESP8266Client", mqttUser, mqttPassword))
    {
      Serial.println("connected");
    }
    else
    {
      Serial.print("failed with state ");
      Serial.print(client.state());
      delay(2000);
    }
  }
  //Publish and Subscribe to MQTT
  client.publish("Hello", "Hello from ESP8266");
  client.subscribe("Limit"); //Subscribe to limit topic to get new limit if it has been updated
  client.subscribe("State");
  client.subscribe("Clear");
  client.subscribe("Message");
  //digitalWrite(relay, HIGH);
  // Initialize Serial port
  s.begin(9600);
  while (!Serial)
    continue;
}

//Variables
float limit = -1;
int relay = 5;
float intervalEnergy = 0;
float avgIrms = 0;
float avgVrms = 0;
float avgPF = 0;
float avgPower = 0;
float message = 0;
void callback(char *topic, byte *payload, unsigned int length)
{
  String strData;
  String topicStr(topic);
  for (int i = 0; i < length; i++)
  {
    strData += (char)payload[i]; //convert payload to string
  }
  //topic and payload become string
  //if topic is Limit then update limit value and print it
  DynamicJsonDocument doc(1024);
  if (topicStr == "Limit")
  {
    limit = strData.toInt();
    doc["limit"] = limit;
    Serial.println(limit);
  }
  //if topic is State then turn off or on the relay
  if (topicStr == "State")
  {
    int state = strData.toInt();
    if (state == 1)
    { //turn on
      Serial.println("on");
      doc["state"] = 1;
    }
    else
    { //trun off
      Serial.println("off");
      doc["state"] = 0;
    }
  }
  //NEW
  //this topic to reset the energy
  if (topicStr == "Clear")
  {
    Serial.println("Clear");
    int energy = strData.toInt();
    doc["energy"] = energy;
  }
  serializeJson(doc, s);
  //add set new power limit sub
}

void push(char topic[])
{
  String str = String(intervalEnergy, 2) + "," + String(avgPower, 2) + "," + String(avgIrms, 2) + "," + String(avgVrms, 2) + "," + String(avgPF, 2);
  char buffer[50];
  str.toCharArray(buffer, 50);
  client.publish(topic, buffer);
  intervalEnergy = avgIrms = avgVrms = avgPower = avgPF = 0.0;
}

void loop()
{
  if (!client.connected())
  {
    reconnect();
  }
  client.loop();
  if (s.available() > 0)
  {
    DynamicJsonDocument doc(1024);
    DeserializationError error = deserializeJson(doc, s);
    if (error)
      return;
    if (doc.containsKey("intervalEnergy"))
    {
      intervalEnergy = doc["intervalEnergy"];
      avgPower = doc["avgPower"];
      avgIrms = doc["avgIrms"];
      avgVrms = doc["avgVrms"];
      avgPF = doc["avgPF"];
      push("Energy");
    }
    else if (doc.containsKey("Message"))
    {
      message = doc["Message"];
      String str = String(message, 2);
      char buffer[50];
      client.publish("Message", buffer);
    }
    Serial.println("data_recevd");
  }
}
