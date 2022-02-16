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

package com.numato.theia.ui;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import com.numato.theia.R;
import com.numato.theia.business.AppConstant;
import com.numato.theia.model.devices.BluetoothDeviceManager;
import com.numato.theia.model.devices.DevicesManager;
import com.numato.theia.model.devices.EthernetDeviceManager;
import com.numato.theia.model.devices.NumatoBluetoothDevice;
import com.numato.theia.model.devices.NumatoEthernetDevice;
import com.numato.theia.model.devices.NumatoUSBDevice;
import com.numato.theia.ui.adapters.AnalogArrayAdapter;
import com.numato.theia.ui.adapters.GpioArrayAdapter;
import com.numato.theia.ui.adapters.RelayArrayAdapter;
import com.numato.theia.utils.ui.AnimatedTabHostListener;

import java.io.IOException;

import butterknife.Bind;
import butterknife.OnClick;

import static android.view.View.GONE;

/**
 * Lists the devices with their detailed information.
 */
public class DeviceViewActivity extends BaseActivity {


    // ------------------------------------------------------------------------
    // TYPES
    // ------------------------------------------------------------------------
    private enum TAB {
        DEFAULT, GPIO, ANALOG
    }

    // ------------------------------------------------------------------------
    // STATIC FIELDS
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // STATIC METHODS
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // FIELDS
    // ------------------------------------------------------------------------

    //@Bind(R.id.tabHostDeviceAView)
    public TabHost tabHost;

    //@Bind(android.R.id.tabs)
    public TabWidget tabWidget;

    private TextView venderid;

    private TextView productid;

    // Device manager
    private DevicesManager mDevicesManager;

    private EthernetDeviceManager mEthernetDevicesManager;
    private BluetoothDeviceManager mBluetoothDevicesManager;
    private NumatoEthernetDevice numatoEthernetDevice;

    private int mCurrentDeviceIndex = 0;
    private TAB mCurrentTab = TAB.DEFAULT;
    private Handler handler = new Handler();

    /*
     * Array adapters for the tabs content
     */
    private RelayArrayAdapter mRelayArrayAdapter;
    private GpioArrayAdapter mGpioArrayAdapter;
    private AnalogArrayAdapter mAnalogArrayAdapter;

    private Button updateUserID;
    private Runnable refresh = new Runnable() {
        @Override
        public void run() {
            if (mCurrentTab == TAB.GPIO) {
                listGpios();
            }

            if (mCurrentTab == TAB.ANALOG) {
                listAnalog();
            }

            handler.postDelayed(this, 1000);
        }
    };

