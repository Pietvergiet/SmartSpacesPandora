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

    String TASK_FLAG = "Raise a flag";
    String TASK_SAFE = "Open a safe";

    String TASK_FAILED = "Failed";

    String ACTIVITY_LOCKPICKING = "lockpicked";
    String ACTIVITY_FLAG = "flag";
    String ACTIVITY_SHAKE = "shake";
    String ACTIVITY_PIROUETTE = "pirouette";
    String ACTIVITY_STILL = "still";

    String OBJECT_LOCK = "lock";

    String PRESSED = "Pressed";
    String RELEASED = "Released";

    String WIN = "win";
    String LOSE = "lose";

    String HEADER_TASK = "T";
    String HEADER_LOCATION = "L";
    String HEADER_BUTTON = "B";
    String HEADER_START = "S";
    String HEADER_END = "E";
    String HEADER_HAZARD = "H";

    String MESSAGE_SEPARATOR = "-";
    String MESSAGE_LIST_START = "[";
    String MESSAGE_LIST_SEPARATOR = ",";
    String MESSAGE_LIST_ELEMENT_SEPARATOR = "+";
    String MESSAGE_LIST_END = "]";
    String TOAST = "toast";
}
