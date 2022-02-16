package com.numato.theia.model.devices;

import android.util.Log;

import org.apache.commons.net.telnet.TelnetClient;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumatoEthernetDevice {
    TelnetClient tc = null;

    public static OutputStream outstr;

    public static InputStream intstr;

    DataInputStream ins ;
    DataOutputStream outs ;

    public static final int VID_NUMATOLAB = 0x2A19;

    /*Populate new devices here. Search for ADD_NEW_DEVICE to find other places where changes needed*/
    /* Ethernet GPIO Devices */
    public static final String PNAME_ETHERGPIO16 = "16 Channel Ethernet GPIO Module";
    public static final String PNAME_ETHERGPIO32 = "32 Channel Ethernet GPIO Module";

    /* Ethernet Relay Devices */
    public static final String PNAME_ETHERRELAY8 = "8 Channel Ethernet Relay Module";
    public static final String PNAME_ETHERRELAY16 = "16 Channel Ethernet Relay Module";
    public static final String PNAME_ETHERRELAY32 = "32 Channel Ethernet Relay Module";

    /* Ethernet SSR Relay Devices */
    public static final String PNAME_ETHERSSR4 = "4 Channel Ethernet Solid State Relay Module";
    public static final String PNAME_ETHERSSR8 = "8 Channel Ethernet Solid State Relay Module";

    /* WiFi GPIO Devices */
    public static final String PNAME_WIFIGPIO16 = "16 Channel Wifi GPIO Module";
    public static final String PNAME_WIFIGPIO32 = "32 Channel Wifi GPIO Module";

    /* WiFi Relay Devices */
    public static final String PNAME_WIFIRELAY8 = "8 Channel Wifi Relay Module";
    public static final String PNAME_WIFIRELAY16 = "16 Channel Wifi Relay Module";
    public static final String PNAME_WIFIRELAY32 = "32 Channel Wifi Relay Module";

    public static String name;
    private int index;
    private int numberOfGpios;
    private int numberOfRelays;
    private int numberOfAnalogInputs;
    private boolean isPortOpen;

    public static ArrayList<Relay> mRelays = new ArrayList<Relay>();
    public static ArrayList<Gpio> mGpios = new ArrayList<Gpio>();
    public static ArrayList<Analog> mAnalogs = new ArrayList<Analog>();
    public static TelnetClient device;

    public NumatoEthernetDevice() {
    }

    public NumatoEthernetDevice(TelnetClient device, String deviceName) {

        this.device = device;
        outstr = this.device.getOutputStream();
        intstr = this.device.getInputStream();
        setName(deviceName);
        /*Populate new devices here. Search for ADD_NEW_DEVICE to find other places where changes needed*/
        switch (deviceName) {

            case PNAME_ETHERGPIO16:
                this.name = PNAME_ETHERGPIO16;
                this.numberOfRelays = 0;
                this.numberOfGpios = 16;
                this.numberOfAnalogInputs = 10;
                break;
            case PNAME_ETHERGPIO32:
                this.name = PNAME_ETHERGPIO32;
                this.numberOfRelays = 0;
                this.numberOfGpios = 32;
                this.numberOfAnalogInputs = 14;
                break;
            case PNAME_ETHERRELAY8:
                this.name = PNAME_ETHERRELAY8;
                this.numberOfRelays = 8;
                this.numberOfGpios = 10;
                this.numberOfAnalogInputs = 10;
                break;
            case PNAME_ETHERRELAY16:
                this.name = PNAME_ETHERRELAY16;
                this.numberOfRelays = 16;
                this.numberOfGpios = 8;
                this.numberOfAnalogInputs = 8;
                break;
            case PNAME_ETHERRELAY32:
                this.name = PNAME_ETHERRELAY32;
                this.numberOfRelays = 32;
                this.numberOfGpios = 8;
                this.numberOfAnalogInputs = 8;
                break;
            case PNAME_ETHERSSR4:
                this.name = PNAME_ETHERSSR4;
                this.numberOfRelays = 4;
                this.numberOfGpios = 10;
                this.numberOfAnalogInputs = 10;
                break;

            case PNAME_ETHERSSR8:
                this.name = PNAME_ETHERSSR8;
                this.numberOfRelays = 8;
                this.numberOfGpios = 10;
                this.numberOfAnalogInputs = 10;
                break;

            case PNAME_WIFIGPIO16:
                this.name = PNAME_WIFIGPIO16;
                this.numberOfRelays = 0;
                this.numberOfGpios = 16;
                this.numberOfAnalogInputs = 14;
                break;
            case PNAME_WIFIGPIO32:
                this.name = PNAME_WIFIGPIO32;
                this.numberOfRelays = 0;
                this.numberOfGpios = 32;
                this.numberOfAnalogInputs = 14;
                break;
            case PNAME_WIFIRELAY8:
                this.name = PNAME_WIFIRELAY8;
                this.numberOfRelays = 8;
                this.numberOfGpios = 10;
                this.numberOfAnalogInputs = 10;
                break;
            case PNAME_WIFIRELAY16:
                this.name = PNAME_WIFIRELAY16;
                this.numberOfRelays = 16;
                this.numberOfGpios = 8;
                this.numberOfAnalogInputs = 8;
                break;
            case PNAME_WIFIRELAY32:
                this.name = PNAME_WIFIRELAY32;
                this.numberOfRelays = 32;
                this.numberOfGpios = 8;
                this.numberOfAnalogInputs = 8;
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
        supportedDevices.add(NumatoEthernetDevice.PNAME_ETHERGPIO16);
        supportedDevices.add(NumatoEthernetDevice.PNAME_ETHERGPIO32);
        supportedDevices.add(NumatoEthernetDevice.PNAME_ETHERRELAY8);
        supportedDevices.add(NumatoEthernetDevice.PNAME_ETHERRELAY16);
        supportedDevices.add(NumatoEthernetDevice.PNAME_ETHERRELAY32);
        supportedDevices.add(NumatoEthernetDevice.PNAME_ETHERSSR4);
        supportedDevices.add(NumatoEthernetDevice.PNAME_ETHERSSR8);
        supportedDevices.add(NumatoEthernetDevice.PNAME_WIFIGPIO16);
        supportedDevices.add(NumatoEthernetDevice.PNAME_WIFIGPIO32);
        supportedDevices.add(NumatoEthernetDevice.PNAME_WIFIRELAY8);
        supportedDevices.add(NumatoEthernetDevice.PNAME_WIFIRELAY16);
        supportedDevices.add(NumatoEthernetDevice.PNAME_WIFIRELAY32);

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

        try {
            if(device.isConnected()) {
                outstr.write(sendBuffer, 0, sendBuffer.length);
                outstr.flush();
            }
        }catch(IOException ex){
            Log.d("Numato", "Exception while trying to write to port");
            return 0;
        }
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
        }
        sendBuffer = new byte[256];
        try {
            int bytes = intstr.read(sendBuffer, 0, sendBuffer.length);
            String responseData = new String(sendBuffer, 0, bytes);
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
                version = version.replaceAll(">", "");
                rx = (Pattern.compile("([0-9]*)"));
                m = rx.matcher(version.trim());

                if (m.find()) {
                    responseData = m.group(0).trim();
                }
            }
            numBytesReceived = Integer.parseInt(responseData.isEmpty()?"0":responseData);
        }catch(IOException ex){
            Log.d("Numato", ex.getMessage());
            return 0;
        }

        return numBytesReceived;
    }
    public int sendAndReceiveGPIO(byte sendBuffer[], int sendLen, byte ReceiveBuffer[], int receiveLen) {

        boolean operationSucceeded = true;
        int numBytesReceived = 0;

        try {
            outstr.write(sendBuffer, 0 , sendBuffer.length);
            outstr.flush();
        }catch(IOException ex){
            Log.d("Numato", "Exception while trying to write to port");
            return 0;
        }
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
        }
        sendBuffer = new byte[256];
        try {
            int bytes = intstr.read(sendBuffer, 0, sendBuffer.length);
            String responseData = new String(sendBuffer, 0, bytes);
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
                version = version.replaceAll(">", "");
                rx = (Pattern.compile("([0-9]*)"));
                m = rx.matcher(version.trim());

                if (m.find()) {
                    responseData = m.group(0).trim();
                }
            }
            numBytesReceived = Integer.parseInt(responseData.isEmpty()?"0":responseData);
        }catch(IOException ex){
            Log.d("Numato", ex.getMessage());
            return 0;
        }

        return numBytesReceived;
    }
    public String sendAndReceiveRelay(byte sendBuffer[], int sendLen, byte ReceiveBuffer[], int receiveLen) {

        boolean operationSucceeded = true;
        int numBytesReceived = 0;
        String responseData = "";

        try {
            outstr.write(sendBuffer, 0 , sendBuffer.length);
            outstr.flush();
        }catch(IOException ex){
            Log.d("Numato", "Exception while trying to write to port");
        }
        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
        }
        sendBuffer = new byte[256];
        try {
            int bytes = intstr.read(sendBuffer, 0, sendBuffer.length);
            responseData = new String(sendBuffer, 0, bytes);

        }catch(IOException ex){
            Log.d("Numato", ex.getMessage());
        }

        return responseData;
    }

    public String getFirmwareVersion() throws IOException {
        // Buffer to store the response bytes.
        byte[] data = new byte[256];
        String message = "ver";
        // Translate the passed message into ASCII and store it as a Byte array.
        data = (new String(message+"\r\n")).getBytes("UTF-8");
        // Send the message to the connected TcpServer.
        outstr.write(data, 0, data.length);
        outstr.flush();

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        data = new byte[256];
        int bytes = intstr.read(data, 0, data.length);
        String responseData = new String(data, 0, bytes);

        responseData = responseData.isEmpty() ? "" : responseData.replaceAll(">", "").trim();
        return responseData;
    }

    public String getDeviceSpecificId() throws IOException{
        // Buffer to store the response bytes.
        byte[] data = new byte[256];
        String message = "id get";
        // Translate the passed message into ASCII and store it as a Byte array.
        data = (new String(message+"\r\n")).getBytes("UTF-8");
        // Send the message to the connected TcpServer.
        outstr.write(data, 0, data.length);
        outstr.flush();

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        data = new byte[256];
        int bytes = intstr.read(data, 0, data.length);
        String responseData = new String(data, 0, bytes);

        responseData = responseData.isEmpty() ? "" : responseData.replaceAll(">", "").trim();
        return responseData;
    }

    public void setDeviceSpecificId(String id) {
        String cmdSetId = "id set " + id + "\r\n";
        sendAndReceive(cmdSetId.getBytes(), cmdSetId.length(), null, 0);
    }
    public void close(){
        try {
            if(device.isConnected()) {
                outstr.close();
                intstr.close();
                device.disconnect();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
