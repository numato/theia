package com.numato.theia.model.devices;

import java.util.ArrayList;
import java.util.List;

public class EthernetDeviceManager {
    // ------------------------------------------------------------------------
    // TYPES
    // ------------------------------------------------------------------------\

    private static class Holder {
        static final EthernetDeviceManager INSTANCE = new EthernetDeviceManager();
    }


    // ------------------------------------------------------------------------
    // STATIC FIELDS
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // STATIC METHODS
    // ------------------------------------------------------------------------
    public static EthernetDeviceManager getInstance() {
        return Holder.INSTANCE;
    }

    // ------------------------------------------------------------------------
    // FIELDS
    // ------------------------------------------------------------------------

    /**
     * Devices list
     */
    private List<NumatoEthernetDevice> mDeviceArray;

    // ------------------------------------------------------------------------
    // CONSTRUCTORS
    // ------------------------------------------------------------------------

    private EthernetDeviceManager() {
        mDeviceArray = new ArrayList<NumatoEthernetDevice>();
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
    public List<NumatoEthernetDevice> getDevices() {
        return mDeviceArray;
    }

    public void setDevices(List<NumatoEthernetDevice> deviceArray) {
        mDeviceArray = deviceArray;
    }

    public void clearDevices() {
        mDeviceArray.clear();
    }

    public void addDevice(NumatoEthernetDevice numatoUSBDevice) {
        mDeviceArray.add(numatoUSBDevice);
    }

    public void addDevices(List<NumatoEthernetDevice> devices) {
        mDeviceArray.addAll(devices);
    }


    /**
     * Returns the device with the given index
     * @param currentDeviceIndex
     * @return
     */
    public NumatoEthernetDevice get(int currentDeviceIndex) {
        return mDeviceArray.get(currentDeviceIndex);
    }
}
