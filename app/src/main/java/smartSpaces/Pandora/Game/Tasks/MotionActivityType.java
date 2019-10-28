package smartSpaces.Pandora.Game.Tasks;

import java.util.HashMap;
import java.util.Map;

import smartSpaces.Pandora.Game.Map.ObjectType;

public enum MotionActivityType {

    PICK_LOCK(1, ObjectType.LOCK),
    RAISE_FLAG(2, ObjectType.ROPE),
    PIROUETTE(3, ObjectType.ROPE),
    HOLD_IN_PLACE(4, ObjectType.ROPE),
    SHAKE_PHONE(5, ObjectType.ROPE);

    private ObjectType objectType;
    private int resource;
    private static Map map = new HashMap<>();

    public int getResource() {
        return resource;
    }

    public ObjectType getObjectType() {
        return objectType;
    }

    static {
        for (MotionActivityType pageType : MotionActivityType.values()) {
            map.put(pageType.resource, pageType);
        }
    }

    public static MotionActivityType valueOf(int pageType) {
        return (MotionActivityType) map.get(pageType);
    }

    MotionActivityType(int resource, ObjectType type) {
        this.objectType = type;
        this.resource = resource;
    }
}


