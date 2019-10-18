package smartSpaces.Pandora;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.lang.reflect.Type;

public class WinScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_win_screen);

        Typeface horrorFont = Typeface.createFromAsset(getAssets(), "buried-before.before-bb.ttf");
        Typeface comicFont = Typeface.createFromAsset(getAssets(), "Bangers-Regular.ttf");

        TextView win = findViewById(R.id.win);
        Button btnNew = findViewById(R.id.new_game);
        Button home = findViewById(R.id.home);

        win.setTypeface(comicFont);
        btnNew.setTypeface(horrorFont);
        home.setTypeface(horrorFont);
    }


    public void startNewGame(View view) {
        // check if explorer or coordinator
        Intent intent = new Intent(this, ExplorerGameScreen.class);
        startActivity(intent);
    }

    public void goToHome(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
