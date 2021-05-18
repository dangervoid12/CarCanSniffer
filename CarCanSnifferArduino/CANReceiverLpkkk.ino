// Copyright (c) DangerVoid. All rights reserved.
// For any more info contact author directly

#include <CAN.h>

void setup() {
  Serial.begin(115200);
  while (!Serial);

  Serial.println("CAN Receiver");

  // start the CAN bus at 500 kbps
  if (!CAN.begin(500E3)) {
    Serial.println("Starting CAN failed!");
    while (1);
  }
}

String getValue(String data, char separator, int index)
{
    int maxIndex = data.length() - 1;
    int j = 0;
    String chunkVal = "";

    for (int i = 0; i <= maxIndex && j <= index; i++)
    {
        chunkVal.concat(data[i]);

        if (data[i] == separator)
        {
            j++;

            if (j > index)
            {
                chunkVal.trim();
                return chunkVal;
            }

            chunkVal = "";
        }
        else if ((i == maxIndex) && (j < index)) {
            chunkVal = "";
            return chunkVal;
        }
    }   
}


void sendPacketToCan(String pId, String pRtr, String pSize, String pData) {
    
  for (int retries = 10; retries > 0; retries--) {
    if(!pRtr.equals("0")){
      CAN.beginExtendedPacket(pId.toInt(), pRtr.toInt(),true);
    }else{
      CAN.beginPacket(pId.toInt(), 0, false);    
    }
    byte dataBytes[pData.length()];
    pData.getBytes(dataBytes, pData.length());
    byte rtrBytes[pRtr.length()];
    pRtr.getBytes(rtrBytes, pRtr.length());
    CAN.write(dataBytes, pRtr.toInt());
    if (CAN.endPacket()) {
      // success
      break;
    } else if (retries <= 1) {
      return;
    }
  }
}

void canEvt(){
  int packetSize = CAN.parsePacket();
  if (packetSize) {
    // received a packet
    //Serial.print("Received ");
    if (CAN.packetExtended()) {
      //Serial.print("extended ");
    }

    if (CAN.packetRtr()) {
      // Remote transmission request, packet contains no data
      //Serial.print("RTR ");
    }

    //Serial.print("packet with id 0x");
    Serial.print(CAN.packetId(), HEX);
    Serial.print(",");
    if (CAN.packetRtr()) {
      //Serial.print(" and requested length ");
      Serial.println(CAN.packetDlc());
      Serial.print(",");
      Serial.print(packetSize);
      Serial.print(",");
    } else {
      Serial.print("0");
      Serial.print(",");
      Serial.print(packetSize);
      Serial.print(",");
      //Serial.print(" Data: ");
      // only print packet data for non-RTR packets
      while (CAN.available()) {
        Serial.print(CAN.read(), HEX);
      }
      Serial.println();
    }

    Serial.println();
  }
}

void serialEvt(){
  
  String readStr = Serial.readString();
  if(!readStr.equals("")){
    //Serial.println("Recieved from serial: " + readStr);
    int i = 0;
    while(i < getValue(readStr,',',0)){
      sendPacketToCan(getValue(readStr,',',1), getValue(readStr,',',2), getValue(readStr,',',3), getValue(readStr,',',4));
      i++;
    }
  }
}

 // id,rtr,packSize,data
void loop() {
  canEvt();
  serialEvt();
}
