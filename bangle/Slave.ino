/* Upload this sketch into Seeeduino and press reset*/

#include <SoftwareSerial.h>   //Software Serial Port
#define RxD 6
#define TxD 7
#define LED 9
#define BT_STAT 11

SoftwareSerial blueToothSerial(RxD,TxD);

void setup() 
{ 
    Serial.begin(9600);

    pinMode(RxD, INPUT);
    pinMode(TxD, OUTPUT);
    pinMode(LED, OUTPUT);
    pinMode(BT_STAT, INPUT);

    setupBlueToothConnection(); 
} 

void got_ping()
{
    char recvChar = blueToothSerial.read();

    Serial.print("got ping: ");
    Serial.println(recvChar);

    digitalWrite(LED, HIGH);
    delay(300);
    digitalWrite(LED, LOW);
}

void loop() 
{ 
    char recvChar;
    boolean need_reset = false;

    while(1) {

        if(blueToothSerial.available()){//check if there's any data sent from the remote bluetooth shield
            got_ping();
        }

        if (get_bt_status() == 0) {
            // not connected
            if (need_reset) {
                // disconnected
                setupBlueToothConnection();
                need_reset = false;
            }
        }
        else {
            // connected
            need_reset = true;
        }

        delay(800);
    }
} 

int get_bt_status()
{
    int stat = digitalRead(BT_STAT);
    Serial.print("status: ");
    Serial.println(stat);
    return stat;
}

void setupBlueToothConnection()
{
    Serial.println("setupBlueToothConnection");

    blueToothSerial.begin(38400); //Set BluetoothBee BaudRate to default baud rate 38400
    blueToothSerial.print("\r\n+STWMOD=0\r\n"); //set the bluetooth work in slave mode
    blueToothSerial.print("\r\n+STNA=SeeedBTSlave\r\n"); //set the bluetooth name as "SeeedBTSlave"
    blueToothSerial.print("\r\n+STPIN=0000\r\n");//Set SLAVE pincode"0000"
    blueToothSerial.print("\r\n+STOAUT=1\r\n"); // Permit Paired device to connect me
    blueToothSerial.print("\r\n+STAUTO=0\r\n"); // Auto-connection should be forbidden here
    delay(2000); // This delay is required.
    blueToothSerial.print("\r\n+INQ=1\r\n"); //make the slave bluetooth inquirable 
    Serial.println("The slave bluetooth is inquirable!");
    delay(2000); // This delay is required.
    blueToothSerial.flush();
}
