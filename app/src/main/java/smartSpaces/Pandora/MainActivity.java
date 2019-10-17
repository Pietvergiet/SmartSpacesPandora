package smartSpaces.Pandora;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.lang.reflect.Type;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnHost = findViewById(R.id.btn_host);
        Button btnJoin = findViewById(R.id.btn_join);

        Typeface font = Typeface.createFromAsset(getAssets(), "buried-before.before-bb.ttf");

        btnHost.setTypeface(font);
        btnJoin.setTypeface(font);
    }

    public void goToCoordinator(View view) {
        Intent intent = new Intent(this, CoordinatorGameScreen.class);
        startActivity(intent);
    }

    public void goToExplorer(View view) {
        Intent intent = new Intent(this, ExplorerGameScreen.class);
        startActivity(intent);
    }
}
