package smartSpaces.Pandora.Game;

public class Player {
    private int id;
    private boolean hasMap;
    private Task displayTask;

    public Player(int number, boolean mapPlayer) {
        id = number;
        hasMap = mapPlayer;
    }

    public void setTask(Task task) {
        displayTask = task;
    }
}
