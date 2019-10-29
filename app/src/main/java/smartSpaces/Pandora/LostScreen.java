package smartSpaces.Pandora;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import smartSpaces.Pandora.Picklock.R;

public class LostScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_screen);

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
        // check if explorer or coordinator
        Intent intent = new Intent(this, ExplorerGameScreen.class);
        startActivity(intent);
    }

    public void goToHome(View view) {
        finish();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
