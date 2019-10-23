package smartSpaces.Pandora.P2P;

public interface Constants {

    // Message types sent from the BluetoothChatService Handler
    int REQUEST_CONNECT_DEVICE = 1;
    int MESSAGE_READ = 2;
    int MESSAGE_WRITE = 3;
    int MESSAGE_DEVICE_NAME = 4;
    int MESSAGE_TOAST = 5;
    int NEW_CONNECTION = 6;

    int LOCATION_FOUND = 7;
    int ACTIVITY_RECOGNIZED = 8;

    int INSTRUCTION_DONE = 9;

    String HEADER_TASK = "T";
    String HEADER_LOCATION = "L";
    String HEADER_BUTTON = "B";

    String MESSAGE_SEPARATOR = "-";
    String MESSAGE_LIST_START = "[";
    String MESSAGE_LIST_SEPARATOR = ",";
    String MESSAGE_LIST_END = "]";
    String TOAST = "toast";
}
