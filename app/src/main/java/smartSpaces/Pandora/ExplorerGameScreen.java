package smartSpaces.Pandora;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
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
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.InputFilter;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import pl.droidsonroids.gif.GifImageView;
import smartSpaces.Pandora.Game.Activities.WekaClassifierActivities;
import smartSpaces.Pandora.Game.Activities.WekaClassifierPicklock;
import smartSpaces.Pandora.Game.GameClient;
import smartSpaces.Pandora.Game.Panel;
import smartSpaces.Pandora.Game.Player;
import smartSpaces.Pandora.P2P.BluetoothService;
import smartSpaces.Pandora.P2P.BluetoothServiceFragment;
import smartSpaces.Pandora.P2P.Constants;
import smartSpaces.Pandora.Picklock.R;

public class ExplorerGameScreen extends AppCompatActivity implements SensorEventListener {

    // controller stuff
    private GameClient game;
    BluetoothServiceFragment fragment;
    private Player player;
    Context view;
    private Boolean gameStarted = false;
    public Boolean panelsFilled = false;

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
    NfcAdapter nfcAdapter;
    PendingIntent pendingIntent;
    IntentFilter writeTagFilters[];
    Context context;
    public String coords = "";
    NFCReader reader = new NFCReader();


    //Activity Recognition meuk
    int stableamount = 9;
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

    int correctTime = 5;

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
        this.game = new GameClient();
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
        switch (msg.what) {
            case Constants.MESSAGE_READ:
                readMessage((String) msg.obj);
                break;
//            case Constants.LOCATION_FOUND:
//                locationFound((String) msg.obj);
//
//                break;
            case Constants.ACTIVITY_RECOGNIZED:

                break;
        }
        }
    };

