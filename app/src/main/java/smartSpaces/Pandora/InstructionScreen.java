package smartSpaces.Pandora;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class InstructionScreen extends AppCompatActivity {
    private int COORDINATOR_ROLE = 0;
    private int EXPLORER_ROLE = 1;
    private String TAG = "Instruction Screen: ";
    private int role;
    private boolean isAnimating = false;
    private int currentInstruction = 0;
    ArrayList<TextView> instructionTexts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction_screen);

        Bundle b = getIntent().getExtras();
        role = b.getInt("role");

        instructionTexts = new ArrayList<>();
        TextView instruction1 = findViewById(R.id.instruction_1);
        TextView instruction2 = findViewById(R.id.instruction_2);
        TextView instruction3 = findViewById(R.id.instruction_3);
        TextView instruction4 = findViewById(R.id.instruction_4);
        TextView instruction5 = findViewById(R.id.instruction_5);
        TextView instruction6 = findViewById(R.id.instruction_6);
        TextView instruction7 = findViewById(R.id.instruction_7);
        instructionTexts.add(instruction1);
        instructionTexts.add(instruction2);
        instructionTexts.add(instruction3);
        instructionTexts.add(instruction4);
        instructionTexts.add(instruction5);
        instructionTexts.add(instruction6);
        instructionTexts.add(instruction7);
        instruction1.setVisibility(View.GONE);
        instruction2.setVisibility(View.GONE);
        instruction3.setVisibility(View.GONE);
        instruction4.setVisibility(View.GONE);
        instruction5.setVisibility(View.GONE);
        instruction6.setVisibility(View.GONE);
        instruction7.setVisibility(View.GONE);

        Typeface horrorFont = Typeface.createFromAsset(getAssets(), "buried-before.before-bb.ttf");
        Button btnNext = findViewById(R.id.next);
        btnNext.setTypeface(horrorFont);

        animateFadeInText(instructionTexts.get(currentInstruction));
//        currentInstruction++;
    }

    public void animateFadeInText(final TextView text) {
        isAnimating = true;
        Log.d(TAG, "animateFadeInText: fading in instruction: " + currentInstruction);
        Animation aniFade = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fadein);
        text.startAnimation(aniFade);

        aniFade.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {text.setVisibility(View.VISIBLE); }

            @Override
            public void onAnimationEnd(Animation animation) {
                isAnimating = false;
//                text.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
    }

    public void animateFadeOutInText(final TextView text) {
        isAnimating = true;
        Log.d(TAG, "continueInstruction: Fading out instruction: " + currentInstruction);

        Animation aniFade = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fadeoutfast);
        text.startAnimation(aniFade);

        aniFade.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
//                text.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                text.setVisibility(View.GONE);
                currentInstruction++;
                animateWaitText(instructionTexts.get(currentInstruction));
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
    }

//    public void animateFadeInOutText(final TextView text) {
//        Animation aniFade = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fadein);
//        text.startAnimation(aniFade);
//
//        aniFade.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//                text.setVisibility(View.VISIBLE);
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                text.setVisibility(View.GONE);
//                animateWaitText(text);
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {}
//        });
//    }

    public void animateWaitText(final TextView text) {
        Log.d(TAG, "animateWaitText: waiting ...");
        Log.d(TAG, "animateWaitText: current instruction: " + currentInstruction);
        Animation wait = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.wait);
        text.startAnimation(wait);

        wait.setAnimationListener(new Animation.AnimationListener(){
            @Override
            public void onAnimationStart(Animation animation) {
                text.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Log.d(TAG, "onAnimationEnd: ... reached end of waiting");
                animateFadeInText(text);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
    }

    public void animateFadeOutText(final TextView text) {
        Animation aniFade = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fadeout);
        text.startAnimation(aniFade);

        aniFade.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                text.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
    }

    public void continueInstruction(View view) {
        Log.d(TAG, "continueInstruction: current instruction: " + currentInstruction);
        if (isAnimating) {
            return;
        } else if (currentInstruction == instructionTexts.size()-1) {
            goToNext();
        } else {
            animateFadeOutInText(instructionTexts.get(currentInstruction));

//            currentInstruction ++;
        }
    }

    public void goToNext() {
        if (role == COORDINATOR_ROLE){
            goToCoordinator();
        } else if (role == EXPLORER_ROLE) {
            goToExplorer();
        }
    }

    public void goToCoordinator() {
        Intent intent = new Intent(this, CoordinatorGameScreen.class);
        startActivity(intent);
    }

    public void goToExplorer() {
        Intent intent = new Intent(this, ExplorerGameScreen.class);
        startActivity(intent);
    }
}
