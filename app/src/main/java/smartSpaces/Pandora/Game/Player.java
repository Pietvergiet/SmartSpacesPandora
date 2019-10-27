package smartSpaces.Pandora.Game;

import smartSpaces.Pandora.Game.Tasks.Task;

public class Player {
    private int id;
    private boolean hasMap;
    private Task displayTask;
    private Panel[] panels;

    public Player(int number, boolean mapPlayer, Panel[] panels) {
        id = number;
        hasMap = mapPlayer;
        this.panels = panels;
    }

    public Player(int number, boolean mapPlayer) {
        id = number;
        hasMap = mapPlayer;
        panels = new Panel[0];
    }

    public int getId() {
        return id;
    }

    public void setPanel(int index, Panel panel) {
        if (index >= 0 && index < panels.length){
            panels[index] = panel;
        }
    }

    public void setPanels(Panel[] p) {
        if (p.length == panels.length) {
            panels = p;
        }
    }


    public Panel[] getPanels() {
        return panels;
    }


    public boolean isReady() {
        if (!hasMap) {
            if (panels == null) {
                return false;
            }
            for (Panel p : panels){
                if (p == null){
                    return false;
                }
            }
        }
        return true;
    }

    public void setTask(Task task) {
        displayTask = task;
    }
}
