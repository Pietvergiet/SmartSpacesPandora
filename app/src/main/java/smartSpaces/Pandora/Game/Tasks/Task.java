package smartSpaces.Pandora.Game.Tasks;

public abstract class Task {

    private int taskType;
    private boolean isConcurrent = false;
    protected String description;

    public Task(int type) {
        taskType = type;
    }

    public Task(int type, boolean ooncurrent) {
        taskType = type;
        isConcurrent = ooncurrent;
        buildDescription();
    }

    public int getTaskType() {
        return taskType;
    }

    public boolean isConcurrent() {
        return isConcurrent;
    }

    public String getDescription() {
        return description;
    }

    public abstract void buildDescription();

}
