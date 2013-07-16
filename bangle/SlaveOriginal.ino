/*
BluetoothShield Demo Code Slave.pde. This sketch could be used with
Master.pde to establish connection between two Arduino. It can also
be used for one slave bluetooth connected by the device(PC/Smart Phone)
with bluetooth function.
2011 Copyright (c) Seeed Technology Inc.  All right reserved.
 
Author: Steve Chang
 
This demo code is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.
 
This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.
 
You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 
For more details about the product please check http://www.seeedstudio.com/depot/
 
*/
 
 
/* Upload this sketch into Seeeduino and press reset*/
 
#include <SoftwareSerial.h>   //Software Serial Port
#define RxD 6
#define TxD 7
#define BT_STAT  13
 
SoftwareSerial blueToothSerial(RxD,TxD);
 
void setup() 
{ 
  Serial.begin(9600);
  pinMode(RxD, INPUT);
  pinMode(BT_STAT, INPUT);
  pinMode(TxD, OUTPUT);
  setupBlueToothConnection(); 
} 

void alert()
{
    Serial.println("ALERT!");
}
 
void loop() 
{ 
    char recvChar;
    int count = 0;
    int isInitialized = 1;
    while(1){
        int bt_stat = digitalRead(BT_STAT);
        Serial.print("BT Status:");
        Serial.println(bt_stat);
        
        if (1 == bt_stat) {
          isInitialized = 0;
        }
        else {
          // disconnected
          if (0 == isInitialized) {
            setupBlueToothConnection();
            isInitialized = 1;
            Serial.println("Setup Connection");
          }
        }
        
        if(blueToothSerial.available()){//check if there's any data sent from the remote bluetooth shield
            recvChar = blueToothSerial.read();
            Serial.println(recvChar);
        }

        delay(1000);
    }
} 
 
void setupBlueToothConnection()
{
  blueToothSerial.begin(38400); //Set BluetoothBee BaudRate to default baud rate 38400
  blueToothSerial.print("\r\n+STWMOD=0\r\n"); //set the bluetooth work in slave mode
  blueToothSerial.print("\r\n+STNA=ViBTSlave\r\n"); //set the bluetooth name as "SeeedBTSlave"
  blueToothSerial.print("\r\n+STPIN=0000\r\n");//Set SLAVE pincode"0000"
  blueToothSerial.print("\r\n+STOAUT=1\r\n"); // Permit Paired device to connect me
  blueToothSerial.print("\r\n+STAUTO=0\r\n"); // Auto-connection should be forbidden here
  delay(2000); // This delay is required.
  blueToothSerial.print("\r\n+INQ=1\r\n"); //make the slave bluetooth inquirable 
  Serial.println("The slave bluetooth is inquirable!");
  delay(2000); // This delay is required.
  blueToothSerial.flush();
}



