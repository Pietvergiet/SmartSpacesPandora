package smartSpaces.Pandora.P2P;


import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import java.util.HashMap;

import smartSpaces.Pandora.R;

import static android.content.pm.PackageManager.PERMISSION_DENIED;


public class BluetoothService extends FragmentActivity {

    private final static int REQUEST_ENABLE_BT = 1;

    private static final String TAG = "BActivity";

    private TextView tekst;
    private TextView messageView;
    private EditText editText;
    private Button join;
    private Button host;


    private LinearLayout layout;

    private boolean isHost;
    private MyBluetoothService myBluetoothService;
    private BluetoothAdapter mBluetoothAdapter;

    private HashMap<Integer, BluetoothDevice> scannerdst;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
//                Log.i(TAG, "Found device" + deviceName);
                String deviceHardwareAddress = device.getAddress(); // MAC address
                scannerdst.put(device.hashCode(), device);
//                tekst.setText(scannerdst.keySet().toString());
                if(deviceName != null && !deviceName.isEmpty()) {
                    makeNewButton(device);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        layout = findViewById(R.id.buttons);
        tekst = findViewById(R.id.textView);
        messageView = findViewById(R.id.messageView);
        editText = findViewById(R.id.messageSend);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Log.i(TAG,"Gaa bluetooth fien jow");
        }
        if (!mBluetoothAdapter.isEnabled()) {
            // We need to enable the Bluetooth, so we ask the user
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            mBluetoothAdapter.enable();
        }

        checkGetPermissions();
        initBluetooth(this.getApplicationContext());
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.getApplicationContext().registerReceiver(receiver, filter);

    }

    private void initBluetooth(Context context) {
        scannerdst = new HashMap<>();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

//        if(!mBluetoothAdapter.setName(TEMP_SERVER_NAME)) {
//            tekst.setText("ging niet goed");
//        } else {
//            tekst.setText("ging wel goed");
//        }

    }

    private void makeNewButton(final BluetoothDevice device) {
        //set the properties for button
        final Button btnTag = new Button(this);
        btnTag.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT));
        btnTag.setText(scannerdst.get(device.hashCode()).getName());
        btnTag.setId(device.hashCode());
        btnTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tekst.setText(scannerdst.get(v.getId()).getName());
                makeConnection(v.getId());
            }
        });
        //add button to the layout
        layout.addView(btnTag);
    }

    private void makeConnection(int id) {
        int counter = 0;
        mBluetoothAdapter.cancelDiscovery();
        myBluetoothService.join(scannerdst.get(id));
        tekst.setText("Waiting for connection");
        while (!myBluetoothService.isClientConnected()) {
            counter++;
            if (counter%100 == 0) {
                tekst.setText(tekst.getText() + ".");
            }

        }
        tekst.setText("Connected to: " + myBluetoothService.getHostName());
    }

    public void startHost(View view) {
        isHost = true;
        myBluetoothService = new MyBluetoothService(mHandler, isHost);
        buttonDis();

        Intent discoverableIntent =
                new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);
        myBluetoothService.host();

    }

    public void joinHost(View view) {
        isHost = false;
        myBluetoothService = new MyBluetoothService(mHandler, isHost);
        buttonDis();
        if (mBluetoothAdapter.isDiscovering()) {
            // Bluetooth is already in modo discovery mode, we cancel to restart it again
            mBluetoothAdapter.cancelDiscovery();
        }
        mBluetoothAdapter.startDiscovery();
    }

    public void startCommunication(View view){
        mBluetoothAdapter.cancelDiscovery();
        myBluetoothService.stopClientDiscovery();
    }


    public void sendMessage(View view){
        String tekst = ((EditText) findViewById(R.id.messageSend)).getText().toString();
        editText.setText("");
        myBluetoothService.write(tekst.getBytes());
    }

    private void checkGetPermissions() {
        int checkCoarse = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int MY_PERMISSIONS_REQUEST = 200;
        int checkFine =ContextCompat.checkSelfPermission (this,Manifest.permission.ACCESS_FINE_LOCATION);
        if (checkFine == PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST);
        }
        if (checkCoarse == PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST);
        }
    }

    private void buttonDis() {
        findViewById(R.id.Host).setEnabled(false);
        findViewById(R.id.Join).setEnabled(false);
    }

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
//            FragmentActivity activity = getActivity();
            switch (msg.what) {
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    Log.i(TAG, "Writinng message");
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    messageView.setText(readMessage);
                    Log.i(TAG, "Read message: " + readMessage);
                    break;
                case Constants.NEW_CONNECTION:
                    String text = "Connected clients: " + myBluetoothService.getConnectedClients().toString();
                    Log.i(TAG, "Displayed connections: " + text);
                    tekst.setText(text);
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myBluetoothService != null){
            myBluetoothService.stop();
            myBluetoothService = null;
        }
        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(receiver);
    }

}
