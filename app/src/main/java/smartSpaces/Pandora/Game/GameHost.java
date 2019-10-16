package smartSpaces.Pandora.Game;

import java.util.ArrayList;
import java.util.HashMap;

import smartSpaces.Pandora.Game.Tasks.Task;

public class GameHost {
    private int playerAmount;
    private int tasksToComplete;
    private int tasksComplete = 0;
    private int panelsPerPlayer;
    private HashMap<Player, Task> playerTasks;
    private ArrayList<Player> playerList;
    private THaMap map;

    public GameHost(int nPlayers, int nTasks, int panelAmount) {
        playerAmount = nPlayers;
        tasksToComplete = nTasks;
        panelsPerPlayer = panelAmount;
        playerTasks = new HashMap<>();
        playerList = new ArrayList<>();
        playerPanels = new HashMap<>();
    }

    public void addPlayer(Player player){
        playerList.add(player);
    }

    public void newTask(Player player, Task task) {
        playerTasks.put(player, task);
    }

    public void completeTask() {
        tasksComplete++;
    }

    public int getTasksLeft() {
        return tasksToComplete - tasksComplete;
    }

    public void addTask() {
        tasksToComplete++;
    }

    public void setMap(ThaMap m) {
        map = m;
    }
}
