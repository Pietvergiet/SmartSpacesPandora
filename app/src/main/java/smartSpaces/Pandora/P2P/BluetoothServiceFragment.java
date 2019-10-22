package smartSpaces.Pandora.P2P;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import java.util.ArrayList;

import smartSpaces.Pandora.Game.Tasks.Task;

import static android.content.pm.PackageManager.PERMISSION_DENIED;

public class BluetoothServiceFragment extends Fragment {
    private static final String TAG = "BluetoothChatFragment";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 3;

    private BluetoothService myBluetoothService;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean isHost;
    private ArrayList<String> clients;

    public BluetoothServiceFragment(boolean isHost){
        this.isHost = isHost;
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

        if(isHost){
            startHost();
        } else {
            startClient();
        }
    }

    private void startHost() {
        myBluetoothService = new BluetoothService(mHandler, true);
        clients = new ArrayList<>();
        Intent discoverableIntent =
                new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);
        myBluetoothService.host();
    }

    private void startClient() {
        myBluetoothService = new BluetoothService(mHandler, false);
        Intent serverIntent = new Intent(getActivity(), DeviceListActivity.class);
        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
    }

    private void checkGetPermissions() {
        FragmentActivity activity = getActivity();
        int checkCoarse = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION);
        int MY_PERMISSIONS_REQUEST = 200;
        int checkFine =ContextCompat.checkSelfPermission (activity,Manifest.permission.ACCESS_FINE_LOCATION);
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
    private final Handler mHandler = new Handler() {

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
                    break;
                case Constants.NEW_CONNECTION:
                    clients.add(myBluetoothService.getConnectedClients().toString());
                    String text = "Connected clients: " + clients.toString();
                    Log.i(TAG, "Displayed connections: " + text);
                    break;
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (myBluetoothService != null){
            myBluetoothService.stop();
            myBluetoothService = null;
        }
    }
}
