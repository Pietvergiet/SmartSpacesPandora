package smartSpaces.Pandora.Game;

public class GameClient {
    private Player player;
    private Task displayTask;
    private ThaMap map;

    public GameClient(Player p, Task task){
        player = p;
        displayTask = task;
    }

    public void setTask(Task task){
        displayTask = task;
    }

    public void setMap(ThaMap m) {
        map = m;
    }
}
