package smartSpaces.Pandora;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
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
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import smartSpaces.Pandora.Game.Activities.WekaClassifierActivities;
import smartSpaces.Pandora.Game.Activities.WekaClassifierPicklock;
import smartSpaces.Pandora.Game.GameHost;
import smartSpaces.Pandora.Game.Map.GameMap;
import smartSpaces.Pandora.Game.Map.MapObject;
import smartSpaces.Pandora.Game.Map.ObjectType;
import smartSpaces.Pandora.Game.Panel;
import smartSpaces.Pandora.Game.Player;
import smartSpaces.Pandora.Game.Tasks.LocationTask;
import smartSpaces.Pandora.Game.Tasks.MotionActivityType;
import smartSpaces.Pandora.Game.Tasks.MotionTask;
import smartSpaces.Pandora.Game.Tasks.PanelTask;
import smartSpaces.Pandora.Game.Tasks.Task;
import smartSpaces.Pandora.Game.Tasks.TaskType;
import smartSpaces.Pandora.P2P.BluetoothServiceFragment;
import smartSpaces.Pandora.P2P.Constants;
import smartSpaces.Pandora.Picklock.R;

public class CoordinatorGameScreen extends AppCompatActivity implements SensorEventListener {
    //Todo appropriate loggin for each method
    //todo javadocs
    //region Game variables
    private final int HOSTPLAYERID = -1;
    private final int MAPDIM = 5;
    private BluetoothServiceFragment bServiceFragment;
    private GameHost game;
    private GameMap gameMap;
    private ArrayList<Panel> panels;
    private HashSet<Integer> playerIds;
    private HashMap<Location, CountDownTimer> mapTiles;
    private HashMap<MotionActivityType, Integer> concurActivityies;
    private HashMap<MotionActivityType, CountDownTimer> concurActivityiesTimers;
    public int lastScannedObject;
    private HashMap<Location, ImageView> objectImages = new HashMap<>();
    //endregion

    //region View variables
    private long GAMETIME =  10 * 60 * 1000;
    private long TASKTIME = 30 * 1000;
    private long GAMETIME_INTERVAL = 1000;
    private long TASKTIME_INTERVAL = 100;
    private long MAPTILE_VISIBLE = 2 * 60 * 1000;
    private long CONCUR_ACTIVITY_TIME = 5 * 1000;
    private long MAPBLOCKS = 5;
    private int COORDINATOR_ROLE = 0;
    private String TAG = "Coordinator ";
    Bitmap bm;
    private int screenWidth;
    Bitmap scaledbm;
    private ArrayList<Location> hiddenMapBlocks;
    CountDownTimer timer;
    CountDownTimer taskTimer;
    //endregion

    //NFC stuff
    NfcAdapter nfcAdapter;
    PendingIntent pendingIntent;
    IntentFilter writeTagFilters[];
    Context context;
    public String coords = "";
    NFCReader reader = new NFCReader();

    //Activity Recognition
    int STABLE_AMOUNT = 10;
    double maxValue;
    double maxCount;
    private boolean accChanged, gyroChanged, magnetoChanged;
    List<Double> activitystable = new ArrayList<>();
    private SensorManager sensorManager;
    private Sensor accelerometer, gyroscope;
    private double accXValue, accYValue, accZValue;
    private double gyroXvalue, gyroYValue, gyroZValue;
    private boolean gameStart = false;


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
        setContentView(R.layout.coordinator_wait_screen);

        TextView start = findViewById(R.id.btn_start);
        Typeface horrorFont = Typeface.createFromAsset(getAssets(), "buried-before.before-bb.ttf");
        start.setTypeface(horrorFont);
//        showPlayersJoined(0);
        ((TextView) findViewById(R.id.amount_players)).setText("0");

        game = new GameHost(30, 4);
        playerIds = new HashSet<>();
        mapTiles = new HashMap<>();
        concurActivityies = new HashMap<>();
        concurActivityiesTimers = new HashMap<>();
        bServiceFragment = new BluetoothServiceFragment(true, hostHandler, this);
        startBluetooth();

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

        //SENSORS
        //magnetometer could be uncommented if it wants to be used
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        //magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorManager.registerListener(this, accelerometer, sensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, gyroscope, sensorManager.SENSOR_DELAY_NORMAL);
        //sensorManager.registerListener(this, magnetometer, sensorManager.SENSOR_DELAY_NORMAL);


