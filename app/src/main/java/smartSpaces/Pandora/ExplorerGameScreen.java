package smartSpaces.Pandora;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
//import androidx.recyclerview.widget.GridLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import pl.droidsonroids.gif.GifImageView;
import smartSpaces.Pandora.Game.Activities.WekaClassifierActivities;
import smartSpaces.Pandora.Game.Activities.WekaClassifierPicklock;
import smartSpaces.Pandora.Game.GameClient;
import smartSpaces.Pandora.Game.Map.ObjectType;
import smartSpaces.Pandora.Game.Panel;
import smartSpaces.Pandora.Game.Player;
import smartSpaces.Pandora.Game.Tasks.MotionActivityType;
import smartSpaces.Pandora.P2P.BluetoothServiceFragment;
import smartSpaces.Pandora.P2P.Constants;
import smartSpaces.Pandora.Picklock.R;

public class ExplorerGameScreen extends AppCompatActivity implements SensorEventListener {

    // controller stuff
//    private GameClient game;
    BluetoothServiceFragment fragment;
    private Player player;
    Context view;
    private Boolean gameStarted = false;
    public Boolean panelsFilled = false;
    public int lastScannedObject;

    // UI stuff
    public long TASKTIME = 30 * 1000;
    public long TASKTIME_INTERVAL = 100;
    public long N_PANELS = 4;
    private int EXPLORER_ROLE = 1;
    public String TAG = "Explorer: ";
    public String task;
    public Boolean hasTask = false;
    public Typeface horrorFont;
    public Typeface pixelFont;

    //NFC stuff
    NfcAdapter nfcAdapter;
    PendingIntent pendingIntent;
//    IntentFilter writeTagFilters[];
    Context context;
    public String coords = "";
    NFCReader reader = new NFCReader();

    //Activity Recognition meuk
    int STABLE_AMOUNT = 9;
    double maxValue;
    double maxCount;
    private boolean accChanged, gyroChanged, magnetoChanged;
    List<Double> activitystable = new ArrayList<>();
    private SensorManager sensorManager;
    private Sensor accelerometer, gyroscope;
    private double accXValue, accYValue, accZValue;
    private double gyroXvalue, gyroYValue, gyroZValue;


