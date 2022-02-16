/*
Copyright [2015] [Numato Systems Private Limited]

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package com.numato.theia.model.devices;


import java.io.UnsupportedEncodingException;

public class Relay {

    private NumatoUSBDevice device;

    private NumatoEthernetDevice ethernetDevice;

    private NumatoBluetoothDevice bluetoothDevice;

    private int number;

    public Relay(NumatoUSBDevice device, int number){
        this.device = device;
        this.number = number;
    }

    public Relay(NumatoEthernetDevice ethernetDevice, int number){
        this.ethernetDevice = ethernetDevice;
        this.number = number;
    }

    public Relay(NumatoBluetoothDevice bluetoothDevice, int number){
        this.bluetoothDevice = bluetoothDevice;
        this.number = number;
    }

    public boolean getState() {
        int tmp = 0;
        byte[] responseBuffer = new byte[64];
        String cmdRelayRead= null;

        if(device != null) {
            if(this.number <= 9) {
                cmdRelayRead = "relay read " + Integer.toString(this.number) + "\r";
            }
            else {
                cmdRelayRead = "relay read " + String.valueOf( Character.toChars(this.number + 0x37) ) + "\r";
            }
            tmp = device.sendAndReceive(cmdRelayRead.getBytes(), cmdRelayRead.length(), responseBuffer, 32);
            if(tmp < 15){
                return false;
            }else{
                String response = new String(responseBuffer);
                if(response.contains("on")){
                    return true;
                }else
                {
                    return false;
                }
            }
        }
        else if(ethernetDevice != null){
            try {
                if(this.number <= 9) {
                    cmdRelayRead = "relay read " + Integer.toString(this.number) + "\r\n";
                }
                else {
                    cmdRelayRead = "relay read " + String.valueOf( Character.toChars(this.number + 0x37) ) + "\r\n";
                }

                responseBuffer = cmdRelayRead.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String value = ethernetDevice.sendAndReceiveRelay(responseBuffer, cmdRelayRead.length(), responseBuffer, 32);
            if(value.contains("on")){
                return true;
            }else
            {
                return false;
            }
        }
        else if(bluetoothDevice != null)
        {
            try {
                if(this.number <= 9) {
                    cmdRelayRead = "relay read " + Integer.toString(this.number) + "\r";
                }
                else {
                    cmdRelayRead = "relay read " + String.valueOf( Character.toChars(this.number + 0x37) ) + "\r";
                }

                responseBuffer = cmdRelayRead.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            String value = bluetoothDevice.readRelay(responseBuffer, cmdRelayRead.length(), responseBuffer, 32);
            if(value.contains("on")){
                return true;
            }else
            {
                return false;
            }
        }
        return false;
    }

    public void setState(boolean state) {
        String cmdRelayState = null;
        byte[] responseBuffer = new byte[256];
        int serialNumber = this.number;
        if(device != null) {
            if(state == true){
                cmdRelayState = "relay on " + this.number + "\r";
            }else{
                cmdRelayState = "relay off " + this.number + "\r";
            }

            device.sendAndReceive(cmdRelayState.getBytes(), cmdRelayState.length(), responseBuffer, 32);
        }
        else if(ethernetDevice != null){
            if(state == true){
                if (serialNumber > 9) {
                    serialNumber = (55 + serialNumber);
                    char serialNumber1 = (char) (serialNumber);

                    cmdRelayState = (new String("relay on " + serialNumber1 + "\r\n"));
                } else {
                    cmdRelayState = (new String("relay on " + serialNumber + "\r\n"));
                }
                //cmdRelayState = "relay on " + this.number + "\r\n";
            }else{
                if (serialNumber > 9) {
                    serialNumber = (55 + serialNumber);
                    char serialNumber1 = (char) (serialNumber);

                    cmdRelayState = (new String("relay off " + serialNumber1 + "\r\n"));
                } else {
                    cmdRelayState = (new String("relay off " + serialNumber + "\r\n"));
                }
                //cmdRelayState = "relay off " + this.number + "\r\n";
            }

            try {
                responseBuffer = cmdRelayState.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            ethernetDevice.sendAndReceiveRelay(responseBuffer, cmdRelayState.length(), null, 0);
        }
        else if(bluetoothDevice != null){
            if(state == true){
                if (serialNumber > 9) {
                    serialNumber = (55 + serialNumber);
                    char serialNumber1 = (char) (serialNumber);

                    cmdRelayState = (new String("relay on " + serialNumber1 + "\r"));
                } else {
                    cmdRelayState = (new String("relay on " + serialNumber + "\r"));
                }
                //cmdRelayState = "relay on " + this.number + "\r\n";
            }else{
                if (serialNumber > 9) {
                    serialNumber = (55 + serialNumber);
                    char serialNumber1 = (char) (serialNumber);

                    cmdRelayState = (new String("relay off " + serialNumber1 + "\r"));
                } else {
                    cmdRelayState = (new String("relay off " + serialNumber + "\r"));
                }
                //cmdRelayState = "relay off " + this.number + "\r\n";
            }

            try {
                responseBuffer = cmdRelayState.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            byte[] bytes = new byte[256];
            try {
                bytes = (new String(cmdRelayState + serialNumber + "\r")).getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            bluetoothDevice.sendAndReceiveRelay(bytes, cmdRelayState.length(), null, 0);
        }
    }

    public int getNumber() {
        return number;
    }
}
