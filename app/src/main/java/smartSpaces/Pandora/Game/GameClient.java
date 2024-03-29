package smartSpaces.Pandora.Game;

import smartSpaces.Pandora.Game.Map.GameMap;
import smartSpaces.Pandora.Game.Tasks.Task;

public class GameClient {
    private Player player;
    private Task displayTask;
    private GameMap map;

    public void setPlayer(Player player){
        this.player = player;
    }

    public void setTask(Task task){
        displayTask = task;
    }

    public boolean isSetUp() {
        return (map != null && player.isReady() && displayTask != null);
    }

    public void setMap(GameMap m) {
        map = m;
    }
}
