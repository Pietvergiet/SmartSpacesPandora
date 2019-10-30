package smartSpaces.Pandora;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Typeface;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Type;

import smartSpaces.Pandora.Game.Controllers.ClientGameController;
import smartSpaces.Pandora.Game.Controllers.HostGameController;
import smartSpaces.Pandora.Game.GameClient;
import smartSpaces.Pandora.Game.GameHost;
import smartSpaces.Pandora.P2P.BluetoothServiceFragment;
import smartSpaces.Pandora.P2P.Constants;
import smartSpaces.Pandora.Picklock.R;

import static android.content.pm.PackageManager.PERMISSION_DENIED;

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

        disableButtons();
//        checkalles();

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


    /**
     * Checks if all required permissions are giving and otherwise ask for those permissions.
     */
    private void checkGetPermissions() {
        int checkCoarse = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int MY_PERMISSIONS_REQUEST = 200;
        int checkFine = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (checkFine == PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST);
        }
        if (checkCoarse == PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST);
        }
    }

    private void checkalles() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            disableButtons();
            return;
        }
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            // Stop here, we definitely need NFC
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
            disableButtons();
            return;
        }

        if (!mBluetoothAdapter.isEnabled()) {
            // We need to enable the Bluetooth, so we ask the user
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 3);
            mBluetoothAdapter.enable();

        }

        checkGetPermissions();

        if (!nfcAdapter.isEnabled()) {
            Toast.makeText(getApplicationContext(), "Please activate NFC and press Back to return to the application!", Toast.LENGTH_LONG).show();
            startActivity(new Intent(android.provider.Settings.ACTION_NFC_SETTINGS));
        }

        if (nfcAdapter.isEnabled() && mBluetoothAdapter.isEnabled()) {
            enableButtons();
        }
    }

    private void disableButtons() {
        findViewById(R.id.btn_join).setEnabled(false);
        findViewById(R.id.btn_host).setEnabled(false);
    }

    private void enableButtons() {
        findViewById(R.id.btn_join).setEnabled(true);
        findViewById(R.id.btn_host).setEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkalles();
    }
}
