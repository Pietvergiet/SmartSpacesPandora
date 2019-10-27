package smartSpaces.Pandora.Game.Tasks;

public abstract class Task {

    private TaskType taskType;
    private boolean isConcurrent = false;
    protected String description = "Dummmy Task";

    public Task(TaskType type) {
        taskType = type;
    }

    public Task(TaskType type, boolean ooncurrent) {
        taskType = type;
        isConcurrent = ooncurrent;
        buildDescription();
    }

    public TaskType getTaskType() {
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
