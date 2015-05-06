package com.example.hamed.obdapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class BluetoothActivity extends Activity implements OnClickListener {

    private static final int REQUEST_ENABLE_BT = 1;

    private Button onBtn;
    private Button offBtn;
    private Button listBtn;
    private Button findBtn;

    private TextView text;
    private BluetoothAdapter myBluetoothAdapter;
    private Set<BluetoothDevice> pairedDevices;
    private ListView myListView;
    private ArrayAdapter<String> BTArrayAdapter;

    private String mDeviceIdentifier;
    private String mDeviceName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth);

        // take an instance of BluetoothAdapter - Bluetooth radio
        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(myBluetoothAdapter == null) {
            onBtn.setEnabled(false);
            offBtn.setEnabled(false);
            listBtn.setEnabled(false);
            findBtn.setEnabled(false);
            text.setText("Status: not supported");

            Toast.makeText(getApplicationContext(),"Your device does not support Bluetooth",
                    Toast.LENGTH_LONG).show();
        } else {

            text = (TextView) findViewById(R.id.text);

            onBtn = (Button)findViewById(R.id.turnOn);
            onBtn.setOnClickListener(this);

            offBtn = (Button)findViewById(R.id.turnOff);
            offBtn.setOnClickListener(this);

            listBtn = (Button)findViewById(R.id.paired);
            listBtn.setOnClickListener(this);

            findBtn = (Button)findViewById(R.id.search);
            findBtn.setOnClickListener(this);

            myListView = (ListView)findViewById(R.id.pairedListView);
            ArrayList<String> values = new ArrayList();
            //values.add("Paired devices");

            // ListView Item Click Listener
            myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {

                    // ListView Clicked item index
                    int itemPosition     = position;

                    // ListView Clicked item value
                    String  itemValue    = (String) myListView.getItemAtPosition(position);
                    // Save device identifier for connection
                    mDeviceIdentifier = itemValue;

                    // Show Alert
                    Toast.makeText(getApplicationContext(),
                            "Position :"+itemPosition+"  ListItem : " +itemValue , Toast.LENGTH_LONG)
                            .show();

                    createConnectDialog();

                }
            });

            BTArrayAdapter = createAdapter(values);
            myListView.setAdapter(BTArrayAdapter);
        }
    }

    public void createConnectDialog(){

        // split string into device name and device identifier
        String[] splitted = mDeviceIdentifier.split("\\s+");
        int len = splitted.length;
        String uuidString = splitted[len - 1];
        String name = "";
        // Add every part of splitted except the last one which is uuid
        for (String str : splitted){
            if (str == uuidString){
                break;
            }
            name += str;
        }

        // Connect to clicked device in list
        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(BluetoothActivity.this);
        //AlertDialog alert = new AlertDialog.Builder(getParent().this).create();

        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage("uuid : " + uuidString)
                .setTitle("Connect to " + name + "?");

        // 3. Get the AlertDialog from create()
        AlertDialog dialog = builder.create();

        builder.setPositiveButton("Yes mein Führer", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
            }
        });
        builder.setNegativeButton("NEIN NEIN NEIN!", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });

        builder.show();
    }

    @Override
    public void onClick(View v)
    {

        switch (v.getId()) {
            case R.id.turnOn:
                on();
               break;
            case R.id.turnOff:
                off();
                break;
            case R.id.paired:
                listPairedDevices();
                break;
            case R.id.search:
                discoverDevices();
                break;
        }
    }

    public void on(){
        if (!myBluetoothAdapter.isEnabled()) {

            Toast.makeText(getApplicationContext(), "Mah method",
                    Toast.LENGTH_LONG).show();

            Intent turnOnIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOnIntent, REQUEST_ENABLE_BT);

            Toast.makeText(getApplicationContext(),"Bluetooth turned on" ,
                    Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(getApplicationContext(),"Bluetooth is already on",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if(requestCode == REQUEST_ENABLE_BT){
            if(myBluetoothAdapter.isEnabled()) {
                text.setText("Status: Enabled");
            } else {
                text.setText("Status: Disabled");
            }
        }
    }

    public void listPairedDevices(){

        Toast.makeText(getApplicationContext(), "Paired Devices",
                Toast.LENGTH_LONG).show();

        	if (!myBluetoothAdapter.isEnabled()) {
    	    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
    	    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    	}

//    	// see if there is all ready paired devices
    	Set<BluetoothDevice> pairedDevices = myBluetoothAdapter.getBondedDevices();
    	ArrayList<String> deviceInfos = new ArrayList();
    	// If there are paired devices
    	if (pairedDevices.size() > 0) {
    	    // Loop through paired devices
    	    for (BluetoothDevice device : pairedDevices) {
    	        // Add the name and address to an array adapter to show in a ListView
    	    	deviceInfos.add(device.getName() + "\n" + device.getAddress());
    	      //  mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
    	    }
    	    myListView.setAdapter(createAdapter(deviceInfos) );

    	}
    }

    public void discoverDevices(){
        if (myBluetoothAdapter.isDiscovering()) {
            // the button is pressed when it discovers, so cancel the discovery
            myBluetoothAdapter.cancelDiscovery();
        }
        else {
            BTArrayAdapter.clear();
            myBluetoothAdapter.startDiscovery();
            registerReceiver(bReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        }
    }

    // Create adapter for use in listPaired devices
    private ArrayAdapter<String> createAdapter(ArrayList<String> data){
        return new ArrayAdapter<String>(this, R.layout.bt_text_view, data.toArray(new String[data.size()]));
    }

    public void list(View view){
        // get paired devices
        pairedDevices = myBluetoothAdapter.getBondedDevices();

        // put it's one to the adapter
        for(BluetoothDevice device : pairedDevices)
            BTArrayAdapter.add(device.getName()+ "\n" + device.getAddress());

        Toast.makeText(getApplicationContext(),"Show Paired Devices",
                Toast.LENGTH_SHORT).show();

    }

    /*
        Class that takes the string with device identifier and tries to create a bluetooth socket to the device and returns the socket.
     */
    public BluetoothSocket connectToOBD(String deviceAddress) throws Exception{
        BluetoothDevice device = myBluetoothAdapter.getRemoteDevice(deviceAddress);
        UUID uuid = UUID.fromString(deviceAddress);
        BluetoothSocket socket = device.createInsecureRfcommSocketToServiceRecord(uuid);
        socket.connect();
        return socket;
    }

    final BroadcastReceiver bReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // add the name and the MAC address of the object to the arrayAdapter
                BTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                BTArrayAdapter.notifyDataSetChanged();
            }
        }
    };

    public void find(View view) {
        if (myBluetoothAdapter.isDiscovering()) {
            // the button is pressed when it discovers, so cancel the discovery
            myBluetoothAdapter.cancelDiscovery();
        }
        else {
            BTArrayAdapter.clear();
            myBluetoothAdapter.startDiscovery();

            registerReceiver(bReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        }
    }

    public void off(){
        myBluetoothAdapter.disable();
        text.setText("Status: Disconnected");

        Toast.makeText(getApplicationContext(),"Bluetooth turned off",
                Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        unregisterReceiver(bReceiver);
    }


}