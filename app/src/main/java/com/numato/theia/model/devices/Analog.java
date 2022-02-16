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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Analog {

    NumatoUSBDevice device;

    int number;

    private NumatoEthernetDevice ethernetDevice;

    private NumatoBluetoothDevice bluetoothDevice;

    public Analog(NumatoUSBDevice device, int number){
        this.device = device;
        this.number = number;
    }
    public Analog(NumatoEthernetDevice ethernetDevice, int number){
        this.ethernetDevice = ethernetDevice;
        this.number = number;
    }

    public Analog(NumatoBluetoothDevice bluetoothDevice, int number){
        this.bluetoothDevice = bluetoothDevice;
        this.number = number;
    }

    public int getAnalog(){

        byte[] responseBuffer = new byte[64];
        int tmp = 0;

        if(device != null) {
            String cmdRelayRead;
            if(device.getProductId() == 0X804 && this.number <= 9)
            {
                cmdRelayRead = "adc read 0" + Integer.toString(this.number) + "\r";
            }
            else
                cmdRelayRead = "adc read " + Integer.toString(this.number) + "\r";
            tmp = device.sendAndReceive(cmdRelayRead.getBytes(), cmdRelayRead.length(), responseBuffer, 32);
        }
        else if(ethernetDevice != null){
            String cmdRelayRead = "adc read " + Integer.toString(this.number) + "\r\n";
            tmp = ethernetDevice.sendAndReceive(cmdRelayRead.getBytes(), cmdRelayRead.length(), responseBuffer, 32);
            return tmp;
        }
        else
        {
            String cmdRelayRead = "adc read " + Integer.toString(this.number) + "\r";
            tmp = bluetoothDevice.receiveADC(cmdRelayRead.getBytes(), cmdRelayRead.length(), responseBuffer, 32);
            return tmp;
        }

        if(tmp < 14){
            String responseData = new String(responseBuffer, 0, 32);

            System.out.println(responseData);
            String version = responseData;

            Pattern rx = (Pattern.compile("([0-9]*)"));
            if (responseData != null) {
                responseData = responseData.trim();
            } else {
                responseData = "";
            }
            Matcher m = rx.matcher(responseData);

            if (m.find()) {
                responseData = m.group(0).trim();
            }
            if (responseData.isEmpty()) {
                version = version.replaceAll("[^\\d]", "");
                rx = (Pattern.compile("([0-9]*)"));
                m = rx.matcher(version.trim());

                if (m.find()) {
                    responseData = m.group(0).trim();
                }
            }
            return Integer.parseInt(responseData);
        }else{
            String response = new String(responseBuffer).substring(12,16);
            response = response.replaceAll("[^\\d]", "");
            return Integer.parseInt(response);
        }
    }

    public int getNumber() {
        return number;
    }
}