    // ------------------------------------------------------------------------
    // CONSTRUCTORS
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // METHODS
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // GETTERS / SETTTERS
    // ------------------------------------------------------------------------


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_view);

        tabHost = (TabHost) findViewById(R.id.tabHostDeviceAView);
        tabWidget = (TabWidget) findViewById(android.R.id.tabs);

        updateUserID = (Button) findViewById(R.id.btnUpdateId);
        venderid = (TextView) findViewById(R.id.lblUsbVendorId);
        productid = (TextView) findViewById(R.id.lblUsbProductId);

        mDevicesManager = DevicesManager.getInstance();

        updateUserID.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                changeDeviceId();
            }
        });

        Bundle deviceIndexBundle = getIntent().getExtras();
        if (deviceIndexBundle == null) {
            BluetoothConnectionService mBluetoothConnection = BluetoothConfigActivity.mBluetoothConnection;
            if(mBluetoothConnection != null)
            {
                venderid.setVisibility(GONE);
                productid.setVisibility(GONE);
                mBluetoothDevicesManager = BluetoothDeviceManager.getInstance();
                mBluetoothDevicesManager.addDevice(new NumatoBluetoothDevice());

                setUpTabHost();
                //getDeviceInfo();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                try {
                    getBluetoothDeviceInfo();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // Init relays list
                NumatoBluetoothDevice ethernetDevice = mBluetoothDevicesManager.get(0);
                ListView relayListView = (ListView) findViewById(R.id.listViewRelays);
                mRelayArrayAdapter = new RelayArrayAdapter(DeviceViewActivity.this, ethernetDevice.mRelays);
                relayListView.setAdapter(mRelayArrayAdapter);

                // Init GPIOs
                ListView gpioListView = (ListView) findViewById(R.id.listViewGpios);
                mGpioArrayAdapter = new GpioArrayAdapter(DeviceViewActivity.this, ethernetDevice.mGpios);
                gpioListView.setAdapter(mGpioArrayAdapter);

                // Init analogs
                ListView analogListView = (ListView) findViewById(R.id.listViewAnalog);
                mAnalogArrayAdapter = new AnalogArrayAdapter(DeviceViewActivity.this, ethernetDevice.mAnalogs);
                analogListView.setAdapter(mAnalogArrayAdapter);
            }
            else {
                venderid.setVisibility(GONE);
                productid.setVisibility(GONE);
                mEthernetDevicesManager = EthernetDeviceManager.getInstance();
                mEthernetDevicesManager.addDevice(new NumatoEthernetDevice());

                setUpTabHost();
                //getDeviceInfo();
                try {
                    getEthernetDeviceInfo();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // Init relays list
                NumatoEthernetDevice ethernetDevice = mEthernetDevicesManager.get(0);
                ListView relayListView = (ListView) findViewById(R.id.listViewRelays);
                mRelayArrayAdapter = new RelayArrayAdapter(DeviceViewActivity.this, ethernetDevice.mRelays);
                relayListView.setAdapter(mRelayArrayAdapter);

                // Init GPIOs
                ListView gpioListView = (ListView) findViewById(R.id.listViewGpios);
                mGpioArrayAdapter = new GpioArrayAdapter(DeviceViewActivity.this, ethernetDevice.mGpios);
                gpioListView.setAdapter(mGpioArrayAdapter);

                // Init analogs
                ListView analogListView = (ListView) findViewById(R.id.listViewAnalog);
                mAnalogArrayAdapter = new AnalogArrayAdapter(DeviceViewActivity.this, ethernetDevice.mAnalogs);
                analogListView.setAdapter(mAnalogArrayAdapter);
            }

        }
    else{
            mCurrentDeviceIndex = deviceIndexBundle.getInt(AppConstant.EXTRA_DEVICE_INDEX);
            setTitle(mDevicesManager.get(mCurrentDeviceIndex).getName());

            setUpTabHost();
            getDeviceInfo();

            // Init relays list
            ListView relayListView = (ListView) findViewById(R.id.listViewRelays);
            mRelayArrayAdapter = new RelayArrayAdapter(DeviceViewActivity.this, mDevicesManager.get(mCurrentDeviceIndex).mRelays);
            relayListView.setAdapter(mRelayArrayAdapter);

            // Init GPIOs
            ListView gpioListView = (ListView) findViewById(R.id.listViewGpios);
            mGpioArrayAdapter = new GpioArrayAdapter(DeviceViewActivity.this, mDevicesManager.get(mCurrentDeviceIndex).mGpios);
            gpioListView.setAdapter(mGpioArrayAdapter);

            // Init analogs
            ListView analogListView = (ListView) findViewById(R.id.listViewAnalog);
            mAnalogArrayAdapter = new AnalogArrayAdapter(DeviceViewActivity.this, mDevicesManager.get(mCurrentDeviceIndex).mAnalogs);
            analogListView.setAdapter(mAnalogArrayAdapter);
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mCurrentDeviceIndex > 0)
            mDevicesManager.get(mCurrentDeviceIndex).close();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mCurrentDeviceIndex > 0)
            mDevicesManager.get(mCurrentDeviceIndex).close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mCurrentDeviceIndex > 0)
            mDevicesManager.get(mCurrentDeviceIndex).close();
        if(mBluetoothDevicesManager != null)
            mBluetoothDevicesManager.get(0).close();
        if(mEthernetDevicesManager != null)
            mEthernetDevicesManager.get(0).close();
    }



    @OnClick(R.id.btnUpdateId)
    public void changeDeviceId() {
        TextView txtDeviceSpecificId = (TextView) findViewById(R.id.txtDeviceSpecificId);
        if (txtDeviceSpecificId.getText().length() == 8) {
            if(mCurrentDeviceIndex > 0)
            mDevicesManager.get(mCurrentDeviceIndex).setDeviceSpecificId(txtDeviceSpecificId.getText().toString().substring(0, 8));
            if(mBluetoothDevicesManager != null)
                mBluetoothDevicesManager.get(0).setDeviceSpecificId(txtDeviceSpecificId.getText().toString().substring(0, 8));
                if(mEthernetDevicesManager != null)
                    mEthernetDevicesManager.get(0).setDeviceSpecificId(txtDeviceSpecificId.getText().toString().substring(0, 8));
            Toast.makeText(DeviceViewActivity.this, R.string.message_refresh_device_info, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(DeviceViewActivity.this, R.string.message_id_must_be_8_characters_long, Toast.LENGTH_SHORT).show();
        }
    }

    @NonNull
    private TabHost setUpTabHost() {
        tabHost.setup();
        tabHost.getTabWidget().setStripEnabled(false);
        tabHost.addTab(tabHost.newTabSpec(getString(R.string.tag_info)).setIndicator(getString(R.string.lbl_info)).setContent(R.id.tabInfo));
        tabHost.addTab(tabHost.newTabSpec(getString(R.string.tag_relay)).setIndicator(getString(R.string.lbl_relay)).setContent(R.id.tabRelay));
        tabHost.addTab(tabHost.newTabSpec(getString(R.string.tag_gpio)).setIndicator(getString(R.string.lbl_gpio)).setContent(R.id.tabGpio));
        tabHost.addTab(tabHost.newTabSpec(getString(R.string.tag_analog)).setIndicator(getString(R.string.lbl_analog_title)).setContent(
                        R.id.tabAnalog));
        tabHost.setOnTabChangedListener(new AnimatedTabHostListener(this, tabHost) {

            @Override
            public void onTabChanged(String tabId) {
                super.onTabChanged(tabId);
                if (tabId.equals(getString(R.string.tag_gpio))) {
                    mCurrentTab = TAB.GPIO; // GPIO tab selected
                    listGpios();
                    handler.postDelayed(refresh, 100);
                } else if (tabId.equals(getString(R.string.tag_analog))) {
                    mCurrentTab = TAB.ANALOG; //Analog tab selected
                    listAnalog();
                    handler.postDelayed(refresh, 100);
                } else {
                    mCurrentTab = TAB.DEFAULT; //Some other tab selected
                    handler.removeCallbacks(refresh);
                }
            }
        });

        // Change background
        for (int i = 0; i < tabWidget.getChildCount(); i++) {
            tabWidget.getChildAt(i).setBackgroundResource(R.drawable.tab_widget_style);
            TextView tv = (TextView) tabWidget.getChildAt(i).findViewById(android.R.id.title); //Unselected Tabs
            tv.setTextColor(getResources().getColor(R.color.text_light));
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_size_small));
        }

        return tabHost;
    }


    public boolean getDeviceInfo() {

        //Firmware on Info page
        String firmwareVersionCompare = mDevicesManager.get(mCurrentDeviceIndex).getFirmwareVersion();
        int ver = mDevicesManager.get(mCurrentDeviceIndex).getVersionid();

        String firmwareVersion = mDevicesManager.get(mCurrentDeviceIndex).getFirmwareVersion();
        if (firmwareVersion == null) {
            Toast.makeText(DeviceViewActivity.this, R.string.message_failed_to_read_device_version, Toast.LENGTH_SHORT).show();
            firmwareVersion = getString(R.string.default_missing_value);
        }

        TextView lblFwVerison = (TextView) findViewById(R.id.lblFwVerison);
        lblFwVerison.setText(getString(R.string.firmware_version) + " " + firmwareVersion);


        //Device specific ID on Info page (The ID set/retrieved using id get/set commands)
        String deviceId = mDevicesManager.get(mCurrentDeviceIndex).getDeviceSpecificId();
        if (deviceId == null) {
            Toast.makeText(DeviceViewActivity.this, R.string.message_failed_to_read_device_id, Toast.LENGTH_SHORT).show();
            deviceId = getString(R.string.default_missing_value);

        } else {
            EditText txtDeviceSpecificId = (EditText) findViewById(R.id.txtDeviceSpecificId);
            txtDeviceSpecificId.setText(deviceId);
        }

        TextView lblDeviceID = (TextView) findViewById(R.id.lblDeviceID);
        lblDeviceID.setText(getString(R.string.device_id) + " " + deviceId);


        //USB Vendor ID on Info page
        TextView lblUsbVendorId = (TextView) findViewById(R.id.lblUsbVendorId);
        lblUsbVendorId.setText(getString(R.string.usb_vendor_id) + " " + AppConstant.DEFAULT_VENDOR_ID);

        //USB Product ID on Info page
        TextView lblUsbProductId = (TextView) findViewById(R.id.lblUsbProductId);
        lblUsbProductId.setText(getString(R.string.usb_product_id) +
                        " 0x" + Integer.toHexString(mDevicesManager.get(mCurrentDeviceIndex).getProductId()).toUpperCase());

        //Device Name on Info page
        TextView lblDeviceName = (TextView) findViewById(R.id.lblDeviceName);
        lblDeviceName.setText(getString(R.string.lbl_name) + " " + mDevicesManager.get(mCurrentDeviceIndex).getName());


        //make sure the text view doesn't grab focus
        LinearLayout dummyLayout = (LinearLayout) findViewById(R.id.dummyLayout);
        dummyLayout.requestFocus();

        return true;
    }

    public boolean getEthernetDeviceInfo() throws IOException {

        //Firmware on Info page
        NumatoEthernetDevice ethernetDevice = mEthernetDevicesManager.get(0);
        String firmwareVersion = ethernetDevice.getFirmwareVersion();
        if (firmwareVersion == null) {
            Toast.makeText(DeviceViewActivity.this, R.string.message_failed_to_read_device_version, Toast.LENGTH_SHORT).show();
            //firmwareVersion = getString(R.string.default_missing_value);
        }

        TextView lblFwVerison = (TextView) findViewById(R.id.lblFwVerison);
        lblFwVerison.setText(getString(R.string.firmware_version) + " " + firmwareVersion);


        //Device specific ID on Info page (The ID set/retrieved using id get/set commands)
        String deviceId = ethernetDevice.getDeviceSpecificId();
        if (deviceId == null) {
            Toast.makeText(DeviceViewActivity.this, R.string.message_failed_to_read_device_id, Toast.LENGTH_SHORT).show();
            deviceId = getString(R.string.default_missing_value);

        } else {
            EditText txtDeviceSpecificId = (EditText) findViewById(R.id.txtDeviceSpecificId);
            txtDeviceSpecificId.setText(deviceId);
        }

        TextView lblDeviceID = (TextView) findViewById(R.id.lblDeviceID);
        lblDeviceID.setText(getString(R.string.device_id) + " " + deviceId);
        System.out.println(NumatoEthernetDevice.name);
        //Device Name on Info page
        TextView lblDeviceName = (TextView) findViewById(R.id.lblDeviceName);
        lblDeviceName.setText(getString(R.string.lbl_name) + " " + ethernetDevice.getName());


        //make sure the text view doesn't grab focus
        LinearLayout dummyLayout = (LinearLayout) findViewById(R.id.dummyLayout);
        dummyLayout.requestFocus();

        return true;
    }

   public boolean getBluetoothDeviceInfo() throws IOException {
        //Firmware on Info page
        NumatoBluetoothDevice bluetoothDevice = mBluetoothDevicesManager.get(0);
        String firmwareVersion = bluetoothDevice.getFirmwareVersion();
        if (firmwareVersion == null) {
            Toast.makeText(DeviceViewActivity.this, R.string.message_failed_to_read_device_version, Toast.LENGTH_SHORT).show();
        }

        TextView lblFwVerison = (TextView) findViewById(R.id.lblFwVerison);
        lblFwVerison.setText(getString(R.string.firmware_version) + " " + firmwareVersion);

        //Device specific ID on Info page (The ID set/retrieved using id get/set commands)
        String deviceId = bluetoothDevice.getDeviceSpecificId();
        if (deviceId == null) {
            Toast.makeText(DeviceViewActivity.this, R.string.message_failed_to_read_device_id, Toast.LENGTH_SHORT).show();
            deviceId = getString(R.string.default_missing_value);

        } else {
            EditText txtDeviceSpecificId = (EditText) findViewById(R.id.txtDeviceSpecificId);
            txtDeviceSpecificId.setText(deviceId);
        }

        TextView lblDeviceID = (TextView) findViewById(R.id.lblDeviceID);
        lblDeviceID.setText(getString(R.string.device_id) + " " + deviceId);
        //Device Name on Info page
        TextView lblDeviceName = (TextView) findViewById(R.id.lblDeviceName);
        lblDeviceName.setText(getString(R.string.lbl_name) + " " + bluetoothDevice.getName());


        //make sure the text view doesn't grab focus
        LinearLayout dummyLayout = (LinearLayout) findViewById(R.id.dummyLayout);
        dummyLayout.requestFocus();

        return true;
    }

    /**
     * Refreshed the list of relays
     */
    private void listRelays() {
        mRelayArrayAdapter.clear();
        mRelayArrayAdapter.addAll(mDevicesManager.get(mCurrentDeviceIndex).mRelays);
        mRelayArrayAdapter.clear();
    }

    /**
     * Refreshes the list of gpios
     */
    public void listGpios() {
        mGpioArrayAdapter.clear();
        mGpioArrayAdapter.addAll(mDevicesManager.getDevices().size() > 0 ? mDevicesManager.get(mCurrentDeviceIndex).mGpios :mEthernetDevicesManager != null ? mEthernetDevicesManager.get(0).mGpios : mBluetoothDevicesManager.getDevices()!= null?mBluetoothDevicesManager.get(0).mGpios:mBluetoothDevicesManager.get(0).mGpios);
        mGpioArrayAdapter.notifyDataSetChanged();
    }

    /**
     * Refreshes the list of analog devices
     */
    public void listAnalog() {
        mAnalogArrayAdapter.clear();
        mAnalogArrayAdapter.addAll(mDevicesManager.getDevices().size() > 0 ? mDevicesManager.get(mCurrentDeviceIndex).mAnalogs :mEthernetDevicesManager != null ? mEthernetDevicesManager.get(0).mAnalogs : mBluetoothDevicesManager.getDevices()!= null?mBluetoothDevicesManager.get(0).mAnalogs:mBluetoothDevicesManager.get(0).mAnalogs);
        mAnalogArrayAdapter.notifyDataSetChanged();
    }


}
