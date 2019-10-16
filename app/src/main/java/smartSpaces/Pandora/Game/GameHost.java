package smartSpaces.Pandora.Game;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class GameHost {
    private int playerAmount;
    private int tasksToComplete;
    private int tasksComplete = 0;
    private HashMap<Player, Task> playerTasks;
    private ArrayList<Player> playerList;
    private THaMap map;

    public GameHost(int nPlayers, int nTasks) {
        playerAmount = nPlayers;
        tasksToComplete = nTasks;
        playerTasks = new HashMap<>();
        playerList = new ArrayList<>();
    }

    private void addPlayer(Player player){
        playerList.add(player);
    }

    private void newTask(Player player, Task task) {
        playerTasks.put(player, task);
    }

    private void completeTask() {
        tasksComplete++;
    }

    private int getTasksLeft() {
        return tasksToComplete - tasksComplete;
    }

    private void addTask() {
        tasksToComplete++;
    }

    private void setMap(ThaMap m) {
        map = m;
    }
}
