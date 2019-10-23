package smartSpaces.Pandora.Game.Tasks;

public abstract class Task {

    private TaskTypes taskType;
    private boolean isConcurrent = false;
    protected String description;

    public Task(TaskTypes type) {
        taskType = type;
    }

    public Task(TaskTypes type, boolean ooncurrent) {
        taskType = type;
        isConcurrent = ooncurrent;
        buildDescription();
    }

    public TaskTypes getTaskType() {
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
