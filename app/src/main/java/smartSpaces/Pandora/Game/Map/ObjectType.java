package smartSpaces.Pandora.Game.Map;

import java.util.HashMap;
import java.util.Map;

import smartSpaces.Pandora.Game.Tasks.MotionActivityType;
import smartSpaces.Pandora.Picklock.R;

public enum ObjectType {

    ROPE(1, MotionActivityType.RAISE_FLAG, false, R.drawable.flag),
    LOCK(2, MotionActivityType.PICK_LOCK, false, R.drawable.safe),
    BOMB(3, true, R.drawable.bomb),
    TULIP(4, MotionActivityType.PIROUETTE, false, R.drawable.bomb);

    private int resource;
    private MotionActivityType motionType;
    private boolean hazard;
    private int icon;
    private static Map map = new HashMap<>();

    static {
        for (ObjectType pageType : ObjectType.values()) {
            map.put(pageType.resource, pageType);
        }
    }

    public MotionActivityType getMotionActivityType() {
        return motionType;
    }

    public int getIcon() {
        return icon;
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

    ObjectType(int resource, MotionActivityType type, boolean hazard, int icon)
    {
        this.resource = resource;
        this.motionType = type;
        this.hazard = hazard;
        this.icon = icon;
    }

    //never used sadness overloaded
    ObjectType(int resource, boolean hazard, int icon)
    {
        this.resource = resource;
        this.hazard = hazard;
        this.icon = icon;
    }
}
