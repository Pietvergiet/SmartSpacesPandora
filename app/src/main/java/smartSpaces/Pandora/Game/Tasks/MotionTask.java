package smartSpaces.Pandora.Game.Tasks;

import smartSpaces.Pandora.Game.Map.MapObject;

/**
 * Makes a motion task with or without a maplocation object.
 */
public class MotionTask extends Task{

    private MotionActivityTypes motionType;
    private MapObject mapObject;

    /**
     * Makes a certain type of motion task.
     * @param motionType The type of {@link MotionActivityTypes}
     */
    public MotionTask(MotionActivityTypes motionType) {
        super(TaskTypes.MOTION);
        this.motionType = motionType;
        buildDescription();
    }

    /**
     * Makes a certain type of motion task to be executed at a certain {@link MapObject}
     * @param motionType The {@link MotionActivityTypes}
     * @param object The {@link MapObject}
     */
    public MotionTask(MotionActivityTypes motionType, MapObject object) {
        super(TaskTypes.MOTION_LOCATION);
        this.motionType = motionType;
        mapObject = object;
        buildDescription();
    }

    /**
     * Makes a certain type of motion task which all player have to execute simultaneously.
     * @param motionType The type of {@link MotionActivityTypes}
     * @param isConcurrent If the task has to be executed by all current players
     */
    public MotionTask(MotionActivityTypes motionType, boolean isConcurrent) {
        super((isConcurrent ? TaskTypes.MOTION_CONCURRENT : TaskTypes.MOTION_LOCATION), isConcurrent);
        this.motionType = motionType;
    }

    @Override
    public void buildDescription() {
        String desc = "Shit's broken yo";
        switch (motionType) {
            case PICK_LOCK:
                desc = "Pick the lock";
                break;
            case  RAISE_FLAG:
                desc = "Let the Flag be raised";
                break;
            case PIROUETTE:
                if (isConcurrent()) {
                    desc = "Let everyone spin round, right round, like a record baby";
                } else {
                    desc = "Make one do a Pirouette";
                }
                break;
            case HOLD_IN_PLACE:
                desc = "Make one immobile for X seconds";
                break;
            case SHAKE_PHONE:
                desc = "Shake phones";
        }
        description = desc;
    }

    public MapObject getMapObject() {
        return mapObject;
    }

    public MotionActivityTypes getMotionType() {
        return motionType;
    }
}