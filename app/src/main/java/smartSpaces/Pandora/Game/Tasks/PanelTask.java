package smartSpaces.Pandora.Game.Tasks;

import android.text.TextUtils;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import smartSpaces.Pandora.Game.Panel;

public class PanelTask extends Task{

    private Panel taskPanel;
    private HashMap<Panel, Boolean> concurPanels;

    public PanelTask(Panel panel) {
        super(TaskType.PANEL);
        taskPanel = panel;
    }

    public PanelTask(ArrayList<Panel> panels) {
        super(TaskType.PANEL_CONCURRENT, true);
        concurPanels = new HashMap<>();
        for (Panel p : panels){
            concurPanels.put(p, false);
        }
    }

    @Override
    public void buildDescription() {
        if (isConcurrent()){
            ArrayList<String> d = new ArrayList<>();
            for (Panel p: concurPanels.keySet()) {
                d.add(p.getVerb() + " the " + p.getObject());
            }
            description = TextUtils.join(",", d);

        } else {
            String desc = taskPanel.getVerb() + " the " + taskPanel.getObject();
            description = desc;
        }

    }

    public Panel getTaskPanel() {
        return taskPanel;
    }

    public HashMap<Panel, Boolean> getConcurPanels() {
        return concurPanels;
    }

    /**
     * Set the panel on pressed if that Panel is part of the task. Otherwise does nothing.
     * @param panelId
     */
    public void setPressed(int panelId) {
        for(Map.Entry<Panel, Boolean> p : concurPanels.entrySet()){
            if (p.getKey().getId() == panelId) {
                concurPanels.put(p.getKey(), true);
            }
        }
    }

    /**
     * Set the panel on released if that Panel is part of the task. Otherwise does nothing.
     * @param panelId
     */
    public void setReleased(int panelId) {
        for(Map.Entry<Panel, Boolean> p : concurPanels.entrySet()){
            if (p.getKey().getId() == panelId) {
                concurPanels.put(p.getKey(), false);
            }
        }
    }

    /**
     * Check if all Panels are pressed otherwise.
     * @return If all required panels are set to Pressed.
     */
    public boolean isCompleted() {
        for (boolean isPressed : concurPanels.values()) {
            if(!isPressed) {
                return false;
            }
        }
        return true;
    }
}
