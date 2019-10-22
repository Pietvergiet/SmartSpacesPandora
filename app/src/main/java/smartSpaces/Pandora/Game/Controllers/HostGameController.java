package smartSpaces.Pandora.Game.Controllers;

import android.text.TextUtils;

import java.util.ArrayList;

import smartSpaces.Pandora.Game.GameHost;
import smartSpaces.Pandora.Game.Map.MapObject;
import smartSpaces.Pandora.Game.Panel;
import smartSpaces.Pandora.Game.Tasks.Task;
import smartSpaces.Pandora.P2P.BluetoothService;
import smartSpaces.Pandora.P2P.Constants;

public class HostGameController {

    private BluetoothService bService;

    public HostGameController(GameHost game) {

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
}
