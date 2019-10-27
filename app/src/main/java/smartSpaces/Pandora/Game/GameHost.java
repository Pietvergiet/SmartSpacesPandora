package smartSpaces.Pandora.Game;

import android.util.SparseArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import smartSpaces.Pandora.Game.Map.GameMap;
import smartSpaces.Pandora.Game.Map.MapObject;
import smartSpaces.Pandora.Game.Tasks.Task;
import smartSpaces.Pandora.Location;

public class GameHost {
    private int playerAmount;
    private int tasksToComplete;
    private int tasksComplete = 0;
    private int panelsPerPlayer;
    private ArrayList<Panel> panels;
    private HashMap<Player, Task> playerTasks;
    private SparseArray<Player> playerList;
//    private HashMap<Object, Location> objects;

    public GameHost(int nTasks, int panelAmount) {
        tasksToComplete = nTasks;
        panelsPerPlayer = panelAmount;
        playerTasks = new HashMap<>();
        playerList = new SparseArray<>();
    }

    public void setPlayerAmount(int amount){
        playerAmount = amount;
    }

    public int getPlayerAmount() {
        return playerAmount;
    }

    public SparseArray<Player> getPlayerList() {
        return playerList;
    }

    public Player getPlayer(int id) {
        return playerList.get(id);
    }

    public int getTotalPanelAmount(){
        return playerList.size() * panelsPerPlayer;
    }

    public int getPanelsPerPlayer() {
        return panelsPerPlayer;
    }

    public void addPlayer(Player player){
        playerList.put(player.getId(), player);
    }

    public void removePlayer(int playerId){
        playerList.remove(playerId);
    }

    public void newTask(Player player, Task task) {
        playerTasks.put(playerList.get(player.getId()), task);
        playerList.get(player.getId()).setTask(task);
    }

    public HashMap<Player, Task> getPlayerTasks() {
        return playerTasks;
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

    public void setPanels(ArrayList<Panel> panels) {
        this.panels = panels;
    }

    public void addPanel(Panel panel){
        panels.add(panel);
    }

    public boolean isSetup() {
        if (playerList.size() == playerAmount && panels.size() == panelsPerPlayer) {
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

    public boolean gameFinished(){
        return (tasksToComplete - tasksComplete <= 0);
    }

//    public void addObject(MapObject object) {
//        objects.put(object, assignObjectLocation());
//
//    }

//    private Location assignObjectLocation() {
//        Random rnd = new Random();
//        int[] dims = map.getMapDimensions();
//        int xLoc = rnd.nextInt(dims[0]);
//        int yLoc = rnd.nextInt(dims[1]);
//        return new Location(xLoc, yLoc);
//    }

//    private void fillPanels() {
//        ArrayList<Panel> ps = new ArrayList<>();
//        for (Player p : playerList) {
//            ps.addAll(Arrays.asList(p.getPanels()));
//        }
//    }

}
