package smartSpaces.Pandora;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import smartSpaces.Pandora.P2P.Constants;
import smartSpaces.Pandora.Picklock.R;

public class LostScreen extends AppCompatActivity {
    private int role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_screen);

        Bundle b = getIntent().getExtras();
        role = b.getInt("role");

        Typeface horrorFont = Typeface.createFromAsset(getAssets(), "buried-before.before-bb.ttf");
        Typeface comicFont = Typeface.createFromAsset(getAssets(), "Bangers-Regular.ttf");

        TextView lost = findViewById(R.id.lost);
        Button btnNew = findViewById(R.id.new_game);
        Button home = findViewById(R.id.home);

        lost.setTypeface(comicFont);
        btnNew.setTypeface(horrorFont);
        home.setTypeface(horrorFont);
    }

    public void startNewGame(View view) {
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
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
