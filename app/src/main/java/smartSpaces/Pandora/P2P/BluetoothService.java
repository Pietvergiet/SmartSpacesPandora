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
public class BluetoothService {
    private static final String TAG = "BService";
    private Handler handler; // handler that gets info from Bluetooth service

    private final static int MAX_CONNECTIONS = 5;
    private final static java.util.UUID MY_UUID = java.util.UUID.fromString("bb807b74-dc2f-4f70-8c05-c101e7d55db7");
    private final static String NAME = "PANDORA";

    private boolean isServer;
    private int connectionAmount = 0;
    private AcceptThread aThread;
    private ConnectThread cThread;
    private ConnectedThread clientConnection;
    private HashMap<Integer, ConnectedThread> serverConnections;
    private HashMap<String, Integer> serverConnectionIds;
    private BluetoothAdapter mBluetoothAdapter;

    private int mState;

    private static final int STATE_NONE = 0;       // we're doing nothing
    private static final int STATE_LISTEN = 1;     // now listening for incoming connections
    private static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    private static final int STATE_CONNECTED = 3;  // now connected to a remote device
    // Defines several constants used when transmitting messages between the
    // service and the UI.
    private interface MessageConstants {
        public static final int MESSAGE_READ = 0;
        public static final int MESSAGE_WRITE = 1;
        public static final int MESSAGE_TOAST = 2;
        public static final int NEW_CONNECTION = 3;

        // ... (Add other message types here as needed.)
    }

