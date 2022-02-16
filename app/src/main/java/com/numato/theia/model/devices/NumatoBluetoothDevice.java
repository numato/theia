package com.numato.theia.model.devices;

import android.util.Log;

import com.numato.theia.ui.BluetoothConnectionService;

import org.apache.commons.net.telnet.TelnetClient;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumatoBluetoothDevice {
    TelnetClient tc = null;

    //public static OutputStream outstr;

    //public static InputStream intstr;

    DataInputStream ins ;
    DataOutputStream outs ;

    public static final int VID_NUMATOLAB = 0x2A19;

    /*Populate new devices here. Search for ADD_NEW_DEVICE to find other places where changes needed*/
    /* Bluetooth GPIO Devices */
    public static final String PNAME_BLUEGPIO8 = "8 Channel Bluetooth GPIO module";

    /* Bluetooth Relay Devices */
    public static final String PNAME_BLUERELAY2 = "2 Channel Bluetooth Relay Module";
    public static final String PNAME_BLUERELAY8 = "8 Channel Bluetooth Relay Module";


    public static String name;
    private int index;
    private int numberOfGpios;
    private int numberOfRelays;
    private int numberOfAnalogInputs;
    private boolean isPortOpen;

    public static ArrayList<Relay> mRelays = new ArrayList<Relay>();
    public static ArrayList<Gpio> mGpios = new ArrayList<Gpio>();
    public static ArrayList<Analog> mAnalogs = new ArrayList<Analog>();

    static BluetoothConnectionService device;

    public NumatoBluetoothDevice() {
    }

    public NumatoBluetoothDevice(BluetoothConnectionService device, String deviceName) {

        this.device = device;
        //outstr = device.getOutputStream();
        //intstr = device.getInputStream();
        setName(deviceName);
        /*Populate new devices here. Search for ADD_NEW_DEVICE to find other places where changes needed*/
        switch (deviceName) {

            case PNAME_BLUEGPIO8:
                this.name = PNAME_BLUEGPIO8;
                this.numberOfRelays = 0;
                this.numberOfGpios = 8;
                this.numberOfAnalogInputs = 6;
                break;
            case PNAME_BLUERELAY2:
                this.name = PNAME_BLUERELAY2;
                this.numberOfRelays = 2;
                this.numberOfGpios = 8;
                this.numberOfAnalogInputs = 7;
                break;
            case PNAME_BLUERELAY8:
                this.name = PNAME_BLUERELAY8;
                this.numberOfRelays = 8;
                this.numberOfGpios = 2;
                this.numberOfAnalogInputs = 1;
                break;
            default:
                this.name = "Unknown Device";
                this.numberOfGpios = 0;
                this.numberOfRelays = 0;
                this.numberOfAnalogInputs = 0;
        }

        //Populate Relay objects for this device
        for (int i = 0; i < this.numberOfRelays; i++) {
            mRelays.add(i, new Relay(this, i));
        }

        //Populate Gpio objects for this device
        for (int i = 0; i < this.numberOfGpios; i++) {
            mGpios.add(i, new Gpio(this, i));
        }

        //Populate Analog objects for this device
        for (int i = 0; i < this.numberOfAnalogInputs; i++) {
            mAnalogs.add(i, new Analog(this, i));
        }
    }

    public static final ArrayList<String> GetSupportedProductIds(){
        ArrayList<String> supportedDevices = new ArrayList<String>();

        /*Populate new devices here. Search for ADD_NEW_DEVICE to find other places where changes needed*/
        supportedDevices.add(NumatoBluetoothDevice.PNAME_BLUEGPIO8);
        supportedDevices.add(NumatoBluetoothDevice.PNAME_BLUERELAY2);
        supportedDevices.add(NumatoBluetoothDevice.PNAME_BLUERELAY8);

        return supportedDevices;
    }

    public String getName() {
        return name;
    }

    public int getIndex() {
        return index;
    }

    public int getNumberOfGpios() {
        return numberOfGpios;
    }

    public int getNumberOfRelays() {
        return numberOfRelays;
    }

    public int getNumberOfAnalogInputs() {
        return numberOfAnalogInputs;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setNumberOfGpios(int numberOfGpios) {
        this.numberOfGpios = numberOfGpios;
    }

    public void setNumberOfRelays(int numberOfRelays) {
        this.numberOfRelays = numberOfRelays;
    }

    public void setNumberOfAnalogInputs(int numberOfAnalogInputs) {
        this.numberOfAnalogInputs = numberOfAnalogInputs;
    }

    //This function return a number that either denotes the number of bytes received or the status
    //of operation. The exact situation depends on the value of receiveLen passed to the function.
    // receiveLen == 0
    //      Return value 0 indicates failure
    //      Return value !=0 indicates success
    // receiveLen > 0
    //      Return value 0 indicates number of bytes received

    public int sendAndReceive(byte sendBuffer[], int sendLen, byte ReceiveBuffer[], int receiveLen) {

        boolean operationSucceeded = true;
        int numBytesReceived = 0;

            device.write(sendBuffer);
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
        }

        return numBytesReceived;
    }
    public int sendGPIO(byte sendBuffer[], int sendLen, byte ReceiveBuffer[], int receiveLen) {

        boolean operationSucceeded = true;
        int numBytesReceived = 0;

        device.write(sendBuffer);

        return numBytesReceived;
    }
    public String readRelay(byte sendBuffer[], int sendLen, byte ReceiveBuffer[], int receiveLen) {

        boolean operationSucceeded = true;
        int numBytesReceived = 0;

        device.write(sendBuffer);

        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
        }
        String value = device.read(sendBuffer);
        value = value.trim();

        return value;
    }
    public int receiveGPIO(byte sendBuffer[], int sendLen, byte ReceiveBuffer[], int receiveLen) {

        boolean operationSucceeded = true;
        int numBytesReceived = 0;

        device.write(sendBuffer);

        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
        }
        String value = device.read(sendBuffer);
        value = value.trim();
        Pattern p = Pattern.compile("[a-z]");
        Matcher m = p.matcher(value);
        numBytesReceived = Integer.parseInt(value.isEmpty()?"0":m.find()?"0":value);
        return numBytesReceived;
    }
    public int receiveADC(byte sendBuffer[], int sendLen, byte ReceiveBuffer[], int receiveLen) {

        boolean operationSucceeded = true;
        int numBytesReceived = 0;

        device.write(sendBuffer);

        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
        }
        String value = device.read(sendBuffer);
        value = value.trim();
        Pattern p = Pattern.compile("[a-z]");
        Matcher m = p.matcher(value);
        numBytesReceived = Integer.parseInt(value.isEmpty()?"0":m.find()?"0":value);
        return numBytesReceived;
    }
    public String sendAndReceiveRelay(byte sendBuffer[], int sendLen, byte ReceiveBuffer[], int receiveLen) {

        boolean operationSucceeded = true;
        int numBytesReceived = 0;
        String responseData = "";

        device.write(sendBuffer);
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
        }
        return responseData;
    }

    public String getFirmwareVersion() throws IOException {
        // Buffer to store the response bytes.
        byte[] data = new byte[256];
        String message = "ver";
        // Translate the passed message into ASCII and store it as a Byte array.
        data = (new String(message+"\r")).getBytes("UTF-8");
        // Send the message to the connected TcpServer.
        device.write(data);

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String responseData = device.read(data);
        return responseData;
    }

    public String getDeviceSpecificId() throws IOException{
        // Buffer to store the response bytes.
        byte[] data = new byte[256];
        String message = "id get";
        // Translate the passed message into ASCII and store it as a Byte array.
        data = (new String(message+"\r")).getBytes("UTF-8");
        // Send the message to the connected TcpServer.
        device.write(data);

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String responseData = device.read(data);
        return responseData;
    }

    public void setDeviceSpecificId(String id) {
        String cmdSetId = "id set " + id + "\r";
        sendAndReceive(cmdSetId.getBytes(), cmdSetId.length(), null, 0);
    }
    public void close(){
        if(device.isConnected())
            device.close();
    }
}
