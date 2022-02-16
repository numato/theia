package com.numato.theia.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.numato.theia.R;
import com.numato.theia.model.devices.NumatoEthernetDevice;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.telnet.TelnetClient;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ConfigActivity extends Activity {
    TelnetClient tc;

    OutputStream outstr;

    InputStream intstr;

    DataInputStream ins;
    DataOutputStream outs;

    private Button connect;

    Spinner deviceName;
    EditText ipAddress;
    EditText portNumber;
    EditText userName;
    EditText password;

    String username;
    String passwords;

    private static final int MY_PERMISSIONS_REQUEST_INTERNET = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.config_ethernet);

        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end,
                                       android.text.Spanned dest, int dstart, int dend) {
                if (end > start) {
                    String destTxt = dest.toString();
                    String resultingTxt = destTxt.substring(0, dstart)
                            + source.subSequence(start, end)
                            + destTxt.substring(dend);
                    if (!resultingTxt
                            .matches("^\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3})?)?)?)?)?)?")) {
                        return "";
                    } else {
                        String[] splits = resultingTxt.split("\\.");
                        for (int i = 0; i < splits.length; i++) {
                            if (Integer.valueOf(splits[i]) > 255) {
                                return "";
                            }
                        }
                    }
                }
                return null;
            }

        };
        deviceName = (Spinner) findViewById(R.id.Products);
        ipAddress = (EditText) findViewById(R.id.txtIPAddress);
        portNumber = (EditText) findViewById(R.id.txtportnumber);
        userName = (EditText) findViewById(R.id.txtusername);
        password = (EditText) findViewById(R.id.txtpassword);
        connect = (Button) findViewById(R.id.btnConnect);

        ipAddress.setFilters(filters);

        int permissionCheck = ContextCompat.checkSelfPermission(ConfigActivity.this, Manifest.permission.INTERNET);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(ConfigActivity.this, Manifest.permission.INTERNET)) {
            } else {
                ActivityCompat.requestPermissions(ConfigActivity.this, new String[]{
                        Manifest.permission.INTERNET
                }, MY_PERMISSIONS_REQUEST_INTERNET);
            }
        }
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        connect.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {
           try {
               String ipaddress = ipAddress.getText() != null ? ipAddress.getText().toString() : "";
               String port = portNumber.getText() != null ? portNumber.getText().toString() : "23";
               username = userName.getText() != null ? userName.getText().toString() : "admin";
               passwords = password.getText() != null ? password.getText().toString() : "admin";
               if (username.isEmpty() || passwords.isEmpty()) {
                   Toast.makeText(ConfigActivity.this, getString(R.string.ethernet_wifi_devices_usename_password), Toast.LENGTH_SHORT).show();
                   return;
               } else {
                   tc = new TelnetClient();
                   tc.connect(ipaddress, Integer.parseInt(port));
               }

           } catch (IOException e) {
               Toast.makeText(ConfigActivity.this, getString(R.string.ethernet_devices_not_found), Toast.LENGTH_SHORT).show();
               e.printStackTrace();
               return;
           }

           outstr = tc.getOutputStream();
           intstr = tc.getInputStream();
           try {
               Thread.sleep(100);
           } catch (InterruptedException e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
           }

           if (tc != null) {
               // Buffer to store the response bytes.
               byte[] data = new byte[256];
               // Read the first batch of the TcpServer response bytes.
               int bytes = 0;
               try {
                   bytes = intstr.read(data, 0, data.length);
               } catch (IOException e) {
                   e.printStackTrace();
               }
               String responseData = new String(data, 0, bytes);
               String result = responseData;
               if (responseData.contains("User Name: ")) {
                   String message = username;
                   // Translate the passed message into ASCII and store it as a Byte array.
                   try {
                       data = (new String(message + "\r\n")).getBytes("UTF-8");
                       // Send the message to the connected TcpServer.

                       outstr.write(data, 0, data.length);
                       outstr.flush();
                   } catch (IOException e) {
                       e.printStackTrace();
                   }

               }
               try {
                   Thread.sleep(100);
               } catch (InterruptedException e) {
                   // TODO Auto-generated catch block
                   e.printStackTrace();
               }
               data = new byte[256];
               try {
                   // Read the first batch of the TcpServer response bytes.
                   bytes = intstr.read(data, 0, data.length);
                   responseData = new String(data, 0, bytes);
                   result = result + responseData;
                   if (responseData.contains("Password: ")) {
                       String message = passwords;
                       // Translate the passed message into ASCII and store it as a Byte array.
                       data = (new String(message + "\r\n")).getBytes("UTF-8");
                       // Send the message to the connected TcpServer.
                       outstr.write(data, 0, data.length);
                       outstr.flush();
                   }
                   try {
                       Thread.sleep(100);
                   } catch (InterruptedException e) {
                       // TODO Auto-generated catch block
                       e.printStackTrace();
                   }
                   data = new byte[256];
                   bytes = intstr.read(data, 0, data.length);
                   responseData = new String(data, 0, bytes);
                   result = result + responseData;
               } catch (IOException e) {
                   e.printStackTrace();
               }
           }
           NumatoEthernetDevice ethernetDevice = new NumatoEthernetDevice(tc, deviceName.getSelectedItem().toString());

           Intent config = new Intent(ConfigActivity.this, DeviceViewActivity.class);
           startActivity(config);

           // since this is the main activity we slide it from left and out to left as well
           overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
       }
   }

        );

    }

    public TelnetClient Connection() throws IOException, InterruptedException {
        TelnetClient telnetClient = null;

        OutputStream outstr;

        InputStream intstr;

        DataInputStream ins;
        DataOutputStream outs;

        telnetClient = new TelnetClient();

        telnetClient.connect("192.168.5.148", 23);
        System.out.println(telnetClient.isConnected());
        //getCOnnection(remoteip, remoteport);
        outstr = telnetClient.getOutputStream();
        intstr = telnetClient.getInputStream();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (telnetClient != null) {
            // Buffer to store the response bytes.
            byte[] data = new byte[256];
            // Read the first batch of the TcpServer response bytes.
            int bytes = intstr.read(data, 0, data.length);
            String responseData = new String(data, 0, bytes);
            String result = responseData;
            if (responseData.contains("User Name: ")) {
                String message = "admin";
                // Translate the passed message into ASCII and store it as a Byte array.
                data = (new String(message + "\r\n")).getBytes("UTF-8");
                // Send the message to the connected TcpServer.
                outstr.write(data, 0, data.length);
                outstr.flush();
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            data = new byte[256];

            // Read the first batch of the TcpServer response bytes.
            bytes = intstr.read(data, 0, data.length);
            responseData = new String(data, 0, bytes);
            result = result + responseData;
            if (responseData.contains("Password: ")) {
                String message = "admin";
                // Translate the passed message into ASCII and store it as a Byte array.
                data = (new String(message + "\r\n")).getBytes("UTF-8");
                // Send the message to the connected TcpServer.
                outstr.write(data, 0, data.length);
                outstr.flush();
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            data = new byte[256];
            bytes = intstr.read(data, 0, data.length);
            responseData = new String(data, 0, bytes);
            result = result + responseData;
        }
        return telnetClient;
    }
}
