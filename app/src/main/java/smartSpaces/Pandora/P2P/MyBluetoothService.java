package smartSpaces.Pandora.P2P;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * This class does all the work for setting up and managing Bluetooth
 * connections with other devices. It has a thread that listens for
 * incoming connections, a thread for connecting with a device, and a
 * thread for performing data transmissions when connected.
 */
public class MyBluetoothService {
    private static final String TAG = "BService";
    private Handler handler; // handler that gets info from Bluetooth service

    private final static int MAX_CONNECTIONS = 5;
    private final static int DISCOVERABLE_BT_REQUEST_CODE = 2;
    private final static int DISCOVERABLE_DURATION = 3600;
    private final static String TEMP_SERVER_NAME = "PANDORA GAME";
    private final static java.util.UUID MY_UUID = java.util.UUID.fromString("bb807b74-dc2f-4f70-8c05-c101e7d55db7");
    private final static String NAME = "PANDORA";
    private static final int REQUEST_COARSE_LOCATION = 300;

    private boolean isServer;
    private int connectionAmount = 0;
    private AcceptThread aThread;
    private ConnectThread cThread;
    private ConnectedThread clientConnection;
    private HashMap<Integer, ConnectedThread> serverConnections;
    private BluetoothAdapter mBluetoothAdapter;


    // Defines several constants used when transmitting messages between the
    // service and the UI.
    private interface MessageConstants {
        public static final int MESSAGE_READ = 0;
        public static final int MESSAGE_WRITE = 1;
        public static final int MESSAGE_TOAST = 2;

        // ... (Add other message types here as needed.)
    }

    /**
     * Constructor which indicates whether this instance of
     * the class will behave as a server or a client
     * @param s true if server, false if client
     */
    public MyBluetoothService(Handler h, boolean s) {
        isServer = s;
        handler = h;
    }

    /**
     * Starts the bluetooth server thread.
     * This will listen for incoming connections after which communication is possible.
     * A maximum of 5 clients can be connected.
     */
    public synchronized void host() {
        serverConnections = new HashMap<>();
        aThread = new AcceptThread();
        aThread.start();
    }

    /**
     * Start the bluetooth client thread.
     * This will connect to the specified device after which communication is possible.
     * @param device The remote bluetooth device
     */
    public synchronized void join(BluetoothDevice device) {
        cThread = new ConnectThread(device);
        cThread.start();
    }

    /**
     * Write to the ConnectedThread
     * If {@link #isServer} is true this will write to all connected devices
     * Use {@link #write(byte[], int)} to write to a specific device
     * @param out The bytes to write
     */
    public void write(byte[] out) {
        // Create temporary object

        // Synchronize a copy of the ConnectedThread
        if (isServer) {
            for (Map.Entry<Integer, ConnectedThread> c : serverConnections.entrySet()) {
                ConnectedThread r;
                synchronized (this) {
//                  if (mState != STATE_CONNECTED) return;
                    r = c.getValue();
                }
                r.write(out);
            }
        } else {
            ConnectedThread r;
            synchronized (this) {
//            if (mState != STATE_CONNECTED) return;
                r = clientConnection;
            }

            // Perform the write unsynchronized
            r.write(out);
        }

    }

