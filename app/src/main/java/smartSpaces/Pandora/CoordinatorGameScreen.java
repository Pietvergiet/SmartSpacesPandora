package smartSpaces.Pandora;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
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

public class CoordinatorGameScreen extends AppCompatActivity {
    //Todo appropriate loggin for each method
    //todo javadocs
    //region Game variables
    private final int HOSTPLAYERID = -1;
    private final int MAPDIM = 5;
    private BluetoothServiceFragment bServiceFragment;
    private GameHost game;
    private GameMap gameMap;
    private ArrayList<Panel> panels;
    private Set<Integer> playerIds;

    //endregion

    //region View variables
    private long GAMETIME = 10 * 60 * 1000;
    private long TASKTIME = 30 * 1000;
    private long GAMETIME_INTERVAL = 1000;
    private long TASKTIME_INTERVAL = 100;
    private long MAPBLOCKS = 5;
    private int COORDINATOR_ROLE = 0;
    private String TAG = "Coordinator ";
    Bitmap bm;
    private int screenWidth;
    Bitmap scaledbm;
    private ArrayList<Location> hiddenMapBlocks;
    CountDownTimer timer;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coordinator_game_screen);
        game = new GameHost(30, 4);

        bServiceFragment = new BluetoothServiceFragment(true, hostHandler, this);
        startBluetooth();


        game.addPlayer(new Player(HOSTPLAYERID, true));

        setUp();
    }

    //region Starters
    private void startBluetooth() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.add(android.R.id.content, bServiceFragment);
        transaction.commit();
    }

    private void setUp(){
        // setting typefaces (ugly but cannot be done in XML :/
        TextView task = findViewById(R.id.task);
        TextView start = findViewById(R.id.btn_start);

        Typeface pixelFont = Typeface.createFromAsset(getAssets(), "Fipps-Regular.otf");
        Typeface horrorFont = Typeface.createFromAsset(getAssets(), "buried-before.before-bb.ttf");

        task.setTypeface(pixelFont);
        start.setTypeface(horrorFont);

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

        // hide map
//        hideMap(0, 0);
//        hideMap(0, 1);
//        hideMap(0, 2);
//        hideMap(0, 3);
//        hideMap(0, 4);
//        hideMap(1, 0);
//        hideMap(1, 1);
//        hideMap(1, 2);
//        hideMap(1, 3);
//        hideMap(1, 4);
//        hideMap(2, 0);
//        hideMap(2, 1);
//        hideMap(2, 2);
//        hideMap(2, 3);
//        hideMap(2, 4);
//        hideMap(3, 0);
//        hideMap(3, 1);
//        hideMap(3, 2);
//        hideMap(3, 3);
//        hideMap(3, 4);
//        hideMap(4, 0);
//        hideMap(4, 1);
//        hideMap(4, 2);
//        hideMap(4, 3);
//        hideMap(4, 4);
//
//        showMap(3,4);
//        showMap(1,1);

//        drawObjectOnMap(R.drawable.flag, new Location(3,3));
//        drawObjectOnMap(R.drawable.safe, new Location(1,2));
//        drawObjectOnMap(R.drawable.bomb, new Location(0,4));
    }

    /**
     *
     */
    public void startGame(){
        playerIds = new HashSet<>(bServiceFragment.getClientIds());
        Log.i(TAG, "IDs" + playerIds.toString());
        playerIds.add(HOSTPLAYERID);
        initMap();
        initButtons();
        initPlayers();
        initTasks();
    }

    /**
     * The onClick method that starts the game
     * @param view
     */
    public void startView(View view) {
        startGame();
        sendGameStart();
        RelativeLayout modal = findViewById(R.id.modal_start);
        modal.setVisibility(View.GONE);

        timer = new CountDownTimer(GAMETIME, GAMETIME_INTERVAL) {
            @Override
            public void onTick(long l) {
                int min = (int) Math.floor((double) Long.valueOf(l / (60*1000)));
                int sec =(int) (l - (min * 60 * 1000)) / 1000;
                Log.d(TAG, "onTick: " + min + ":" + sec);

                updateTimer(min, sec);
            }

            @Override
            public void onFinish() {
                endGame(false);
                goToLost();
            }
        };

        timer.start();


//        setTask("Chase a cart");
    }
    //endregion

    //region Initialisers
    private void initButtons(){
        panels = new ArrayList<>();
        int buttonAmount = game.getTotalPanelAmount();
        Panel tmpPanel = new Panel(-1);
        ArrayList<String> verbs = new ArrayList<>(Arrays.asList(tmpPanel.getVERBS()));
        ArrayList<String> objects = new ArrayList<>(Arrays.asList(tmpPanel.getOBJECTS()));
        Random r = new Random();
        for (int i = 0; i < buttonAmount; i++) {
            int vInt = r.nextInt(buttonAmount-i);
            String verb = verbs.get(vInt);
            verbs.remove(verb);
            vInt = r.nextInt(buttonAmount - i);
            String object = objects.get(vInt);
            objects.remove(object);
            panels.add(new Panel(i, verb, object));
        }
    }

    private void initMap(){
        gameMap = new GameMap(MAPDIM);

        for (ObjectType ot : ObjectType.values()) {
            Location rLoc = randomLocation();
            while (gameMap.getObjectFromLocation(rLoc) != null) {
                rLoc = randomLocation();
            }
            gameMap.addObject(new MapObject(ot), rLoc);
        }
        Log.i("INITGAMEMAP", gameMap.getObjects().toString());
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
            }
        }
    }

    private void initTasks(){
        for(int id : playerIds) {
            Task task = randomTaskForPlayer(id);
            game.newTask(game.getPlayer(id), task);
            if (id != HOSTPLAYERID) {

                sendNewTask(id);
            } else {
                setTask(task.getDescription());
            }
        }
    }

    //endregion

    //region Randomizers
    private Task randomTaskForPlayer(int id) {
        Player player = game.getPlayer(id);
        TaskType rTask = getRandomTaskType();
//        TaskType rTask = TaskType.LOCATION_CONCURRENT;
        Random r = new Random();
        Task task = null;
        switch (rTask) {
            case PANEL:
                Log.i(TAG, panels.toString());
                ArrayList<Panel> pList = new ArrayList<>(panels);
                pList.removeAll(Arrays.asList(player.getPanels()));
                task = new PanelTask(pList.get(r.nextInt(pList.size())));
                break;
            case PANEL_CONCURRENT:
                ArrayList<Panel> list = new ArrayList<>();
                for (int i : playerIds){
                    Log.i("PANEL CONCURRENT", game.getPlayer(i).getPanels().length + "   " + game.getPanelsPerPlayer() );
                    if (i != HOSTPLAYERID) {
                        list.add(game.getPlayer(i).getPanels()[r.nextInt(game.getPanelsPerPlayer())]);
                    }
                }
                task = new PanelTask(list);
                break;
            case MOTION:
                MotionActivityType gtype = randomMotionActivityType();
                Log.i("MOTIonTTYPER", gtype.toString());
                task = new MotionTask(gtype);
                break;
            case MOTION_LOCATION:
                MotionActivityType type = randomMotionActivityType();
                Log.i("MOTIonTTYPER0 LOCATION", type.toString());
                task = new MotionTask(type, randomMapObject());
                break;
            case MOTION_CONCURRENT:
                MotionActivityType rtype = randomMotionActivityType();
                Log.i("MOTIonTTYP CONCURRETN", rtype.toString());
                task = new MotionTask(rtype, true);
                break;
            case LOCATION:
                task = new LocationTask(randomMapObject());
                break;
            case LOCATION_CONCURRENT:
                HashSet<MapObject> objects = new HashSet<>();
                Log.i("LOCATIONCONCURRENT", gameMap.getObjects().toString());
                Log.i("PLAYERAMOUNT", game.getPlayerAmount() + " ");
                int locationAmount = game.getPlayerAmount() > gameMap.getObjects().size() ? gameMap.getObjects().size() : game.getPlayerAmount();
                while(objects.size() < locationAmount) {
                    objects.add(randomMapObject());
                }
                Log.i("LOCATIONCONCURRENT", objects.toString() + "  " + locationAmount);
                task =  new LocationTask(new ArrayList<>(objects));
                break;
        }
        return task;
    }

    private MotionActivityType randomMotionActivityType(){
        Random r = new Random();
        ArrayList<MapObject> objects = gameMap.getObjects();
        Log.i("RANDOMMOTIN", objects.toString());
        Log.i("RANodMMOTi", objects.get(0).getObjectType().toString());
        Log.i("RANODMMOIT", objects.get(0).getObjectType().getMotionActivityType().toString());
        int index = r.nextInt(objects.size());
        MotionActivityType result = objects.get(index).getObjectType().getMotionActivityType();
        Log.i("MOTINEONDGD", index + " : " + result.toString());
        return result;
//        ArrayList<MotionActivityType> activities = new ArrayList<>();
//        for(MapObject m : objects) {
//            activities.add(m.getObjectType().getMotionActivityType());
//        }
//        return activities.get(r.nextInt(activities.size()));
    }

    private TaskType getRandomTaskType() {
        Random r = new Random();
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
     * @param min
     * @param sec
     */
    public void updateTimer(int min, int sec) {
        ImageView timeMF = findViewById(R.id.time_mf);
        ImageView timeMS = findViewById(R.id.time_ms);
        ImageView timeSF = findViewById(R.id.time_sf);
        ImageView timeSS = findViewById(R.id.time_ss);
        int minf = (int) Math.floor(min/10);
        int mins = min - minf;
        int secf = (int) Math.floor(sec/10);
        int secs = sec - (secf*10);

        setTimerImage(timeMF, minf);
        setTimerImage(timeMS, mins);
        setTimerImage(timeSF, secf);
        setTimerImage(timeSS, secs);
    }

    /**
     * Converts a number to an image, and sets that image as timer image
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
     * @param task
     */
    public void setTask(String task) {
        TextView taskView = findViewById(R.id.task);
        final ProgressBar progressBar = findViewById(R.id.progressBar);
        taskView.setText(task);

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

//                taskCompleted();
                taskFailed();
                setTask("Do a pirouette");
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
     * Hide a certain block of the map
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

        for (int i = 0; i < gameMap.getInvisibleList().size(); i ++) {
            Location block = gameMap.getInvisibleList().get(i);

            float left = (float) (block.getX() * stepsize);
            float right = left + stepsize;
            float top = (float) (block.getY() * stepsize);
            float bottom = top + stepsize;

            tempCanvas.drawRect(left, top, right, bottom, p);
        }

        map.setImageDrawable(new BitmapDrawable(getResources(), mutableBM));
    }

    public void drawObjectOnMap(int resId, Location location){      // where location is top left corner of image
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

        RelativeLayout relativeLayout = findViewById(R.id.map_objects);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(stepsize, stepsize);
        layoutParams.setMargins(left, top, 0, 0);

        relativeLayout.addView(imageView, layoutParams);
    }

    /**
     * Adapts UI in how many players joined
     * @param n
     */
    public void showPlayersJoined(int n) {
        TextView nPlayers = findViewById(R.id.amount_players);
        nPlayers.setText(n);
    }

    public void goToWin() {
        Intent intent = new Intent(this, WinScreen.class);
        intent.putExtra("role", COORDINATOR_ROLE);
        startActivity(intent);
    }

    public void goToLost() {
        Intent intent = new Intent(this, LostScreen.class);
        intent.putExtra("role", COORDINATOR_ROLE);
        startActivity(intent);
    }

    //endregion

    //region Communication
    private void sendGameStart() {
        Log.i("START SEND", "SENDST START");
        bServiceFragment.sendMessage(Constants.HEADER_START);
    }

    private void sendGameEnd(String winOrLose) {
        bServiceFragment.sendMessage(Constants.HEADER_END + Constants.MESSAGE_SEPARATOR + winOrLose);
    }

    private void sendNewTask(int playerId){
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

    private void sendPlayerPanels(int playerId){
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
        Log.i("SEND PANEL", out);
        bServiceFragment.sendMessage(out);
    }

    private void sendHazardTriggered(MapObject hazard, int playerId){
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
     * @param playerId
     */
    private void newTask(int playerId){
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
    }

    private void endGame(boolean gameWon){
        if(gameWon){
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
     * @param task
     * @param playerId
     * @return
     */
    private void finishMotionTask(MotionActivityType task, int playerId) {
        //todo cuncurrent check and concurrent allez
        HashMap<Player, Task> pTasks = new HashMap<>(game.getPlayerTasks());
        //Remove task assigned to the player which completed task
        pTasks.remove(game.getPlayer(playerId));
        //Find which player the completed task belonged to
        for (Map.Entry<Player, Task> pt : pTasks.entrySet()) {
            if (playerId != pt.getKey().getId() &&
                    pt.getValue() instanceof MotionTask &&
                    ((MotionTask) pt.getValue()).getMotionType() == task) {
                game.completeTask();
                newTask(pt.getKey().getId());

                if (pt.getKey().getId() == HOSTPLAYERID) {
                    taskCompleted();
                }
            }
        }
    }

    /**
     * Checks if the pressed panel completed any tasks and completes it. Otherwise does nothing.
     * @param p
     * @param playerId
     */
    private void finishButtonTask(Panel p, int playerId){
//        HashMap<Player, Task> pTasks = new HashMap<>(game.getPlayerTasks());
        //Loop through all playerTasks
        for (Map.Entry<Player, Task> pTask : game.getPlayerTasks().entrySet()) {
            if (pTask.getValue() instanceof PanelTask) {
                PanelTask task = (PanelTask) pTask.getValue();
                if (task.isConcurrent()){
                    // Set panel on pressed.
                    task.setPressed(p.getId());
                    if (task.isCompleted()){
                        game.completeTask();
                        if (pTask.getKey().getId() == HOSTPLAYERID) {
                            taskCompleted();
                        }
                        newTask(pTask.getKey().getId());
                    }
                } else {
                    // If panel being pressed is the same complete the task
                    if (p.getId() == task.getTaskPanel().getId()){
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
                if (lTask.isConcurrent()){
                    ArrayList<MapObject> objects = lTask.getMapObjects();
                    boolean finished = true;
                    // Loop though all location in the locationTask and
                    // if one of the locations is not in the set with the playerlocations the task is not finished
                    for (MapObject m : objects) {
                        if (!gameMap.getPlayerLocations().values().contains(gameMap.getObjectLocation(m))){
                            finished = false;
                            break;
                        }
                    }
                    if (finished) {
                        if(pTask.getKey().getId() == HOSTPLAYERID) {
                            taskCompleted();
                        }
                        newTask(pTask.getKey().getId());
                    }
                } else {
                    if (gameMap.getPlayerLocations().values().contains(gameMap.getObjectLocation(lTask.getMapObject()))){
                        if(pTask.getKey().getId() == HOSTPLAYERID) {
                            taskCompleted();
                        }
                        newTask(pTask.getKey().getId());
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

    private void locationFound(Location location) {
        gameMap.setVisible(location);
        drawMap();
    }

    private void findLocationObject(Location location, int playerId){
        MapObject object = gameMap.getObjectFromLocation(location);
        if(object != null){
            sendLocationObject(object, playerId);
        }
    }
    //endregion

    private void parseMessage(String message, int playerId) {
        String[] msgToParse = message.split(Constants.MESSAGE_SEPARATOR);
        switch (msgToParse[0]){
            case Constants.HEADER_TASK:
                String activity = msgToParse[2];
                if (activity.equals(Constants.TASK_FAILED)) {
                    failTask(playerId);
                } else if (Integer.getInteger(activity) != null) {
                    finishMotionTask(MotionActivityType.valueOf(Integer.getInteger(activity)), playerId);
                }
                break;
            case Constants.HEADER_BUTTON:
                int buttonId = Integer.getInteger(msgToParse[2]);
                String buttonPressed = msgToParse[3];
                for (Panel p : panels) {
                    if (p.getId() == buttonId){
                        if (buttonPressed.equals(Constants.PRESSED)) {
                            finishButtonTask(p, playerId);
                        } else if (buttonPressed.equals(Constants.RELEASED)){
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
                    Log.i(TAG, "Message READ: " + msg.obj);
                    parseMessage((String) msg.obj, msg.arg1);
                    break;
                case Constants.LOCATION_FOUND:
                    //Todo location thingies
                    break;
                case Constants.ACTIVITY_RECOGNIZED:
                    //Todo activity dinges
                    break;
                case Constants.NEW_CONNECTION:
                    Log.i(TAG, "NEW CONNECTION");
                    //The view mainactivity view is edited to show the amount of joined players
                    ((TextView)(findViewById(R.id.amount_players))).setText(String.valueOf(msg.obj));
                    findViewById(R.id.btn_start).setEnabled(true);
                    //For every new connection a player is added to the game.
                    game.setPlayerAmount((Integer) msg.obj);
                    break;
            }
        }
    };
}
