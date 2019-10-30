package smartSpaces.Pandora.P2P;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import java.util.ArrayList;

import smartSpaces.Pandora.Picklock.R;

import static android.content.pm.PackageManager.PERMISSION_DENIED;


public class DemoBluetoothService extends FragmentActivity {

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 3;

    private static final String TAG = "BActivity";

    private TextView text;
    private TextView messageView;
    private EditText editText;
    private ArrayAdapter<String> mNewDevicesArrayAdapter;
    private ArrayList<String> clients;
    private ListView messageListView;

    private BluetoothService myBluetoothService;
    private BluetoothAdapter mBluetoothAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        text = findViewById(R.id.textView);
        messageView = findViewById(R.id.messageView);
        editText = findViewById(R.id.messageSend);
        mNewDevicesArrayAdapter = new ArrayAdapter<>(this, R.layout.device_name);
        messageListView = findViewById(R.id.buttons);
        messageListView.setAdapter(mNewDevicesArrayAdapter);
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
        initBluetooth();
    }

    private void initBluetooth() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    }

    private void makeConnection(Intent data) {
        Bundle extras = data.getExtras();
        if (extras == null) {
            return;
        }
        String address = extras.getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        text.setText("NIEWU: " + address);
        int counter = 0;
        mBluetoothAdapter.cancelDiscovery();
        myBluetoothService.join(device);
        text.setText("Waiting for connection");
        while (!myBluetoothService.isClientConnected()) {
            counter++;
            if (counter%100 == 0) {
                text.setText(text.getText() + ".");
            }

        }
        text.setText("Connected to: " + myBluetoothService.getHostName());
    }

    public void startHost(View view) {
        myBluetoothService = new BluetoothService(mHandler, true);
        buttonDis();
        clients = new ArrayList<>();
        Intent discoverableIntent =
                new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);
        myBluetoothService.host();

    }

    public void joinHost(View view) {
        myBluetoothService = new BluetoothService(mHandler, false);
        Intent serverIntent = new Intent(DemoBluetoothService.this, DeviceListActivity.class);
        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);

    }

    public void startCommunication(View view){
        mBluetoothAdapter.cancelDiscovery();
        myBluetoothService.stopClientDiscovery();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    makeConnection(data);
                }
                break;
        }
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
                    Log.i(TAG, "Writinng message : " + writeMessage);
                    mNewDevicesArrayAdapter.add("You:\n" + writeMessage);
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    int clientId = msg.arg2;
                    String clientName = clientId > -1 ? myBluetoothService.getConnectedClients().get(clientId).getName() : myBluetoothService.getHostName();
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    mNewDevicesArrayAdapter.add(clientName+ ":\n" + readMessage);
                    messageView.setText(readMessage);
                    Log.i(TAG, "Read message: " + readMessage);
                    break;
                case Constants.NEW_CONNECTION:
                    clients.add(myBluetoothService.getConnectedClients().toString());
                    String text = "Connected clients: " + clients.toString();
                    Log.i(TAG, "Displayed connections: " + text);
                    DemoBluetoothService.this.text.setText(text);
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
    }

}
