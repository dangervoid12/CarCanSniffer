package com.dangervoid;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.PrintStream;
import java.util.Enumeration;

public class SerialContr implements SerialPortEventListener{
        MainWindow mainWindow;
        SerialPort serialPort;
        String devName = "";

        private BufferedReader input;
        private PrintStream output;
        private static final int TIME_OUT = 2000;
        private static final int DATA_RATE = 115200;//9600;

        public String initialize() {
            String res = "";
            String PORT_NAMES[] = {
                    "/dev/tty.usbserial-A9007UX1", // Mac OS X
                    //"/dev/ttyACM0", // Raspberry Pi gps
                    "/dev/ttyUSB0", // Linux
                    devName,
                    "COM1", // Windows
            };
            CommPortIdentifier portId = null;
            Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

            while (portEnum.hasMoreElements()) {
                CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
                for (String portName : PORT_NAMES) {
                    if (currPortId.getName().equals(portName)) {
                        portId = currPortId;
                        break;
                    }
                }
            }
            if (portId == null) {
                System.out.println("Could not find port");
                res = "Could not find port...";
                return res;
            }

            try {
                serialPort = (SerialPort) portId.open(this.getClass().getName(),
                        TIME_OUT);
                serialPort.setSerialPortParams(DATA_RATE,
                        SerialPort.DATABITS_8,
                        SerialPort.STOPBITS_1,
                        SerialPort.PARITY_NONE);

                input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
                output = new PrintStream(serialPort.getOutputStream(), true);

                serialPort.addEventListener(this);
                serialPort.notifyOnDataAvailable(true);
                res = "Connected";
            } catch (Exception e) {
                res = "Couldn't connect"; //e.toString();
                System.err.println(e.toString());
            }
            return res;
        }

        public void sendData(String newData){
            if(output != null){
                output.print(newData);
                output.print("\r\n");
            }
        }

        public synchronized void close() {
            if (serialPort != null) {
                serialPort.removeEventListener();
                serialPort.close();
            }
        }


        public synchronized void serialEvent(SerialPortEvent oEvent) {
            if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
                try {
                    String inputLine=input.readLine();
                    //System.out.println("Recieved: " + inputLine);
                   mainWindow.addNewRow(MyCanPacket.returnPacketFromComplexData(inputLine));
                } catch (Exception e) {
                    System.err.println(e.toString());
                }
            }

        }


        public SerialContr(MainWindow mainWindow, String devName){
            this.mainWindow = mainWindow;
            this.devName = devName;
            //initialize(); //moved to MainWindow
        }


}
