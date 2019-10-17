package smartSpaces.Pandora.Game.Tasks;

import smartSpaces.Pandora.Game.Panel;

public class PanelTask extends Task{

    private Panel taskPanel;

    public PanelTask(Panel panel) {
        super(TaskTypes.PANEL);
        taskPanel = panel;
    }

    @Override
    public void buildDescription() {
        String desc = taskPanel.getVerb() + " the " + taskPanel.getObject();
        description = desc;
    }

    public Panel getTaskPanel() {
        return taskPanel;
    }
}
