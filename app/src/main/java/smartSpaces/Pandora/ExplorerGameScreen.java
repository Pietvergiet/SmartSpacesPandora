package smartSpaces.Pandora;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
//import androidx.recyclerview.widget.GridLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.util.Log;
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

import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import smartSpaces.Pandora.Game.GameClient;
import smartSpaces.Pandora.Game.Panel;
import smartSpaces.Pandora.Game.Player;
import smartSpaces.Pandora.P2P.BluetoothService;
import smartSpaces.Pandora.P2P.BluetoothServiceFragment;
import smartSpaces.Pandora.P2P.Constants;
import smartSpaces.Pandora.Picklock.R;

public class ExplorerGameScreen extends AppCompatActivity {

    // controller stuff
    private GameClient game;
    BluetoothServiceFragment fragment;
    private Player player;
    Context view;

    // UI stuff
    public long TASKTIME = 30 * 1000;
    public long TASKTIME_INTERVAL = 100;
    public long N_PANELS = 4;
    private int EXPLORER_ROLE = 1;
    public String TAG = "Explorer: ";
    public Typeface horrorFont;
    public Typeface pixelFont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explorer_game_screen);

        // controller stuff: ===================================
        this.game = new GameClient();
        this.player = new Player(false);

        FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
        fragment = new BluetoothServiceFragment(false, clientHandler, view);
        transaction.add(android.R.id.content, fragment);
        transaction.commit();

        // UI stuff: ===========================================
        // set visibilities of elements
        ProgressBar spinner = findViewById(R.id.spinner);
        spinner.setVisibility(View.GONE);

        // initiate fonts
        this.pixelFont = Typeface.createFromAsset(getAssets(), "Fipps-Regular.otf");
        this.horrorFont = Typeface.createFromAsset(getAssets(), "buried-before.before-bb.ttf");
        TextView task = findViewById(R.id.task);
        task.setTypeface(pixelFont);

        // initiate panels
//        try {
//            initiateGame();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

    @SuppressLint("HandlerLeak")
    private final Handler clientHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
        switch (msg.what) {
            case Constants.MESSAGE_READ:
                readMessage((String) msg.obj);

                break;
            case Constants.LOCATION_FOUND:

                break;
            case Constants.ACTIVITY_RECOGNIZED:

                break;
        }
        }
    };

    private void readMessage(String msg) {
        String[] splittedMesssage = msg.split(Constants.MESSAGE_SEPARATOR);
        switch (splittedMesssage[0]) {
            case Constants.HEADER_TASK:
                handleReceiveTask(splittedMesssage);
                break;
            case Constants.HEADER_LOCATION:

                break;
            case Constants.HEADER_BUTTON:
                handleReceiveButtons(splittedMesssage);
                break;
        }
    }

    public void handleReceiveTask(String[] msg) {
        String task = msg[1];
        setTask(task);
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
        fillPanels(player.getPanels());
    }


    public void initiateGame() throws InterruptedException{
        // add spinner
        ProgressBar spinner = findViewById(R.id.spinner);
        TextView wait = findViewById(R.id.wait_start_game);

        // set visibilities
        spinner.setVisibility(View.VISIBLE);
        wait.setVisibility(View.VISIBLE);

        // find roomcode and join game
        Boolean joined = joinGame();

        if (joined) {
            //remove view, remove spinner, start game
            RelativeLayout modal = findViewById(R.id.modal_start);
            modal.setVisibility(View.GONE);

            //TODO: START GAME AND SET TASK
            setTask("Open the safe");
        } else {
            // show error
        }
    }

    /**
     *  //TODO: Function that implements the joining of a game.
     * @return boolean if joining game worked or failed.
     * @throws InterruptedException
     */
    public boolean joinGame() throws InterruptedException {
        return true;
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
//                taskFailed();
//                setTask("Do a pirouette");
//                goToWin();
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
    public void fillPanels(Panel[] panels) {
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
    }

    /**
     * //TODO: handles when a panel button is clicked. (v.getTag is the ID of the panel)
     * @param v
     */
    public void panelButtonClicked(View v) {
        Log.d(TAG, "panelButtonClicked: id: " + v.getTag() + ", text: ");
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
}
