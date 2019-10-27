package smartSpaces.Pandora.P2P;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import smartSpaces.Pandora.Game.Tasks.Task;
import smartSpaces.Pandora.Picklock.R;

import static android.content.pm.PackageManager.PERMISSION_DENIED;

public class BluetoothServiceFragment extends Fragment {
    private static final String TAG = "BluetoothChatFragment";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 3;

    private BluetoothService myBluetoothService;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean isHost;
    /**
     * Will always be filled regardless if the service is host or client.
     */
    private HashMap<Integer, String> clients;
    private Handler cHandler;
    private Context curContext;
    private boolean isStarted = false;

    public BluetoothServiceFragment(boolean isHost, Handler handler, Context context) {
        this.isHost = isHost;
        cHandler = handler;
        curContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentActivity activity = getActivity();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null && activity != null) {
            Toast.makeText(activity, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            activity.finish();
        }


    }

    @Override
    public void onStart() {
        super.onStart();

        if (mBluetoothAdapter == null) {
            return;
        }

        if (!mBluetoothAdapter.isEnabled()) {
            // We need to enable the Bluetooth, so we ask the user
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            mBluetoothAdapter.enable();

        }

        checkGetPermissions();

        while (!mBluetoothAdapter.isEnabled()) {

        }
        if (!isStarted) {
            if (isHost) {
                Log.i(TAG, "START HOST");
                startHost();
            } else {
                Log.i(TAG, "START CLIENT");
                startClient();
            }
        }


    }

    /**
     * Starts the a {@link BluetoothService} as host and edits the view in the start screen.
     */
    private void startHost() {
        isStarted = true;
        myBluetoothService = new BluetoothService(bHandler, true);
        clients = new HashMap<>();
        Intent discoverableIntent =
                new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);
        myBluetoothService.host();
        ((Activity) curContext).findViewById(R.id.modal_start).setVisibility(View.VISIBLE);
    }

    /**
     * Starts a {@link BluetoothService} as a client and start a {@link DeviceListActivity}.
     */
    private void startClient() {
        isStarted = true;
        myBluetoothService = new BluetoothService(bHandler, false);
        Intent serverIntent = new Intent(getActivity(), DeviceListActivity.class);
        startActivityForResult(serverIntent, Constants.REQUEST_CONNECT_DEVICE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "ONACTIVITY CALLED");
        switch (requestCode) {
            case Constants.REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    Log.i(TAG, "Get HOST ADDRESS123");
                    makeConnection(data);
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    Log.i(TAG, "RESULT CANCELED");
                }

                break;
        }
    }

    /**
     * Uses the data returned from {@link DeviceListActivity} to connect to the host and edits the view in the home screen.
     * @param data The MAC address of the host.
     */
    private void makeConnection(Intent data) {
        //TODO Acties
        Bundle extras = data.getExtras();
        if (extras == null) {
            return;
        }
        String address = extras.getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);


        int counter = 0;
        mBluetoothAdapter.cancelDiscovery();
        myBluetoothService.join(device);
        while (!myBluetoothService.isClientConnected()) {
            counter++;
            if (counter % 100 == 0) {
            }

        }
    }

    /**
     * Sends a message to all connected Bluetooth devices.
     * @param message The message
     */
    public void sendMessage(String message) {
        myBluetoothService.write(message.getBytes());
    }

    /**
     * Sends a message to a specific connected Bluetooth device.
     * @param message The message
     * @param id The internal id of the connected Bluetooth device.
     */
    public void sendMessage(String message, int id) {
        myBluetoothService.write(message.getBytes(), id);
    }

    public Set<Integer> getClientIds() {
        return clients.keySet();
    }

    /**
     * Checks if all required permissions are giving and otherwise ask for those permissions.
     */
    private void checkGetPermissions() {
        FragmentActivity activity = getActivity();
        int checkCoarse = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION);
        int MY_PERMISSIONS_REQUEST = 200;
        int checkFine = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);
        if (checkFine == PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST);
        }
        if (checkCoarse == PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST);
        }
    }

    @SuppressLint("HandlerLeak")
    private final Handler bHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    Log.i(TAG, "Writinng message : " + writeMessage);
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    int clientId = msg.arg2;
                    String clientName = clientId > -1 ? myBluetoothService.getConnectedClients().get(clientId).getName() : myBluetoothService.getHostName();
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    Log.i(TAG, "Read message: " + readMessage);
                    Message readMsg = cHandler.obtainMessage(
                            Constants.MESSAGE_DEVICE_NAME, clientId, -1, readMessage);
                    readMsg.sendToTarget();
                    Log.i(TAG, "Read message: " + readMessage);
                    break;
                case Constants.NEW_CONNECTION:
                    for (Map.Entry<Integer, BluetoothDevice> bd : myBluetoothService.getConnectedClients().entrySet()) {
                        clients.put(bd.getKey(), bd.getValue().getName());
                    }
                    String text = "Connected clients: " + clients.toString();
                    Log.i(TAG, "Displayed connections: " + text);
                    if (isHost) {
                        Message conMessage = cHandler.obtainMessage(
                                Constants.NEW_CONNECTION, clients.size());
                        conMessage.sendToTarget();
                    }
                    break;
            }
        }
    };

    @Override
    public void onDestroy() {
        Log.i(TAG, "FRAGMENT IS GONE");
        super.onDestroy();
        if (myBluetoothService != null) {
            myBluetoothService.stop();
            myBluetoothService = null;
        }
        isStarted = false;
    }
}
