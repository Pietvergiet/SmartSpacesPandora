package smartSpaces.Pandora.Game.Map;

import java.util.HashMap;
import java.util.Map;

import smartSpaces.Pandora.Game.Tasks.MotionActivityType;

public enum ObjectType {

    ROPE(1, MotionActivityType.RAISE_FLAG, false),
    LOCK(2, MotionActivityType.PICK_LOCK, false);

    private int resource;
    private MotionActivityType motionType;
    private boolean hazard;
    private static Map map = new HashMap<>();

    static {
        for (ObjectType pageType : ObjectType.values()) {
            map.put(pageType.resource, pageType);
        }
    }

    public MotionActivityType getMotionActivityType() {
        return motionType;
    }

    public static ObjectType valueOf(int pageType) {
        return (ObjectType) map.get(pageType);
    }

    // getter method
    public int getResource()
    {
        return resource;
    }

    public boolean isHazard() {
        return hazard;
    }

    ObjectType(int resource, MotionActivityType type, boolean hazard)
    {
        this.resource = resource;
        this.motionType = type;
        this.hazard = hazard;
    }

    //never used sadness overloaded
    ObjectType(int resource, boolean hazard)
    {
        this.resource = resource;
        this.hazard = hazard;
    }
}
