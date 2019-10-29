package smartSpaces.Pandora.Game.Map;

import smartSpaces.Pandora.Game.Tasks.MotionActivityType;

public enum ObjectType {

    ROPE(1, MotionActivityType.RAISE_FLAG, false),
    LOCK(2, MotionActivityType.PICK_LOCK, false);

    private int resource;
    private MotionActivityType motionActivityType;
    private boolean hazard;

    // getter method
    public int getResource()
    {
        return resource;
    }

    public MotionActivityType getMotionActivityType() {
        return motionActivityType;
    }

    public boolean isHazard() {
        return hazard;
    }

    ObjectType(int resource, MotionActivityType type, boolean hazard)
    {
        this.resource = resource;
        this.motionActivityType = type;
        this.hazard = hazard;
    }

    //never used sadness overloaded
    ObjectType(int resource, boolean hazard)
    {
        this.resource = resource;
        this.hazard = hazard;
    }
}
