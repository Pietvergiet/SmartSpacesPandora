package smartSpaces.Pandora.Game.Controllers;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import smartSpaces.Pandora.ExplorerGameScreen;
import smartSpaces.Pandora.Game.GameClient;
import smartSpaces.Pandora.Game.GameHost;
import smartSpaces.Pandora.Game.Panel;
import smartSpaces.Pandora.Game.Player;
import smartSpaces.Pandora.P2P.BluetoothService;
import smartSpaces.Pandora.P2P.BluetoothServiceFragment;
import smartSpaces.Pandora.P2P.Constants;

public class ClientGameController {
    private GameClient game;
    BluetoothServiceFragment fragment;
    private Player player;
    Context view;

    public ClientGameController(GameClient game, Context view) {
        this.game = game;
        this.player = new Player(false);
        this.view = view;

//        this.gameView = view;

//        Intent intent = new Intent(view, InstructionScreen.class);
//        intent.putExtra("role", 0);
//        ((Activity)view).startActivityForResult(intent, Constants.INSTRUCTION_DONE);

        FragmentTransaction transaction = ((FragmentActivity) view).getSupportFragmentManager().beginTransaction();
        fragment = new BluetoothServiceFragment(false, clientHandler, view);
        transaction.add(android.R.id.content, fragment);
        transaction.commit();
    }

    public void startGame(){
        Intent intent = new Intent(view, ExplorerGameScreen.class);
        view.startActivity(intent);


    }

    @SuppressLint("HandlerLeak")
    private final Handler clientHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_READ:
                    readMessage((String) msg.obj);

                    break;
                case Constants.LOCATION_FOUND:

                    break;
                case Constants.ACTIVITY_RECOGNIZED:

                    break;
            }
        }
    };

    private void readMessage(String msg) {
        String[] splittedMesssage = msg.split(Constants.MESSAGE_SEPARATOR);
        switch (splittedMesssage[0]) {
            case Constants.HEADER_TASK:

                break;
            case Constants.HEADER_LOCATION:

                break;
            case Constants.HEADER_BUTTON:
                handleReceiveButtons(splittedMesssage);

                break;
        }
    }

    public void handleReceiveButtons(String[] msg) {
        String btnAmount = msg[1];
        Panel[] panels = new Panel[Integer.parseInt(btnAmount)];
        String list = msg[2];
        String[] listElement = list.split(Constants.MESSAGE_LIST_SEPARATOR);
        for (int i = 0; i < listElement.length; i ++) {
            String[] elementItems = listElement[i].split(Constants.MESSAGE_LIST_ELEMENT_SEPARATOR);
            String buttonId = elementItems[0];
            String verb = elementItems[1];
            String object = elementItems[2];
            Panel panel = new Panel(Integer.parseInt(buttonId), verb, object);
            panels[i] = panel;
        }
        player.setPanels(panels);
    }
}
