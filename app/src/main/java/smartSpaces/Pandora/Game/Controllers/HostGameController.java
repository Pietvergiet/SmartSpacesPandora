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
import android.widget.Switch;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

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

public class HostGameController implements Serializable {
    private static final String TAG = "HostController";

    private BluetoothServiceFragment bServiceFragment;
    private GameHost game;
    private Context mainView;
    private Context gameView;

    public HostGameController(GameHost game, Context view) {
        this.game = game;

    }

    private void startBluetooth() {
        FragmentTransaction transaction = ((FragmentActivity) mainView).getSupportFragmentManager().beginTransaction();

        transaction.add(android.R.id.content, bServiceFragment);
        transaction.commit();
    }

    private void startInstructionScreen(){
        Intent intent = new Intent(mainView, InstructionScreen.class);
        intent.putExtra("role", 0);
        ((Activity)mainView).startActivityForResult(intent, Constants.INSTRUCTION_DONE);
    }

    public void startGame(Context context){
        gameView = context;
        ((TextView)((Activity) context).findViewById(R.id.tasks_left)).setText("Geedit vanuit controller");
    }

    public void startCoordinator() {
        Intent intent = new Intent(mainView, CoordinatorGameScreen.class);
        intent.putExtra("controller", this);
        mainView.startActivity(intent);
        //TODO start het spel
    }

    private void generateButtons(){
        int buttonAmount = game.getTotalPanelAmount();
        Panel tmpPanel = new Panel(-1);
        ArrayList<String> verbs = new ArrayList<>(Arrays.asList(tmpPanel.getVERBS()));
        ArrayList<String> objects = new ArrayList<>(Arrays.asList(tmpPanel.getOBJECTS()));
        Random r = new Random();
        for (int i = 0; i < buttonAmount; i++){
            int vInt = r.nextInt(buttonAmount-i);
            String verb = verbs.get(vInt);
            verbs.remove(verb);
            vInt = r.nextInt(buttonAmount - i);
            String object = objects.get(vInt);
            objects.remove(object);
//            if(!game.addPanel(new Panel(i, verb, object))) {
//                break;
//            }
        }
    }

    private void sendNewTask(Task task, int playerId){
        ArrayList<String> message = new ArrayList<>();
        message.add(Constants.HEADER_TASK);
        message.add(task.getDescription());
        String out = TextUtils.join(Constants.MESSAGE_SEPARATOR, message);
        bServiceFragment.sendMessage(out, playerId);
    }

    private void sendLocationObject(MapObject object, int playerId) {
        ArrayList<String> message = new ArrayList<>();
        message.add(Constants.HEADER_LOCATION);
        message.add(String.valueOf(object.getResource()));
        String out = TextUtils.join(Constants.MESSAGE_SEPARATOR, message);

        bServiceFragment.sendMessage(out, playerId);
    }

    private void sendPlayerPanels(Panel[] buttons, int playerId){
        ArrayList<String> message = new ArrayList<>();
        message.add(Constants.HEADER_BUTTON);
        message.add(String.valueOf(buttons.length));

        ArrayList<String> list = new ArrayList<>();
        for (Panel p : buttons) {
            list.add(TextUtils.join(Constants.MESSAGE_LIST_ELEMENT_SEPARATOR, new String[]{String.valueOf(p.getId()), p.getVerb(), p.getObject()}));
        }
        message.add(
                TextUtils.join(Constants.MESSAGE_LIST_SEPARATOR, list));

        String out = TextUtils.join(Constants.MESSAGE_SEPARATOR, message);

        bServiceFragment.sendMessage(out, playerId);
    }

    private void parseMessage(String message, int playerId) {
        String[] msgToParse = message.split(Constants.MESSAGE_SEPARATOR);
        switch (msgToParse[0]){
            case Constants.HEADER_TASK:

                break;
            case Constants.HEADER_BUTTON:

                break;
            case Constants.HEADER_LOCATION:

                break;
        }
    }

    @SuppressLint("HandlerLeak")
    private final Handler hostHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_READ:
                    Log.i(TAG, "Message READ");
                    parseMessage((String) msg.obj, msg.arg1);
                    break;
                case Constants.LOCATION_FOUND:
                    //Todo location thingies
                    break;
                case Constants.ACTIVITY_RECOGNIZED:
                    //Todo activity dinges
                    break;
                case Constants.NEW_CONNECTION:
                    Log.i(TAG, "NEW CONNECTION");
                    //The view mainactivity view is edited to show the amount of joined players
                    ((TextView)((Activity)mainView).findViewById(R.id.amount_players)).setText(String.valueOf(msg.obj));
                    (((Activity)mainView).findViewById(R.id.btn_start)).setEnabled(true);
                    //For every new connection a player is added to the game.
                    game.setPlayerAmount((Integer) msg.obj);
                    break;
            }
        }
    };
}
