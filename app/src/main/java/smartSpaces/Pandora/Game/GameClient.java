package smartSpaces.Pandora.Game;

import smartSpaces.Pandora.Game.Tasks.Task;

public class GameClient {
    private Player player;
    private Task displayTask;
    private ThaMap map;

    public GameClient(Player p, Task task){
        player = p;
    }

    public void setTask(Task task){
        displayTask = task;
    }

    public void setMap(ThaMap m) {
        map = m;
    }
}
