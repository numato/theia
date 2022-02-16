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

import android.util.Log;

public class Gpio {

    private NumatoUSBDevice device;
    private int number;
    private boolean gpioIsInput;//1 Input, 0 Output
    private boolean currentOutputState = false;

    private NumatoEthernetDevice ethernetDevice;

    private NumatoBluetoothDevice bluetoothDevice;

    public Gpio(NumatoUSBDevice device, int number){
        this.device = device;
        this.number = number;
        gpioIsInput = true; //Input by default
    }

    public Gpio(NumatoEthernetDevice ethernetDevice, int number){
        this.ethernetDevice = ethernetDevice;
        this.number = number;
        gpioIsInput = true; //Input by default
    }

    public Gpio(NumatoBluetoothDevice bluetoothDevice, int number){
        this.bluetoothDevice = bluetoothDevice;
        this.number = number;
        gpioIsInput = true; //Input by default
    }

    public boolean getState(){
        String cmdRelayRead = null;
        byte[] responseBuffer = new byte[64];
        int tmp = 0;

        if(device != null) {
            if(device.getProductId() == 0X804 && this.number <= 9)
                cmdRelayRead = "gpio read 0" + Integer.toString(this.number) + "\r";
            if(this.number <= 9){
                cmdRelayRead = "gpio read " + Integer.toString(this.number) + "\r";
            }else{
                cmdRelayRead = "gpio read " + String.valueOf( Character.toChars(this.number + 0x37) ) + "\r";
            }

            tmp = device.sendAndReceive(cmdRelayRead.getBytes(), cmdRelayRead.length(), responseBuffer, 32);
        }
        else if(ethernetDevice != null){
            if(this.number <= 9){
                cmdRelayRead = "gpio read " + Integer.toString(this.number) + "\r\n";
            }else{
                cmdRelayRead = "gpio read " + String.valueOf( Character.toChars(this.number + 0x37) ) + "\r\n";
            }
            tmp = ethernetDevice.sendAndReceiveGPIO(cmdRelayRead.getBytes(), cmdRelayRead.length(), responseBuffer, 32);
            if(tmp == 0)
                return false;
            else
                return true;
        }
        else if(bluetoothDevice != null){
            cmdRelayRead = "gpio read " + Integer.toString(this.number) + "\r";
            tmp = bluetoothDevice.receiveGPIO(cmdRelayRead.getBytes(), cmdRelayRead.length(), responseBuffer, 32);
            if(tmp == 0)
                return false;
            else
                return true;
        }
        String response = new String(responseBuffer, 0, 30);
        if(response.contains("1")){
            return true;
        }else
        {
            return false;
        }
        /*if(tmp < 15){
            return false;
        }else{
            String response = new String(responseBuffer);
            if(response.substring(13,14).contains("1")){
                return true;
            }else
            {
                return false;
            }
        }*/
    }

    public void setState(boolean state) {
        String cmdRelayState;
        byte[] responseBuffer = new byte[64];
        if(device != null) {
            if(this.number <= 9){
                if (state == true) {
                    if(device.getProductId() == 0X804)
                        cmdRelayState = "gpio set 0" + this.number + "\r\n";
                    else
                        cmdRelayState = "gpio set " + this.number + "\r\n";
                } else {
                    if(device.getProductId() == 0X804)
                        cmdRelayState = "gpio clear 0" + this.number + "\r";
                    else
                        cmdRelayState = "gpio clear " + this.number + "\r";
                }
            }else{
                if (state == true) {
                    cmdRelayState = "gpio set " + String.valueOf(Character.toChars(this.number + 0x37)) + "\r\n";
                } else {
                    cmdRelayState = "gpio clear " + String.valueOf(Character.toChars(this.number + 0x37)) + "\r";
                }
            }
            device.sendAndReceive(cmdRelayState.getBytes(), cmdRelayState.length(), responseBuffer, 32);
        }
        else if(ethernetDevice != null){
            if(this.number <= 9){
                if (state == true) {
                    cmdRelayState = "gpio set " + this.number + "\r\n";
                } else {
                    cmdRelayState = "gpio clear " + this.number + "\r\n";
                }
            }else{
                if (state == true) {
                    cmdRelayState = "gpio set " + String.valueOf(Character.toChars(this.number + 0x37)) + "\r\n";
                } else {
                    cmdRelayState = "gpio clear " + String.valueOf(Character.toChars(this.number + 0x37)) + "\r\n";
                }
            }
            ethernetDevice.sendAndReceiveGPIO(cmdRelayState.getBytes(), cmdRelayState.length(), null, 0);
        }
        else if(bluetoothDevice != null){
            if (state == true) {
                cmdRelayState = "gpio set " + this.number + "\r";
            } else {
                cmdRelayState = "gpio clear " + this.number + "\r";
            }
            bluetoothDevice.sendGPIO(cmdRelayState.getBytes(), cmdRelayState.length(), null, 0);
        }
    }

    public int getNumber() {
        return number;
    }


    public boolean isGpioIsInput() {
        return gpioIsInput;
    }

    public void setGpioIsInput(boolean gpioIsInput) {
        this.gpioIsInput = gpioIsInput;
    }

    public void setCurrentOutputState(boolean currentOutputState) {
        this.currentOutputState = currentOutputState;
    }

    public boolean getCurrentOutputState() {
        return currentOutputState;
    }
}
