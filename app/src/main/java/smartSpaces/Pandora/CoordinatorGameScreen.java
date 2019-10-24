package smartSpaces.Pandora;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
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
import android.view.Display;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import smartSpaces.Pandora.Game.Controllers.HostGameController;
import smartSpaces.Pandora.Game.GameHost;
import smartSpaces.Pandora.Game.Map.MapObject;
import smartSpaces.Pandora.Game.Panel;
import smartSpaces.Pandora.Game.Tasks.Task;
import smartSpaces.Pandora.P2P.BluetoothServiceFragment;
import smartSpaces.Pandora.P2P.Constants;
import smartSpaces.Pandora.Picklock.R;

public class CoordinatorGameScreen extends AppCompatActivity {

    private BluetoothServiceFragment bServiceFragment;
    private GameHost game;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coordinator_game_screen);
        game = new GameHost(30, 4);


        bServiceFragment = new BluetoothServiceFragment(true, hostHandler, this);
        startBluetooth();

        generateButtons();
        setUp();
    }

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
        hiddenMapBlocks = new ArrayList<>();
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
        hideMap(0, 0);
        hideMap(0, 1);
//        hideMap(0, 2);
//        hideMap(0, 3);
//        hideMap(0, 4);
        hideMap(1, 0);
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
        hideMap(4, 4);
//
//        showMap(3,4);
//        showMap(1,1);

        drawObjectOnMap(R.drawable.flag, new Location(3,3));
        drawObjectOnMap(R.drawable.safe, new Location(1,2));
        drawObjectOnMap(R.drawable.bomb, new Location(0,4));
    }

    /**
     * Starts a game
     * @param view
     */
    public void startGame(View view) {
        RelativeLayout modal = findViewById(R.id.modal_start);
        modal.setVisibility(view.GONE);

        CountDownTimer timer = new CountDownTimer(GAMETIME, GAMETIME_INTERVAL) {
            @Override
            public void onTick(long l) {
                int min = (int) Math.floor((double) Long.valueOf(l / (60*1000)));
                int sec =(int) (l - (min * 60 * 1000)) / 1000;
                Log.d(TAG, "onTick: " + min + ":" + sec);

                updateTimer(min, sec);
            }

            @Override
            public void onFinish() {
                // TODO: go to win or loser screen
            }
        };

        timer.start();
        setTask("Chase a cart");
    }

    private void generateButtons(){
        int buttonAmount = game.getTotalPanelAmount();
        Panel tmpPanel = new Panel(-1);
        ArrayList<String> verbs = new ArrayList<>(Arrays.asList(tmpPanel.getVERBS()));
        ArrayList<String> objects = new ArrayList<>(Arrays.asList(tmpPanel.getOBJECTS()));
        Random r = new Random();
        for (int i = 0; i < buttonAmount; i++){
            int vInt = r.nextInt(buttonAmount-i);
            String verb = verbs.get(vInt);
            verbs.remove(verb);
            vInt = r.nextInt(buttonAmount - i);
            String object = objects.get(vInt);
            objects.remove(object);
            if(!game.addPanel(new Panel(i, verb, object))) {
                break;
            }
        }
    }


    //region View updaters
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
     * @param x
     * @param y
     */
    public void hideMap(int x, int y) { // matrix from 0-4 0-4
        hiddenMapBlocks.add(new Location(x, y));
        drawMap();
    }

    public void showMap(int x, int y) {
        for (int i = 0; i < hiddenMapBlocks.size(); i++ ){
            if (hiddenMapBlocks.get(i).isLocation(x, y)){
                hiddenMapBlocks.remove(i);
                break;
            }
        }
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

        for (int i = 0; i < hiddenMapBlocks.size(); i ++) {
            Location block = hiddenMapBlocks.get(i);

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
        bServiceFragment.sendMessage(Constants.HEADER_START);
    }

    private void sendGameEnd(String winOrLose) {
        bServiceFragment.sendMessage(Constants.HEADER_END + Constants.MESSAGE_SEPARATOR + winOrLose);
    }

    private void sendNewTask(Task task, int playerId){
        ArrayList<String> message = new ArrayList<>();
        message.add(Constants.HEADER_TASK);
        message.add(task.getDescription());
        String out = TextUtils.join(Constants.MESSAGE_SEPARATOR, message);
        bServiceFragment.sendMessage(out, playerId);
    }

    private void sendLocationObject(MapObject object, int playerId) {
        ArrayList<String> message = new ArrayList<>();
        message.add(Constants.HEADER_LOCATION);
        message.add(String.valueOf(object.getResource()));
        String out = TextUtils.join(Constants.MESSAGE_SEPARATOR, message);

        bServiceFragment.sendMessage(out, playerId);
    }

    private void sendPlayerPanels(Panel[] buttons, int playerId){
        ArrayList<String> message = new ArrayList<>();
        message.add(Constants.HEADER_BUTTON);
        message.add(String.valueOf(buttons.length));

        ArrayList<String> list = new ArrayList<>();
        for (Panel p : buttons) {
            list.add(TextUtils.join(Constants.MESSAGE_LIST_ELEMENT_SEPARATOR, new String[]{String.valueOf(p.getId()), p.getVerb(), p.getObject()}));
        }
        message.add(
                TextUtils.join(Constants.MESSAGE_LIST_SEPARATOR, list));

        String out = TextUtils.join(Constants.MESSAGE_SEPARATOR, message);

        bServiceFragment.sendMessage(out, playerId);
    }
    //endregion

    private void parseMessage(String message, int playerId) {
        String[] msgToParse = message.split(Constants.MESSAGE_SEPARATOR);
        switch (msgToParse[0]){
            case Constants.HEADER_TASK:

                break;
            case Constants.HEADER_BUTTON:

                break;
            case Constants.HEADER_LOCATION:

                break;
        }
    }

    @SuppressLint("HandlerLeak")
    private final Handler hostHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_READ:
                    Log.i(TAG, "Message READ");
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