//    private void locationFound(String msg) {
//        // TODO: convert msg to Location
//        String[] splittedMessage = msg.split(Constants.MESSAGE_SEPARATOR);
//        String location = splittedMessage[1];
//    }

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
        //TODO: START ACTIVITY RECOGNITION
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

        if (player.getPanels().length > 0) {
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
            //GifImageView animation = findViewById(R.id.animation);
            //animation.setImageResource(R.drawable.flaganimation);
        } else if (task.equals(Constants.TASK_SAFE)) {
            //GifImageView animation = findViewById(R.id.animation);
            //animation.setImageResource(R.drawable.safeanimation);
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
                if (motionEvent.getAction() == MotionEvent.ACTION_BUTTON_PRESS) {
                    String msg = Constants.HEADER_BUTTON + Constants.MESSAGE_SEPARATOR + "0" + Constants.MESSAGE_SEPARATOR + panels[0].getId() + Constants.MESSAGE_SEPARATOR + Constants.PRESSED;
                    fragment.sendMessage(msg);
                } else if (motionEvent.getAction() == MotionEvent.ACTION_BUTTON_RELEASE) {
                    String msg = Constants.HEADER_BUTTON + Constants.MESSAGE_SEPARATOR + "0" + Constants.MESSAGE_SEPARATOR + panels[0].getId() + Constants.MESSAGE_SEPARATOR + Constants.RELEASED;
                    fragment.sendMessage(msg);
                }
                return false;
            }
        });

        btn2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_BUTTON_PRESS) {
                    String msg = Constants.HEADER_BUTTON + Constants.MESSAGE_SEPARATOR + "0" + Constants.MESSAGE_SEPARATOR + panels[1].getId() + Constants.MESSAGE_SEPARATOR + Constants.PRESSED;
                    fragment.sendMessage(msg);
                } else if (motionEvent.getAction() == MotionEvent.ACTION_BUTTON_RELEASE) {
                    String msg = Constants.HEADER_BUTTON + Constants.MESSAGE_SEPARATOR + "0" + Constants.MESSAGE_SEPARATOR + panels[1].getId() + Constants.MESSAGE_SEPARATOR + Constants.RELEASED;
                    fragment.sendMessage(msg);
                }
                return false;
            }
        });

        btn3.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_BUTTON_PRESS) {
                    String msg = Constants.HEADER_BUTTON + Constants.MESSAGE_SEPARATOR + "0" + Constants.MESSAGE_SEPARATOR + panels[2].getId() + Constants.MESSAGE_SEPARATOR + Constants.PRESSED;
                    fragment.sendMessage(msg);
                } else if (motionEvent.getAction() == MotionEvent.ACTION_BUTTON_RELEASE) {
                    String msg = Constants.HEADER_BUTTON + Constants.MESSAGE_SEPARATOR + "0" + Constants.MESSAGE_SEPARATOR + panels[2].getId() + Constants.MESSAGE_SEPARATOR + Constants.RELEASED;
                    fragment.sendMessage(msg);
                }
                return false;
            }
        });

        btn4.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_BUTTON_PRESS) {
                    String msg = Constants.HEADER_BUTTON + Constants.MESSAGE_SEPARATOR + "0" + Constants.MESSAGE_SEPARATOR + panels[3].getId() + Constants.MESSAGE_SEPARATOR + Constants.PRESSED;
                    fragment.sendMessage(msg);
                } else if (motionEvent.getAction() == MotionEvent.ACTION_BUTTON_RELEASE) {
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
        //Dit is hoe het geimplement zou moeten worden, maar daar hebben we dus location enzo voor nodig.
        if(location.object == lock && done ==false){
            if(lockpickbool == false){
                lockpickbool = true;
                Lockpicker();
            }
            checkSensorsChanged(event);
            if (accChanged && gyroChanged ) {

                Double[] arraypicklock = getSensorData();

                try{
                    double resultpick = WekaClassifierPicklock.classify(arraypicklock);
                    if(picklockstable.size() == 0){
                        startTime = System.nanoTime();
                    }
                    picklockstable.add(resultpick);
                    if(picklockstable.size() == stableamount){

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
                            textresultpicklock = "you are doing nothing";
                            picklockstable.clear();
                        }

                        System.out.println(textresultpicklock);
                        System.out.println("volgende take zijn: " + picklock);

                    }


                }catch (Exception e){
                    System.out.println("and it stopped working");
                }
            }


        }

        checkSensorsChanged(event);
        changedSensorActivity();
    }

    private void changedSensorActivity() {
        if (accChanged && gyroChanged ){
            Double[] array = getSensorData();

            try{
                classifyData(array);
            }catch (Exception e){
                System.out.println("and it stopped working");
            }
        }
    }

    private void classifyData(Double[] array) throws Exception {
        double result = WekaClassifierActivities.classify(array);

        String textresult;

        activitystable.add(result);
        if(activitystable.size() == stableamount){


            result = getBestClassifiedActivity();

            if(result == 0.0){

                textresult = "you are Flag";

                activitystable.clear();
            }else if(result == 1.0) {
                textresult = "you are Still";

                activitystable.clear();
            }else if(result == 2.0) {
                textresult = "you are Shaking";

                activitystable.clear();
            }else if(result == 3.0) {
                textresult = "you are pirhouette";

                activitystable.clear();
            }else{
                textresult = "you are doing nothing";

                activitystable.clear();
            }

            //TextView placeText = (TextView)findViewById(R.id.textresult);
            //placeText.setText(textresult);

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
        Sensor sensor = event.sensor;

        Log.d(TAG, "onSensorChanged: sensor: " + Sensor.TYPE_ACCELEROMETER);
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){

            this.accXValue = event.values[0];
            this.accYValue = event.values[1];
            this.accZValue = event.values[2];
            accChanged = true;
            Log.d(TAG, "startTracking: sensordata changed: x: " + this.accXValue + ", y: " + this.accYValue + ", z: " + this.accZValue);
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
        System.out.println("allemaal dingen" + picklock);
        activity = picklock.get(0).toString();
        //String textresult = activity;
        //TextView placeText = (TextView)findViewById(R.id.textresult);
        //placeText.setText(textresult);
    }
    public void Lockpickerresult(int time){

        if(correctTime < time){
            picklock.remove(0);
            Vibrate();
            if(picklock.size() != 0){
                activity = picklock.get(0).toString();

            }else{
                activity = "you did lockpicked it!";
                lockpickbool = false;
                done = true;
            }
            String textresult = activity;
            //TextView placeText = (TextView)findViewById(R.id.textresult);
            //placeText.setText(textresult);
        }
    }
    public int randomNumber() {
        int randomInt = new Random().nextInt(4) + 2;
        return randomInt;
    }

    public List randomActions(int amount){
        int i = 0;
        List<String> myList = new ArrayList<String>();
        myList.add("Flat");
        myList.add("Right");
        myList.add("Middle");
        myList.add("Left");
        myList.add("Down");
        List picklockactivity = new ArrayList();
        int lastrand = 300;
        while(i < amount){

            int rand = new Random().nextInt(myList.size());
            if(lastrand == rand){
                rand = (rand * rand * rand + 3) % myList.size();
            }
            lastrand = rand;
            String activitiy = myList.get(rand);

            picklockactivity.add(activitiy);
            i = i + 1;
        }
        return picklockactivity;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
