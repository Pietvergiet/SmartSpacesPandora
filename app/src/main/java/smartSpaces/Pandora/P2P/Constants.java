package smartSpaces.Pandora.P2P;

public interface Constants {

    // Message types sent from the BluetoothChatService Handler
    int MESSAGE_STATE_CHANGE = 1;
    int MESSAGE_READ = 2;
    int MESSAGE_WRITE = 3;
    int MESSAGE_DEVICE_NAME = 4;
    int MESSAGE_TOAST = 5;
    int NEW_CONNECTION = 6;

    // Key names received from the BluetoothChatService Handler
    String DEVICE_NAME = "device_name";
    String TOAST = "toast";

    String HEADER_TASK = "T";
    String HEADER_LOCATION = "L";
    String HEADER_BUTTON = "B";

    String MESSAGE_SEPARATOR = "-";
    String MESSAGE_LIST_START = "[";
    String MESSAGE_LIST_SEPARATOR = ",";
    String MESSAGE_LIST_END = "]";
}
