package smartSpaces.Pandora;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.sql.Time;

public class CoordinatorGameScreen extends AppCompatActivity {

    private long GAMETIME = 10 * 60 * 1000;
    private long TASKTIME = 30 * 1000;
    private long GAMETIME_INTERVAL = 1000;
    private long TASKTIME_INTERVAL = 100;
    private long MAPBLOCKS = 5;
    private String TAG = "Coordinator ";
    private Bitmap bm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coordinator_game_screen);

        // setting typefaces (ugly but cannot be done in XML :/
        TextView task = findViewById(R.id.task);
        TextView start = findViewById(R.id.btn_start);
        TextView roomCode = findViewById(R.id.room_code);

        Typeface pixelFont = Typeface.createFromAsset(getAssets(), "Fipps-Regular.otf");
        Typeface horrorFont = Typeface.createFromAsset(getAssets(), "buried-before.before-bb.ttf");

        task.setTypeface(pixelFont);
        start.setTypeface(horrorFont);
        roomCode.setTypeface(horrorFont);

        RelativeLayout mapContainer = findViewById(R.id.map);

//        int screenWidth = mapContainer.getMeasuredWidth();
//        mapContainer.setMinimumHeight(screenWidth);
//
//        try {
//            bm = BitmapFactory.decodeStream(this.getApplicationContext().getAssets().open("redblock.png"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        ImageView map = findViewById(R.id.map_background);
//        map.setImageBitmap(bm);

        //updateMap(1,2);
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
        setTask("Cook an egg");
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

    public void updateMap(int x, int y) { // matrix from 0-4 0-4
        Bitmap mutableBM = bm.copy(Bitmap.Config.ARGB_8888, true);
        ImageView map = findViewById(R.id.map_background);
        int width = map.getMeasuredWidth();
        int stepsize = Math.round(width / MAPBLOCKS);

        map.setImageBitmap(bm);

        Canvas tempCanvas = new Canvas(mutableBM);
        Paint p = new Paint();
        p.setColor(Color.BLACK);
        tempCanvas.drawBitmap(bm, 0, 0, null);

        float cx = (float) (x * stepsize);
        float cy = (float) (y * stepsize);

        tempCanvas.drawCircle(cy, cx, stepsize, p);
        map.setImageDrawable(new BitmapDrawable(getResources(), mutableBM));
    }
}
