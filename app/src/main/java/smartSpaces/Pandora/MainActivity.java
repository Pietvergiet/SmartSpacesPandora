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

    private int EXPLORER_ROLE = 1;
    private int COORDINATOR_ROLE = 0;
    private int role;
    private HostGameController hController;
    private ClientGameController cController;

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
        GameHost newGame = new GameHost(30, 4);
//        CoordinatorGameScreen newCoordinatorGameScreen = new CoordinatorGameScreen();
//        startBluetooth(true);
        hController = new HostGameController(newGame, this);
//        Intent intent = new Intent(this, InstructionScreen.class);
//        intent.putExtra("role", COORDINATOR_ROLE);
//        startActivity(intent);
    }

    public void goToInstructionExplorer(View view) {
        GameClient newGame = new GameClient();
        CoordinatorGameScreen newCoordinatoGameScreen = new CoordinatorGameScreen();
//        startBluetooth(false);
        cController = new ClientGameController(newGame, this);
//        Intent intent = new Intent(this, InstructionScreen.class);
//        intent.putExtra("role", EXPLORER_ROLE);
//        startActivity(intent);
    }

    private void startBluetooth(boolean isHost) {
//        FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
//        BluetoothServiceFragment fragment = new BluetoothServiceFragment(isHost);
//        transaction.replace(android.R.id.content, fragment);
//        transaction.commit();
    }

    private void startGame(View view){
        if(hController != null) {
            String amount = ((TextView)this.findViewById(R.id.amount_players)).getText().toString();
            hController.startGame(Integer.parseInt(amount));
        } else if (cController != null){
            cController.startGame();
        }
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
            case Constants.REQUEST_CONNECT_DEVICE:
                Log.i(TAG, "Correct actitvyti connect device");
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    Log.i(TAG, "Get HOST ADDRESS");
//                    makeConnection(data);
                }
                break;
            default:
                Log.i(TAG, "super onactivity calledS");
                super.onActivityResult(requestCode, resultCode, intent);
                break;
        }
    }
}
