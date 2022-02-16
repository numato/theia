package com.numato.theia.model.devices;

import java.util.ArrayList;
import java.util.List;

public class BluetoothDeviceManager {
    // ------------------------------------------------------------------------
    // TYPES
    // ------------------------------------------------------------------------\

    private static class Holder {
        static final BluetoothDeviceManager INSTANCE = new BluetoothDeviceManager();
    }


    // ------------------------------------------------------------------------
    // STATIC FIELDS
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // STATIC METHODS
    // ------------------------------------------------------------------------
    public static BluetoothDeviceManager getInstance() {
        return Holder.INSTANCE;
    }

    // ------------------------------------------------------------------------
    // FIELDS
    // ------------------------------------------------------------------------

    /**
     * Devices list
     */
    private List<NumatoBluetoothDevice> mDeviceArray;

    // ------------------------------------------------------------------------
    // CONSTRUCTORS
    // ------------------------------------------------------------------------

    private BluetoothDeviceManager() {
        mDeviceArray = new ArrayList<NumatoBluetoothDevice>();
    }

    // ------------------------------------------------------------------------
    // METHODS
    // ------------------------------------------------------------------------


    // ------------------------------------------------------------------------
    // GETTERS / SETTTERS
    // ------------------------------------------------------------------------


    /**
     * @return the list of devices which are present.
     */
    public List<NumatoBluetoothDevice> getDevices() {
        return mDeviceArray;
    }

    public void setDevices(List<NumatoBluetoothDevice> deviceArray) {
        mDeviceArray = deviceArray;
    }

    public void clearDevices() {
        mDeviceArray.clear();
    }

    public void addDevice(NumatoBluetoothDevice numatoUSBDevice) {
        mDeviceArray.add(numatoUSBDevice);
    }

    public void addDevices(List<NumatoBluetoothDevice> devices) {
        mDeviceArray.addAll(devices);
    }


    /**
     * Returns the device with the given index
     * @param currentDeviceIndex
     * @return
     */
    public NumatoBluetoothDevice get(int currentDeviceIndex) {
        return mDeviceArray.get(currentDeviceIndex);
    }
}
