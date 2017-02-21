#include <SoftwareSerial.h>
SoftwareSerial BT1(4,2); // RX, TX recorder que se cruzan


char command;
String string;
boolean ledon = false;
#define led 13

  void setup()
  {
    Serial.begin(9600);
    BT1.begin(9600);
    pinMode(led, OUTPUT);
  }

  void loop()
  {
    if (BT1.available() > 0) 
    {string = "";}
    
    while(BT1.available() > 0)
    {
      command = ((byte)BT1.read());
      if(command == ':')
      {
        break;
      }
      
      else
      {
        string += command;
      }
      
      delay(1);
    }
      Serial.println(command);
    
    if(string == "TO")
    {
        ledOn();
        ledon = true;
    }
    
    if(string =="TF")
    {
        ledOff();
        ledon = false;
        Serial.println(string);
    }
    
    if ((string.toInt()>=0)&&(string.toInt()<=255))
    {
      if (ledon==true)
      {
     //   analogWrite(led, string.toInt());
        BT1.println(string);
        delay(10);
      }
    }
 }
 
void ledOn()
   {
      digitalWrite(led,HIGH);
      //analogWrite(led, 255);
      delay(10);
    }
 
 void ledOff()
 {
  
      digitalWrite(led,LOW);
      //analogWrite(led, 0);
      delay(10);
 }
 

    
