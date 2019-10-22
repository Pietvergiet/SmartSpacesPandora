package smartSpaces.Pandora.Game.Controllers;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;

import smartSpaces.Pandora.CoordinatorGameScreen;
import smartSpaces.Pandora.Game.GameHost;
import smartSpaces.Pandora.Game.Map.MapObject;
import smartSpaces.Pandora.Game.Panel;
import smartSpaces.Pandora.Game.Tasks.Task;
import smartSpaces.Pandora.InstructionScreen;
import smartSpaces.Pandora.MainActivity;
import smartSpaces.Pandora.P2P.BluetoothService;
import smartSpaces.Pandora.P2P.BluetoothServiceFragment;
import smartSpaces.Pandora.P2P.Constants;
import smartSpaces.Pandora.Picklock.R;

public class HostGameController {
    private static final String TAG = "HostController";

    private BluetoothService bService;
    private GameHost game;
    private Context gameView;

    public HostGameController(GameHost game, Context view) {
        this.game = game;
//        this.gameView = view;

//        Intent intent = new Intent(view, InstructionScreen.class);
//        intent.putExtra("role", 0);
//        ((Activity)view).startActivityForResult(intent, Constants.INSTRUCTION_DONE);
        gameView = view;
        FragmentTransaction transaction = ((FragmentActivity) view).getSupportFragmentManager().beginTransaction();
        BluetoothServiceFragment fragment = new BluetoothServiceFragment(true, hostHandler, view);
        transaction.add(android.R.id.content, fragment);
        transaction.commit();
    }

    public void startGame(int playerAmount) {
        //TODO start het spel
        game.setPlayerAmount(playerAmount);
    }

    private void sendNewTask(Task task, int playerId){
        ArrayList<String> message = new ArrayList<>();
        message.add(Constants.HEADER_TASK);
        message.add(task.getDescription());
        String out = TextUtils.join(Constants.MESSAGE_SEPARATOR, message);
        bService.write(out.getBytes(), playerId);
    }

    private void sendLocationObject(MapObject object, int playerId) {
        ArrayList<String> message = new ArrayList<>();
        message.add(Constants.HEADER_LOCATION);
        message.add(String.valueOf(object.getResource()));
        String out = TextUtils.join(Constants.MESSAGE_SEPARATOR, message);

        bService.write(out.getBytes(), playerId);
    }

    private void sendPlayerPanels(Panel[] buttons, int playerId){
        ArrayList<String> message = new ArrayList<>();
        message.add(Constants.HEADER_BUTTON);
        message.add(String.valueOf(buttons.length));

        ArrayList<String> list = new ArrayList<>();
        for (Panel p : buttons) {
            list.add(TextUtils.join(Constants.MESSAGE_SEPARATOR, new String[]{String.valueOf(p.getId()), p.getVerb(), p.getObject()}));
        }
        message.add(Constants.MESSAGE_LIST_START +
                TextUtils.join(Constants.MESSAGE_LIST_SEPARATOR, list) +
                Constants.MESSAGE_LIST_END);

        String out = TextUtils.join(Constants.MESSAGE_SEPARATOR, message);

        bService.write(out.getBytes(), playerId);
    }

    @SuppressLint("HandlerLeak")
    private final Handler hostHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_READ:

                    break;
                case Constants.LOCATION_FOUND:

                    break;
                case Constants.ACTIVITY_RECOGNIZED:

                    break;
                case Constants.NEW_CONNECTION:
                    Log.i(TAG, "NEW CONNECTION");
                    ((TextView)((Activity)gameView).findViewById(R.id.amount_players)).setText(String.valueOf(msg.obj));
                    (((Activity)gameView).findViewById(R.id.btn_start)).setEnabled(true);
                    break;
            }
        }
    };
}