    /**
     * Constructor which indicates whether this instance of
     * the class will behave as a server or a client
     * @param s true if server, false if client
     */
    public BluetoothService(Handler h, boolean s) {
        isServer = s;
        handler = h;
        mState = STATE_NONE;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public synchronized int getState(){
        return mState;
    }

    /**
     * Starts the bluetooth server thread.
     * This will listen for incoming connections after which communication is possible.
     * A maximum of 5 clients can be connected.
     */
    public synchronized void host() {
        serverConnections = new HashMap<>();
        serverConnectionIds = new HashMap<>();
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
     * Write to the {@link ConnectedThread}
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
     * Write to the specified {@link ConnectedThread}
     * Won't send anything if the id does not exist.
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
        if (r != null) {
            r.write(out);
        }
    }

    /**
     * Starts communication between bluetooth sockets.
     * Makes an instance of {@link ConnectedThread}
     * which manages the Bluetooth connection.
     * @param socket The socket
     */
    public synchronized void startConnection(BluetoothSocket socket) {
        mState = STATE_CONNECTED;
        if (isServer){
            connectionAmount++;
            if (connectionAmount <= MAX_CONNECTIONS){
                BluetoothSocket tmpS = socket;
                ConnectedThread tmp = new ConnectedThread(tmpS);
                tmp.start();
                serverConnections.put(socket.getRemoteDevice().hashCode(), tmp);
                serverConnectionIds.put(socket.getRemoteDevice().getAddress(), socket.getRemoteDevice().hashCode());
            }
        } else {
            if (cThread != null) {
                cThread.cancel();
                cThread = null;
            }

            if (clientConnection != null) {
                clientConnection.cancel();
                clientConnection = null;
            }

            BluetoothSocket tmpS = socket;
            clientConnection = new ConnectedThread(tmpS);
            clientConnection.start();
        }
    }

    /**
     * Stops waiting for new connections
     */
    public synchronized void stopClientDiscovery() {
        if (aThread != null) {
            aThread.cancel();
            aThread = null;
        }
    }

    /**
     * Checks if the client is connected to a remote device.
     * @return true if the Socket is connected
     */
    public boolean isClientConnected() {
        if (clientConnection != null) {
            return clientConnection.mmSocket.isConnected();
        }
        return false;
    }

    /**
     * Return the name of the remote Bluetooth device the client is connected to.
     * @return The name of the remote Bluetooth device
     */
    public String getHostName() {
        if (isClientConnected()) {
            return clientConnection.mmSocket.getRemoteDevice().getName();
        }
        return null;
    }

    /**
     * Returns a list of connected Bluetooth devices
     * @return ArrayList of connected clients
     */
    public synchronized HashMap<Integer, BluetoothDevice> getConnectedClients(){
        HashMap<Integer, BluetoothDevice> connectedDevices = new HashMap<>();
        if (serverConnections != null && !serverConnections.isEmpty()){
            for (Map.Entry<Integer, ConnectedThread> sc : serverConnections.entrySet()){
                if(sc.getValue().mmSocket.isConnected()) {
                    connectedDevices.put(sc.getKey(), sc.getValue().mmSocket.getRemoteDevice());
                }
            }
        }
        return connectedDevices;
    }

    /**
     * Stops all threads
     */
    public synchronized void stop(){
        Log.d(TAG, "Stop");
        if (aThread != null) {
            Log.d(TAG, "Stop athread");
            aThread.cancel();
            aThread = null;
        }

        if (cThread != null) {
            cThread.cancel();
            cThread = null;
        }

        if (serverConnections != null) {
            for (Map.Entry<Integer, ConnectedThread> sc : serverConnections.entrySet()) {
                if (sc.getValue() != null) {
                    sc.getValue().cancel();
                    serverConnections.remove(sc.getKey());
                }
            }
            serverConnections.clear();
            serverConnections = null;
        }

        if (clientConnection != null) {
            clientConnection.cancel();
            clientConnection = null;
        }

        mState = STATE_NONE;
    }

    /**
     * Indicate that the connection attempt failed and notify the UI Activity.
     */
    private void connectionFailed() {
        // Send a failure message back to the Activity
        Log.i(TAG, "CONNEction failed");
//        Message msg = handler.obtainMessage(Constants.MESSAGE_TOAST);
//        Log.i(TAG, "CONNEction failed22");
//        Bundle bundle = new Bundle();
//        bundle.putString(Constants.TOAST, "Unable to connect device");
//        msg.setData(bundle);
        Message msg = handler.obtainMessage(Constants.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TOAST, "Unable to connect device");
        msg.setData(bundle);
//        handler.sendMessage(msg);
//        Message msg2 = handler.obtainMessage(Constants.MESSAGE_READ, "CONNECTION FAILED".getBytes());
//        msg2.sendToTarget();
        Log.i(TAG, "CONNEction failed2");
        mState = STATE_NONE;

    }

    /**
     * Indicate that the connection was lost and notify the UI Activity.
     */
    private void connectionLost() {
        // Send a failure message back to the Activity
        Message msg = handler.obtainMessage(Constants.MESSAGE_TOAST, "Device connection was lost");
//        Bundle bundle = new Bundle();
//        bundle.putString(Constants.TOAST, "Device connection was lost");
//        msg.setData(bundle);
//        handler.sendMessage(msg);
        msg.sendToTarget();

        mState = STATE_NONE;
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
            mState = STATE_LISTEN;
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
                    synchronized (BluetoothService.this) {
                        // A connection was accepted. Perform work associated with
                        // the connection in a separate thread.
                        startConnection(socket);
                        Message readMsg = handler.obtainMessage(
                                Constants.NEW_CONNECTION, socket.getRemoteDevice().hashCode());
                        readMsg.sendToTarget();
                        Log.i(TAG, "New connection with: " + socket.getRemoteDevice().getName());
//                    manageMyConnectedSocket(socket);
//                        tekst.setText("Connected!!!");
                        socket = null;
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
            mState = STATE_CONNECTING;
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
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
                connectionFailed();
                return;
            }

            // Reset the ConnectThread because we're done
            synchronized (BluetoothService.this) {
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
            mState = STATE_CONNECTED;
        }

        public void run() {
            mmBuffer = new byte[1024];
            int numBytes; // bytes returned from read()
            int clientId;

            // Keep listening to the InputStream until an exception occurs.
            while (mState == STATE_CONNECTED) {
                try {
                    // Read from the InputStream.
                    numBytes = mmInStream.read(mmBuffer);

                    clientId = isServer ? serverConnectionIds.get(mmSocket.getRemoteDevice().getAddress()) : -1;
                    // Send the obtained bytes to the UI activity.
                    Message readMsg = handler.obtainMessage(
                            Constants.MESSAGE_READ, numBytes, clientId,
                            mmBuffer);
                    readMsg.sendToTarget();
                } catch (IOException e) {
                    Log.d(TAG, "Input stream was disconnected", e);
                    connectionLost();
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
                        Constants.MESSAGE_WRITE, -1, -1, bytes);
                writtenMsg.sendToTarget();
                Thread.sleep(100);
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
            } catch (InterruptedException e) {
                e.printStackTrace();
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
