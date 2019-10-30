package smartSpaces.Pandora;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.lang.reflect.Type;

import smartSpaces.Pandora.Game.Controllers.ClientGameController;
import smartSpaces.Pandora.Game.Controllers.HostGameController;
import smartSpaces.Pandora.Game.GameClient;
import smartSpaces.Pandora.Game.GameHost;
import smartSpaces.Pandora.P2P.BluetoothServiceFragment;
import smartSpaces.Pandora.P2P.Constants;
import smartSpaces.Pandora.Picklock.R;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

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

    public void goToInstructionCoordinator(View view) {
//        GameHost newGame = new GameHost(30, 4);
//        hController = new HostGameController(newGame, this);
//        hController.startCoordinator();
        Intent intent = new Intent(this, CoordinatorGameScreen.class);
        startActivity(intent);
    }

    public void goToInstructionExplorer(View view) {
//        GameClient newGame = new GameClient();
//        CoordinatorGameScreen newCoordinatoGameScreen = new CoordinatorGameScreen();
//        startBluetooth(false);
//        cController = new ClientGameController(newGame, this);
        Intent intent = new Intent(this, InstructionScreen.class);
        intent.putExtra("role", Constants.EXPLORER_ROLE);
        startActivity(intent);
//        cController = new ClientGameController(newGame, this);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        //TODO call shit om te doen na instruction.
        Log.i(TAG, "Onactivity called");
        switch (requestCode) {
            case Constants.INSTRUCTION_DONE:
                Log.i(TAG,"Instruction finished");
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
//                    startGame();
                    Log.i(TAG, "Intstuction OK");
                }
                break;
            default:
                Log.i(TAG, "super onactivity calledS");
                super.onActivityResult(requestCode, resultCode, intent);
                break;
        }
    }
}
