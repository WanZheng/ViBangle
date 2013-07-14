/* Upload this sketch into Seeeduino and press reset*/
#include <SoftwareSerial.h>   //Software Serial Port
#include <stdio.h>

#define RxD 6
#define TxD 7

#define BT_STAT 13

String retSymb = "+RTINQ=";//start symble when there's any return
String slaveName = ";ViBTSlave";//Set the Slave name ,caution that ';'must be included
int nameIndex = 0;
int addrIndex = 0;

String recvBuf;
String slaveAddr;

String connectCmd = "\r\n+CONN=";

SoftwareSerial blueToothSerial(RxD,TxD);

/*
 * Header of LED
 */
const int COLOR_RED = (1 << 0);
const int COLOR_GREEN = (1 << 1);
const int COLOR_BLUE = (1 << 2);
void led_setup();
void led_set_color(int color);

/*
 * Header of MOTOR
 */
const int MOTOR_PIN = 8;
void motor_setup();
void motor_setOnOff(int onOff);

/*
 * Header of BEEP
 */
const int BEEP_PIN = 12;
void beep_setup();
void beep_setOnOff(int onOff);

void setup() 
{ 
    Serial.begin(9600);

    pinMode(RxD, INPUT);
    pinMode(TxD, OUTPUT);
    pinMode(BT_STAT, INPUT);

    led_setup();
    motor_setup();
    beep_setup();

    connecting();
    resetBluetooth();
} 

void loop() 
{
    int bt_setuped = 1;

    while(1) {
        ping();
        if (get_bt_status() == 0) {
            alert();
            if (bt_setuped == 0) {
                resetBluetooth();
                bt_setuped = 1;
            }
        }
        else {
            bt_setuped = 0;
        }

        delay(1000);
    } 
} 

int get_bt_status()
{
    int stat = digitalRead(BT_STAT);
    Serial.print("status: ");
    Serial.println(stat);
    return stat;
}

void ping()
{
    Serial.println("ping!");
    blueToothSerial.print("a");

    led_set_color(COLOR_GREEN);
    beep_setOnOff(0);
    motor_setOnOff(0);
}

void alert()
{
    Serial.println("alert!");

    led_set_color(COLOR_RED);
    beep_setOnOff(1);
    motor_setOnOff(1);
}

void connecting()
{
    Serial.println("connecting!");

    led_set_color(COLOR_BLUE);
    beep_setOnOff(0);
    motor_setOnOff(0);
}

void resetBluetooth()
{
    Serial.println("resetBluetooth!");

    setupBlueToothConnection();
    //wait 1s and flush the serial buffer
    delay(1000);
    Serial.flush();
    blueToothSerial.flush();
}

void setupBlueToothConnection()
{
    Serial.println("setupBlueToothConnection!");

    blueToothSerial.begin(38400); //Set BluetoothBee BaudRate to default baud rate 38400
    blueToothSerial.print("\r\n+STWMOD=1\r\n");//set the bluetooth work in master mode
    blueToothSerial.print("\r\n+STNA=ViBTMaster\r\n");//set the bluetooth name as "ViBTMaster"
    blueToothSerial.print("\r\n+STPIN=0000\r\n");//Set Master pincode"0000",it must be same as Slave pincode
    blueToothSerial.print("\r\n+STAUTO=0\r\n");// Auto-connection is forbidden here

    delay(2000); // This delay is required.

    blueToothSerial.flush();
    blueToothSerial.print("\r\n+INQ=1\r\n");//make the master inquire
    Serial.println("Master is inquiring!");

    delay(2000); // This delay is required.

    //find the target slave
    char recvChar;
    while (1) {
        if(blueToothSerial.available()) {
            recvChar = blueToothSerial.read();
            recvBuf += recvChar;
            nameIndex = recvBuf.indexOf(slaveName);//get the position of slave name
            //nameIndex -= 1;//decrease the ';' in front of the slave name, to get the position of the end of the slave address
            if ( nameIndex != -1 ){
                //Serial.print(recvBuf);
                addrIndex = (recvBuf.indexOf(retSymb,(nameIndex - retSymb.length()- 18) ) + retSymb.length());//get the start position of slave address	 		
                slaveAddr = recvBuf.substring(addrIndex, nameIndex);//get the string of slave address 			
                break;
            }
        }
    }

    Serial.println("Slave found!");

    connecting();

    //form the full connection command
    connectCmd += slaveAddr;
    connectCmd += "\r\n";
    int connectOK = 0;
    Serial.print("Connecting to slave:");
    Serial.print(slaveAddr);
    Serial.println(slaveName);
    //connecting the slave till they are connected
    do {
        blueToothSerial.print(connectCmd);//send connection command
        recvBuf = "";
        while(1) {
            if(blueToothSerial.available()) {
                recvChar = blueToothSerial.read();
                recvBuf += recvChar;
                if(recvBuf.indexOf("CONNECT:OK") != -1){
                    connectOK = 1;
                    Serial.println("Connected!");
                    blueToothSerial.print("Connected!");
                    break;
                }else if(recvBuf.indexOf("CONNECT:FAIL") != -1){
                    Serial.println("Connect again!");
                    break;
                }
            }
        }
    } while (0 == connectOK);
}

// ####################################################

/*
 * Implement of LED
 */
// LED leads connected to PWM pins
const int RED_LED_PIN = 9;
const int GREEN_LED_PIN = 10;
const int BLUE_LED_PIN = 11;

// Used to store the current intensity level of the individual LEDs
int redIntensity = 0;
int greenIntensity = 0;
int blueIntensity = 0;

void led_setup() {
    pinMode(RED_LED_PIN, OUTPUT);
    pinMode(GREEN_LED_PIN, OUTPUT);
    pinMode(BLUE_LED_PIN, OUTPUT);
    led_set_color(0);
}

void led_set_color(int color) {
    int redIntensity = 255;
    int greenIntensity = 255;
    int blueIntensity = 255;

    if (color & COLOR_RED) {
        redIntensity = 0;
    }
    if (color & COLOR_GREEN) {
        greenIntensity = 0;
    }
    if (color & COLOR_BLUE) {
        blueIntensity = 0;
    }

    analogWrite(RED_LED_PIN, redIntensity);
    analogWrite(GREEN_LED_PIN, greenIntensity);
    analogWrite(BLUE_LED_PIN, blueIntensity);
}

/*
 * Implement of motor
 */
void motor_setup()
{
    pinMode(MOTOR_PIN, OUTPUT); 
    motor_setOnOff(0);
}

void motor_setOnOff(int onOff)
{
    digitalWrite(MOTOR_PIN, onOff ? HIGH : LOW); // turns the motor On
}

/*
 * Implement of BEEP
 */
void beep_setup()
{
    pinMode(BEEP_PIN, OUTPUT); 
    beep_setOnOff(0);
}

void beep_setOnOff(int onOff)
{
    digitalWrite(BEEP_PIN, onOff ? HIGH : LOW); // turns the motor On
}

