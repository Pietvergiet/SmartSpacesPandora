package smartSpaces.Pandora.Game.Controllers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import smartSpaces.Pandora.Game.GameClient;
import smartSpaces.Pandora.Game.GameHost;
import smartSpaces.Pandora.P2P.BluetoothServiceFragment;
import smartSpaces.Pandora.P2P.Constants;

public class ClientGameController {
    private GameClient game;

    public ClientGameController(GameClient game, Context view) {
        this.game = game;
//        this.gameView = view;

//        Intent intent = new Intent(view, InstructionScreen.class);
//        intent.putExtra("role", 0);
//        ((Activity)view).startActivityForResult(intent, Constants.INSTRUCTION_DONE);

        FragmentTransaction transaction = ((FragmentActivity) view).getSupportFragmentManager().beginTransaction();
        BluetoothServiceFragment fragment = new BluetoothServiceFragment(false, clientHandler, view);
        transaction.add(android.R.id.content, fragment);
        transaction.commit();
    }

    public void startGame(){
        //TODO de rest
    }

    @SuppressLint("HandlerLeak")
    private final Handler clientHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_READ:

                    break;
                case Constants.LOCATION_FOUND:

                    break;
                case Constants.ACTIVITY_RECOGNIZED:

                    break;
            }
        }
    };
}
