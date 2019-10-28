package smartSpaces.Pandora.Game.Tasks;

import android.util.Log;

import smartSpaces.Pandora.Game.Map.MapObject;

/**
 * Makes a motion task with or without a maplocation object.
 */
public class MotionTask extends Task{

    private MotionActivityType motionType;
    private MapObject mapObject;

    /**
     * Makes a certain type of motion task.
     * @param motionType The type of {@link MotionActivityType}
     */
    public MotionTask(MotionActivityType motionType) {
        super(TaskType.MOTION);
        this.motionType = motionType;
        buildDescription();
    }

    /**
     * Makes a certain type of motion task to be executed at a certain {@link MapObject}
     * @param motionType The {@link MotionActivityType}
     * @param object The {@link MapObject}
     */
    public MotionTask(MotionActivityType motionType, MapObject object) {
        super(TaskType.MOTION_LOCATION);
        this.motionType = motionType;
        mapObject = object;
        buildDescription();
    }

    /**
     * Makes a certain type of motion task which all player have to execute simultaneously.
     * @param motionType The type of {@link MotionActivityType}
     * @param isConcurrent If the task has to be executed by all current players
     */
    public MotionTask(MotionActivityType motionType, boolean isConcurrent) {
        super((isConcurrent ? TaskType.MOTION_CONCURRENT : TaskType.MOTION_LOCATION), isConcurrent);
        this.motionType = motionType;
    }

    public void buildDescription() {
        String desc = "Shit's broken yo";
        Log.i("MOTIIONTASK" , motionType.getResource() + "  " + motionType.toString());
        switch (motionType) {
            case PICK_LOCK:
                desc = "Pick the lock";
                break;
            case  RAISE_FLAG:
                desc = "Let the Flag be raised";
                break;
            case PIROUETTE:
                if (isConcurrent()) {
                    desc = "Everyone spin round, right round, like a record baby";
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
        super.description = desc;
    }

    public MapObject getMapObject() {
        return mapObject;
    }

    public MotionActivityType getMotionType() {
        return motionType;
    }
}