        game.addPlayer(new Player(HOSTPLAYERID, true));


    }

    //region Starters
    private void startBluetooth() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.add(android.R.id.content, bServiceFragment);
        transaction.commit();
    }

    private void setUp() {
        // setting typefaces (ugly but cannot be done in XML :/
        TextView task = findViewById(R.id.task);

        Typeface pixelFont = Typeface.createFromAsset(getAssets(), "Fipps-Regular.otf");

        task.setTypeface(pixelFont);

        // compute screenwidth;
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        Log.e("Width", "" + screenWidth);

        // initializing map
        try {
            bm = BitmapFactory.decodeStream(this.getApplicationContext().getAssets().open("map.png"));
            scaledbm = bm.createScaledBitmap(bm, screenWidth, screenWidth, false);
            Log.d(TAG, "onCreate: Updating map...");
        } catch (IOException e) {
            e.printStackTrace();
        }
        ImageView map = findViewById(R.id.map_background);
        map.setImageBitmap(scaledbm);
        Log.d(TAG, "onCreate: Map filled");

    }

    /**
     *
     */
    public void startGame() {
        playerIds.addAll(bServiceFragment.getClientIds());
        Log.i(TAG, "IDs" + playerIds.toString());
        playerIds.add(HOSTPLAYERID);


        initMap();
        // Demo test stuff

        initButtons();
        initPlayers();
        initTasks();
    }

    /**
     * The onClick method that starts the game
     *
     * @param view
     */
    public void startView(View view) {
        findViewById(R.id.modal_start).setVisibility(View.GONE);
        setContentView(R.layout.activity_coordinator_game_screen);

        setUp();
        startGame();
        sendGameStart();

        timer = new CountDownTimer(GAMETIME, GAMETIME_INTERVAL) {
            @Override
            public void onTick(long l) {
                int min = (int) Math.floor((double) Long.valueOf(l / (60 * 1000)));
                int sec = (int) (l - (min * 60 * 1000)) / 1000;
//                Log.d(TAG, "onTick: " + min + ":" + sec);

                updateTimer(min, sec);
            }

            @Override
            public void onFinish() {
                endGame(false);
                goToLost();
            }
        };

        timer.start();
        drawMap();
        gameStart = true;

//        setTask("Chase a cart");
    }
    //endregion

    //region Initialisers
    private void initButtons() {
        panels = new ArrayList<>();
        int buttonAmount = game.getTotalPanelAmount();
        Panel tmpPanel = new Panel(-1);
        ArrayList<String> verbs = new ArrayList<>(Arrays.asList(tmpPanel.getVERBS()));
        ArrayList<String> objects = new ArrayList<>(Arrays.asList(tmpPanel.getOBJECTS()));
        Random r = new Random();
        for (int i = 0; i < buttonAmount; i++) {
            int vInt = r.nextInt(buttonAmount - i);
            String verb = verbs.get(vInt);
            verbs.remove(verb);
            vInt = r.nextInt(buttonAmount - i);
            String object = objects.get(vInt);
            objects.remove(object);
            panels.add(new Panel(i, verb, object));
        }
    }

    private void initMap() {
        gameMap = new GameMap(MAPDIM);

        for (ObjectType ot : ObjectType.values()) {
            Location rLoc = randomLocation();
            while (gameMap.getObjectFromLocation(rLoc) != null) {
                rLoc = randomLocation();
            }
            gameMap.addObject(new MapObject(ot), rLoc);
        }
        Log.i("INITGAMEMAP", gameMap.getObjects().toString());
//        gameMap.addObject(new MapObject(ObjectType.BOMB), new Location(0, 0));
        drawMap();
    }

    /**
     * Initialises each {@link Player} and assigns the defined amount of panel to each Player.
     * This will also be send to each player's Client device.
     */
    private void initPlayers() {
        int startId = 0;
        for (int id : playerIds) {
            if (id != HOSTPLAYERID) {
                List<Panel> p = panels.subList(startId, game.getPanelsPerPlayer());
                Log.i("PANELFOR PLAYER", id + ": " + p.toString() + "  " + p.toArray(new Panel[0]).toString());
                game.addPlayer(new Player(id, false, p.toArray(new Panel[0])));
                Log.i("INITPLAYER", id + "");
                sendPlayerPanels(id);
                startId += game.getPanelsPerPlayer();
            }
        }
    }

    private void initTasks() {

        for (int id : playerIds) {
            Task task = randomTaskForPlayer(id);
            newTask(id);
//            game.newTask(game.getPlayer(id), task);
//            if (id != HOSTPLAYERID) {
////                //Demotask
////                game.newTask(game.getPlayer(id), new MotionTask(MotionActivityType.PICK_LOCK));
//////                game.newTask(game.getPlayer(id), new PanelTask(panels.get(0)));
////                //Demotask
//                sendNewTask(id);
//            } else {
//                // Demotask
//                Task demotask = new MotionTask(MotionActivityType.PICK_LOCK);
////                PanelTask demotask =  new PanelTask(panels.get(0));
//////                game.newTask(game.getPlayer(id), new MotionTask(MotionActivityType.PICK_LOCK));
////                game.newTask(game.getPlayer(id), task);
////                setTask(demotask.getDescription());
//                //Demotask
//
//
//                setTask(task.getDescription());
//            }
        }
    }

    //endregion

    //region Randomizers
    private Task randomTaskForPlayer(int id) {
        Player player = game.getPlayer(id);
        TaskType rTask = getRandomTaskType(id);
//        TaskType rTask = id!=HOSTPLAYERID ?  TaskType.MOTION : TaskType.PANEL;
//        TaskType rTask = TaskType.LOCATION;
        Random r = new Random();
        Task task = null;
        switch (rTask) {
            case PANEL:
                Log.i(TAG, panels.toString());
                ArrayList<Panel> pList = new ArrayList<>(panels);
                pList.removeAll(Arrays.asList(player.getPanels()));
                int index = r.nextInt(pList.size());
                task = new PanelTask(pList.get(index));
                break;
            case PANEL_CONCURRENT:
                ArrayList<Panel> list = new ArrayList<>();
                for (int i : playerIds) {
                    Log.i("PANEL CONCURRENT", game.getPlayer(i).getPanels().length + "   " + game.getPanelsPerPlayer());
                    if (i != HOSTPLAYERID) {
                        list.add(game.getPlayer(i).getPanels()[r.nextInt(game.getPanelsPerPlayer())]);
                    }
                }
                task = new PanelTask(list);
                break;
            case MOTION:
                MotionActivityType gtype = randomMotionActivityType(false);
                Log.i("MOTIonTTYPER", gtype.toString());
                if (id == HOSTPLAYERID && gtype.getResource() == MotionActivityType.PICK_LOCK.getResource()) {
                    gtype = MotionActivityType.SHAKE_PHONE;
                }
                task = new MotionTask(gtype);
//                task = new MotionTask(MotionActivityType.PICK_LOCK);
                break;
            case MOTION_LOCATION:
                MotionActivityType type = randomMotionActivityType(true);
                Log.i("MOTIonTTYPER0 LOCATION", type.toString());
                task = new MotionTask(type, randomMapObject());
                break;
            case MOTION_CONCURRENT:
                MotionActivityType rtype = randomMotionActivityType(false);
                Log.i("MOTIonTTYP CONCURRETN", rtype.toString());
                task = new MotionTask(rtype, true);
                break;
            case LOCATION:
                task = new LocationTask(new MapObject(ObjectType.LOCK));
//                task = new LocationTask(randomMapObject());
                break;
            case LOCATION_CONCURRENT:
                HashSet<MapObject> objects = new HashSet<>();
                Log.i("LOCATIONCONCURRENT", gameMap.getObjects().toString());
                Log.i("PLAYERAMOUNT", game.getPlayerAmount() + " ");
                int locationAmount = game.getPlayerAmount() > gameMap.getObjects().size() ? gameMap.getObjects().size() : game.getPlayerAmount();
                while (objects.size() < locationAmount) {
                    objects.add(randomMapObject());
                }
                Log.i("LOCATIONCONCURRENT", objects.toString() + "  " + locationAmount);
                task = new LocationTask(new ArrayList<>(objects));
                break;
        }
        return task;
    }

    private MotionActivityType randomMotionActivityType(boolean isMapObject) {
        Random r = new Random();
        MotionActivityType result;
        int index;
        if (isMapObject) {
            ArrayList<MapObject> objects = gameMap.getObjects();
            Log.i("RANDOMMOTIN", objects.size() + "");
//        Log.i("RANodMMOTi", objects.get(0).getObjectType().toString());
//        Log.i("RANODMMOIT", objects.get(0).getObjectType().getMotionActivityType().toString());

            index = r.nextInt(objects.size());
            Log.i("RANDOMMOTIN", objects.get(index).getObjectType().toString());
            while (objects.get(index).getObjectType().isHazard()) {
                index = r.nextInt(objects.size());
            }
            result = objects.get(index).getObjectType().getMotionActivityType();
        } else {
            index = r.nextInt(MotionActivityType.values().length);
            while (MotionActivityType.values()[index].getObjectType() != null){
                r.nextInt(MotionActivityType.values().length);
            }
            result = MotionActivityType.values()[index];
        }

        Log.i("MOTINEONDGD", index + " : " + result.toString());
        return result;
//        ArrayList<MotionActivityType> activities = new ArrayList<>();
//        for(MapObject m : objects) {
//            activities.add(m.getObjectType().getMotionActivityType());
//        }
//        return activities.get(r.nextInt(activities.size()));
    }

    private TaskType getRandomTaskType(int playerId) {
        Random r = new Random();
        if (game.getPlayerAmount() <= 2 && playerId != HOSTPLAYERID) {
            return r.nextInt(2) == 1 ? TaskType.MOTION : TaskType.LOCATION;
        }
//        else if (playerId == HOSTPLAYERID){
//            return TaskType.PANEL;
//        }
        return TaskType.values()[r.nextInt(TaskType.values().length)];
    }


    private Location randomLocation() {
        Random r = new Random();
        return new Location(r.nextInt(MAPDIM), r.nextInt(MAPDIM));
    }

    private MapObject randomMapObject() {
        Random r = new Random();
        return gameMap.getObjects().get(r.nextInt(gameMap.getObjects().size()));
    }
    //endregion

    //region View updaters

    public void updateGameStatus() {

    }

    /**
     * Updates the game timer (microwave view)
     *
     * @param min
     * @param sec
     */
    public void updateTimer(int min, int sec) {
        ImageView timeMF = findViewById(R.id.time_mf);
        ImageView timeMS = findViewById(R.id.time_ms);
        ImageView timeSF = findViewById(R.id.time_sf);
        ImageView timeSS = findViewById(R.id.time_ss);
        int minf = (int) Math.floor(min / 10);
        int mins = min - minf;
        int secf = (int) Math.floor(sec / 10);
        int secs = sec - (secf * 10);

        setTimerImage(timeMF, minf);
        setTimerImage(timeMS, mins);
        setTimerImage(timeSF, secf);
        setTimerImage(timeSS, secs);
    }

    /**
     * Converts a number to an image, and sets that image as timer image
     *
     * @param view
     * @param num
     */
    public void setTimerImage(ImageView view, int num) {
        switch (num) {
            case 0:
                view.setImageResource(R.drawable.i0);
                break;
            case 1:
                view.setImageResource(R.drawable.i1);
                break;
            case 2:
                view.setImageResource(R.drawable.i2);
                break;
            case 3:
                view.setImageResource(R.drawable.i3);
                break;
            case 4:
                view.setImageResource(R.drawable.i4);
                break;
            case 5:
                view.setImageResource(R.drawable.i5);
                break;
            case 6:
                view.setImageResource(R.drawable.i6);
                break;
            case 7:
                view.setImageResource(R.drawable.i7);
                break;
            case 8:
                view.setImageResource(R.drawable.i8);
                break;
            case 9:
                view.setImageResource(R.drawable.i9);
                break;
        }
    }

    /**
     * Set a new task, start task timer
     *
     * @param task
     */
    public void setTask(String task) {
        if (task.equals("Dummmy Task")) {
            Log.i("DUMMYTASK", game.getPlayerTasks().get(game.getPlayer(HOSTPLAYERID)).getTaskType().toString() + " " + game.getPlayerTasks().get(game.getPlayer(HOSTPLAYERID)).getDescription());
        }
        TextView taskView = findViewById(R.id.task);

        taskView.setText(task);

        if (taskTimer != null) {
            taskTimer.cancel();
        }
        taskTimer = new CountDownTimer(TASKTIME, TASKTIME_INTERVAL) {
            final ProgressBar progressBar = findViewById(R.id.progressBar);

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

//                taskCompleted();
                taskFailed();
                newTask(HOSTPLAYERID);
//                setTask("Do a pirouette");
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
     *
     * @param image
     */
    public void fadeOutImage(final ImageView image) {
        Animation aniFade = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fadeout);
        image.startAnimation(aniFade);

        aniFade.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                image.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    /**
     * Hide a certain block of the map
     *
     * @param location
     */
    public void hideMap(Location location) { // matrix from 0-4 0-4
        gameMap.setInvisible(location);
        drawMap();
    }

    public void showMap(Location location) {
        gameMap.setVisible(location);
        drawMap();
    }

    /**
     * Updates the map according to hiddenMapBlocks
     */
    public void drawMap() {
        Log.d(TAG, "hideMap: Updating map ...");
        Bitmap mutableBM = scaledbm.copy(Bitmap.Config.ARGB_8888, true);
        ImageView map = findViewById(R.id.map_background);
        int stepsize = Math.round(screenWidth / MAPBLOCKS);

        map.setImageBitmap(scaledbm);

        Canvas tempCanvas = new Canvas(mutableBM);
        Paint p = new Paint();
        p.setColor(Color.BLACK);
        tempCanvas.drawBitmap(scaledbm, 0, 0, null);

        for (int i = 0; i < gameMap.getInvisibleList().size(); i++) {
            Location block = gameMap.getInvisibleList().get(i);

            float left = (float) (block.getX() * stepsize);
            float right = left + stepsize;
            float top = (float) (block.getY() * stepsize);
            float bottom = top + stepsize;

            tempCanvas.drawRect(left, top, right, bottom, p);
        }

        map.setImageDrawable(new BitmapDrawable(getResources(), mutableBM));
    }

    public void drawObjectOnMap(int resId, Location location) {      // where location is top left corner of image
        /**
         *         possible drawables:
         *         - safe
         *         - flag
         *         -
         *
         */

        int stepsize = Math.round(screenWidth / MAPBLOCKS);
        int left = location.getX() * stepsize;
        int top = location.getY() * stepsize;

        ImageView imageView = new ImageView(this);
        imageView.setImageResource(resId);

        objectImages.put(location, imageView);

        RelativeLayout relativeLayout = findViewById(R.id.map_objects);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(stepsize, stepsize);
        layoutParams.setMargins(left, top, 0, 0);

        relativeLayout.addView(imageView, layoutParams);
    }

    /**
     * Adapts UI in how many players joined
     *
     * @param n
     */
    public void showPlayersJoined(int n) {
        TextView nPlayers = findViewById(R.id.amount_players);
        nPlayers.setText(n);
    }

    public void goToWin() {
        finish();
        Intent intent = new Intent(this, WinScreen.class);
        intent.putExtra("role", COORDINATOR_ROLE);
        intent.putExtra("tasks", game.getTasksComplete());
        startActivity(intent);
    }

    public void goToLost() {

        Intent intent = new Intent(this, LostScreen.class);
        intent.putExtra("role", COORDINATOR_ROLE);
        intent.putExtra("tasks", game.getTasksComplete());
        startActivity(intent);
//        finish();
    }

    //endregion

    //region Communication
    private void sendGameStart() {
        Log.i("START SEND", "SENDST START");
        bServiceFragment.sendMessage(Constants.HEADER_START);
    }

    private void sendGameEnd(String winOrLose) {
        Log.d(TAG, "sendGameEnd: tasks completed: " + game.getTasksComplete());
        bServiceFragment.sendMessage(Constants.HEADER_END + Constants.MESSAGE_SEPARATOR + winOrLose + Constants.MESSAGE_SEPARATOR + game.getTasksComplete());
    }

    private void sendNewTask(int playerId) {
        ArrayList<String> message = new ArrayList<>();
        message.add(Constants.HEADER_TASK);
        Log.i("SENDNEWTASK", playerId + "  " + game.getPlayerTasks().toString());
        message.add(game.getPlayerTasks().get(game.getPlayer(playerId)).getDescription());
        String out = TextUtils.join(Constants.MESSAGE_SEPARATOR, message);
        bServiceFragment.sendMessage(out);
    }

    private void sendLocationObject(MapObject object, int playerId) {
        ArrayList<String> message = new ArrayList<>();
        message.add(Constants.HEADER_LOCATION);
        message.add(String.valueOf(object.getResource()));
        String out = TextUtils.join(Constants.MESSAGE_SEPARATOR, message);

        bServiceFragment.sendMessage(out, playerId);
    }

    private void sendPlayerPanels(int playerId) {
        ArrayList<String> message = new ArrayList<>();
        Panel[] panels = game.getPlayer(playerId).getPanels();
        message.add(Constants.HEADER_BUTTON);
        message.add(String.valueOf(panels.length));

        ArrayList<String> list = new ArrayList<>();
        for (Panel p : panels) {
            list.add(TextUtils.join(Constants.MESSAGE_LIST_ELEMENT_SEPARATOR, new String[]{String.valueOf(p.getId()), p.getVerb(), p.getObject()}));
        }
        message.add(
                TextUtils.join(Constants.MESSAGE_LIST_SEPARATOR, list));

        String out = TextUtils.join(Constants.MESSAGE_SEPARATOR, message);
        Log.i("SEND PANEL", "ID: " + playerId);
        Log.i("SEND PANEL", out);
        bServiceFragment.sendMessage(out, playerId);
    }

    private void sendHazardTriggered(MapObject hazard, int playerId) {
        ArrayList<String> message = new ArrayList<>();
        message.add(Constants.HEADER_HAZARD);
        message.add(String.valueOf(hazard.getResource()));

        String out = TextUtils.join(Constants.MESSAGE_SEPARATOR, message);

        bServiceFragment.sendMessage(out, playerId);
    }
    //endregion

    //region GameState alterations

    /**
     * Sends a new task to specific player.
     *
     * @param playerId
     */
    private void newTask(int playerId) {
        //Set new task for that player
        Task nTask = randomTaskForPlayer(playerId);
        game.newTask(game.getPlayer(playerId), nTask);

        if (game.gameFinished()) {
            endGame(true);
        }
        // Process new task
        if (playerId == HOSTPLAYERID) {
            setTask(nTask.getDescription());
        } else {
            sendNewTask(playerId);
        }
//        ((TextView) findViewById(R.id.tasks_left)).setText(game.getTasksLeft());
//        ((TextView) findViewById(R.id.tasks_completed)).setText(game.getTasksComplete());

    }

    private void endGame(boolean gameWon) {
        if (gameWon) {
            timer.cancel();
            sendGameEnd(Constants.WIN);
            goToWin();
        } else {
            sendGameEnd(Constants.LOSE);
            goToLost();
        }
    }

    /**
     * Checks if the motionActivity is part of a task and finished that task, completes it, and sets a new one.
     *
     * @param task
     * @param playerId
     * @return
     */
    private void finishMotionTask(MotionActivityType task, int playerId) {
        //todo cuncurrent check and concurrent allez
        setConcurPlayerTasks(task);
        HashMap<Player, Task> pTasks = new HashMap<>(game.getPlayerTasks());
        //Remove task assigned to the player which completed task
        pTasks.remove(game.getPlayer(playerId));
        //Find which player the completed task belonged to
        for (Map.Entry<Player, Task> pt : pTasks.entrySet()) {
            if (playerId != pt.getKey().getId() && pt.getValue() instanceof MotionTask) {
//                Log.i("TASKTYPE IN TASKS", ((MotionTask) pt.getValue()).getMotionType().toString() + ":" + pt.getValue().getDescription());
                if (pt.getValue().isConcurrent() && concurActivityies.containsKey(task)) {
                    if(concurActivityies.get(task) == game.getPlayerAmount()) {
                        game.completeTask();
                        newTask(pt.getKey().getId());

                        if (pt.getKey().getId() == HOSTPLAYERID) {
                            taskCompleted();
                        }
                        Log.i("MOTINOFINISJ", "FINIS: " + task.toString());
                    }
                } else {
                    if (((MotionTask) pt.getValue()).getMotionType() == task) {
                        game.completeTask();
                        newTask(pt.getKey().getId());
                        Log.i("MOTINOFINISJ", "FINISjh");
                        if (pt.getKey().getId() == HOSTPLAYERID) {
                            taskCompleted();
                            Log.i("MOTINOFINISJ", "FINI HOSTS");
                        }
                    }
                }
            }
        }
    }

    /**
     * Checks if the pressed panel completed any tasks and completes it. Otherwise does nothing.
     *
     * @param p
     * @param playerId
     */
    private void finishButtonTask(Panel p, int playerId) {
//        HashMap<Player, Task> pTasks = new HashMap<>(game.getPlayerTasks());
        //Loop through all playerTasks
        for (Map.Entry<Player, Task> pTask : game.getPlayerTasks().entrySet()) {
            if (pTask.getValue() instanceof PanelTask) {
                Log.i("FINISHBUTToN", "ISTASK");
                PanelTask task = (PanelTask) pTask.getValue();
                if (task.isConcurrent()) {
                    // Set panel on pressed.
                    task.setPressed(p.getId());
                    if (task.isCompleted()) {
                        game.completeTask();
                        if (pTask.getKey().getId() == HOSTPLAYERID) {
                            taskCompleted();
                        }
                        newTask(pTask.getKey().getId());
                    }
                } else {
                    // If panel being pressed is the same complete the task
                    Log.i("FINISHBUTToN", "Is bUtton");
                    if (p.getId() == task.getTaskPanel().getId()) {
                        game.completeTask();
                        if (pTask.getKey().getId() == HOSTPLAYERID) {
                            taskCompleted();
                        }
                        newTask(pTask.getKey().getId());
                    }
                }
            }
        }
    }

    /**
     * Sets button of certain task to released
     *
     * @param p
     */
    private void releaseButton(Panel p) {
        for (Map.Entry<Player, Task> pTask : game.getPlayerTasks().entrySet()) {
            if (pTask.getValue() instanceof PanelTask) {
                PanelTask task = (PanelTask) pTask.getValue();
                if (task.isConcurrent()) {
                    task.setReleased(p.getId());
                }
            }
        }
    }

    /**
     * Check is a locationtask can be finished and completes it, otherwise does nothing.
     */
    private void finishLocationTask() {
        // Loop through all playerTasks
        for (Map.Entry<Player, Task> pTask : game.getPlayerTasks().entrySet()) {
            if (pTask.getValue() instanceof LocationTask) {

                LocationTask lTask = (LocationTask) pTask.getValue();
                if (lTask.isConcurrent()) {
                    ArrayList<MapObject> objects = lTask.getMapObjects();
                    boolean finished = true;
                    // Loop though all location in the locationTask and
                    // if one of the locations is not in the set with the playerlocations the task is not finished
                    for (MapObject m : objects) {
                        if (!gameMap.isPlayerLocation(gameMap.getObjectLocation(m))) {
                            finished = false;
                            break;
                        }
                    }
                    if (finished) {
                        if (pTask.getKey().getId() == HOSTPLAYERID) {
                            taskCompleted();
                        }
                        newTask(pTask.getKey().getId());
                    }
                } else {
                    Log.i("FINISHTASK", "TRY :" + lTask.getMapObject().getName());
//                    Log.i("FINISHTASK", gameMap.getObjectLocationFromType());
                    if (gameMap.getObjectLocation(lTask.getMapObject()) != null) {
                        Log.i("FINISHTASK", gameMap.getObjectLocation(lTask.getMapObject()).toString() + " :  " + lTask.getMapObject().getName());
                        if (gameMap.isPlayerLocation(gameMap.getObjectLocation(lTask.getMapObject()))) {
                            if (pTask.getKey().getId() == HOSTPLAYERID) {
                                taskCompleted();
                            }
                            newTask(pTask.getKey().getId());
                            Log.i(TAG, "LOCATION FINISHEDSFSDFSDFS");
                        }
                    }
                }
            }
        }
    }

    private void failTask(int playerId) {
        Task nTask = randomTaskForPlayer(playerId);
        game.newTask(game.getPlayer(playerId), nTask);
        if (playerId == HOSTPLAYERID) {
            taskFailed();
            setTask(nTask.getDescription());
        } else {
            sendNewTask(playerId);
        }
    }

    private void setConcurPlayerTasks(MotionActivityType mType) {
        int amount = concurActivityies.containsKey(mType) ? concurActivityies.get(mType)+1 : 1;
        CountDownTimer timer = new CountDownTimer(CONCUR_ACTIVITY_TIME, CONCUR_ACTIVITY_TIME) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                concurActivityies.put(mType, concurActivityies.get(mType)-1);
            }
        };
        if (concurActivityiesTimers.containsKey(mType)) {
            concurActivityiesTimers.get(mType).cancel();
        }
        concurActivityies.put(mType, amount);
        concurActivityiesTimers.put(mType,timer);

    }

    private void locationFound(Location location) {
        boolean isVisible = false;
        for (Location visible : gameMap.getVisibleList()) {
            if (visible.isLocation(location)) {
                isVisible = true;
                break;
            }

        }
        if (!isVisible) {
            if (gameMap.getObjectFromLocation(location) != null) {
                drawObjectOnMap(gameMap.getObjectFromLocation(location).getObjectType().getIcon(), location);
            }

            gameMap.setVisible(location);
        }
        CountDownTimer mapTimer = new CountDownTimer(MAPTILE_VISIBLE, MAPTILE_VISIBLE) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                Log.i("MAPTIMER", "TIMER DONE: " + location.getX() + " " + location.getY());
                gameMap.setInvisible(location);
                if (objectImages.get(location) != null) {
                    ImageView objectImage = objectImages.get(location);
                    objectImage.setVisibility(View.GONE);
                }
                drawMap();
            }

        };
        for (Map.Entry<Location, CountDownTimer> mt : mapTiles.entrySet()) {
            if (mt.getKey().isLocation(location)) {
                Log.i("MAPPP", "rewrite timer");
                mt.getValue().cancel();
            }
        }
        mapTiles.put(location, mapTimer);
        mapTimer.start();
        drawMap();
    }

    private void findLocationObject(Location location, int playerId) {
        MapObject object = gameMap.getObjectFromLocation(location);
        if (object != null) {
            sendLocationObject(object, playerId);
        }
    }
    //endregion

    private void parseMessage(String message, int playerId) {
        String[] msgToParse = message.split(Constants.MESSAGE_SEPARATOR);
        switch (msgToParse[0]) {
            case Constants.HEADER_TASK:
                String activity = msgToParse[2];
                int index;
                try {
                    index = Integer.parseInt(msgToParse[2]);
                } catch (NumberFormatException e) {
                    index = -15;
                }
                if (activity.equals(Constants.TASK_FAILED)) {
                    failTask(playerId);
                } else if (index >= 0) {
                    Log.i(TAG, message);
                    finishMotionTask(MotionActivityType.valueOf(Integer.parseInt(activity)), playerId);
                }
                break;
            case Constants.HEADER_BUTTON:
                Log.i("BUTTONPARSE", msgToParse[2]);
                int buttonId = Integer.parseInt(msgToParse[2]);
                String buttonPressed = msgToParse[3];
                for (Panel p : panels) {
                    if (p.getId() == buttonId) {
                        if (buttonPressed.equals(Constants.PRESSED)) {
                            finishButtonTask(p, playerId);
                        } else if (buttonPressed.equals(Constants.RELEASED)) {
                            releaseButton(p);
                        }
                    }
                }


                break;
            case Constants.HEADER_LOCATION:
                //todo reset after x time
                String locationString = msgToParse[2];
                int x = Character.getNumericValue(locationString.charAt(1));
                int y = Character.getNumericValue(locationString.charAt(3));
                if (x >= 0 && y >= 0) {
                    Location location = new Location(x, y);
                    // Sets players location on the map
                    gameMap.setPlayerLocation(location, game.getPlayer(playerId));
                    // Sets maplocation visible
                    locationFound(location);
                    // Sends mapObject to player if there is one
                    findLocationObject(location, playerId);
                    // Finishes a locationTask if possible
                    finishLocationTask();
                }
                break;
            case Constants.HEADER_HAZARD:
                //todo
                break;
        }
    }

    @SuppressLint("HandlerLeak")
    private final Handler hostHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_READ:
