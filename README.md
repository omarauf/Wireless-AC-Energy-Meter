# Wireless AC Energy Meter

it's a device which measures the energy, power, current, voltage and uploads these data to a database to be shown on android app. As well as it can control the usage of electricity by setting a limit to the device will cut the electric when the energy exceeds the limit. Moreover, the user can control the device automatically by setting a start and end time for each day or manually by click on start/end button on the android app.

The device consist of three parts:
  - Arduino
  - Server
  - Android
  
# Device
The device consists of a few main parts NodeMCU, Arduino nano, boost converter, current sensor and voltage transformer. the device using NodeMCU because of wifi features the NodeMCU will be connected to Arduino nano for measuring the voltage by downscaling the 220 volt to 9 volt using AC Transformer since 220 volts is dangerous for Arduino and in the same time we are using that 9 volt AC signal and converted to DC signal to feed the system with power by converting the AC to DC signal using a bridge rectifier and capacitor to smooth the signal moreover we are measuring the current using electromagnetic technique by converting the electromagnetic that is an induced by The Wire to voltage can be read by Arduino there is also OLED display to show the real-time data
![](https://raw.githubusercontent.com/omarauf/Wireless-AC-Energy-Meter/master/Schematic/Circuit.png)
For Designing the box using inventor program to design it and print it as a 3D printer but unfortunately, our first print was too small so we design extension 5cm so call the part can fit inside the Box will use plastic since it's a non-conductive material
![](https://raw.githubusercontent.com/omarauf/Wireless-AC-Energy-Meter/master/Schematic/Design.PNG)


For communicating between the Arduino Nano and the NodeMCU we are using the serial connection between them the Arduino Nano no will calculate the summation of energy and the average of current, voltage and power and send it to the NodeMCU every 5 minutes by JSON format meanwhile the data will send to the server using MQTT protocol so the NodeMCU will act as mediator between nano and the server
*MQTT* Port protocol consists of a broker which called mosquito and a lot of clients each client are connected to the broker with at least one topic
*Node-RED* is a flow-based development tool for visual programming developed originally by IBM for wiring together hardware devices, APIs and online services as part of the Internet of Things.
The NodeMCU is connected to the broker with a lot of topics one of these topics is data and in the same time the NodeRED are connected to the same topic which results in receiving the data from the NodeMCU and put it in the database each 5 minutes
![](https://raw.githubusercontent.com/omarauf/Wireless-AC-Energy-Meter/master/Schematic/Communication%20system..PNG)

To sum up the device are calculating the energy and real power using voltage transformer and current sensor after it sending the data to NodeMCU every 5 minutes and then the NodeMCU will send the data to NodeRED which will save it in database and data would be stored in the database so other mates can use it in their Android app

# Server
data are coming to the server every 5 minutes so these data will be saved in the data table which acts as a buffer at the end of the day there are come SQL script which will work and take the average of the power, current and voltage and the summation of the energy and save it in the day.
![](https://raw.githubusercontent.com/omarauf/Wireless-AC-Energy-Meter/master/Schematic/DataBase.png?)
In the same time, these data also will be saved in another table called period table which consists of six hours in the day there are four periods in a day.
After that, the data table will be deleted so it can be used for the next day. after that the weeks and months table are recalculated
the Node-RED also control the timing for turning on/off the device it can be manually so the user turn it off or on by button on android app or automatically by setting a start and end time for each day
![](https://raw.githubusercontent.com/omarauf/Wireless-AC-Energy-Meter/master/Schematic/NodeRED.jpg?)

# Android
the Android app fetches the data from the server whenever it starts it consists of two parts dashboard statistics the dashboard is for controlling the device using NodeRED it's setting the device time schedule if it's automatically or controlling it by start/stop button if it's manually the user can change the limit or reset the energy of the device
statistics part show the user the amount of electric usage for each day and the user can select a specific day to show periods for that day down there is a circle chart for the month the user can change the data type to average voltage, current and power
when the device exceeded the limit an email will be sent to the user and a pop up will ask him to change the limit
![](https://raw.githubusercontent.com/omarauf/Wireless-AC-Energy-Meter/master/Schematic/Android%202.jpg?)

### Tech
Wireless AC Energy Meter app uses a number of open source projects to work properly:

* [EmonLib] - Arduino Energy Monitoring Library.
* [MPAndroidChart] - A powerful & easy to use chart library for Android.
* [PHP] - Hypertext Preprocessor is a server-side scripting language designed for Web development.
* [MySQL] - MySQL is an open source relational database management system.
* [NodeRED] - Login and Registration with PHP, MySQL.
* [Volley] - Volley is an HTTP library that makes networking for Android apps easier and most importantly, faster.

# CONCLUSION
The device will allow the users to control their uses of electricity by providing a limit for how much energy will they use so the device will warn the users by email in case the limit has been exceeded. Moreover, the usage of electricity will be saved in the database as well it will be shown in the graph so the user will be more aware about how much he/she uses in the last month, week or day. the user will have the abilty to turn on/off the device over the internet


   [EmonLib]: <https://github.com/openenergymonitor/EmonLib/>
   [MPAndroidChart]: <https://github.com/PhilJay/MPAndroidChart/>
   [php]: <http://twitter.com/tjholowaychuk>
   [mysql]: <https://www.mysql.com/>
   [NodeRED]: <Flow-based programming for the Internet of Things/>
   [Volley]: <https://developer.android.com/training/volley/>
   
