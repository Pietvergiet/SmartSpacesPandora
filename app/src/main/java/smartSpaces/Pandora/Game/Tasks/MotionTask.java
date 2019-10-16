package smartSpaces.Pandora.Game.Tasks;

/**
 * Makes a motion task with or without a maplocation object.
 */
public class MotionTask extends Task{

    private int motionType;
    private MapObject mapObject;

    /**
     * Makes a certain type of motion task.
     * @param motionType The type of {@link MotionActivityTypes}
     */
    public MotionTask(int motionType) {
        super(TaskTypes.TYPE_MOTION);
        this.motionType = motionType;
        buildDescription();
    }

    /**
     * Makes a certain type of motion task to be executed at a certain {@link MapObject}
     * @param motionType The {@link MotionActivityTypes}
     * @param object The {@link MapObject}
     */
    public MotionTask(int motionType, MapObject object) {
        super(TaskTypes.TYPE_MOTION_LOCATION);
        this.motionType = motionType;
        mapObject = object;
        buildDescription();
    }

    /**
     * Makes a certain type of motion task which all player have to execute simultaneously.
     * @param motionType The type of {@link MotionActivityTypes}
     * @param isConcurrent If the task has to be executed by all current players
     */
    public MotionTask(int motionType, boolean isConcurrent) {
        super((isConcurrent ? TaskTypes.TYPE_MOTION_CONCURRENT : TaskTypes.TYPE_MOTION_LOCATION), isConcurrent);
        this.motionType = motionType;
    }

    @Override
    public void buildDescription() {
        String desc = "Shit's broken yo";
        switch (motionType) {
            case MotionActivityTypes.PICK_LOCK:
                desc = "Pick the lock";
                break;
            case  MotionActivityTypes.RAISE_FLAG:
                desc = "Let the Flag be raised";
                break;
            case MotionActivityTypes.PIROUETTE:
                if (isConcurrent()) {
                    desc = "Let everyone spin round, right round, like a record baby";
                } else {
                    desc = "Make one do a Pirouette";
                }
                break;
            case MotionActivityTypes.HOLD_IN_PLACE:
                desc = "Make one immobile for X seconds";
                break;
            case MotionActivityTypes.SHAKE_PHONE:
                desc = "Shake phones";
        }
        description = desc;
    }

    public MapObject getMapObject() {
        return mapObject;
    }

    public int getMotionType() {
        return motionType;
    }
}
