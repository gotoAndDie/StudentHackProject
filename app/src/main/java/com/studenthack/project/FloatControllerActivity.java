package com.studenthack.project;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class FloatControllerActivity extends AppCompatActivity {

    private OutputStream outputStream;
    private InputStream inputStream;
    private final UUID PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");//Serial Port Service ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_floatcontroller);
        int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
        uiOptions |= View.SYSTEM_UI_FLAG_FULLSCREEN;
        uiOptions |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        uiOptions |= View.SYSTEM_UI_FLAG_IMMERSIVE;
        uiOptions |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        getWindow().getDecorView().setSystemUiVisibility(uiOptions);
        FloatingTouchEventView controls = findViewById(R.id.drawing);
        controls.setInfoBox((TextView) findViewById(R.id.infoView));


        // Perform bluetooth connection again because it cannot be passed from main method
        // It is performed with the same parameters so we assume nothing has changed
        String target_mac = getIntent().getStringExtra("target_mac");
        BluetoothDevice device = null;
        if(target_mac == null) return;
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();
        for (BluetoothDevice iterator : bondedDevices)
            if(iterator.getAddress().equals(target_mac))
                device=iterator;
        if(device == null) return;

        // Next, extract the input and output streams (if possible)
        BluetoothSocket socket;
        try {
            socket = device.createRfcommSocketToServiceRecord(PORT_UUID);
            socket.connect();
            outputStream=socket.getOutputStream();
            inputStream=socket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        controls.setIO(inputStream, outputStream);
    }

}
