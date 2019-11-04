package smartSpaces.Pandora.Game.Tasks;

import java.util.HashMap;
import java.util.Map;

import smartSpaces.Pandora.Game.Map.ObjectType;

public enum MotionActivityType {

    PICK_LOCK(5, ObjectType.LOCK),
    RAISE_FLAG(0, ObjectType.FLAG),
    PIROUETTE(3, ObjectType.TULIP),
    HOLD_IN_PLACE(1),
    SHAKE_PHONE(2);

    private ObjectType objectType = null;
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

    MotionActivityType(int resource) {
        this.resource = resource;
    }
}