    //ALL VARIABLES FOR PICKLOCK
    int elapsedTime;
    long startTime;
    int flatTime = 0;
    int rightTime = 0;
    int middleTime = 0;
    int downTime = 0;
    int leftTime = 0;
    int ACTIVITY_RECOGNITION_LENGTH = 5;
    String activity;
    List picklock = new ArrayList();
    List<Double> picklockstable = new ArrayList<>();
    boolean lockpickbool = false;
    boolean done = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.explorer_wait_screen);

        //SENSORS
        //magnetometer could be uncommented if it wants to be used
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        //magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorManager.registerListener(this, accelerometer, sensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, gyroscope, sensorManager.SENSOR_DELAY_NORMAL);
        //sensorManager.registerListener(this, magnetometer, sensorManager.SENSOR_DELAY_NORMAL);

        //NFC stuff
        context = this;
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            // Stop here, we definitely need NFC
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
            finish();
        }
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);

        // controller stuff: ===================================
        this.player = new Player(false);

        FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
        fragment = new BluetoothServiceFragment(false, clientHandler, view);
        transaction.add(android.R.id.content, fragment);
        transaction.commit();
        Log.d(TAG, "onNewIntent: Create");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        //super.onNewIntent(intent);
        Log.d(TAG, "onNewIntent: " + intent.getAction());
        if(NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            coords = reader.readFromIntent(intent);
            String message = Constants.HEADER_LOCATION + Constants.MESSAGE_SEPARATOR + "0" + Constants.MESSAGE_SEPARATOR + coords;
            Log.d(TAG, "onNewIntent: " + message);
            fragment.sendMessage(message);
        }
    }

    @SuppressLint("HandlerLeak")
    private final Handler clientHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG, "handleMessage: Incoming msg: " + msg);

            switch (msg.what) {
                case Constants.MESSAGE_READ:
                    Log.d(TAG, "handleMessage: Incoming message: " + msg.obj);
                    readMessage((String) msg.obj);
                    break;
    //            case Constants.LOCATION_FOUND:
    //                locationFound((String) msg.obj);
    //
    //                break;
    //            case Constants.ACTIVITY_RECOGNIZED:
    //
    //                break;
            }
        }
    };

    /**
     * Read messages received from host
     * @param msg
     */
    private void readMessage(String msg) {
        String[] splittedMesssage = msg.split(Constants.MESSAGE_SEPARATOR);
        switch (splittedMesssage[0]) {
            case Constants.HEADER_TASK:
                handleReceiveTask(splittedMesssage);
                break;
            case Constants.HEADER_LOCATION:
                handleReceiveLocation(splittedMesssage);
                break;
            case Constants.HEADER_BUTTON:
                handleReceiveButtons(splittedMesssage);
                break;
            case Constants.HEADER_START:
                startGame();
                break;
            case Constants.HEADER_END:
                handleReceiveGameEnd(splittedMesssage);
                break;
        }
    }

    public void handleReceiveGameEnd(String[] msg) {
        String result = msg[1];
        if (result.equals(Constants.WIN)) {
            goToWin();
        } else if (result.equals(Constants.LOSE)) {
            goToLost();
        }
    }

    public void handleReceiveLocation(String[] msg) {
        lastScannedObject = Integer.parseInt(msg[1]);
    }

    public void handleReceiveTask(String[] msg) {
        String newTask = msg[1];

        if (hasTask) {
            taskCompleted();
        }

        if (gameStarted) {
            setTask(newTask);
        } else {
            task = newTask;
        }
    }

    public void handleReceiveButtons(String[] msg) {
        Log.d(TAG, "handleReceiveButtons: Received buttons...");
        String btnAmount = msg[1];
        Panel[] panels = new Panel[Integer.parseInt(btnAmount)];
        String list = msg[2];
        String[] listElement = list.split(Constants.MESSAGE_LIST_SEPARATOR);
        for (int i = 0; i < listElement.length; i ++) {
            String[] elementItems = listElement[i].split(Constants.MESSAGE_LIST_ELEMENT_SEPARATOR);
            String buttonId = elementItems[0];
            String verb = elementItems[1];
            String object = elementItems[2];
            Panel panel = new Panel(Integer.parseInt(buttonId), verb, object);
            panels[i] = panel;
        }
        player.setPanels(panels);
        Log.d(TAG, "handleReceiveButtons: Panels created: " + panels);
        Log.d(TAG, "handleReceiveButtons: panels received: " + player.getPanels());
        if (gameStarted) {
            fillPanels(player.getPanels());
            panelsFilled = true;
        }
    }

    public void startGame() {
        setContentView(R.layout.activity_explorer_game_screen);

        // initiate fonts
        this.pixelFont = Typeface.createFromAsset(getAssets(), "Fipps-Regular.otf");
        this.horrorFont = Typeface.createFromAsset(getAssets(), "buried-before.before-bb.ttf");
        TextView taskText = findViewById(R.id.task);
        taskText.setTypeface(pixelFont);

        Log.d(TAG, "startGame: panels: " + player.getPanels());
        if (player.getPanels() != null && player.getPanels().length > 0) {
            fillPanels(player.getPanels());
        }

        gameStarted = true;
        if (task != null) {
            setTask(task);
        }
    }

    /**
     * Set a new task, start task timer
     * @param task
     */
    public void setTask(String task) {
        hasTask = true;
        TextView taskView = findViewById(R.id.task);
        final ProgressBar progressBar = findViewById(R.id.progressBar);
        taskView.setText(task);

        if (task.equals(Constants.TASK_FLAG)) {
            GifImageView animation = findViewById(R.id.animation);
            animation.setImageResource(R.drawable.flaganimation);
        } else if (task.equals(Constants.TASK_SAFE)) {
            GifImageView animation = findViewById(R.id.animation);
            animation.setImageResource(R.drawable.safeanimation);
        }

        CountDownTimer taskTimer = new CountDownTimer(TASKTIME, TASKTIME_INTERVAL) {

            /**
             * For every tick update the progressbar
             */
            @Override
            public void onTick(long l) {
                int progress = (int) Math.round((((double) Long.valueOf(l)) / TASKTIME) * 100);
                progressBar.setProgress(progress);
            }

            /**
             * When task timer is finished, show completed or fail
             */
            @Override
            public void onFinish() {
                Log.d(TAG, "onFinish: TIME OUT! TASK FAILED...");
                String msg = Constants.HEADER_TASK + Constants.MESSAGE_SEPARATOR + "0" + Constants.MESSAGE_SEPARATOR + Constants.TASK_FAILED;
                fragment.sendMessage(msg);
                taskFailed();
                hasTask = false;
            }
        };

        taskTimer.start();
    }

    /**
     * Show bloodsplatters when task is failed
     */
    public void taskFailed() {
        ImageView blood1 = findViewById(R.id.blood1);
        ImageView blood2 = findViewById(R.id.blood2);
        ImageView blood3 = findViewById(R.id.blood3);

        blood1.setImageResource(R.drawable.bloodsplatter1);
        blood2.setImageResource(R.drawable.bloodsplatter2);
        blood3.setImageResource(R.drawable.bloodsplatter3);

        fadeOutImage(blood1);
        fadeOutImage(blood2);
        fadeOutImage(blood3);
    }

    /**
     * Show thumbsup when task is completed
     */
    public void taskCompleted() {
        ImageView thumbsUp = findViewById(R.id.thumbsup);
        thumbsUp.setImageResource(R.drawable.thumbsup);
        fadeOutImage(thumbsUp);
    }

    /**
     * Function for animating fade out of image
     * @param image
     */
    public void fadeOutImage(final ImageView image) {
        Animation aniFade = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fadeout);
        image.startAnimation(aniFade);

        aniFade.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                image.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
    }

    /**
     * Creates the panels and adds them to the UI
     */
    @SuppressLint("ClickableViewAccessibility")
    public void fillPanels(final Panel[] panels) {
        Button btn1 = findViewById(R.id.button1);
        Button btn2 = findViewById(R.id.button2);
        Button btn3 = findViewById(R.id.button3);
        Button btn4 = findViewById(R.id.button4);

        TextView desc1 = findViewById(R.id.description1);
        TextView desc2 = findViewById(R.id.description2);
        TextView desc3 = findViewById(R.id.description3);
        TextView desc4 = findViewById(R.id.description4);

        // set button texts
        btn1.setText(panels[0].getVerb());
        btn2.setText(panels[1].getVerb());
        btn3.setText(panels[2].getVerb());
        btn4.setText(panels[3].getVerb());

        // set description
        desc1.setText(panels[0].getObject());
        desc2.setText(panels[1].getObject());
        desc3.setText(panels[2].getObject());
        desc4.setText(panels[3].getObject());

        // set fonts
        btn1.setTypeface(horrorFont);
        btn2.setTypeface(horrorFont);
        btn3.setTypeface(horrorFont);
        btn4.setTypeface(horrorFont);

        btn1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.d(TAG, "onTouch: Button clicked");
                if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                    Log.d(TAG, "onTouch: Button pressed");
                    String msg = Constants.HEADER_BUTTON + Constants.MESSAGE_SEPARATOR + "0" + Constants.MESSAGE_SEPARATOR + panels[0].getId() + Constants.MESSAGE_SEPARATOR + Constants.PRESSED;
                    fragment.sendMessage(msg);
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    Log.d(TAG, "onTouch: Button released");
                    String msg = Constants.HEADER_BUTTON + Constants.MESSAGE_SEPARATOR + "0" + Constants.MESSAGE_SEPARATOR + panels[0].getId() + Constants.MESSAGE_SEPARATOR + Constants.RELEASED;
                    fragment.sendMessage(msg);
                }
                return false;
            }
        });

        btn2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                    String msg = Constants.HEADER_BUTTON + Constants.MESSAGE_SEPARATOR + "0" + Constants.MESSAGE_SEPARATOR + panels[1].getId() + Constants.MESSAGE_SEPARATOR + Constants.PRESSED;
                    fragment.sendMessage(msg);
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    String msg = Constants.HEADER_BUTTON + Constants.MESSAGE_SEPARATOR + "0" + Constants.MESSAGE_SEPARATOR + panels[1].getId() + Constants.MESSAGE_SEPARATOR + Constants.RELEASED;
                    fragment.sendMessage(msg);
                }
                return false;
            }
        });

        btn3.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                    String msg = Constants.HEADER_BUTTON + Constants.MESSAGE_SEPARATOR + "0" + Constants.MESSAGE_SEPARATOR + panels[2].getId() + Constants.MESSAGE_SEPARATOR + Constants.PRESSED;
                    fragment.sendMessage(msg);
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    String msg = Constants.HEADER_BUTTON + Constants.MESSAGE_SEPARATOR + "0" + Constants.MESSAGE_SEPARATOR + panels[2].getId() + Constants.MESSAGE_SEPARATOR + Constants.RELEASED;
                    fragment.sendMessage(msg);
                }
                return false;
            }
        });

        btn4.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                    String msg = Constants.HEADER_BUTTON + Constants.MESSAGE_SEPARATOR + "0" + Constants.MESSAGE_SEPARATOR + panels[3].getId() + Constants.MESSAGE_SEPARATOR + Constants.PRESSED;
                    fragment.sendMessage(msg);
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    String msg = Constants.HEADER_BUTTON + Constants.MESSAGE_SEPARATOR + "0" + Constants.MESSAGE_SEPARATOR + panels[3].getId() + Constants.MESSAGE_SEPARATOR + Constants.RELEASED;
                    fragment.sendMessage(msg);
                }
                return false;
            }
        });
    }

    public void goToWin() {
        Intent intent = new Intent(this, WinScreen.class);
        intent.putExtra("role", EXPLORER_ROLE);
        startActivity(intent);
    }

    public void goToLost() {
        Intent intent = new Intent(this, LostScreen.class);
        intent.putExtra("role", EXPLORER_ROLE);
        startActivity(intent);
    }

    @Override
    public void onPause(){
        super.onPause();
        if (nfcAdapter!=null) {
            nfcAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // TODO: is always trying to recognize activity when it has last scanned an object. --> fix?
        if (gameStarted) {
            if(lastScannedObject == ObjectType.LOCK.getResource()){
                if(lockpickbool == false){
                    lockpickbool = true;
                    Lockpicker();
                }
                checkSensorsChanged(event);
                if (accChanged && gyroChanged ) {
                    Double[] arrayPicklock = getSensorData();
                    changedSensorActivity(arrayPicklock);

                    try{
                        double resultpick = WekaClassifierPicklock.classify(arrayPicklock);
                        if(picklockstable.size() == 0){
                            startTime = System.nanoTime();
                        }
                        picklockstable.add(resultpick);
                        if(picklockstable.size() == STABLE_AMOUNT){
                            String textresultpicklock;
                            elapsedTime = (int)((System.nanoTime() - startTime)/1000000);
                            resultpick = getBestClassifiedActivity();

                            if(resultpick == 0.0){
                                textresultpicklock = "Left";
                                flatTime = 0;
                                rightTime = 0;
                                middleTime = 0;
                                downTime = 0;
                                leftTime= leftTime + elapsedTime;
                                picklockstable.clear();
                                if(activity.equals(textresultpicklock)){
                                    Lockpickerresult(leftTime);
                                }
                            }else if(resultpick == 1.0) {
                                textresultpicklock = "Down";
                                flatTime = 0;
                                rightTime = 0;
                                middleTime = 0;
                                leftTime = 0;
                                downTime= downTime + elapsedTime;
                                picklockstable.clear();
                                if(activity.equals(textresultpicklock)){
                                    Lockpickerresult(downTime);
                                }
                            }else if(resultpick == 2.0) {
                                textresultpicklock = "Flat";
                                rightTime = 0;
                                middleTime = 0;
                                leftTime = 0;
                                downTime = 0;

                                flatTime= flatTime + elapsedTime;
                                picklockstable.clear();
                                if(activity.equals(textresultpicklock)){
                                    Lockpickerresult(flatTime);
                                }
                            }else if(resultpick == 3.0) {
                                textresultpicklock = "Right";
                                middleTime = 0;
                                leftTime = 0;
                                downTime = 0;
                                flatTime = 0;


                                rightTime= rightTime + elapsedTime;
                                picklockstable.clear();
                                if(activity.equals(textresultpicklock)){
                                    Lockpickerresult(rightTime);
                                }
                            }else if(resultpick == 4.0) {
                                rightTime = 0;
                                leftTime = 0;
                                downTime = 0;
                                flatTime = 0;
                                textresultpicklock = "Middle";
                                middleTime= middleTime + elapsedTime;
                                picklockstable.clear();
                                if(activity.equals(textresultpicklock)){
                                    Lockpickerresult(middleTime);
                                }
                            }else{
                                picklockstable.clear();
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            } else {
                checkSensorsChanged(event);
                changedSensorActivity();
            }
        }
    }

    private void changedSensorActivity() {
        if (accChanged && gyroChanged ){
            Double[] array = getSensorData();

            try{
                classifyData(array);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void changedSensorActivity(Double[] sensordata) {
        try{
            classifyData(sensordata);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void classifyData(Double[] array) throws Exception {
        double result = WekaClassifierActivities.classify(array);
        activitystable.add(result);
        
        if( activitystable.size() == STABLE_AMOUNT) {
            result = getBestClassifiedActivity();
            String msg;
            if (result == 0.0 && lastScannedObject == ObjectType.ROPE.getResource()) {
                msg = Constants.HEADER_TASK + Constants.MESSAGE_SEPARATOR + "0" + Constants.MESSAGE_SEPARATOR + MotionActivityType.RAISE_FLAG.getResource();
            } else if (result == 1.0) {
                msg = Constants.HEADER_TASK + Constants.MESSAGE_SEPARATOR + "0" + Constants.MESSAGE_SEPARATOR + MotionActivityType.HOLD_IN_PLACE.getResource();
            } else if (result == 2.0) {
                msg = Constants.HEADER_TASK + Constants.MESSAGE_SEPARATOR + "0" + Constants.MESSAGE_SEPARATOR + MotionActivityType.SHAKE_PHONE.getResource();
            } else if (result == 3.0) {
                msg = Constants.HEADER_TASK + Constants.MESSAGE_SEPARATOR + "0" + Constants.MESSAGE_SEPARATOR + MotionActivityType.PIROUETTE.getResource();
            } else {
                return;
            }

            fragment.sendMessage(msg);
            activitystable.clear();
        }
    }

    private double getBestClassifiedActivity() {
        double result;
        for (int i = 0; i < activitystable.size(); ++i) {
            int count = 0;
            for (int j = 0; j < activitystable.size(); ++j) {
                if (activitystable.get(j) == activitystable.get(i)) ++count;
            }
            if (count > maxCount) {
                maxCount = count;
                maxValue = activitystable.get(i);
            }
        }

        result = maxValue;
        maxValue = -1;
        maxCount = -1;
        return result;
    }

    private Double[] getSensorData() {
        accChanged = false;
        gyroChanged = false;
        Double array [] = new Double[6];
        array[0] = accXValue;
        array[1] = accYValue;
        array[2] = accZValue;
        array[3] = gyroXvalue;
        array[4] = gyroYValue;
        array[5] = gyroZValue;

        return array;
    }

    private void checkSensorsChanged(SensorEvent event) {
//        Log.d(TAG, "onSensorChanged: sensor: " + Sensor.TYPE_ACCELEROMETER);
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){

            this.accXValue = event.values[0];
            this.accYValue = event.values[1];
            this.accZValue = event.values[2];
            accChanged = true;
//            Log.d(TAG, "startTracking: sensordata changed: x: " + this.accXValue + ", y: " + this.accYValue + ", z: " + this.accZValue);
        } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            this.gyroXvalue = event.values[0];
            this.gyroYValue = event.values[1];
            this.gyroZValue = event.values[2];
            gyroChanged = true;
        }
    }


    public void Vibrate(){
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(500);
        }
    }

    public void Lockpicker(){
        picklock = randomActions(randomNumber());
        activity = picklock.get(0).toString();
    }

    public void Lockpickerresult(int time) {
        if (ACTIVITY_RECOGNITION_LENGTH < time) {
            picklock.remove(0);
            Vibrate();
            if (picklock.size() != 0) {
                activity = picklock.get(0).toString();
            } else {
                String msg = Constants.HEADER_TASK + Constants.MESSAGE_SEPARATOR + "0" + Constants.MESSAGE_SEPARATOR + MotionActivityType.PICK_LOCK.getResource();
                fragment.sendMessage(msg);
                lockpickbool = false;
                done = true;
            }
        }
    }

    public int randomNumber() {
        int randomInt = new Random().nextInt(4) + 2;
        return randomInt;
    }

    public List randomActions(int amount){
        List<String> myList = new ArrayList<String>();
        myList.add("Flat");
        myList.add("Right");
        myList.add("Middle");
        myList.add("Left");
        myList.add("Down");
        List picklockActivity = new ArrayList();
        int lastRand = 300;
        for (int i = 0; i < amount; i++) {
            int rand = new Random().nextInt(myList.size());
            while (rand == lastRand) {
                rand = new Random().nextInt(myList.size());
            }
            picklockActivity.add(myList.get(rand));
        }
        return picklockActivity;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }
}
