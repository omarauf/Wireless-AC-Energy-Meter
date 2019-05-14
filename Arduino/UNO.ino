#include <SoftwareSerial.h>
#include <ArduinoJson.h>
#include <Adafruit_SSD1306.h>
#include <Adafruit_GFX.h>
#include "EmonLib.h" // Include Emon Library
EnergyMonitor emon1; // Create an instance

#define OLED_RESET 4
Adafruit_SSD1306 display(OLED_RESET);
SoftwareSerial s(5, 6); // (Rx, Tx)

//Variables
const int relayPin = 5; // the number of the pushbutton pin

unsigned long pushPreviousMillis = 0;
unsigned long pushInterval = 5.0 * 60.0 * 1000; //the interval to push power & current to mqtt server 5 min

unsigned long lastTime = 0;
unsigned long currentTime = 0;

float Vcc = 5.0;    // ADC reference voltage // voltage at 5V pin
float limit = -1;   // -1: unlimited
int stateOnOff = 1; // 1: on, 0: off

float energy = 0;
float intervalEnergy = 0;
float avgPower = 0;
float avgIrms = 0;
float avgVrms = 0;
float avgPF = 0;

//float currentCalibration = 47;
//float voltageCalibration = 243;
float realPower;     //extract Real Power into variable
float apparentPower; //extract Apparent Power into variable
float powerFActor;   //extract Power Factor into Variable
float supplyVoltage; //extract Vrms into Variable
float Irms;          //extract Irms into Variable

int flag = 4;
bool checkFlag()
{
  if (energy >= limit && limit != -1 && flag != 0)
  {
    flag = 0;
    Serial.println("message shulud be send");
    sendMessage();
    digitalWrite(12, LOW);
    return false;
  }
  if (stateOnOff == 1 && (energy <= limit || limit == -1) && flag != 1)
  {
    flag = 1;
    Serial.print("on the limit ");
    Serial.println(limit);
    digitalWrite(12, HIGH);
    return true;
  }
  else if (stateOnOff == 0 && (energy <= limit || limit == -1) && flag != 2)
  {
    flag = 2;
    Serial.print("off the limit ");
    Serial.println(limit);
    pushPreviousMillis = 0;
    digitalWrite(12, LOW);
    return false;
  }
}

void sendMessage()
{
  StaticJsonDocument<256> doc;
  doc["Message"] = energy;
  serializeJson(doc, s);
}

void calculatePower()
{
  emon1.calcVI(20, 2000); // Calculate all. No.of half wavelengths (crossings), time-out
  emon1.serialprint();    // Print out all variables (realpower, apparent power, Vrms, Irms, power factor)
  realPower = emon1.realPower;         //extract Real Power into variable
  apparentPower = emon1.apparentPower; //extract Apparent Power into variable
  powerFActor = emon1.powerFactor;     //extract Power Factor into Variable
  supplyVoltage = emon1.Vrms;          //extract Vrms into Variable
  Irms = emon1.Irms;                   //extract Irms into Variable

  if ((Irms > -0.015) && (Irms < 0.10)) // remove low power and current
    realPower = apparentPower = powerFActor = Irms = 0.0;
  else
  { //avg for run time only
    avgPower = (avgPower + realPower) / 2;
    avgIrms = (avgIrms + Irms) / 2;
    avgPF = (avgPF + powerFActor) / 2;
  }
  avgVrms = (avgVrms + supplyVoltage) / 2; //there is alawys voltage
  lastTime = currentTime;
  currentTime = millis();
  energy = energy + realPower * ((currentTime - lastTime) / 3600000.0);                 // calculating energy in Watt-Hour
  intervalEnergy = intervalEnergy + realPower * ((currentTime - lastTime) / 3600000.0); // calculating energy in Watt-Hour
  //Serial.println("power calucatede");
  //Serial.println(energy);

  displaydata();
}

void displaydata()
{
  display.clearDisplay();
  display.setTextColor(WHITE);
  display.setTextSize(1);
  display.setCursor(0, 0);
  display.println(supplyVoltage);
  display.setCursor(35, 0);
  display.println("V");
  //-------------------
  display.setCursor(65, 0);
  display.println(Irms);
  display.setCursor(95, 0);
  display.println("A");
  //------------------
  display.setCursor(0, 10);
  display.println(realPower);
  display.setCursor(35, 10);
  display.println("W");
  //------------------
  display.setCursor(65, 10);
  display.println(powerFActor);
  display.setCursor(95, 10);
  display.println("pf");
  //------------------
  display.setCursor(0, 20);
  display.println(energy);
  display.setCursor(35, 20);
  display.println("Wh");
  //------------------
  display.setCursor(65, 20);
  display.println(intervalEnergy);
  display.setCursor(95, 20);
  display.println("Wh");
  //------------------
  display.display();
}

void sendValue()
{
  unsigned long currentMillis = millis();
  if (currentMillis - pushPreviousMillis >= pushInterval)
  {
    pushPreviousMillis = currentMillis;
    StaticJsonDocument<256> doc;
    doc["intervalEnergy"] = intervalEnergy;
    doc["avgPower"] = avgPower;
    doc["avgIrms"] = avgIrms;
    doc["avgVrms"] = avgVrms;
    doc["avgPF"] = avgPF;
    serializeJson(doc, s);
    intervalEnergy = avgIrms = avgPower = avgVrms = avgPF = 0;
  }
}

void setup()
{
  Serial.begin(9600);
  s.begin(9600);
  pinMode(12, OUTPUT);
  display.begin(SSD1306_SWITCHCAPVCC, 0x3C);
  //emon1.voltage(2, 243, 1.7); // Voltage: input pin, calibration, phase_shift
  emon1.voltage(2, 95, 1.7); // Voltage: input pin, calibration, phase_shift
  emon1.current(1, 47);       // Current: input pin, calibration.
}

void loop()
{
  if (checkFlag())
  {
    //Serial.print("enter : ");
    //Serial.println(energy);
    calculatePower();
    sendValue();
  }
  if (s.available() > 0)
  {
    StaticJsonDocument<256> doc;
    DeserializationError error = deserializeJson(doc, s);
    if (error)
      return;
    if (doc.containsKey("limit"))
    {
      limit = doc["limit"];
      Serial.print("limit: ");
      Serial.println(limit);
    }
    if (doc.containsKey("state"))
    {
      stateOnOff = doc["state"];
      //Serial.print("stateOnOff: ");
      //Serial.println(stateOnOff);
    }
    //NEW to resty the energy
    if (doc.containsKey("energy"))
    {
      energy = doc["energy"];
      //Serial.print("energy: ");
      //Serial.println(energy);
    }
  }
}
