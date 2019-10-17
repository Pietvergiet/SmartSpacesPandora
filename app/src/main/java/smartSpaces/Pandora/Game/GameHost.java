package smartSpaces.Pandora.Game;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import smartSpaces.Pandora.Game.Map.GameMap;
import smartSpaces.Pandora.Game.Map.Location;
import smartSpaces.Pandora.Game.Map.MapObject;
import smartSpaces.Pandora.Game.Tasks.Task;

public class GameHost {
    private int playerAmount;
    private int tasksToComplete;
    private int tasksComplete = 0;
    private int panelsPerPlayer;
    private ArrayList<Panel> panels;
    private HashMap<Player, Task> playerTasks;
    private ArrayList<Player> playerList;
    private HashMap<Object, Location> objects;
    private GameMap map;

    public GameHost(int nPlayers, int nTasks, int panelAmount) {
        playerAmount = nPlayers;
        tasksToComplete = nTasks;
        panelsPerPlayer = panelAmount;
        playerTasks = new HashMap<>();
        playerList = new ArrayList<>();
    }

    public void addPlayer(Player player){
        playerList.add(player);
        fillPanels();
    }

    public void removePlayer(Player player){
        playerList.remove(player);
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

    public void setMap(GameMap m) {
        map = m;
    }

    public void setPanels(ArrayList<Panel> panels) {
        this.panels = panels;
    }

    public void addPanel(Panel panel){
        panels.add(panel);
    }

    public boolean isSetup() {
        if (playerList.size() == playerAmount && map != null && panels.size() == panelsPerPlayer) {
            for (Map.Entry<Player, Task> pt : playerTasks.entrySet()){
                if (pt.getValue() == null) {
                    return false;
                }
            }
        } else {
            return false;
        }
        return true;
    }

    public void addObject(MapObject object) {
        objects.put(object, assignObjectLocation());

    }

    private Location assignObjectLocation() {
        Random rnd = new Random();
        int[] dims = map.getMapDimensions();
        int xLoc = rnd.nextInt(dims[0]);
        int yLoc = rnd.nextInt(dims[1]);
        return new Location(xLoc, yLoc);
    }

    private void fillPanels() {
        ArrayList<Panel> ps = new ArrayList<>();
        for (Player p : playerList) {
            ps.addAll(Arrays.asList(p.getPanels()));
        }
    }

}