//                    Log.i(TAG, "CLient id: " + msg.arg1);
//                    Log.i(TAG, "Message READ: " + msg.obj);
                    parseMessage((String) msg.obj, msg.arg1);
                    break;
                case Constants.NEW_CONNECTION:
                    Log.i(TAG, "NEW CONNECTION : " + msg.obj.toString());
                    //The view mainactivity view is edited to show the amount of joined players
                    ((TextView) (findViewById(R.id.amount_players))).setText(String.valueOf(msg.arg1));
                    findViewById(R.id.btn_start).setEnabled(true);
                    playerIds.add((Integer) msg.obj);
                    //For every new connection a player is added to the game.
                    game.setPlayerAmount((Integer) msg.arg1);
                    break;
            }
        }
    };

    //region nfc
    @Override
    protected void onNewIntent(Intent intent) {
        //super.onNewIntent(intent);
        Log.d(TAG, "NFC FOUND: " + intent.getAction());
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            coords = reader.readFromIntent(intent);
            Log.d(TAG, "LOCATION: " + coords);
            int x = Character.getNumericValue(coords.charAt(1));
            int y = Character.getNumericValue(coords.charAt(3));
            Location location = new Location(x, y);
            // Sets players location on the map
            gameMap.setPlayerLocation(location, game.getPlayer(HOSTPLAYERID));
            // Sets maplocation visible
            locationFound(location);
            // Finishes a locationTask if possible
            finishLocationTask();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
    }
    //endregion

    //region lockpickmeuk
    @Override
    public void onSensorChanged(SensorEvent event) {
        // TODO: is always trying to recognize activity when it has last scanned an object. --> fix?
//        Log.d("SENSOR", "START");
        if(gameStart) {
            if (gameMap.getPlayerLocations().get(game.getPlayer(HOSTPLAYERID)) != null && gameMap.getPlayerLocations().get(game.getPlayer(HOSTPLAYERID)).isLocation(gameMap.getObjectLocationFromType(ObjectType.LOCK))) {
                Log.d("SENSOR", "LOCKPIKC??");
                Log.i("LOCKPICK", picklock.toString());
                if (!lockpickbool) {
                    lockpickbool = true;
                    Lockpicker();
                }
                checkSensorsChanged(event);
                if (accChanged && gyroChanged) {
                    Double[] arrayPicklock = getSensorData();
                    changedSensorActivity(arrayPicklock);

                    try {
                        double resultpick = WekaClassifierPicklock.classify(arrayPicklock);
                        //Log.i("LOCKPICK", String.valueOf(resultpick));
                        Log.i("LOCKPICK", String.valueOf(picklockstable));
                        if (picklockstable.size() == 0) {
                            startTime = System.nanoTime();
                            Log.i("LOCKPICK", "Start tijd");
                        }

                        picklockstable.add(resultpick);
                        if (picklockstable.size() == STABLE_AMOUNT-1) {

                            String textresultpicklock;
                            elapsedTime = (int) ((System.nanoTime() - startTime) / 1000000);
                            resultpick = getBestClassifiedPicklock();
                            Log.i("LOCKPICK", "resultpick: " + String.valueOf(resultpick));
                            if (resultpick == 0.0) {
                                textresultpicklock = "Left";
                                flatTime = 0;
                                rightTime = 0;
                                middleTime = 0;
                                downTime = 0;
                                leftTime = leftTime + elapsedTime;
                                picklockstable.clear();

                                if (activity.equals(textresultpicklock)) {
                                    Lockpickerresult(leftTime);
                                    Log.d(TAG, "onSensorChanged: left");
                                }
                            } else if (resultpick == 1.0) {
                                textresultpicklock = "Down";
                                flatTime = 0;
                                rightTime = 0;
                                middleTime = 0;
                                leftTime = 0;
                                downTime = downTime + elapsedTime;
                                picklockstable.clear();

                                if (activity.equals(textresultpicklock)) {
                                    Lockpickerresult(downTime);
                                    Log.d(TAG, "onSensorChanged: down");
                                }
                            } else if (resultpick == 2.0) {
                                textresultpicklock = "Flat";
                                rightTime = 0;
                                middleTime = 0;
                                leftTime = 0;
                                downTime = 0;

                                flatTime = flatTime + elapsedTime;
                                picklockstable.clear();

                                if (activity.equals(textresultpicklock)) {
                                    Lockpickerresult(flatTime);
                                    Log.d(TAG, "onSensorChanged: flat");
                                }
                            } else if (resultpick == 3.0) {
                                textresultpicklock = "Right";
                                middleTime = 0;
                                leftTime = 0;
                                downTime = 0;
                                flatTime = 0;


                                rightTime = rightTime + elapsedTime;
                                picklockstable.clear();

                                if (activity.equals(textresultpicklock)) {
                                    Lockpickerresult(rightTime);
                                    Log.d(TAG, "onSensorChanged: right");
                                }
                            } else if (resultpick == 4.0) {
                                rightTime = 0;
                                leftTime = 0;
                                downTime = 0;
                                flatTime = 0;
                                textresultpicklock = "Middle";
                                middleTime = middleTime + elapsedTime;

                                picklockstable.clear();
                                if (activity.equals(textresultpicklock)) {
                                    Lockpickerresult(middleTime);
                                    Log.d(TAG, "onSensorChanged: middle");
                                }
                            } else {
                                picklockstable.clear();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
//            Log.d("SENSOR", "ChecksensorChanged");
                checkSensorsChanged(event);
                changedSensorActivity();

            }
        }
    }

    private void changedSensorActivity() {
        if (accChanged && gyroChanged ){
//            Log.i("SENSORCHANGED", "SENDOSRGDFESFS");
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
//        Log.i("ACITIVEYTD234", MotionActivityType.valueOf((int)result).toString());
//        Log.i("ACITIVEYTD234", activitystable.size() + "");
        if( activitystable.size() >= STABLE_AMOUNT) {
            result = getBestClassifiedActivity();
//            Log.i("ACITIVEYTD", result + " ; " + ((int) result));
//            Log.i("ACITIVEYTD", MotionActivityType.valueOf((int)result).toString());
            String msg;
            if (result == 0.0) {
                finishMotionTask(MotionActivityType.valueOf((int)result), HOSTPLAYERID);
                msg = Constants.HEADER_TASK + Constants.MESSAGE_SEPARATOR + "0" + Constants.MESSAGE_SEPARATOR + MotionActivityType.RAISE_FLAG.getResource();
            } else{
                finishMotionTask(MotionActivityType.valueOf((int)result), HOSTPLAYERID);
            }


//            fragment.sendMessage(msg);
            activitystable.clear();
        }
    }

    private double getBestClassifiedActivity() {
        double result;
        for (int i = 0; i < activitystable.size(); ++i) {
            int count = 0;
            for (int j = 0; j < activitystable.size(); ++j) {
                if (activitystable.get(j) == activitystable.get(i)){
                    ++count;
                }
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

    private double getBestClassifiedPicklock() {
        double result;
        for (int i = 0; i < picklockstable.size(); ++i) {
            int count = 0;
            for (int j = 0; j < picklockstable.size(); ++j) {
                if (picklockstable.get(j) == picklockstable.get(i)){
                    ++count;
                }
            }
            if (count > maxCount) {
                maxCount = count;
                maxValue = picklockstable.get(i);
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
            Log.i("LOCKPICK", String.valueOf(time));
            picklock.remove(0);
            Vibrate();
            Log.i("LOCKPICK", picklock.toString());
            if (picklock.size() != 0) {
                activity = picklock.get(0).toString();
            } else {
//                String msg = Constants.HEADER_TASK + Constants.MESSAGE_SEPARATOR + "0" + Constants.MESSAGE_SEPARATOR + MotionActivityType.PICK_LOCK.getResource();
//                fragment.sendMessage(msg);
                Log.i("LOCKPICK", "woop woop");
                finishMotionTask(MotionActivityType.PICK_LOCK,HOSTPLAYERID);
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
    //endregion
}
