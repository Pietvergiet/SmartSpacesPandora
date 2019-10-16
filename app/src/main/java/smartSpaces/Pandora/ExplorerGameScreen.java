package smartSpaces.Pandora;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import java.util.concurrent.TimeUnit;

public class ExplorerGameScreen extends AppCompatActivity {

    public ArrayList<Panel> panels = new ArrayList<>();

    public long TASKTIME = 30 * 1000;
    public long TASKTIME_INTERVAL = 100;
    public long N_PANELS = 4;
    public String TAG = "Explorer: ";
    public Typeface horrorFont;
    public Typeface pixelFont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explorer_game_screen);

        // initiate input field
        EditText input = findViewById(R.id.room_code);
        input.setFilters(new InputFilter[] {new InputFilter.AllCaps()});

        // set visibilities of elements
        ProgressBar spinner = findViewById(R.id.spinner);
        TextView wait = findViewById(R.id.wait_start_game);
        spinner.setVisibility(View.GONE);
        wait.setVisibility(View.GONE);

        // initiate fonts
        this.pixelFont = Typeface.createFromAsset(getAssets(), "Fipps-Regular.otf");
        this.horrorFont = Typeface.createFromAsset(getAssets(), "buried-before.before-bb.ttf");

        TextView task = findViewById(R.id.task);
        TextView start = findViewById(R.id.btn_start);

        task.setTypeface(pixelFont);
        start.setTypeface(horrorFont);
        input.setTypeface(horrorFont);

        // initiate panels
        fillPanels();
    }


    public void initiateGame(View view) throws InterruptedException{
        // add spinner
        ProgressBar spinner = findViewById(R.id.spinner);
        TextView wait = findViewById(R.id.wait_start_game);
        Button btnStart = findViewById(R.id.btn_start);

        // set visibilities
        btnStart.setVisibility(View.GONE);
        spinner.setVisibility(View.VISIBLE);
        wait.setVisibility(View.VISIBLE);

        // find roomcode and join game
        EditText input = findViewById(R.id.room_code);
        String roomCode = input.getText().toString();
        Boolean joined = joinGame(roomCode);

        if (joined) {
            //remove view, remove spinner, start game
            RelativeLayout modal = findViewById(R.id.modal_start);
            modal.setVisibility(View.GONE);

            //TODO: START GAME AND SET TASK
            setTask("Cook an egg");
        } else {
            // show error
        }
    }

    /**
     * Function that implements the joining of a game.
     * @param roomCode
     * @return boolean if joining game worked or failed.
     * @throws InterruptedException
     */
    public boolean joinGame(String roomCode) throws InterruptedException {
        TimeUnit.SECONDS.sleep(5);
        Log.d(TAG, "startGame: room code: " + roomCode);
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


    public void fillPanels() {
        Button btn1 = findViewById(R.id.button1);
        Button btn2 = findViewById(R.id.button2);
        Button btn3 = findViewById(R.id.button3);
        Button btn4 = findViewById(R.id.button4);

        TextView desc1 = findViewById(R.id.description1);
        TextView desc2 = findViewById(R.id.description2);
        TextView desc3 = findViewById(R.id.description3);
        TextView desc4 = findViewById(R.id.description4);

        // create panels
        for (int i = 0; i < N_PANELS; i++){
            Panel newPanel = new Panel(i);
            panels.add(newPanel);
            Log.d(TAG, "fillPanels: name: " + newPanel.getVerb() + " " + newPanel.getObject());
        }

        // set button texts
        btn1.setText(panels.get(0).getVerb());
        btn2.setText(panels.get(1).getVerb());
        btn3.setText(panels.get(2).getVerb());
        btn4.setText(panels.get(3).getVerb());

        // set description
        desc1.setText(panels.get(0).getObject());
        desc2.setText(panels.get(1).getObject());
        desc3.setText(panels.get(2).getObject());
        desc4.setText(panels.get(3).getObject());

        // set fonts
        btn1.setTypeface(horrorFont);
        btn2.setTypeface(horrorFont);
        btn3.setTypeface(horrorFont);
        btn4.setTypeface(horrorFont);

//        RecyclerView panelGrid = findViewById(R.id.panel_grid);
//
//        // use this setting to improve performance if you know that changes
//        // in content do not change the layout size of the RecyclerView
//        panelGrid.setHasFixedSize(true);
//
//        // use a linear layout manager
//        GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 2);
//        panelGrid.setLayoutManager(layoutManager);
//
//        // specify an adapter (see also next example)
//        mAdapter = new MyAdapter(myDataset);
//        recyclerView.setAdapter(mAdapter);

//        GridView buttonGrid = findViewById(R.id.button_grid); // init GridView
        // Create an object of CustomAdapter and set Adapter to GridView
//        Typeface horrorFont = Typeface.createFromAsset(getAssets(), "buried-before.before-bb.ttf");
//        Typeface handwrittenFont = Typeface.createFromAsset(getAssets(), "GloriaHallelujah-Regular.ttf");
//        CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(), panels, horrorFont, handwrittenFont);
//        buttonGrid.setAdapter(customAdapter);
//        // implement setOnItemClickListener event on GridView
////        buttonGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
////            @Override
////            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
////                int panelId = (int) id;
////                // set an Intent to Another Activity
////                Log.d(TAG, "onItemClick: button clicked: " + panels.get(panelId).getVerb() + " " + panels.get(panelId).getObject());
////            }
////        });
//        View.OnClickListener listener = new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//                int buttonId = view.getId();
//                Log.d(TAG, "onClick: button clicked: " + panels.get(buttonId).getVerb() + " " + panels.get(buttonId).getObject());
//
//            }
//        };
//
//        customAdapter.setOnClickListener(listener);
    }

    public void panelButtonClicked(View v) {
        Log.d(TAG, "panelButtonClicked: id: " + v.getTag() + ", text: ");
    }
}
