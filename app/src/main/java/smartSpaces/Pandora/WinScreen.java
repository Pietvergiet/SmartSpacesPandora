package smartSpaces.Pandora;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.lang.reflect.Type;

import smartSpaces.Pandora.P2P.Constants;
import smartSpaces.Pandora.Picklock.R;

public class WinScreen extends AppCompatActivity {
    private int role;
    private int tasksCompleted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_win_screen);

        Bundle b = getIntent().getExtras();
        role = b.getInt("role");
        tasksCompleted = b.getInt("tasks");

        Typeface horrorFont = Typeface.createFromAsset(getAssets(), "buried-before.before-bb.ttf");
        Typeface comicFont = Typeface.createFromAsset(getAssets(), "Bangers-Regular.ttf");

        TextView win = findViewById(R.id.win);
        Button btnNew = findViewById(R.id.new_game);
        Button home = findViewById(R.id.home);
        TextView tasksCompletedView = findViewById(R.id.tasks_completed);
        tasksCompletedView.setText(tasksCompleted);

        win.setTypeface(comicFont);
        btnNew.setTypeface(horrorFont);
        home.setTypeface(horrorFont);
    }

    public void startNewGame(View view) {
        // check if explorer or coordinator
        if (role == Constants.COORDINATOR_ROLE) {
            // is coordinator
            Intent intent = new Intent(this, CoordinatorGameScreen.class);
            startActivity(intent);
        } else if (role == Constants.EXPLORER_ROLE) {
            // is explorer
            Intent intent = new Intent(this, ExplorerGameScreen.class);
            startActivity(intent);
        }
    }

    public void goToHome(View view) {
        finish();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