    /**
     * Write to the specified ConnectedThread
     * Use {@link #write(byte[])} to send to all connected devices.
     * @param out The bytes to write
     * @param id The id of the device
     */
    public void write(byte[] out, int id) {
        // Create temporary object
        ConnectedThread r;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
//            if (mState != STATE_CONNECTED) return;
            r = serverConnections.get(id);
        }
        // Perform the write unsynchronized
        r.write(out);
    }

    /**
     * Starts communication between bluetooth sockets.
     * Makes an instance of {@link ConnectedThread}
     * which manages the Bluetooth connection.
     * @param socket The socket
     */
    public synchronized void startConnection(BluetoothSocket socket) {
        if (isServer){
            connectionAmount++;
            if (connectionAmount <= MAX_CONNECTIONS){
                ConnectedThread tmp = new ConnectedThread(socket);
                tmp.start();
                serverConnections.put(connectionAmount, tmp);
            }
            aThread.cancel();
        } else {
            clientConnection = new ConnectedThread(socket);
            clientConnection.start();
            cThread.cancel();
        }
    }

    /**
     * Stops all threads
     */
    public synchronized void stop(){
        Log.d(TAG, "Stop");
        aThread.cancel();
        aThread = null;

        cThread.cancel();
        cThread = null;

        for (Map.Entry<Integer, ConnectedThread> sc: serverConnections.entrySet()){
            sc.getValue().cancel();
            serverConnections.remove(sc.getKey());
        }
        serverConnections.clear();
        serverConnections = null;

        clientConnection.cancel();
        clientConnection = null;

    }

    /**
     * Server thread
     */
    private class AcceptThread extends Thread {

        private BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            // Use a temporary object that is later assigned to mmServerSocket
            // because mmServerSocket is final.
            BluetoothServerSocket tmp = null;
            try {
                // MY_UUID is the app's UUID string, also used by the client code.
                tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
            } catch (IOException e) {
                Log.i(TAG, "Socket's listen() method failed");
            }
            mmServerSocket = tmp;
        }

        public void run() {
            BluetoothSocket socket = null;
            // Keep listening until exception occurs or a socket is returned.
            while (true) {
                try {
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    Log.i(TAG, "Socket's accept() method failed");

                }

                if (socket != null) {
                    synchronized (MyBluetoothService.this) {
                        // A connection was accepted. Perform work associated with
                        // the connection in a separate thread.
                        startConnection(socket);
                        Log.i(TAG, "New connection with: " + socket.getRemoteDevice().getName());
//                    manageMyConnectedSocket(socket);
//                        tekst.setText("Connected!!!");
                    }
                }
            }
        }

        // Closes the connect socket and causes the thread to finish.
        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the connect socket", e);
            }
        }
    }

    /**
     * Client thread
     */
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            BluetoothSocket tmp = null;
            mmDevice = device;

            try {
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                // MY_UUID is the app's UUID string, also used in the server code.
                tmp = mmDevice.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                Log.e(TAG, "Socket's create() method failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it otherwise slows down the connection.
            mBluetoothAdapter.cancelDiscovery();
            Log.i(TAG,"STart connectiong");
            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                mmSocket.connect();
            } catch (IOException connectException) {
                Log.i(TAG,"Shit is stukkkkk");
                // Unable to connect; close the socket and return.
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    Log.i(TAG,"Shit is stuk");
                    Log.e(TAG,"Could not close the client socket", closeException);
                }
                return;
            }

            // Reset the ConnectThread because we're done
            synchronized (MyBluetoothService.this) {
                cThread = null;
            }

            startConnection(mmSocket);
            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
//            manageMyConnectedSocket(mmSocket);
        }

        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the client socket", e);
            }
        }
    }

    /**
     * Communication manager thread
     */
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private byte[] mmBuffer; // mmBuffer store for the stream

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams; using temp objects because
            // member streams are final.
            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating input stream", e);
            }
            try {
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating output stream", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            mmBuffer = new byte[1024];
            int numBytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                try {
                    // Read from the InputStream.
                    numBytes = mmInStream.read(mmBuffer);
                    // Send the obtained bytes to the UI activity.
                    Message readMsg = handler.obtainMessage(
                            MessageConstants.MESSAGE_READ, numBytes, -1,
                            mmBuffer);
                    readMsg.sendToTarget();
                } catch (IOException e) {
                    Log.d(TAG, "Input stream was disconnected", e);
                    break;
                }
            }
        }

        // Call this from the main activity to send data to the remote device.
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);

                // Share the sent message with the UI activity.
                Message writtenMsg = handler.obtainMessage(
                        MessageConstants.MESSAGE_WRITE, -1, -1, mmBuffer);
                writtenMsg.sendToTarget();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when sending data", e);

                // Send a failure message back to the activity.
                Message writeErrorMsg =
                        handler.obtainMessage(MessageConstants.MESSAGE_TOAST);
                Bundle bundle = new Bundle();
                bundle.putString("toast",
                        "Couldn't send data to the other device");
                writeErrorMsg.setData(bundle);
                handler.sendMessage(writeErrorMsg);
            }
        }

        // Call this method from the main activity to shut down the connection.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the connect socket", e);
            }
        }
    }
}
